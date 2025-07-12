package com.iskaypet.modules;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import com.iskaypet.core.RestClient;
import java.util.Properties;
import org.junit.jupiter.api.Test;

class AppModuleTest {

    @Test
    void binds_rest_clients_from_properties() {
        Properties props = new Properties();
        props.setProperty("rest.client.user.base.url", "https://gorest.co.in");
        props.setProperty("rest.client.foo.base.url", "https://foo.com");
        Injector injector = Guice.createInjector(new AppModule() {
            @Override
            protected void configure() {
                bind(Properties.class).toInstance(props);
                // Lógica de bindings dinámica ya está en AppModule
                super.configure();
            }
        });
        RestClient user = injector.getInstance(Key.get(RestClient.class, Names.named("user")));
        RestClient foo = injector.getInstance(Key.get(RestClient.class, Names.named("foo")));
        assertThat(user).isNotNull();
        assertThat(foo).isNotNull();
        assertThat(user).isNotSameAs(foo);
    }

    @Test
    void throws_if_client_not_bound() {
        Properties props = new Properties();
        props.setProperty("rest.client.user.base.url", "https://gorest.co.in");
        Injector injector = Guice.createInjector(new AppModule() {
            @Override
            protected void configure() {
                bind(Properties.class).toInstance(props);
                super.configure();
            }
        });
        assertThatThrownBy(() -> injector.getInstance(Key.get(RestClient.class, Names.named("notfound"))))
            .isInstanceOf(Exception.class);
    }
} 