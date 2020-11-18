package com.swm.sprint1.repository.bookmark;

import com.swm.sprint1.payload.response.BookmarkResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookmarkRepositoryCustom {
    Page<BookmarkResponseDto> findDtosByUserId(Long userId, Pageable pageable);

    Page<BookmarkResponseDto> findDtosByUserIdOrderByLike(Long userId, Pageable pageable);

    Page<BookmarkResponseDto> findDtosByUserIdOrderById(Long userId, Pageable pageable);
}
