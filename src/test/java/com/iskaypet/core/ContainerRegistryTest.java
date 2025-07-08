package com.iskaypet.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class ContainerRegistryTest {

    @AfterEach
    void cleanup() {
        // Reset el injector para no afectar otros tests
        ContainerRegistry.setInjector(null);
    }

    @Test
    void set_and_get_injector_returns_instance() {
        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(String.class).toInstance("hello");
            }
        });
        ContainerRegistry.setInjector(injector);
        String value = ContainerRegistry.get(String.class);
        assertThat(value).isEqualTo("hello");
    }

    @Test
    void get_throws_if_injector_not_set() {
        ContainerRegistry.setInjector(null);
        assertThatThrownBy(() -> ContainerRegistry.get(String.class))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Injector not set");
    }
}
