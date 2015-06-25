package com.pseudochaos.xpom;

import com.pseudochaos.xpom.annotation.XPath;

import java.lang.reflect.*;
import java.util.*;

import static java.util.Arrays.stream;

class HierarchicalConverterResolver implements ConverterResolver {

    private Map<Class<?>, Converter<?, ?>> registry = new HashMap<>();

    public HierarchicalConverterResolver() {
        // Primitives
        registry.put(Byte.TYPE, f -> Byte.decode((String) f));
        registry.put(Byte.class, f -> Byte.decode((String) f));
        registry.put(Short.TYPE, f -> Short.decode((String) f));
        registry.put(Short.class, f -> Short.decode((String) f));
        registry.put(Integer.TYPE, f -> Integer.decode((String) f));
        registry.put(Integer.class, f -> Integer.decode((String) f));
        registry.put(Long.TYPE, f -> Long.decode((String) f));
        registry.put(Long.class, f -> Long.decode((String) f));
        registry.put(Float.TYPE, f -> Float.valueOf((String) f));
        registry.put(Float.class, f -> Float.valueOf((String) f));
        registry.put(Double.TYPE, f -> Double.valueOf((String) f));
        registry.put(Double.class, f -> Double.valueOf((String) f));
        registry.put(Boolean.TYPE, f -> Boolean.parseBoolean((String) f));
        registry.put(Boolean.class, f -> Boolean.parseBoolean((String) f));
        registry.put(Character.TYPE, f -> ((String) f).charAt(0));
        registry.put(Character.class, f -> ((String) f).charAt(0));
        // Simple Data Objects
        registry.put(String.class, f -> f);

    }

    @Override
    public Converter<?, ?> resolve(Field field) {
        XPath xPath = field.getAnnotation(XPath.class);
        Class<? extends Converter> converter = xPath.converter();
        if (!converter.equals(Converter.class)) {
            System.out.println("Hi");
            return newInstance(converter);
        } else if (Collection.class.isAssignableFrom(field.getType())) {
            return f -> {
                String[] rawItems = (String[]) f;

                Converter<String, ?> conv;
                if (field.getGenericType() instanceof ParameterizedType) {
                    Class<?> elementType = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                    conv = (Converter<String, ?>) resolve(elementType);
                } else {
                    conv = (Converter<String, ?>) registry.get(String.class);
                }

                Collection collection = null;
                Class<?> type = field.getType();
                if (type.isInterface() || Modifier.isAbstract(type.getModifiers())) {
                    if (List.class.isAssignableFrom(type)) {
                        collection = new ArrayList<>();
                    }
                }

                for (String item : rawItems) {
                    collection.add(conv.convert(item));
                }
                return collection;
            };

        } else {
            return resolve(field.getType());
        }
    }

    @Override
    public Converter<?, ?> resolve(Class<?> type) {
        Converter<?, ?> result;
        if (type.isArray()) {
            result = new ArrayConverter(type, this);
        } else if (Collection.class.isAssignableFrom(type)) {
            throw new IllegalStateException("Can't be here");
        } else if (type.isEnum()) {
            result = enumConverter(type);
        } else {
            result = registry.get(type);
        }
        if (result == null) {
            throw new IllegalStateException("Failed to find a suitable converter for the type: " + type);
        }
        return result;
    }

    private <T> T newInstance(Class<T> type) {
        try {
            return type.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    private Converter<?, ?> enumConverter(Class<?> type) {
        return value -> {
            Optional<?> constant = stream(type.getEnumConstants()).filter(c -> c.toString().equals(value)).findFirst();
            if (constant.isPresent()) {
                return constant.get();
            } else {
                // TODO: handle non-matching value
                throw new IllegalStateException("No enum constant value found");
            }
        };
    }

    static class ArrayConverter implements Converter<Object, Object> {

        private final Class<?> type;
        private final ConverterResolver resolver;

        public ArrayConverter(Class<?> type, ConverterResolver resolver) {
            this.type = type;
            this.resolver = resolver;
        }

        @Override
        public Object convert(Object source) {
            String[] values = (String[]) source;
            Class<?> componentType = type.getComponentType();
            Object array = Array.newInstance(componentType, values.length);

            Converter<Object, ?> converter = (Converter<Object, ?>) resolver.resolve(componentType);
            for (int i = 0; i < values.length; i++) {
                Array.set(array, i, converter.convert(values[i]));
            }
            return array;
        }
    }
}
