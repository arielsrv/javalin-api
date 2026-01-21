package com.iskaypet.core;

import com.iskaypet.modules.AppComponent;
import com.iskaypet.modules.DaggerAppComponent;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ContainerRegistryTest {

	private static void resetComponent() {
		try {
			java.lang.reflect.Field f = ContainerRegistry.class.getDeclaredField("component");
			f.setAccessible(true);
			f.set(null, null);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	void getComponent_throws_if_component_not_set() {
		resetComponent();
		assertThatThrownBy(ContainerRegistry::getComponent)
			.isInstanceOf(IllegalStateException.class)
			.hasMessageContaining("Component not set");
	}

	@Test
	void getComponent_returns_component_when_set() {
		resetComponent();
		AppComponent component = DaggerAppComponent.create();
		ContainerRegistry.setComponent(component);
		AppComponent result = ContainerRegistry.getComponent();
		assertThat(result).isNotNull();
		assertThat(result).isSameAs(component);
	}
}
