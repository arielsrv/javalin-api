package com.iskaypet.modules;

import com.iskaypet.controllers.UserController;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {

	UserController userController();
}

