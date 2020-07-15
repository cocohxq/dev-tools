package com.dev.tool.jms.rocketmq.configuration;

import com.dev.tool.common.serializer.DevToolObjectSerializer;
import com.dev.tool.common.util.ClassUtils;
import com.dev.tool.jms.rocketmq.processor.RocketMqProducerToolProcessor;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnBean(RocketMqConfigProducerProperties.class)
public class RocketMqToolAutoProducerConfiguration {

    private Logger logger = LoggerFactory.getLogger(RocketMqToolAutoProducerConfiguration.class);


    @Bean(value = "rocketmqRepeaterProducer", initMethod = "start", destroyMethod = "shutdown")
    public DefaultMQProducer rocketmqRepeaterProducer(RocketMqConfigProducerProperties rocketMqConfigProducerProperties) {
        DefaultMQProducer producer = new DefaultMQProducer();
        producer.setProducerGroup(rocketMqConfigProducerProperties.getProducerGroupName());
        producer.setNamesrvAddr(rocketMqConfigProducerProperties.getNamesrvAddr());
        producer.setInstanceName(rocketMqConfigProducerProperties.getInstanceName());
        return producer;
    }


    @Bean(value = "rocketMqProducerToolProcessor")
    public RocketMqProducerToolProcessor rocketMqProducerToolProcessor(DefaultMQProducer rocketmqRepeaterProducer, RocketMqConfigProducerProperties rocketMqConfigProducerProperties) throws Exception {
        RocketMqProducerToolProcessor producerToolProcessor = new RocketMqProducerToolProcessor();
        producerToolProcessor.setDefaultMQProducer(rocketmqRepeaterProducer);
        producerToolProcessor.setRocketMqConfigProducerProperties(rocketMqConfigProducerProperties);
        if (StringUtils.isNoneBlank(rocketMqConfigProducerProperties.getSerializerClass())) {
            producerToolProcessor.setDevToolObjectSerializer((DevToolObjectSerializer) ClassUtils.forName(rocketMqConfigProducerProperties.getSerializerClass()).newInstance());
        }
        return producerToolProcessor;
    }

}
