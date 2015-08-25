package com.pseudochaos;

import org.junit.Test;

import java.util.NoSuchElementException;

import static com.pseudochaos.ObjectUtils.coalesce;
import static org.assertj.core.api.Assertions.assertThat;

public class ObjectUtilsTest {

    @Test
    public void shouldReturnFirstNonNullValue() {
        assertThat(coalesce(null, "value", null)).isEqualTo("value");
    }

    @Test(expected = NoSuchElementException.class)
    public void shouldThrowExceptionWhenNoNonNullValue() {
        coalesce(null, null);
    }
}