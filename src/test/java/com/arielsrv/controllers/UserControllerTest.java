package com.arielsrv.controllers;

import com.arielsrv.dto.UserDTO;
import com.arielsrv.services.UserService;
import io.javalin.http.Context;
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
class UserControllerTest {

	@Mock
	UserService userService;
	@Mock
	Context context;
	@InjectMocks
	UserController userController;

	@Test
	void getUsers_returnsUserDTOList() {
		UserDTO user1 = new UserDTO(1L, "Alice", "alice@example.com", List.of(), List.of());
		UserDTO user2 = new UserDTO(2L, "Bob", "bob@example.com", List.of(), List.of());
		List<UserDTO> userList = Arrays.asList(user1, user2);
		when(userService.getUsers()).thenReturn(Observable.just(userList));

		List<UserDTO> result = userController.getUsers(context).blockingFirst();

		assertThat(result).hasSize(2);
		assertThat(result.getFirst().userId()).isEqualTo(1L);
		assertThat(result.get(0).name()).isEqualTo("Alice");
		assertThat(result.get(0).email()).isEqualTo("alice@example.com");
		assertThat(result.get(1).userId()).isEqualTo(2L);
		assertThat(result.get(1).name()).isEqualTo("Bob");
		assertThat(result.get(1).email()).isEqualTo("bob@example.com");
	}
}
