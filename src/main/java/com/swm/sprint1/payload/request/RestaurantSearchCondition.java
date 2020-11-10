package com.swm.sprint1.payload.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class RestaurantSearchCondition {
    private String name;

    @NotNull
    private BigDecimal longitude;

    @NotNull
    private BigDecimal latitude;

    private String filter = "distance";

    private BigDecimal radius = BigDecimal.valueOf(0.01);
}
