package com.dev.tool.auth.configuration;

import com.dev.tool.auth.service.ToolAuthService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(AuthConfigProperties.class)
@ConditionalOnWebApplication
@ConditionalOnProperty(prefix = "spring.dev.tool", matchIfMissing = true, value = "enable")
//spring.tool.enable=true则开启工具
public class AuthAutoConfiguration {


    @Bean(name="toolAuthService")
    @ConditionalOnBean(AuthConfigProperties.class)
    @ConditionalOnClass(value = ToolAuthService.class)
    @ConditionalOnProperty(prefix = "spring.dev.tool", value = {"auth-target", "auth-method"})
    public ToolAuthService initToolAuthService(AuthConfigProperties authConfigProperties) {
        ToolAuthService service = new ToolAuthService();
//        processor.initialize(authConfigProperties.getJmsIbmTarget(), authConfigProperties.getJmsIbmMethod());
        return service;
    }

    


}
