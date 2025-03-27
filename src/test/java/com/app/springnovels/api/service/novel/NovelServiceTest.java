package com.app.springnovels.api.service.novel;

import com.app.springnovels.IntegrationTestSupport;
import com.app.springnovels.api.service.novel.request.NovelCreateServiceRequest;
import com.app.springnovels.api.service.novel.response.NovelResponse;
import com.app.springnovels.domain.author.Author;
import com.app.springnovels.domain.author.AuthorRepository;
import com.app.springnovels.domain.member.Member;
import com.app.springnovels.domain.member.MemberRepository;
import com.app.springnovels.domain.novel.Genre;
import com.app.springnovels.domain.novel.Novel;
import com.app.springnovels.domain.novel.NovelRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

class NovelServiceTest extends IntegrationTestSupport {

    @Autowired
    NovelService novelService;

    @Autowired
    NovelRepository novelRepository;

    @Autowired
    AuthorRepository authorRepository;

    @Autowired
    MemberRepository memberRepository;

    @AfterEach
    void tearDown() {
        novelRepository.deleteAll();
        authorRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @DisplayName("작품을 등록한다.")
    @Test
    void postNovel() throws Exception {
        Author author = Author.builder()
                .email("a@spring.novels.author")
                .password("1234")
                .penName("작가")
                .build();

        Author savedAuthor = authorRepository.save(author);


        //given
        NovelCreateServiceRequest request = NovelCreateServiceRequest.builder()
                .title("1화")
                .genre(Genre.DRAMA)
                .authorId(savedAuthor.getId())
                .content("1화 입니다.")
                .build();

        //when
        NovelResponse novelResponse = novelService.postNovel(request);

        //then
        assertThat(novelResponse).extracting(
                "title",
                "genre",
                "penName",
                "authorId",
                "content"
        ).contains(
                "1화",
                Genre.DRAMA,
                "작가",
                savedAuthor.getId(),
                "1화 입니다."
        );
     }

     @DisplayName("전체 소설 목록을 불러온다")
     @Test
     void getAllNovels() throws Exception {
         //given
         Author author1 = Author.builder()
                 .email("a@spring.novels.author")
                 .password("1234")
                 .penName("a작가")
                 .build();

         Author author2 = Author.builder()
                 .email("a@spring.novels.author")
                 .password("1234")
                 .penName("b작가")
                 .build();

         List<Author> authors = authorRepository.saveAll(List.of(author1, author2));

         Novel novel1 = createNovel("1화", Genre.DRAMA, authors.get(0), "내용");
         Novel novel2 = createNovel("2화", Genre.DRAMA, authors.get(0), "내용");
         Novel novel3 = createNovel("3화", Genre.DRAMA, authors.get(0), "내용");
         Novel novel4 = createNovel("일화", Genre.DRAMA, authors.get(1), "내용");
         Novel novel5 = createNovel("이화", Genre.DRAMA, authors.get(1), "내용");
         Novel novel6 = createNovel("삼화", Genre.DRAMA, authors.get(1), "내용");

         novelRepository.saveAll(List.of(novel1, novel2, novel3, novel4, novel5, novel6));

         //when
         List<NovelResponse> result = novelService.getAllNovels();

         //then
         assertThat(result).hasSize(6);
      }

    @DisplayName("novelId로 한 작품을 불러온다")
    @Test
    void getNovel() throws Exception {
        //given
        Member member = Member.builder()
                .email("a@a.com")
                .password("1234")
                .nickname("테스터")
                .build();
        member.addCoin(10);

        Author author1 = Author.builder()
                .email("a@spring.novels.author")
                .password("1234")
                .penName("a작가")
                .build();

        Author author2 = Author.builder()
                .email("b@spring.novels.author")
                .password("1234")
                .penName("b작가")
                .build();

        Member savedMember = memberRepository.save(member);
        List<Author> authors = authorRepository.saveAll(List.of(author1, author2));

        Novel novel1 = createNovel("1화", Genre.DRAMA, authors.get(0), "내용");
        Novel novel2 = createNovel("2화", Genre.DRAMA, authors.get(0), "내용");
        Novel novel3 = createNovel("3화", Genre.DRAMA, authors.get(0), "내용");
        Novel novel4 = createNovel("일화", Genre.DRAMA, authors.get(1), "내용");
        Novel novel5 = createNovel("이화", Genre.DRAMA, authors.get(1), "내용");
        Novel novel6 = createNovel("삼화", Genre.DRAMA, authors.get(1), "내용");

        List<Novel> novels = novelRepository.saveAll(List.of(novel1, novel2, novel3, novel4, novel5, novel6));

        //when
        NovelResponse result = novelService.getNovel(novels.get(0).getId(), savedMember.getId());

        //then
        assertThat(result).extracting(
                "title",
                "genre",
                "penName",
                "authorId",
                "content"
        ).contains(
                "1화",
                Genre.DRAMA,
                "a작가",
                authors.get(0).getId(),
                "내용"
        );
    }

    @DisplayName("novelId로 한 작품을 불러올대 조회수가 1 증가한다.")
    @Test
    void getNovelAddViewCount() throws Exception {
        //given
        Member member = Member.builder()
                .email("a@a.com")
                .password("1234")
                .nickname("테스터")
                .build();
        member.addCoin(10);

        Author author1 = Author.builder()
                .email("a@spring.novels.author")
                .password("1234")
                .penName("a작가")
                .build();

        Author author2 = Author.builder()
                .email("a@spring.novels.author")
                .password("1234")
                .penName("b작가")
                .build();

        Member savedMember = memberRepository.save(member);
        List<Author> authors = authorRepository.saveAll(List.of(author1, author2));

        Novel novel1 = createNovel("1화", Genre.DRAMA, authors.get(0), "내용");
        Novel novel2 = createNovel("2화", Genre.DRAMA, authors.get(0), "내용");
        Novel novel3 = createNovel("3화", Genre.DRAMA, authors.get(0), "내용");
        Novel novel4 = createNovel("일화", Genre.DRAMA, authors.get(1), "내용");
        Novel novel5 = createNovel("이화", Genre.DRAMA, authors.get(1), "내용");
        Novel novel6 = createNovel("삼화", Genre.DRAMA, authors.get(1), "내용");

        List<Novel> novels = novelRepository.saveAll(List.of(novel1, novel2, novel3, novel4, novel5, novel6));

        //when
        NovelResponse result = novelService.getNovel(novels.get(0).getId(), savedMember.getId());

        //then
        assertThat(result).extracting(
                "title",
                "genre",
                "penName",
                "authorId",
                "content",
                "viewCount"
        ).contains(
                "1화",
                Genre.DRAMA,
                "a작가",
                authors.get(0).getId(),
                "내용",
                1
        );
    }

    @DisplayName("novelId로 한 작품을 불러올 때 멤버의 코인은 소모되고 작가의 코인은 증가된다.")
    @Test
    void getNovelAddViewCountAndCoin() throws Exception {
        //given
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

        Novel novel = createNovel("1화", Genre.DRAMA, savedAuthor, "내용");

        Novel savedNovel = novelRepository.save(novel);

        novelService.getNovel(savedNovel.getId(), savedMember.getId());

        Novel updatedNovel = novelRepository.findById(savedNovel.getId()).orElseThrow();
        Author updatedAuthor = authorRepository.findById(savedAuthor.getId()).orElseThrow();
        Member updatedMember = memberRepository.findById(savedMember.getId()).orElseThrow();

        //when
        //then
        assertThat(updatedNovel).extracting(
                "id",
                "author.id",
                "viewCount"
        ).contains(
                savedNovel.getId(),
                savedNovel.getAuthor().getId(),
                1
        );

        //then
        assertThat(updatedAuthor).extracting(
                "id",
                "salesCoin"
        ).contains(
                savedAuthor.getId(),
                1
        );

        //then
        assertThat(updatedMember).extracting(
                "id",
                "coin"
        ).contains(
                savedMember.getId(),
                9
        );


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
                    novelService.getNovel(savedNovel.getId(), savedMember.getId());
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
                    novelService.getNovel(savedNovel.getId(), savedMember.getId());
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