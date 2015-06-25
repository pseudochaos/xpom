package com.pseudochaos.xpom;

@FunctionalInterface
public interface Converter<S, T> {
    T convert(S source);
}
