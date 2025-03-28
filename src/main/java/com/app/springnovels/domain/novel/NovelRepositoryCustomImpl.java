package com.app.springnovels.domain.novel;

import com.app.springnovels.api.service.novel.response.NovelResponse;
import com.app.springnovels.api.service.novel.response.QNovelResponse;
import com.app.springnovels.domain.author.QAuthor;
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
                .join(author)
                .on(novel.author.eq(author))
                .orderBy(novel.createdDateTime.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(novel.count())
                .from(novel);

        return PageableExecutionUtils.getPage(novelResponses, pageable, countQuery::fetchOne);
    }
}
