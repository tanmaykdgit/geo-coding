package com.caching;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class CachingApplicationTest {

    @Test
    void contextLoads() {
        // This test method can be empty; it's just used to ensure that the application context loads successfully
        assertTrue(true, "The application context should load without any issues.");
    }
}