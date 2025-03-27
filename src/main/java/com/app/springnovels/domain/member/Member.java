package com.app.springnovels.domain.member;

import com.app.springnovels.api.exception.CoinDeficiencyException;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String password;

    private String nickname;

    private boolean isDeleted;

    private LocalDateTime deletedAt;

    private Integer coin;

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
