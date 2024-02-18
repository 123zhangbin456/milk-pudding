package com.zhang.bin.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ProjectName: milk-pudding
 * @ProjectPackage: com.zhang.bin.controller
 * @Author: Mr.Pudding
 * @CreateTime: 2024-01-11  16:28
 * @Description: TODO
 * @Version: 1.0
 */
@RestController
@Slf4j
@RequestMapping("/log")
public class LogController {
    //通过日志管理器获取Logger对象
    //注意导入的包import org.slf4j.Logger; import org.slf4j.LoggerFactory;
    //Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping("/logCatch/{msg}")
    public String logCatch(@PathVariable(value = "msg") String msg) {
        //打印因为SLF4J就这5个级别
        //日志格式具体可以自己调："%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"
        log.error(" 错误信息，不会影响系统运行");
        log.warn(" 警告信息，可能会发生问题");
        log.info(" 运行信息，数据连接，网络连接，IO操作等"); //当前我们的log4j2.xml设置的级别
        log.debug(" 调试信息，一般在开发中使用，记录程序变量传递信息等等"); //SLF4J默认级别
        log.trace(" 追踪信息，记录程序所有的流程信息");
        log.info("打印请求过来的数据：{}", msg);
        return "请求成功！！";
    }
}
