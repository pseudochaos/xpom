package com.pseudochaos.xpom.jaxp;

import com.pseudochaos.xpom.ValueExtractor;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.xpath.*;
import java.io.StringReader;

public class JaxpValueExtractor implements ValueExtractor {

    @Override
    public String extractScalar(String xml, String xPath) {
        InputSource source = new InputSource(new StringReader(xml));
        try {
            XPath emptyXPath = XPathFactory.newInstance().newXPath();
            XPathExpression xPathExpression = emptyXPath.compile(xPath);
            return xPathExpression.evaluate(source);
        } catch (XPathExpressionException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String[] extractCollection(String xml, String xPath) {
        InputSource source = new InputSource(new StringReader(xml));
        try {
            XPath emptyXPath = XPathFactory.newInstance().newXPath();
            XPathExpression xPathExpression = emptyXPath.compile(xPath);
            NodeList nodes = (NodeList) xPathExpression.evaluate(source, XPathConstants.NODESET);
            String[] result = new String[nodes.getLength()];
            for (int i = 0; i < nodes.getLength(); i++) {
                result[i] = nodes.item(i).getTextContent();
            }
            return result;
        } catch (XPathExpressionException e) {
            throw new IllegalStateException(e);
        }
    }
}
