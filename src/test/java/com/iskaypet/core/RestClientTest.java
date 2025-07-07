package com.iskaypet.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RestClientTest {

    static class UserFake {

        public Long userId;
        public String name;
        public String email;
    }

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
        restClient = new RestClient(objectMapper);
        mockWebServer = new MockWebServer();
        mockWebServer.start();
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
        String url = mockWebServer.url("/user").toString();
        UserFake result = restClient.getObservable(url, UserFake.class)
            .timeout(2, TimeUnit.SECONDS)
            .blockingFirst().getData();
        assertThat(result.userId).isEqualTo(1L);
        assertThat(result.name).isEqualTo("Alice");
        assertThat(result.email).isEqualTo("alice@example.com");
    }

    @Test
    void getObservable_propagates_http_status_code() {
        String json = "{\"user_id\":2,\"name\":\"Bob\",\"email\":\"bob@example.com\"}";
        mockWebServer.enqueue(new MockResponse().setBody(json).setResponseCode(404));
        String url = mockWebServer.url("/user").toString();
        Response<UserFake> response = restClient.getObservable(url, UserFake.class)
            .timeout(2, TimeUnit.SECONDS)
            .blockingFirst();
        assertThat(response.getCode()).isEqualTo(404);
        assertThat(response.getData().userId).isEqualTo(2L);
    }

    @Test
    void getObservable_throws_on_invalid_json() {
        mockWebServer.enqueue(new MockResponse().setBody("not-json").setResponseCode(200));
        String url = mockWebServer.url("/user").toString();
        assertThatThrownBy(() -> restClient.getObservable(url, UserFake.class)
            .timeout(2, TimeUnit.SECONDS)
            .blockingFirst())
            .isInstanceOf(RuntimeException.class);
    }
}
