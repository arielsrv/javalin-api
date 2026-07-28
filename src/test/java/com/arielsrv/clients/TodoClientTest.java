package com.arielsrv.clients;

import com.arielsrv.clients.responses.TodoResponse;
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
class TodoClientTest {

	@Mock
	RestClient restClient;

	@InjectMocks
	TodoClient todoClient;

	@Test
	void getComments_returnsListOfTodoResponse() {
		TodoResponse[] arr = {
			new TodoResponse(100L, "Todo 1", "Body 1", null),
			new TodoResponse(200L, "Todo 2", "Body 2", null)
		};
		Response<TodoResponse[]> response = new Response<>(200, arr);
		when(restClient.getObservable(org.mockito.ArgumentMatchers.anyString(),
			org.mockito.ArgumentMatchers.<Class<TodoResponse[]>>any())).thenReturn(
			Observable.just(response));

		List<TodoResponse> result = todoClient.getTodos(1L).blockingFirst();

		assertThat(result).hasSize(2);
		assertThat(result.getFirst().id()).isEqualTo(100L);
		assertThat(result.getFirst().title()).isEqualTo("Todo 1");
		assertThat(result.getFirst().body()).isEqualTo("Body 1");
		assertThat(result.get(1).id()).isEqualTo(200L);
		assertThat(result.get(1).title()).isEqualTo("Todo 2");
		assertThat(result.get(1).body()).isEqualTo("Body 2");
	}
}
