package com.pseudochaos.xpom;

import com.google.common.base.Defaults;
import com.pseudochaos.xpom.annotation.ExceptionHandlingStrategy;
import com.pseudochaos.xpom.annotation.XPath;

import javax.xml.namespace.NamespaceContext;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Objects;

public class XField {

    private final Field field;
    private final com.pseudochaos.xpom.XPath xPath;

    public XField(Field field, NamespaceContext namespaceContext) {
        this.field = field;
        this.xPath = new com.pseudochaos.xpom.XPath(getRawXPath(), namespaceContext);
    }

    public Field getJavaField() {
        return field;
    }

    public String getRawXPath() {
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
        return String.format("@XPath(\"%s\") %s %s", getRawXPath(), getTypeString(), field.getName());
    }

    public com.pseudochaos.xpom.XPath getXPath() {
        return xPath;
    }

    public ExceptionHandling getExceptionHandlingStrategy() {
        return field.isAnnotationPresent(ExceptionHandlingStrategy.class) ?
            field.getDeclaredAnnotation(ExceptionHandlingStrategy.class).value() : null;
    }

    public boolean hasDefaultValue(Object instance) {
        Object javaDefault = Defaults.defaultValue(field.getType());
        field.setAccessible(true);
        try {
            Object a = field.get(instance);
            return !Objects.equals(a, javaDefault);
        } catch (IllegalAccessException e) {
            throw new XPomException("Failed to get default value assigned to the field: " + this, e);
        }
    }
}
