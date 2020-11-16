package com.swm.sprint1.service;

import com.swm.sprint1.exception.ResourceNotFoundException;
import com.swm.sprint1.exception.RestaurantLessThan7Exception;
import com.swm.sprint1.payload.request.RestaurantSearchCondition;
import com.swm.sprint1.payload.response.RestaurantResponseDto;
import com.swm.sprint1.repository.restaurant.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;
    private final Logger logger = LoggerFactory.getLogger(RestaurantService.class);

    public List<RestaurantResponseDto> findDtosByUserCategory(Long userId, BigDecimal longitude, BigDecimal latitude, BigDecimal radius) {
        return restaurantRepository.findDtosByUserCategory(userId, longitude, latitude, radius);
    }

    public Page<RestaurantResponseDto> searchRestaurants(Pageable pageable, RestaurantSearchCondition condition) {
        if(condition.getFilter().equals("like")) {
            return restaurantRepository.searchRestaurantsOrderByLikeCount(pageable, condition);
        }
        return restaurantRepository.searchRestaurantsOrderByDistance(pageable, condition);
    }

    public List<RestaurantResponseDto> findGameCards(List<Long> userIds, List<Long> restaurantIds, BigDecimal longitude, BigDecimal latitude, BigDecimal radius) {
        logger.debug("findDtos 호출");

        Set<Long> restaurantIdsSet = new HashSet<>(restaurantIds);
        List<RestaurantResponseDto> restaurants = new ArrayList<>(restaurantRepository.findDtosById(new ArrayList<>(restaurantIdsSet)));

        if(restaurantIdsSet.size() < 7){
            List<RestaurantResponseDto> dtos = restaurantRepository.findDtos(userIds, latitude, longitude, radius, 50);
            Iterator<RestaurantResponseDto> iterator = dtos.iterator();

            while(restaurants.size() < 7 && iterator.hasNext()){
                RestaurantResponseDto dto = iterator.next();
                if(restaurants.contains(dto))
                    continue;
                restaurants.add(dto);
            }
        }
        if(restaurants.size() < 7 )
            throw new RestaurantLessThan7Exception("식당 카드가 7장 미만입니다.");
        return restaurants;
    }

    public RestaurantResponseDto findDtoById(Long restaurantId) {
        List<RestaurantResponseDto> dtosById = restaurantRepository.findDtosById(Arrays.asList(restaurantId));
        if(!dtosById.isEmpty())
            return dtosById.get(0);
        else
            throw new ResourceNotFoundException("Restaurant", "id", restaurantId, "210");
    }
}
