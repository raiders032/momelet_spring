package com.swm.sprint1.apple;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class Payload {

    private String iss;
    private String aud;
    private Long exp;
    private Long iat;
    private String sub;
    private String nonce;
    private String c_hash;
    private String at_hash;
    private String email;
    private String email_verified;
    private String is_private_email;
    private Long auth_time;
    private boolean nonce_supported;

}
