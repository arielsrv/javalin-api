package com.arielsrv.core;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import io.reactivex.rxjava3.core.Observable;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class RestClient {

	private static final Tracer TRACER = GlobalOpenTelemetry.getTracer("com.arielsrv.core.RestClient");

	private final HttpClient client = HttpClient.newHttpClient();
	private final ObjectMapper objectMapper;
	private String baseUrl;

	private RestClient(String baseUrl) {
		objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
		objectMapper.registerModule(new ParameterNamesModule());
		objectMapper.registerModule(new Jdk8Module());
		objectMapper.registerModule(new JavaTimeModule());
		this.baseUrl = baseUrl;
	}

	public RestClient(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	public static RestClient createRestClient(String baseUrl) {
		return new RestClient(baseUrl);
	}

	public <T> Observable<Response<T>> getObservable(String apiUrl, Class<T> clazz) {
		String uri = "%s%s".formatted(this.baseUrl, apiUrl);
		// Nombre de span de baja cardinalidad: los IDs numéricos del path se
		// reemplazan por {id} (ej: "GET /public/v2/users/{id}/posts"), para no
		// generar un span distinto por cada usuario.
		String spanName = "GET " + apiUrl.replaceAll("/\\d+", "/{id}");
		// defer: el span y el sendAsync se crean en la suscripción (no al construir
		// el Observable), así cada reintento/suscripción abre su propio span y el
		// paralelismo de concatMapEager se mantiene.
		return Observable.defer(() -> {
			Span span = TRACER.spanBuilder(spanName).setSpanKind(SpanKind.CLIENT).startSpan();
			// makeCurrent mientras se dispara sendAsync: así el span HTTP crudo del
			// agente (el "GET") queda anidado debajo de este span lógico.
			try (Scope ignored = span.makeCurrent()) {
				var future = this.client.sendAsync(
					HttpRequest.newBuilder().uri(URI.create(uri)).GET().build(),
					HttpResponse.BodyHandlers.ofString()).thenApply(response -> {
					try {
						T data = objectMapper.readValue(response.body(), clazz);
						return new Response<>(response.statusCode(), data);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				});
				// fromCompletionStage es no-bloqueante (no hace future.get()), lo que
				// permite que concatMapEager dispare las llamadas realmente en paralelo.
				return Observable.fromCompletionStage(future)
					.doOnError(error -> {
						span.setStatus(StatusCode.ERROR, error.getMessage());
						span.recordException(error);
					})
					.doFinally(span::end);
			}
		});
	}
}

