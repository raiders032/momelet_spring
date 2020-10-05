package com.swm.sprint1.controller;

import com.swm.sprint1.domain.User;
import com.swm.sprint1.exception.RequestParamException;
import com.swm.sprint1.exception.ResourceNotFoundException;
import com.swm.sprint1.payload.request.UserLikingReqeust;
import com.swm.sprint1.payload.response.ApiResponse;
import com.swm.sprint1.payload.response.UserInfoDto;
import com.swm.sprint1.repository.user.UserRepository;
import com.swm.sprint1.security.CurrentUser;
import com.swm.sprint1.security.UserPrincipal;
import com.swm.sprint1.service.UserLikingService;
import com.swm.sprint1.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Validated
@Api(value = "user")
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final UserLikingService userLikingService;
    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    @ApiOperation(value = "유저의 정보를 반환")
    @GetMapping("/api/v1/users/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> retrieveUserInfo(@CurrentUser UserPrincipal userPrincipal) {
        logger.debug("retrieveUserInfo 호출되었습니다.");
        User user = userPrincipal.getUser();
        Map<String, Integer> categories = userService.findAllCategoryNameByUserId(user.getId());

        UserInfoDto userInfoDto = new UserInfoDto(user.getId(), user.getName(), user.getEmail(), user.getImageUrl(), categories);
        ApiResponse response = new ApiResponse(true);
        response.putData("userInfo", userInfoDto);

        return ResponseEntity.ok(response);
    }

    @ApiOperation(value = "유저 정보 수정")
    @PostMapping("/api/v1/users/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> updateUserInfo(@CurrentUser UserPrincipal userPrincipal,
                                            @RequestParam (required = false) MultipartFile imageFile,
                                            @RequestParam @NotBlank String name,
                                            @RequestParam @NotEmpty List<String> categories,
                                            @PathVariable Long id) throws IOException {
        logger.debug("updateUserInfo 호출되었습니다.");
        if(!id.equals(userPrincipal.getId())) {
            logger.error("jwt token의 유저 아이디와 path param 유저 아이디가 일치하지 않습니다.");
            throw new RequestParamException("jwt token의 유저 아이디와 path param 유저 아이디가 일치하지 않습니다. :" + id, "103");
        }
        userService.updateUser(id, imageFile, name, categories);
        return ResponseEntity
                .ok(new ApiResponse(true, "회원 정보 수정 완료"));
    }

    @ApiOperation(value ="유저 의사표현 저장")
    @PostMapping("/api/v1/users/{id}/liking")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createUserLiking(@CurrentUser UserPrincipal userPrincipal,
                                              @PathVariable Long id,
                                              @Valid @RequestBody UserLikingReqeust userLikingReqeust,
                                              BindingResult result){
        logger.debug("createUserLiking 호출되었습니다.");
        if(!id.equals(userPrincipal.getId())) {
            logger.error("jwt token의 유저 아이디와 path param 유저 아이디가 일치하지 않습니다.");
            throw new RequestParamException("jwt token의 유저 아이디와 path param 유저 아이디가 일치하지 않습니다. :" + id, "103");
        }

        if(result.hasErrors()){
            throw new RequestParamException(result.getAllErrors().toString(),"102");
        }

        List<Long> userLikingId = userLikingService.saveUserLiking(userPrincipal.getId(), userLikingReqeust);

        ApiResponse apiResponse = new ApiResponse(true, "유저 의사 표현 저장 완료");
        apiResponse.putData("userLikingId", userLikingId);
        return ResponseEntity.created(null).body(apiResponse);
    }

    @ApiOperation(value = "유저의 목록을 반환")
    @GetMapping("/api/v1/users")
    @PreAuthorize("hasRole('USER')")
    public List<User> getUserList(@CurrentUser UserPrincipal userPrincipal) {
        logger.debug("getUserList 호출되었습니다.");
        return userRepository.findAllCustom();
    }

    @ApiOperation(value = "유저의 정보를 반환")
    @GetMapping("/users/me")
    @PreAuthorize("hasRole('USER')")
    public User getCurrentUser2(@CurrentUser UserPrincipal userPrincipal) {
        return userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId(), "200"));
    }

}