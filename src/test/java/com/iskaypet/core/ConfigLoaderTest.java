package com.iskaypet.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(SystemStubsExtension.class)
class ConfigLoaderTest {

	@SystemStub
	private EnvironmentVariables environmentVariables;

	@Test
	void load_returns_properties_for_local() {
		// When ENV is not set, defaults to "local"
		Properties props = ConfigLoader.load();
		assertThat(props).isNotNull();
		assertThat(props.stringPropertyNames()).isNotEmpty();
	}

	@Test
	void load_returns_properties_for_dev_environment() {
		environmentVariables.set("ENV", "dev");
		Properties props = ConfigLoader.load();
		assertThat(props).isNotNull();
		assertThat(props.stringPropertyNames()).isNotEmpty();
	}

	@Test
	void load_returns_properties_for_prod_environment() {
		environmentVariables.set("ENV", "prod");
		Properties props = ConfigLoader.load();
		assertThat(props).isNotNull();
		assertThat(props.stringPropertyNames()).isNotEmpty();
	}

	@Test
	void load_returns_properties_for_localstack_environment() {
		environmentVariables.set("ENV", "localstack");
		Properties props = ConfigLoader.load();
		assertThat(props).isNotNull();
		assertThat(props.stringPropertyNames()).isNotEmpty();
	}

	@Test
	void load_uses_default_when_env_is_blank() {
		environmentVariables.set("ENV", "   ");
		Properties props = ConfigLoader.load();
		assertThat(props).isNotNull();
		assertThat(props.stringPropertyNames()).isNotEmpty();
	}

	@Test
	void load_uses_default_when_env_is_empty() {
		environmentVariables.set("ENV", "");
		Properties props = ConfigLoader.load();
		assertThat(props).isNotNull();
		assertThat(props.stringPropertyNames()).isNotEmpty();
	}

	@Test
	void load_throws_if_file_missing() {
		environmentVariables.set("ENV", "nonexistent_env_xyz");
		assertThatThrownBy(ConfigLoader::load)
			.isInstanceOf(RuntimeException.class)
			.hasMessageContaining("config file not found");
	}

	@Test
	void constructor_can_be_instantiated() {
		// Test to cover default constructor
		ConfigLoader loader = new ConfigLoader();
		assertThat(loader).isNotNull();
	}
}
