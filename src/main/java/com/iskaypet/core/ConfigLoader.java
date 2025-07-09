package com.iskaypet.core;

import java.io.IOException;
import java.io.InputStream;
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
        return props;
    }
}
