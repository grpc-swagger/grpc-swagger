package io.grpc.grpcswagger.store;

import com.google.common.collect.ImmutableMap;

/**
 * @author Jikai Zhang
 * @date 2019-08-24
 */
public interface BaseStorage<K, V> {
    void put(K key, V value);
    V get(K key);
    void remove(K key);
    boolean exists(K key);
    ImmutableMap<K, V> getAll();
}
