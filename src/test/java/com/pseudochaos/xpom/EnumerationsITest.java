package com.pseudochaos.xpom;

import com.pseudochaos.xpom.annotation.XPath;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class EnumerationsITest {

    private static final String XML =
            "<enums>" +
                "<timeUnit>SECONDS</timeUnit>" +
                "<timeUnit>MILLISECONDS</timeUnit>" +
            "</enums>";

    private static <T> T to(Class<T> clazz) {
        return XPom.map(XML).to(clazz);
    }

    static class XEnum {
        @XPath("/enums/timeUnit[2]")
        TimeUnit timeUnit;
    }

    @Test
    public void enum_constant() {
        assertThat(to(XEnum.class).timeUnit).isEqualTo(TimeUnit.MILLISECONDS);
    }
}
