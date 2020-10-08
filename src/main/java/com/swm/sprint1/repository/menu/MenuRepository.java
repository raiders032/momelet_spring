package com.swm.sprint1.repository.menu;

import com.swm.sprint1.domain.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuRepository extends JpaRepository<Menu, Long>, MenuRepositoryCustom{
    List<Menu> findByRestaurantId(Long restaurantId);

    Menu findByRestaurantIdAndId(Long restaurantId, Long menuId);
}
