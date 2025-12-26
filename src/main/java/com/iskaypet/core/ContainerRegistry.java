package com.iskaypet.core;

import com.iskaypet.modules.AppComponent;

public class ContainerRegistry {

	private static AppComponent component;

	public static AppComponent getComponent() {
		if (component == null) {
			throw new IllegalStateException("Component not set");
		}
		return component;
	}

	public static void setComponent(AppComponent component) {
		ContainerRegistry.component = component;
	}
}
