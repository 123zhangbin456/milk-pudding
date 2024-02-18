package com.zhang.bin.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ProjectName: milk-pudding
 * @ProjectPackage: com.zhang.bin.controller
 * @Author: Mr.Pudding
 * @CreateTime: 2024-01-24  17:46
 * @Description: TODO
 * @Version: 1.0
 */
@RestController
@RequestMapping("user-service")
@Slf4j
public class UserController {
    @ResponseBody
    @RequestMapping("/sayHello")
    public String sayHello(@RequestHeader(value = "sign",required = false) String sign) {
        log.info("我进来啦！！！！！");
        return "这是好吃的：" + sign;
    }
}
