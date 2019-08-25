package io.grpc.grpcswagger.openapi.v2;

import javax.annotation.Nullable;

import io.grpc.grpcswagger.store.BaseStorage;
import io.grpc.grpcswagger.store.StorageUtils;

/**
 * @author liuzhengyang
 */
public class DocumentRegistry {
    private static final DocumentRegistry INSTANCE = new DocumentRegistry();

    public static DocumentRegistry getInstance() {
        return INSTANCE;
    }

    private final BaseStorage<String, SwaggerV2Documentation> storage = StorageUtils.newStorage();

    public void put(String serviceName, SwaggerV2Documentation swaggerV2Documentation) {
        storage.put(serviceName, swaggerV2Documentation);
    }

    @Nullable
    public SwaggerV2Documentation get(String serviceName) {
        return storage.get(serviceName);
    }
}
