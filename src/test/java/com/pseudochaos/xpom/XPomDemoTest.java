package com.pseudochaos.xpom;

import com.pseudochaos.xpom.annotation.XPath;
import org.junit.Ignore;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <b>XPom</b> (XPath to Object Mapping) performs mapping of arbitrary complex xml to POJOs annotated with XPathes.
 * Seamless, out-of-the-box data type conversion of:
 * <li>
 *     <ul>DONE: fields with <code>primitive</code> types: <code>byte, short, int, long, float, double, boolean, char</code>.</ul>
 *     <ul>DONE: fields with <code>wrapped</code> types: <code>Byte, Short, Integer, Long, Float, Double, Boolean, Character</code>.</ul>
 *
 *     <ul>DONE: fields containing <code>enumerations</code></ul>
 *     <ul>TODO: fields containing <code>Simple Data Objects</code> like Numbers and Strings.</ul>
 *
 *     <ul>DONE: fields containing arrays of <code>primitive</code> types.</ul>
 *     <ul>DONE: fields containing arrays of <code>wrapped</code> types.</ul>
 *     <ul>DONE: fields containing arrays of <code>Simple Data Objects</code> and <code>enumerations</code></ul>
 *
 *     <ul>TODO: fields containing <code>collections</code> of <code>Simple Data Objects</code></ul>
 * </li>
 *
 * Exception handling strategies levels (down-to-top):
 *      1) JVM Level   - can be overridden by setting a value on XPomFactory
 *      2) Class Level - can be overridden by setting a value on a particular class mapper
 *      3) Field Level - can be overridden by setting a value for a particular field on a particular class mapper (not recommended)
 *
 * Hierarchical configuration:
 *      JVM Level: XPomFactory.registerTypeConverter(Byte.class, new ByteConverter())
 *        Class Factory Level: XPom.map(Hamlet.class).registerTypeConverter(Byte.class, new HexByteConverter())
 *        ???Field Factory Level: XPom.map(Hamlet.class).registerTypeConverter(Byte.class, new HexByteConverter())
 *          Class Level: @TypeConverter(HexByteConverter.class)
 *            Field Level: @TypeConverter(ByteConverter.class)
 *
 * @TypeConverter vs @FieldConverter
 *
 * Exceptions and Default values
 *
 * TODO: Default values. Haw to handle them?
 * TODO: What to do with final fields?
 * TODO: What to do with static fields?
 * TODO: unsignedInteger, unsignedLong
 *
 * TODO: Levels of type converters: default -> factory -> class -> type
 * TODO: Chain of converters for parsing some value (i.e. boolean "true/false", "yes/no", "1/0")
 * TODO: Difference between Repository and Registry patterns
 */
public class XPomDemoTest {

    private static final String XML =
            "<dataTypes>" +
                "<string>Hello, XPom!</string>" +
                "<boolean>" +
                    "<true>true</true>" +
                    "<false>false</false>" +
                "</boolean>" +
                "<bin>0b11010</bin>" +
            "</dataTypes>";

    private static <T> T to(Class<T> clazz) {
        return XPomFactory.create(clazz).using(XML);
    }

    static class Clazz { @XPath("/none") String string; }
    @Test
    public void no_value() throws Exception {
        assertThat(to(Clazz.class).string).isEqualTo("Hello, XPom!");
    }


    // -------------------- Simple Data Objects -------------------------------

    static class XString { @XPath("/dataTypes/string") String string; }
    @Test
    public void object_String_absolute_XPath() throws Exception {
        assertThat(to(XString.class).string).isEqualTo("Hello, XPom!");
    }

    // -------------------- Edge cases ----------------------------------------

    // TODO: Inheritance
    // TODO: field is static
    // TODO: 128 for byte
    
    private static class PrivateStaticNestedClass { @XPath("/dataTypes/string") String string; }
    @Test
    @Ignore
    public void shouldPerformMappingOfPrivateStaticNestedClass() {
        assertThat(to(PrivateStaticNestedClass.class).string).isEqualTo("Hello, XPom!");
    }

    class InnerClass { @XPath("/dataTypes/string") String string; }
    @Test(expected = XPomException.class)
    @Ignore
    public void shouldThrowExceptionWhenCreatingMapperForInnerClass() {
        XPomFactory.create(InnerClass.class);
    }

    private class PrivateInnerClass { @XPath("/dataTypes/string") String string; }
    @Test(expected = XPomException.class)
    @Ignore
    public void shouldThrowExceptionWhenCreatingMapperForPrivateInnerClass() {
        XPomFactory.create(PrivateInnerClass.class);
    }

    static class StaticNestedClass { @XPath("/dataTypes/string") String string; }
    @Test
    public void shouldPerformMappingOfStaticNestedClass() {
        assertThat(to(StaticNestedClass.class).string).isEqualTo("Hello, XPom!");
    }

    static class FinalField { @XPath("/dataTypes/string") final String string = "changeMeViaXPath"; }
    @Test(expected = XPomException.class)
    @Ignore("not ready yet. Don't forget about positive scenario - all annotated fields are not final")
    public void shouldThrowExceptionWhenCreatingMapperForClassWithAnnotatedFinalFields() {
        XPomFactory.create(FinalField.class);
    }

    // TODO: Define a strategy of handling non annotated classes. Options: a warning to the log or exception
    static class NonAnnotatedClass { int intField; }
    @Test
    public void shouldCorrectlyHandleClassWithoutXPathAnnotations() {
        assertThat(to(NonAnnotatedClass.class).intField).isZero();
    }

    // TODO: How to handle classes without zero argument constructors?
    static class NoZeroArgumentConstructor {
        NoZeroArgumentConstructor(int intField) {}
    }
    @Test(expected = XPomException.class)
    @Ignore
    public void shouldThrowExceptionWhenCreatingMapperForClassWithoutZeroArgumentConstructor() {
        XPomFactory.create(NoZeroArgumentConstructor.class);
    }
}
