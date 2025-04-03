package com.app.springnovels.domain.member;

import com.app.springnovels.api.exception.CoinDeficiencyException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class MemberTest {
    
    @DisplayName("보유한 코인보다 많은 코인을 사용할수 없다.")
    @Test
    void payCoinMuchThanBalance() throws Exception {
        //given
        Member member = Member.builder()
                .email("a@a.com")
                .password("1234")
                .nickname("aaa")
                .build();
        
        member.addCoin(1);
        
        //when

        
        //then
        assertThatThrownBy(() ->  member.payCoin(2))
                .isInstanceOf(CoinDeficiencyException.class)
                .hasMessage("코인이 부족합니다.");

        assertThat(member.getCoin()).isEqualTo(1);
     }
}