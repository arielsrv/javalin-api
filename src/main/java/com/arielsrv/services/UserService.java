package com.arielsrv.services;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.arielsrv.clients.PostClient;
import com.arielsrv.clients.TodoClient;
import com.arielsrv.clients.UserClient;
import com.arielsrv.clients.responses.PostResponse;
import com.arielsrv.clients.responses.TodoResponse;
import com.arielsrv.dto.PostDTO;
import com.arielsrv.dto.TodoDTO;
import com.arielsrv.dto.UserDTO;
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

	@WithSpan
	public Observable<List<UserDTO>> getUsers() {
		return this.userClient.getUsers().flatMap(userResponses ->
			Observable.fromIterable(userResponses)
				.concatMapEager(userResponse ->
						Observable.zip(
							this.postClient.getPosts(userResponse.id),
							this.todoClient.getTodos(userResponse.id),
							(postsResponse, todosResponse) -> mapToUserDTO(userResponse, postsResponse, todosResponse)
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

	private UserDTO mapToUserDTO(
		com.arielsrv.clients.responses.UserResponse userResponse,
		List<PostResponse> postsResponse,
		List<TodoResponse> todosResponse
	) {
		UserDTO userDTO = new UserDTO();
		userDTO.userId = userResponse.id;
		userDTO.email = userResponse.email;
		userDTO.name = userResponse.name;
		userDTO.posts = postsResponse.stream().map(p -> {
			PostDTO dto = new PostDTO();
			dto.id = p.id;
			dto.title = p.title;
			return dto;
		}).toList();
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
}
