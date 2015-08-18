package com.pseudochaos.xpom;

public interface ExceptionHandlingStrategy {
    Object handleValueNotPresent(boolean mandatory, Object defaultValue);
    Object handleConversionException(Throwable e, boolean mandatory, Object defaultValue);
}
