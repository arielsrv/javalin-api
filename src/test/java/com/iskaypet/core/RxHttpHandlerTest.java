package com.iskaypet.core;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

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
}
