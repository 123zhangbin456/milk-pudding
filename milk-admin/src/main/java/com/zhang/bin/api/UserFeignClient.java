package com.zhang.bin.api;

import com.zhang.bin.config.FeignDecoderConfig;
import com.zhang.bin.dto.UserAuthInfo;
import com.zhang.bin.api.fallback.UserFeignFallbackClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @ProjectName: milk-pudding
 * @ProjectPackage: com.zhang.bin.api
 * @Author: Mr.Pudding
 * @CreateTime: 2024-01-31  18:00
 * @Description: TODO
 * @Version: 1.0
 */
@FeignClient(value = "milk-admin", fallback = UserFeignFallbackClient.class, configuration = {FeignDecoderConfig.class})
public interface UserFeignClient {

    @GetMapping("/api/v1/users/{username}/authInfo")
    UserAuthInfo getUserAuthInfo(@PathVariable String username);
//
//    @GetMapping("/api/v1/users/{username}/authInfoReturnString")
//    String getUserAuthInfoReturnString(@PathVariable String username);
}
