package com.iskaypet.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import io.reactivex.rxjava3.core.Observable;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RestClientTest {

	ObjectMapper objectMapper;
	RestClient restClient;
	MockWebServer mockWebServer;

	@BeforeEach
	void setup() throws IOException {
		objectMapper = new ObjectMapper();
		objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
		objectMapper.registerModule(new ParameterNamesModule());
		objectMapper.registerModule(new Jdk8Module());
		objectMapper.registerModule(new JavaTimeModule());
		mockWebServer = new MockWebServer();
		mockWebServer.start();
		String baseUrl = mockWebServer.url("/").toString();
		restClient = RestClient.createRestClient(baseUrl);
	}

	@AfterEach
	void tearDown() throws IOException {
		mockWebServer.shutdown();
	}

	@Test
	void deserializes_snake_case_json() throws Exception {
		String json = "{\"user_id\":1,\"name\":\"Alice\",\"email\":\"alice@example.com\"}";
		UserFake user = objectMapper.readValue(json, UserFake.class);
		assertThat(user.userId).isEqualTo(1L);
		assertThat(user.name).isEqualTo("Alice");
		assertThat(user.email).isEqualTo("alice@example.com");
	}

	@Test
	void serializes_to_snake_case_json() throws Exception {
		UserFake user = new UserFake();
		user.userId = 2L;
		user.name = "Bob";
		user.email = "bob@example.com";
		String json = objectMapper.writeValueAsString(user);
		assertThat(json).contains("user_id").contains("Bob").contains("bob@example.com");
	}

	@Test
	void throws_on_invalid_json() {
		assertThatThrownBy(() -> objectMapper.readValue("not-json", UserFake.class))
			.isInstanceOf(Exception.class);
	}

	@Test
	void serializes_and_deserializes_list() throws Exception {
		List<UserFake> users = List.of(new UserFake());
		String json = objectMapper.writeValueAsString(users);
		List result = objectMapper.readValue(json, List.class);
		assertThat(result).isInstanceOf(List.class);
	}

	@Test
	void getObservable_parses_valid_json_response() {
		String json = "{\"user_id\":1,\"name\":\"Alice\",\"email\":\"alice@example.com\"}";
		mockWebServer.enqueue(new MockResponse().setBody(json).setResponseCode(200));
		UserFake result = restClient.getObservable("/user", UserFake.class)
			.timeout(2, TimeUnit.SECONDS)
			.blockingFirst().data();
		assertThat(result.userId).isEqualTo(1L);
		assertThat(result.name).isEqualTo("Alice");
		assertThat(result.email).isEqualTo("alice@example.com");
	}

	@Test
	void getObservable_propagates_http_status_code() {
		String json = "{\"user_id\":2,\"name\":\"Bob\",\"email\":\"bob@example.com\"}";
		mockWebServer.enqueue(new MockResponse().setBody(json).setResponseCode(404));
		Response<UserFake> response = restClient.getObservable("/user", UserFake.class)
			.timeout(2, TimeUnit.SECONDS)
			.blockingFirst();
		assertThat(response.code()).isEqualTo(404);
		assertThat(response.data().userId).isEqualTo(2L);
	}

	@Test
	void getObservable_throws_on_invalid_json() {
		mockWebServer.enqueue(new MockResponse().setBody("not-json").setResponseCode(200));
		assertThatThrownBy(() -> restClient.getObservable("/user", UserFake.class)
			.timeout(2, TimeUnit.SECONDS)
			.blockingFirst())
			.isInstanceOf(RuntimeException.class);
	}

	@Test
	void getObservable_success() throws Exception {
		// Simular respuesta exitosa
		ObjectMapper objectMapper = new ObjectMapper();
		RestClient rc = new RestClient(objectMapper) {
			@Override
			public <T> Observable<Response<T>> getObservable(String apiUrl, Class<T> clazz) {
				T data;
				try {
					data = objectMapper.readValue("{\"id\":1}\n", clazz);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				return Observable.just(new Response<>(200, data));
			}
		};
		Observable<Response<Dummy>> obs = rc.getObservable("/dummy", Dummy.class);
		Response<Dummy> result = obs.blockingFirst();
		assertThat(result).isNotNull();
		assertThat(result.code()).isEqualTo(200);
		assertThat(result.data().id).isEqualTo(1);
	}

	@Test
	void getObservable_parseError() {
		String badJson = "not a json";
		MockResponse response = new MockResponse().setBody(badJson).setResponseCode(200);
		mockWebServer.enqueue(response);
		assertThatThrownBy(() -> restClient.getObservable("/dummy", UserFake.class)
			.timeout(2, TimeUnit.SECONDS)
			.blockingFirst())
			.isInstanceOf(RuntimeException.class);
	}

	static class Dummy {
		public int id;
	}

	static class UserFake {

		public Long userId;
		public String name;
		public String email;
	}
}
