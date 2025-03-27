package com.app.springnovels;

import com.app.springnovels.api.controller.member.MemberController;
import com.app.springnovels.api.controller.novel.NovelController;
import com.app.springnovels.api.service.auth.JwtService;
import com.app.springnovels.api.service.auth.SecurityContextService;
import com.app.springnovels.api.service.member.MemberService;
import com.app.springnovels.api.service.novel.NovelService;
import com.app.springnovels.config.CorsConfig;
import com.app.springnovels.config.SecurityConfig;
import com.app.springnovels.config.auth.JwtProvider;
import com.app.springnovels.config.auth.MultipleUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@Import({SecurityConfig.class, CorsConfig.class})
@WebMvcTest(controllers = {
        NovelController.class,
        MemberController.class
})
public abstract class ControllerTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    protected NovelService novelService;
    @MockitoBean
    protected MemberService memberService;
    @MockitoBean
    protected JwtService jwtService;
    @MockitoBean
    protected SecurityContextService securityContextService;

}
