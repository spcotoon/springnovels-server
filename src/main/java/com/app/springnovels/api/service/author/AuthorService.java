package com.app.springnovels.api.service.author;

import com.app.springnovels.api.exception.*;
import com.app.springnovels.api.service.author.request.AuthorCreateServiceRequest;
import com.app.springnovels.api.service.author.request.AuthorLoginServiceRequest;
import com.app.springnovels.api.service.author.response.AuthorResponse;
import com.app.springnovels.api.service.member.request.MemberLoginServiceRequest;
import com.app.springnovels.api.service.member.response.MemberResponse;
import com.app.springnovels.domain.author.Author;
import com.app.springnovels.domain.author.AuthorRepository;
import com.app.springnovels.domain.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public AuthorResponse join(AuthorCreateServiceRequest request) {
        if (!request.getEmail().endsWith("@spring.novels.author")) {
            throw new InvalidEmailException();
        }

        if (authorRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new AlreadyExistsEmailException();
        }

        if (authorRepository.findByPenName(request.getPenName()).isPresent()) {
            throw new AlreadyExistsNicknameException();
        }

        Author author = Author.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .penName(request.getPenName())
                .build();

        Author savedAuthor = authorRepository.save(author);

        return AuthorResponse.from(savedAuthor);
    }


    public AuthorResponse getMe(Long authorId) {
        Author member = authorRepository.findById(authorId).orElseThrow(NotExistMemberException::new);
        return AuthorResponse.from(member);
    }


    public Authentication authenticateJwtAuthor(AuthorLoginServiceRequest request) {
        try {
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());

            Authentication auth = authenticationManagerBuilder.getObject().authenticate(authToken);

            SecurityContextHolder.getContext().setAuthentication(auth);
            return SecurityContextHolder.getContext().getAuthentication();
        } catch (BadCredentialsException e) {
            throw new InvalidSignInException();
        }
    }

}
