package com.app.springnovels.api.service.member.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberCreateServiceRequest {

    private String email;
    private String password;
    private String nickname;

    @Builder
    private MemberCreateServiceRequest(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }
}
