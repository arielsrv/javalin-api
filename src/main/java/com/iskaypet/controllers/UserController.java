package com.iskaypet.controllers;

import com.iskaypet.core.ApiController;
import com.iskaypet.dto.UserDTO;
import com.iskaypet.services.UserService;
import io.javalin.http.Context;
import io.reactivex.rxjava3.core.Observable;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class UserController extends ApiController {

    @Inject
    UserService userService;

    @Inject
    public UserController(UserService userService) {
        this.userService = userService;
    }

    public Observable<List<UserDTO>> getUsers(Context context) {
        return this.userService.getUsers();
    }
}
