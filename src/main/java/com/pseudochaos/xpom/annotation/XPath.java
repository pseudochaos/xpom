package com.pseudochaos.xpom.annotation;

import com.pseudochaos.xpom.Converter;
import com.pseudochaos.xpom.ExceptionHandling;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface XPath {

    String value();

    ExceptionHandling exceptionHandling() default ExceptionHandling.FAIL;

    boolean mandatory() default false;

    Class<? extends Converter> converter() default Converter.class;
}
