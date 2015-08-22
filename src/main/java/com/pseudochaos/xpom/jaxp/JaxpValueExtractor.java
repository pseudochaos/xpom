package com.pseudochaos.xpom.jaxp;

import com.pseudochaos.xpom.*;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.xpath.*;
import javax.xml.xpath.XPath;
import java.io.StringReader;
import java.util.Optional;
import java.util.stream.IntStream;

public class JaxpValueExtractor implements ValueExtractor {

    @Override
    public Optional<String> extractScalar(String xml, com.pseudochaos.xpom.XPath xPath) {
        InputSource source = new InputSource(new StringReader(xml));
        try {
            XPath emptyXPath = XPathFactory.newInstance().newXPath();
            emptyXPath.setNamespaceContext(xPath.getNamespaceContext());
            XPathExpression xPathExpression = emptyXPath.compile(xPath.asString());
            String maybeResult = xPathExpression.evaluate(source);
            return Optional.ofNullable(maybeResult);
        } catch (XPathExpressionException e) {
            throw new IllegalStateException(e);
        }
    }

    public Optional<String[]> extractCollection(String xml, com.pseudochaos.xpom.XPath xPath) {
        InputSource source = new InputSource(new StringReader(xml));
        try {
            XPath emptyXPath = XPathFactory.newInstance().newXPath();
            emptyXPath.setNamespaceContext(xPath.getNamespaceContext());
            XPathExpression xPathExpression = emptyXPath.compile(xPath.asString());
            NodeList nodes = (NodeList) xPathExpression.evaluate(source, XPathConstants.NODESET);
            if (nodes.getLength() > 0) {
                String[] maybeResult = new String[nodes.getLength()];
                for (int i = 0; i < nodes.getLength(); i++) {
                    maybeResult[i] = nodes.item(i).getTextContent();
                }
                return Optional.of(maybeResult);
            } else {
                return Optional.empty();
            }
        } catch (XPathExpressionException e) {
            throw new IllegalStateException(e);
        }
    }
}
