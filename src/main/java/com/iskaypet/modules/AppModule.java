package com.iskaypet.modules;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iskaypet.core.ConfigLoader;
import com.iskaypet.core.RestClient;
import com.iskaypet.providers.ObjectMapperProvider;
import dagger.Module;
import dagger.Provides;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Properties;

/**
 * Dagger module providing application-level dependencies.
 */
@Module
public class AppModule {

	/**
	 * Provides the singleton ObjectMapper.
	 *
	 * @return the ObjectMapper instance
	 */
	@Provides
	@Singleton
	ObjectMapper provideObjectMapper() {
		return new ObjectMapperProvider().get();
	}

	/**
	 * Provides the singleton configuration Properties.
	 *
	 * @return the configuration Properties
	 */
	@Provides
	@Singleton
	Properties provideConfig() {
		return ConfigLoader.load();
	}

	/**
	 * Provides the REST client for the user service.
	 *
	 * @param config the application configuration
	 * @return the user REST client
	 */
	@Provides
	@Singleton
	@Named("user")
	RestClient provideUserRestClient(Properties config) {
		String baseUrl = config.getProperty("rest.client.user.base.url");
		return RestClient.createRestClient(baseUrl);
	}

	/**
	 * Provides the REST client for the post service.
	 *
	 * @param config the application configuration
	 * @return the post REST client
	 */
	@Provides
	@Singleton
	@Named("post")
	RestClient providePostRestClient(Properties config) {
		String baseUrl = config.getProperty("rest.client.post.base.url");
		return RestClient.createRestClient(baseUrl);
	}

	/**
	 * Provides the REST client for the todo service.
	 *
	 * @param config the application configuration
	 * @return the todo REST client
	 */
	@Provides
	@Singleton
	@Named("todo")
	RestClient provideTodoRestClient(Properties config) {
		String baseUrl = config.getProperty("rest.client.todo.base.url");
		return RestClient.createRestClient(baseUrl);
	}
}
