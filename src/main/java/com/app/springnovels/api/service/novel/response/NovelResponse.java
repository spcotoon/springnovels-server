package com.app.springnovels.api.service.novel.response;

import com.app.springnovels.domain.novel.Genre;
import com.app.springnovels.domain.novel.Novel;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class NovelResponse {

    private Long id;
    private String title;
    private Genre genre;
    private String penName;
    private Long authorId;
    private String content;
    private LocalDateTime createdDateTime;
    private int viewCount;

    @Builder
    @QueryProjection
    public NovelResponse(Long id, String title, Genre genre, String penName, Long authorId, String content, LocalDateTime createdDateTime, int viewCount) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.penName = penName;
        this.authorId = authorId;
        this.content = content;
        this.createdDateTime = createdDateTime;
        this.viewCount = viewCount;
    }

    public static NovelResponse from(Novel novel) {
        return NovelResponse.builder()
                .id(novel.getId())
                .title(novel.getTitle())
                .genre(novel.getGenre())
                .penName(novel.getAuthor().getPenName())
                .authorId(novel.getAuthor().getId())
                .content(novel.getContent())
                .createdDateTime(novel.getCreatedDateTime())
                .viewCount(novel.getViewCount())
                .build();
    }
}
