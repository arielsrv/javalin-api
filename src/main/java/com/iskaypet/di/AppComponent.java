package com.iskaypet.di;

import com.iskaypet.controllers.UserController;
import dagger.Component;
import javax.inject.Singleton;

@Singleton
@Component
public interface AppComponent {
    UserController userController();
} 