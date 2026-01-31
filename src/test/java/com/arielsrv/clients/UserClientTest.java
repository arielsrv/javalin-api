package com.arielsrv.clients;

import com.arielsrv.clients.responses.UserResponse;
import com.arielsrv.core.Response;
import com.arielsrv.core.RestClient;
import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserClientTest {

	@Mock
	RestClient restClient;

	@InjectMocks
	UserClient userClient;

	@Test
	void getUsers_returnsListOfUserResponse() {
		UserResponse[] usersArr = new UserResponse[2];
		usersArr[0] = new UserResponse();
		usersArr[0].id = 1L;
		usersArr[0].name = "Alice";
		usersArr[0].email = "alice@example.com";
		usersArr[1] = new UserResponse();
		usersArr[1].id = 2L;
		usersArr[1].name = "Bob";
		usersArr[1].email = "bob@example.com";
		Response<UserResponse[]> response = new Response<>(200, usersArr);
		when(restClient.getObservable(org.mockito.ArgumentMatchers.anyString(),
			org.mockito.ArgumentMatchers.<Class<UserResponse[]>>any())).thenReturn(
			Observable.just(response));

		List<UserResponse> result = userClient.getUsers().blockingFirst();

		assertThat(result).hasSize(2);
		assertThat(result.getFirst().id).isEqualTo(1L);
		assertThat(result.get(0).name).isEqualTo("Alice");
		assertThat(result.get(0).email).isEqualTo("alice@example.com");
		assertThat(result.get(1).id).isEqualTo(2L);
		assertThat(result.get(1).name).isEqualTo("Bob");
		assertThat(result.get(1).email).isEqualTo("bob@example.com");
	}
}
