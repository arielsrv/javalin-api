package com.arielsrv;

import com.arielsrv.controllers.UserController;
import com.arielsrv.core.Config;
import com.arielsrv.core.ContainerRegistry;
import com.arielsrv.core.Server;
import com.arielsrv.modules.AppModule;
import com.google.inject.Guice;

public class Main {

	/**
	 * Creates server; registers handler; starts server on configured port
	 */
	public static void main(String[] args) {
		ContainerRegistry.setInjector(Guice.createInjector(new AppModule()));

		Server server = Server.create();
		server.get("/users", ctx -> ContainerRegistry.get(UserController.class).getUsers(ctx));
		server.start(Config.getIntValue("app.port"));
	}
}
