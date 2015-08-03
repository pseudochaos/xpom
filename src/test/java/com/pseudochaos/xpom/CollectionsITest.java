package com.pseudochaos.xpom;

import com.pseudochaos.xpom.annotation.XPath;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * If you don't specify an underlying collection implementation, it will be chosen according to the following rules:
 *      Collection => ArrayList
 *      List       => ArrayList
 *      Set        => HashSet
 *      Queue      => LinkedList
 *
 * You can specify desired collection implementation in field declaration (not recommended):
 *      private LinkedList<String> values;
 *
 * Alternatively, you can define a field with an interface and initialize it with either empty or filled collection of
 * a desired type:
 *      private Set<String> values = new TreeSet<String>();
 *
 * You are allowed to initialize a field with some particular collection implementation.
 *
 */
public class CollectionsITest {

    private static final String XML =
            "<dataTypes>" +
                "<boolean>" +
                    "<true>true</true>" +
                    "<false>false</false>" +
                "</boolean>" +
                "<bin>0b11010</bin>" +
            "</dataTypes>";

    private static <T> T to(Class<T> clazz) {
        return XPom.map(XML).to(clazz);
    }

    // List

    static class StringList { @XPath("/dataTypes/boolean/*") List<String> values; }
    @Test
    public void collection_List_String() {
        assertThat(to(StringList.class).values).isInstanceOf(ArrayList.class).hasSize(2).contains("true", "false");
    }

    static class BooleanList { @XPath("/dataTypes/boolean/*") List<Boolean> values; }
    @Test
    public void collection_List_Boolean() {
        assertThat(to(BooleanList.class).values).isInstanceOf(ArrayList.class).hasSize(2).contains(true, false);
    }

    static class RawList { @XPath("/dataTypes/boolean/*") List values; }
    @Test
    public void collection_List_Raw() {
        assertThat(to(RawList.class).values).hasSize(2).contains("true", "false").isInstanceOf(ArrayList.class);
        // TODO: Submit a bug report
//        assertThat(to(RawList.class).values).isInstanceOf(ArrayList.class).hasSize(2).contains("true", "false");
    }
}
