package com.swm.sprint1.repository.user;

import com.swm.sprint1.domain.UserLiking;

import java.util.Optional;

public interface UserLikingRepositoryCustom {
    Optional<UserLiking> findUserLikingByIdWithRestaurant(Long userLikingId);
}
