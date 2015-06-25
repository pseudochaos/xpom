package com.pseudochaos.xpom.annotation;

import com.pseudochaos.xpom.Converter;
import com.pseudochaos.xpom.ExceptionHandlingPolicy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface XPath {

    String value();

    Class<? extends Converter> converter() default Converter.class;

    ExceptionHandlingPolicy exceptionHandlingPolicy() default ExceptionHandlingPolicy.THROW;
}
