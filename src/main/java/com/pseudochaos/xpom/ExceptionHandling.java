package com.pseudochaos.xpom;

public enum ExceptionHandling {
    /**
     * Throw an exception if any exception occurs during converting the raw value to the
     * target type or the raw value isn't present for a mandatory field. This strategy ignores any default
     * values set to fields.
     */
    FAIL,

    /**
     * Use default values explicitly set to fields in case of any conversion exceptions or absence of the value in xml
     */
    USE_DEFAULT,

}
