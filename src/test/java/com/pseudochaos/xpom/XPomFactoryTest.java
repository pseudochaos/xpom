package com.pseudochaos.xpom;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class XPomFactoryTest {

    @Test
    public void shouldCreateInstanceOfXPomMapper() {
        XPom<XPomFactoryTest> mapper = XPomFactory.create(XPomFactoryTest.class);
        assertThat(mapper).isNotNull();
    }

    @Test
    public void shouldReturnTheSameInstanceOfXPomMapperIfCreatedBefore() {
        XPom<XPomFactoryTest> mapper1 = XPomFactory.create(XPomFactoryTest.class);
        XPom<XPomFactoryTest> mapper2 = XPomFactory.create(XPomFactoryTest.class);
        assertThat(mapper1).isEqualTo(mapper2);
    }
}