package io.grpc.grpcswagger.model;

import lombok.Data;

/**
 * @author liuzhengyang
 */
@Data
public class Result<T> {
    private int code;
    private String message;
    private T data;

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setData(data);
        return result;
    }

    public static <T> Result<T> error(String msg) {
        Result<T> result = new Result<>();
        result.setMessage(msg);
        result.setCode(-1);
        return result;
    }
}
