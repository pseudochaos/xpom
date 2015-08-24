package com.pseudochaos.xpom;

import com.pseudochaos.xpom.annotation.XPath;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class XPathFunctionITest {

    private static final String XML =
            "<dataTypes>" +
                "<boolean>" +
                    "<true>true</true>" +
                    "<false>false</false>" +
                "</boolean>" +
            "</dataTypes>";

    private static <T> T to(Class<T> clazz) {
        return XPomFactory.create(clazz).using(XML);
    }

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
}
