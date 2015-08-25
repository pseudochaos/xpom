package com.pseudochaos.xpom;

import static com.pseudochaos.ObjectUtils.firstNonNull;
import static com.pseudochaos.ObjectUtils.newInstanceOf;

public class Configuration<T> {

    private final Class<T> clazz;
    private ConverterResolver converterResolver;

    public Configuration(Class<T> clazz) {
        this.clazz = clazz;
        this.converterResolver = new HierarchicalConverterResolver();
    }

    public Converter<Object, ?> resolveConverter(XField field) {
        if (field.getConverter().isPresent()) {
            return newInstanceOf(field.getConverter().get());
        }
        return (Converter<Object, ?>) converterResolver.resolve(field.getJavaField());
    }

    public ExceptionHandlingStrategy getExceptionHandlingStrategy(XField field) {
        return firstNonNull(
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
