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

@Module
public class AppModule {

	@Provides
	@Singleton
	ObjectMapper provideObjectMapper() {
		return new ObjectMapperProvider().get();
	}

	@Provides
	@Singleton
	Properties provideConfig() {
		return ConfigLoader.load();
	}

	@Provides
	@Singleton
	@Named("user")
	RestClient provideUserRestClient(Properties config) {
		String baseUrl = config.getProperty("rest.client.user.base.url");
		return RestClient.createRestClient(baseUrl);
	}

	@Provides
	@Singleton
	@Named("post")
	RestClient providePostRestClient(Properties config) {
		String baseUrl = config.getProperty("rest.client.post.base.url");
		return RestClient.createRestClient(baseUrl);
	}

	@Provides
	@Singleton
	@Named("todo")
	RestClient provideTodoRestClient(Properties config) {
		String baseUrl = config.getProperty("rest.client.todo.base.url");
		return RestClient.createRestClient(baseUrl);
	}
}
