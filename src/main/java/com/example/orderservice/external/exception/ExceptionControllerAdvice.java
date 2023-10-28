package com.example.orderservice.external.exception;

import com.example.orderservice.external.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionControllerAdvice {
    @ExceptionHandler(OpenFeignException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleFeignException(OpenFeignException e) {
        ErrorResponse errorResponse = ErrorResponse.builder().errorMessage(e.getMessage()).errorCode(e.getStatusCode().value()).build();
        return ResponseEntity.status(e.getStatusCode()).body(errorResponse);
    }


}
