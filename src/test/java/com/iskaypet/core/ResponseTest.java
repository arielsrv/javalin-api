package com.iskaypet.core;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ResponseTest {

	@Test
	void constructor_and_getters_work() {
		Response<String> response = new Response<>(200, "ok");
		assertThat(response.code()).isEqualTo(200);
		assertThat(response.data()).isEqualTo("ok");
	}

	@Test
	void toString_includes_code_and_data() {
		Response<String> response = new Response<>(404, "not found");
		String str = response.toString();
		assertThat(str).contains("404").contains("not found");
	}

	@Test
	void allows_null_data() {
		Response<String> response = new Response<>(500, null);
		assertThat(response.data()).isNull();
		assertThat(response.toString()).contains("data=null");
	}

	@Test
	void allows_negative_code() {
		Response<String> response = new Response<>(-1, "error");
		assertThat(response.code()).isEqualTo(-1);
	}

	@Test
	void supports_generic_types() {
		Response<java.util.List<String>> response = new Response<>(201,
			java.util.List.of("a", "b"));
		assertThat(response.code()).isEqualTo(201);
		assertThat(response.data()).containsExactly("a", "b");
	}

	@Test
	void response_fieldsAndToString() {
		Response<String> r = new Response<>(200, "ok");
		assertThat(r.code()).isEqualTo(200);
		assertThat(r.data()).isEqualTo("ok");
		assertThat(r.toString()).contains("code=200").contains("data=ok");
	}
}
