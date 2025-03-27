package com.app.springnovels.api.controller.member;

import com.app.springnovels.api.controller.member.requestDto.MemberCreateRequest;
import com.app.springnovels.api.controller.member.requestDto.MemberLoginRequest;
import com.app.springnovels.api.exception.InvalidEmailException;
import com.app.springnovels.api.service.member.MemberService;
import com.app.springnovels.api.service.member.response.MemberResponse;
import com.app.springnovels.config.auth.CustomUser;
import com.app.springnovels.config.auth.JwtProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/join")
    public ResponseEntity<MemberResponse> join(@Valid @RequestBody MemberCreateRequest request) {
        MemberResponse response = memberService.join(request.toServiceRequest());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody MemberLoginRequest request, HttpServletResponse response) {
        Authentication authentication = memberService.authenticateJwtUser(request.toServiceRequest());

        if (request.getEmail().endsWith("author")) {
            throw new InvalidEmailException();
        }

        String accessToken = JwtProvider.createAccessToken(authentication);

        Cookie cookie = JwtProvider.createCookie("accessToken", accessToken);
        response.addCookie(cookie);
        String value = cookie.getValue();

        return ResponseEntity.ok(value);
    }

    @GetMapping("/me")
    public ResponseEntity<MemberResponse> me() {

        MemberResponse response = memberService.getMe(getCurrentUserId());

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
