package com.iskaypet.core;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.iskaypet.modules.AppModule;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

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

	@Test
	void start_registers_metrics_endpoint() {
		Javalin mockJavalin = Mockito.mock(Javalin.class);
		Server server = new Server(mockJavalin);
		server.start(8082);
		Mockito.verify(mockJavalin).get(eq("/metrics"), any());
	}

	@Test
	void start_registers_ping_endpoint() {
		Javalin mockJavalin = Mockito.mock(Javalin.class);
		Server server = new Server(mockJavalin);
		server.start(8083);
		Mockito.verify(mockJavalin).get(eq("/ping"), any());
	}

	@Test
	void javalin_accessor_returns_instance() {
		Javalin mockJavalin = Mockito.mock(Javalin.class);
		Server server = new Server(mockJavalin);
		assertThat(server.javalin()).isEqualTo(mockJavalin);
	}

	@Test
	void get_with_observable_handler() {
		Javalin javalin = mock(Javalin.class);
		Server server = new Server(javalin);

		server.get("/observable-test", ctx -> Observable.just("test"));
		verify(javalin).get(eq("/observable-test"), any());
	}

	@Test
	void create_initializes_prometheus_metrics() {
		// Verificar que create() configura las métricas de Prometheus
		Server server = Server.create();
		assertThat(server).isNotNull();
		// Las métricas se registran como side effect, verificamos que no hay errores
		assertThat(server.javalin()).isNotNull();
	}

	@Test
	void start_metrics_handler_executes_correctly() throws Exception {
		Javalin mockJavalin = Mockito.mock(Javalin.class);
		ArgumentCaptor<Handler> handlerCaptor = ArgumentCaptor.forClass(Handler.class);
		Server server = new Server(mockJavalin);

		server.start(8084);

		verify(mockJavalin, atLeast(2)).get(anyString(), handlerCaptor.capture());

		// Find and execute the metrics handler
		for (Handler handler : handlerCaptor.getAllValues()) {
			Context ctx = mock(Context.class);
			when(ctx.contentType(anyString())).thenReturn(ctx);
			handler.handle(ctx);
		}
	}

	@Test
	void start_ping_handler_returns_pong() throws Exception {
		Javalin mockJavalin = Mockito.mock(Javalin.class);
		ArgumentCaptor<String> pathCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<Handler> handlerCaptor = ArgumentCaptor.forClass(Handler.class);
		Server server = new Server(mockJavalin);

		server.start(8085);

		verify(mockJavalin, atLeast(2)).get(pathCaptor.capture(), handlerCaptor.capture());

		// Find and execute the ping handler
		var paths = pathCaptor.getAllValues();
		var handlers = handlerCaptor.getAllValues();
		for (int i = 0; i < paths.size(); i++) {
			if ("/ping".equals(paths.get(i))) {
				Context ctx = mock(Context.class);
				when(ctx.contentType(anyString())).thenReturn(ctx);
				when(ctx.result(anyString())).thenReturn(ctx);
				handlers.get(i).handle(ctx);
				verify(ctx).result("pong");
			}
		}
	}

	@Test
	void start_metrics_handler_returns_prometheus_scrape() throws Exception {
		Javalin mockJavalin = Mockito.mock(Javalin.class);
		ArgumentCaptor<String> pathCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<Handler> handlerCaptor = ArgumentCaptor.forClass(Handler.class);
		Server server = new Server(mockJavalin);

		server.start(8086);

		verify(mockJavalin, atLeast(2)).get(pathCaptor.capture(), handlerCaptor.capture());

		// Find and execute the metrics handler
		var paths = pathCaptor.getAllValues();
		var handlers = handlerCaptor.getAllValues();
		for (int i = 0; i < paths.size(); i++) {
			if ("/metrics".equals(paths.get(i))) {
				Context ctx = mock(Context.class);
				when(ctx.contentType(anyString())).thenReturn(ctx);
				when(ctx.result(anyString())).thenReturn(ctx);
				handlers.get(i).handle(ctx);
				verify(ctx).contentType("text/plain; version=0.0.4; charset=utf-8");
				verify(ctx).result(anyString());
			}
		}
	}
}
