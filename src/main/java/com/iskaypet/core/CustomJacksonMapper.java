package com.iskaypet.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.json.JsonMapper;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

/**
 * Custom implementation of Javalin's JsonMapper using Jackson.
 */
public class CustomJacksonMapper implements JsonMapper {

	private final ObjectMapper objectMapper;

	/**
	 * Creates a new CustomJacksonMapper with the specified ObjectMapper.
	 *
	 * @param objectMapper the Jackson ObjectMapper to use
	 */
	public CustomJacksonMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@NotNull
	@Override
	public String toJsonString(@NotNull Object obj, @NotNull Type type) {
		try {
			return objectMapper.writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@NotNull
	@Override
	public <T> T fromJsonString(@NotNull String json, @NotNull Type targetType) {
		try {
			return objectMapper.readValue(json, objectMapper.constructType(targetType));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
