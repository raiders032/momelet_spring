package com.swm.sprint1.repository.user;

import com.swm.sprint1.domain.UserLiking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserLikingRepository extends JpaRepository<UserLiking,Long>, UserLikingRepositoryCustom {
}
