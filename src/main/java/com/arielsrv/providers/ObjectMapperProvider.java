package com.arielsrv.providers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.inject.Provider;
import jakarta.inject.Singleton;

@Singleton
public class ObjectMapperProvider implements Provider<ObjectMapper> {

	@Override
	public ObjectMapper get() {
		return JsonMapper.builder()
			.addModule(new JavaTimeModule())
			.propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
			.defaultPropertyInclusion(JsonInclude.Value.ALL_NON_NULL)
			.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
			.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
			.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
			.build();
	}
}