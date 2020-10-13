package com.swm.sprint1.payload.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RestaurantSearchCondition {
    private String name;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private BigDecimal radius = BigDecimal.valueOf(0.01);
}
