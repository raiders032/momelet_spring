package com.swm.sprint1.repository.user;

import com.swm.sprint1.domain.UserRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRefreshTokenRepository extends JpaRepository<UserRefreshToken, Long> {
    Optional<UserRefreshToken> findByUserId(Long userId);

    boolean existsByUserIdAndRefreshToken(Long userId, String refreshToken);
}
