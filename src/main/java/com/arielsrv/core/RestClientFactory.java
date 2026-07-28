package com.arielsrv.core;

import io.avaje.inject.BeanScope;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class RestClientFactory {

	private final BeanScope beanScope;

	@Inject
	public RestClientFactory(BeanScope beanScope) {
		this.beanScope = beanScope;
	}

	public RestClient get(String name) {
		return beanScope.get(RestClient.class, name);
	}
}
