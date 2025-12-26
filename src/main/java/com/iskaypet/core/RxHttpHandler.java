package com.iskaypet.core;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;
import io.reactivex.rxjava3.core.Observable;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class RxHttpHandler {

	public static <T> Handler intercept(Function<Context, Observable<T>> func) {
		return ctx -> {
			CompletableFuture<Void> future = new CompletableFuture<>();
			func.apply(ctx)
				.firstElement()
				.subscribe(
					result -> {
						ctx.status(HttpStatus.OK);
						ctx.json(result);
						future.complete(null);
					},
					error -> {
						ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
						ctx.json(Map.of("error", error.getMessage()));
						future.complete(null);
					},
					() -> {
						ctx.status(HttpStatus.NOT_FOUND);
						ctx.json(Map.of("error", "Not found"));
						future.complete(null);
					}
				);
			ctx.future(() -> future);
		};
	}
}
