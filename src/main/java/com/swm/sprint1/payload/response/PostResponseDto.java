package com.swm.sprint1.payload.response;

import com.swm.sprint1.domain.Restaurant;
import com.swm.sprint1.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostResponseDto {

    private Long id;

    private Long restaurantId;

    private String imageUrl;

    private String claim;
}
