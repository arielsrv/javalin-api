package com.iskaypet.core;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;
import io.reactivex.rxjava3.core.Observable;

import io.reactivex.rxjava3.disposables.Disposable;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Utility class to bridge RxJava Observables with Javalin's HTTP handlers.
 */
public final class RxHttpHandler {

	private RxHttpHandler() {
		// Utility class
	}

	/**
	 * Intercepts an RxJava-based handler and adapts it to Javalin's async handling.
	 *
	 * @param func the reactive handler function
	 * @param <T>  the type of the result
	 * @return a Javalin Handler
	 */
	public static <T> Handler intercept(Function<Context, Observable<T>> func) {
		return ctx -> {
			CompletableFuture<Void> future = new CompletableFuture<>();
			Disposable disposable = func.apply(ctx)
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
