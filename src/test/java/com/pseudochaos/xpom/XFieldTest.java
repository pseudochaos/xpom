package com.pseudochaos.xpom;

import com.pseudochaos.xpom.annotation.XPath;
import org.junit.Test;

import java.lang.reflect.Field;
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
        XField xField = new XField(getField("mandatoryField"));
        assertThat(xField.isMandatory()).isTrue();
    }

    private Field getField(String name) throws NoSuchFieldException {
        return XFieldTest.class.getDeclaredField(name);
    }

    @Test
    public void shouldIdentifyNotMandatoryFieldWhenAttributeSetOnXPathAnnotation() throws Exception {
        XField xField = new XField(getField("notMandatoryField"));
        assertThat(xField.isMandatory()).isFalse();
    }

    @Test
    public void shouldExtractStringXPathValueFromAnnotation() throws Exception {
        XField xField = new XField(getField("notMandatoryField"));
        assertThat(xField.getRawXPath()).isEqualTo("/dummy");
    }

    @Test
    public void shouldRecognizeArrayFieldAsCollection() throws Exception {
        XField xField = new XField(getField("arrayField"));
        assertThat(xField.isCollection()).isTrue();
    }

    @Test
    public void shouldRecognizeListFieldAsCollection() throws Exception {
        XField xField = new XField(getField("listField"));
        assertThat(xField.isCollection()).isTrue();
    }

    @Test
    public void shouldReturnCorrectlyFormattedStringForWrappedScalarType() throws Exception {
        XField xField = new XField(getField("notMandatoryField"));
        assertThat(xField.getTypeString()).isEqualTo("Double");
    }

    @Test
    public void shouldReturnCorrectlyFormattedStringForArray() throws Exception {
        XField xField = new XField(getField("arrayField"));
        assertThat(xField.getTypeString()).isEqualTo("String[]");
    }

    @Test
    public void shouldReturnCorrectlyFormattedStringForPrimitiveType() throws Exception {
        XField xField = new XField(getField("intFieldWithDefaultValue"));
        assertThat(xField.getTypeString()).isEqualTo("int");
    }

    @Test
    public void shouldReturnCorrectlyFormattedStringForStandardCollection() throws Exception {
        XField xField = new XField(getField("listField"));
        assertThat(xField.getTypeString()).isEqualTo("java.util.List<String>");
    }

    @Test
    public void shouldReturnCorrectlyFormattedString() throws Exception {
        XField xField = new XField(getField("listField"));
        assertThat(xField.toString()).isEqualTo("@XPath(\"/dummy\") java.util.List<String> listField");
    }
}