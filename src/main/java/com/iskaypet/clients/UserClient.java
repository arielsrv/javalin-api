package com.iskaypet.clients;

import com.iskaypet.clients.responses.UserResponse;
import com.iskaypet.core.RestClient;
import io.reactivex.rxjava3.core.Observable;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.List;

@Singleton
public class UserClient {

    @Inject
    RestClient restClient;

    @Inject
    public UserClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public Observable<List<UserResponse>> getUsers() {
        return this.restClient.getObservable("https://gorest.co.in/public/v2/users",
                UserResponse[].class)
            .map(response -> Arrays.asList(response.getData()));
    }
}
