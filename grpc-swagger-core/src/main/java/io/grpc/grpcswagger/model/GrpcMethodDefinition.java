package io.grpc.grpcswagger.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhangjikai
 * Created on 2018-12-16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GrpcMethodDefinition {
    private String packageName;
    private String serviceName;
    private String methodName;

    public String getFullServiceName() {
        return packageName + "." + serviceName;
    }

    public String getFullMethodName() {
        return packageName + "." + serviceName + "/" + methodName;
    }
}
