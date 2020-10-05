package com.swm.sprint1.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private final Auth auth = new Auth();
    private final OAuth2 oauth2 = new OAuth2();
    private final S3 s3 = new S3();

    @Setter
    @Getter
    public static class Auth {
        private String tokenSecret;

        private long refreshTokenExpirationMsec;

        private long accessTokenExpirationMsec;
    }

    public static final class OAuth2 {
        private List<String> authorizedRedirectUris = new ArrayList<>();

        public List<String> getAuthorizedRedirectUris() {
            return authorizedRedirectUris;
        }

        public OAuth2 authorizedRedirectUris(List<String> authorizedRedirectUris) {
            this.authorizedRedirectUris = authorizedRedirectUris;
            return this;
        }
    }

    @Setter
    @Getter
    public static final class S3 {
        private String defaultImageUri;

        private Integer defaultNumber;

        private String defaultExtension;
    }
}
