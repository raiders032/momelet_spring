package com.swm.sprint1.payload.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@NoArgsConstructor
public class UserInfoDto {
    private Long id;
    private String email;
    private String name;
    private String imageUrl;
    private Map<String, Integer> categories;

    public UserInfoDto(Long id, String name, String email, String imageUrl, Map<String, Integer> categories) {
        this.id=id;
        this.name=name;
        this.email=email;
        this.imageUrl=imageUrl;
        this.categories=categories;
    }
}
