package com.pseudochaos.xpom;

public interface ValueExtractor {

    String extractScalar(String xml, XPath xPath);

    String[] extractCollection(String xml, XPath xPath);

}
