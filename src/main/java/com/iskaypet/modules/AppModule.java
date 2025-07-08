package com.iskaypet.modules;

import com.google.inject.AbstractModule;
import jakarta.inject.Singleton;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iskaypet.providers.ObjectMapperProvider;
import java.util.Properties;
import com.iskaypet.providers.ConfigProvider;

public class AppModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ObjectMapper.class).toProvider(ObjectMapperProvider.class).in(Singleton.class);
        bind(Properties.class).toProvider(ConfigProvider.class).in(Singleton.class);
    }
}
