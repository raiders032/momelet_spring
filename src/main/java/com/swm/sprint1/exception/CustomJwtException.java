package com.swm.sprint1.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class CustomJwtException extends RuntimeException {

    private String errorCode;

    public CustomJwtException(String message, String errorCode) {
        super(message);
        this.errorCode=errorCode;
    }
}
