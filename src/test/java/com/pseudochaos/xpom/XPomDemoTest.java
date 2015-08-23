package com.pseudochaos.xpom;

import com.pseudochaos.xpom.annotation.Converter;
import com.pseudochaos.xpom.annotation.XPath;
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

    // -------------------- XPath functions -----------------------------------

    static class XPathFunctionCount { @XPath("count(//boolean/*)") int javaPrimitivesCount; }
    @Test
    public void function_count_to_int() throws Exception {
        assertThat(to(XPathFunctionCount.class).javaPrimitivesCount).isEqualTo(2);
    }

    static class XPathFunctionBoolean {
        @XPath("boolean(/unexistingNode)") private boolean isUnexistingNodeExist;
        @XPath("boolean(/dataTypes)") private boolean isExistingNodeExist;
    }
    @Test
    public void function_node_to_boolean() throws Exception {
        XPathFunctionBoolean functionBoolean = to(XPathFunctionBoolean.class);
        assertThat(functionBoolean.isUnexistingNodeExist).isFalse();
        assertThat(functionBoolean.isExistingNodeExist).isTrue();
    }

    // -------------------- Edge cases ----------------------------------------

    // TODO: Inheritance
    // TODO: Final field
    // TODO: field is static
    // TODO: 128 for byte
    // TODO: Not found: exception or warning, java default, users default
    // TODO: Find a way to deal with private inner/static classes

    // TODO: Define strategy of handling non annotated classes. Options: a warning to the log or exception
    static class NonAnnotatedClass { int intField; }
    @Test
    public void shouldCorrectlyHandleClassWithoutXPathAnnotations() throws Exception {
        assertThat(to(NonAnnotatedClass.class).intField).isZero();
    }

    // TODO: How to handle private classes
    private static class PrivateClass {}
    @Test(expected = IllegalStateException.class)
    public void shouldCorrectlyHandlePrivateClass() throws Exception {
        to(PrivateClass.class);
    }

    // TODO: How to handle classes without zero argument constructors
    static class NoZeroArgumentConstructor { NoZeroArgumentConstructor(int intField) {} }
    @Test(expected = IllegalStateException.class)
    public void shouldCorrectlyHandleClassWithNoZeroArgumentConstructor() throws Exception {
        to(NoZeroArgumentConstructor.class);
    }

    // -------------------- Converters  ---------------------------------------

    // Filed level converter

    static class BinIntConverter implements com.pseudochaos.xpom.Converter<String, Integer> {
        @Override
        public Integer convert(String source) {
            return source.startsWith("0b") ? Integer.parseInt(source.replaceFirst("0b", ""), 2) : Integer.parseInt( source, 2);
        }
    }
    static class PBinInt {
        @XPath(value = "/dataTypes/bin", converter = BinIntConverter.class)
        @Converter(BinIntConverter.class)
        int binInt;
    }
    @Test
    public void primitive_int_bin_absolute_XPath() throws Exception {
        assertThat(to(PBinInt.class).binInt).isEqualTo(26);
    }

}
