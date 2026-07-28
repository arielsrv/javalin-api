package com.arielsrv.services;

import com.arielsrv.clients.CommentClient;
import com.arielsrv.clients.PostClient;
import com.arielsrv.clients.TodoClient;
import com.arielsrv.clients.UserClient;
import com.arielsrv.clients.responses.CommentResponse;
import com.arielsrv.clients.responses.PostResponse;
import com.arielsrv.clients.responses.TodoResponse;
import com.arielsrv.clients.responses.UserResponse;
import com.arielsrv.dto.CommentDTO;
import com.arielsrv.dto.PostDTO;
import com.arielsrv.dto.TodoDTO;
import com.arielsrv.dto.UserDTO;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.reactivex.rxjava3.core.Observable;

import java.util.List;

import static com.arielsrv.core.RxOperators.parallelMapEach;

@Singleton
public class UserService {

	@Inject
	UserClient userClient;

	@Inject
	PostClient postClient;

	@Inject
	TodoClient todoClient;

	@Inject
	CommentClient commentClient;

	@WithSpan
	public Observable<List<UserDTO>> getUsers() {
		return this.userClient.getUsers()
			.compose(parallelMapEach(userResponse ->
				Observable.zip(
					postsWithComments(userResponse.id()),
					this.todoClient.getTodos(userResponse.id()),
					(posts, todosResponse) -> mapToUserDTO(userResponse, posts, todosResponse)
				)));
	}

	// Por cada post del usuario busca sus comments en paralelo y arma el PostDTO.
	// Anida un segundo nivel de fan-out (posts -> comments) manteniendo el orden.
	private Observable<List<PostDTO>> postsWithComments(Long userId) {
		return this.postClient.getPosts(userId)
			.compose(parallelMapEach(postResponse ->
				this.commentClient.getComments(postResponse.id())
					.map(commentsResponse -> mapToPostDTO(postResponse, commentsResponse))));
	}

	private UserDTO mapToUserDTO(
		UserResponse userResponse,
		List<PostDTO> posts,
		List<TodoResponse> todosResponse
	) {
		List<TodoDTO> todos = todosResponse.stream()
			.map(todoResponse -> new TodoDTO(
				todoResponse.id(),
				todoResponse.title(),
				todoResponse.body(),
				todoResponse.dueOn()))
			.toList();
		return new UserDTO(
			userResponse.id(),
			userResponse.name(),
			userResponse.email(),
			posts,
			todos);
	}

	private PostDTO mapToPostDTO(PostResponse postResponse, List<CommentResponse> commentsResponse) {
		List<CommentDTO> comments = commentsResponse.stream()
			.map(commentResponse -> new CommentDTO(
				commentResponse.id(),
				commentResponse.name(),
				commentResponse.email(),
				commentResponse.body()))
			.toList();
		return new PostDTO(postResponse.id(), postResponse.title(), comments);
	}
}
