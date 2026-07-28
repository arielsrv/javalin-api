package com.arielsrv.core;

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
			// Abrimos el boundary async ANTES de suscribir: cuando la emision llegue
			// en otro hilo (HttpClient), la respuesta ya esta en modo async.
			ctx.future(() -> future);
			func.apply(ctx)
				.firstElement()
				.subscribe(
					result -> respond(ctx, future, HttpStatus.OK, result),
					error -> respond(ctx, future, HttpStatus.INTERNAL_SERVER_ERROR,
						Map.of("error", messageOf(error))),
					() -> respond(ctx, future, HttpStatus.NOT_FOUND,
						Map.of("error", "Not found"))
				);
		};
	}

	private static void respond(Context ctx, CompletableFuture<Void> future, HttpStatus status, Object body) {
		ctx.status(status);
		ctx.json(body);
		future.complete(null);
	}

	// getMessage() puede ser null (ej: NPE upstream) y Map.of no acepta null:
	// sin esto, el handler de error tiraria NPE y el future quedaria sin completar,
	// dejando la request colgada hasta el timeout del server.
	private static String messageOf(Throwable error) {
		return error.getMessage() != null ? error.getMessage() : error.getClass().getSimpleName();
	}
}