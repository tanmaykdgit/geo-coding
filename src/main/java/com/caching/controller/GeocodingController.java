package com.caching.controller;

import com.caching.dto.out.AddressDTO;
import com.caching.dto.out.LocationDTO;
import com.caching.exception.InvalidAddressException;
import com.caching.service.core.GeocodingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class for handling geocoding and reverse geocoding requests.
 *
 * <p>This REST controller exposes endpoints for geocoding and reverse geocoding operations.
 * It integrates with the {@link GeocodingService} to process the requests and handle exceptions
 * gracefully.
 */

@Slf4j
@RestController
@RequiredArgsConstructor
public class GeocodingController {

    private final GeocodingService geocodingService;

    /**
     * Endpoint for geocoding an address.
     *
     * <p>This method takes an address as a query parameter, validates it, and fetches the geolocation
     * (latitude and longitude) corresponding to the address using the {@link GeocodingService}.
     *
     * <p>Possible responses:
     * <ul>
     *   <li>200 OK: Returns the {@link LocationDTO} with latitude and longitude.</li>
     *   <li>400 Bad Request: If the address is invalid or an {@link InvalidAddressException} is thrown.</li>
     *   <li>404 Not Found: If no location data is found for the given address.</li>
     *   <li>500 Internal Server Error: If an unexpected error occurs.</li>
     * </ul>
     *
     * @param address the address to geocode.
     * @return a {@link ResponseEntity} containing the geolocation data or an appropriate error response.
     */
    @GetMapping("/geocoding")
    public ResponseEntity<LocationDTO> getGeocoding(@RequestParam String address) {
        log.info("Getting geocoding for address: {} from the Geocoding Service", address);
        try {
            LocationDTO locationDTO = geocodingService.getGeocoding(address);
            return ResponseEntity.ok(locationDTO);
        } catch (InvalidAddressException e) {
            // Catch invalid address and return a 400 Bad Request response with an error message
            throw new InvalidAddressException(e.getMessage());
        } catch (Exception e) {
            // Handle other exceptions, return INTERNAL SERVER ERROR if something goes wrong
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    /**
     * Endpoint for reverse geocoding a location.
     *
     * <p>This method takes latitude and longitude as query parameters and fetches the corresponding
     * address using the {@link GeocodingService}.
     *
     * <p>Possible responses:
     * <ul>
     *   <li>200 OK: Returns the address as a plain string.</li>
     *   <li>500 Internal Server Error: If an unexpected error occurs.</li>
     * </ul>
     *
     * @param latitude the latitude of the location to reverse geocode.
     * @param longitude the longitude of the location to reverse geocode.
     * @return a {@link ResponseEntity} containing the address or an appropriate error response.
     */

    @GetMapping("/reverse-geocoding")
    public ResponseEntity<String> getReverseGeocoding(@RequestParam Double latitude, @RequestParam Double longitude) {
        log.info("Getting reverse geocoding for latitude: {} and longitude: {} from the Geocoding Service", latitude, longitude);
        try {
            AddressDTO addressDTO = geocodingService.getReverseGeocoding(latitude, longitude);
            return ResponseEntity.ok(addressDTO.getAddress());
        } catch (InvalidAddressException e) {
            // Catch invalid address and return a 400 Bad Request response with an error message
            throw new InvalidAddressException(e.getMessage());
        } catch (Exception e) {
            // Handle general exceptions
            log.error("Error occurred while fetching reverse geocoding data: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching reverse geocoding data.");
        }
    }

    @GetMapping("/")
    public ResponseEntity<String> getHomePage(){
        return ResponseEntity.ok("Welcome to the Geo Coding API.");
    }

}
