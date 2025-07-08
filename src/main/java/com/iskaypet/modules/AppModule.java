package com.iskaypet.modules;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.iskaypet.core.RestClient;
import com.iskaypet.providers.ConfigProvider;
import com.iskaypet.providers.ObjectMapperProvider;
import jakarta.inject.Singleton;
import java.util.Properties;

public class AppModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ObjectMapper.class).toProvider(ObjectMapperProvider.class).in(Singleton.class);
        bind(Properties.class).toProvider(ConfigProvider.class).in(Singleton.class);

        bind(RestClient.class)
            .annotatedWith(Names.named("user"))
            .toInstance(new RestClient("https://gorest.co.in"));
    }
}
