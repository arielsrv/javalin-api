package com.iskaypet;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.iskaypet.controllers.UserController;
import com.iskaypet.core.Server;
import com.iskaypet.modules.AppModule;

public class Main {

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new AppModule());

        Server server = Server.create();

        server.get("/users", injector.getInstance(UserController.class)::getUsers);

        server.start(7070);
    }
}
