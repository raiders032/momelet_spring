package com.swm.sprint1.controller;

import com.swm.sprint1.domain.Bookmark;
import com.swm.sprint1.payload.response.ApiResponse;
import com.swm.sprint1.security.CurrentUser;
import com.swm.sprint1.security.UserPrincipal;
import com.swm.sprint1.service.BookmarkService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@PreAuthorize("hasRole('USER')")
@RequiredArgsConstructor
@RestController
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @ApiOperation(value = "북마크 생성", notes = "북마크를 생성합니다.")
    @PostMapping("/api/v1/bookmark/restaurants/{restaurantId}")
    public ResponseEntity<?> createBookmark(@CurrentUser UserPrincipal userPrincipal,
                                            @PathVariable Long restaurantId){
        bookmarkService.createBookmark(userPrincipal.getId(), restaurantId);
        ApiResponse response = new ApiResponse(true, "북마크 생성 완료");
        return ResponseEntity.ok(response);
    }
}
