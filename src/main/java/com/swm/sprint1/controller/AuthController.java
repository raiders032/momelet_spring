package com.swm.sprint1.controller;

import com.swm.sprint1.domain.AuthProvider;
import com.swm.sprint1.domain.User;
import com.swm.sprint1.exception.BadRequestException;
import com.swm.sprint1.exception.RequestParamException;
import com.swm.sprint1.payload.request.JwtDto;
import com.swm.sprint1.payload.request.LoginRequest;
import com.swm.sprint1.payload.request.SignUpRequest;
import com.swm.sprint1.payload.response.ApiResponse;
import com.swm.sprint1.payload.response.AuthResponse;
import com.swm.sprint1.repository.user.UserRepository;
import com.swm.sprint1.security.Token;
import com.swm.sprint1.security.TokenProvider;
import com.swm.sprint1.service.AuthService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
public class AuthController {

    private final AuthService authService;

    private final TokenProvider tokenProvider;

    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @ApiOperation(value = "액세스 토큰 재발급", notes = "새로 갱신된 액세스 토큰을 발급합니다.")
    @PostMapping("/api/v1/auth/access-token")
    public ResponseEntity<?> refreshAccessToken(@Valid @RequestBody JwtDto jwtDto , BindingResult result){
        logger.debug("refreshAccessToken 호출되었습니다.");

        if(result.hasErrors()) {
            logger.error("JwtDto 바인딩 에러가 발생했습니다.");
            throw new RequestParamException("JwtDto 바인딩 에러가 발생했습니다.", "102");
        }

        Token accessToken = authService.refreshAccessToken(jwtDto);

        ApiResponse response = new ApiResponse(true, "액세스 토큰 갱신 완료");
        response.putData("accessToken", accessToken);
        return ResponseEntity.ok(response);
    }

    @ApiOperation(value = "액세스 토큰 & 리프레시 토큰 재발급", notes = "새로 갱신된 액세스 토큰과 리프레시 토큰을 발급합니다.")
    @PostMapping("/api/v1/auth/refresh-token")
    public ResponseEntity<?> refreshAccessAndRefreshTokens(@Valid @RequestBody JwtDto jwtDto , BindingResult result){
        logger.debug("refreshAccessAndRefreshTokens 호출되었습니다.");

        if(result.hasErrors()) {
            logger.error("JwtDto 바인딩 에러가 발생했습니다.");
            throw new RequestParamException("JwtDto 바인딩 에러가 발생했습니다.", "102");
        }

        AuthResponse authResponse = authService.refreshAccessAndRefreshToken(jwtDto);

        ApiResponse response = new ApiResponse(true, "액세스 & 리프레시 토큰 갱신 완료");
        response.putData("tokens", authResponse);
        return ResponseEntity.ok(response);
    }

    @ApiOperation(value = "액세스 토큰 유효성 검사", notes = "토큰의 유효성을 검사하고 결과를 반환합니다.")
    @PostMapping("/api/v1/auth/validation/access")
    public ResponseEntity<?> validateAccessJwtToken(@Valid @RequestBody JwtDto jwtDto, BindingResult result){

        logger.debug("validateAccessJwtToken 호출되었습니다.");

        if(result.hasErrors()) {
            logger.error("JwtDto 바인딩 에러가 발생했습니다.");
            throw new RequestParamException("JwtDto 바인딩 에러가 발생했습니다.", "102");
        }

        tokenProvider.validateAccessToken(jwtDto.getJwt());
        return ResponseEntity.ok(new ApiResponse(true, "유효한 액세스 토큰 입니다."));
    }

    @ApiOperation(value = "리프레시 토큰 유효성 검사", notes = "리프레시 토큰의 유효성을 검사하고 결과를 반환합니다.")
    @PostMapping("/api/v1/auth/validation/refresh")
    public ResponseEntity<?> validateRefreshJwtToken(@Valid @RequestBody JwtDto jwtDto, BindingResult result){
        logger.debug("validateRefreshJwtToken 호출되었습니다.");

        if(result.hasErrors()) {
            logger.error("JwtDto 바인딩 에러가 발생했습니다.");
            throw new RequestParamException("JwtDto 바인딩 에러가 발생했습니다.", "102");
        }

        tokenProvider.validateRefreshToken(jwtDto.getJwt());
        return ResponseEntity.ok(new ApiResponse(true, "유효한 리프레시 토큰 입니다."));
    }

    @ApiOperation(value = "유저 로그인", notes = "로그인 하고 응답으로 액세스 토큰과 리프레시 토큰을 발급합니다.")
    @PostMapping("/api/v1/auth/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        logger.debug("authenticateUser 호출되었습니다.");
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        Token accessToken = tokenProvider.createAccessToken(authentication);
        Token refreshToken = tokenProvider.createRefreshToken(authentication);
        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
    }

    @ApiOperation(value = "유저 생성", notes = "유저를 생성합니다. 응답으로 액세스 토큰과 리프레시 토큰을 발급합니다..")
    @PostMapping("/api/v1/auth/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        logger.debug("registerUser 호출되었습니다.");
        if(userRepository.existsByEmail(signUpRequest.getEmail())) {
            logger.error("Email address already in use.");
            throw new BadRequestException("Email address already in use.");
        }

        User user = User.builder()
                .name(signUpRequest.getName())
                .email(signUpRequest.getEmail())
                .emailVerified(false)
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .provider(AuthProvider.test)
                .providerId("test")
                .build();

        userRepository.save(user);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        signUpRequest.getEmail(),
                        signUpRequest.getPassword()
                )
        );

        Token accessToken = tokenProvider.createAccessToken(authentication);
        Token refreshToken = tokenProvider.createRefreshToken(authentication);
        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
    }

}
