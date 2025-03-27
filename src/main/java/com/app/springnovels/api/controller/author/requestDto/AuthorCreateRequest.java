package com.app.springnovels.api.controller.author.requestDto;

import com.app.springnovels.api.service.author.request.AuthorCreateServiceRequest;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AuthorCreateRequest {

    private String email;
    private String password;
    private String penName;

    @Builder
    private AuthorCreateRequest(String email, String password, String penName) {
        this.email = email;
        this.password = password;
        this.penName = penName;
    }

    public AuthorCreateServiceRequest toServiceRequest() {
        return AuthorCreateServiceRequest.builder()
                .email(email)
                .password(password)
                .penName(penName)
                .build();
    }
}
