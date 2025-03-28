package com.app.springnovels.domain.novel;

import com.app.springnovels.domain.BaseEntity;
import com.app.springnovels.domain.author.Author;
import com.app.springnovels.domain.purchaseHistory.PurchaseHistory;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Novel extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "novel_id")
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

    @OneToMany(mappedBy = "novel")
    private List<PurchaseHistory> purchaseHistories = new ArrayList<>();

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
