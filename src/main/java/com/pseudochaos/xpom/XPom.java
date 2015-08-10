package com.pseudochaos.xpom;

import org.slf4j.LoggerFactory;

import com.pseudochaos.xpom.annotation.Namespace;
import com.pseudochaos.xpom.annotation.NamespaceContext;
import com.pseudochaos.xpom.jaxp.JaxpValueExtractor;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public final class XPom<T> {

    private static final Logger logger = LoggerFactory.getLogger(XPom.class);

    private final Class<T> clazz;
    private final ValueExtractor extractor;
    private final Configuration configuration;

    XPom(Class<T> clazz) {
        this.clazz = clazz;
        this.extractor = new JaxpValueExtractor();
        this.configuration = new Configuration();
    }

    public T using(String xml) {
        T instance = newInstanceOf(clazz);
        stream(clazz.getDeclaredFields())
                .filter(annotatedFields())
                .forEach(populateValue(instance, xml));
        return instance;
    }

    private T newInstanceOf(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    private Predicate<Field> annotatedFields() {
        return field -> field.isAnnotationPresent(com.pseudochaos.xpom.annotation.XPath.class);
    }

    private Consumer<Field> populateValue(T instance, String xml) {
        return field -> {
            Object value = extractValue(field, xml);
            setValue(instance, field, value);
        };
    }

    private Object extractValue(Field field, String xml) {
        if (isCollection(field)) {
            return extractor.extractCollection(xml, getXPath(field), getNamespaceContext());
        } else {
            return extractor.extractScalar(xml, getXPath(field), getNamespaceContext());
        }
    }

    private boolean isCollection(Field field) {
        return field.getType().isArray() || Collection.class.isAssignableFrom(field.getType());
    }

    private String getXPath(Field field) {
        return field.getAnnotation(com.pseudochaos.xpom.annotation.XPath.class).value();
    }

    private void setValue(T instance, Field field, Object value) {
        logger.debug("[{}] {} {} = {}", getXPath(field), getTypeString(field), field.getName(), value);
        Object result = configuration.resolveConverter(field).convert(value);
        set(instance, field, result);
    }

    private void set(T instance, Field field, Object result) {
        field.setAccessible(true);
        try {
            field.set(instance, result);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    private String getTypeString(Field field) {
        Class<?> type = field.getType();
        return type.isArray() ? type.getComponentType() + "[]" : type.toString();
    }

    public javax.xml.namespace.NamespaceContext getNamespaceContext() {
        final Map<String, String> namespaceContext = new HashMap<>();
        if (clazz.isAnnotationPresent(NamespaceContext.class)) {
            NamespaceContext context = clazz.getAnnotation(NamespaceContext.class);
            namespaceContext.putAll(stream(context.value()).collect(toMap(Namespace::prefix, Namespace::uri)));
        }

        return new javax.xml.namespace.NamespaceContext() {
            @Override
            public String getNamespaceURI(String prefix) {
                return namespaceContext.get(prefix);
            }

            @Override
            public String getPrefix(String namespaceURI) {
                return namespaceContext.entrySet().stream()
                        .filter(entry -> entry.getKey().equals(namespaceURI))
                        .map(Map.Entry::getKey)
                        .findFirst().orElse(null);
            }

            @Override
            public Iterator getPrefixes(String namespaceURI) {
                return namespaceContext.entrySet().stream()
                        .filter(entry -> entry.getKey().equals(namespaceURI))
                        .map(Map.Entry::getKey)
                        .collect(toList()).iterator();
            }
        };
    }
}
