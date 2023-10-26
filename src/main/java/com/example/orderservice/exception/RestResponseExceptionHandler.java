package com.example.orderservice.exception;


import com.example.orderservice.external.model.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        return ResponseEntity.status(e.getStatus())
            .body(ErrorResponse.builder()
                .errorMessage(e.getMessage())
                .build()
            );
    }

}
