package com.pseudochaos.xpom;

import com.pseudochaos.xpom.annotation.XPath;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class ArraysITest {
    
    private static final String XML =
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
                "<booleans>" +
                    "<true>true</true>" +
                    "<false>false</false>" +
                "</booleans>" +
                "<timeUnits>" +
                    "<timeUnit>SECONDS</timeUnit>" +
                    "<timeUnit>MILLISECONDS</timeUnit>" +
                "</timeUnits>" +
            "</arrays>";

    private static <T> T to(Class<T> clazz) {
        return XPom.map(XML).to(clazz);
    }

    // Arrays of primitive types:

    static class PByteArray { @XPath("/arrays/integers/value") byte[] values; }
    @Test
    public void array_of_bytes() {
        assertThat(to(PByteArray.class).values).hasSize(3).contains((byte) 1, (byte) -2, (byte) 3);
    }

    static class PShortArray { @XPath("/arrays/integers/value") short[] values; }
    @Test
    public void array_of_shorts() {
        assertThat(to(PShortArray.class).values).hasSize(3).contains((short) 1, (short) -2, (short) 3);
    }

    static class PIntArray { @XPath("/arrays/integers/value") int[] values; }
    @Test
    public void array_of_ints() {
        assertThat(to(PIntArray.class).values).hasSize(3).contains(1, -2, 3);
    }

    static class PLongArray { @XPath("/arrays/integers/value") long[] values; }
    @Test
    public void array_of_longs() {
        assertThat(to(XLongArray.class).values).hasSize(3).contains(1L, -2L, 3L);
    }

    static class PFloatArray { @XPath("/arrays/decimals/value") float[] values; }
    @Test
    public void array_of_floats() {
        assertThat(to(XFloatArray.class).values).hasSize(3).contains(3.1415926f, -3.1415926f, 2.7182818f);
    }

    static class PDoubleArray { @XPath("/arrays/decimals/value") double[] values; }
    @Test
    public void array_of_doubles() {
        assertThat(to(XDoubleArray.class).values).hasSize(3).contains(3.1415926d, -3.1415926d, 2.7182818d);
    }

    static class PBooleanArray { @XPath("/arrays/booleans/*") boolean[] values; }
    @Test
    public void array_of_booleans() {
        assertThat(to(XBooleanArray.class).values).hasSize(2).contains(true, false);
    }

    static class PCharArray { @XPath("/arrays/booleans/*") char[] values; }
    @Test
    public void array_of_chars() {
        assertThat(to(XCharacterArray.class).values).hasSize(2).contains('t', 'f');
    }

    // Arrays of Wrapped types

    static class XByteArray { @XPath("/arrays/integers/value") Byte[] values; }
    @Test
    public void array_of_Bytes() {
        assertThat(to(XByteArray.class).values).hasSize(3).contains(Byte.valueOf("1"), Byte.valueOf("-2"), Byte.valueOf("3"));
    }

    static class XShortArray { @XPath("/arrays/integers/value") Short[] values; }
    @Test
    public void array_of_Shorts() {
        assertThat(to(XShortArray.class).values).hasSize(3).contains(Short.valueOf("1"), Short.valueOf("-2"), Short.valueOf("3"));
    }

    static class XIntegerArray { @XPath("/arrays/integers/value") Integer[] values; }
    @Test
    public void array_of_Integers() {
        assertThat(to(XIntegerArray.class).values).hasSize(3).contains(Integer.valueOf(1), Integer.valueOf(-2), Integer.valueOf(3));
    }

    static class XLongArray { @XPath("/arrays/integers/value") Long[] values; }
    @Test
    public void array_of_Longs() {
        assertThat(to(XLongArray.class).values).hasSize(3).contains(Long.valueOf(1L), Long.valueOf(-2L), Long.valueOf(3L));
    }

    static class XFloatArray { @XPath("/arrays/decimals/value") Float[] values; }
    @Test
    public void array_of_Floats() {
        assertThat(to(XFloatArray.class).values).hasSize(3).contains(Float.valueOf(3.1415926f), Float.valueOf(-3.1415926f), Float.valueOf(2.7182818f));
    }

    static class XDoubleArray { @XPath("/arrays/decimals/value") Double[] values; }
    @Test
    public void array_of_Doubles() {
        assertThat(to(XDoubleArray.class).values).hasSize(3).contains(Double.valueOf(3.1415926d), Double.valueOf(-3.1415926d), Double.valueOf(2.7182818d));
    }

    static class XBooleanArray { @XPath("/arrays/booleans/*") Boolean[] values; }
    @Test
    public void array_of_Booleans() {
        assertThat(to(XBooleanArray.class).values).hasSize(2).contains(Boolean.TRUE, Boolean.FALSE);
    }

    static class XCharacterArray { @XPath("/arrays/booleans/*") Character[] values; }
    @Test
    public void array_of_Characters() {
        assertThat(to(XCharacterArray.class).values).hasSize(2).contains(Character.valueOf('t'), Character.valueOf('f'));
    }

    // Arrays of simple data objects:

    static class StringArray { @XPath("/arrays/booleans/*") String[] values; }
    @Test
    public void array_of_Strings() {
        assertThat(to(StringArray.class).values).hasSize(2).contains("true", "false");
    }

    // Arrays of enum constants:

    static class EnumConstantArray { @XPath("/arrays/timeUnits/timeUnit") TimeUnit[] values; }
    @Test
    public void array_of_EnumConstants() {
        assertThat(to(EnumConstantArray.class).values).hasSize(2).contains(TimeUnit.SECONDS, TimeUnit.MILLISECONDS);
    }
}
