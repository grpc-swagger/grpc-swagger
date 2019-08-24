package io.grpc.grpcswagger.model;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

/**
 * @author liuzhengyang
 */
@Data
public class Result<T> {
    private int code;
    private String message;
    private T data;
    private String endpoint;

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(1);
        result.setData(data);
        return result;
    }

    public static <T> Result<T> error(String msg) {
        Result<T> result = new Result<>();
        result.setMessage(msg);
        result.setCode(-1);
        return result;
    }
    
    @JsonInclude(NON_EMPTY)
    public String getEndpoint() {
        return endpoint;
    }
    
    public Result<T> setEndpoint(String endpoint) {
        this.endpoint = endpoint;
        return this;
    }
}
