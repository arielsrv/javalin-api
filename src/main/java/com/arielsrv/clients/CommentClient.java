package com.arielsrv.clients;

import com.google.inject.Singleton;
import com.arielsrv.clients.responses.CommentResponse;
import com.arielsrv.core.RestClient;
import io.reactivex.rxjava3.core.Observable;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.util.Arrays;
import java.util.List;

@Singleton
public class CommentClient {

	private final RestClient restClient;

	@Inject
	public CommentClient(@Named("comment") RestClient restClient) {
		this.restClient = restClient;
	}

	public Observable<List<CommentResponse>> getComments(Long postId) {
		String apiUrl = "/public/v2/posts/%s/comments".formatted(postId);
		return this.restClient.getObservable(apiUrl, CommentResponse[].class)
			.map(response -> Arrays.asList(response.data()));
	}
}
