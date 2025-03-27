package com.app.springnovels.domain.author;

import com.app.springnovels.domain.novel.Novel;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "author_id")
    private Long id;

    private String email;
    private String password;
    private String penName;
    private int salesCoin;
    private boolean isDeleted;
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "author")
    private List<Novel> novels = new ArrayList<>();

    @Builder
    private Author(String email, String password, String penName) {
        this.email = email;
        this.password = password;
        this.penName = penName;
        this.salesCoin = 0;
        this.isDeleted = false;
        this.deletedAt = null;
    }

    public void addSalesCoin(int amount) {
        salesCoin+=amount;
    }


}
