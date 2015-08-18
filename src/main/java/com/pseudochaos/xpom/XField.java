package com.pseudochaos.xpom;

import com.pseudochaos.xpom.annotation.XPath;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;

public class XField {

    private final Field field;

    public XField(Field field) {
        this.field = field;
    }

    public Field getJavaField() {
        return field;
    }

    public String getXPath() {
        return field.getDeclaredAnnotation(XPath.class).value();
    }

    public boolean isMandatory() {
        return field.getDeclaredAnnotation(XPath.class).mandatory();
    }

    public boolean isCollection() {
        return field.getType().isArray() || Collection.class.isAssignableFrom(field.getType());
    }

    public String getTypeString() {
        if (field.getGenericType() instanceof ParameterizedType) {
            Class<?> elementType = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
            return formatStringFor(field.getType()) + "<" + formatStringFor(elementType) + ">";
        } else {
            return formatStringFor(field.getType());
        }
    }

    private String formatStringFor(Class<?> type) {
        String result = type.isArray() ? type.getComponentType() + "[]" : type.toString();
        return result.replaceFirst("^(class|interface) ", "").replaceFirst("java\\.lang\\.", "");
    }

    @Override
    public String toString() {
        return String.format("@XPath(\"%s\") %s %s", getXPath(), getTypeString(), field.getName());
    }

    public Object getDefaultValue() {
        return null;
    }

    public boolean hasExplicitDefaultValue() {
        return false;
    }
}
