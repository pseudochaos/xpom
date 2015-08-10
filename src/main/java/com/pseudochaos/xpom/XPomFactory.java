package com.pseudochaos.xpom;

import java.util.HashMap;
import java.util.Map;

public class XPomFactory {

    private static Map<Class, XPom> mappers = new HashMap<>();

    public static <T> XPom<T> create(Class<T> clazz) {
        if (!mappers.containsKey(clazz)) {
            mappers.put(clazz, new XPom(clazz));
        }
        return mappers.get(clazz);
    }
}
