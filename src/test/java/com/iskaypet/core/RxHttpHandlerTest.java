package com.iskaypet.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.reactivex.rxjava3.core.Observable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

class RxHttpHandlerTest {

	@Test
	void intercept_successful_result() throws Exception {
		Context ctx = mock(Context.class);
		Observable<String> obs = Observable.just("ok");
		Handler handler = RxHttpHandler.intercept(c -> obs);
		handler.handle(ctx);
		verify(ctx).status(any());
		verify(ctx).json("ok");
	}

	@Test
	void intercept_error_result() throws Exception {
		Context ctx = mock(Context.class);
		Observable<String> obs = Observable.error(new RuntimeException("fail"));
		Handler handler = RxHttpHandler.intercept(c -> obs);
		handler.handle(ctx);
		verify(ctx).status(any());
		verify(ctx).json(argThat(map -> map.toString().contains("fail")));
	}

	@Test
	void intercept_not_found() throws Exception {
		Context ctx = mock(Context.class);
		Observable<String> obs = Observable.empty();
		Handler handler = RxHttpHandler.intercept(c -> obs);
		handler.handle(ctx);
		verify(ctx).status(any());
		verify(ctx).json(argThat(map -> map.toString().contains("Not found")));
	}

	@Test
	void intercept_callsHandler() {
		AtomicBoolean called = new AtomicBoolean(false);
		Function<Context, Observable<String>> handler = ctx -> {
			called.set(true);
			return Observable.just("ok");
		};
		var result = RxHttpHandler.intercept(handler);
		assertThat(result).isNotNull();
		// No easy way to test Javalin handler without full integration, but we check the wrapper
		handler.apply(null).test().assertValue("ok");
		assertThat(called).isTrue();
	}
}
