package com.app.springnovels.api.facade;

import com.app.springnovels.domain.author.Author;
import com.app.springnovels.domain.author.AuthorRepository;
import com.app.springnovels.domain.member.Member;
import com.app.springnovels.domain.member.MemberRepository;
import com.app.springnovels.domain.novel.Genre;
import com.app.springnovels.domain.novel.Novel;
import com.app.springnovels.domain.novel.NovelRepository;
import com.app.springnovels.domain.purchaseHistory.PurchaseHistoryRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
class RedissonLockNovelFacadeTest {

    @Autowired
    RedissonLockNovelFacade redissonLockNovelFacade;

    @Autowired
    NovelRepository novelRepository;

    @Autowired
    AuthorRepository authorRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PurchaseHistoryRepository purchaseHistoryRepository;

    @AfterEach
    void tearDown() {
        purchaseHistoryRepository.deleteAll();
        novelRepository.deleteAll();
        authorRepository.deleteAll();
        memberRepository.deleteAll();
    }


    @DisplayName("novelId로 한 작품을 불러올 때, 100명이 조회하면 증가된 조회수는 100이다.")
    @Test
    void getNovelAddViewCountHundred() throws Exception {
        //given
        Author author = Author.builder()
                .email("a@spring.novels.author")
                .password("1234")
                .penName("a작가")
                .build();

        Author savedAuthor = authorRepository.save(author);

        Novel novel = createNovel("1화", Genre.DRAMA, savedAuthor, "내용");

        Novel savedNovel = novelRepository.save(novel);

        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);

        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            Member member = Member.builder()
                    .email("test" + i + "@a.com")
                    .password("1234")
                    .nickname("테스터" + i)
                    .build();
            member.addCoin(10);

            Member savedMember = memberRepository.save(member);

            executorService.submit(() -> {
                try {
                    redissonLockNovelFacade.getNovel(savedNovel.getId(), savedMember.getId(), LocalDateTime.now());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        //when

        Novel result = novelRepository.findById(savedNovel.getId()).orElseThrow();

        //then
        assertThat(result).extracting(
                "title",
                "genre",
                "author.id",
                "content",
                "viewCount"
        ).contains(
                "1화",
                Genre.DRAMA,
                savedAuthor.getId(),
                "내용",
                100
        );
    }


    @DisplayName("novelId로 한 작품을 불러올 때, 100명이 조회하면 작가는 100개의 코인을 얻는다.")
    @Test
    void getNovelAddCoin() throws Exception {
        //given
        Author author = Author.builder()
                .email("a@spring.novels.author")
                .password("1234")
                .penName("a작가")
                .build();

        Author savedAuthor = authorRepository.save(author);

        Novel novel = createNovel("1화", Genre.DRAMA, savedAuthor, "내용");

        Novel savedNovel = novelRepository.save(novel);

        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);

        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            Member member = Member.builder()
                    .email("test" + i + "@a.com")
                    .password("1234")
                    .nickname("테스터" + i)
                    .build();
            member.addCoin(10);

            Member savedMember = memberRepository.save(member);

            executorService.submit(() -> {
                try {
                    redissonLockNovelFacade.getNovel(savedNovel.getId(), savedMember.getId(), LocalDateTime.now());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        //when

        Author result = authorRepository.findById(savedNovel.getAuthor().getId()).orElseThrow();

        //then
        assertThat(result).extracting(
                "id",
                "penName",
                "salesCoin"
        ).contains(
                savedAuthor.getId(),
                savedAuthor.getPenName(),
                100
        );
    }



    private static Novel createNovel(String title, Genre genre, Author author, String content) {
        return Novel.builder()
                .title(title)
                .genre(genre)
                .author(author)
                .content(content)
                .build();
    }

}