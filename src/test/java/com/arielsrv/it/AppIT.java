package com.arielsrv.it;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.Testcontainers;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Paths;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test de integracion: corre el jar sobre la imagen distroless real (misma base que
 * produccion), apunta los clients a un MockWebServer local (upstream determinista, sin
 * depender de gorest) y verifica que la app levante y sirva GET /users end-to-end.
 */
class AppIT {

	private static final String USERS_JSON =
		"[{\"id\":1,\"name\":\"Alice\",\"email\":\"alice@example.com\"}]";
	private static final String POSTS_JSON =
		"[{\"id\":10,\"title\":\"Post 1\"}]";
	private static final String TODOS_JSON =
		"[{\"id\":100,\"title\":\"Todo 1\",\"body\":\"Body 1\"}]";
	private static final String COMMENTS_JSON =
		"[{\"id\":1000,\"name\":\"Carol\",\"email\":\"carol@example.com\",\"body\":\"Nice post\"}]";

	private static MockWebServer upstream;
	private static GenericContainer<?> app;

	@BeforeAll
	static void startAll() throws IOException {
		upstream = new MockWebServer();
		upstream.setDispatcher(new Dispatcher() {
			@NotNull
			@Override
			public MockResponse dispatch(@NotNull RecordedRequest request) {
				String path = request.getPath() == null ? "" : request.getPath();
				String body;
				if (path.equals("/public/v2/users")) {
					body = USERS_JSON;
				} else if (path.endsWith("/posts")) {
					body = POSTS_JSON;
				} else if (path.endsWith("/todos")) {
					body = TODOS_JSON;
				} else if (path.endsWith("/comments")) {
					body = COMMENTS_JSON;
				} else {
					return new MockResponse().setResponseCode(404).setBody("[]");
				}
				return new MockResponse()
					.setResponseCode(200)
					.setHeader("Content-Type", "application/json")
					.setBody(body);
			}
		});
		upstream.start();

		int mockPort = upstream.getPort();
		// Hace visible el puerto del mock (en el host) para el contenedor.
		Testcontainers.exposeHostPorts(mockPort);
		String baseUrl = "http://host.testcontainers.internal:" + mockPort;

		app = new GenericContainer<>(
			DockerImageName.parse("gcr.io/distroless/java25-debian13:nonroot"))
			.withCopyFileToContainer(
				MountableFile.forHostPath(Paths.get("target", "app.jar")), "/app/app.jar")
			.withCreateContainerCmdModifier(cmd -> cmd.withEntrypoint("java", "-jar", "/app/app.jar"))
			.withEnv("ENV", "prod")
			.withEnv("REST_CLIENT_USER_BASE_URL", baseUrl)
			.withEnv("REST_CLIENT_POST_BASE_URL", baseUrl)
			.withEnv("REST_CLIENT_TODO_BASE_URL", baseUrl)
			.withEnv("REST_CLIENT_COMMENT_BASE_URL", baseUrl)
			.withExposedPorts(8081)
			.waitingFor(Wait.forHttp("/ping").forPort(8081).forStatusCode(200)
				.withStartupTimeout(Duration.ofMinutes(2)));
		app.start();
	}

	@AfterAll
	static void stopAll() throws IOException {
		if (app != null) {
			app.stop();
		}
		if (upstream != null) {
			upstream.shutdown();
		}
	}

	@Test
	void app_boots_on_distroless_and_serves_users() throws Exception {
		String url = "http://" + app.getHost() + ":" + app.getMappedPort(8081) + "/users";
		HttpResponse<String> response = HttpClient.newHttpClient().send(
			HttpRequest.newBuilder().uri(URI.create(url)).GET().build(),
			HttpResponse.BodyHandlers.ofString());

		assertThat(response.statusCode()).isEqualTo(200);
		assertThat(response.body())
			.contains("\"user_id\":1")
			.contains("Alice")
			.contains("\"title\":\"Post 1\"")
			.contains("Nice post");
	}
}
