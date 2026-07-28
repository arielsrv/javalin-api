package com.arielsrv.clients;

import com.arielsrv.clients.responses.PostResponse;
import com.arielsrv.core.RestClient;
import io.avaje.inject.External;
import io.reactivex.rxjava3.core.Observable;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

import java.util.Arrays;
import java.util.List;

@Singleton
public class PostClient {

	private final RestClient restClient;

	@Inject
	public PostClient(@External @Named("post") RestClient restClient) {
		this.restClient = restClient;
	}

	public Observable<List<PostResponse>> getPosts(Long userId) {
		String apiUrl = "/public/v2/users/%s/posts".formatted(userId);
		return this.restClient.getObservable(apiUrl, PostResponse[].class)
			.map(response -> Arrays.asList(response.data()));
	}
}
