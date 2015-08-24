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

    private static final String PREFIX_ONE = "prefix1uri";
    private static final String PREFIX_TWO = "prefix2uri";
    private static final String URI = "URI";
    private static final String ANOTHER_PREFIX = "anotherPrefix";
    private static final String ANOTHER_URI = "another.URI";
    private static final String DEFAULT_NS_URI = "default.ns.uri";

    @NamespaceContext({
            @Namespace(prefix = PREFIX_ONE, uri = URI),
            @Namespace(prefix = PREFIX_TWO, uri = URI),
            @Namespace(prefix = ANOTHER_PREFIX, uri = ANOTHER_URI),
    })
    private static class CommonNamespaceTest {}
    private javax.xml.namespace.NamespaceContext commonContext = new XNamespaceContext(CommonNamespaceTest.class);

    @NamespaceContext({
            @Namespace(uri = DEFAULT_NS_URI),
    })
    private static class DefaultURITest {}
    private javax.xml.namespace.NamespaceContext defaultURIContext = new XNamespaceContext(DefaultURITest.class);

    private Object dataForGetNamespaceURI() {
        return new Object[]{
                new Object[]{XMLConstants.DEFAULT_NS_PREFIX, XMLConstants.NULL_NS_URI},
                new Object[]{XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI},
                new Object[]{XMLConstants.XMLNS_ATTRIBUTE, XMLConstants.XMLNS_ATTRIBUTE_NS_URI},
                new Object[]{"unboundPrefix", XMLConstants.NULL_NS_URI},
                new Object[]{PREFIX_ONE, URI},
                new Object[]{PREFIX_TWO, URI},
                new Object[]{ANOTHER_PREFIX, ANOTHER_URI},
        };
    }

    @Parameters(method = "dataForGetNamespaceURI")
    @Test
    public void shouldReturnCorrectNamespaceURIForGivenPrefix(String prefix, String uri) {
        assertThat(commonContext.getNamespaceURI(prefix)).isEqualTo(uri);
    }

    @Test
    public void shouldReturnCorrectNamespaceURIWhenDefaultNamespaceExplicitlyDefined() {
        assertThat(defaultURIContext.getNamespaceURI(XMLConstants.DEFAULT_NS_PREFIX)).isEqualTo(DEFAULT_NS_URI);
    }

    private Object dataForGetPrefix() {
        return new Object[]{
                new Object[]{XMLConstants.NULL_NS_URI, XMLConstants.DEFAULT_NS_PREFIX}, // TODO: Think what to do with NULL_NS_URI when no default ns bound: null or DEFAULT_NS_PREFIX
                new Object[]{XMLConstants.XML_NS_URI, XMLConstants.XML_NS_PREFIX},
                new Object[]{XMLConstants.XMLNS_ATTRIBUTE_NS_URI, XMLConstants.XMLNS_ATTRIBUTE},
                new Object[]{"unboundNamespaceURI", null},
                new Object[]{ANOTHER_URI, ANOTHER_PREFIX},
        };
    }

    @Parameters(method = "dataForGetPrefix")
    @Test
    public void shouldReturnCorrectPrefixForGivenNamespaceURI(String uri, String prefix) {
        assertThat(commonContext.getPrefix(uri)).isEqualTo(prefix);
    }

    @Test
    public void shouldReturnCorrectPrefixWhenDefaultNamespaceURIExplicitlyDefined() {
        assertThat(defaultURIContext.getPrefix(DEFAULT_NS_URI)).isEqualTo(XMLConstants.DEFAULT_NS_PREFIX);
    }

    @Test
    public void shouldReturnCorrectPrefixWhenSeveralPrefixesDefinedForNamespaceURI() {
        assertThat(commonContext.getPrefix(URI)).isIn(PREFIX_ONE, PREFIX_TWO);
    }

    private Object dataForGetPrefixes() {
        return new Object[]{
                new Object[]{XMLConstants.XML_NS_URI, asList(XMLConstants.XML_NS_PREFIX)},
                new Object[]{XMLConstants.XMLNS_ATTRIBUTE_NS_URI, asList(XMLConstants.XMLNS_ATTRIBUTE)},
                new Object[]{"unboundNamespaceURI", emptyList()},
                new Object[]{URI, asList(PREFIX_ONE, PREFIX_TWO)},
                new Object[]{ANOTHER_URI, asList(ANOTHER_PREFIX)},
                new Object[]{XMLConstants.NULL_NS_URI, asList(XMLConstants.DEFAULT_NS_PREFIX)},
        };
    }

    @Parameters(method = "dataForGetPrefixes")
    @Test
    public void shouldReturnAllPrefixesForGivenNamespaceURI(String uri, List<String> prefixes) {
        assertThat(commonContext.getPrefixes(uri)).containsAll(prefixes);
    }

    @Test
    public void shouldReturnAllPrefixesForDefaultNamespaceURIWhenItIsExplicitlyDefined() {
        assertThat(defaultURIContext.getPrefixes(DEFAULT_NS_URI)).containsAll(asList(XMLConstants.DEFAULT_NS_PREFIX));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenPrefixIsNull() {
        commonContext.getNamespaceURI(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenQueryingPrefixForNullNamespaceURI() {
        commonContext.getPrefix(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenQueryingPrefixesForNullNamespaceURI() {
        commonContext.getPrefixes(null);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldFailWhenThereAreSeveralIdenticalPrefixes() {
        @NamespaceContext({
                @Namespace(prefix = PREFIX_ONE, uri = URI),
                @Namespace(prefix = PREFIX_ONE, uri = ANOTHER_URI),
        })
        class DuplicatedPrefixesTest {}
        new XNamespaceContext(DuplicatedPrefixesTest.class);
    }
}