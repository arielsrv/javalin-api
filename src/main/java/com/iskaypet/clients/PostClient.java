package com.iskaypet.clients;

import com.google.inject.Singleton;
import com.iskaypet.clients.responses.PostResponse;
import com.iskaypet.core.RestClient;
import io.reactivex.rxjava3.core.Observable;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.util.Arrays;
import java.util.List;

@Singleton
public class PostClient {

	private final RestClient restClient;

	@Inject
	public PostClient(@Named("post") RestClient restClient) {
		this.restClient = restClient;
	}

	public Observable<List<PostResponse>> getPosts(Long userId) {
		String apiUrl = "/public/v2/users/%s/posts".formatted(userId);
		return this.restClient.getObservable(apiUrl, PostResponse[].class)
			.map(response -> Arrays.asList(response.data()));
	}
}
