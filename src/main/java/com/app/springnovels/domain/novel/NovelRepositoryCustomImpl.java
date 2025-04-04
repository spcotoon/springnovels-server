package com.app.springnovels.domain.novel;

import com.app.springnovels.api.service.novel.response.NovelResponse;
import com.app.springnovels.api.service.novel.response.QNovelResponse;
import com.app.springnovels.domain.author.QAuthor;
import com.app.springnovels.domain.member.QMember;
import com.app.springnovels.domain.purchaseHistory.QPurchaseHistory;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

@RequiredArgsConstructor
public class NovelRepositoryCustomImpl implements NovelRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;
    private final QNovel novel = QNovel.novel;
    private final QAuthor author = QAuthor.author;
    private final QMember member = QMember.member;
    private final QPurchaseHistory purchaseHistory = QPurchaseHistory.purchaseHistory;

    @Override
    public Page<NovelResponse> findNovelListByPageable(Pageable pageable) {

        List<NovelResponse> novelResponses = jpaQueryFactory
                .select(new QNovelResponse(
                        novel.id,
                        novel.title,
                        novel.genre,
                        novel.author.penName,
                        novel.author.id,
                        novel.content,
                        novel.createdDateTime,
                        novel.viewCount
                ))
                .from(novel)
                .leftJoin(novel.author, author)
                .orderBy(novel.createdDateTime.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(novel.count())
                .from(novel);

        return PageableExecutionUtils.getPage(novelResponses, pageable, countQuery::fetchOne);
    }

    @Override
    public NovelContextDto findNovelContextDto(Long novelId, Long memberId) {

        Tuple tuple = jpaQueryFactory
                .select(member, author, novel, purchaseHistory.isRead.coalesce(false))
                .from(novel)
                .join(novel.author, author).fetchJoin()
                .leftJoin(purchaseHistory)
                .on(purchaseHistory.novel.id.eq(novel.id)
                        .and(purchaseHistory.member.id.eq(memberId)))
                .join(member).on(member.id.eq(memberId)).fetchJoin()
                .where(novel.id.eq(novelId))
                .fetchOne();

        if (tuple == null) return null;

        return NovelContextDto.builder()
                .author(tuple.get(author))
                .member(tuple.get(member))
                .isRead(tuple.get(purchaseHistory.isRead.coalesce(false)))
                .novel(tuple.get(novel))
                .build();
    }

}
