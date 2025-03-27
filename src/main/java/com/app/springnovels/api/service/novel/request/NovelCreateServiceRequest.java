package com.app.springnovels.api.service.novel.request;

import com.app.springnovels.domain.author.Author;
import com.app.springnovels.domain.novel.Genre;
import com.app.springnovels.domain.novel.Novel;
import lombok.Builder;
import lombok.Getter;

@Getter
public class NovelCreateServiceRequest {

    private String title;
    private Genre genre;
    private Long authorId;
    private String content;

    @Builder
    private NovelCreateServiceRequest(String title, Genre genre, Long authorId, String content) {
        this.title = title;
        this.genre = genre;
        this.authorId = authorId;
        this.content = content;
    }

    public Novel toEntity(Author author) {
        return Novel.builder()
                .title(title)
                .genre(genre)
                .author(author)
                .content(content)
                .build();
    }
}
