package com.pseudochaos.xpom;

import javax.xml.namespace.NamespaceContext;

/**
 * Idendity objects vs value objects - Fowler???
 */
public class XPath {

    private final String xPath;
    private final NamespaceContext namespaceContext;

    public XPath(String xPath) {
        this.xPath = xPath;
        this.namespaceContext = new XNamespaceContext();
    }

    public XPath(String xPath, NamespaceContext namespaceContext) {
        this.xPath = xPath;
        this.namespaceContext = namespaceContext;
    }

    public String asString() {
        return xPath;
    }

    public NamespaceContext getNamespaceContext() {
        return namespaceContext;
    }

}
