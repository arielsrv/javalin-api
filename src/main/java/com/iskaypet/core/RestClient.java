package com.iskaypet.core;

import com.google.gson.Gson;
import io.reactivex.rxjava3.core.Observable;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Singleton
public class RestClient {

    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    @Inject
    public RestClient() {}

    public <T> Observable<Response<T>> getObservable(String apiUrl, Class<T> clazz) {
        return Observable.fromFuture(
            this.client.sendAsync(HttpRequest.newBuilder().uri(URI.create(apiUrl)).GET().build(),
                HttpResponse.BodyHandlers.ofString()).thenApply(response -> {
                T data = gson.fromJson(response.body(), clazz);
                return new Response<>(response.statusCode(), data);
            }));
    }
}

