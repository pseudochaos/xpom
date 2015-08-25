package com.pseudochaos;

import org.junit.Test;

import java.util.NoSuchElementException;

import static com.pseudochaos.ObjectUtils.firstNonNull;
import static org.assertj.core.api.Assertions.assertThat;

public class ObjectUtilsTest {

    @Test
    public void shouldReturnFirstNonNullValue() {
        assertThat(firstNonNull(null, "value", null)).isEqualTo("value");
    }

    @Test(expected = NoSuchElementException.class)
    public void shouldThrowExceptionWhenNoNonNullValue() {
        firstNonNull(null, null);
    }
}