package com.app.springnovels.api.service.author.response;

import com.app.springnovels.domain.author.Author;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AuthorResponse {

    private Long id;
    private String email;
    private String penName;
    private int salesCoin;

    @Builder
    private AuthorResponse(Long id, String email, String penName, int salesCoin) {
        this.id = id;
        this.email = email;
        this.penName = penName;
        this.salesCoin = salesCoin;
    }


    public static AuthorResponse from(Author author) {
        return AuthorResponse.builder()
                .id(author.getId())
                .email(author.getEmail())
                .penName(author.getPenName())
                .salesCoin(author.getSalesCoin())
                .build();
    }
}
