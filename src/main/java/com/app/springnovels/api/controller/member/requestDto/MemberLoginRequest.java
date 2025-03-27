package com.app.springnovels.api.controller.member.requestDto;

import com.app.springnovels.api.service.member.request.MemberLoginServiceRequest;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberLoginRequest {
    private String email;
    private String password;

    @Builder
    private MemberLoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public MemberLoginServiceRequest toServiceRequest() {
        return MemberLoginServiceRequest.builder()
                .email(email)
                .password(password)
                .build();
    }
}
