package com.iskaypet.modules;

import com.iskaypet.controllers.UserController;
import dagger.Component;

import javax.inject.Singleton;

/**
 * Dagger component for the application, defining the injection points.
 */
@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {

	/**
	 * Provides the UserController instance.
	 *
	 * @return the UserController
	 */
	UserController userController();
}

