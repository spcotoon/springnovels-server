package com.app.springnovels.domain.novel;

import com.app.springnovels.api.service.novel.response.NovelResponse;
import com.app.springnovels.domain.author.Author;
import com.app.springnovels.domain.author.AuthorRepository;
import com.app.springnovels.domain.member.Member;
import com.app.springnovels.domain.member.MemberRepository;
import com.app.springnovels.domain.purchaseHistory.PurchaseHistory;
import com.app.springnovels.domain.purchaseHistory.PurchaseHistoryRepository;
import org.assertj.core.api.Assertions;
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

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
class NovelRepositoryCustomImplTest {

    @Autowired
    private NovelRepository novelRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PurchaseHistoryRepository purchaseHistoryRepository;

    @Autowired
    private NovelRepositoryCustomImpl novelRepositoryCustomImpl;

    @AfterEach
    void tearDown() {
        purchaseHistoryRepository.deleteAll();
        memberRepository.deleteAll();
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


    @DisplayName("멤버,작가,구매내역,노벨 엔티티를 한 DTO에 담는다")
    @Test
    void getMemberAuthorPurchaseNovelDto() throws Exception {
        Member member = Member.builder()
                .email("a@a.com")
                .password("1234")
                .nickname("테스터")
                .build();
        member.addCoin(10);

        Author author = Author.builder()
                .email("a@spring.novels.author")
                .password("1234")
                .penName("a작가")
                .build();


        Member savedMember = memberRepository.save(member);
        Author savedAuthor = authorRepository.save(author);

        Novel novel = Novel.builder()
                .title("1화")
                .author(savedAuthor)
                .genre(Genre.DRAMA)
                .content("내용")
                .build();
        Novel savedNovel = novelRepository.save(novel);

        //when
        NovelContextDto novelContextDto = novelRepository.findNovelContextDto(savedNovel.getId(), savedMember.getId());

        //then
        assertThat(novelContextDto.getMember().getId()).isEqualTo(savedMember.getId());
        assertThat(novelContextDto.getNovel().getId()).isEqualTo(savedNovel.getId());
        assertThat(novelContextDto.getAuthor().getId()).isEqualTo(savedAuthor.getId());
        assertThat(novelContextDto.getIsRead()).isFalse();
     }

    @DisplayName("멤버,작가,구매내역,노벨 엔티티를 한 DTO에 담는다")
    @Test
    void getMemberAuthorPurchaseNovelDtoInPurchaseExist() throws Exception {
        Member member = Member.builder()
                .email("a@a.com")
                .password("1234")
                .nickname("테스터")
                .build();
        member.addCoin(10);

        Author author = Author.builder()
                .email("a@spring.novels.author")
                .password("1234")
                .penName("a작가")
                .build();


        Member savedMember = memberRepository.save(member);
        Author savedAuthor = authorRepository.save(author);

        Novel novel = Novel.builder()
                .title("1화")
                .author(savedAuthor)
                .genre(Genre.DRAMA)
                .content("내용")
                .build();
        Novel savedNovel = novelRepository.save(novel);
        PurchaseHistory purchaseHistory = PurchaseHistory.builder()
                .member(savedMember)
                .novel(savedNovel)
                .isRead(true)
                .purchaseDate(LocalDateTime.now())
                .build();
        purchaseHistoryRepository.save(purchaseHistory);

        //when
        NovelContextDto novelContextDto = novelRepository.findNovelContextDto(savedNovel.getId(), savedMember.getId());

        //then
        assertThat(novelContextDto.getMember().getId()).isEqualTo(savedMember.getId());
        assertThat(novelContextDto.getNovel().getId()).isEqualTo(savedNovel.getId());
        assertThat(novelContextDto.getAuthor().getId()).isEqualTo(savedAuthor.getId());
        assertThat(novelContextDto.getIsRead()).isTrue();
    }
}