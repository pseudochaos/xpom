package com.pseudochaos.xpom;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.stream.IntStream;

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
        if (Collection.class.isAssignableFrom(field.getType())) {
            return new CollectionConverter(field, this);
        } else {
            return resolve(field.getType());
        }
    }

    @Override
    public Converter<?, ?> resolve(Class<?> type) {
        Converter<?, ?> result;
        if (Collection.class.isAssignableFrom(type)) {
            throw new XPomException("Can't be here!");
        } else if (type.isArray()) {
            result = arrayConverter(type, this);
        } else if (type.isEnum()) {
            result = enumConverter(type);
        } else {
            result = registry.get(type);
        }
        return Objects.requireNonNull(result, "Failed to find a suitable converter for the type: " + type);
    }

    private Converter<?, ?> enumConverter(Class<?> type) {
        return value -> stream(type.getEnumConstants())
                .filter(c -> c.toString().equals(value))
                .findFirst()
                // TODO: How to handle non-matching values
                .orElseThrow(() -> new XPomException("No enum constant value found"));
    }

    private Converter<String[], ?> arrayConverter(Class<?> type, ConverterResolver resolver) {
        return values -> {
            Class<?> componentType = type.getComponentType();
            Object array = Array.newInstance(componentType, values.length);
            Converter<String, ?> converter = (Converter<String, ?>) resolver.resolve(componentType);
            IntStream.range(0, values.length).forEach(index -> Array.set(array, index, converter.convert(values[index])));
            return array;
        };
    }

    static class CollectionConverter implements Converter<String[], Object> {

        private final Field field;
        private final ConverterResolver resolver;

        public CollectionConverter(Field field, ConverterResolver resolver) {
            this.field = field;
            this.resolver = resolver;
        }

        @Override
        public Object convert(String[] rawItems) {
            Collection collection = getCollection();
            Converter<String, ?> converter = resolveConverterForCollectionItem();
            stream(rawItems).forEach(item -> collection.add(converter.convert(item)));
            return collection;
        }

        private Converter<String, ?> resolveConverterForCollectionItem() {
            Class<?> elementType;
            if (field.getGenericType() instanceof ParameterizedType) {
                elementType = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
            } else {
                elementType = String.class; // If no GenericType present on the collection
            }
            return (Converter<String, ?>) resolver.resolve(elementType);
        }

        private Collection getCollection() {
            Class<?> type = field.getType();
            if (type.isInterface() || Modifier.isAbstract(type.getModifiers())) {
                if (List.class.isAssignableFrom(type)) {
                    return new ArrayList<>();
                }
                // TODO: Add more Collection subtypes here
            }
            return null;
        }
    }
}
