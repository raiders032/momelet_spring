package com.swm.sprint1.service;

import com.swm.sprint1.domain.UserRefreshToken;
import com.swm.sprint1.payload.request.JwtDto;
import com.swm.sprint1.payload.response.AuthResponse;
import com.swm.sprint1.repository.user.UserRefreshTokenRepository;
import com.swm.sprint1.repository.user.UserRepository;
import com.swm.sprint1.security.CustomUserDetailsService;
import com.swm.sprint1.security.Token;
import com.swm.sprint1.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AuthService {

    private final TokenProvider tokenProvider;

    private final CustomUserDetailsService customUserDetailsService;
    private final UserRepository userRepository;
    private final UserRefreshTokenRepository userRefreshTokenRepository;

    public Token refreshAccessToken(JwtDto jwtDto) {
        String refreshToken = jwtDto.getJwt();
        final Long userId = tokenProvider.validateRefreshToken(refreshToken);

        UserDetails userDetails = customUserDetailsService.loadUserById(userId);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        return tokenProvider.createAccessToken(authentication);
    }

    @Transactional
    public AuthResponse refreshAccessAndRefreshToken(JwtDto jwtDto) {
        String token = jwtDto.getJwt();
        final Long userId = tokenProvider.validateRefreshToken(token);

        UserDetails userDetails = customUserDetailsService.loadUserById(userId);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        Token accessToken = tokenProvider.createAccessToken(authentication);
        Token refreshToken = tokenProvider.createRefreshToken(authentication);

        Optional<UserRefreshToken> byUserId = userRefreshTokenRepository.findByUserId(userId);

        if(byUserId.isPresent())
            byUserId.get().updateRefreshToken(refreshToken.getJwtToken());
        else
            userRefreshTokenRepository.save(new UserRefreshToken(userId, refreshToken.getJwtToken()));

        return new AuthResponse(accessToken, refreshToken);
    }

    @Transactional
    public AuthResponse createAccessAndRefreshToken(Long userId) {

        UserDetails userDetails = customUserDetailsService.loadUserById(userId);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        Token accessToken = tokenProvider.createAccessToken(authentication);
        Token refreshToken = tokenProvider.createRefreshToken(authentication);

        Optional<UserRefreshToken> byUserId = userRefreshTokenRepository.findByUserId(userId);

        if(byUserId.isPresent())
            byUserId.get().updateRefreshToken(refreshToken.getJwtToken());
        else
            userRefreshTokenRepository.save(new UserRefreshToken(userId, refreshToken.getJwtToken()));

        return new AuthResponse(accessToken, refreshToken);
    }

    @Transactional
    public AuthResponse createAccessAndRefreshToken(Long userId, long accessExpiryDate, long refreshExpiryDate) {

        UserDetails userDetails = customUserDetailsService.loadUserById(userId);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        Token accessToken = tokenProvider.createAccessToken(authentication, accessExpiryDate);
        Token refreshToken = tokenProvider.createRefreshToken(authentication, refreshExpiryDate);

        Optional<UserRefreshToken> byUserId = userRefreshTokenRepository.findByUserId(userId);

        if(byUserId.isPresent())
            byUserId.get().updateRefreshToken(refreshToken.getJwtToken());
        else
            userRefreshTokenRepository.save(new UserRefreshToken(userId, refreshToken.getJwtToken()));

        return new AuthResponse(accessToken, refreshToken);
    }


}
