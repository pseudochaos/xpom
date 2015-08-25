package com.pseudochaos.xpom;

import com.pseudochaos.xpom.annotation.XPath;
import org.junit.Test;

import static com.pseudochaos.xpom.ExceptionHandling.FAIL;
import static com.pseudochaos.xpom.ExceptionHandling.USE_DEFAULT;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Exception handling strategies levels (down-to-top):
 *      1) JVM Level   - can be overridden by setting a value on XPomFactory
 *      2) Class Level - can be overridden by setting a value on a particular class mapper
 *      3) Field Level - can be overridden by setting a value for a particular field on a particular class mapper (not recommended)
 *
 */
public class ExceptionHandlingITest {

    private static final String XML =
            "<values>" +
                "<present>Hello, XPom!</present>" +
            "</values>";

    public static final String DEFAULT_VALUE = "user's defined default value";

    private static <T> T to(Class<T> clazz) {
        return XPomFactory.create(clazz).using(XML);
    }

    // ------------ Value is present ------------------------------------------

    static class XmlValuePresent {
        @XPath(value = "/values/present", mandatory = true)
        private String mandatoryWithDefault = DEFAULT_VALUE;

        @XPath(value = "/values/present", mandatory = true)
        private String mandatoryWithoutDefault;

        @XPath("/values/present")
        private String notMandatoryWithDefault = DEFAULT_VALUE;

        @XPath("/values/present")
        private String notMandatoryWithoutDefault;
    }

    @Test
    public void shouldPopulateFieldsWithValuesFromXmlWhenAllValuesPresent() {
        XmlValuePresent testClass = to(XmlValuePresent.class);
        assertThat(testClass.mandatoryWithDefault).isEqualTo("Hello, XPom!");
        assertThat(testClass.mandatoryWithoutDefault).isEqualTo("Hello, XPom!");
        assertThat(testClass.notMandatoryWithDefault).isEqualTo("Hello, XPom!");
        assertThat(testClass.notMandatoryWithoutDefault).isEqualTo("Hello, XPom!");
    }

    // ------------ Value is NOT present --------------------------------------

    static class NotPresentMandatoryDefault {
        @XPath(value = "/values/not-present", mandatory = true)
        private String field = DEFAULT_VALUE;
    }

    @Test
    public void shouldPopulateMandatoryFieldWithDefaultValueWhenNoXmlValuePresent_USE_DEFAULT() {
        XPomFactory.setExceptionHandlingStrategy(USE_DEFAULT);
        assertThat(to(NotPresentMandatoryDefault.class).field).isEqualTo(DEFAULT_VALUE);
    }

    @Test(expected = NoValueException.class)
    public void shouldThrowExceptionForMandatoryFieldWithDefaultValueWhenNoXmlValuePresent_FAIL() {
        XPomFactory.setExceptionHandlingStrategy(FAIL);
        to(NotPresentMandatoryDefault.class);
    }

    static class NotPresentMandatoryNoDefault {
        @XPath(value = "/values/not-present", mandatory = true) private String field;
    }

    @Test(expected = NoValueException.class)
    public void shouldThrowExceptionForMandatoryFieldWithoutDefaultValueWhenNoXmlValuePresent_FAIL() {
        XPomFactory.setExceptionHandlingStrategy(FAIL);
        to(NotPresentMandatoryNoDefault.class);
    }

    @Test(expected = NoValueException.class)
    public void shouldThrowExceptionForMandatoryFieldWithoutDefaultValueWhenNoXmlValuePresent_USE_DEFAULT() {
        XPomFactory.setExceptionHandlingStrategy(USE_DEFAULT);
        to(NotPresentMandatoryNoDefault.class);
    }

    static class NotPresentNotMandatoryDefault {
        @XPath("/values/not-present") private String field = DEFAULT_VALUE;
    }

    @Test
    public void shouldPopulateFieldWithDefaultValueWhenNoXmlValuePresent_USE_DEFAULT() {
        XPomFactory.setExceptionHandlingStrategy(USE_DEFAULT);
        assertThat(to(NotPresentNotMandatoryDefault.class).field).isEqualTo(DEFAULT_VALUE);
    }

