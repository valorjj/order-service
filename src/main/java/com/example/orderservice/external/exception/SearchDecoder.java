package com.example.orderservice.external.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import feign.codec.StringDecoder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;

@Slf4j
public class SearchDecoder implements ErrorDecoder {

    private final StringDecoder stringDecoder = new StringDecoder();
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    @SneakyThrows
    public Exception decode(String methodKey, Response response) {
        String message = stringDecoder.decode(response, String.class).toString();
        SearchErrorResponse searchErrorResponse = objectMapper.readValue(message, SearchErrorResponse.class);
        return new OpenFeignException(HttpStatusCode.valueOf(searchErrorResponse.getCode()), searchErrorResponse);
    }
}
