package com.milkpudding.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
public class PuddingUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(PuddingUserApplication.class, args);
    }
}
