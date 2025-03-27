package com.app.springnovels.api.service.author.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class AuthorCreateServiceRequest {
    private String email;
    private String password;
    private String penName;

    @Builder
    private AuthorCreateServiceRequest(String email, String password, String penName) {
        this.email = email;
        this.password = password;
        this.penName = penName;
    }
}
