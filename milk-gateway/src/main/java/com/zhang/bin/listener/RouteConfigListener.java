package com.zhang.bin.listener;


import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.zhang.bin.service.RouteOperator;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * @ProjectName: milk-pudding
 * @ProjectPackage: com.zhang.bin.listener
 * @Author: Mr.Pudding
 * @CreateTime: 2024-01-30  14:48
 * @Description: 监听器的主要作用监听nacos路由配置信息，获取配置信息后刷新进程内路由信息。
 * 该配置类通过@PostConstruct注解，启动时加载dynamicRouteByNacosListener方法，通过nacos的host、namespace、group等信息，读取nacos配置信息。
 * addListener接口获取到配置信息后，将配置信息交给routeOperator.refreshAll处理。
 * @Version: 1.0
 */
@Component
@Slf4j
@RefreshScope   //加上该注解后，就可以解决nacos 上先加了配置路由，再启动 gateway，启动不起来的问题
public class RouteConfigListener {

    private String dataId = "gateway-json-routes";

    @Value("${spring.cloud.nacos.config.server-addr}")
    private String serverAddr;
    @Value("${spring.cloud.nacos.config.namespace}")
    private String namespace;
    @Value("${spring.cloud.nacos.config.group}")
    private String group;

    @Autowired
    private RouteOperator routeOperator;

    @PostConstruct
    public void dynamicRouteByNacosListener() throws NacosException {
        log.info("gateway-json-routes dynamicRouteByNacosListener config serverAddr is {} namespace is {} group is {}", serverAddr, namespace, group);
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.SERVER_ADDR, serverAddr);
        properties.put(PropertyKeyConst.NAMESPACE, namespace);
        ConfigService configService = NacosFactory.createConfigService(properties);
        // 添加监听，nacos上的配置变更后会执行
        configService.addListener(dataId, group, new Listener() {
            @Override
            public void receiveConfigInfo(String configInfo) {
                // 解析和处理都交给RouteOperator完成
                routeOperator.refreshAll(configInfo);
            }

            @Override
            public Executor getExecutor() {
                return null;
            }
        });

        // 获取当前的配置
        String initConfig = configService.getConfig(dataId, group, 5000);

        // 立即更新
        routeOperator.refreshAll(initConfig);
    }
}
