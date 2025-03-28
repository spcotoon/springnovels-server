package com.app.springnovels.domain.member;

import com.app.springnovels.api.exception.CoinDeficiencyException;
import com.app.springnovels.domain.novel.Novel;
import com.app.springnovels.domain.purchaseHistory.PurchaseHistory;
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
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private String email;

    private String password;

    private String nickname;

    private boolean isDeleted;

    private LocalDateTime deletedAt;

    private Integer coin;

    @OneToMany(mappedBy = "member")
    private List<PurchaseHistory> purchaseHistories = new ArrayList<>();

    @Builder
    private Member(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.coin = 0;
        this.isDeleted = false;
        this.deletedAt = null;
    }

    public Integer addCoin(int amount) {
        coin += amount;
        return coin;
    }

    public Integer payCoin(int amount) {
        if (coin < amount) {
            throw new CoinDeficiencyException();
        }
        coin -= amount;
        return amount;
    }

    public Integer addWelcomeCoin() {
        coin += 10;
        return coin;
    }
}
