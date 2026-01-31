package com.arielsrv;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.arielsrv.core.ContainerRegistry;
import com.arielsrv.core.Server;
import com.arielsrv.modules.AppModule;
import io.javalin.Javalin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class MainTest {

	@BeforeEach
	void setup() {
		Injector injector = Guice.createInjector(new AppModule());
		ContainerRegistry.setInjector(injector);
	}

	@Test
	void main_smoke_test() throws Exception {
		// Verify that the class is loaded and the entry point exists
		Class<?> mainClass = Class.forName("com.arielsrv.Main");
		Method mainMethod = mainClass.getDeclaredMethod("main", String[].class);
		assertThat(mainMethod).isNotNull();
		assertThat(mainMethod.getParameterCount()).isEqualTo(1);
	}

	@Test
	void main_flow_creates_server_and_registers_routes() {
		// Simulate the Main.main() flow without starting the actual server
		Server server = Server.create();
		assertThat(server).isNotNull();
		assertThat(server.javalin()).isNotNull();

		// Verify that we can register endpoints as main does
		Javalin mockJavalin = mock(Javalin.class);
		Server mockServer = new Server(mockJavalin);
		mockServer.get("/users", ctx -> io.reactivex.rxjava3.core.Observable.empty());
		verify(mockJavalin).get(eq("/users"), any());
	}
}
