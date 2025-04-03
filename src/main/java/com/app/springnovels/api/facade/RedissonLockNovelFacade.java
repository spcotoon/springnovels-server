package com.app.springnovels.api.facade;

import com.app.springnovels.api.service.novel.NovelService;
import com.app.springnovels.api.service.novel.response.NovelResponse;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedissonLockNovelFacade {

    private final RedissonClient redissonClient;
    private final NovelService novelService;

    public NovelResponse getNovel(Long novelId, Long memberId, LocalDateTime purchaseDateTime) {
        RLock lock = redissonClient.getLock(novelId.toString());


        try {
            lock.tryLock(10, 1, TimeUnit.SECONDS);
             return novelService.getNovel(novelId, memberId, purchaseDateTime);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }
}
