package com.app.springnovels.api.controller.author.requestDto;

import com.app.springnovels.api.service.author.request.AuthorLoginServiceRequest;
import com.app.springnovels.api.service.member.request.MemberLoginServiceRequest;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AuthorLoginRequest {
    private String email;
    private String password;

    @Builder
    private AuthorLoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public AuthorLoginServiceRequest toServiceRequest() {
        return AuthorLoginServiceRequest.builder()
                .email(email)
                .password(password)
                .build();
    }
}
