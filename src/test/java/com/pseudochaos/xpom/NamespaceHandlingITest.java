package com.pseudochaos.xpom;

import com.pseudochaos.xpom.annotation.Namespace;
import com.pseudochaos.xpom.annotation.NamespaceContext;
import com.pseudochaos.xpom.annotation.XPath;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class NamespaceHandlingITest {

    private static final String XML =
            "<ns1:user xmlns:ns1=\"com.pseudochaos.user\" xmlns:ns2=\"com.pseudochaos.user.name\">" +
                    "<ns2:name>Alex</ns2:name>" +
                    "<ns3:nick xmlns:ns3=\"com.pseudochaos.user.nick\">pseudochaos</ns3:nick>" +
            "</ns1:user>";

    @NamespaceContext({
            @Namespace(prefix = "ns1", uri = "com.pseudochaos.user"),
            @Namespace(prefix = "ns2", uri = "com.pseudochaos.user.name"),
            @Namespace(prefix = "ns3", uri = "com.pseudochaos.user.nick")
    })
    static class User {
        @XPath("/ns1:user/ns2:name/text()") String name;
        @XPath("/ns1:user/ns3:nick/text()") String nick;
    }

    @Test
    public void shouldReadNamespaceContextFromAnnotation() {
        XPom xpom = new XPom(User.class);
        javax.xml.namespace.NamespaceContext context = xpom.getNamespaceContext();
        assertThat(context.getNamespaceURI("ns1")).isEqualTo("com.pseudochaos.user");
        assertThat(context.getNamespaceURI("ns2")).isEqualTo("com.pseudochaos.user.name");
        assertThat(context.getNamespaceURI("ns3")).isEqualTo("com.pseudochaos.user.nick");
    }

    private static <T> T to(Class<T> clazz) {
        return XPomFactory.create(clazz).using(XML);
    }

    @Test
    public void shouldPerformMappingUsingNamespacesFromAnnotations() {
        User user = to(User.class);
        assertThat(user.name).isEqualTo("Alex");
        assertThat(user.nick).isEqualTo("pseudochaos");
    }
}
