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

    public static java.util.Set<String> getRestClientNames() {
        java.util.Properties props = getProperties();
        java.util.Set<String> names = new java.util.HashSet<>();
        for (String key : props.stringPropertyNames()) {
            if (key.startsWith("rest.client.") && key.endsWith(".base.url")) {
                String name = key.substring("rest.client.".length(), key.length() - ".base.url".length());
                names.add(name);
            }
        }
        return names;
    }
}
