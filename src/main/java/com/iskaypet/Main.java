package com.iskaypet;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.iskaypet.controllers.UserController;
import com.iskaypet.core.RxJavalin;
import com.iskaypet.modules.AppModule;

public class Main {

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new AppModule());

        RxJavalin app = RxJavalin.create(config -> {
            config.useVirtualThreads = true;   // ← ¡aquí la magia!
            config.showJavalinBanner = false;
        });

        UserController userController = injector.getInstance(UserController.class);

        app.get("/users", userController::getUsers);

        app.start(7070);
    }
}
