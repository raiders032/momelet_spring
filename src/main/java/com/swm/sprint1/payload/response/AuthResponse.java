package com.swm.sprint1.payload.response;

import com.swm.sprint1.security.Token;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class AuthResponse {
    private Token accessToken;
    private Token refreshToken;
    private String tokenType = "Bearer";

    public AuthResponse(Token accessToken, Token refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
