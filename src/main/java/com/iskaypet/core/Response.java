package com.iskaypet.core;

import org.jetbrains.annotations.NotNull;

/**
 * Generic response wrapper for API responses.
 *
 * @param <T>  the type of the data
 * @param code the HTTP status code
 * @param data the response data
 */
public record Response<T>(int code, T data) {

	@NotNull
	@Override
	public String toString() {
		return "Response{" +
			"code=" + code +
			", data=" + data +
			'}';
	}
}
