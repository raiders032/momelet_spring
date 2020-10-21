package com.swm.sprint1.controller;

import com.swm.sprint1.domain.RestaurantPhoto;
import com.swm.sprint1.payload.response.ApiResponse;
import com.swm.sprint1.payload.response.RestaurantPhotoResponseDto;
import com.swm.sprint1.security.CurrentUser;
import com.swm.sprint1.security.UserPrincipal;
import com.swm.sprint1.service.RestaurantPhotoService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@PreAuthorize("hasRole('USER')")
@RequiredArgsConstructor
@RestController
public class RestaurantPhotoController {

    private final RestaurantPhotoService restaurantPhotoService;

    @ApiOperation(value = "식당 사진 저장", notes = "식당 사진을 저장합니다.")
    @PostMapping("/api/v1/photos/restaurants/{restaurantId}")
    public ResponseEntity<?> createPhoto(@CurrentUser UserPrincipal userPrincipal,
                                         @PathVariable Long restaurantId,
                                         @RequestParam MultipartFile imageFile) throws IOException {
        restaurantPhotoService.createPhoto(userPrincipal.getId(), restaurantId, imageFile);

        ApiResponse response = new ApiResponse(true, "식당 사진 저장완료");
        return ResponseEntity.ok(response);
    }

    @ApiOperation(value = "식당 사진 조회", notes = "식당 사진을 조회합니다.")
    @GetMapping("/api/v1/photos/restaurants/{restaurantId}")
    public ResponseEntity<?> getPhoto(@PathVariable Long restaurantId){
        List<RestaurantPhotoResponseDto> photos = restaurantPhotoService.findByRestaurantId(restaurantId);

        ApiResponse response = new ApiResponse(true, "식당 사진 저장완료");
        response.putData("photos", photos);
        return ResponseEntity.ok(response);
    }
}
