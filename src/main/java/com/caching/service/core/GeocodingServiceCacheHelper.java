package com.caching.service.core;

import com.caching.model.Address;
import com.caching.dto.out.LocationDTO;
import com.caching.dto.out.AddressDTO;
import com.caching.mapper.DTOMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * Service class for handling geocoding and reverse geocoding operations, with caching for improved performance.
 *
 * <p>This class integrates with the {@link ClientService} to fetch geocoding data and leverages
 * {@link Cacheable} annotations to reduce redundant external API calls. Additionally, it maps raw data
 * from the repository into DTOs for easier consumption by client code.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class GeocodingServiceCacheHelper {

    /**
     * Repository for accessing geocoding and reverse geocoding data.
     */
    private final ClientService clientService;

    /**
     * Mapper for converting {@link Address} entities to corresponding DTOs.
     */
    private final DTOMapper dtoMapper;

    /**
     * Retrieves geocoding data (latitude, longitude, and related location information) for a specified address.
     *
     * <p>The result is cached under the cache name "geocoding" with the address as the key.
     * Caching is skipped for the address "goa" or if the result is {@code null}.
     *
     * @param address the address to geocode.
     * @return a {@link LocationDTO} containing the geocoded details, or {@code null} if no results are found.
     * @Cacheable(cacheNames = "geocoding", key = "#address", unless = "#result == null || #address.equalsIgnoreCase('goa')")
     */
    @Cacheable(cacheNames = "geocoding", key = "#address", unless = "#result == null || #address.equalsIgnoreCase('goa')")
    public LocationDTO getGeocoding(String address) {
        log.info("Fetching geocoding data for address: {} in service", address);
        Address geocoded = clientService.getGeocoding(address);
        return dtoMapper.mapToLocationDTO(geocoded);
    }

    /**
     * Retrieves reverse geocoding data (address information) for specified geographic coordinates.
     *
     * <p>The result is cached under the cache name "reverse-geocoding" using a composite key of latitude and longitude.
     *
     * @param latitude  the latitude of the location.
     * @param longitude the longitude of the location.
     * @return an {@link AddressDTO} containing reverse geocoding details.
     * @Cacheable(cacheNames = "reverse-geocoding", key = "{#latitude,#longitude}")
     */
    @Cacheable(cacheNames = "reverse-geocoding", key = "{#latitude,#longitude}")
    public AddressDTO getReverseGeocoding(Double latitude, Double longitude) {
        log.info("Fetching reverse geocoding data for latitude: {} and longitude: {} in service", latitude, longitude);
        Address reverseCoded = clientService.getReverseGeocoding(latitude, longitude);
        return dtoMapper.mapToAddressDTO(reverseCoded);
    }
}
