package com.app.springnovels.domain.novel;

import com.app.springnovels.domain.author.Author;
import com.app.springnovels.domain.member.Member;
import com.app.springnovels.domain.purchaseHistory.PurchaseHistory;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;

@Getter
public class NovelContextDto {

    private Member member;
    private Author author;
    private Novel novel;
    private Boolean isRead;

    @Builder
    private NovelContextDto(Member member, Author author, Boolean isRead, Novel novel) {
        this.member = member;
        this.author = author;
        this.novel = novel;
        this.isRead = isRead != null ? isRead : false;
    }
}
