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
@PreAuthorize("hasRole('USER')")
public class MenuController {

    private final MenuService menuService;
    private final RestaurantRepository restaurantRepository;

    @ApiOperation(value = "식당 메뉴 추가" , notes = "해당 식당의 메뉴를 추가합니다.")
    @PostMapping("/api/v1/restaurant/{restaurantId}/menu")
    public ResponseEntity<?> createMenu(@PathVariable Long restaurantId,
                                        @RequestBody @NotEmpty List<MenuDto> menuDto){

        menuService.createMenu(restaurantId, menuDto);

        ApiResponse response = new ApiResponse(true, "식당 메뉴 추가 완");
        return ResponseEntity.ok(response);
    }

    @ApiOperation(value = "식당 메뉴 삭제", notes = "해당 식당의 메뉴를 삭제합니다.")
    @DeleteMapping("/api/v1/restaurant/{restaurantId}/menu/{menuId}")
    public ResponseEntity<?> deleteMenu(@PathVariable Long restaurantId, @PathVariable Long menuId){
        menuService.deleteMenu(restaurantId, menuId);
        ApiResponse response = new ApiResponse(true, "식당 메뉴 삭제 완료");
        return ResponseEntity.ok(response);
    }

    @ApiOperation(value="식당 메뉴 수정", notes="해당 식당의 메뉴를 수정합니다.")
    @PutMapping("/api/v1/restaurant/{restaurantId}/menu/{menuId}")
    public ResponseEntity<?> updateMenu(@PathVariable Long restaurantId,
                                        @PathVariable Long menuId, @RequestBody MenuDto menuDto){
        menuService.updateMenu(restaurantId, menuId, menuDto);
        ApiResponse response = new ApiResponse(true, "식당 메뉴 수정 완료");
        return ResponseEntity.ok(response);
    }
    

}
