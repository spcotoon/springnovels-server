package com.app.springnovels.api.controller.author;

import com.app.springnovels.api.controller.author.requestDto.AuthorCreateRequest;
import com.app.springnovels.api.controller.member.requestDto.MemberCreateRequest;
import com.app.springnovels.api.service.author.AuthorService;
import com.app.springnovels.api.service.member.MemberService;
import com.app.springnovels.domain.author.AuthorRepository;
import com.app.springnovels.domain.member.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthorControllerTest {


    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthorRepository authorRepository;

    @Autowired
    AuthorService authorService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @DisplayName("회원가입을 할 수 있다.")
    @Test
    void join() throws Exception {
        //given
        AuthorCreateRequest request = AuthorCreateRequest.builder()
                .email("a@spring.novels.author")
                .password("test1234")
                .penName("작가")
                .build();

        //when
        //then
        mockMvc.perform(
                        post("/api/v1/author/join")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

}