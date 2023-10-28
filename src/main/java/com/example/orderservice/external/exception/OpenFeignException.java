package com.example.orderservice.external.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;


public class OpenFeignException extends ResponseStatusException {

    private final SearchErrorResponse searchErrorResponse;

    public OpenFeignException(HttpStatusCode status, SearchErrorResponse searchErrorResponse) {
        super(status);
        this.searchErrorResponse = searchErrorResponse;
    }

    public OpenFeignException(HttpStatusCode status, String reason, SearchErrorResponse searchErrorResponse) {
        super(status, reason);
        this.searchErrorResponse = searchErrorResponse;
    }
}
