package com.pseudochaos.xpom.annotation;

import javax.xml.XMLConstants;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Namespace {
    String prefix() default XMLConstants.DEFAULT_NS_PREFIX;
    String uri();
}
