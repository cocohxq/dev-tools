package com.dev.tool.rmi.dubbo.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.dev.tool.rmi.dubbo")
//spring.tool.enable=true则开启工具
@ConditionalOnProperty(prefix = "spring.dev.tool.rmi.dubbo", matchIfMissing = false, havingValue = "true", value = "enable")
public class DubboConfigProperties {

    /*
    spring.dev.tool.rmi.dubbo.registry-address=zookeeper://127.0.0.1:2181
     */


    private String registryAddress;


    public String getRegistryAddress() {
        return registryAddress;
    }

    public void setRegistryAddress(String registryAddress) {
        this.registryAddress = registryAddress;
    }
}
