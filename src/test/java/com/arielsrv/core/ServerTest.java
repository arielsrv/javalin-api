package com.arielsrv.core;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.arielsrv.modules.AppModule;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HandlerType;
import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ServerTest {

	@BeforeEach
	void setup() {
		Injector injector = Guice.createInjector(new AppModule());
		ContainerRegistry.setInjector(injector);
	}

	@Test
	void create_returns_server_instance() {
		Server server = Server.create();
		assertThat(server).isNotNull();
	}

	@Test
	void create_with_config_applies_config() {
		AtomicBoolean called = new AtomicBoolean(false);
		Server server = Server.create(cfg -> called.set(true));
		assertThat(called).isTrue();
		assertThat(server).isNotNull();
	}

	@Test
	void get_registers_endpoint() {
		Server server = Server.create(cfg -> {});
		server.get("/test", ctx -> null);
		assertThat(server.javalin().unsafe.internalRouter.hasHttpHandlerEntry(HandlerType.GET, "/test")).isTrue();
	}

	@Test
	void start_registersEndpoints() {
		Server server = createSpyServer();
		server.start(8081);
		var router = server.javalin().unsafe.internalRouter;
		assertThat(router.hasHttpHandlerEntry(HandlerType.GET, "/metrics")).isTrue();
		assertThat(router.hasHttpHandlerEntry(HandlerType.GET, "/ping")).isTrue();
	}

	@Test
	void start_registers_metrics_endpoint() {
		Server server = createSpyServer();
		server.start(8082);
		assertThat(server.javalin().unsafe.internalRouter.hasHttpHandlerEntry(HandlerType.GET, "/metrics")).isTrue();
	}

	@Test
	void start_registers_ping_endpoint() {
		Server server = createSpyServer();
		server.start(8083);
		assertThat(server.javalin().unsafe.internalRouter.hasHttpHandlerEntry(HandlerType.GET, "/ping")).isTrue();
	}

	@Test
	void javalin_accessor_returns_instance() {
		Javalin mockJavalin = mock(Javalin.class);
		Server server = new Server(mockJavalin);
		assertThat(server.javalin()).isEqualTo(mockJavalin);
	}

	@Test
	void get_with_observable_handler() {
		Server server = Server.create(cfg -> {});
		server.get("/observable-test", ctx -> Observable.just("test"));
		assertThat(server.javalin().unsafe.internalRouter.hasHttpHandlerEntry(HandlerType.GET, "/observable-test")).isTrue();
	}

	@Test
	void create_initializes_prometheus_metrics() {
		Server server = Server.create();
		assertThat(server).isNotNull();
		assertThat(server.javalin()).isNotNull();
	}

	@Test
	void start_metrics_handler_executes_correctly() throws Exception {
		Server server = createSpyServer();
		server.start(8084);

		var handler = Objects.requireNonNull(server.javalin().unsafe.internalRouter
			.findFirstHttpHandlerEntry(HandlerType.GET, "/metrics"))
			.endpoint.handler;

		Context ctx = mock(Context.class);
		when(ctx.contentType(anyString())).thenReturn(ctx);
		handler.handle(ctx);
		verify(ctx, atLeastOnce()).contentType(anyString());
	}

	@Test
	void start_ping_handler_returns_pong() throws Exception {
		Server server = createSpyServer();
		server.start(8085);

		var handler = Objects.requireNonNull(server.javalin().unsafe.internalRouter
			.findFirstHttpHandlerEntry(HandlerType.GET, "/ping"))
			.endpoint.handler;

		Context ctx = mock(Context.class);
		when(ctx.contentType(anyString())).thenReturn(ctx);
		when(ctx.result(anyString())).thenReturn(ctx);
		handler.handle(ctx);
		verify(ctx).result("pong");
	}

	@Test
	void start_metrics_handler_returns_prometheus_scrape() throws Exception {
		Server server = createSpyServer();
		server.start(8086);

		var handler = server.javalin().unsafe.internalRouter
			.findFirstHttpHandlerEntry(HandlerType.GET, "/metrics")
			.endpoint.handler;

		Context ctx = mock(Context.class);
		when(ctx.contentType(anyString())).thenReturn(ctx);
		when(ctx.result(anyString())).thenReturn(ctx);
		handler.handle(ctx);
		verify(ctx).contentType("text/plain; version=0.0.4; charset=utf-8");
		verify(ctx).result(anyString());
	}

	@Test
	void record_methods_coverage() {
		Javalin javalin1 = mock(Javalin.class);
		Javalin javalin2 = mock(Javalin.class);
		Server server1 = new Server(javalin1);
		Server server2 = new Server(javalin1);
		Server server3 = new Server(javalin2);

		assertThat(server1).isEqualTo(server2);
		assertThat(server1).isNotEqualTo(server3);
		assertThat(server1.hashCode()).isEqualTo(server2.hashCode());
		assertThat(server1.toString()).contains("Server");
		assertThat(server1.javalin()).isSameAs(javalin1);
	}

	@Test
	void create_methods_coverage() {
		Server server1 = Server.create();
		assertThat(server1).isNotNull();
		server1.javalin().stop();

		Server server2 = Server.create(config -> config.startup.showJavalinBanner = false);
		assertThat(server2).isNotNull();
		server2.javalin().stop();
	}

	/**
	 * Creates a Server backed by a real Javalin instance (so unsafe is properly initialized),
	 * but with start(String, int) stubbed to avoid binding a real port.
	 */
	private Server createSpyServer() {
		Javalin realJavalin = Javalin.create();
		Javalin spyJavalin = spy(realJavalin);
		doReturn(spyJavalin).when(spyJavalin).start(anyString(), anyInt());
		return new Server(spyJavalin);
	}
}
