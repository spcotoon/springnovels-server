package com.app.springnovels.api.service.author.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class AuthorLoginServiceRequest {

    private String email;
    private String password;

    @Builder
    private AuthorLoginServiceRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
