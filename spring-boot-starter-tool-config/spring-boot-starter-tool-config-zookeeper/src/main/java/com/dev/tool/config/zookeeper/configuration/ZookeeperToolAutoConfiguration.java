package com.dev.tool.config.zookeeper.configuration;

import com.dev.tool.config.zookeeper.processor.ZookeeperToolProcessor;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnBean(ZookeeperConfigProperties.class)
public class ZookeeperToolAutoConfiguration {


    @Bean(name = "zookeeper")
    public ZooKeeper initZookeeper(ZookeeperConfigProperties zookeeperConfigProperties) {
        try {
            //这里需要改成连接池，以确保链接不会中断
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

}
