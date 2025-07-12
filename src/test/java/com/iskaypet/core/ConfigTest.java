package com.iskaypet.core;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import java.util.Properties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ConfigTest {

    @BeforeEach
    void setup() {
        Properties props = new Properties();
        props.setProperty("foo", "bar");
        props.setProperty("num", "42");
        props.setProperty("rest.client.user.base.url", "https://gorest.co.in");
        props.setProperty("rest.client.foo.base.url", "https://foo.com");
        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(Properties.class).toInstance(props);
            }
        });
        ContainerRegistry.setInjector(injector);
    }

    @Test
    void getStringValue_returns_value() {
        assertThat(Config.getStringValue("foo")).isEqualTo("bar");
    }

    @Test
    void getLongValue_returns_long() {
        assertThat(Config.getLongValue("num")).isEqualTo(42L);
    }

    @Test
    void getIntValue_returns_int() {
        assertThat(Config.getIntValue("num")).isEqualTo(42);
    }

    @Test
    void getRestClientNames_returns_names() {
        assertThat(Config.getRestClientNames()).containsExactlyInAnyOrder("user", "foo");
    }
} 