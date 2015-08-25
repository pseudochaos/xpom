package com.pseudochaos.xpom;

import static java.lang.String.format;

public enum ExceptionHandling implements ExceptionHandlingStrategy{
    /**
     * Throw an exception if any exception occurs during converting the raw value to the
     * target type or the raw value isn't present for a mandatory field. This strategy ignores any default
     * values set to fields.
     */
    FAIL {
        @Override
        public void handleValueNotPresent(XField field, Object instance) throws NoValueException {
            if (field.isMandatory()) {
                throw new NoValueException(format("Given xml doesn't contain a value for the %smandatory field: %s",
                    field.isMandatory() ? "" : "non-", field));
            }
        }

        @Override
        public void handleConversionException(Throwable e, XField field) throws ConversionException {
            throw new ConversionException(format("Failed to convert a value for the %smandatory field: %s",
                    field.isMandatory() ? "" : "non-", field), e);
        }
    },

    /**
     * Use default values explicitly set to fields in case of any conversion exceptions or absence of the value in xml
     */
    USE_DEFAULT {
        @Override
        public void handleValueNotPresent(XField field, Object instance) throws NoValueException {
            if (field.isMandatory() && !field.hasDefaultValue(instance)) {
                throw new NoValueException(format("Mandatory field %s doesn't have either xml value or default value", field));
            } else {
                // do nothing in order not to override a default value set to the field
            }
        }

        @Override
        public void handleConversionException(Throwable e, XField field) throws ConversionException {
            // do nothing in order not to override a default value set to the field
        }
    },

}
