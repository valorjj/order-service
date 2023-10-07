package com.example.orderservice.external.decoder;

import com.example.orderservice.exception.CustomException;
import com.example.orderservice.external.model.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.io.IOException;

@Slf4j
public class CustomErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        ObjectMapper om = new ObjectMapper();

        try {
            ErrorResponse errorResponse = om.readValue(response.body().asInputStream(), ErrorResponse.class);
            return new CustomException(errorResponse.errorMessage(), errorResponse.errorCode(), response.status());
        } catch (IOException e) {
            throw new CustomException("Internal Server Error", "INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
}
