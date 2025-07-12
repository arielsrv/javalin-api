package com.iskaypet.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RestClientFactoryTest {

    Injector injector;
    RestClientFactory factory;

    @BeforeEach
    void setup() {
        injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(RestClient.class).annotatedWith(Names.named("user")).toInstance(RestClient.createRestClient("https://gorest.co.in"));
                bind(RestClient.class).annotatedWith(Names.named("foo")).toInstance(RestClient.createRestClient("https://foo.com"));
            }
        });
        factory = new RestClientFactory(injector);
    }

    @Test
    void get_returns_client_by_name() {
        RestClient user = factory.get("user");
        RestClient foo = factory.get("foo");
        assertThat(user).isNotNull();
        assertThat(foo).isNotNull();
        assertThat(user).isNotSameAs(foo);
    }

    @Test
    void get_throws_if_name_not_bound() {
        assertThatThrownBy(() -> factory.get("notfound")).isInstanceOf(Exception.class);
    }
} 