package com.pseudochaos.xpom;

import com.pseudochaos.xpom.annotation.Converter;
import com.pseudochaos.xpom.annotation.ExceptionHandlingStrategy;
import com.pseudochaos.xpom.annotation.XPath;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class XFieldTest {

    @XPath(value = "/dummy", mandatory = true) private String mandatoryFieldWithDefault = "xpom";
    @XPath(value = "/dummy", mandatory = false) private Double notMandatoryFieldWithoutDefault;
    @XPath("/dummy") private String[] arrayField;
    @XPath("/dummy") private List<String> listField;
    @XPath("/dummy") private int intFieldWithDefault = 1;
    @XPath("/dummy") private int intFieldWithoutDefault;

    @Test
    public void shouldIdentifyMandatoryFieldWhenAttributeSetOnXPathAnnotation() throws Exception {
        assertThat(xFieldFor("mandatoryFieldWithDefault").isMandatory()).isTrue();
    }

    private XField xFieldFor(String fieldName) throws NoSuchFieldException {
        return new XField(getClass().getDeclaredField(fieldName), new XNamespaceContext(XFieldTest.class));
    }

    @Test
    public void shouldIdentifyNotMandatoryFieldWhenAttributeSetOnXPathAnnotation() throws Exception {
        assertThat(xFieldFor("notMandatoryFieldWithoutDefault").isMandatory()).isFalse();
    }

    @Test
    public void shouldExtractStringXPathValueFromAnnotation() throws Exception {
        assertThat(xFieldFor("notMandatoryFieldWithoutDefault").getRawXPath()).isEqualTo("/dummy");
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
        assertThat(xFieldFor("notMandatoryFieldWithoutDefault").getTypeString()).isEqualTo("Double");
    }

    @Test
    public void shouldReturnCorrectlyFormattedStringForArray() throws Exception {
        assertThat(xFieldFor("arrayField").getTypeString()).isEqualTo("String[]");
    }

    @Test
    public void shouldReturnCorrectlyFormattedStringForPrimitiveType() throws Exception {
        assertThat(xFieldFor("intFieldWithDefault").getTypeString()).isEqualTo("int");
    }

    @Test
    public void shouldReturnCorrectlyFormattedStringForStandardCollection() throws Exception {
        assertThat(xFieldFor("listField").getTypeString()).isEqualTo("java.util.List<String>");
    }

    @Test
    public void shouldReturnCorrectlyFormattedString() throws Exception {
        assertThat(xFieldFor("listField").toString()).isEqualTo("@XPath(\"/dummy\") java.util.List<String> listField");
    }

    @XPath("/dummy")
    @ExceptionHandlingStrategy(ExceptionHandling.FAIL)
    private int fieldWithExceptionHandlingStrategy;

    @Test
    public void shouldExtractExceptionHandlingStrategyFromAnnotation() throws Exception {
        assertThat(xFieldFor("fieldWithExceptionHandlingStrategy").getExceptionHandlingStrategy()).isEqualTo(ExceptionHandling.FAIL);
    }

    @Test
    public void shouldReturnNullExceptionHandlingStrategyWhenNoAnnotationPresent() throws Exception {
        assertThat(xFieldFor("mandatoryFieldWithDefault").getExceptionHandlingStrategy()).isNull();
    }

    @Test
    public void shouldIdentifyThatPrimitiveFieldHasNonJavaDefaultValue() throws Exception {
        assertThat(xFieldFor("intFieldWithDefault").hasDefaultValue(this)).isTrue();
    }

    @Test
    public void shouldIdentifyThatPrimitiveFieldHasJavaDefaultValue() throws Exception {
        assertThat(xFieldFor("intFieldWithoutDefault").hasDefaultValue(this)).isFalse();
    }

    @Test
    public void shouldIdentifyThatReferenceFieldHasNonJavaDefaultValue() throws Exception {
        assertThat(xFieldFor("mandatoryFieldWithDefault").hasDefaultValue(this)).isTrue();
    }

    @Test
    public void shouldIdentifyThatReferenceFieldHasJavaDefaultValue() throws Exception {
        assertThat(xFieldFor("notMandatoryFieldWithoutDefault").hasDefaultValue(this)).isFalse();
    }

    @XPath("/dummy")
    @Converter(com.pseudochaos.xpom.Converter.class)
    private int fieldWithConverter;

    @Test
    public void shouldExtractOptionalWithConverterFromConverterAnnotation() throws Exception {
        assertThat(xFieldFor("fieldWithConverter").getConverter()).isEqualTo(Optional.of(com.pseudochaos.xpom.Converter.class));
    }

    @Test
    public void shouldExtractEmptyOptionalWhenNoConverterAnnotationPresent() throws Exception {
        assertThat(xFieldFor("intFieldWithDefault").getConverter()).isEqualTo(Optional.empty());
    }
}
