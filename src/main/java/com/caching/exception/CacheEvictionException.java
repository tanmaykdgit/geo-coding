package com.caching.exception;

public class CacheEvictionException extends RuntimeException {
    public CacheEvictionException(String message, Throwable cause) {
        super(message, cause);
    }
}
