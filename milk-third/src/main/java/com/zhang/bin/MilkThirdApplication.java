package com.zhang.bin;

import com.zhang.bin.api.UserFeignClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @ProjectName: Default (Template) Project
 * @ProjectPackage: com.zhang.bin
 * @Author: Mr.Pudding
 * @CreateTime: 2024-01-11  14:17
 * @Description: TODO
 * @Version: 1.0
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackageClasses = {UserFeignClient.class})
public class MilkThirdApplication {
    public static void main(String[] args) {
        SpringApplication.run(MilkThirdApplication.class, args);
    }
}
