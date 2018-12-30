package io.grpc.grpcswagger.openapi.v2;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * @author liuzhengyang
 */
@Data
public class Items {
    private String type;
    private String format;
    @JsonProperty("$ref")
    private String ref;
}
