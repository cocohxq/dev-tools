package com.dev.tool.rmi.configuration;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ConsumerConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.dev.tool.common.model.Tool;
import com.dev.tool.common.util.GroupToolEnum;
import com.dev.tool.rmi.processor.DubboToolProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

@Configuration
@EnableConfigurationProperties(DubboConfigProperties.class)
@ConditionalOnProperty(prefix = "spring.dev.tool.rmi.dubbo", matchIfMissing = false, havingValue = "true", value = "enable")
//spring.tool.enable=true则开启工具
public class DubboToolAutoConfiguration {


    @Bean
    public ApplicationConfig applicationConfig() {
        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig.setName("spring-dev-tool");
        return applicationConfig;
    }

    //<dubbo:registry protocol="zookeeper" address="127.0.0.1:2181"></dubbo:registry>
    @Bean
    @ConditionalOnBean(DubboConfigProperties.class)
    public RegistryConfig registryConfig(com.dev.tool.rmi.configuration.DubboConfigProperties dubboConfigProperties) {
        String addr = dubboConfigProperties.getRegistryAddress();
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setProtocol("zookeeper");
        if (addr.indexOf("://") != -1) {
            String[] addrs = addr.split("://");
            registryConfig.setProtocol(addrs[0]);
            registryConfig.setAddress(addrs[1]);
        }
        registryConfig.setCheck(false);
        return registryConfig;
    }

    @Bean
    public ConsumerConfig consumerConfig() {
        ConsumerConfig consumerConfig = new ConsumerConfig();
        consumerConfig.setTimeout(60000);
        consumerConfig.setCheck(false);
        return consumerConfig;
    }


    @Bean(name = "referenceConfig")
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.NO)
    public ReferenceConfig referenceConfig(ConsumerConfig consumerConfig, ApplicationConfig applicationConfig, RegistryConfig registryConfig) {
        ReferenceConfig referenceConfig = new ReferenceConfig();
        referenceConfig.setConsumer(consumerConfig);
        referenceConfig.setApplication(applicationConfig);
        referenceConfig.setRegistry(registryConfig);
        referenceConfig.setProtocol("dubbo");
        referenceConfig.setTimeout(60000);
        return referenceConfig;
    }

    @Bean(name="dubboToolProcessor")
    public DubboToolProcessor initDubboToolProcessor(){
        DubboToolProcessor dubboToolProcessor = new DubboToolProcessor();
        return dubboToolProcessor;
    }

    @Bean(name = "dubboTool")
    public Tool initDubboTool(DubboToolProcessor dubboToolProcessor) {
        Tool tool = new Tool(GroupToolEnum.DUBBO, dubboToolProcessor);
        return tool;
    }

}
