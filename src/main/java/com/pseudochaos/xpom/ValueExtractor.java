package com.pseudochaos.xpom;

import java.util.Optional;

public interface ValueExtractor {

    Optional<String> extractScalar(String xml, XPath xPath);

    Optional<String[]> extractCollection(String xml, XPath xPath);

}
