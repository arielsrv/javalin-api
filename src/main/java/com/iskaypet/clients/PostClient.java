package com.iskaypet.clients;

import com.iskaypet.clients.responses.PostResponse;
import com.iskaypet.core.RestClient;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.List;

/**
 * Client for interacting with the post service API.
 */
@Singleton
public class PostClient {

	private final RestClient restClient;

	/**
	 * Creates a new PostClient with the specified RestClient.
	 *
	 * @param restClient the REST client to use
	 */
	@Inject
	public PostClient(@Named("post") RestClient restClient) {
		this.restClient = restClient;
	}

	/**
	 * Gets all posts for a specific user.
	 *
	 * @param userId the ID of the user
	 * @return an Observable of the list of PostResponses
	 */
	public Observable<List<PostResponse>> getPosts(Long userId) {
		String apiUrl = "/public/v2/users/%s/posts".formatted(userId);
		return this.restClient.getObservable(apiUrl, PostResponse[].class)
			.map(response -> Arrays.asList(response.data()));
	}
}
