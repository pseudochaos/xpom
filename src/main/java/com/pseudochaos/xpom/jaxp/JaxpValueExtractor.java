package com.pseudochaos.xpom.jaxp;

import com.pseudochaos.xpom.ValueExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.xpath.*;
import java.io.StringReader;
import java.util.Optional;

public class JaxpValueExtractor implements ValueExtractor {

    private static final Logger logger = LoggerFactory.getLogger(JaxpValueExtractor.class);

    @Override
    public Optional<String> extractScalar(String xml, com.pseudochaos.xpom.XPath xPath) {
        InputSource source = new InputSource(new StringReader(xml));
        XPath emptyXPath = XPathFactory.newInstance().newXPath();
        emptyXPath.setNamespaceContext(xPath.getNamespaceContext());
        try {
            XPathExpression xPathExpression = emptyXPath.compile(xPath.asString());
            String result = xPathExpression.evaluate(source);
            return result.isEmpty() ? Optional.empty() : Optional.of(result);
        } catch (XPathExpressionException e) {
            throw new IllegalStateException(e);
        }
    }

    public Optional<String[]> extractCollection(String xml, com.pseudochaos.xpom.XPath xPath) {
        InputSource source = new InputSource(new StringReader(xml));
        XPath emptyXPath = XPathFactory.newInstance().newXPath();
        emptyXPath.setNamespaceContext(xPath.getNamespaceContext());
        try {
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