    @Test
    public void shouldPopulateFieldWithDefaultValueWhenNoXmlValuePresent_FAIL() {
        XPomFactory.setExceptionHandlingStrategy(FAIL);
        assertThat(to(NotPresentNotMandatoryDefault.class).field).isEqualTo(DEFAULT_VALUE);
    }

    static class NotPresentNotMandatoryNoDefault {
        @XPath("/values/not-present") private double field;
    }

    @Test
    public void shouldPopulateFieldWithJavaDefaultValueWhenNoXmlValuePresent_FAIL() {
        XPomFactory.setExceptionHandlingStrategy(FAIL);
        assertThat(to(NotPresentNotMandatoryNoDefault.class).field).isZero();
    }

    @Test
    public void shouldPopulateFieldWithJavaDefaultValueWhenNoXmlValuePresent_USE_DEFAULT() {
        XPomFactory.setExceptionHandlingStrategy(USE_DEFAULT);
        assertThat(to(NotPresentNotMandatoryNoDefault.class).field).isZero();
    }

    // Value is present, but an exception occurs during conversion ------------

    static class ConversionExceptionMandatoryDefault {
        @XPath(value = "/values/present", mandatory = true) private int field = 42;
    }

    @Test(expected = ConversionException.class)
    public void shouldThrowExceptionForMandatoryFieldWithDefaultWhenConversionExceptionOccurs_FAIL() {
        XPomFactory.setExceptionHandlingStrategy(FAIL);
        to(ConversionExceptionMandatoryDefault.class);
    }

    @Test
    public void shouldPopulateMandatoryFieldWithDefaultWhenConversionExceptionOccurs_USE_DEFAULT() {
        XPomFactory.setExceptionHandlingStrategy(USE_DEFAULT);
        assertThat(to(ConversionExceptionMandatoryDefault.class).field).isEqualTo(42);
    }

    static class ConversionExceptionMandatoryNoDefault {
        @XPath(value = "/values/present", mandatory = true) private int field;
    }

    @Test(expected = ConversionException.class)
    public void shouldThrowExceptionForMandatoryFieldWithoutDefaultWhenConversionExceptionOccurs_FAIL() {
        XPomFactory.setExceptionHandlingStrategy(FAIL);
        to(ConversionExceptionMandatoryNoDefault.class);
    }

    @Test
    public void shouldPopulateMandatoryFieldWithJavaDefaultWhenConversionExceptionOccurs_USE_DEFAULT() {
        XPomFactory.setExceptionHandlingStrategy(USE_DEFAULT);
        assertThat(to(ConversionExceptionMandatoryNoDefault.class).field).isZero();
    }

    static class ConversionExceptionNotMandatoryDefault {
        @XPath("/values/present") private int field = 42;
    }

    @Test(expected = ConversionException.class)
    public void shouldThrowExceptionForFieldWithDefaultWhenConversionExceptionOccurs_FAIL() {
        XPomFactory.setExceptionHandlingStrategy(FAIL);
        to(ConversionExceptionNotMandatoryDefault.class);
    }

    @Test
    public void shouldPopulateFieldWithDefaultWhenConversionExceptionOccurs_USE_DEFAULT() {
        XPomFactory.setExceptionHandlingStrategy(USE_DEFAULT);
        assertThat(to(ConversionExceptionNotMandatoryDefault.class).field).isEqualTo(42);
    }

    static class ConversionExceptionNotMandatoryNoDefault {
        @XPath("/values/present") private int field;
    }

    @Test(expected = ConversionException.class)
    public void shouldThrowExceptionForFieldWithoutDefaultWhenConversionExceptionOccurs_FAIL() {
        XPomFactory.setExceptionHandlingStrategy(FAIL);
        to(ConversionExceptionNotMandatoryNoDefault.class);
    }

    @Test
    public void shouldPopulateFieldWithJavaDefaultWhenConversionExceptionOccurs_USE_DEFAULT() {
        XPomFactory.setExceptionHandlingStrategy(USE_DEFAULT);
        assertThat(to(ConversionExceptionNotMandatoryNoDefault.class).field).isZero();
    }
}
