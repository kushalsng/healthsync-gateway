package com.healthcare.legacymockservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class LegacyMockServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LegacyMockServiceApplication.class, args);
    }

}
