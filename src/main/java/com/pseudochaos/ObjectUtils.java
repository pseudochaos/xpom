package com.pseudochaos;

import com.pseudochaos.xpom.XPomException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

import static java.lang.String.format;

public final class ObjectUtils {

    private ObjectUtils() {}

    public static <T> T firstNonNull(T... nullableItems) {
        return Stream.of(nullableItems).filter(Objects::nonNull).findFirst().get();
    }

    public static <T> T newInstanceOf(Class<T> clazz) {
        try {
            Constructor<?> zeroArgumentConstructor = Arrays.stream(clazz.getDeclaredConstructors())
                    .filter(c -> c.getParameterCount() == 0)
                    .findFirst().orElseThrow(() -> new XPomException("No zero-argument constructor found for class: " + clazz));
            zeroArgumentConstructor.setAccessible(true);
            return (T) zeroArgumentConstructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new XPomException(format("Failed to instantiate the class %s using zero-argument constructor", clazz), e);
        }
    }


}
