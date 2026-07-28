package com.arielsrv.core;

import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.arielsrv.core.RxOperators.parallelMapEach;

class RxOperatorsTest {

	@Test
	void parallelMapEach_maps_every_item() {
		Observable<List<Integer>> source = Observable.just(List.of(1, 2, 3));

		source.compose(parallelMapEach(n -> Observable.just(n * 10)))
			.test()
			.assertValue(List.of(10, 20, 30))
			.assertComplete();
	}

	@Test
	void parallelMapEach_preserves_input_order_despite_async_delays() {
		// El primer item resuelve mas lento que el resto: si no se preservara el
		// orden, terminaria ultimo. concatMapEager garantiza el orden de entrada.
		Observable<List<Integer>> source = Observable.just(List.of(1, 2, 3));

		source.compose(parallelMapEach(n ->
				n == 1
					? Observable.just(n).delay(50, java.util.concurrent.TimeUnit.MILLISECONDS)
					: Observable.just(n)))
			.test()
			.awaitDone(2, java.util.concurrent.TimeUnit.SECONDS)
			.assertValue(List.of(1, 2, 3))
			.assertComplete();
	}

	@Test
	void parallelMapEach_on_empty_list_emits_empty_list() {
		Observable<List<Integer>> source = Observable.just(List.of());

		source.compose(parallelMapEach(Observable::just))
			.test()
			.assertValue(List.of())
			.assertComplete();
	}

	@Test
	void parallelMapEach_propagates_mapper_error() {
		Observable<List<Integer>> source = Observable.just(List.of(1, 2, 3));

		source.compose(parallelMapEach(n -> {
				if (n == 2) {
					return Observable.error(new RuntimeException("boom"));
				}
				return Observable.just(n);
			}))
			.test()
			.assertError(RuntimeException.class)
			.assertError(t -> "boom".equals(t.getMessage()));
	}
}