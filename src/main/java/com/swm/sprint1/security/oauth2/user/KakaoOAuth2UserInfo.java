package com.swm.sprint1.security.oauth2.user;

import java.util.Map;

public class KakaoOAuth2UserInfo extends OAuth2UserInfo {

    public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return attributes.get("id").toString();
    }

    @Override
    public String getName() {
        return ((Map<String, Object>) attributes.get("properties")).get("nickname").toString();
    }

    @Override
    public String getEmail() {
        if(attributes.containsKey("kakao_account")) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
                if(kakaoAccount.containsKey("email")) {
                    return (String) kakaoAccount.get("email");
                }
        }
        return null;
    }

    @Override
    public String getImageUrl() {
        if(attributes.containsKey("kakao_account")) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            if(kakaoAccount.containsKey("profile")) {
                Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
                if(profile.containsKey("profile_image_url")) {
                    return (String) profile.get("profile_image_url");
                }
            }
        }
        return null;
    }
}
