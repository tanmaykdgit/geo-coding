/**
 * Service responsible for tracking cache access times and evicting stale cache entries
 * for geocoding and reverse geocoding caches.
 *
 * <p>This service uses Spring's {@link CacheManager} to interact with cache instances
 * and implements custom eviction logic based on the last access time of cache entries.
 * It ensures that cache entries remain within a defined size limit and are evicted if they exceed
 * a specified expiration time.
 */
package com.caching.service.cacheeviction;

import com.caching.exception.CacheEvictionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class CacheTrackingService {
    private static final long CACHE_SIZE = 10;

    /**
     * Spring's cache manager to manage cache instances.
     */
    private final CacheManager cacheManager;

    /**
     * A map to track the last access times for geocoding cache entries.
     * The key is the cache key, and the value is the last access timestamp in milliseconds.
     */
    private final Map<String, Long> geocodingAccessTimes = new ConcurrentHashMap<>();

    /**
     * A map to track the last access times for reverse geocoding cache entries.
     * The key is the cache key, and the value is the last access timestamp in milliseconds.
     */
    private final Map<String, Long> reverseGeocodingAccessTimes = new ConcurrentHashMap<>();

    /**
     * Cache expiration time in milliseconds. Default is 5 minutes.
     */
    private static final long CACHE_EXPIRATION_TIME = 5 * 60 * 1000L;

    /**
     * Constructs a new {@code CacheTrackingService} with the provided {@link CacheManager}.
     *
     * @param cacheManager the cache manager used to manage cache instances.
     */
    public CacheTrackingService(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    /**
     * Updates the last access time for a geocoding cache entry.
     * This ensures the entry's timestamp is refreshed whenever it is accessed.
     *
     * @param cacheKey the key of the geocoding cache entry.
     */
    public void updateGeocodingAccessTime(String cacheKey) {
        geocodingAccessTimes.put(cacheKey, System.currentTimeMillis());
    }

    /**
     * Updates the last access time for a reverse geocoding cache entry.
     * This ensures the entry's timestamp is refreshed whenever it is accessed.
     *
     * @param cacheKey the key of the reverse geocoding cache entry.
     */
    public void updateReverseGeocodingAccessTime(String cacheKey) {
        reverseGeocodingAccessTimes.put(cacheKey, System.currentTimeMillis());
    }

    /**
     * Evicts stale entries from the geocoding cache.
     * Stale entries are those that exceed the cache expiration time or if the cache size exceeds the limit.
     */
    public void evictStaleGeocodingEntries() {
        evictStaleEntries("geocoding", geocodingAccessTimes);
    }

    /**
     * Evicts stale entries from the reverse geocoding cache.
     * Stale entries are those that exceed the cache expiration time or if the cache size exceeds the limit.
     */
    public void evictStaleReverseGeocodingEntries() {
        evictStaleEntries("reverse-geocoding", reverseGeocodingAccessTimes);
    }

    /**
     * Helper method to evict stale cache entries for a given cache name and access time map.
     * Stale entries are removed either if their last access time exceeds the configured expiration time
     * or if the number of cache entries exceeds the allowed size.
     *
     * @param cacheName  the name of the cache to clean up.
     * @param accessTimes a map of cache keys and their corresponding last access times.
     */
    private void evictStaleEntries(String cacheName, Map<String, Long> accessTimes) {
        if (accessTimes.isEmpty()) {
            return;
        }

        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            log.error("Cache {} not found in cache manager", cacheName);
            return;
        }

        List<Map.Entry<String, Long>> sortedEntries = new ArrayList<>(accessTimes.entrySet());
        sortedEntries.sort(Map.Entry.comparingByValue()); // Sort by last access time (oldest first)

        // Remove entries until the cache size is reduced to CACHE_SIZE or expired entries are evicted
        while (!accessTimes.isEmpty() &&
                (accessTimes.size() > CACHE_SIZE || System.currentTimeMillis() - sortedEntries.get(0).getValue() > CACHE_EXPIRATION_TIME)) {
            try {
                // Evict the oldest entry
                String oldestKey = sortedEntries.get(0).getKey();
                cache.evict(oldestKey);
                accessTimes.remove(oldestKey);

                // Remove the entry from the sorted list
                sortedEntries.remove(0);
                log.info("Evicted stale cache entry from {}: {}", cacheName, oldestKey);
            } catch (Exception e) {
                log.error("Failed to evict cache entry for key {}: {}", sortedEntries.get(0).getKey(), e.getMessage());
                throw new CacheEvictionException("Cache eviction failed for cache: " + cacheName, e);
            }
        }
    }
}
