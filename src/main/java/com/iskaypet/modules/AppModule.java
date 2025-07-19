package com.iskaypet.modules;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.iskaypet.core.ConfigLoader;
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

		Properties config = ConfigLoader.load();

		config.stringPropertyNames().stream()
			.filter(key -> key.startsWith("rest.client.") && key.endsWith(".base.url"))
			.forEach(key -> {
				String name = key.substring("rest.client.".length(),
					key.length() - ".base.url".length());
				String baseUrl = config.getProperty(key);
				bind(RestClient.class)
					.annotatedWith(Names.named(name))
					.toInstance(RestClient.createRestClient(baseUrl));
			});
	}
}
