package com.pseudochaos.xpom;

import java.lang.reflect.Field;

public class Configuration {

    private ConverterResolver converterResolver;

    Configuration() {
        converterResolver = new HierarchicalConverterResolver();
    }

    Converter<Object, ?> resolveConverter(Field field) {
        return (Converter<Object, ?>) converterResolver.resolve(field);
    }

}
