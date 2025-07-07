package com.iskaypet.modules;

import com.google.inject.AbstractModule;
import jakarta.inject.Singleton;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iskaypet.providers.ObjectMapperProvider;

public class AppModule extends AbstractModule {

    @Override
    protected void configure() {
        // Puedes agregar bindings personalizados aquí si lo necesitas
        bind(ObjectMapper.class).toProvider(ObjectMapperProvider.class).in(Singleton.class);
    }
}
