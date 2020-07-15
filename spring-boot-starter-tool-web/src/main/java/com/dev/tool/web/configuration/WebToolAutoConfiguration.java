package com.dev.tool.web.configuration;

import com.dev.tool.common.processor.Processor;
import com.dev.tool.common.util.BeanFactoryUtils;
import com.dev.tool.web.service.ToolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnWebApplication
@ConditionalOnProperty(prefix = "spring.dev.tool", matchIfMissing = true, value = "enable")
//spring.tool.enable=true则开启工具
public class WebToolAutoConfiguration {

    private Logger logger = LoggerFactory.getLogger(WebToolAutoConfiguration.class);

    private Processor[] processors;

    public WebToolAutoConfiguration(ObjectProvider<Processor[]> objectProvider) {
        this.processors = objectProvider.getIfAvailable();
    }


    @Bean(name = "toolService")
    public ToolService initToolService(BeanFactoryUtils beanFactoryUtils) {
        ToolService toolService = new ToolService();
        if (null == processors || processors.length == 0) {
            logger.error("没有发现任何工具");
            return toolService;
        }
        for (Processor processor : processors) {
            toolService.buildTool(processor);
        }
        return toolService;
    }

}
