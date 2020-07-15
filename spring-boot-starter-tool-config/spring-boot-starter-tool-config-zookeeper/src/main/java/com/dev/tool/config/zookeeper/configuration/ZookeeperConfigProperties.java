package com.dev.tool.config.zookeeper.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.dev.tool.config.zk")
//spring.tool.enable=true则开启工具
@ConditionalOnProperty(prefix = "spring.dev.tool.config.zk", matchIfMissing = false, havingValue = "true", value = "enable")
public class ZookeeperConfigProperties {


    private String address;
    private int timeout = 500000;


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
