package com.swm.sprint1.controller;

import com.swm.sprint1.payload.response.ApiResponse;
import com.swm.sprint1.payload.response.BookmarkResponseDto;
import com.swm.sprint1.security.CurrentUser;
import com.swm.sprint1.security.UserPrincipal;
import com.swm.sprint1.service.BookmarkService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@PreAuthorize("hasRole('USER')")
@RequiredArgsConstructor
@RestController
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @ApiOperation(value = "북마크 생성", notes = "북마크를 생성합니다.")
    @PostMapping("/api/v1/bookmarks/restaurants/{restaurantId}")
    public ResponseEntity<?> createBookmark(@CurrentUser UserPrincipal userPrincipal,
                                            @PathVariable Long restaurantId){
        bookmarkService.createBookmark(userPrincipal.getId(), restaurantId);
        ApiResponse response = new ApiResponse(true, "북마크 생성 완료");
        return ResponseEntity.ok(response);
    }

    @ApiOperation(value = "북마크 조회", notes = "북마크를 조회합니다.")
    @GetMapping("/api/v1/bookmarks")
    public ResponseEntity<?> createBookmark(@CurrentUser UserPrincipal userPrincipal,
                                            Pageable pageable){
        Page<BookmarkResponseDto> bookmarks = bookmarkService.getBookmark(userPrincipal.getId(), pageable);
        ApiResponse response = new ApiResponse(true, "북마크 조회 완료");
        response.putData("bookmarks", bookmarks);
        return ResponseEntity.ok(response);
    }

    @ApiOperation(value = "북마크 삭제", notes = "북마크를 삭제합니다.")
    @DeleteMapping("/api/v1/bookmarks/{bookmarkId}")
    public ResponseEntity<?> deleteBookmark(@CurrentUser UserPrincipal userPrincipal,
                                            @PathVariable Long bookmarkId){
        bookmarkService.deleteBookmark(userPrincipal.getId(), bookmarkId);
        ApiResponse response = new ApiResponse(true, "북마크 삭제 완료");
        return ResponseEntity.ok(response);
    }



}
