package com.swm.sprint1.repository.user;

import com.swm.sprint1.domain.UserCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserCategoryRepository extends JpaRepository<UserCategory,Long>, UserCategoryRepositoryCustom {
    List<UserCategory> findByUserId(Long id);
}
