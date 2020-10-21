package com.swm.sprint1.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class RestaurantPhotoResponseDto {
    private Long id;

    private Long restaurantId;

    private Long userId;

    private String filename;

    private String path;
}
