package com.swm.sprint1.repository.restaurant;

import com.swm.sprint1.domain.Category;
import com.swm.sprint1.domain.Restaurant;
import com.swm.sprint1.payload.response.RestaurantDtoResponse;
import com.swm.sprint1.payload.response.RetrieveRestaurantResponseV1;

import java.math.BigDecimal;
import java.util.List;

public interface RestaurantRepositoryCustom {

    List<RetrieveRestaurantResponseV1> findRetrieveRestaurantByLatitudeAndLongitudeAndUserCategory(BigDecimal latitude, BigDecimal longitude, BigDecimal radius, List<Category> categoryList);

    List<RestaurantDtoResponse> findRestaurantDtoResponseByLatitudeAndLongitudeAndUserCategory(BigDecimal latitude, BigDecimal longitude, BigDecimal radius, Long id);

    List<Restaurant> findByLatitudeAndLongitudeAndCategories(BigDecimal latitude, BigDecimal longitude, BigDecimal radius, List<Category> categoryList);

    List<Restaurant> findByLatitudeAndLongitudeAndCategory(BigDecimal latitude, BigDecimal longitude, BigDecimal radius, Long category_id, Long limit);

    List<RestaurantDtoResponse> findRestaurant7(BigDecimal latitude, BigDecimal longitude, BigDecimal radius, List<Long> ids);

    List<Restaurant> findAllByIdOrderByIdAsc(List<Long> restaurantId);
}
