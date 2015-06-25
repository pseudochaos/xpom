package com.pseudochaos.xpom;

import java.lang.reflect.Field;

interface ConverterResolver {
    Converter<?, ?> resolve(Class<?> type);
    Converter<?, ?> resolve(Field field);
}
