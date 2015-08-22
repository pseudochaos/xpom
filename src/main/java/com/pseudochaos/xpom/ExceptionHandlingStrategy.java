package com.pseudochaos.xpom;

public interface ExceptionHandlingStrategy {
    Object handleValueNotPresent(XField field);
    Object handleConversionException(Throwable e, XField field);
}
