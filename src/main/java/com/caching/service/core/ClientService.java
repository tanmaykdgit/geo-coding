package com.caching.service.core;

import com.caching.exception.InvalidAddressException;
import com.caching.model.Address;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Service class for handling geocoding and reverse geocoding operations.
 *
 * <p>This class integrates with the {@link RestTemplate} to fetch geocoding data and reverse geocoding data
 * from external APIs. It also validates the input address and response data before returning the results.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClientService {

    /**
     * RestTemplate instance for performing HTTP requests.
     */
    private final RestTemplate restTemplate;

    /**
     * API access key for authentication, retrieved from application properties.
     */
    @Value("${api-key}")
    private String accessKey;

    /**
     * Base URL for the geocoding API, retrieved from application properties.
     */
    @Value("${geocoding-url}")
    private String geocodingURL;

    /**
     * Base URL for the reverse geocoding API, retrieved from application properties.
     */
    @Value("${reverse-geocoding-url}")
    private String reverseGeocodingURL;

    /**
     * Fetches geocoding data (latitude, longitude, and other location details) for a given address.
     *
     * <p>Replaces placeholders in the configured API URL with actual values. Validates the address
     * before making the API call and throws an exception if the address is invalid or if no data
     * is returned by the API.
     *
     * <p>Replaces "delhi" with the address to be geocoded because in the TestGeocoding URL it is hardcoded.
     *
     * @param address the address to geocode.
     * @return an {@link Address} object containing geocoding details.
     * @throws InvalidAddressException if the address is null, empty, or invalid.
     */
    public Address getGeocoding(String address) {
        log.info("Fetching geocoding data for address: {} from Client's External API", address);

        if (address == null || address.trim().isEmpty()) {
            log.error("Provided address is null or empty.");
            throw new InvalidAddressException("Invalid address: No Address is passed" + address);
        }

        // Build the request URL by replacing placeholders
        String requestURL = geocodingURL
                .replace("ACCESS_KEY_PLACEHOLDER", accessKey)
                .replace("ADDRESS", address).replace("delhi", address);

        ResponseEntity<Address> response = restTemplate.exchange(requestURL, HttpMethod.GET, null, Address.class);
        Address responseBody = response.getBody();
        if (responseBody == null || responseBody.getData() == null || responseBody.getData().isEmpty()) {
            log.error("No geocoding results found for address: {} from Client's External API", address);
            throw new InvalidAddressException("No results found for the given address: " + address);
        }

        return responseBody;
    }

    /**
     * Fetches reverse geocoding data (address) for a given latitude and longitude.
     *
     * <p>Replaces placeholders in the configured API URL with actual values. Validates the response
     * from the API and returns the {@link Address} object containing reverse geocoding details.
     *
     * @param latitude  the latitude for reverse geocoding.
     * @param longitude the longitude for reverse geocoding.
     * @return an {@link Address} object containing reverse geocoding details.
     */
    public Address getReverseGeocoding(Double latitude, Double longitude) {
        log.info("Fetching reverse geocoding data for latitude: {} and longitude: {} from Client's External API", latitude, longitude);

        if (latitude == null || longitude == null) {
            log.error("Latitude or longitude is null.");
            throw new InvalidAddressException("Invalid latitude or longitude: Latitude or Longitude is null");
        }
        // Build the request URL by replacing placeholders
        String requestURL = reverseGeocodingURL
                .replace("API_KEY_PLACEHOLDER", accessKey)
                .replace("LATITUDE", latitude.toString())
                .replace("LONGITUDE", longitude.toString());

        ResponseEntity<Address> response = restTemplate.exchange(requestURL, HttpMethod.GET, null, Address.class);
        Address responseBody = response.getBody();
        if (responseBody == null || responseBody.getData() == null || responseBody.getData().isEmpty()) {
            log.error("No reverse geocoding results found for latitude: {} and longitude: {} from Client's External API", latitude, longitude);
            throw new InvalidAddressException("No results found for the given latitude and longitude: " + latitude + ", " + longitude);
        }
        return responseBody;
    }
}
