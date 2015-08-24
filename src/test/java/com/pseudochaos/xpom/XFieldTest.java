package com.pseudochaos.xpom;

import com.pseudochaos.xpom.annotation.XPath;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class XFieldTest {

    @XPath(value = "/dummy", mandatory = true) private String mandatoryField;
    @XPath(value = "/dummy", mandatory = false) private Double notMandatoryField;
    @XPath("/dummy") private String[] arrayField;
    @XPath("/dummy") private List<String> listField;
    @XPath("/dummy") private int intFieldWithDefaultValue = 1;

    @Test
    public void shouldIdentifyMandatoryFieldWhenAttributeSetOnXPathAnnotation() throws Exception {
        assertThat(xFieldFor("mandatoryField").isMandatory()).isTrue();
    }

    private XField xFieldFor(String fieldName) throws NoSuchFieldException {
        return new XField(getClass().getDeclaredField(fieldName), new XNamespaceContext(XFieldTest.class));
    }

    @Test
    public void shouldIdentifyNotMandatoryFieldWhenAttributeSetOnXPathAnnotation() throws Exception {
        assertThat(xFieldFor("notMandatoryField").isMandatory()).isFalse();
    }

    @Test
    public void shouldExtractStringXPathValueFromAnnotation() throws Exception {
        assertThat(xFieldFor("notMandatoryField").getRawXPath()).isEqualTo("/dummy");
    }

    @Test
    public void shouldRecognizeArrayFieldAsCollection() throws Exception {
        assertThat(xFieldFor("arrayField").isCollection()).isTrue();
    }

    @Test
    public void shouldRecognizeListFieldAsCollection() throws Exception {
        assertThat(xFieldFor("listField").isCollection()).isTrue();
    }

    @Test
    public void shouldReturnCorrectlyFormattedStringForWrappedScalarType() throws Exception {
        assertThat(xFieldFor("notMandatoryField").getTypeString()).isEqualTo("Double");
    }

    @Test
    public void shouldReturnCorrectlyFormattedStringForArray() throws Exception {
        assertThat(xFieldFor("arrayField").getTypeString()).isEqualTo("String[]");
    }

    @Test
    public void shouldReturnCorrectlyFormattedStringForPrimitiveType() throws Exception {
        assertThat(xFieldFor("intFieldWithDefaultValue").getTypeString()).isEqualTo("int");
    }

    @Test
    public void shouldReturnCorrectlyFormattedStringForStandardCollection() throws Exception {
        assertThat(xFieldFor("listField").getTypeString()).isEqualTo("java.util.List<String>");
    }

    @Test
    public void shouldReturnCorrectlyFormattedString() throws Exception {
        assertThat(xFieldFor("listField").toString()).isEqualTo("@XPath(\"/dummy\") java.util.List<String> listField");
    }
}