package com.arielsrv.core;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableTransformer;

import java.util.List;
import java.util.function.Function;

/**
 * Operadores RxJava reutilizables, pensados para engancharse a la cadena con
 * {@code .compose(...)}. Es lo más parecido a los extension methods de C# que da
 * Java puro: {@code .compose()} es el mecanismo nativo de RxJava para inyectar
 * operadores custom en el pipeline fluido.
 */
public final class RxOperators {

	private RxOperators() {
	}

	/**
	 * Recibe un {@code Observable<List<T>>}, hace fan-out async de cada item y las
	 * suscribe TODAS en paralelo (concurrencia = cantidad de items: 10 usuarios ->
	 * 10, 20 comments -> 20). {@code concatMapEager} sin {@code maxConcurrency}
	 * preserva el orden de entrada.
	 */
	public static <T, R> ObservableTransformer<List<T>, List<R>> parallelMapEach(
		Function<T, Observable<R>> mapper
	) {
		return upstream -> upstream.flatMap(items ->
			Observable.fromIterable(items)
				.concatMapEager(mapper::apply)
				.toList()
				.toObservable());
	}
}