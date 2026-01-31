package com.arielsrv.clients;

import com.google.inject.Singleton;
import com.arielsrv.clients.responses.UserResponse;
import com.arielsrv.core.RestClient;
import io.reactivex.rxjava3.core.Observable;
import jakarta.inject.Inject;
import jakarta.inject.Named;

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

