package com.dev.tool.jms.rocketmq.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.dev.tool.jms.rocketmq.producer")
//spring.tool.enable=true则开启工具
@ConditionalOnProperty(prefix = "spring.dev.tool.jms.rocketmq.producer", matchIfMissing = false, havingValue = "true", value = "enable")
public class RocketMqConfigProducerProperties {

    private String namesrvAddr;
    private String producerGroupName;
    private String instanceName;

    private String serializerClass;
    private boolean notJdkClassSupport = false;//是否需要非jdk的外部class来支持序列化，反序列化


    public String getNamesrvAddr() {
        return namesrvAddr;
    }

    public void setNamesrvAddr(String namesrvAddr) {
        this.namesrvAddr = namesrvAddr;
    }

    public String getProducerGroupName() {
        return producerGroupName;
    }

    public void setProducerGroupName(String producerGroupName) {
        this.producerGroupName = producerGroupName;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public String getSerializerClass() {
        return serializerClass;
    }

    public void setSerializerClass(String serializerClass) {
        this.serializerClass = serializerClass;
    }

    public boolean isnotJdkClassSupport() {
        return notJdkClassSupport;
    }

    public void setnotJdkClassSupport(boolean notJdkClassSupport) {
        this.notJdkClassSupport = notJdkClassSupport;
    }
}
