package com.example.orderservice;

import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Test
@Transactional
public @interface TestAnnotation {
}
