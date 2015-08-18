package com.pseudochaos.xpom;

import com.pseudochaos.xpom.annotation.Namespace;
import com.pseudochaos.xpom.annotation.NamespaceContext;
import com.pseudochaos.xpom.jaxp.JaxpValueExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.*;

public final class XPom<T> {

    private static final Logger logger = LoggerFactory.getLogger(XPom.class);

    private final Class<T> clazz;
    private final ValueExtractor extractor;
    private final Configuration configuration;
    private final Set<XField> fields;

    XPom(Class<T> clazz) {
        this.clazz = clazz;
        this.extractor = new JaxpValueExtractor();
        this.configuration = new Configuration();

        this.fields = stream(clazz.getDeclaredFields())
                .filter(annotatedFields())
                .map(XField::new)
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
            result = extractor.extractCollection(xml, field.getXPath(), getNamespaceContext());
        } else {
            result = extractor.extractScalar(xml, field.getXPath(), getNamespaceContext());
        }
        logger.debug("{} = {}", field, result);
        return result;
    }

    private Object convert(Object rawValue, XField field) {
        ExceptionHandlingStrategy strategy = configuration.getExceptionHandlingStrategy(field.getJavaField());
        if (rawValue == null) {
            return strategy.handleValueNotPresent(field.isMandatory(), field.getDefaultValue());
        } else {
            try {
                return configuration.resolveConverter(field.getJavaField()).convert(rawValue);
            } catch (Exception e) {
                return strategy.handleConversionException(e, field.isMandatory(), field.getDefaultValue());
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
