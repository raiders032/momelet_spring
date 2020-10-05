package com.swm.sprint1.exception;

import com.swm.sprint1.payload.response.ApiResponse;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import javax.validation.ConstraintViolationException;

@RestController
@ControllerAdvice
public class CustomizedResponseEntityExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(CustomizedResponseEntityExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ApiResponse> handleAllExceptions(Exception ex, WebRequest request) {
        logger.error(ex.getMessage());
        ApiResponse response = new ApiResponse(false,"500",ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({ConstraintViolationException.class, MethodArgumentNotValidException.class, NotSupportedExtension.class, MissingServletRequestParameterException.class})
    public final ResponseEntity<ApiResponse> handleConstraintViolationExceptions(Exception ex, WebRequest request){
        logger.error(ex.getMessage());
        ApiResponse response = new ApiResponse(false, "102", ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RequestParamException.class)
    public final ResponseEntity<ApiResponse> handleRequestParamExceptions(Exception ex, WebRequest request){
        logger.error(ex.getMessage());
        RequestParamException exception = (RequestParamException)ex;
        ApiResponse response = new ApiResponse(false, exception.getErrorCode(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public final ResponseEntity<ApiResponse> handleResourceNotFoundExceptions(Exception ex, WebRequest request) {
        logger.error(ex.getMessage());
        ResourceNotFoundException exception = (ResourceNotFoundException) ex;
        ApiResponse response = new ApiResponse(false, exception.getErrorCode(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RestaurantLessThan7Exception.class)
    public final ResponseEntity<ApiResponse> handleRestaurantLessThan7Exceptions(Exception ex, WebRequest request) {
        logger.error(ex.getMessage());
        ApiResponse response = new ApiResponse(false,"211", ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(JwtException.class)
    public final ResponseEntity<ApiResponse> handleJwtExceptions(Exception ex, WebRequest request) {
        ApiResponse response;
        if(ex.getClass().equals(ExpiredJwtException.class)){
            logger.error("Expired JWT token");
            response = new ApiResponse(false,"400", "Expired JWT token",
                    request.getDescription(false));
        }
        else{
            logger.error(ex.getMessage());
            response = new ApiResponse(false,"401", "Invalid JWT token",
                    request.getDescription(false));
        }

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(CustomJwtException.class)
    public final  ResponseEntity<ApiResponse> handleCustomJwtExceptions(Exception ex, WebRequest request) {
        logger.error(ex.getMessage());
        CustomJwtException exception = (CustomJwtException)ex;
        ApiResponse response = new ApiResponse(false, exception.getErrorCode(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(OAuth2AuthenticationProcessingException.class)
    public final ResponseEntity<ApiResponse> handleOAuth2AuthenticationProcessingExceptions(Exception ex, WebRequest request) {
        logger.error(ex.getMessage());
        ApiResponse response = new ApiResponse(false,ex.getMessage(),"403", request.getDescription(false));
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
