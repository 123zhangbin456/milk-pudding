package com.zhang.bin.controller;

import com.zhang.bin.dto.UserAuthInfo;
import com.zhang.bin.service.SysUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @ProjectName: milk-pudding
 * @ProjectPackage: com.zhang.bin.controller
 * @Author: Mr.Pudding
 * @CreateTime: 2024-01-24  17:46
 * @Description: TODO
 * @Version: 1.0
 */
@RestController
@RequestMapping("user")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final SysUserDetailsService service;

    @ResponseBody
    @RequestMapping("/sayHello")
    public String sayHello(@RequestHeader(value = "sign",required = false) String sign) {
        log.info("我进来啦！！！！！");
        UserAuthInfo info = service.loadUserByUsername("admin");
        return "这是好吃的：" + info;
    }

//    @GetMapping("/sayString")
//    public String sayString(@RequestHeader(value = "sign",required = false) String sign) {
//        log.info("我进来啦！！！！！");
//        String info = service.loadUserByUsernameforString("admin");
//        return "这是好吃的：" + info;
//    }
}
