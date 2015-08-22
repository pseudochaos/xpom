package com.pseudochaos.xpom.jaxp;

import com.pseudochaos.xpom.*;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.xpath.*;
import javax.xml.xpath.XPath;
import java.io.StringReader;

public class JaxpValueExtractor implements ValueExtractor {

    @Override
    public String extractScalar(String xml, com.pseudochaos.xpom.XPath xPath) {
        InputSource source = new InputSource(new StringReader(xml));
        try {
            XPath emptyXPath = XPathFactory.newInstance().newXPath();
            emptyXPath.setNamespaceContext(xPath.getNamespaceContext());
            XPathExpression xPathExpression = emptyXPath.compile(xPath.asString());
            return xPathExpression.evaluate(source);
        } catch (XPathExpressionException e) {
            throw new IllegalStateException(e);
        }
    }

    public String[] extractCollection(String xml, com.pseudochaos.xpom.XPath xPath) {
        InputSource source = new InputSource(new StringReader(xml));
        try {
            XPath emptyXPath = XPathFactory.newInstance().newXPath();
            emptyXPath.setNamespaceContext(xPath.getNamespaceContext());
            XPathExpression xPathExpression = emptyXPath.compile(xPath.asString());
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
