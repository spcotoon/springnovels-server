package com.app.springnovels.api.controller.novel;

import com.app.springnovels.ControllerTestSupport;
import com.app.springnovels.api.controller.novel.requestDto.NovelCreateRequest;
import com.app.springnovels.api.service.auth.SecurityContextService;
import com.app.springnovels.api.service.novel.request.NovelCreateServiceRequest;
import com.app.springnovels.api.service.novel.response.NovelResponse;
import com.app.springnovels.domain.author.Author;
import com.app.springnovels.domain.member.Member;
import com.app.springnovels.domain.novel.Genre;
import com.app.springnovels.mock.MockAuthor;
import com.app.springnovels.mock.MockMember;
import com.app.springnovels.mock.MockMemberSecurityContextFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class NovelControllerTest extends ControllerTestSupport {

    @DisplayName("신규 소설을 등록한다")
    @Test
    @MockAuthor
    void createNovel() throws Exception {

        //given //when
        NovelCreateRequest request = NovelCreateRequest.builder()
                .title("1화")
                .genre(Genre.DRAMA)
                .authorId(1L)
                .content("1화 입니다")
                .build();

        NovelResponse mockResponse = NovelResponse.builder()
                .id(1L)
                .title(request.getTitle())
                .genre(request.getGenre())
                .penName(String.valueOf(request.getAuthorId()))
                .authorId(request.getAuthorId())
                .content(request.getContent())
                .build();

        given(novelService.postNovel(any(NovelCreateServiceRequest.class))).willReturn(mockResponse);

        //then
        mockMvc.perform(
                post("/api/v1/novels/new")
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
     }

    @DisplayName("신규 소설을 등록할 때, 제목은 필수이다.")
    @Test
    @MockAuthor
    void createNovelWithoutTitle() throws Exception {
        //given
        NovelCreateRequest request = NovelCreateRequest.builder()
                .title("")
                .genre(Genre.DRAMA)
                .authorId(1L)
                .content("1화 입니다")
                .build();

        //when
        //then
        mockMvc.perform(
                        post("/api/v1/novels/new")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation[0].fieldName").value("title"))
                .andExpect(jsonPath("$.validation[0].errorMessage").value("제목은 필수입니다."));
    }

    @DisplayName("신규 소설을 등록할 때, 장르, 제목은 필수이며 동시에 여러 필드를 입력하지 않으면 모두 validation 배열에 포함된다.")
    @Test
    @MockAuthor
    void createNovelWithoutGenreAndContent() throws Exception {
        //given
        NovelCreateRequest request = NovelCreateRequest.builder()
                .title("1화")
                .genre(null)
                .authorId(1L)
                .content("")
                .build();

        //when
        //then
        mockMvc.perform(
                        post("/api/v1/novels/new")
                                .with(csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation[?(@.fieldName == 'genre')].errorMessage").value("장르 선택은 필수입니다."))
                .andExpect(jsonPath("$.validation[?(@.fieldName == 'content')].errorMessage").value("내용은 필수입니다."));
    }

    @DisplayName("업로드된 소설들의 리스트를 불러온다")
    @Test
    @WithMockUser
    void getAllNovels() throws Exception {
        // given
        List<NovelResponse> mockNovels = List.of(
                NovelResponse.builder()
                        .id(1L)
                        .title("1화")
                        .genre(Genre.DRAMA)
                        .authorId(1L)
                        .content("내용")
                        .createdDateTime(LocalDateTime.now())
                        .build(),
                NovelResponse.builder()
                        .id(2L)
                        .title("2화")
                        .genre(Genre.DRAMA)
                        .authorId(2L)
                        .content("내용")
                        .createdDateTime(LocalDateTime.now())
                        .build()
        );
        int pageNum = 0;
        int pageSize = 20;
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        Page<NovelResponse> mockPage = new PageImpl<>(mockNovels, pageable, mockNovels.size());

        given(novelService.getAllNovels(pageNum)).willReturn(mockPage);


        // then
        mockMvc.perform(get("/api/v1/novels/all").with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].title").value("1화"))
                .andExpect(jsonPath("$.content[1].title").value("2화"));
    }

    @DisplayName("소설의 아이디로 작품 하나를 불러온다")
    @Test
    @MockMember
    void getNovelById() throws Exception {
        // given
        NovelResponse mockNovel = NovelResponse.builder()
                .id(1L)
                .title("1화")
                .genre(Genre.DRAMA)
                .authorId(1L)
                .content("내용")
                .createdDateTime(LocalDateTime.now())
                .viewCount(1)
                .build();

        given(novelService.getNovel(anyLong(), anyLong(), any(LocalDateTime.class))).willReturn(mockNovel);

        NovelResponse result = novelService.getNovel(1L, 1L, LocalDateTime.now());
        System.out.println(result);

        // when
        // then
        mockMvc.perform(get("/api/v1/novels/1").with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("1화"));
    }

}