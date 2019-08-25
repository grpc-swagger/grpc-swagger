package io.grpc.grpcswagger.store;

import io.grpc.grpcswagger.config.AppConfig;

/**
 * @author Jikai Zhang
 * @date 2019-08-24
 */
public class StorageUtils {
   
    private static final int MAX_CACHE_SIZE = 100000;
    
    public static <K, V> BaseStorage<K, V> newStorage() {
        if (AppConfig.serviceExpiredSeconds() > 0) {
            return LocalCacheStorage.<K, V>newBuilder()
                    .setExpireSeconds(AppConfig.serviceExpiredSeconds())
                    .setMaxSize(MAX_CACHE_SIZE)
                    .build();
        } else {
            return new MapStorage<>();
        }
    }
}
