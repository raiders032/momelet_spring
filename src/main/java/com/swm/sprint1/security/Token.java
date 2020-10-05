package com.swm.sprint1.security;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Getter
@Setter
@NoArgsConstructor
public class Token {
    String jwtToken;
    Date expiryDate;
    String formattedExpiryDate;

    public Token(String jwtToken, Date expiryDate) {
        SimpleDateFormat formatter = new SimpleDateFormat ( "yyyy/MM/dd HH:mm:ss", Locale.KOREA );
        this.jwtToken = jwtToken;
        this.expiryDate = expiryDate;
        formattedExpiryDate = formatter.format(expiryDate);
    }
}
