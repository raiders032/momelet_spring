package com.swm.sprint1.controller;

import com.swm.sprint1.payload.response.ApiResponse;
import com.swm.sprint1.payload.response.MenuDto;
import com.swm.sprint1.repository.MenuRepository;
import com.swm.sprint1.repository.restaurant.RestaurantRepository;
import com.swm.sprint1.security.CurrentUser;
import com.swm.sprint1.security.UserPrincipal;
import com.swm.sprint1.service.MenuService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
public class MenuController {

    private final MenuService menuService;
    private final RestaurantRepository restaurantRepository;

    @ApiOperation(value = "식당 메뉴 추가" , notes = "해당 식당의 메뉴를 추가합니다.")
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/api/v1/restaurant/{restaurantId}/menu")
    public ResponseEntity<?> createMenu(@PathVariable Long restaurantId,
                                        @RequestBody @NotEmpty List<MenuDto> menuDto){

        menuService.createMenu(restaurantId, menuDto);

        ApiResponse response = new ApiResponse(true, "식당 메뉴 추가 완");
        return ResponseEntity.ok(response);
    }

}
