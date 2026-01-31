package com.arielsrv.core;

import com.google.inject.Injector;

public class ContainerRegistry {

	private static Injector injector;

	public static void setInjector(Injector injector) {
		ContainerRegistry.injector = injector;
	}

	public static <T> T get(Class<T> clazz) {
		if (injector == null) {
			throw new IllegalStateException("Injector not set");
		}
		return injector.getInstance(clazz);
	}
}
