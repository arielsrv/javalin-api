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
		return this.userClient.getUsers().flatMap(userResponses ->
			Observable.fromIterable(userResponses)
				.concatMapEager(userResponse ->
						Observable.zip(
							postsWithComments(userResponse.id),
							this.todoClient.getTodos(userResponse.id),
							(posts, todosResponse) -> mapToUserDTO(userResponse, posts, todosResponse)
						),
					10, // maxConcurrency: hasta 10 usuarios en paralelo
					1   // prefetch: cada zip emite un solo item
				)
				// concatMapEager (vs flatMap) suscribe los inner en paralelo pero
				// emite en el orden de entrada, preservando el orden de la lista.
				.toList()
				.toObservable()
		);
	}

	// Por cada post del usuario busca sus comments en paralelo y arma el PostDTO.
	// Anida un segundo nivel de fan-out (posts -> comments) manteniendo el orden.
	private Observable<List<PostDTO>> postsWithComments(Long userId) {
		return this.postClient.getPosts(userId).flatMap(postsResponse ->
			Observable.fromIterable(postsResponse)
				.concatMapEager(postResponse ->
						this.commentClient.getComments(postResponse.id)
							.map(commentsResponse -> mapToPostDTO(postResponse, commentsResponse)),
					10, // maxConcurrency: hasta 10 posts (comments) en paralelo por usuario
					1
				)
				.toList()
				.toObservable()
		);
	}

	private UserDTO mapToUserDTO(
		UserResponse userResponse,
		List<PostDTO> posts,
		List<TodoResponse> todosResponse
	) {
		UserDTO userDTO = new UserDTO();
		userDTO.userId = userResponse.id;
		userDTO.email = userResponse.email;
		userDTO.name = userResponse.name;
		userDTO.posts = posts;
		userDTO.todos = todosResponse.stream().map(t -> {
			TodoDTO dto = new TodoDTO();
			dto.id = t.id;
			dto.title = t.title;
			dto.body = t.body;
			dto.dueOn = t.dueOn;
			return dto;
		}).toList();
		return userDTO;
	}

	private PostDTO mapToPostDTO(PostResponse postResponse, List<CommentResponse> commentsResponse) {
		PostDTO dto = new PostDTO();
		dto.id = postResponse.id;
		dto.title = postResponse.title;
		dto.comments = commentsResponse.stream().map(c -> {
			CommentDTO commentDTO = new CommentDTO();
			commentDTO.id = c.id;
			commentDTO.name = c.name;
			commentDTO.email = c.email;
			commentDTO.body = c.body;
			return commentDTO;
		}).toList();
		return dto;
	}
}
