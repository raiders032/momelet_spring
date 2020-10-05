package com.swm.sprint1.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RestaurantLessThan7Exception extends RuntimeException {
    public RestaurantLessThan7Exception(String message) {
        super(message);
    }
}
