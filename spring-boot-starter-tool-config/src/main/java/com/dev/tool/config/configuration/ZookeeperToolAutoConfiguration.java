package com.dev.tool.config.configuration;

import com.dev.tool.common.model.Tool;
import com.dev.tool.common.util.GroupToolEnum;
import com.dev.tool.config.processor.ZookeeperToolProcessor;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ZookeeperConfigProperties.class)
@ConditionalOnProperty(prefix = "spring.dev.tool.config.zk", matchIfMissing = false, havingValue = "true", value = "enable")
//spring.tool.enable=true则开启工具
public class ZookeeperToolAutoConfiguration {


    @Bean(name = "zookeeper")
    public ZooKeeper initZookeeper(ZookeeperConfigProperties zookeeperConfigProperties) {
        try {
            return new ZooKeeper(zookeeperConfigProperties.getAddress(), zookeeperConfigProperties.getTimeout(), (event) -> {
            });
        } catch (Exception e) {
            return null;
        }
    }

    @Bean(name = "zookeeperToolProcessor")
    public ZookeeperToolProcessor initZookeeperToolProcessor(ZooKeeper zooKeeper) {
        ZookeeperToolProcessor zookeeperToolProcessor = new ZookeeperToolProcessor();
        zookeeperToolProcessor.setZooKeeper(zooKeeper);
        return zookeeperToolProcessor;
    }

    @Bean(name = "zookeeperTool")
    public Tool initzookeeperTool(ZookeeperToolProcessor zookeeperToolProcessor) {
        Tool tool = new Tool(GroupToolEnum.ZOOKEEPER, zookeeperToolProcessor);
        return tool;
    }

}
