package com.pseudochaos.xpom;

import com.pseudochaos.xpom.annotation.Converter;
import com.pseudochaos.xpom.annotation.XPath;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ConverterITest {

    private static final String XML =
            "<dataTypes>" +
                "<bin>0b11010</bin>" +
            "</dataTypes>";

    private static <T> T to(Class<T> clazz) {
        return XPomFactory.create(clazz).using(XML);
    }

    static class BinIntConverter implements com.pseudochaos.xpom.Converter<String, Integer> {
        @Override
        public Integer convert(String source) {
            return source.startsWith("0b") ? Integer.parseInt(source.replaceFirst("0b", ""), 2) : Integer.parseInt( source, 2);
        }
    }

    static class PBinInt {
        @Converter(BinIntConverter.class)
        @XPath(value = "/dataTypes/bin") int binInt;
    }

    @Test
    public void primitive_bin_to_int() throws Exception {
        assertThat(to(PBinInt.class).binInt).isEqualTo(26);
    }

}
