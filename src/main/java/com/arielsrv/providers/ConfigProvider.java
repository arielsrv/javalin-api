package com.arielsrv.providers;

import com.arielsrv.core.ConfigLoader;
import jakarta.inject.Provider;
import jakarta.inject.Singleton;

import java.util.Properties;

@Singleton
public class ConfigProvider implements Provider<Properties> {

	private final Properties config;

	public ConfigProvider() {
		this.config = ConfigLoader.load();
	}

	@Override
	public Properties get() {
		return config;
	}
}
