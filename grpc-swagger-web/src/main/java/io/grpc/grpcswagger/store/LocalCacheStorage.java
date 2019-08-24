package io.grpc.grpcswagger.store;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableMap;

/**
 * @author Jikai Zhang
 * @date 2019-08-24
 */
public class LocalCacheStorage<K, V> implements BaseStorage<K, V> {
    
    private final Cache<K, V> cache;
    
    private LocalCacheStorage(Builder builder) {
        cache = CacheBuilder.newBuilder()
                .expireAfterAccess(builder.expireSeconds, TimeUnit.SECONDS)
                .maximumSize(builder.maxSize)
                .build();
    }
    
    public static <K, V> Builder<K, V> newBuilder() {
        return new LocalCacheStorage.Builder<>();
    }
   
    
    @Override
    public void put(K key, V value) {
        cache.put(key, value);
    }
    
    @Override
    public V get(K key) {
        return cache.getIfPresent(key);
    }
    
    @Override
    public void remove(K key) {
        cache.invalidate(key);
    }
    
    @Override
    public boolean exists(K key) {
        return get(key) != null;
    }
    
    @Override
    public ImmutableMap<K, V> getAll() {
        return ImmutableMap.copyOf(cache.asMap());
    }
    
    public static class Builder<K, V> {
        private long expireSeconds = 60;
        private long maxSize = 100;
    
        public Builder<K, V> setExpireSeconds(long expireSeconds) {
            checkArgument(expireSeconds > 0);
            this.expireSeconds = expireSeconds;
            return this;
        }
    
        public Builder<K, V> setMaxSize(long maxSize) {
            checkArgument(maxSize > 0);
            this.maxSize = maxSize;
            return this;
        }
        
        public LocalCacheStorage<K, V> build() {
            return new LocalCacheStorage<>(this);
        }
    }
}
