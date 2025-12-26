package com.iskaypet.clients;

import com.iskaypet.clients.responses.PostResponse;
import com.iskaypet.core.RestClient;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
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
