package com.swm.sprint1.payload.request;

import com.swm.sprint1.domain.Liking;
import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class UserLikingDto {

    @NotNull
    private @Min(1) Long restaurantId;

    @NotNull
    private Liking liking;

    @NotNull
    private Integer elapsedTime;
}
