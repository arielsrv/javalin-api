package com.iskaypet.core;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ConfigTest {

	@Test
	void getStringValue_returns_value() {
		// Config now uses ConfigLoader directly, so we test against actual config
		String appPort = Config.getStringValue("app.port");
		assertThat(appPort).isNotNull();
	}

	@Test
	void getIntValue_returns_int() {
		Integer port = Config.getIntValue("app.port");
		assertThat(port).isNotNull();
		assertThat(port).isGreaterThan(0);
	}

	@Test
	void getRestClientNames_returns_names() {
		Set<String> names = Config.getRestClientNames();
		assertThat(names).isNotEmpty();
		assertThat(names).contains("user");
	}

	@Test
	void constructor_covers_default_constructor() {
		Config config = new Config();
		assertThat(config).isNotNull();
	}
}
