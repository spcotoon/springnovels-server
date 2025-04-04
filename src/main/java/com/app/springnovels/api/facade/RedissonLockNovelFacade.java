package com.app.springnovels.api.facade;

import com.app.springnovels.api.service.novel.NovelService;
import com.app.springnovels.api.service.novel.response.NovelResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedissonLockNovelFacade {

    private final RedissonClient redissonClient;
    private final NovelService novelService;

    public NovelResponse getNovel(Long novelId, Long memberId, LocalDateTime purchaseDateTime) {
        RLock lock = redissonClient.getLock(novelId.toString());
        boolean available = false;

        try {
            available = lock.tryLock(10, 1, TimeUnit.SECONDS);

            if (!available) {
                log.info("get lock failed");
            }

             return novelService.getNovel(novelId, memberId, purchaseDateTime);
        } catch (InterruptedException e) {
            log.info("high traffic, try again please");
            throw new RuntimeException(e);
        } finally {
            if (available && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
