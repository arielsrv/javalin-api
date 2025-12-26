package com.iskaypet.core;

import java.util.Properties;
import java.util.Set;

public final class Config {

	private static final Properties properties = ConfigLoader.load();

	private Config() {
		// Utility class
	}

	public static String getStringValue(String key) {
		return properties.getProperty(key);
	}

	public static Long getLongValue(String key) {
		return Long.valueOf(properties.getProperty(key));
	}

	public static Integer getIntValue(String key) {
		return Integer.valueOf(properties.getProperty(key));
	}

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
