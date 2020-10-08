package com.swm.sprint1.service;

import com.swm.sprint1.domain.Menu;
import com.swm.sprint1.domain.Restaurant;
import com.swm.sprint1.exception.ResourceNotFoundException;
import com.swm.sprint1.payload.response.MenuDto;
import com.swm.sprint1.payload.response.MenuResponseDto;
import com.swm.sprint1.repository.menu.MenuRepository;
import com.swm.sprint1.repository.restaurant.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MenuService {

    private final MenuRepository menuRepository;
    private final RestaurantRepository restaurantRepository;

    @Transactional
    public void createMenu(Long restaurantId, List<MenuDto> menuDtoList) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant", "id", restaurantId, "210"));
        List<Menu> collect = menuDtoList.stream().map(menuDto -> new Menu(restaurant, menuDto)).collect(Collectors.toList());
        menuRepository.saveAll(collect);
    }

    @Transactional
    public void deleteMenu(Long restaurantId, Long menuId) {
        Menu menu = menuRepository.findByRestaurantIdAndId(restaurantId, menuId);
        menuRepository.delete(menu);
    }

    @Transactional
    public void updateMenu(Long restaurantId, Long menuId, MenuDto menuDto) {
        Menu menu = menuRepository.findByRestaurantIdAndId(restaurantId, menuId);
        menu.update(menuDto.getName(), menuDto.getPrice());
    }

    public List<MenuResponseDto> getAllMenu(Long restaurantId) {
        return menuRepository.findMenuResponseDto(restaurantId);
    }
}
