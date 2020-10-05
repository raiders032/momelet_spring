package com.swm.sprint1.controller;

import com.swm.sprint1.exception.RequestParamException;
import com.swm.sprint1.payload.response.ApiResponse;
import com.swm.sprint1.payload.response.RestaurantDtoResponse;
import com.swm.sprint1.security.CurrentUser;
import com.swm.sprint1.security.UserPrincipal;
import com.swm.sprint1.service.RestaurantService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Validated
@RequiredArgsConstructor
@RestController
public class RestaurantController {

    private final RestaurantService restaurantService;

    private final Logger logger = LoggerFactory.getLogger(RestaurantController.class);

    @ApiOperation(value = "유저 카테고리 기반 식당 조회" , notes = "유저의 카테고리를 기반으로 하여 최대 100개의 주변 식당 목록을 반환합니다.")
    @GetMapping("/api/v1/restaurants/users/{id}/categories")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getRestaurantWithUserCategory(@CurrentUser UserPrincipal userPrincipal,
                                                           @RequestParam @DecimalMin("122") @DecimalMax("133")BigDecimal longitude,
                                                           @RequestParam @DecimalMin("32") @DecimalMax("43")BigDecimal latitude,
                                                           @RequestParam @DecimalMin("0.001") @DecimalMax("0.02") BigDecimal radius,
                                                           @PathVariable Long id){
        logger.debug("getRestaurantWithUserCategory 호출되었습니다.");
        if(!id.equals(userPrincipal.getId())) {
            logger.error("jwt token의 유저 아이디와 path param 유저 아이디가 일치하지 않습니다.");
            throw new RequestParamException("jwt token의 유저 아이디와 path param 유저 아이디가 일치하지 않습니다. :" + id, "103");
        }
        List<RestaurantDtoResponse> restaurants = restaurantService.findRestaurantDtoResponse(latitude, longitude, radius, userPrincipal.getId());
        ApiResponse response = new ApiResponse(true);
        response.putData("restaurants", restaurants);

        return ResponseEntity.ok(response);
    }

    @ApiOperation(value = "유저들의 카테고리 기반 식당 카드 7장 조회" , notes = "유저들 카테고리를 기반으로 하여 7개의 주변 식당 목록을 반환합니다.")
    @GetMapping("/api/v1/restaurants7")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getRestaurant7SimpleCategoryBased(@RequestParam @NotBlank String id,
                                                               @RequestParam @NotNull @DecimalMin("123") @DecimalMax("133")BigDecimal longitude,
                                                               @RequestParam @DecimalMin("32") @DecimalMax("43")BigDecimal latitude,
                                                               @RequestParam @DecimalMin("0.001") @DecimalMax("0.02") BigDecimal radius){
        logger.debug("getRestaurant7SimpleCategoryBased 호출되었습니다.");
        List<Long> ids = Arrays.stream(id.split(",")).map(Long::parseLong).collect(Collectors.toList());
        List<RestaurantDtoResponse> restaurants = restaurantService.findRestaurant7SimpleCategoryBased(ids,longitude,latitude,radius);
        ApiResponse response = new ApiResponse(true);
        response.putData("restaurants", restaurants);

        return ResponseEntity.ok(response);
    }
}
