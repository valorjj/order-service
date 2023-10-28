package com.example.orderservice.exception;


import lombok.*;

/**
 * {@code @EqualsAndHashCode(callSuper = true)} 는 equals, hascode 메서드를 자동으로 생성해준다.
 * callUSuper 속성을 true 도 두면 현재 부모인 RuntimeException 필드 값도 포함해서 검사한다.
 * false 로 설정하면 자신의 필드만을 검사한다.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class CustomException extends RuntimeException {

    private final Integer status;
    private final String errorMessage;

    public CustomException(String message, Integer status, String errorMessage) {
        super(message);
        this.status = status;
        this.errorMessage = errorMessage;
    }


}
