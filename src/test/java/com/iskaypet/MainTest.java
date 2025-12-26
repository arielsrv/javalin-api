package com.iskaypet;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.iskaypet.core.ContainerRegistry;
import com.iskaypet.core.Server;
import com.iskaypet.modules.AppModule;
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
		// Verificar que la clase se carga y el punto de entrada existe
		Class<?> mainClass = Class.forName("com.iskaypet.Main");
		Method mainMethod = mainClass.getDeclaredMethod("main", String[].class);
		assertThat(mainMethod).isNotNull();
		assertThat(mainMethod.getParameterCount()).isEqualTo(1);
	}

	@Test
	void main_flow_creates_server_and_registers_routes() {
		// Simular el flujo de Main.main() sin arrancar el servidor real
		Server server = Server.create();
		assertThat(server).isNotNull();
		assertThat(server.javalin()).isNotNull();

		// Verificar que podemos registrar endpoints como lo hace main
		Javalin mockJavalin = mock(Javalin.class);
		Server mockServer = new Server(mockJavalin);
		mockServer.get("/users", ctx -> io.reactivex.rxjava3.core.Observable.empty());
		verify(mockJavalin).get(eq("/users"), any());
	}
}
