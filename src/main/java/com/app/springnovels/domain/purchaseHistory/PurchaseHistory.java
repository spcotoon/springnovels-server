package com.app.springnovels.domain.purchaseHistory;

import com.app.springnovels.domain.member.Member;
import com.app.springnovels.domain.novel.Novel;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class PurchaseHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "novel_id", nullable = false)
    private Novel novel;

    private LocalDateTime purchaseDate;

    private boolean isRead = false;

    @Builder
    private PurchaseHistory(Member member, Novel novel, LocalDateTime purchaseDate, boolean isRead) {
        this.member = member;
        this.novel = novel;
        this.purchaseDate = purchaseDate;
        this.isRead = isRead;
    }

    public void markAsRead() {
        this.isRead = true;
    }
}
