package com.swm.sprint1.service;

import com.swm.sprint1.exception.RestaurantLessThan7Exception;
import com.swm.sprint1.payload.response.RestaurantDtoResponse;
import com.swm.sprint1.repository.restaurant.RestaurantRepository;
import com.swm.sprint1.repository.user.UserCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;
    private final Logger logger = LoggerFactory.getLogger(RestaurantService.class);

    public List<RestaurantDtoResponse> findRestaurantDtoResponse(BigDecimal latitude, BigDecimal longitude, BigDecimal radius, Long id) {
        logger.debug("findRestaurantDtoResponse 호출됨");
        return restaurantRepository.findRestaurantDtoResponseByLatitudeAndLongitudeAndUserCategory(latitude,longitude,radius,id);
    }

    public List<RestaurantDtoResponse> findRestaurant7SimpleCategoryBased(List<Long> ids, BigDecimal longitude, BigDecimal latitude, BigDecimal radius) {
        logger.debug("findRestaurant7SimpleCategoryBased 호출");
        List<RestaurantDtoResponse> restaurants = restaurantRepository.findRestaurant7(latitude, longitude, radius, ids);
        if(restaurants.size() < 7){
            logger.info("선택된 식당 카드가 7장 미만 반경을 넓혀 다시 조회합니다.");
            restaurants = restaurantRepository.findRestaurant7(latitude, longitude, BigDecimal.valueOf(0.02), ids);
            if(restaurants.size() < 7){
                logger.error("선택된 식당 카드가 7장 미만입니다.");
                throw new RestaurantLessThan7Exception("선택된 식당 카드가 7장 미만입니다.");
            }
        }
        return restaurants;
    }
}
