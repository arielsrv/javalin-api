package com.iskaypet;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.iskaypet.controllers.UserController;
import com.iskaypet.core.Config;
import com.iskaypet.core.ContainerRegistry;
import com.iskaypet.core.Server;
import com.iskaypet.modules.AppModule;

public class Main {

	public static void main(String[] args) {
		Injector injector = Guice.createInjector(new AppModule());
		ContainerRegistry.setInjector(injector);

		Server server = Server.create();
		server.get("/users", ctx -> ContainerRegistry.get(UserController.class).getUsers(ctx));

		server.start(Config.getIntValue("app.port"));
	}
}
