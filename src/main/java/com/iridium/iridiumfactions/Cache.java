package com.iridium.iridiumfactions;

import java.util.concurrent.CompletableFuture;

public class Cache<T> {

    private T cache;
    private CompletableFuture<T> cacheAsync;
    private long lastCache;
    private final long refreshTimeInMilliseconds;

    public Cache(long refreshTimeInMilliseconds) {
        this.refreshTimeInMilliseconds = refreshTimeInMilliseconds;
    }

    public T getCache(CacheContentProvider<T> cacheContentProvider) {
        long currentTime = System.currentTimeMillis();
        if (lastCache + refreshTimeInMilliseconds < currentTime || cache == null) {
            this.cache = cacheContentProvider.getObject();
            this.lastCache = currentTime;
        }
        return cache;
    }

    public CompletableFuture<T> getCacheAsync(CacheContentProvider<CompletableFuture<T>> cacheContentProvider) {
        long currentTime = System.currentTimeMillis();
        if (lastCache + refreshTimeInMilliseconds < currentTime || cache == null) {
            this.cacheAsync = cacheContentProvider.getObject();
            this.lastCache = currentTime;
        }
        return cacheAsync;
    }

    public void clearCache() {
        this.cache = null;
    }

    public interface CacheContentProvider<T> {
        T getObject();
    }
}
