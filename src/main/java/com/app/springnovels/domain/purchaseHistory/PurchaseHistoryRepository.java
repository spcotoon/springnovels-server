package com.app.springnovels.domain.purchaseHistory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PurchaseHistoryRepository extends JpaRepository<PurchaseHistory, Long> {

    Optional<PurchaseHistory> findByMemberIdAndNovelId(Long memberId, Long novelId);
}
