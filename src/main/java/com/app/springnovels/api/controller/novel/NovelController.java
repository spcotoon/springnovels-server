package com.app.springnovels.api.controller.novel;

import com.app.springnovels.api.controller.novel.requestDto.NovelCreateRequest;
import com.app.springnovels.api.facade.RedissonLockNovelFacade;
import com.app.springnovels.api.service.novel.NovelService;
import com.app.springnovels.api.service.novel.response.NovelResponse;
import com.app.springnovels.config.auth.CustomUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/novels")
public class NovelController {

    private final NovelService novelService;
    private final RedissonLockNovelFacade redissonLockNovelFacade;

    @PostMapping("/new")
    public ResponseEntity<NovelResponse> createNovel(@Valid @RequestBody NovelCreateRequest request) {
        Long authorId = getCurrentUserId();
        request.setAuthorId(authorId);
        NovelResponse novelResponse = novelService.postNovel(request.toServiceRequest());
        return ResponseEntity.ok(novelResponse);
    }

    @GetMapping("/all")
    public ResponseEntity<PagedModel<NovelResponse>> getNovels(
            @RequestParam(name = "page", defaultValue = "0") int pageNum
            ) {
        return ResponseEntity.ok(new PagedModel<>(novelService.getAllNovels(pageNum)));
    }

    @GetMapping("/{novelId}")
    public ResponseEntity<NovelResponse> getNovelBy(@PathVariable("novelId") Long novelId) {
        Long memberId = getCurrentUserId();
        LocalDateTime purchaseDateTime = LocalDateTime.now();
        NovelResponse novelResponse = redissonLockNovelFacade.getNovel(novelId, memberId,purchaseDateTime);
        return ResponseEntity.ok(novelResponse);
    }

    private Long getCurrentUserId() {
        CustomUser customUser = (CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return customUser.getId();
    }
}
