package com.iskaypet.clients;

import com.google.inject.Singleton;
import com.iskaypet.clients.responses.TodoResponse;
import com.iskaypet.core.RestClient;
import io.reactivex.rxjava3.core.Observable;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.util.Arrays;
import java.util.List;

@Singleton
public class TodoClient {

	private final RestClient restClient;

	@Inject
	public TodoClient(@Named("todo") RestClient restClient) {
		this.restClient = restClient;
	}

	public Observable<List<TodoResponse>> getComments(Long userId) {
		String apiUrl = "/public/v2/users/%s/todos".formatted(userId);
		return this.restClient.getObservable(apiUrl, TodoResponse[].class)
			.map(response -> Arrays.asList(response.data()));
	}
}
