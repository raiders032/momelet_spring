package com.swm.sprint1.domain;

import com.swm.sprint1.domain.base.DateEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class UserRefreshToken extends DateEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_refresh_token_id")
    private Long id;

    private Long userId;

    private String refreshToken;

    public UserRefreshToken(Long userId, String refreshToken) {
        this.userId=userId;
        this.refreshToken = refreshToken;
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
