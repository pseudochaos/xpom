package com.pseudochaos.xpom;

import com.pseudochaos.xpom.annotation.Converter;
import com.pseudochaos.xpom.annotation.XPath;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <b>XPom</b> (XPath to Object Mapping) performs mapping of arbitrary complex xml to POJOs annotated with XPathes.
 * Seamless, out-of-the-box data type conversion of:
 * <li>
 *     <ul>fields with <code>primitive</code> types: <code>byte, short, int, long, float, double, boolean, char</code>.</ul>
 *     <ul>fields containing <code>Simple Data Objects</code> like Numbers and Strings.</ul>
 *     <ul>fields containing arrays of <code>primitive</code> types.</ul>
 *     <ul>fields containing arrays of <code>Simple Data Objects</code></ul>
 *     <ul>fields containing <b>enumerations</b></ul>
 *     <ul>fields containing <b>collections</b> of <code>Simple Data Objects</code></ul>
 * </li>
 *
 * Hierarchical configuration:
 *      JVM Level: XPom.registerTypeConverter(Byte.class, new ByteConverter())
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
public class XPomTest {

    @XPath(value = "/xpath", exceptionHandlingPolicy = ExceptionHandlingPolicy.THROW)
    private static final String XML =
            "<dataTypes>" +
                    "<primitives>" +
                        "<byte>100</byte>" +
                        "<short>256</short>" +
                        "<int>" +
                            "<decVal>26</decVal>" +
                            "<hexVal>0x1a</hexVal>" +
                            "<binVal>0b11010</binVal>" +
                        "</int>" +
                        "<long>1234567890123456</long>" +
//                        "<long>0b11010010_01101001_10010100_10010010</long>" + // TODO: Using Underscore Characters in Numeric Literals
//                        "<float>3.1415926f</float>" +
                        "<float>3.1415926F</float>" +
                        "<double>3.1415926d</double>" +
//                        "<double>1.234e2</double>" + // TODO: Scientific notation
                        "<boolean>" +
                            "<values>" +
                                "<true>true</true>" +
                                "<false>false</false>" +
                            "</values>" +
                        "</boolean>" +
                        "<char>c</char>" +
//                        "<char>S\u00ED Se\u00F1or</char>" + // TODO: Unicode char - "S? Se?or in Spanish"
                    "</primitives>"+
                    "<simpleDataObjects>" +
                        "<string>Hello, XPom!</string>" +
                        "<integer>13</integer>" +
                    "</simpleDataObjects>" +
                    "<enums>" +
                        "<timeUnit>SECONDS</timeUnit>" +
                        "<timeUnit>MILLISECONDS</timeUnit>" +
                    "</enums>" +
                    "<arrays>" +
                        "<integers>" +
                            "<value>1</value>" +
                            "<value>-2</value>" +
                            "<value>+3</value>" +
                        "</integers>" +
                        "<decimals>" +
                            "<value>3.1415926</value>" +
                            "<value>-3.1415926</value>" +
                            "<value>+2.7182818</value>" +
                        "</decimals>" +
                    "</arrays>" +
            "</dataTypes>";

    private static <T> T to(Class<T> clazz) {
        return XPom.map(XML).to(clazz);
    }

    // -------------------- Primitives and Wrappers ---------------------------

    static class PByte { @XPath("/dataTypes/primitives/byte") byte aByte; }
    @Test
    public void primitive_byte_absolute_XPath() throws Exception {
        assertThat(to(PByte.class).aByte).isEqualTo((byte) 100);
    }

    static class XByte { @XPath("/dataTypes/primitives/byte") Byte aByte; }
    @Test
    public void wrapper_Byte_absolute_XPath() throws Exception {
        assertThat(to(XByte.class).aByte).isEqualTo(Byte.valueOf((byte) 100));
    }

    static class PShort { @XPath("/dataTypes/primitives/short") short aShort; }
    @Test
    public void primitive_short_absolute_XPath() throws Exception {
        assertThat(to(PShort.class).aShort).isEqualTo((short) 256);
    }

    static class XShort { @XPath("/dataTypes/primitives/short") Short aShort; }
    @Test
    public void wrapper_Short_absolute_XPath() throws Exception {
        assertThat(to(XShort.class).aShort).isEqualTo(Short.valueOf((short) 256));
    }

    static class PDecInt { @XPath("/dataTypes/primitives/int/decVal") int decInt; }
    @Test
    public void primitive_int_dec_absolute_XPath() throws Exception {
        assertThat(to(PDecInt.class).decInt).isEqualTo(26);
    }

    static class XDecInteger { @XPath("/dataTypes/primitives/int/decVal") Integer decInt; }
    @Test
    public void wrapper_Integer_dec_absolute_XPath() throws Exception {
        assertThat(to(XDecInteger.class).decInt).isEqualTo(Integer.valueOf(26));
    }

