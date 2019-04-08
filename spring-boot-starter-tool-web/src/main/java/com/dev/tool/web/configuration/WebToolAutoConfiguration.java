package com.dev.tool.web.configuration;

import com.dev.tool.common.model.Tool;
import com.dev.tool.common.util.BeanFactoryUtils;
import com.dev.tool.common.util.GroupToolEnum;
import com.dev.tool.web.service.ToolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
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

    @Bean(name = "toolService")
    @ConditionalOnBean(BeanFactoryUtils.class)
    public ToolService initToolService(BeanFactoryUtils beanFactoryUtils) {
        ToolService toolService = new ToolService();
        for(GroupToolEnum groupToolEnum : GroupToolEnum.values()){
            String toolName = groupToolEnum.getName().toLowerCase()+"Tool";
            Tool tool = null;
            try {
                tool = BeanFactoryUtils.getBean(toolName, Tool.class);
            } catch (Exception e) {
                logger.error("工具"+toolName+"未启用，如需启用，请配置相应的enable=true");
            }
            if(null == tool){
                continue;
            }
            toolService.registryTool(tool);
        }
        return toolService;
    }

}
