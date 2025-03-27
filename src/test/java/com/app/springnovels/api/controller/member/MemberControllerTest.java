package com.app.springnovels.api.controller.member;

import com.app.springnovels.ControllerTestSupport;
import com.app.springnovels.api.controller.member.requestDto.MemberCreateRequest;
import com.app.springnovels.api.controller.member.requestDto.MemberLoginRequest;
import com.app.springnovels.api.controller.novel.requestDto.NovelCreateRequest;
import com.app.springnovels.api.service.author.AuthorService;
import com.app.springnovels.api.service.member.MemberService;
import com.app.springnovels.api.service.member.request.MemberCreateServiceRequest;
import com.app.springnovels.api.service.member.request.MemberLoginServiceRequest;
import com.app.springnovels.api.service.member.response.MemberResponse;
import com.app.springnovels.api.service.novel.request.NovelCreateServiceRequest;
import com.app.springnovels.api.service.novel.response.NovelResponse;
import com.app.springnovels.config.CorsConfig;
import com.app.springnovels.config.SecurityConfig;
import com.app.springnovels.config.auth.CustomUser;
import com.app.springnovels.config.auth.MultipleUserDetailsService;
import com.app.springnovels.domain.member.Member;
import com.app.springnovels.domain.member.MemberRepository;
import com.app.springnovels.domain.novel.Genre;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ClaimsBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class MemberControllerTest   {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    AuthorService authorService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @AfterEach
    void tearDown() {
        memberRepository.deleteAll();
    }

    @DisplayName("회원가입을 할 수 있다.")
    @Test
    void join() throws Exception {
        //given
        MemberCreateRequest request = MemberCreateRequest.builder()
                .email("a@a.com")
                .password("test1234")
                .nickname("테스터")
                .build();

        //when
        //then
        mockMvc.perform(
                        post("/api/v1/member/join")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());
     }

    @DisplayName("회원가입시 이메일 형식을 지켜야한다.")
    @Test
    void joinWithInvalidEmail() throws Exception {
        //given
        MemberCreateRequest request = MemberCreateRequest.builder()
                .email("aacom")
                .password("test1234")
                .nickname("테스터")
                .build();

        //when
        //then
        mockMvc.perform(
                        post("/api/v1/member/join")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation[0].fieldName").value("email"))
                .andExpect(jsonPath("$.validation[0].errorMessage").value("이메일 형식이 아닙니다."));
    }

    @DisplayName("회원가입시 비밀번호는 8글자 이상 12글자 이하이다.")
    @Test
    void joinWithInvalidPassword() throws Exception {
        //given
        MemberCreateRequest request = MemberCreateRequest.builder()
                .email("a@a.com")
                .password("1234567")
                .nickname("테스터")
                .build();

        //when
        //then
        mockMvc.perform(
                        post("/api/v1/member/join")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation[0].fieldName").value("password"))
                .andExpect(jsonPath("$.validation[0].errorMessage").value("비밀번호는 8글자 이상 12글자 이하입니다."));
    }


    @DisplayName("회원가입시 닉네임은 필수이다.")
    @Test
    void joinWithoutNickname() throws Exception {
        //given
        MemberCreateRequest request = MemberCreateRequest.builder()
                .email("a@a.com")
                .password("test1234")
                .nickname(null)
                .build();

        //when
        //then
        mockMvc.perform(
                        post("/api/v1/member/join")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation[0].fieldName").value("nickname"))
                .andExpect(jsonPath("$.validation[0].errorMessage").value("닉네임은 필수입니다."));
    }


    @DisplayName("로그인시 쿠키에 accessToken 을 생성한다.")
    @Test
    void login() throws Exception {
        //given
        String email = "a@a.com";
        String password = "test1234";
        Member member = Member.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .nickname("테스터")
                .build();

        memberRepository.save(member);

        MemberLoginRequest request = MemberLoginRequest.builder()
                .email(email)
                .password(password)
                .build();

        //then
        mockMvc.perform(
                        post("/api/v1/member/login")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(cookie().exists("accessToken"))
                .andExpect(cookie().value("accessToken", notNullValue()));
    }

    @DisplayName("아이디 비밀번호가 틀리면 로그인에 실패한다.")
    @Test
    void loginWithInvalidIdAndPw() throws Exception {
        //given
        String email = "a@a.com";
        String password = "test1234";
        Member member = Member.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .nickname("테스터")
                .build();

        memberRepository.save(member);

        MemberLoginRequest request = MemberLoginRequest.builder()
                .email("a@a.com")
                .password("test1111")
                .build();

        //then
        mockMvc.perform(
                        post("/api/v1/member/login")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("아이디 또는 비밀번호가 올바르지 않습니다."));
    }

    @DisplayName("로그아웃시 accessToken 쿠키를 삭제한다")
    @Test
    void logout() throws Exception {
        //given
        String email = "a@a.com";
        String password = "test1234";
        Member member = Member.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .nickname("테스터")
                .build();

        memberRepository.save(member);

        MemberLoginRequest request = MemberLoginRequest.builder()
                .email(email)
                .password(password)
                .build();

        mockMvc.perform(
                        post("/api/v1/member/login")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(cookie().exists("accessToken"))
                .andExpect(cookie().value("accessToken", notNullValue()));

        //when
        //then
        mockMvc.perform(
                        post("/api/v1/member/logout")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(cookie().value("accessToken", nullValue()));
    }

}