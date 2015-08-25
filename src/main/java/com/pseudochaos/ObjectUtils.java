package com.pseudochaos;

import java.util.Objects;
import java.util.stream.Stream;

public final class ObjectUtils {

    private ObjectUtils() {}

    public static <T> T coalesce(T... nullableItems) {
        return Stream.of(nullableItems).filter(Objects::nonNull).findFirst().get();
    }

}
