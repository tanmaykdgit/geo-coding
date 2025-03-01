package com.caching.service.core;

import com.caching.dto.out.AddressDTO;
import com.caching.dto.out.LocationDTO;
import com.caching.service.cacheeviction.CacheTrackingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service responsible for handling geocoding and reverse geocoding operations.
 *
 * <p>This service integrates with the {@link CacheTrackingService} to update cache access times
 * and utilizes {@link GeocodingServiceCacheHelper} to retrieve geocoding and reverse geocoding data.
 */

@RequiredArgsConstructor
@Service
@Slf4j
public class GeocodingService {

    /**
     * Service for tracking and updating cache access times for geocoding and reverse geocoding operations.
     */
    private final CacheTrackingService cacheTrackingService;

    /**
     * Helper service for interacting with the geocoding cache.
     */
    private final GeocodingServiceCacheHelper geocodingServiceCacheHelper;

    /**
     * Retrieves the geocoded location (latitude and longitude) for a given address.
     * <p>
     * Updates the cache access time for the provided address in the geocoding cache.
     *
     * @param address the address for which geocoding is requested.
     * @return a {@link LocationDTO} containing the latitude and longitude of the address.
     */
    public LocationDTO getGeocoding(String address) {
        cacheTrackingService.updateGeocodingAccessTime(address);
        try {
            cacheTrackingService.evictStaleReverseGeocodingEntries();
        } catch (Exception e) {
            log.error("Error during geocoding cache cleanup: {}", e.getMessage());
        }
        return geocodingServiceCacheHelper.getGeocoding(address);
    }

    /**
     * Retrieves the address corresponding to the given geographic coordinates (latitude and longitude).
     * <p>
     * Updates the cache access time for the provided coordinates in the reverse geocoding cache.
     *
     * @param latitude  the latitude of the location.
     * @param longitude the longitude of the location.
     * @return an {@link AddressDTO} containing the address information for the coordinates.
     */
    public AddressDTO getReverseGeocoding(Double latitude, Double longitude) {
        cacheTrackingService.updateReverseGeocodingAccessTime(latitude + "," + longitude);
        try {
            cacheTrackingService.evictStaleReverseGeocodingEntries();
        } catch (Exception e) {
            log.error("Error during reverse geocoding cache cleanup: {}", e.getMessage());
        }
        return geocodingServiceCacheHelper.getReverseGeocoding(latitude, longitude);
    }
}
