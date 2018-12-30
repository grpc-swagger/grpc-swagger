package io.grpc.grpcswagger.openapi.v2;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.Nullable;

/**
 * @author liuzhengyang
 */
public class DocumentRegistry {
    private static final DocumentRegistry INSTANCE = new DocumentRegistry();

    public static DocumentRegistry getInstance() {
        return INSTANCE;
    }

    private final ConcurrentMap<String, SwaggerV2Documentation> registryMap =
            new ConcurrentHashMap<>();

    public void put(String serviceName, SwaggerV2Documentation swaggerV2Documentation) {
        registryMap.put(serviceName, swaggerV2Documentation);
    }

    @Nullable
    public SwaggerV2Documentation get(String serviceName) {
        return registryMap.get(serviceName);
    }
}
