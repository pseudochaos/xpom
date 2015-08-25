package com.pseudochaos.xpom;

import com.pseudochaos.xpom.annotation.XPath;
import com.pseudochaos.xpom.annotation.ExceptionHandlingStrategy;
import org.junit.Test;

import static com.pseudochaos.xpom.ExceptionHandling.FAIL;
import static com.pseudochaos.xpom.ExceptionHandling.USE_DEFAULT;
import static org.assertj.core.api.StrictAssertions.assertThat;

public class ConfigurationTest {

    @ExceptionHandlingStrategy(USE_DEFAULT)
    static class UseDefaultClassLevelStrategy {
        @XPath("/dummy") private String withoutStrategy;

        @ExceptionHandlingStrategy(FAIL)
        @XPath("/dummy") private String withFailStrategy;
    }

    static class NoClassLevelStrategy {
        @XPath("/dummy") private String withoutStrategy;

        @ExceptionHandlingStrategy(USE_DEFAULT)
        @XPath("/dummy") private String withDefaultStrategy;
    }

    @Test
    public void shouldReadExceptionHandlingStrategyFromAnnotationOnClass() {
        Configuration configuration = new Configuration(UseDefaultClassLevelStrategy.class);
        assertThat(configuration.getClassExceptionHandlingStrategy()).isEqualTo(USE_DEFAULT);
    }

    @Test
    public void shouldReturnNullWhenNoStrategyDefinedOnClass() {
        Configuration configuration = new Configuration(NoClassLevelStrategy.class);
        assertThat(configuration.getClassExceptionHandlingStrategy()).isNull();
    }

    @Test
    public void shouldReturnDefaultStrategyDefinedOnXPomFactoryWhenNoOverridesOnOtherLevels() throws Exception {
        Configuration configuration = new Configuration(NoClassLevelStrategy.class);
        XField field = xFieldFor("withoutStrategy", NoClassLevelStrategy.class);
        assertThat(configuration.getExceptionHandlingStrategy(field)).isEqualTo(FAIL);
    }

    private XField xFieldFor(String fieldName, Class clazz) throws Exception {
        return new XField(clazz.getDeclaredField(fieldName), new XNamespaceContext(clazz));
    }

    @Test
    public void shouldReturnStrategyOverriddenOnXPomFactoryLevelWhenNoOverridesOnOtherLevels() throws Exception {
        XPomFactory.setExceptionHandlingStrategy(USE_DEFAULT);
        Configuration configuration = new Configuration(NoClassLevelStrategy.class);
        XField field = xFieldFor("withoutStrategy", NoClassLevelStrategy.class);
        assertThat(configuration.getExceptionHandlingStrategy(field)).isEqualTo(USE_DEFAULT);
    }

    @Test
    public void shouldReturnStrategyDefinedOnClassLevelWhenNoOverridesOnFieldLevel() throws Exception {
        XPomFactory.setExceptionHandlingStrategy(FAIL);
        Configuration configuration = new Configuration(UseDefaultClassLevelStrategy.class);
        XField field = xFieldFor("withoutStrategy", UseDefaultClassLevelStrategy.class);
        assertThat(configuration.getExceptionHandlingStrategy(field)).isEqualTo(USE_DEFAULT);
    }

    @Test
    public void shouldReturnStrategyDefinedOnFieldLevel() throws Exception {
        XPomFactory.setExceptionHandlingStrategy(USE_DEFAULT);
        Configuration configuration = new Configuration(UseDefaultClassLevelStrategy.class);
        XField field = xFieldFor("withFailStrategy", UseDefaultClassLevelStrategy.class);
        assertThat(configuration.getExceptionHandlingStrategy(field)).isEqualTo(FAIL);
    }


}