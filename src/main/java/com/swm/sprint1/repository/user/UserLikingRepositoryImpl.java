package com.swm.sprint1.repository.user;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.swm.sprint1.domain.QRestaurant;
import com.swm.sprint1.domain.QUserLiking;
import com.swm.sprint1.domain.UserLiking;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static com.swm.sprint1.domain.QRestaurant.restaurant;
import static com.swm.sprint1.domain.QUserLiking.userLiking;

@RequiredArgsConstructor
public class UserLikingRepositoryImpl implements UserLikingRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<UserLiking> findUserLikingByIdWithRestaurant(Long userLikingId) {
        UserLiking userLiking = queryFactory
                .select(QUserLiking.userLiking)
                .from(QUserLiking.userLiking)
                .join(QUserLiking.userLiking.restaurant, restaurant).fetchJoin()
                .where(QUserLiking.userLiking.id.eq(userLikingId))
                .fetchOne();

        return Optional.ofNullable(userLiking);
    }
}
