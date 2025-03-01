/**
 * Mapper class responsible for converting {@link Address} objects into data transfer objects (DTOs),
 * specifically {@link LocationDTO} and {@link AddressDTO}.
 *
 * <p>This class provides utility methods to ensure the transformation of domain objects into DTOs,
 * handling potential null or empty data scenarios gracefully.
 */
package com.caching.mapper;

import com.caching.exception.InvalidAddressException;
import com.caching.model.Address;
import com.caching.dto.out.AddressDTO;
import com.caching.dto.out.LocationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class DTOMapper {

    /**
     * Maps an {@link Address} object to a {@link LocationDTO}.
     *
     * <p>The method extracts the latitude and longitude from the first element of the data in the
     * {@link Address} object. If the data is null, empty, or the label is missing, the method logs
     * an error and returns {@code null}.
     *
     * @param location the {@link Address} object containing geolocation data.
     * @return a {@link LocationDTO} containing the latitude and longitude, or {@code null} if no valid data is available.
     */
    public LocationDTO mapToLocationDTO(Address location) {
        try {
            if (location.getData() == null || location.getData().isEmpty() || location.getData().get(0).getLabel() == null) {
                log.error("No results found for the given address in Address to LocationDTO mapping.");
                throw new InvalidAddressException("Invalid address data: " + location);
            }
            double latitude = location.getData().get(0).getLatitude();
            double longitude = location.getData().get(0).getLongitude();
            log.info("LocationDTO mapped successfully with latitude: {} and longitude: {}", latitude, longitude);
            return new LocationDTO(latitude, longitude);
        } catch (Exception e) {
            log.error("Error mapping Address to LocationDTO: {}", e.getMessage(), e);
            throw e; // Propagate for higher-level handling
        }
    }

    /**
     * Maps an {@link Address} object to an {@link AddressDTO}.
     *
     * <p>The method extracts the address label from the first element of the data in the
     * {@link Address} object. If the data is null or empty, the method logs an error and returns {@code null}.
     *
     * @param location the {@link Address} object containing address data.
     * @return an {@link AddressDTO} containing the address label, or {@code null} if no valid data is available.
     */
    public AddressDTO mapToAddressDTO(Address location) {
        try {
            if (location.getData() == null || location.getData().isEmpty()) {
                log.error("No results found for the given address in Address to AddressDTO mapping.");
                throw new InvalidAddressException("Invalid address data: " + location);
            }
            String address = location.getData().get(0).getLabel();
            log.info("AddressDTO mapped successfully with address: {}", address);
            return new AddressDTO(address);
        }
        catch (Exception e) {
            log.error("Error mapping Address to AddressDTO: {}", e.getMessage(), e);
            throw e; // Propagate for higher-level handling
        }
    }
}
