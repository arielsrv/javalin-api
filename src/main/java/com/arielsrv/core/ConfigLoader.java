package com.arielsrv.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

public class ConfigLoader {

	private static final String DEFAULT_ENV = "local";
	private static final String CONFIG_PATH_FORMAT = "/config/config.%s.properties";

	public static Properties load() {
		String env = System.getenv("ENV");
		if (env == null || env.isBlank()) {
			env = DEFAULT_ENV;
		}
		String path = String.format(CONFIG_PATH_FORMAT, env);
		Properties props = new Properties();
		try (InputStream is = ConfigLoader.class.getResourceAsStream(path)) {
			if (is == null) {
				throw new RuntimeException("config file not found: " + path);
			}
			props.load(is);
		} catch (IOException e) {
			throw new RuntimeException("error loading config: " + path, e);
		}
		overrideFromEnv(props);
		return props;
	}

	// 12-factor: cualquier env var con el nombre de la key en MAYUS y puntos -> guiones
	// bajos pisa el valor del .properties (ej: rest.client.user.base.url ->
	// REST_CLIENT_USER_BASE_URL). Permite override en runtime y en tests de integracion.
	private static void overrideFromEnv(Properties props) {
		for (String key : props.stringPropertyNames()) {
			String envKey = key.toUpperCase(Locale.ROOT).replace('.', '_');
			String envVal = System.getenv(envKey);
			if (envVal != null && !envVal.isBlank()) {
				props.setProperty(key, envVal);
			}
		}
	}
}