    static class PHexInt { @XPath("/dataTypes/primitives/int/hexVal") int hexInt; }
    @Test
    public void primitive_int_hex_absolute_XPath() throws Exception {
        assertThat(to(PHexInt.class).hexInt).isEqualTo(26);
    }

    static class PLong { @XPath("/dataTypes/primitives/long") long aLong; }
    @Test
    public void primitive_long_absolute_XPath() throws Exception {
        assertThat(to(PLong.class).aLong).isEqualTo(1234567890123456L);
    }

    static class XLong { @XPath("/dataTypes/primitives/long") Long aLong; }
    @Test
    public void wrapper_Long_absolute_XPath() throws Exception {
        assertThat(to(XLong.class).aLong).isEqualTo(new Long(1234567890123456L));
    }

    static class PFloat { @XPath("/dataTypes/primitives/float") float aFloat; }
    @Test
    public void primitive_float_absolute_XPath() throws Exception {
        assertThat(to(PFloat.class).aFloat).isEqualTo(3.1415926f);
    }

    static class XFloat { @XPath("/dataTypes/primitives/float") Float aFloat; }
    @Test
    public void wrapper_Float_absolute_XPath() throws Exception {
        assertThat(to(XFloat.class).aFloat).isEqualTo(new Float(3.1415926f));
    }

    static class PDouble { @XPath("/dataTypes/primitives/double") double aDouble; }
    @Test
    public void primitive_double_absolute_XPath() throws Exception {
        assertThat(to(PDouble.class).aDouble).isEqualTo(3.1415926d);
    }

    static class XDouble { @XPath("/dataTypes/primitives/double") Double aDouble; }
    @Test
    public void wrapper_Double_absolute_XPath() throws Exception {
        assertThat(to(XDouble.class).aDouble).isEqualTo(new Double(3.1415926d));
    }

    static class PBoolean {
        @XPath("/dataTypes/primitives/boolean/values/true") boolean aTrue;
        @XPath("/dataTypes/primitives/boolean/values/false") boolean aFalse;
    }
    @Test
    public void primitive_boolean_absolute_XPath() throws Exception {
        PBoolean pBoolean = to(PBoolean.class);
        assertThat(pBoolean.aTrue).isEqualTo(true);
        assertThat(pBoolean.aFalse).isEqualTo(false);
    }

    static class XBoolean {
        @XPath("/dataTypes/primitives/boolean/values/true") Boolean aTrue;
        @XPath("/dataTypes/primitives/boolean/values/false") Boolean aFalse;
    }
    @Test
    public void wrapper_Boolean_absolute_XPath() throws Exception {
        XBoolean xBoolean = to(XBoolean.class);
        assertThat(xBoolean.aTrue).isEqualTo(Boolean.TRUE);
        assertThat(xBoolean.aFalse).isEqualTo(Boolean.FALSE);
    }

    static class PChar { @XPath("/dataTypes/primitives/char") char aChar; }
    @Test
    public void primitive_char_absolute_XPath() throws Exception {
        assertThat(to(PChar.class).aChar).isEqualTo('c');
    }

    static class XCharacter { @XPath("/dataTypes/primitives/char") Character aChar; }
    @Test
    public void wrapper_Character_absolute_XPath() throws Exception {
        assertThat(to(XCharacter.class).aChar).isEqualTo(Character.valueOf('c'));
    }

    // -------------------- Simple Data Objects -------------------------------

    static class XString { @XPath("/dataTypes/simpleDataObjects/string") String string; }
    @Test
    public void object_String_absolute_XPath() throws Exception {
        assertThat(to(XString.class).string).isEqualTo("Hello, XPom!");
    }

    // -------------------- Enumerations --------------------------------------

    static class XEnum { @XPath("/dataTypes/enums/timeUnit") TimeUnit timeUnit; }
    @Test
    public void enum_absolute_XPath() throws Exception {
        assertThat(to(XEnum.class).timeUnit).isEqualTo(TimeUnit.SECONDS);
    }

    // -------------------- Arrays --------------------------------------------
    // Arrays of primitive types:

    static class PByteArray { @XPath("/dataTypes/arrays/integers/value") byte[] values; }
    @Test
    public void array_byte_absolute_XPath() throws Exception {
        assertThat(to(PByteArray.class).values).hasSize(3).contains((byte) 1, (byte) -2, (byte) 3);
    }

    static class PShortArray { @XPath("/dataTypes/arrays/integers/value") short[] values; }
    @Test
    public void array_short_absolute_XPath() throws Exception {
        assertThat(to(PShortArray.class).values).hasSize(3).contains((short) 1, (short) -2, (short) 3);
    }

