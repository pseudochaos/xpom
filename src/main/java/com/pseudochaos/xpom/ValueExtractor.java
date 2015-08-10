package com.pseudochaos.xpom;

public interface ValueExtractor {

    String extractScalar(String xml, String xPath);
    String[] extractCollection(String xml, String xPath);

}
