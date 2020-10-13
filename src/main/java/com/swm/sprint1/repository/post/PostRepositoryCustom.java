package com.swm.sprint1.repository.post;

import com.swm.sprint1.payload.response.PostResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepositoryCustom {
    Page<PostResponseDto> findAllPostResponseDto(Pageable pageable);
}
