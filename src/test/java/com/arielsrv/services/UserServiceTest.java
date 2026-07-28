package com.arielsrv.services;

import com.arielsrv.clients.CommentClient;
import com.arielsrv.clients.PostClient;
import com.arielsrv.clients.TodoClient;
import com.arielsrv.clients.UserClient;
import com.arielsrv.clients.responses.CommentResponse;
import com.arielsrv.clients.responses.PostResponse;
import com.arielsrv.clients.responses.TodoResponse;
import com.arielsrv.clients.responses.UserResponse;
import com.arielsrv.dto.UserDTO;
import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@Mock
	UserClient userClient;

	@Mock
	PostClient postClient;

	@Mock
	TodoClient todoClient;

	@Mock
	CommentClient commentClient;

	@InjectMocks
	UserService userService;

	@Test
	void getUsers_mapsUserResponsesToUserDTOs_withPostsAndTodos() {
		UserResponse user1 = new UserResponse(1L, "Alice", "alice@example.com");
		UserResponse user2 = new UserResponse(2L, "Bob", "bob@example.com");
		List<UserResponse> userResponses = Arrays.asList(user1, user2);
		when(userClient.getUsers()).thenReturn(Observable.just(userResponses));

		PostResponse post1 = new PostResponse(10L, "Post 1");
		PostResponse post2 = new PostResponse(20L, "Post 2");
		when(postClient.getPosts(1L)).thenReturn(Observable.just(List.of(post1)));
		when(postClient.getPosts(2L)).thenReturn(Observable.just(List.of(post2)));

		TodoResponse todo1 = new TodoResponse(100L, "Todo 1", "Body 1", null);
		TodoResponse todo2 = new TodoResponse(200L, "Todo 2", "Body 2", null);
		when(todoClient.getTodos(1L)).thenReturn(Observable.just(List.of(todo1)));
		when(todoClient.getTodos(2L)).thenReturn(Observable.just(List.of(todo2)));

		// Cada post busca sus comments (nivel anidado de concurrencia).
		CommentResponse comment1 = new CommentResponse(1000L, "Carol", "carol@example.com", "Comment on post 10");
		CommentResponse comment2 = new CommentResponse(2000L, "Dave", "dave@example.com", "Comment on post 20");
		when(commentClient.getComments(10L)).thenReturn(Observable.just(List.of(comment1)));
		when(commentClient.getComments(20L)).thenReturn(Observable.just(List.of(comment2)));

		List<UserDTO> result = userService.getUsers().blockingFirst();

		assertThat(result).hasSize(2);
		UserDTO alice = result.get(0);
		UserDTO bob = result.get(1);

		assertThat(alice.userId()).isEqualTo(1L);
		assertThat(alice.name()).isEqualTo("Alice");
		assertThat(alice.email()).isEqualTo("alice@example.com");
		assertThat(alice.posts()).hasSize(1);
		assertThat(alice.posts().get(0).id()).isEqualTo(10L);
		assertThat(alice.posts().get(0).title()).isEqualTo("Post 1");
		assertThat(alice.posts().get(0).comments()).hasSize(1);
		assertThat(alice.posts().get(0).comments().get(0).id()).isEqualTo(1000L);
		assertThat(alice.posts().get(0).comments().get(0).name()).isEqualTo("Carol");
		assertThat(alice.posts().get(0).comments().get(0).email()).isEqualTo("carol@example.com");
		assertThat(alice.posts().get(0).comments().get(0).body()).isEqualTo("Comment on post 10");
		assertThat(alice.todos()).hasSize(1);
		assertThat(alice.todos().get(0).id()).isEqualTo(100L);
		assertThat(alice.todos().get(0).title()).isEqualTo("Todo 1");
		assertThat(alice.todos().get(0).body()).isEqualTo("Body 1");

		assertThat(bob.userId()).isEqualTo(2L);
		assertThat(bob.name()).isEqualTo("Bob");
		assertThat(bob.email()).isEqualTo("bob@example.com");
		assertThat(bob.posts()).hasSize(1);
		assertThat(bob.posts().get(0).id()).isEqualTo(20L);
		assertThat(bob.posts().get(0).title()).isEqualTo("Post 2");
		assertThat(bob.posts().get(0).comments()).hasSize(1);
		assertThat(bob.posts().get(0).comments().get(0).id()).isEqualTo(2000L);
		assertThat(bob.posts().get(0).comments().get(0).body()).isEqualTo("Comment on post 20");
		assertThat(bob.todos()).hasSize(1);
		assertThat(bob.todos().get(0).id()).isEqualTo(200L);
		assertThat(bob.todos().get(0).title()).isEqualTo("Todo 2");
		assertThat(bob.todos().get(0).body()).isEqualTo("Body 2");
	}

	// More tests can be added for error cases, empty lists, etc.
}
