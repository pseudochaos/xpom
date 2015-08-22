package com.pseudochaos.xpom;

import com.pseudochaos.xpom.annotation.Namespace;
import com.pseudochaos.xpom.annotation.NamespaceContext;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.xml.XMLConstants;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnitParamsRunner.class)
public class XNamespaceContextTest {

    private static final String PREFIX_ONE = "prefixOne";
    private static final String PREFIX_TWO = "prefixTwo";
    private static final String URI = "URI.for.prefix.one.and.two";
    private static final String ANOTHER_PREFIX = "another.prefix";
    private static final String ANOTHER_URI = "another.URI";
    private static final String DEFAULT_NS_URI = "default.ns.URI";

    @NamespaceContext({
            @Namespace(uri = DEFAULT_NS_URI),
            @Namespace(prefix = PREFIX_ONE, uri = URI),
            @Namespace(prefix = PREFIX_TWO, uri = URI),
            @Namespace(prefix = ANOTHER_PREFIX, uri = ANOTHER_URI),
    })
    static class AnnotationTest {}

    private javax.xml.namespace.NamespaceContext context = new XNamespaceContext(AnnotationTest.class);

    private Object defaultMapping() {
        return new Object[]{
                new Object[]{XMLConstants.DEFAULT_NS_PREFIX, XMLConstants.NULL_NS_URI}, // FIXME: It doesn't work. Fix issue with resolving default ns uri when it's present
                new Object[]{XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI},
                new Object[]{XMLConstants.XMLNS_ATTRIBUTE, XMLConstants.XMLNS_ATTRIBUTE_NS_URI},
        };
    }

    private Object getNamespaceURI() {
        return new Object[]{
                new Object[]{"unboundPrefix", XMLConstants.NULL_NS_URI},
                new Object[]{PREFIX_ONE, URI},
                new Object[]{PREFIX_TWO, URI},
        };
    }

    @Parameters(method = "defaultMapping,getNamespaceURI")
    @Test
    public void shouldReturnCorrectNamespaceURIForGivenPrefix(String prefix, String uri) {
        assertThat(context.getNamespaceURI(prefix)).isEqualTo(uri);
    }

    private Object getPrefix() {
        return new Object[]{
                new Object[]{XMLConstants.DEFAULT_NS_PREFIX, DEFAULT_NS_URI},
                new Object[]{null, "unboundNamespaceURI"},
                new Object[]{ANOTHER_PREFIX, ANOTHER_URI},
        };
    }

    @Parameters(method = "defaultMapping,getPrefix")
    @Test
    public void shouldReturnCorrectPrefixForGivenNamespaceURI(String prefix, String uri) {
        assertThat(context.getPrefix(uri)).isEqualTo(prefix);
    }

    private Object getPrefixes() {
        return new Object[]{
                new Object[]{XMLConstants.XML_NS_URI, asList(XMLConstants.XML_NS_PREFIX)},
                new Object[]{XMLConstants.XMLNS_ATTRIBUTE_NS_URI, asList(XMLConstants.XMLNS_ATTRIBUTE) },
                new Object[]{"unboundNamespaceURI", emptyList()},
                new Object[]{URI, asList(PREFIX_ONE, PREFIX_TWO)},
                new Object[]{ANOTHER_URI, asList(ANOTHER_PREFIX)},
                new Object[]{XMLConstants.DEFAULT_NS_PREFIX, asList(DEFAULT_NS_URI)}, // TODO: Make it pass
                new Object[]{XMLConstants.DEFAULT_NS_PREFIX, asList(XMLConstants.NULL_NS_URI)}, // TODO: Make it fail! in another scenario
        };
    }

    @Parameters(method = "getPrefixes")
    @Test
    public void shouldReturnAllPrefixesForGivenNamespaceURI(String uri, List<String> prefixes) {
        assertThat(context.getPrefixes(uri)).containsAll(prefixes); // TODO: choose correct contains* function
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenPrefixIsNull() {
        context.getNamespaceURI(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenQueryingPrefixForNullNamespaceURI() {
        context.getPrefix(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenQueryingPrefixesForNullNamespaceURI() {
        context.getPrefixes(null);
    }
}