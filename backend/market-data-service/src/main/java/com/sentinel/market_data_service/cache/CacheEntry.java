package com.sentinel.market_data_service.cache;

public class CacheEntry<T> {

    private final T value;
    private final long expiresAtMillis;

    public CacheEntry(T value, long ttlMillis) {
        this.value = value;
        this.expiresAtMillis = System.currentTimeMillis() + ttlMillis;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expiresAtMillis;
    }

    public T getValue() {
        return value;
    }
}
