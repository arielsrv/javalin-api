package com.iskaypet.core;

import java.util.Properties;

public class Config {

    private static Properties getProperties() {
        return ContainerRegistry.get(Properties.class);
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
}
