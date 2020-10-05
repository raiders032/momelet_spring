package com.swm.sprint1.payload.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

@Builder
@AllArgsConstructor
@Getter
@NoArgsConstructor
public class UserLikingReqeust {

    @NotNull @Min(-180) @Max(180)
    private BigDecimal userLongitude;

    @NotNull @Min(-90) @Max(90)
    private BigDecimal userLatitude;

    @Valid
    @NotEmpty @Size( min=7, max =7)
    private List<UserLikingDto> userLiking;

}