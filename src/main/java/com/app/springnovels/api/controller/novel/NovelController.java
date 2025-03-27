package com.app.springnovels.api.controller.novel;

import com.app.springnovels.api.controller.novel.requestDto.NovelCreateRequest;
import com.app.springnovels.api.service.novel.NovelService;
import com.app.springnovels.api.service.novel.response.NovelResponse;
import com.app.springnovels.config.auth.CustomUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/novels")
public class NovelController {

    private final NovelService novelService;

    @PostMapping("/new")
    public ResponseEntity<NovelResponse> createNovel(@Valid @RequestBody NovelCreateRequest request) {
        Long authorId = getCurrentUserId();
        request.setAuthorId(authorId);
        NovelResponse novelResponse = novelService.postNovel(request.toServiceRequest());
        return ResponseEntity.ok(novelResponse);
    }

    @GetMapping("/all")
    public ResponseEntity<List<NovelResponse>> getNovels() {
        List<NovelResponse> allNovels = novelService.getAllNovels();

        return ResponseEntity.of(Optional.ofNullable(allNovels));
    }

    @GetMapping("/{novelId}")
    public ResponseEntity<NovelResponse> getNovelBy(@PathVariable("novelId") Long novelId) {
        Long memberId = getCurrentUserId();
        NovelResponse novelResponse = novelService.getNovel(novelId, memberId);
        return ResponseEntity.ok(novelResponse);
    }

    private Long getCurrentUserId() {
        CustomUser customUser = (CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return customUser.getId();
    }
}
