package com.swm.sprint1.repository.photo;

import com.swm.sprint1.payload.response.RestaurantPhotoResponseDto;

import java.util.List;

public interface RestaurantPhotoRepositoryCustom {
    List<RestaurantPhotoResponseDto> findDtoByRestaurantId(Long restaurantId);
}
