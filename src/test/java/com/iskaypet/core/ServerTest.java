package com.iskaypet.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.iskaypet.modules.AppModule;
import io.javalin.Javalin;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

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
		AtomicBoolean called = new AtomicBoolean(false);
		Server server = Server.create(cfg -> called.set(true));
		assertThat(called).isTrue();
		assertThat(server).isNotNull();
	}

	@Test
	void get_registers_endpoint() {
		Javalin javalin = mock(Javalin.class);
		Server server = new Server(javalin);
		server.get("/test", ctx -> null);
		verify(javalin).get(eq("/test"), any());
	}

	@Test
	void start_registersEndpoints() {
		Javalin mockJavalin = Mockito.mock(Javalin.class);
		Server server = new Server(mockJavalin);
		server.start(8081);
		Mockito.verify(mockJavalin, Mockito.atLeastOnce()).get(Mockito.anyString(), Mockito.any());
		Mockito.verify(mockJavalin).start(Mockito.anyString(), Mockito.eq(8081));
	}
}
