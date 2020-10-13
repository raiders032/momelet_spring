package com.swm.sprint1.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigInteger;

@NoArgsConstructor
@Setter
@Getter
@AllArgsConstructor
public class BookmarkResponseDto {

    private Long id;
    private Long restaurantId;
    private String name;
    private String thumUrl;


}
