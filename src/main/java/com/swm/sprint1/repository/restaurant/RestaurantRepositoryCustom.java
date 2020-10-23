package com.swm.sprint1.repository.restaurant;

import com.swm.sprint1.domain.Category;
import com.swm.sprint1.domain.Restaurant;
import com.swm.sprint1.payload.request.RestaurantSearchCondition;
import com.swm.sprint1.payload.response.RestaurantResponseDto;
import com.swm.sprint1.payload.response.RetrieveRestaurantResponseV1;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface RestaurantRepositoryCustom {

    List<RetrieveRestaurantResponseV1> findRetrieveRestaurantByLatitudeAndLongitudeAndUserCategory(BigDecimal latitude, BigDecimal longitude, BigDecimal radius, List<Category> categoryList);

    List<RestaurantResponseDto> findRestaurantDtoResponseByLatitudeAndLongitudeAndUserCategory(BigDecimal latitude, BigDecimal longitude, BigDecimal radius, Long id);

    List<Restaurant> findByLatitudeAndLongitudeAndCategories(BigDecimal latitude, BigDecimal longitude, BigDecimal radius, List<Category> categoryList);

    List<Restaurant> findByLatitudeAndLongitudeAndCategory(BigDecimal latitude, BigDecimal longitude, BigDecimal radius, Long category_id, Long limit);

    List<RestaurantResponseDto> findRestaurant7(BigDecimal latitude, BigDecimal longitude, BigDecimal radius, List<Long> ids);

    List<Restaurant> findAllByIdOrderByIdAsc(List<Long> restaurantId);

    Page<RestaurantResponseDto> findDto(Pageable pageable, RestaurantSearchCondition condition);

    List<RestaurantResponseDto> findDtosByUserCategory(Long userId, RestaurantSearchCondition condition);
}
