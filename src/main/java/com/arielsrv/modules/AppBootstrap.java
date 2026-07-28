package com.arielsrv.modules;

import com.arielsrv.core.ConfigLoader;
import com.arielsrv.core.RestClient;
import com.arielsrv.providers.ObjectMapperProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.avaje.inject.BeanScope;
import io.avaje.inject.BeanScopeBuilder;

import java.util.Properties;

// Arma el BeanScope de Avaje. Los beans @Singleton (controllers, services, clients)
// los cablea el processor en tiempo de compilacion; aca solo registramos los beans
// que se crean dinamicamente en runtime (ObjectMapper, Properties y los RestClient
// con nombre) como beans externos ("supplied") del scope.
public final class AppBootstrap {

	private AppBootstrap() {
	}

	public static BeanScope createBeanScope() {
		return createBeanScope(ConfigLoader.load());
	}

	public static BeanScope createBeanScope(Properties config) {
		// Un unico ObjectMapper compartido: lo usa Javalin para serializar respuestas
		// y todos los RestClient para deserializar lo que vuelve de las APIs externas.
		ObjectMapper objectMapper = new ObjectMapperProvider().get();

		BeanScopeBuilder builder = BeanScope.builder()
			.bean(ObjectMapper.class, objectMapper)
			.bean(Properties.class, config);

		// Un RestClient por cada rest.client.<name>.base.url, registrado con ese nombre
		// para que Avaje lo inyecte en cada client via @Named(name).
		config.stringPropertyNames().stream()
			.filter(key -> key.startsWith("rest.client.") && key.endsWith(".base.url"))
			.forEach(key -> {
				String name = key.substring("rest.client.".length(),
					key.length() - ".base.url".length());
				String baseUrl = config.getProperty(key);
				builder.bean(name, RestClient.class, RestClient.createRestClient(baseUrl, objectMapper));
			});

		return builder.build();
	}
}