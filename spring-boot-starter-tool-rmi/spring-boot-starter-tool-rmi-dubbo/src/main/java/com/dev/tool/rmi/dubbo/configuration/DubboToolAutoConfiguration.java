package com.dev.tool.rmi.dubbo.configuration;

import com.dev.tool.rmi.dubbo.processor.DubboToolProcessor;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ConsumerConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

@Configuration
@ConditionalOnBean(DubboConfigProperties.class)
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
    public RegistryConfig registryConfig(DubboConfigProperties dubboConfigProperties) {
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

    @Bean(name = "dubboToolProcessor")
    public DubboToolProcessor initDubboToolProcessor() {
        DubboToolProcessor dubboToolProcessor = new DubboToolProcessor();
        return dubboToolProcessor;
    }

}
