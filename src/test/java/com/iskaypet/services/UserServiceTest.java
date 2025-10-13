package com.iskaypet.services;

import com.iskaypet.clients.PostClient;
import com.iskaypet.clients.TodoClient;
import com.iskaypet.clients.UserClient;
import com.iskaypet.clients.responses.PostResponse;
import com.iskaypet.clients.responses.TodoResponse;
import com.iskaypet.clients.responses.UserResponse;
import com.iskaypet.dto.UserDTO;
import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class UserServiceTest {

	@Mock
	UserClient userClient;

	@Mock
	PostClient postClient;

	@Mock
	TodoClient todoClient;

	@InjectMocks
	UserService userService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void getUsers_mapsUserResponsesToUserDTOs_withPostsAndTodos() {
		UserResponse user1 = new UserResponse();
		user1.id = 1L;
		user1.name = "Alice";
		user1.email = "alice@example.com";
		UserResponse user2 = new UserResponse();
		user2.id = 2L;
		user2.name = "Bob";
		user2.email = "bob@example.com";
		List<UserResponse> userResponses = Arrays.asList(user1, user2);
		when(userClient.getUsers()).thenReturn(Observable.just(userResponses));

		PostResponse post1 = new PostResponse();
		post1.id = 10L;
		post1.title = "Post 1";
		PostResponse post2 = new PostResponse();
		post2.id = 20L;
		post2.title = "Post 2";
		when(postClient.getPosts(1L)).thenReturn(Observable.just(List.of(post1)));
		when(postClient.getPosts(2L)).thenReturn(Observable.just(List.of(post2)));

		TodoResponse todo1 = new TodoResponse();
		todo1.id = 100L;
		todo1.title = "Todo 1";
		todo1.body = "Body 1";
		TodoResponse todo2 = new TodoResponse();
		todo2.id = 200L;
		todo2.title = "Todo 2";
		todo2.body = "Body 2";
		when(todoClient.getComments(1L)).thenReturn(Observable.just(List.of(todo1)));
		when(todoClient.getComments(2L)).thenReturn(Observable.just(List.of(todo2)));

		List<UserDTO> result = userService.getUsers().blockingFirst();

		assertThat(result).hasSize(2);
		UserDTO alice = result.get(0);
		UserDTO bob = result.get(1);

		assertThat(alice.userId).isEqualTo(1L);
		assertThat(alice.name).isEqualTo("Alice");
		assertThat(alice.email).isEqualTo("alice@example.com");
		assertThat(alice.posts).hasSize(1);
		assertThat(alice.posts.get(0).id).isEqualTo(10L);
		assertThat(alice.posts.get(0).title).isEqualTo("Post 1");
		assertThat(alice.todos).hasSize(1);
		assertThat(alice.todos.get(0).id).isEqualTo(100L);
		assertThat(alice.todos.get(0).title).isEqualTo("Todo 1");
		assertThat(alice.todos.get(0).body).isEqualTo("Body 1");

		assertThat(bob.userId).isEqualTo(2L);
		assertThat(bob.name).isEqualTo("Bob");
		assertThat(bob.email).isEqualTo("bob@example.com");
		assertThat(bob.posts).hasSize(1);
		assertThat(bob.posts.get(0).id).isEqualTo(20L);
		assertThat(bob.posts.get(0).title).isEqualTo("Post 2");
		assertThat(bob.todos).hasSize(1);
		assertThat(bob.todos.get(0).id).isEqualTo(200L);
		assertThat(bob.todos.get(0).title).isEqualTo("Todo 2");
		assertThat(bob.todos.get(0).body).isEqualTo("Body 2");
	}

	// Puedes agregar más tests para casos de error, listas vacías, etc.
}
