package com.arielsrv.core;

import io.avaje.inject.BeanScope;

public class ContainerRegistry {

	private static BeanScope beanScope;

	public static void setBeanScope(BeanScope beanScope) {
		ContainerRegistry.beanScope = beanScope;
	}

	public static <T> T get(Class<T> clazz) {
		if (beanScope == null) {
			throw new IllegalStateException("BeanScope not set");
		}
		return beanScope.get(clazz);
	}
}