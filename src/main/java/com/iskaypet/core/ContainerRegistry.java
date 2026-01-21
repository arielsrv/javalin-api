package com.iskaypet.core;

import com.iskaypet.modules.AppComponent;

/**
 * Registry for the Dagger application component.
 */
public final class ContainerRegistry {

	private static AppComponent component;

	private ContainerRegistry() {
		// Utility class
	}

	/**
	 * Gets the registered application component.
	 *
	 * @return the application component
	 * @throws IllegalStateException if the component has not been set
	 */
	public static AppComponent getComponent() {
		if (component == null) {
			throw new IllegalStateException("Component not set");
		}
		return component;
	}

	/**
	 * Sets the application component.
	 *
	 * @param component the application component to register
	 */
	public static void setComponent(AppComponent component) {
		ContainerRegistry.component = component;
	}
}
