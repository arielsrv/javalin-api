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
import java.util.function.Function;

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

	// Mapea cada item de la lista a una llamada async y las suscribe TODAS en
	// paralelo (concurrencia = cantidad de items: 10 usuarios -> 10, 20 comments
	// -> 20). concatMapEager sin maxConcurrency preserva el orden de entrada.
	private static <T, R> Observable<List<R>> parallelMap(
		List<T> items,
		Function<T, Observable<R>> mapper
	) {
		return Observable.fromIterable(items)
			.concatMapEager(mapper::apply)
			.toList()
			.toObservable();
	}

	@WithSpan
	public Observable<List<UserDTO>> getUsers() {
		return this.userClient.getUsers().flatMap(userResponses ->
			parallelMap(userResponses, userResponse ->
				Observable.zip(
					postsWithComments(userResponse.id),
					this.todoClient.getTodos(userResponse.id),
					(posts, todosResponse) -> mapToUserDTO(userResponse, posts, todosResponse)
				)
			)
		);
	}

	// Por cada post del usuario busca sus comments en paralelo y arma el PostDTO.
	// Anida un segundo nivel de fan-out (posts -> comments) manteniendo el orden.
	private Observable<List<PostDTO>> postsWithComments(Long userId) {
		return this.postClient.getPosts(userId).flatMap(postsResponse ->
			parallelMap(postsResponse, postResponse ->
				this.commentClient.getComments(postResponse.id)
					.map(commentsResponse -> mapToPostDTO(postResponse, commentsResponse))
			)
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
		userDTO.todos = todosResponse.stream().map(todoResponse -> {
			TodoDTO dto = new TodoDTO();
			dto.id = todoResponse.id;
			dto.title = todoResponse.title;
			dto.body = todoResponse.body;
			dto.dueOn = todoResponse.dueOn;
			return dto;
		}).toList();
		return userDTO;
	}

	private PostDTO mapToPostDTO(PostResponse postResponse, List<CommentResponse> commentsResponse) {
		PostDTO dto = new PostDTO();
		dto.id = postResponse.id;
		dto.title = postResponse.title;
		dto.comments = commentsResponse.stream().map(commentResponse -> {
			CommentDTO commentDTO = new CommentDTO();
			commentDTO.id = commentResponse.id;
			commentDTO.name = commentResponse.name;
			commentDTO.email = commentResponse.email;
			commentDTO.body = commentResponse.body;
			return commentDTO;
		}).toList();
		return dto;
	}
}
