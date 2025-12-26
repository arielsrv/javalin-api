package com.iskaypet.core;

import java.util.Properties;
import java.util.Set;

public class Config {

	private static final Properties properties = ConfigLoader.load();

	private static Properties getProperties() {
		return properties;
	}

	public static String getStringValue(String key) {
		return getProperties().getProperty(key);
	}

	public static Long getLongValue(String key) {
		return Long.valueOf(getProperties().getProperty(key));
	}

	public static Integer getIntValue(String key) {
		return Integer.valueOf(getProperties().getProperty(key));
	}

	public static java.util.Set<String> getRestClientNames() {
		Properties props = getProperties();
		Set<String> names = new java.util.HashSet<>();
		for (String key : props.stringPropertyNames()) {
			if (key.startsWith("rest.client.") && key.endsWith(".base.url")) {
				String name = key.substring("rest.client.".length(),
					key.length() - ".base.url".length());
				names.add(name);
			}
		}
		return names;
	}
}
