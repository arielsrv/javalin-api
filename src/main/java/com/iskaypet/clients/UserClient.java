package com.iskaypet.clients;

import com.iskaypet.clients.responses.UserResponse;
import com.iskaypet.core.RestClient;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.List;

/**
 * Client for interacting with the user service API.
 */
@Singleton
public class UserClient {

	private final RestClient restClient;

	/**
	 * Creates a new UserClient with the specified RestClient.
	 *
	 * @param restClient the REST client to use
	 */
	@Inject
	public UserClient(@Named("user") RestClient restClient) {
		this.restClient = restClient;
	}

	/**
	 * Gets all users from the user service.
	 *
	 * @return an Observable of the list of UserResponses
	 */
	public Observable<List<UserResponse>> getUsers() {
		String apiUrl = "/public/v2/users";
		return this.restClient.getObservable(apiUrl, UserResponse[].class)
			.map(response -> Arrays.asList(response.data()));
	}
}

