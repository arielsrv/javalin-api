package com.iskaypet.clients;

import com.iskaypet.clients.responses.UserResponse;
import com.iskaypet.core.RestClient;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.List;

@Singleton
public class UserClient {

	private final RestClient restClient;

	@Inject
	public UserClient(@Named("user") RestClient restClient) {
		this.restClient = restClient;
	}

	public Observable<List<UserResponse>> getUsers() {
		String apiUrl = "/public/v2/users";
		return this.restClient.getObservable(apiUrl, UserResponse[].class)
			.map(response -> Arrays.asList(response.data()));
	}
}

