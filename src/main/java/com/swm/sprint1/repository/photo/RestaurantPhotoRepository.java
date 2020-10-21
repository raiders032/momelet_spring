package com.swm.sprint1.repository.photo;

import com.swm.sprint1.domain.RestaurantPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantPhotoRepository extends JpaRepository<RestaurantPhoto, Long>, RestaurantPhotoRepositoryCustom {
}
