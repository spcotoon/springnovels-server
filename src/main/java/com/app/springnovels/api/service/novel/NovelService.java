package com.app.springnovels.api.service.novel;

import com.app.springnovels.api.exception.NotEnoughCoinException;
import com.app.springnovels.api.exception.NotExistAuthorException;
import com.app.springnovels.api.exception.NotExistNovelException;
import com.app.springnovels.api.service.novel.request.NovelCreateServiceRequest;
import com.app.springnovels.api.service.novel.response.NovelResponse;
import com.app.springnovels.domain.author.Author;
import com.app.springnovels.domain.author.AuthorRepository;
import com.app.springnovels.domain.member.Member;
import com.app.springnovels.domain.member.MemberRepository;
import com.app.springnovels.domain.novel.Novel;
import com.app.springnovels.domain.novel.NovelRepository;
import com.app.springnovels.domain.purchaseHistory.PurchaseHistory;
import com.app.springnovels.domain.purchaseHistory.PurchaseHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NovelService {

    private final NovelRepository novelRepository;
    private final AuthorRepository authorRepository;
    private final MemberRepository memberRepository;
    private final PurchaseHistoryRepository purchaseHistoryRepository;

    @Transactional
    public NovelResponse postNovel(NovelCreateServiceRequest request) {
        Author author = authorRepository.findById(request.getAuthorId()).orElseThrow(NotExistAuthorException::new);
        Novel novel = request.toEntity(author);
        Novel savedNovel = novelRepository.save(novel);
        return NovelResponse.from(savedNovel);
    }

    public List<NovelResponse> getAllNovels() {

        List<Novel> novels = novelRepository.findAll();

        return novels.stream().map(NovelResponse::from).collect(Collectors.toList());
    }

    @Transactional
    public NovelResponse getNovel(Long novelId, Long memberId, LocalDateTime purchaseDateTime) {
        Novel novel = novelRepository.findByIdWithLock(novelId).orElseThrow(NotExistNovelException::new);
        Member member = memberRepository.findById(memberId).orElseThrow();
        Author author = authorRepository.findById(novel.getAuthor().getId()).orElseThrow();

        Optional<PurchaseHistory> purchaseHistory = purchaseHistoryRepository.findByMemberIdAndNovelId(memberId, novelId);

        if (purchaseHistory.isEmpty()) {
            int payCoin = 1;

            if (member.getCoin() < payCoin) {
                throw new NotEnoughCoinException();
            }

            Integer paidCoin = member.payCoin(payCoin);
            author.addSalesCoin(paidCoin);
            novel.addViewCount();

            PurchaseHistory newPurchaseHistory = PurchaseHistory.builder()
                    .member(member)
                    .novel(novel)
                    .purchaseDate(purchaseDateTime)
                    .isRead(true)
                    .build();

            purchaseHistoryRepository.save(newPurchaseHistory);
        } else if (purchaseHistory.get().isRead()) {
            return NovelResponse.from(novel);
        }
        return NovelResponse.from(novel);
    }

}
