package com.zhang.bin.service;

import com.zhang.bin.api.UserFeignClient;
import com.zhang.bin.dto.UserAuthInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @ProjectName: milk-pudding
 * @ProjectPackage: com.zhang.bin.controller.service
 * @Author: Mr.Pudding
 * @CreateTime: 2024-02-01  11:45
 * @Description: TODO
 * @Version: 1.0
 */
@Service
//@RequiredArgsConstructor
@Slf4j
public class SysUserDetailsService {

    @Autowired
    private UserFeignClient userFeignClient;
    public UserAuthInfo loadUserByUsername(String username) {
        UserAuthInfo info = userFeignClient.getUserAuthInfo(username);
        log.info("SysUserDetailsService.UserAuthInfo:{}",info);
        return info;
    }

//    public String loadUserByUsernameforString(String username) {
//        String info = userFeignClient.getUserAuthInfoReturnString(username);
//        log.info("SysUserDetailsService.String:{}",info);
//        return info;
//    }
}
