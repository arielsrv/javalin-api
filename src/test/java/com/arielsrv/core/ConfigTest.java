package com.arielsrv.core;

import io.avaje.inject.BeanScope;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ConfigTest {

	@BeforeEach
	void setup() {
		Properties props = new Properties();
		props.setProperty("foo", "bar");
		props.setProperty("num", "42");
		props.setProperty("rest.client.user.base.url", "https://gorest.co.in");
		props.setProperty("rest.client.foo.base.url", "https://foo.com");
		props.setProperty("rest.client.invalid", "value"); // Covers T && F
		BeanScope scope = mock(BeanScope.class);
		when(scope.get(Properties.class)).thenReturn(props);
		ContainerRegistry.setBeanScope(scope);
	}

	@Test
	void getStringValue_returns_value() {
		assertThat(Config.getStringValue("foo")).isEqualTo("bar");
	}

	@Test
	void getLongValue_returns_long() {
		assertThat(Config.getLongValue("num")).isEqualTo(42L);
	}

	@Test
	void getIntValue_returns_int() {
		assertThat(Config.getIntValue("num")).isEqualTo(42);
	}

	@Test
	void getRestClientNames_returns_names() {
		assertThat(Config.getRestClientNames()).containsExactlyInAnyOrder("user", "foo");
	}

	@Test
	void constructor_covers_default_constructor() {
		// Instantiate to cover the default constructor
		Config config = new Config();
		assertThat(config).isNotNull();
	}
}
