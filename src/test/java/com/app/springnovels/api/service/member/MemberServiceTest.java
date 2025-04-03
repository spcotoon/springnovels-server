package com.app.springnovels.api.service.member;

import com.app.springnovels.IntegrationTestSupport;
import com.app.springnovels.api.exception.AlreadyExistsEmailException;
import com.app.springnovels.api.exception.AlreadyExistsNicknameException;
import com.app.springnovels.api.service.member.request.MemberCreateServiceRequest;
import com.app.springnovels.api.service.member.request.MemberLoginServiceRequest;
import com.app.springnovels.api.service.member.response.MemberResponse;
import com.app.springnovels.config.auth.CustomUser;
import com.app.springnovels.domain.member.Member;
import com.app.springnovels.domain.member.MemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;


import static org.assertj.core.api.Assertions.*;


class MemberServiceTest extends IntegrationTestSupport {

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @AfterEach
    void tearDown() {
        memberRepository.deleteAll();
    }

    @DisplayName("회원가입을 하면 비밀번호를 암호화하여 저장한다")
    @Test
    void join() throws Exception {
        //given
        MemberCreateServiceRequest request = MemberCreateServiceRequest.builder()
                .email("a@a.com")
                .password("test1234")
                .nickname("테스터")
                .build();

        MemberResponse result = memberService.join(request);

        //when
        Member member = memberRepository.findById(result.getId()).orElseThrow();

        //then
        assertThat(member.getId()).isNotNull();
        assertThat(passwordEncoder.matches(request.getPassword(), member.getPassword())).isTrue();
    }

    @DisplayName("이미 존재하는 이메일로 회원가입 시도하면 예외가 발생한다.")
    @Test
    void joinWithAlreadyEmail() throws Exception {
        //given
        Member alreadyMember = Member.builder()
                .email("a@a.com")
                .password("test1234")
                .nickname("테스터")
                .build();
        memberRepository.save(alreadyMember);

        MemberCreateServiceRequest request = MemberCreateServiceRequest.builder()
                .email("a@a.com")
                .password("test1234")
                .nickname("가입시도자")
                .build();

        //when
        //then
        assertThatThrownBy(() -> memberService.join(request))
                .isInstanceOf(AlreadyExistsEmailException.class)
                .hasMessage("이미 사용중인 이메일 입니다.");
     }

    @DisplayName("이미 존재하는 닉네임으로 회원가입 시도하면 예외가 발생한다.")
    @Test
    void joinWithAlreadyNickname() throws Exception {
        //given
        Member alreadyMember = Member.builder()
                .email("a@a.com")
                .password("test1234")
                .nickname("테스터")
                .build();
        memberRepository.save(alreadyMember);

        MemberCreateServiceRequest request = MemberCreateServiceRequest.builder()
                .email("b@b.com")
                .password("test1234")
                .nickname("테스터")
                .build();


        //when
        //then
        assertThatThrownBy(() -> memberService.join(request))
                .isInstanceOf(AlreadyExistsNicknameException.class)
                .hasMessage("이미 사용중인 닉네임 입니다.");
    }


    @DisplayName("deleteAt 필드가 null 이면 최초 가입이므로 웰컴코인 10개가 지급된다.")
    @Test
    void firstJoin() throws Exception {
        //given
        MemberCreateServiceRequest request = MemberCreateServiceRequest.builder()
                .email("a@a.com")
                .password("test1234")
                .nickname("테스터")
                .build();

        MemberResponse result = memberService.join(request);

        //when
        Member member = memberRepository.findById(result.getId()).orElseThrow();

        //then
        assertThat(member.getDeletedAt()).isNull();
        assertThat(member.getCoin()).isEqualTo(10);
    }

    @DisplayName("Member 로그인 시 SecurityContext 에 Member 엔티티 정보를 가진 Authentication 객체가 설정된다")
    @Test
    void loginMember() throws Exception {
        //given
        String encoded = passwordEncoder.encode("test1234");
        Member joinedMember = Member.builder()
                .email("a@a.com")
                .password(encoded)
                .nickname("테스터")
                .build();
        memberRepository.save(joinedMember);

        MemberLoginServiceRequest request = MemberLoginServiceRequest.builder()
                .email("a@a.com")
                .password("test1234")
                .build();

        Authentication authentication = memberService.authenticateJwtUser(request);

        CustomUser customUser = (CustomUser) authentication.getPrincipal();

        Long id = customUser.getId();
        String displayName = customUser.getDisplayName();
        String email = customUser.getUsername();

        //when
        //then
        assertThat(id).isEqualTo(joinedMember.getId());
        assertThat(displayName).isEqualTo(joinedMember.getNickname());
        assertThat(email).isEqualTo(joinedMember.getEmail());
     }


}