    static class PIntArray { @XPath("/dataTypes/arrays/integers/value") int[] values; }
    @Test
    public void array_int_absolute_XPath() throws Exception {
        assertThat(to(PIntArray.class).values).hasSize(3).contains(1, -2, 3);
    }

    static class PLongArray { @XPath("/dataTypes/arrays/integers/value") long[] values; }
    @Test
    public void array_long_absolute_XPath() throws Exception {
        assertThat(to(PLongArray.class).values).hasSize(3).contains(1L, -2L, 3L);
    }

    static class PFloatArray { @XPath("/dataTypes/arrays/decimals/value") float[] values; }
    @Test
    public void array_float_absolute_XPath() throws Exception {
        assertThat(to(PFloatArray.class).values).hasSize(3).contains(3.1415926f, -3.1415926f, 2.7182818f);
    }

    static class PDoubleArray { @XPath("/dataTypes/arrays/decimals/value") double[] values; }
    @Test
    public void array_double_absolute_XPath() throws Exception {
        assertThat(to(PDoubleArray.class).values).hasSize(3).contains(3.1415926d, -3.1415926d, 2.7182818d);
    }

    static class PBooleanArray { @XPath("/dataTypes/primitives/boolean/values/*") boolean[] values; }
    @Test
    public void array_boolean_absolute_XPath() throws Exception {
        assertThat(to(PBooleanArray.class).values).hasSize(2).contains(true, false);
    }

    static class PCharArray { @XPath("/dataTypes/primitives/boolean/values/*") char[] values; }
    @Test
    public void array_char_absolute_XPath() throws Exception {
        assertThat(to(PCharArray.class).values).hasSize(2).contains('t', 'f');
    }

    // Arrays of simple data objects:

    static class StringArray { @XPath("/dataTypes/primitives/boolean/values/*") String[] values; }
    @Test
    public void array_String_absolute_XPath() throws Exception {
        assertThat(to(StringArray.class).values).hasSize(2).contains("true", "false");
    }

    // Arrays of enum constants:

    static class EnumConstantArray { @XPath("/dataTypes/enums/timeUnit") TimeUnit[] values; }
    @Test
    public void array_EnumConstant_absolute_XPath() throws Exception {
        assertThat(to(EnumConstantArray.class).values).hasSize(2).contains(TimeUnit.SECONDS, TimeUnit.MILLISECONDS);
    }

    // -------------------- Collections ---------------------------------------

    static class StringList { @XPath("/dataTypes/primitives/boolean/values/*") List<String> values; }
    @Test
    public void collection_List_String_absolute_XPath() throws Exception {
        assertThat(to(StringList.class).values).isInstanceOf(ArrayList.class).hasSize(2).contains("true", "false");
    }

    static class BooleanList { @XPath("/dataTypes/primitives/boolean/values/*") List<Boolean> values; }
    @Test
    public void collection_List_Boolean_absolute_XPath() throws Exception {
        assertThat(to(BooleanList.class).values).isInstanceOf(ArrayList.class).hasSize(2).contains(true, false);
    }

    static class RawList { @XPath("/dataTypes/primitives/boolean/values/*") List values; }
    @Test
    public void collection_List_Raw_absolute_XPath() throws Exception {
        assertThat(to(RawList.class).values).hasSize(2).contains("true", "false").isInstanceOf(ArrayList.class);
        // TODO: Submit a bug report
//        assertThat(to(RawList.class).values).isInstanceOf(ArrayList.class).hasSize(2).contains("true", "false");
    }

    // -------------------- XPath functions -----------------------------------

    static class XPathFunctionCount { @XPath("count(//primitives/*)") int javaPrimitivesCount; }
    @Test
    public void function_count_to_int() throws Exception {
        assertThat(to(XPathFunctionCount.class).javaPrimitivesCount).isEqualTo(8);
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
        XPom.map(XML).to(PrivateClass.class);
    }

    // TODO: How to handle classes without zero argument constructors
    static class NoZeroArgumentConstructor { NoZeroArgumentConstructor(int intField) {} }
    @Test(expected = IllegalStateException.class)
    public void shouldCorrectlyHandleClassWithNoZeroArgumentConstructor() throws Exception {
        XPom.map(XML).to(NoZeroArgumentConstructor.class);
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
        @XPath(value = "/dataTypes/primitives/int/binVal", converter = BinIntConverter.class)
        @Converter(BinIntConverter.class)
        int binInt;
    }
    @Test
    public void primitive_int_bin_absolute_XPath() throws Exception {
        assertThat(to(PBinInt.class).binInt).isEqualTo(26);
    }

}
