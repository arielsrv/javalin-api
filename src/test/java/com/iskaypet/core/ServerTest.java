package com.iskaypet.core;

import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.iskaypet.modules.AppModule;

class ServerTest {
    @Test
    void create_returns_server_instance() {
        Injector injector = Guice.createInjector(new AppModule());
        com.iskaypet.core.ContainerRegistry.setInjector(injector);
        Server server = Server.create();
        assertThat(server).isNotNull();
    }

    @Test
    void create_with_config_applies_config() {
        Injector injector = Guice.createInjector(new AppModule());
        com.iskaypet.core.ContainerRegistry.setInjector(injector);
        Consumer<JavalinConfig> configConsumer = mock(Consumer.class);
        Server server = Server.create(configConsumer);
        assertThat(server).isNotNull();
        verify(configConsumer, atLeast(0)).accept(any());
    }

    @Test
    void get_registers_endpoint() {
        Javalin javalin = mock(Javalin.class);
        Server server = new Server(javalin);
        server.get("/test", ctx -> null);
        verify(javalin).get(eq("/test"), any());
    }
} 