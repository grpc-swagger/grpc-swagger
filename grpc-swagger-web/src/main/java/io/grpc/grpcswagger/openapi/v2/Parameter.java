package io.grpc.grpcswagger.openapi.v2;

import lombok.Data;

/**
 * @author liuzhengyang
 */
@Data
class Parameter {
    private String in = "body";
    private String name;
    private String description;
    private boolean required = true;
    private ParameterSchema schema;
}
