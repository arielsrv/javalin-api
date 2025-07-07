package com.iskaypet.core;

import org.jetbrains.annotations.NotNull;

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
