package io.grpc.grpcswagger.openapi.v2;

import lombok.Builder;
import lombok.Data;

/**
 * @author liuzhengyang
 */
@Data
@Builder
class InfoObject {
    private String title;
    private String version = "0.0.1";
}
