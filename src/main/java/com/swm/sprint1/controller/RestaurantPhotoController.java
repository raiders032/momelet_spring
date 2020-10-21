package com.swm.sprint1.controller;

import com.swm.sprint1.payload.response.ApiResponse;
import com.swm.sprint1.security.CurrentUser;
import com.swm.sprint1.security.UserPrincipal;
import com.swm.sprint1.service.RestaurantPhotoService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@PreAuthorize("hasRole('USER')")
@RequiredArgsConstructor
@RestController
public class RestaurantPhotoController {

    private final RestaurantPhotoService restaurantPhotoService;

    @ApiOperation(value = "식당 사진 저장", notes = "식당 사진을 저장합니다.")
    @PostMapping("/api/v1/photo/restaurants/{restaurantId}")
    public ResponseEntity<?> createPhoto(@CurrentUser UserPrincipal userPrincipal,
                                         @PathVariable Long restaurantId,
                                         @RequestParam MultipartFile imageFile) throws IOException {
        restaurantPhotoService.createPhoto(userPrincipal.getId(), restaurantId, imageFile);

        ApiResponse response = new ApiResponse(true, "식당 사진 저장완료");
        return ResponseEntity.ok(response);
    }
}
