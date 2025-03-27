package com.app.springnovels.api.controller.member.requestDto;

import com.app.springnovels.api.service.member.request.MemberCreateServiceRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@Getter
public class MemberCreateRequest {

    @Email(message = "이메일 형식이 아닙니다.")
    @NotBlank(message = "이메일은 필수입니다.")
    private String email;
    @Length(min = 8, max = 12, message = "비밀번호는 8글자 이상 12글자 이하입니다.")
    @NotBlank
    private String password;
    @Length(min = 2, max = 6, message = "닉네임은 2글자 이상 6글자 이하입니다.")
    @NotBlank(message = "닉네임은 필수입니다.")
    private String nickname;

    @Builder
    private MemberCreateRequest(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }

    public MemberCreateServiceRequest toServiceRequest() {
        return MemberCreateServiceRequest.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .build();
    }
}
