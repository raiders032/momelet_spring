package com.swm.sprint1.apple;

import com.nimbusds.jose.JOSEException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.Map;

public interface AppleService {

    String getAppleClientSecret(String id_token) throws JOSEException, InvalidKeyException, IOException, URISyntaxException;

    TokenResponse requestCodeValidations(String client_secret, String code, String refresh_token);

    Map<String, String> getLoginMetaInfo();

    String getPayload(String id_token);

}
