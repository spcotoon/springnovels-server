package com.app.springnovels.api.controller.author;

import com.app.springnovels.api.controller.author.requestDto.AuthorCreateRequest;
import com.app.springnovels.api.controller.author.requestDto.AuthorLoginRequest;
import com.app.springnovels.api.exception.InvalidEmailException;
import com.app.springnovels.api.service.author.AuthorService;
import com.app.springnovels.api.service.author.response.AuthorResponse;
import com.app.springnovels.config.auth.CustomUser;
import com.app.springnovels.config.auth.JwtProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/author")
public class AuthorController {

    private final AuthorService authorService;

    @PostMapping("/join")
    public ResponseEntity<AuthorResponse> join(@Valid @RequestBody AuthorCreateRequest request) {
        AuthorResponse response = authorService.join(request.toServiceRequest());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthorLoginRequest request, HttpServletResponse response) {
        Authentication authentication = authorService.authenticateJwtAuthor(request.toServiceRequest());

        if (!request.getEmail().endsWith("author")) {
            throw new InvalidEmailException();
        }

        String accessToken = JwtProvider.createAccessToken(authentication);
        Cookie cookie = JwtProvider.createCookie("accessToken", accessToken);

        response.addCookie(cookie);

        return ResponseEntity.ok("login");
    }

    @GetMapping("/me")
    public ResponseEntity<AuthorResponse> me() {

        AuthorResponse response = authorService.getMe(getCurrentUserId());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        JwtProvider.deleteCookie(response, "accessToken");

        return ResponseEntity.ok("logout");
    }


    private Long getCurrentUserId() {
        CustomUser customUser = (CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return customUser.getId();
    }
}
