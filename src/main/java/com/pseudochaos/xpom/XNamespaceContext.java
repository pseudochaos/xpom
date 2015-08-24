package com.pseudochaos.xpom;

import com.pseudochaos.xpom.annotation.Namespace;
import com.pseudochaos.xpom.annotation.NamespaceContext;

import javax.xml.XMLConstants;
import java.util.*;

import static java.util.Arrays.stream;
import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.*;

class XNamespaceContext implements javax.xml.namespace.NamespaceContext {

    final Map<String, Set<String>> uriToPrefixes;
    final Map<String, String> prefixToUri = new HashMap<>();

    /**
     *
     * @param clazz a class annotated with {@code @NamespaceContext} annotation
     * @throws IllegalStateException if several identical prefixes defined on the class
     */
    public XNamespaceContext(Class<?> clazz) {
        prefixToUri.put(XMLConstants.DEFAULT_NS_PREFIX, XMLConstants.NULL_NS_URI);
        prefixToUri.put(XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI);
        prefixToUri.put(XMLConstants.XMLNS_ATTRIBUTE, XMLConstants.XMLNS_ATTRIBUTE_NS_URI);

        if (clazz.isAnnotationPresent(NamespaceContext.class)) {
            NamespaceContext context = clazz.getAnnotation(NamespaceContext.class);
            prefixToUri.putAll(
                    stream(context.value()).collect(toMap(Namespace::prefix, Namespace::uri))
            );
        }

        uriToPrefixes = prefixToUri.entrySet().stream()
                .collect(groupingBy(Map.Entry::getValue, mapping(Map.Entry::getKey, toSet())));
    }

    @Override
    public String getNamespaceURI(String prefix) {
        if (prefix == null) throw new IllegalArgumentException("Given prefix is Null");
        return prefixToUri.getOrDefault(prefix, XMLConstants.NULL_NS_URI);

//        return uriToPrefixes.entrySet().stream()
//                .flatMap(e -> e.getValue().stream()
//                        .collect(toMap(identity(), p -> e.getKey()))
//                        .entrySet().stream())
//                .filter(e -> e.getKey().equals(prefix))
//                .findFirst().map(Map.Entry::getValue).orElse(XMLConstants.NULL_NS_URI);

//        return uriToPrefixes.entrySet().stream()
//                .filter(e -> e.getValue().contains(prefix))
//                .findFirst().map(Map.Entry::getKey).orElse(XMLConstants.NULL_NS_URI);
    }

    @Override
    public String getPrefix(String namespaceURI) {
        if (namespaceURI == null) throw new IllegalArgumentException("Given namespaceURI is Null");
        Iterator<String> prefixesIterator = getPrefixes(namespaceURI);
        return prefixesIterator.hasNext() ? prefixesIterator.next() : null;
    }

    @Override
    public Iterator getPrefixes(String namespaceURI) {
        if (namespaceURI == null) throw new IllegalArgumentException("Given namespaceURI is Null");
        return uriToPrefixes.getOrDefault(namespaceURI, emptySet()).iterator();
    }
}
