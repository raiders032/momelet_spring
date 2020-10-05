package com.swm.sprint1.service;

import com.swm.sprint1.domain.Restaurant;
import com.swm.sprint1.domain.User;
import com.swm.sprint1.domain.UserLiking;
import com.swm.sprint1.exception.ResourceNotFoundException;
import com.swm.sprint1.payload.request.UserLikingDto;
import com.swm.sprint1.payload.request.UserLikingReqeust;
import com.swm.sprint1.repository.restaurant.RestaurantRepository;
import com.swm.sprint1.repository.user.UserLikingRepository;
import com.swm.sprint1.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserLikingService {
    private final UserLikingRepository userLikingRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final Logger logger = LoggerFactory.getLogger(UserLikingService.class);

    @Transactional
    public List<Long> saveUserLiking(Long userId, UserLikingReqeust userLikingReqeust) {
        logger.debug("saveUserLiking 호출됨");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("user", "id", userId, "200"));

        List<UserLikingDto> userLikingList = userLikingReqeust.getUserLiking();
        List<Long> restaurantIds = userLikingList.stream().map(UserLikingDto::getRestaurantId).collect(Collectors.toList());
        List<Restaurant> restaurants = restaurantRepository.findAllByIdOrderByIdAsc(restaurantIds);
        if(restaurants.size() != 7){
            throw new ResourceNotFoundException("restaurant", "id", "식당  존재하지 않는 식당 아이디가 포함되어 있습니다.","210");
        }
        List<UserLiking> userLikings = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            UserLiking userLiking = UserLiking.builder()
                    .user(user)
                    .restaurant(restaurants.get(i))
                    .liking(userLikingList.get(i).getLiking())
                    .userLatitude(userLikingReqeust.getUserLatitude())
                    .userLongitude(userLikingReqeust.getUserLongitude())
                    .elapsedTime(userLikingList.get(i).getElapsedTime())
                    .build();

            userLikings.add(userLiking);
        }

        List<UserLiking> savedUserLikings = userLikingRepository.saveAll(userLikings);
        return savedUserLikings.stream().map(UserLiking::getId).collect(Collectors.toList());
    }
}
