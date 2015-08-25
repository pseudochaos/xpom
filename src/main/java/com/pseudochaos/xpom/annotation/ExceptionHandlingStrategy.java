package com.pseudochaos.xpom.annotation;

import com.pseudochaos.xpom.ExceptionHandling;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface ExceptionHandlingStrategy {
    ExceptionHandling value();
}
