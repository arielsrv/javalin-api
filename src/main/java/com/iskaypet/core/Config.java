package com.iskaypet.core;

import java.util.Properties;
import java.util.Set;

/**
 * Configuration utility class to access application properties.
 */
public final class Config {

	private static final Properties properties = ConfigLoader.load();

	private Config() {
		// Utility class
	}

	/**
	 * Gets a configuration value as a String.
	 *
	 * @param key the configuration key
	 * @return the configuration value
	 */
	public static String getStringValue(String key) {
		return properties.getProperty(key);
	}

	/**
	 * Gets a configuration value as a Long.
	 *
	 * @param key the configuration key
	 * @return the configuration value
	 */
	public static Long getLongValue(String key) {
		return Long.valueOf(properties.getProperty(key));
	}

	/**
	 * Gets a configuration value as an Integer.
	 *
	 * @param key the configuration key
	 * @return the configuration value
	 */
	public static Integer getIntValue(String key) {
		return Integer.valueOf(properties.getProperty(key));
	}

	/**
	 * Gets the names of all configured REST clients.
	 *
	 * @return a set of REST client names
	 */
	public static java.util.Set<String> getRestClientNames() {
		Set<String> names = new java.util.HashSet<>();
		for (String key : properties.stringPropertyNames()) {
			if (key.startsWith("rest.client.") && key.endsWith(".base.url")) {
				String name = key.substring("rest.client.".length(),
					key.length() - ".base.url".length());
				names.add(name);
			}
		}
		return names;
	}
}
