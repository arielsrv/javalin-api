package com.iskaypet.core;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import io.reactivex.rxjava3.core.Observable;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class RestClient {

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
		return Observable.fromFuture(
			this.client.sendAsync(HttpRequest.newBuilder().uri(URI.create(uri)).GET().build(),
				HttpResponse.BodyHandlers.ofString()).thenApply(response -> {
				try {
					T data = objectMapper.readValue(response.body(), clazz);
					return new Response<>(response.statusCode(), data);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}));
	}
}

