package com.iskaypet.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CustomJacksonMapperTest {

	@Test
	void serializes_to_snake_case() {
		ObjectMapper om = new ObjectMapper();
		om.setPropertyNamingStrategy(
			com.fasterxml.jackson.databind.PropertyNamingStrategies.SNAKE_CASE);
		CustomJacksonMapper mapper = new CustomJacksonMapper(om);
		User user = new User();
		user.userId = 42L;
		user.name = "A";
		user.email = "a@b.com";
		String json = mapper.toJsonString(user, User.class);
		assertThat(json).contains("user_id").contains("A").contains("a@b.com");
	}

	@Test
	void deserializes_from_snake_case() {
		ObjectMapper om = new ObjectMapper();
		om.setPropertyNamingStrategy(
			com.fasterxml.jackson.databind.PropertyNamingStrategies.SNAKE_CASE);
		CustomJacksonMapper mapper = new CustomJacksonMapper(om);
		String json = "{\"user_id\":99,\"name\":\"B\",\"email\":\"b@b.com\"}";
		User user = mapper.fromJsonString(json, User.class);
		assertThat(user.userId).isEqualTo(99L);
		assertThat(user.name).isEqualTo("B");
		assertThat(user.email).isEqualTo("b@b.com");
	}

	@Test
	void serializes_nulls() {
		ObjectMapper om = new ObjectMapper();
		om.setPropertyNamingStrategy(
			com.fasterxml.jackson.databind.PropertyNamingStrategies.SNAKE_CASE);
		CustomJacksonMapper mapper = new CustomJacksonMapper(om);
		User user = new User();
		user.userId = null;
		user.name = null;
		user.email = "x@x.com";
		String json = mapper.toJsonString(user, User.class);
		assertThat(json).contains("x@x.com");
	}

	@Test
	void throws_on_invalid_json() {
		ObjectMapper om = new ObjectMapper();
		CustomJacksonMapper mapper = new CustomJacksonMapper(om);
		assertThatThrownBy(() -> mapper.fromJsonString("not-json", User.class))
			.isInstanceOf(RuntimeException.class);
	}

	@Test
	void serializes_and_deserializes_list() {
		ObjectMapper om = new ObjectMapper();
		CustomJacksonMapper mapper = new CustomJacksonMapper(om);
		List<User> users = List.of(new User());
		String json = mapper.toJsonString(users, List.class);
		List result = mapper.fromJsonString(json, List.class);
		assertThat(result).isInstanceOf(List.class);
	}

	static class User {

		public Long userId;
		public String name;
		public String email;
	}
}
