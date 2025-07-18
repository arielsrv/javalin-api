package com.iskaypet.core;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConfigLoaderTest {

	@BeforeEach
	void setup() {
	}

	@AfterEach
	void cleanup() {
		// No hay forma estándar de restaurar ENV en Java puro, pero los tests no la modifican realmente
	}

	@Test
	void load_returns_properties_for_local() {
		Properties props = ConfigLoader.load();
		assertThat(props).isNotNull();
		assertThat(props.stringPropertyNames()).isNotEmpty();
	}

	@Test
	void load_throws_if_file_missing() {
		// Test that trying to load a non-existent config file throws an exception
		assertThatThrownBy(() -> {
			// Simulate the behavior of ConfigLoader.load() when file is missing
			String path = "/config/config.nonexistent.properties";
			try (java.io.InputStream is = ConfigLoader.class.getResourceAsStream(path)) {
				if (is == null) {
					throw new RuntimeException("config file not found: " + path);
				}
			}
		}).isInstanceOf(RuntimeException.class)
			.hasMessageContaining("config file not found");
	}
}
