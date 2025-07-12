package com.iskaypet.services;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.iskaypet.clients.PostClient;
import com.iskaypet.clients.TodoClient;
import com.iskaypet.clients.UserClient;
import com.iskaypet.clients.responses.PostResponse;
import com.iskaypet.clients.responses.TodoResponse;
import com.iskaypet.dto.PostDTO;
import com.iskaypet.dto.TodoDTO;
import com.iskaypet.dto.UserDTO;
import io.reactivex.rxjava3.core.Observable;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class UserService {

	@Inject
	UserClient userClient;

	@Inject
	PostClient postClient;

	@Inject
	TodoClient todoClient;

	public Observable<List<UserDTO>> getUsers() {
		return this.userClient.getUsers().flatMap(userResponses ->
			Observable.fromIterable(userResponses)
				.flatMap(userResponse ->
					Observable.zip(
						this.postClient.getPosts(userResponse.id),
						this.todoClient.getComments(userResponse.id),
						(postsResponse, todosResponse) -> {
							UserDTO userDTO = new UserDTO();
							userDTO.userId = userResponse.id;
							userDTO.email = userResponse.email;
							userDTO.name = userResponse.name;
							userDTO.posts = new ArrayList<>();
							userDTO.todos = new ArrayList<>();

							for (PostResponse postResponse : postsResponse) {
								PostDTO postDTO = new PostDTO();
								postDTO.id = postResponse.id;
								postDTO.title = postResponse.title;
								userDTO.posts.add(postDTO);
							}

							for (TodoResponse todoResponse : todosResponse) {
								TodoDTO todoDTO = new TodoDTO();
								todoDTO.id = todoResponse.id;
								todoDTO.title = todoResponse.title;
								todoDTO.body = todoResponse.body;
								todoDTO.dueOn = todoResponse.dueOn;
								userDTO.todos.add(todoDTO);
							}

							return userDTO;
						}
					)
				)
				.toList()
				.toObservable()
		);
	}
}
