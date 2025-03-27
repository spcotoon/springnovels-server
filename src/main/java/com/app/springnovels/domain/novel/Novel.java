package com.app.springnovels.domain.novel;

import com.app.springnovels.domain.BaseEntity;
import com.app.springnovels.domain.author.Author;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Novel extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Enumerated(EnumType.STRING)
    private Genre genre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private Author author;

    @Column(columnDefinition = "TEXT")
    private String content;

    private int viewCount;

    @Builder
    private Novel(String title, Genre genre, Author author, String content) {
        this.title = title;
        this.genre = genre;
        this.author = author;
        this.content = content;
    }

    public void addViewCount() {
        viewCount++;
    }

}
