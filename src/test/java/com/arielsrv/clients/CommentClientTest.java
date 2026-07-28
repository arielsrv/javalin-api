package com.arielsrv.clients;

import com.arielsrv.clients.responses.CommentResponse;
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
class CommentClientTest {

	@Mock
	RestClient restClient;

	@InjectMocks
	CommentClient commentClient;

	@Test
	void getComments_returnsListOfCommentResponse() {
		CommentResponse[] arr = {
			new CommentResponse(100L, "Alice", "alice@example.com", "Comment 1"),
			new CommentResponse(200L, "Bob", "bob@example.com", "Comment 2")
		};
		Response<CommentResponse[]> response = new Response<>(200, arr);
		when(restClient.getObservable(org.mockito.ArgumentMatchers.anyString(),
			org.mockito.ArgumentMatchers.<Class<CommentResponse[]>>any())).thenReturn(
			Observable.just(response));

		List<CommentResponse> result = commentClient.getComments(286457L).blockingFirst();

		assertThat(result).hasSize(2);
		assertThat(result.getFirst().id()).isEqualTo(100L);
		assertThat(result.getFirst().name()).isEqualTo("Alice");
		assertThat(result.getFirst().email()).isEqualTo("alice@example.com");
		assertThat(result.getFirst().body()).isEqualTo("Comment 1");
		assertThat(result.get(1).id()).isEqualTo(200L);
		assertThat(result.get(1).name()).isEqualTo("Bob");
		assertThat(result.get(1).email()).isEqualTo("bob@example.com");
		assertThat(result.get(1).body()).isEqualTo("Comment 2");
	}
}
