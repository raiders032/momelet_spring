package com.swm.sprint1.payload.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class GetRestaurantRequest {

    private Long id;

    @NotNull
    private BigDecimal longitude;

    @NotNull
    private BigDecimal latitude;

}
