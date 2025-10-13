package com.iskaypet.clients;

import com.iskaypet.clients.responses.TodoResponse;
import com.iskaypet.core.Response;
import com.iskaypet.core.RestClient;
import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class TodoClientTest {

    @Mock
    RestClient restClient;

    @InjectMocks
    TodoClient todoClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getComments_returnsListOfTodoResponse() {
        TodoResponse[] arr = new TodoResponse[2];
        arr[0] = new TodoResponse();
        arr[0].id = 100L;
        arr[0].title = "Todo 1";
        arr[0].body = "Body 1";
        arr[1] = new TodoResponse();
        arr[1].id = 200L;
        arr[1].title = "Todo 2";
        arr[1].body = "Body 2";
        Response<TodoResponse[]> response = new Response<>(200, arr);
        when(restClient.getObservable(org.mockito.ArgumentMatchers.anyString(),
            org.mockito.ArgumentMatchers.<Class<TodoResponse[]>>any())).thenReturn(
            Observable.just(response));

        List<TodoResponse> result = todoClient.getComments(1L).blockingFirst();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).id).isEqualTo(100L);
        assertThat(result.get(0).title).isEqualTo("Todo 1");
        assertThat(result.get(0).body).isEqualTo("Body 1");
        assertThat(result.get(1).id).isEqualTo(200L);
        assertThat(result.get(1).title).isEqualTo("Todo 2");
        assertThat(result.get(1).body).isEqualTo("Body 2");
    }
}
