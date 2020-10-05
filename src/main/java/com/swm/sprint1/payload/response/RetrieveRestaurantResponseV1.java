package com.swm.sprint1.payload.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class RetrieveRestaurantResponseV1 {
    private Long id;
    private String name;
    private String thumUrl;
    private List<String> menuList = new ArrayList<>();
    private String categories;
    private BigDecimal googleRating;
    private int googleReviewCount;
    private String openingHours;
    private int priceLevel;
    private String address;
    private String roadAddress;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private Long naverId;
    private Long googleId;
    private String phoneNumber;

   /* public RetrieveRestaurantResponse(Long id, String name, String thumUrl, String categories, BigDecimal googleRating, int googleReviewCount, String openingHours, int priceLevel, String address, String roadAddress, BigDecimal longitude, BigDecimal latitude, Long naverId, Long googleId, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.thumUrl = thumUrl;
        this.categories = categories;
        this.googleRating = googleRating;
        this.googleReviewCount = googleReviewCount;
        this.openingHours = openingHours;
        this.priceLevel = priceLevel;
        this.address = address;
        this.roadAddress = roadAddress;
        this.longitude = longitude;
        this.latitude = latitude;
        this.naverId = naverId;
        this.googleId = googleId;
        this.phoneNumber = phoneNumber;
    }*/
}
