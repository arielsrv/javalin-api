package com.iskaypet;

import com.iskaypet.core.Config;
import com.iskaypet.core.ContainerRegistry;
import com.iskaypet.core.Server;
import com.iskaypet.modules.AppComponent;
import com.iskaypet.modules.DaggerAppComponent;

public class Main {

	/**
	 * Creates server; registers handler; starts server on configured port
	 */
	public static void main(String[] args) {
		AppComponent component = DaggerAppComponent.create();
		ContainerRegistry.setComponent(component);

		Server server = Server.create();
		server.get("/users", ctx -> component.userController().getUsers(ctx));
		server.start(Config.getIntValue("app.port"));
	}
}
