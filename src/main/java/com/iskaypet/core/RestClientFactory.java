package com.iskaypet.core;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;

public class RestClientFactory {

    private final Injector injector;

    @Inject
    public RestClientFactory(Injector injector) {
        this.injector = injector;
    }

    public RestClient get(String name) {
        return injector.getInstance(Key.get(RestClient.class, Names.named(name)));
    }
}
