package com.app.springnovels.api.service.member.response;

import com.app.springnovels.domain.member.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberResponse {

    private Long id;
    private String email;
    private String nickname;
    private Integer coin;

    @Builder
    private MemberResponse(Long id, String email, String nickname, Integer coin) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.coin = coin;
    }

    public static MemberResponse from(Member member) {
        return MemberResponse.builder()
                .id(member.getId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .coin(member.getCoin())
                .build();
    }
}
