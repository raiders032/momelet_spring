package com.swm.sprint1.repository.photo;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.swm.sprint1.domain.QRestaurantPhoto;
import com.swm.sprint1.payload.response.RestaurantPhotoResponseDto;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.swm.sprint1.domain.QRestaurantPhoto.restaurantPhoto;

@RequiredArgsConstructor
public class RestaurantPhotoRepositoryImpl implements RestaurantPhotoRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<RestaurantPhotoResponseDto> findDtoByRestaurantId(Long restaurantId) {
        return queryFactory
                .select(Projections.fields(RestaurantPhotoResponseDto.class,
                        restaurantPhoto.id,
                        restaurantPhoto.restaurant.id.as("restaurantId"),
                        restaurantPhoto.user.id.as("userId"),
                        restaurantPhoto.filename,
                        restaurantPhoto.path))
                .from(restaurantPhoto)
                .where(restaurantPhoto.restaurant.id.eq(restaurantId))
                .fetch();
    }
}
