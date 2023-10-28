package com.example.orderservice.external.exception;

import lombok.Data;

import java.util.List;

@Data
public class SearchErrorResponse {
    private Integer code;
    private String message;
    private List<FieldError> fieldErrorList;
    private String timestamps;

    @Data
    public static class FieldError {
        private String field;
        private String value;
        private String message;
    }


}
