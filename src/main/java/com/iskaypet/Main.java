package com.iskaypet;

import com.iskaypet.core.Server;
import com.iskaypet.di.AppComponent;
import com.iskaypet.di.DaggerAppComponent;

public class Main {

    public static void main(String[] args) {
        AppComponent component = DaggerAppComponent.create();
        Server server = Server.create();
        server.get("/users", component.userController()::getUsers);
        server.start(8081);
    }
}
