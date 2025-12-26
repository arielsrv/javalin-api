package com.iskaypet;

import com.google.inject.Guice;
import com.iskaypet.controllers.UserController;
import com.iskaypet.core.Config;
import com.iskaypet.core.ContainerRegistry;
import com.iskaypet.core.Server;
import com.iskaypet.modules.AppModule;

public class Main {

	/**
	 * Creates server; registers handler; starts server on configured port
	 */
	public static void main(String[] args) {
		// Create injector
		ContainerRegistry.setInjector(Guice.createInjector(new AppModule()));

		// Create server
		Server server = Server.create();

		// Register handlers
		server.get("/users", ctx -> ContainerRegistry.get(UserController.class).getUsers(ctx));

		// Start server
		server.start(Config.getIntValue("app.port"));
	}
}
