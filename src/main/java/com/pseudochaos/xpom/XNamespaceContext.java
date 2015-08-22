package com.pseudochaos.xpom;

import com.pseudochaos.xpom.annotation.Namespace;
import com.pseudochaos.xpom.annotation.NamespaceContext;

import javax.xml.XMLConstants;
import java.util.*;
import java.util.function.Function;

import static java.util.Arrays.stream;
import static java.util.Collections.emptySet;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.*;

/**
 * TODO: Add validation - several NS URIs for one prefix
 */
class XNamespaceContext implements javax.xml.namespace.NamespaceContext {

    final Map<String, Set<String>> uriToPrefixes = new HashMap<>();

    public <T> XNamespaceContext() {
        uriToPrefixes.put(XMLConstants.NULL_NS_URI, Collections.singleton(XMLConstants.DEFAULT_NS_PREFIX));
        uriToPrefixes.put(XMLConstants.XML_NS_URI, Collections.singleton(XMLConstants.XML_NS_PREFIX));
        uriToPrefixes.put(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, Collections.singleton(XMLConstants.XMLNS_ATTRIBUTE));
    }

    public <T> XNamespaceContext(Class<T> clazz) {
        this();

        if (clazz.isAnnotationPresent(NamespaceContext.class)) {
            NamespaceContext context = clazz.getAnnotation(NamespaceContext.class);
            Map<String, Set<String>> clazzContext = stream(context.value())
                    .collect(groupingBy(Namespace::uri, mapping(Namespace::prefix, toSet())));
            uriToPrefixes.putAll(clazzContext);
        }
        System.out.println(uriToPrefixes);
    }

    @Override
    public String getNamespaceURI(String prefix) {
        if (prefix == null) throw new IllegalArgumentException("Given prefix is Null");
        return uriToPrefixes.entrySet().stream()
                .flatMap(e -> e.getValue().stream()
                        .collect(toMap(identity(), p -> e.getKey()))
                        .entrySet().stream())
                .filter(e -> e.getKey().equals(prefix))
                .findFirst().map(Map.Entry::getValue).orElse(XMLConstants.NULL_NS_URI);
//        return uriToPrefixes.entrySet().stream()
//                .filter(e -> e.getValue().contains(prefix))
//                .findFirst().map(Map.Entry::getKey).orElse(XMLConstants.NULL_NS_URI);
    }

    @Override
    public String getPrefix(String namespaceURI) {
        if (namespaceURI == null) throw new IllegalArgumentException("Given namespaceURI is Null");
        Iterator<String> prefixesIterator = uriToPrefixes.getOrDefault(namespaceURI, emptySet()).iterator();
        return prefixesIterator.hasNext() ? prefixesIterator.next() : null;
    }

    @Override
    public Iterator getPrefixes(String namespaceURI) {
        if (namespaceURI == null) throw new IllegalArgumentException("Given namespaceURI is Null");
        return uriToPrefixes.getOrDefault(namespaceURI, emptySet()).iterator();
    }
}
