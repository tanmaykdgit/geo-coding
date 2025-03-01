package com.caching;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = "com.caching")
@SpringBootApplication
@EnableCaching
public class GeolocationApplication {
    public static void main(String[] args) {
        SpringApplication.run(GeolocationApplication.class, args);
    }
}
