package com.example.orderservice.exception;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * {@code @EqualsAndHashCode(callSuper = true)} 는 equals, hascode 메서드를 자동으로 생성해준다.
 * callUSuper 속성을 true 도 두면 현재 부모인 RuntimeException 필드 값도 포함해서 검사한다.
 * false 로 설정하면 자신의 필드만을 검사한다.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class CustomException extends RuntimeException{

    private final String errorCode;
    private final Integer status;

    public CustomException(String message, String errorCode, Integer status) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
    }

}
