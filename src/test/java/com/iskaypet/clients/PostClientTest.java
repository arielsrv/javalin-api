package com.iskaypet.clients;

import com.iskaypet.clients.responses.PostResponse;
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

class PostClientTest {

    @Mock
    RestClient restClient;

    @InjectMocks
    PostClient postClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getPosts_returnsListOfPostResponse() {
        PostResponse[] postsArr = new PostResponse[2];
        postsArr[0] = new PostResponse();
        postsArr[0].id = 10L;
        postsArr[0].title = "Post 1";
        postsArr[1] = new PostResponse();
        postsArr[1].id = 20L;
        postsArr[1].title = "Post 2";
        Response<PostResponse[]> response = new Response<>(200, postsArr);
        when(restClient.getObservable(org.mockito.ArgumentMatchers.anyString(),
            org.mockito.ArgumentMatchers.<Class<PostResponse[]>>any())).thenReturn(
            Observable.just(response));

        List<PostResponse> result = postClient.getPosts(1L).blockingFirst();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).id).isEqualTo(10L);
        assertThat(result.get(0).title).isEqualTo("Post 1");
        assertThat(result.get(1).id).isEqualTo(20L);
        assertThat(result.get(1).title).isEqualTo("Post 2");
    }
}
