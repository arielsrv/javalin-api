package com.iskaypet.clients;

import com.iskaypet.clients.responses.TodoResponse;
import com.iskaypet.core.RestClient;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.List;

/**
 * Client for interacting with the todo service API.
 */
@Singleton
public class TodoClient {

	private final RestClient restClient;

	/**
	 * Creates a new TodoClient with the specified RestClient.
	 *
	 * @param restClient the REST client to use
	 */
	@Inject
	public TodoClient(@Named("todo") RestClient restClient) {
		this.restClient = restClient;
	}

	/**
	 * Gets all todos for a specific user.
	 *
	 * @param userId the ID of the user
	 * @return an Observable of the list of TodoResponses
	 */
	public Observable<List<TodoResponse>> getComments(Long userId) {
		String apiUrl = "/public/v2/users/%s/todos".formatted(userId);
		return this.restClient.getObservable(apiUrl, TodoResponse[].class)
			.map(response -> Arrays.asList(response.data()));
	}
}
