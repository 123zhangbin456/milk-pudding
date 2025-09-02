package com.milkpudding.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import com.github.lianjiatech.retrofit.spring.boot.core.RetrofitScan;

@SpringBootApplication
@EnableFeignClients
@RetrofitScan("com.milkpudding.api.client")
public class PuddingApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(PuddingApiApplication.class, args);
    }

}
