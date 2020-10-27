package com.swm.sprint1.payload.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
public class SignUpRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String password;

}
