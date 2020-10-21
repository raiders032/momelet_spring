package com.swm.sprint1.repository.photo;

import com.swm.sprint1.domain.RestaurantPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RestaurantPhotoRepository extends JpaRepository<RestaurantPhoto, Long>, RestaurantPhotoRepositoryCustom {
    Optional<RestaurantPhoto> findByUserIdAndId(Long userId, Long id);
}
