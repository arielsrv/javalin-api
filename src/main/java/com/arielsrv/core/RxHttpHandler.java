package com.arielsrv.core;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

public class RxHttpHandler {

	public static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(30);

	public static <T> Handler intercept(Function<Context, Observable<T>> func) {
		return intercept(func, DEFAULT_TIMEOUT);
	}

	public static <T> Handler intercept(Function<Context, Observable<T>> func, Duration timeout) {
		return ctx -> {
			CompletableFuture<Void> future = new CompletableFuture<>();
			// Abrimos el boundary async ANTES de suscribir: cuando la emision llegue
			// en otro hilo (HttpClient), la respuesta ya esta en modo async.
			ctx.future(() -> future);
			// timeout: techo duro para toda la request. Sin esto, un upstream colgado
			// deja la conexion tomada indefinidamente.
			Disposable subscription = func.apply(ctx)
				.firstElement()
				.timeout(timeout.toMillis(), TimeUnit.MILLISECONDS)
				.subscribe(
					result -> respond(ctx, future, HttpStatus.OK, result),
					error -> respond(ctx, future, statusFor(error), Map.of("error", messageOf(error))),
					() -> respond(ctx, future, HttpStatus.NOT_FOUND, Map.of("error", "Not found"))
				);
			// Si el cliente corta la conexion (Javalin cancela el future) antes de que
			// el upstream termine, cancelamos la suscripcion en vuelo. En terminacion
			// normal ya esta disposed, asi que este dispose es no-op.
			future.whenComplete((v, e) -> subscription.dispose());
		};
	}

	private static void respond(Context ctx, CompletableFuture<Void> future, HttpStatus status, Object body) {
		ctx.status(status);
		ctx.json(body);
		future.complete(null);
	}

	// El upstream se colgo mas alla del timeout: 504 (culpa del upstream), no 500.
	private static HttpStatus statusFor(Throwable error) {
		return error instanceof TimeoutException
			? HttpStatus.GATEWAY_TIMEOUT
			: HttpStatus.INTERNAL_SERVER_ERROR;
	}

	// getMessage() puede ser null (ej: NPE upstream, o TimeoutException) y Map.of no
	// acepta null: sin esto, el handler de error tiraria NPE y el future quedaria sin
	// completar, dejando la request colgada hasta el timeout del server.
	private static String messageOf(Throwable error) {
		return error.getMessage() != null ? error.getMessage() : error.getClass().getSimpleName();
	}
}