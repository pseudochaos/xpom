package com.pseudochaos.xpom;

import com.pseudochaos.xpom.annotation.XPath;
import org.junit.Test;

import static org.assertj.core.api.StrictAssertions.assertThat;

public class PrimitivesAndWrappersITest {

    private static final String XML =
            "<dataTypes>" +
                "<byte>100</byte>" +
                "<short>256</short>" +
                "<int>" +
                    "<dec>26</dec>" +
                    "<hex>0x1a</hex>" +
                    "<bin>0b11010</bin>" + // TODO
                "</int>" +
                "<long>" +
                    "<simple>1234567890123456</simple>" +
                    "<underscore>0b11010010_01101001_10010100_10010010</underscore>" + // TODO: Using Underscore Characters in Numeric Literals
                "</long>" +
                "<float>3.1415926F</float>" +
                "<double>" +
                    "<simple>3.1415926d</simple>" +
                    "<scientific>1.234e2</scientific>" + // TODO: Scientific notation
                "</double>" +
                "<boolean>" +
                    "<true>true</true>" +
                    "<false>false</false>" +
                "</boolean>" +
                "<char>" +
                    "<simple>c</simple>" +
                    "<unicode>\\u00ED</unicode>" + // TODO: Unicode char - "S? Se?or in Spanish"
                "</char>" +
            "</dataTypes>";

    private static <T> T to(Class<T> clazz) {
        return XPom.map(XML).to(clazz);
    }
    
    static class PByte { @XPath("/dataTypes/byte") byte aByte; }
    @Test
    public void primitive_byte() {
        assertThat(to(PByte.class).aByte).isEqualTo((byte) 100);
    }

    static class XByte { @XPath("/dataTypes/byte") Byte aByte; }
    @Test
    public void wrapper_Byte() {
        assertThat(to(XByte.class).aByte).isEqualTo(Byte.valueOf((byte) 100));
    }

    static class PShort { @XPath("/dataTypes/short") short aShort; }
    @Test
    public void primitive_short() {
        assertThat(to(PShort.class).aShort).isEqualTo((short) 256);
    }

    static class XShort { @XPath("/dataTypes/short") Short aShort; }
    @Test
    public void wrapper_Short() {
        assertThat(to(XShort.class).aShort).isEqualTo(Short.valueOf((short) 256));
    }

    static class PDecInt { @XPath("/dataTypes/int/dec") int decInt; }
    @Test
    public void primitive_int_dec() {
        assertThat(to(PDecInt.class).decInt).isEqualTo(26);
    }

    static class XDecInteger { @XPath("/dataTypes/int/dec") Integer decInt; }
    @Test
    public void wrapper_Integer_dec() {
        assertThat(to(XDecInteger.class).decInt).isEqualTo(Integer.valueOf(26));
    }

    static class PHexInt { @XPath("/dataTypes/int/hex") int hexInt; }
    @Test
    public void primitive_int_hex() {
        assertThat(to(PHexInt.class).hexInt).isEqualTo(26);
    }

    static class PLong { @XPath("/dataTypes/long/simple") long aLong; }
    @Test
    public void primitive_long() {
        assertThat(to(PLong.class).aLong).isEqualTo(1234567890123456L);
    }

    static class XLong { @XPath("/dataTypes/long/simple") Long aLong; }
    @Test
    public void wrapper_Long() {
        assertThat(to(XLong.class).aLong).isEqualTo(new Long(1234567890123456L));
    }

    static class PFloat { @XPath("/dataTypes/float") float aFloat; }
    @Test
    public void primitive_float() {
        assertThat(to(PFloat.class).aFloat).isEqualTo(3.1415926f);
    }

    static class XFloat { @XPath("/dataTypes/float") Float aFloat; }
    @Test
    public void wrapper_Float() {
        assertThat(to(XFloat.class).aFloat).isEqualTo(new Float(3.1415926f));
    }

    static class PDouble { @XPath("/dataTypes/double/simple") double aDouble; }
    @Test
    public void primitive_double() {
        assertThat(to(PDouble.class).aDouble).isEqualTo(3.1415926d);
    }

    static class XDouble { @XPath("/dataTypes/double/simple") Double aDouble; }
    @Test
    public void wrapper_Double() {
        assertThat(to(XDouble.class).aDouble).isEqualTo(new Double(3.1415926d));
    }

    static class PBoolean {
        @XPath("/dataTypes/boolean/true") boolean aTrue;
        @XPath("/dataTypes/boolean/false") boolean aFalse;
    }
    @Test
    public void primitive_boolean() {
        PBoolean pBoolean = to(PBoolean.class);
        assertThat(pBoolean.aTrue).isEqualTo(true);
        assertThat(pBoolean.aFalse).isEqualTo(false);
    }

    static class XBoolean {
        @XPath("/dataTypes/boolean/true") Boolean aTrue;
        @XPath("/dataTypes/boolean/false") Boolean aFalse;
    }
    @Test
    public void wrapper_Boolean() {
        XBoolean xBoolean = to(XBoolean.class);
        assertThat(xBoolean.aTrue).isEqualTo(Boolean.TRUE);
        assertThat(xBoolean.aFalse).isEqualTo(Boolean.FALSE);
    }

    static class PChar { @XPath("/dataTypes/char/simple") char aChar; }
    @Test
    public void primitive_char() {
        assertThat(to(PChar.class).aChar).isEqualTo('c');
    }

    static class XCharacter { @XPath("/dataTypes/char/simple") Character aChar; }
    @Test
    public void wrapper_Character() {
        assertThat(to(XCharacter.class).aChar).isEqualTo(Character.valueOf('c'));
    }
}
