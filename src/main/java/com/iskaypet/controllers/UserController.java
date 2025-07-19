package com.iskaypet.controllers;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.iskaypet.core.ApiController;
import com.iskaypet.dto.UserDTO;
import com.iskaypet.services.UserService;
import io.javalin.http.Context;
import io.javalin.openapi.HttpMethod;
import io.javalin.openapi.OpenApi;
import io.javalin.openapi.OpenApiContent;
import io.javalin.openapi.OpenApiResponse;
import io.reactivex.rxjava3.core.Observable;

import java.util.List;

@Singleton
public class UserController extends ApiController {

	@Inject
	UserService userService;

	@OpenApi(
		summary = "Get all users",
		operationId = "getUsers",
		path = "/users",
		methods = HttpMethod.GET,
		responses = {
			@OpenApiResponse(
				status = "200",
				content = @OpenApiContent(
					from = UserDTO[].class,
					example = "[{\"userId\":1,\"name\":\"Alice\",\"email\":\"alice@example.com\",\"posts\":[{\"id\":10,\"title\":\"Post 1\"}],\"todos\":[{\"id\":100,\"title\":\"Todo 1\",\"body\":\"Body 1\",\"dueOn\":\"2024-07-19\"}]}]"
				)
			),
			@OpenApiResponse(status = "500", description = "Internal server error")
		}
	)
	public Observable<List<UserDTO>> getUsers(Context context) {
		return this.userService.getUsers();
	}
}
