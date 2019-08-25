package io.grpc.grpcswagger.store;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.collect.ImmutableMap;

/**
 * @author Jikai Zhang
 * @date 2019-08-24
 */
public class MapStorage<K, V> implements BaseStorage<K, V> {
    private final Map<K, V> map = new ConcurrentHashMap<>();
    
    @Override
    public void put(K key, V value) {
        map.put(key, value);
    }
    
    @Override
    public V get(K key) {
        return map.get(key);
    }
    
    @Override
    public void remove(K key) {
        map.remove(key);
    }
    
    @Override
    public boolean exists(K key) {
        return map.containsKey(key);
    }
    
    @Override
    public ImmutableMap<K, V> getAll() {
        return ImmutableMap.copyOf(map);
    }
}
