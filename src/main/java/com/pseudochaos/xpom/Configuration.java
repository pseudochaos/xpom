package com.pseudochaos.xpom;

import java.lang.reflect.Field;

import static com.pseudochaos.ObjectUtils.coalesce;

public class Configuration<T> {

    private final Class<T> clazz;
    private ConverterResolver converterResolver;

    public Configuration(Class<T> clazz) {
        this.clazz = clazz;
        this.converterResolver = new HierarchicalConverterResolver();
    }

    Converter<Object, ?> resolveConverter(Field field) {
        return (Converter<Object, ?>) converterResolver.resolve(field);
    }

    public ExceptionHandlingStrategy getExceptionHandlingStrategy(XField field) {
        return coalesce(
                field.getExceptionHandlingStrategy(),
                getClassExceptionHandlingStrategy(),
                XPomFactory.getExceptionHandlingStrategy()
        );
    }

    ExceptionHandlingStrategy getClassExceptionHandlingStrategy() {
        return clazz.isAnnotationPresent(com.pseudochaos.xpom.annotation.ExceptionHandlingStrategy.class) ?
                clazz.getDeclaredAnnotation(com.pseudochaos.xpom.annotation.ExceptionHandlingStrategy.class).value() : null;
    }
}
