package com.swm.sprint1.controller;

import com.swm.sprint1.payload.response.ApiResponse;
import com.swm.sprint1.payload.response.MenuDto;
import com.swm.sprint1.payload.response.MenuResponseDto;
import com.swm.sprint1.security.CurrentUser;
import com.swm.sprint1.security.UserPrincipal;
import com.swm.sprint1.service.MenuService;
import com.swm.sprint1.service.PostService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.util.List;

@PreAuthorize("hasRole('USER')")
@RequiredArgsConstructor
@RestController
public class PostController {

    private final PostService postService;

    @ApiOperation(value = "식당 정보 수정 요청", notes = "식당 정보 수정을 요청합니다.")
    @PostMapping("/api/v1/post/restaurant/{restaurantId}")
    public ResponseEntity<?> createPost(@CurrentUser UserPrincipal userPrincipal,
                                        @PathVariable Long restaurantId,
                                        @RequestParam (required = false) MultipartFile imageFile,
                                        @RequestParam @NotBlank String claim) throws IOException {

        postService.createPost(userPrincipal.getId(), restaurantId, imageFile, claim);

        ApiResponse response = new ApiResponse(true, "식당 정보 수정 요청 완료");
        return ResponseEntity.ok(response);
    }

}
