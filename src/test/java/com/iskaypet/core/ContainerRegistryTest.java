package com.iskaypet.core;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ContainerRegistryTest {

    static class Sample {}

    private static void resetInjector() {
        try {
            java.lang.reflect.Field f = ContainerRegistry.class.getDeclaredField("injector");
            f.setAccessible(true);
            f.set(null, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void get_throws_if_injector_not_set() {
        resetInjector();
        assertThatThrownBy(() -> ContainerRegistry.get(Sample.class))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Injector not set");
    }

    @Test
    void get_returns_instance_from_injector() {
        resetInjector();
        Injector injector = Guice.createInjector(binder -> binder.bind(Sample.class).toInstance(new Sample()));
        ContainerRegistry.setInjector(injector);
        Sample s = ContainerRegistry.get(Sample.class);
        assertThat(s).isNotNull();
    }

    @Test
    void constructor_can_be_instantiated() {
        // Test to cover default constructor
        ContainerRegistry registry = new ContainerRegistry();
        assertThat(registry).isNotNull();
    }
}
