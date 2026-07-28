package com.arielsrv.core;

import io.avaje.inject.BeanScope;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ContainerRegistryTest {

	private static void resetBeanScope() {
		try {
			java.lang.reflect.Field f = ContainerRegistry.class.getDeclaredField("beanScope");
			f.setAccessible(true);
			f.set(null, null);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	void get_throws_if_bean_scope_not_set() {
		resetBeanScope();
		assertThatThrownBy(() -> ContainerRegistry.get(Sample.class))
			.isInstanceOf(IllegalStateException.class)
			.hasMessageContaining("BeanScope not set");
	}

	@Test
	void get_returns_instance_from_bean_scope() {
		resetBeanScope();
		Sample sample = new Sample();
		BeanScope scope = mock(BeanScope.class);
		when(scope.get(Sample.class)).thenReturn(sample);
		ContainerRegistry.setBeanScope(scope);
		assertThat(ContainerRegistry.get(Sample.class)).isSameAs(sample);
	}

	@Test
	void constructor_can_be_instantiated() {
		// Test to cover default constructor
		ContainerRegistry registry = new ContainerRegistry();
		assertThat(registry).isNotNull();
	}

	static class Sample {
	}
}
