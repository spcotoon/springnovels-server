package com.app.springnovels.domain.novel;

import com.app.springnovels.api.service.novel.response.NovelResponse;
import com.app.springnovels.domain.author.Author;
import com.app.springnovels.domain.author.AuthorRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.util.Comparator;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
class NovelRepositoryCustomImplTest {

    @Autowired
    private NovelRepository novelRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private NovelRepositoryCustomImpl novelRepositoryCustomImpl;

    @AfterEach
    void tearDown() {
        novelRepository.deleteAllInBatch();
        authorRepository.deleteAll();
    }

    @DisplayName("전체 소설 리스트 페이징")
    @Test
    void paging() throws Exception {
        //given
        Author author = Author.builder()
                .email("a@a")
                .password("12341234")
                .penName("작가")
                .build();

        Author savedAuthor = authorRepository.save(author);
        int totalNovelsAmount = 20;
        for (int i = 0; i < totalNovelsAmount; i++) {
            Novel novel = Novel.builder()
                    .title(i + "화")
                    .genre(Genre.DRAMA)
                    .author(savedAuthor)
                    .content(i + "내용")
                    .build();

            novelRepository.save(novel);
        }
        int pageSize = 5;
        Pageable pageable = PageRequest.of(0, pageSize, Sort.by(Sort.Order.desc("createdDateTime")));

        //when
        Page<NovelResponse> result = novelRepositoryCustomImpl.findNovelListByPageable(pageable);

        NovelResponse latestNovel = result.stream()
                .max(Comparator.comparing(NovelResponse::getCreatedDateTime))
                .orElseThrow(() -> new NoSuchElementException("No novel found"));

        //then
        assertThat(result).hasSize(pageSize);
        assertThat(result.getTotalPages()).isEqualTo(totalNovelsAmount / pageSize);
        assertThat(result.getContent().get(0).getId()).isEqualTo(latestNovel.getId());
    }
}