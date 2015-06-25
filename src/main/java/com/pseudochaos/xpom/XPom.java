package com.pseudochaos.xpom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static java.util.Arrays.stream;

public final class XPom {

    private static final Logger logger = LoggerFactory.getLogger(XPom.class);

    private final String xml;
    private final ConverterResolver converterResolver;

    private XPom(String xml, ConverterResolver converterResolver) {
        this.xml = xml;
        this.converterResolver = converterResolver;
    }

    public static XPom map(String xml) {
        return new XPom(xml,  new HierarchicalConverterResolver());
    }

    public <T> T to(Class<T> clazz) {
        T instance = newInstanceOf(clazz);
        stream(clazz.getDeclaredFields())
                .filter(annotatedFields())
                .forEach(populateValue(instance));
        return instance;
    }

    private <T> T newInstanceOf(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    private Predicate<Field> annotatedFields() {
        return field -> field.isAnnotationPresent(com.pseudochaos.xpom.annotation.XPath.class);
    }

    private <T> Consumer<Field> populateValue(T instance) {
        return field -> {
            Object value = extractValue(field);
            setValue(instance, field, value);
        };
    }

    private Object extractValue(Field field) {
        if (isCollection(field)) {
            return extractCollection(getXPath(field));
        } else {
            return extractScalar(getXPath(field));
        }
    }

    private boolean isCollection(Field field) {
        return field.getType().isArray() || Collection.class.isAssignableFrom(field.getType());
    }

    private String getXPath(Field field) {
        return field.getAnnotation(com.pseudochaos.xpom.annotation.XPath.class).value();
    }

    private String extractScalar(String xPath) {
        InputSource source = new InputSource(new StringReader(xml));
        try {
            XPathExpression xPathExpression = XPathFactory.newInstance().newXPath().compile(xPath);
            return xPathExpression.evaluate(source);
        } catch (XPathExpressionException e) {
            throw new IllegalStateException(e);
        }
    }

    private String[] extractCollection(String xPath) {
        InputSource source = new InputSource(new StringReader(xml));
        try {
            XPathExpression xPathExpression = XPathFactory.newInstance().newXPath().compile(xPath);
            NodeList nodes = (NodeList) xPathExpression.evaluate(source, XPathConstants.NODESET);
            String[] result = new String[nodes.getLength()];
            for (int i = 0; i < nodes.getLength(); i++) {
                result[i] = nodes.item(i).getTextContent();
            }
            return result;
        } catch (XPathExpressionException e) {
            throw new IllegalStateException(e);
        }
    }


    private <T> void setValue(T instance, Field field, Object value) {
        logger.debug("[{}] {} {} = {}", getXPath(field), getTypeString(field), field.getName(), value);
//        Converter<Object, ?> converter = (Converter<Object, ?>) converterResolver.resolve(field.getType());
        Converter<Object, ?> converter = (Converter<Object, ?>) converterResolver.resolve(field);
        Object result = converter.convert(value);
        set(instance, field, result);
    }

    private <T> void set(T instance, Field field, Object result) {
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
}
