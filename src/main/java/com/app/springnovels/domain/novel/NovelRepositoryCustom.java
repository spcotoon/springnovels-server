package com.app.springnovels.domain.novel;

import com.app.springnovels.api.service.novel.response.NovelResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NovelRepositoryCustom {
    Page<NovelResponse> findNovelListByPageable(Pageable pageable);
}
