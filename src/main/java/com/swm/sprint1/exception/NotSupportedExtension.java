package com.swm.sprint1.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NotSupportedExtension extends RuntimeException {
    public NotSupportedExtension(String message) {
        super(message);
    }

    public NotSupportedExtension(String message, Throwable cause) {
        super(message, cause);
    }
}
