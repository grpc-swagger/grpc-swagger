package io.grpc.grpcswagger.openapi.v2;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * @author liuzhengyang
 */
@Data
class ParameterSchema {
    @JsonProperty("$ref")
    private String ref;
}
