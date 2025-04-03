package com.app.springnovels.api.service.author;

import com.app.springnovels.IntegrationTestSupport;
import com.app.springnovels.api.exception.InvalidEmailException;
import com.app.springnovels.api.service.author.request.AuthorCreateServiceRequest;
import com.app.springnovels.api.service.author.request.AuthorLoginServiceRequest;
import com.app.springnovels.config.auth.CustomUser;
import com.app.springnovels.domain.author.Author;
import com.app.springnovels.domain.author.AuthorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class AuthorServiceTest extends IntegrationTestSupport {

    @Autowired
    private AuthorService authorService;

    @Autowired
    private AuthorRepository authorRepository;


    @Autowired
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void tearDown() {
        authorRepository.deleteAll();
    }

    @DisplayName("작가는 전용 도메인 @spring.novels.author 아이디로 회원가입을 한다.")
    @Test
    void join() throws Exception {
        String email = "a@spring.novels.author";
        //given
        AuthorCreateServiceRequest request = AuthorCreateServiceRequest.builder()
                .email(email)
                .password("test1234")
                .penName("작가")
                .build();
        //when
        authorService.join(request);

        //then
        assertThat(authorRepository.count()).isEqualTo(1);
        assertThat(authorRepository.findAll().get(0).getEmail()).isEqualTo(email);
     }

     @DisplayName("작가는 전용 도메인 @spring.novels.author 아이디가 아닌 일반 이메일로 가입 시도를 하면 예외가 발생한다.")
     @Test
     void joinWithInvalidEmail() throws Exception {
         //given
         AuthorCreateServiceRequest request = AuthorCreateServiceRequest.builder()
                 .email("a@a.com")
                 .password("test1234")
                 .penName("작가")
                 .build();

         //when
         //then
         assertThatThrownBy(() -> authorService.join(request))
                 .isInstanceOf(InvalidEmailException.class)
                 .hasMessage("유효하지 않은 이메일 입니다.");
      }


    @DisplayName("Author 로그인 시 SecurityContext 에 Author 엔티티 정보를 가진 Authentication 객체가 설정된다")
    @Test
    void loginMember() throws Exception {
        //given
        String joinedEmail = "a@spring.novels.author";
        String encoded = passwordEncoder.encode("test1234");
        Author joinedAuthor = Author.builder()
                .email(joinedEmail)
                .password(encoded)
                .penName("작가")
                .build();
        authorRepository.save(joinedAuthor);

        AuthorLoginServiceRequest request = AuthorLoginServiceRequest.builder()
                .email(joinedEmail)
                .password("test1234")
                .build();

        Authentication authentication = authorService.authenticateJwtAuthor(request);

        CustomUser customUser = (CustomUser) authentication.getPrincipal();

        Long id = customUser.getId();
        String displayName = customUser.getDisplayName();
        String email = customUser.getUsername();

        //when
        //then
        assertThat(id).isEqualTo(joinedAuthor.getId());
        assertThat(displayName).isEqualTo(joinedAuthor.getPenName());
        assertThat(email).isEqualTo(joinedAuthor.getEmail());
    }
}