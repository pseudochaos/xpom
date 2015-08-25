package com.pseudochaos.xpom;

public interface ExceptionHandlingStrategy {
    void handleValueNotPresent(XField field, Object instance) throws NoValueException;
    void handleConversionException(Throwable e, XField field) throws ConversionException;
}
