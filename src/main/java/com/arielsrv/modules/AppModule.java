package com.arielsrv.modules;

import com.arielsrv.core.ConfigLoader;
import com.arielsrv.core.RestClient;
import com.arielsrv.providers.ConfigProvider;
import com.arielsrv.providers.ObjectMapperProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import jakarta.inject.Singleton;

import java.util.Properties;

public class AppModule extends AbstractModule {

	@Override
	protected void configure() {
		// Un unico ObjectMapper compartido: lo usa Javalin para serializar respuestas
		// y todos los RestClient para deserializar lo que vuelve de las APIs externas.
		ObjectMapper objectMapper = new ObjectMapperProvider().get();
		bind(ObjectMapper.class).toInstance(objectMapper);
		bind(Properties.class).toProvider(ConfigProvider.class).in(Singleton.class);

		Properties config = ConfigLoader.load();

		config.stringPropertyNames().stream()
			.filter(key -> key.startsWith("rest.client.") && key.endsWith(".base.url"))
			.forEach(key -> {
				String name = key.substring("rest.client.".length(),
					key.length() - ".base.url".length());
				String baseUrl = config.getProperty(key);
				bind(RestClient.class)
					.annotatedWith(Names.named(name))
					.toInstance(RestClient.createRestClient(baseUrl, objectMapper));
			});
	}
}
