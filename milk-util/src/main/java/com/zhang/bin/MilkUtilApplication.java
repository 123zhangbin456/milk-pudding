package com.zhang.bin;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

/**
 * @ProjectName: Default (Template) Project
 * @ProjectPackage: com.zhang.bin
 * @Author: Mr.Pudding
 * @CreateTime: 2024-01-11  11:13
 * @Description: TODO
 * @Version: 1.0
 */
@SpringBootApplication
@EnableDiscoveryClient
public class MilkUtilApplication {
    public static void main(String[] args) {
        SpringApplication.run(MilkUtilApplication.class,args);
    }
}
