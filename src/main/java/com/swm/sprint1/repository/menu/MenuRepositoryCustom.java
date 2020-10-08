package com.swm.sprint1.repository.menu;

import com.swm.sprint1.payload.response.MenuResponseDto;

import java.util.List;

public interface MenuRepositoryCustom {
    List<MenuResponseDto> findMenuResponseDto(Long restaurantId);
}
