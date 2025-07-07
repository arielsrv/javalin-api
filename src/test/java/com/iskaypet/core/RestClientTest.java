package com.iskaypet.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RestClientTest {
    static class UserFake {
        public Long userId;
        public String name;
        public String email;
    }

    ObjectMapper objectMapper;
    RestClient restClient;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        objectMapper.registerModule(new ParameterNamesModule());
        objectMapper.registerModule(new Jdk8Module());
        objectMapper.registerModule(new JavaTimeModule());
        restClient = new RestClient(objectMapper);
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
} 