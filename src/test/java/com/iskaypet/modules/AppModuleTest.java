package com.iskaypet.modules;

import com.iskaypet.controllers.UserController;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AppModuleTest {

	@Test
	void component_provides_user_controller() {
		AppComponent component = DaggerAppComponent.create();
		UserController controller = component.userController();
		assertThat(controller).isNotNull();
	}

	@Test
	void component_returns_same_singleton_instance() {
		AppComponent component = DaggerAppComponent.create();
		UserController controller1 = component.userController();
		UserController controller2 = component.userController();
		assertThat(controller1).isSameAs(controller2);
	}

	@Test
	void app_module_can_be_instantiated() {
		AppModule module = new AppModule();
		assertThat(module).isNotNull();
	}
}
