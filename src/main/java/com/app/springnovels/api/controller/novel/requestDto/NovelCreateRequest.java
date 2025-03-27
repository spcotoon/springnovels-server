package com.app.springnovels.api.controller.novel.requestDto;

import com.app.springnovels.api.service.novel.request.NovelCreateServiceRequest;
import com.app.springnovels.domain.novel.Genre;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NovelCreateRequest {

    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    @NotNull(message = "장르 선택은 필수입니다.")
    private Genre genre;

    private Long authorId;

    @NotBlank(message = "내용은 필수입니다.")
    private String content;

    @Builder
    private NovelCreateRequest(String title, Genre genre, Long authorId, String content) {
        this.title = title;
        this.genre = genre;
        this.authorId = authorId;
        this.content = content;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public NovelCreateServiceRequest toServiceRequest() {
        return NovelCreateServiceRequest.builder()
                .title(title)
                .genre(genre)
                .authorId(authorId)
                .content(content)
                .build();
    }
}
