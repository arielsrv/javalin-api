package com.iskaypet.core;

public class Response<T> {

    private final int code;
    private final T data;

    public Response(int code, T data) {
        this.code = code;
        this.data = data;
    }

    public T getData() {
        return this.data;
    }

    public int getCode() {
        return this.code;
    }

    @Override
    public String toString() {
        return "Response{" +
                "code=" + code +
                ", data=" + data +
                '}';
    }
}
