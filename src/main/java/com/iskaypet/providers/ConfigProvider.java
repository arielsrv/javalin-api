package com.iskaypet.providers;

import com.iskaypet.core.ConfigLoader;
import jakarta.inject.Provider;
import jakarta.inject.Singleton;

import java.util.Properties;

/**
 * Provider for the application configuration Properties.
 */
@Singleton
public class ConfigProvider implements Provider<Properties> {

	private final Properties config;

	/**
	 * Creates a new ConfigProvider and loads the configuration.
	 */
	public ConfigProvider() {
		this.config = ConfigLoader.load();
	}

	/**
	 * Gets the loaded configuration properties.
	 *
	 * @return the configuration properties
	 */
	@Override
	public Properties get() {
		return config;
	}
}
