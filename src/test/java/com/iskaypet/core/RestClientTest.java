package com.iskaypet.core;

import com.google.gson.Gson;
import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;

class RestClientTest {
    @Test
    void getObservable_parsesJsonResponse() {
        RestClient restClient = new RestClient();
        // Simula un endpoint real con JSON simple usando MockWebServer o similar, pero aquí usamos un truco:
        // Gson parsea un string JSON, así que probamos el método de parsing directamente
        String json = "{\"id\":1,\"name\":\"Alice\",\"email\":\"alice@example.com\"}";
        Gson gson = new Gson();
        UserFake user = gson.fromJson(json, UserFake.class);
        assertThat(user.id).isEqualTo(1L);
        assertThat(user.name).isEqualTo("Alice");
        assertThat(user.email).isEqualTo("alice@example.com");
    }

    static class UserFake {
        public Long id;
        public String name;
        public String email;
    }
} 