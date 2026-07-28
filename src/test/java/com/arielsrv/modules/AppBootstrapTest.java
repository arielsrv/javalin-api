package com.arielsrv.modules;

import com.arielsrv.core.RestClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.avaje.inject.BeanScope;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AppBootstrapTest {

	// Los 4 clients @Singleton se instancian al construir el scope, asi que las 4
	// base.url tienen que estar presentes o el build falla.
	private static Properties baseProps() {
		Properties props = new Properties();
		props.setProperty("rest.client.user.base.url", "https://gorest.co.in");
		props.setProperty("rest.client.post.base.url", "https://gorest.co.in");
		props.setProperty("rest.client.todo.base.url", "https://gorest.co.in");
		props.setProperty("rest.client.comment.base.url", "https://gorest.co.in");
		return props;
	}

	@Test
	void binds_rest_clients_from_properties() {
		Properties props = baseProps();
		props.setProperty("rest.client.extra.base.url", "https://extra.com");

		try (BeanScope scope = AppBootstrap.createBeanScope(props)) {
			RestClient user = scope.get(RestClient.class, "user");
			RestClient extra = scope.get(RestClient.class, "extra");
			assertThat(user).isNotNull();
			assertThat(extra).isNotNull();
			assertThat(user).isNotSameAs(extra);
		}
	}

	@Test
	void ignores_keys_without_base_url_suffix() {
		Properties props = baseProps();
		props.setProperty("rest.client.invalid", "value"); // no .base.url -> filtrado (T && F)
		props.setProperty("other.property", "value");       // F && ...

		try (BeanScope scope = AppBootstrap.createBeanScope(props)) {
			assertThat(scope.get(RestClient.class, "user")).isNotNull();
			assertThatThrownBy(() -> scope.get(RestClient.class, "invalid"))
				.isInstanceOf(RuntimeException.class);
		}
	}

	@Test
	void supplies_shared_object_mapper_and_properties() {
		Properties props = baseProps();
		try (BeanScope scope = AppBootstrap.createBeanScope(props)) {
			assertThat(scope.get(ObjectMapper.class)).isNotNull();
			assertThat(scope.get(Properties.class)).isSameAs(props);
		}
	}
}
