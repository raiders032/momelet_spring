package com.swm.sprint1.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = "naver_id")})
public class Restaurant {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "restaurant_id")
    private Long id;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    private List<Menu> menuList = new ArrayList<>();

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    private List<RestaurantPhoto> photos = new ArrayList<>();

    @OneToMany(mappedBy = "restaurant")
    private List<RestaurantCategory> restaurantCategories = new ArrayList<>();

    @Column(nullable = false)
    private String name;

    @Column(precision = 3, scale = 2)
    private BigDecimal googleRating;

    @Column(precision = 2, scale = 1)
    private BigDecimal naverRating;

    @Column(columnDefinition = "int(5) default 0")
    private int naverReviewCount;

    @Column(columnDefinition = "int(5) default 0")
    private int googleReviewCount;

    private String openingHours;

    @Column(columnDefinition = "int(1) default 0")
    private int priceLevel;

    private String address;

    private String roadAddress;

    @Column(precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(name = "naver_id")
    private Long naverId;

    private Long googleId;

    private String phoneNumber;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdDate;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime modifiedDate;

    private String thumUrl;
}