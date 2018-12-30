package io.grpc.grpcswagger.openapi.v2;

import lombok.Data;

/**
 * @author liuzhengyang
 */
@Data
class ResponseObject {
    private String code;
    private ParameterSchema schema;
}
