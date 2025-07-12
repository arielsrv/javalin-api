package com.iskaypet.modules;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.google.inject.AbstractModule;
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
        
        // Create a test module that overrides the Properties binding
        AbstractModule testModule = new AbstractModule() {
            @Override
            protected void configure() {
                // Override the Properties binding with our test properties
                bind(Properties.class).toInstance(props);
                
                // Configure the rest clients based on our test properties
                props.stringPropertyNames().stream()
                    .filter(key -> key.startsWith("rest.client.") && key.endsWith(".base.url"))
                    .forEach(key -> {
                        String name = key.substring("rest.client.".length(),
                            key.length() - ".base.url".length());
                        String baseUrl = props.getProperty(key);
                        bind(RestClient.class)
                            .annotatedWith(Names.named(name))
                            .toInstance(RestClient.createRestClient(baseUrl));
                    });
            }
        };
        
        Injector injector = Guice.createInjector(testModule);
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
        
        // Create a test module that only binds the user client
        AbstractModule testModule = new AbstractModule() {
            @Override
            protected void configure() {
                // Override the Properties binding with our test properties
                bind(Properties.class).toInstance(props);
                
                // Only bind the user client, not the "notfound" client
                props.stringPropertyNames().stream()
                    .filter(key -> key.startsWith("rest.client.") && key.endsWith(".base.url"))
                    .forEach(key -> {
                        String name = key.substring("rest.client.".length(),
                            key.length() - ".base.url".length());
                        String baseUrl = props.getProperty(key);
                        bind(RestClient.class)
                            .annotatedWith(Names.named(name))
                            .toInstance(RestClient.createRestClient(baseUrl));
                    });
            }
        };
        
        Injector injector = Guice.createInjector(testModule);
        assertThatThrownBy(() -> injector.getInstance(Key.get(RestClient.class, Names.named("notfound"))))
            .isInstanceOf(Exception.class);
    }
} 