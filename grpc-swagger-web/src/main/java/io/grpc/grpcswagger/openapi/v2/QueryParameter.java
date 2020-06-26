package io.grpc.grpcswagger.openapi.v2;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Jikai Zhang
 * @date 2020-06-26
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QueryParameter extends Parameter{
    private String in = "query";
    private String type;
}
