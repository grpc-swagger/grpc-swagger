package io.grpc.grpcswagger.manager;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.net.HostAndPort;

import io.grpc.grpcswagger.model.ServiceConfig;
import io.grpc.grpcswagger.store.BaseStorage;
import io.grpc.grpcswagger.store.StorageUtils;

/**
 * @author zhangjikai
 */
public class ServiceConfigManager {
    
    private static final BaseStorage<String, ServiceConfig> STORAGE = StorageUtils.newStorage();
    
    @Nullable
    public static HostAndPort getEndPoint(String fullServiceName) {
        ServiceConfig config = STORAGE.get(fullServiceName);
        if (config == null) {
            return null;
        }
        return HostAndPort.fromString(config.getEndPoint());
    }

    public static List<ServiceConfig> getServiceConfigs() {
        return STORAGE.getAll().values().asList();
    }

    public static ServiceConfig addServiceConfig(ServiceConfig serviceConfig) {
        STORAGE.put(serviceConfig.getService(), serviceConfig);
        return serviceConfig;
    }

    
}
