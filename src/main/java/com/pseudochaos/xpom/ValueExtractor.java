package com.pseudochaos.xpom;

import javax.xml.namespace.NamespaceContext;

public interface ValueExtractor {

    String extractScalar(String xml, String xPath, NamespaceContext namespaceContext);
    String[] extractCollection(String xml, String xPath, NamespaceContext namespaceContext);

}
