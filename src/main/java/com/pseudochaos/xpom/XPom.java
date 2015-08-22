package com.pseudochaos.xpom;

import com.pseudochaos.xpom.jaxp.JaxpValueExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.NamespaceContext;
import java.lang.reflect.Field;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toSet;

public final class XPom<T> {

    private static final Logger logger = LoggerFactory.getLogger(XPom.class);

    private final Class<T> clazz;
    private final ValueExtractor extractor;
    private final Configuration configuration;
    private final Set<XField> fields;
    private final NamespaceContext namespaceContext;

    XPom(Class<T> clazz) {
        this.clazz = clazz;
        this.extractor = new JaxpValueExtractor();
        this.configuration = new Configuration();

        this.namespaceContext = new XNamespaceContext(clazz);
        this.fields = stream(clazz.getDeclaredFields())
                .filter(annotatedFields())
                .map(field -> new XField(field, namespaceContext))
                .collect(toSet());
    }

    private Predicate<Field> annotatedFields() {
        return field -> field.isAnnotationPresent(com.pseudochaos.xpom.annotation.XPath.class);
    }

    public T using(String xml) {
        T instance = newInstanceOf(clazz);
        fields.stream().forEach(populateValue(instance, xml));
        return instance;
    }

    private T newInstanceOf(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    private Consumer<XField> populateValue(T instance, String xml) {
        return field -> {
            Object rawValue = extractValueFrom(xml, field);
            Object value = convert(rawValue, field);
            set(value, field, instance);
        };
    }

    private Object extractValueFrom(String xml, XField field) {
        Object result;
        if (field.isCollection()) {
            result = extractor.extractCollection(xml, field.getXPath());
        } else {
            result = extractor.extractScalar(xml, field.getXPath());
        }
        logger.debug("{} = {}", field, result);
        return result;
    }

    private Object convert(Object rawValue, XField field) {
        if (rawValue == null) {
            return configuration.getExceptionHandlingStrategy(field.getJavaField()).handleValueNotPresent(field);
        } else {
            try {
                return configuration.resolveConverter(field.getJavaField()).convert(rawValue);
            } catch (Exception e) {
                return configuration.getExceptionHandlingStrategy(field.getJavaField()).handleConversionException(e, field);
            }
        }
    }

    private void set(Object value, XField xField, T instance) {
        Field field = xField.getJavaField();
        field.setAccessible(true);
        try {
            field.set(instance, value);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    public NamespaceContext getNamespaceContext() {
        return namespaceContext;
    }
}
