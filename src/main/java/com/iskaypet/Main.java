package com.iskaypet;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.iskaypet.controllers.UserController;
import com.iskaypet.core.RxJavalin;
import com.iskaypet.modules.AppModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new AppModule());

        System.setProperty("org.slf4j.simpleLogger.log.io.javalin", "error");
        System.setProperty("org.slf4j.simpleLogger.log.org.eclipse.jetty", "error");

        RxJavalin app = RxJavalin.create(config -> {
            config.useVirtualThreads = true;
            config.showJavalinBanner = false;
        });

        UserController userController = injector.getInstance(UserController.class);

        app.get("/users", userController::getUsers);

        logger.info("Listening on http://localhost:7070/");
        app.start(7070);
    }
}
