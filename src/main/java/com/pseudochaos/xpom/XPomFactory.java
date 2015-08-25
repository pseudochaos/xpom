package com.pseudochaos.xpom;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class XPomFactory {

    private static Map<Class, XPom> mappers = new HashMap<>();
    private static ExceptionHandling strategy = ExceptionHandling.FAIL;

    public static <T> XPom<T> create(Class<T> clazz) {
        if (!mappers.containsKey(clazz)) {
            mappers.put(clazz, new XPom(clazz));
        }
        return mappers.get(clazz);
    }

    public static void setExceptionHandlingStrategy(ExceptionHandling strategy) {
        XPomFactory.strategy = Objects.requireNonNull(strategy, "Default JVM level exception strategy can't be null");
    }

    public static ExceptionHandling getExceptionHandlingStrategy() {
        return strategy;
    }
}
