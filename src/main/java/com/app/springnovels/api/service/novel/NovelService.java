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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
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

    public Page<NovelResponse> getAllNovels(int pageNum) {

        int pageSize = 20;

        Pageable requestPageable = PageRequest.of(pageNum, pageSize);

        return novelRepository.findNovelListByPageable(requestPageable);
    }


    @Transactional
    public NovelResponse getNovel(Long novelId, Long memberId, LocalDateTime purchaseDateTime) {
        log.info("request: {}", memberId);
        Novel novel = novelRepository.findById(novelId).orElseThrow(NotExistNovelException::new);
        Member member = memberRepository.findById(memberId).orElseThrow();
        Author author = authorRepository.findById(novel.getAuthor().getId()).orElseThrow();

        Optional<PurchaseHistory> purchaseHistory = purchaseHistoryRepository.findByMemberIdAndNovelId(memberId, novelId);

        if (purchaseHistory.isEmpty()) {
            int payCoin = 1;

            if (member.getCoin() < payCoin) {
                log.warn("Not enough coins. memberId={}, currentCoins={}, requiredCoins={}", memberId, member.getCoin(), payCoin);
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

            log.info("Saving new purchase history. memberId={}, novelId={}, purchaseDateTime={}", memberId, novelId, purchaseDateTime);
            purchaseHistoryRepository.save(newPurchaseHistory);
        } else if (purchaseHistory.get().isRead()) {
            return NovelResponse.from(novel);
        }
        return NovelResponse.from(novel);
    }


}
