package com.arielsrv.clients;

import com.arielsrv.clients.responses.TodoResponse;
import com.arielsrv.core.RestClient;
import io.avaje.inject.External;
import io.reactivex.rxjava3.core.Observable;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

import java.util.Arrays;
import java.util.List;

@Singleton
public class TodoClient {

	private final RestClient restClient;

	@Inject
	public TodoClient(@External @Named("todo") RestClient restClient) {
		this.restClient = restClient;
	}

	public Observable<List<TodoResponse>> getTodos(Long userId) {
		String apiUrl = "/public/v2/users/%s/todos".formatted(userId);
		return this.restClient.getObservable(apiUrl, TodoResponse[].class)
			.map(response -> Arrays.asList(response.data()));
	}
}
