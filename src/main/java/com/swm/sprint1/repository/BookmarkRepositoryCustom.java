package com.swm.sprint1.repository;

import com.swm.sprint1.payload.response.BookmarkResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookmarkRepositoryCustom {
    Page<BookmarkResponseDto> findAllByUserId(Long userId, Pageable pageable);
}
