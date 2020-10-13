package com.swm.sprint1.controller;

import com.swm.sprint1.payload.response.ApiResponse;
import com.swm.sprint1.payload.response.PostResponseDto;
import com.swm.sprint1.security.CurrentUser;
import com.swm.sprint1.security.UserPrincipal;
import com.swm.sprint1.service.PostService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.io.IOException;

@PreAuthorize("hasRole('USER')")
@RequiredArgsConstructor
@RestController
public class PostController {

    private final PostService postService;

    @ApiOperation(value = "식당 정보 수정 요청", notes = "식당 정보 수정을 요청합니다.")
    @PostMapping("/api/v1/posts/restaurants/{restaurantId}")
    public ResponseEntity<?> createPost(@CurrentUser UserPrincipal userPrincipal,
                                        @PathVariable Long restaurantId,
                                        @RequestParam (required = false) MultipartFile imageFile,
                                        @RequestParam @NotBlank String claim) throws IOException {

        postService.createPost(userPrincipal.getId(), restaurantId, imageFile, claim);

        ApiResponse response = new ApiResponse(true, "식당 정보 수정 요청 완료");
        return ResponseEntity.ok(response);
    }

    @ApiOperation(value = "식당 정보 수정 요청 조회", notes = "식당 정보 수정 요청을 조회합니다.")
    @GetMapping("/api/v1/posts")
    public ResponseEntity<?> getPost(Pageable pageable){

        Page<PostResponseDto> posts = postService.getPost(pageable);

        ApiResponse response = new ApiResponse(true, "식당 정보 수정 요청 조회 완료");
        response.putData("posts", posts);
        return ResponseEntity.ok(response);
    }

    @ApiOperation(value = "식당 정보 수정 요청 삭제", notes = "식당 정보 수정 요청을 삭제합니다.")
    @DeleteMapping("/api/v1/posts/{postId}")
    public ResponseEntity<?> getPost(@PathVariable Long postId){

        postService.deletePost(postId);

        ApiResponse response = new ApiResponse(true, "식당 정보 수정 요청 삭제 완료");
        return ResponseEntity.ok(response);
    }


}
