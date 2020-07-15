package com.dev.tool.jms.rocketmq.processor;

import com.alibaba.fastjson.JSONObject;
import com.dev.tool.common.model.ClassLoadFromConfig;
import com.dev.tool.common.model.Event;
import com.dev.tool.common.model.Result;
import com.dev.tool.common.processor.AbstractClassSensitiveProcessor;
import com.dev.tool.common.serializer.DevToolObjectSerializer;
import com.dev.tool.common.util.CacheUtils;
import com.dev.tool.common.util.ClassLoadFromEnum;
import com.dev.tool.common.util.ClassUtils;
import com.dev.tool.common.util.ContextUtils;
import com.dev.tool.common.util.GroupEnum;
import com.dev.tool.common.util.GroupToolEnum;
import com.dev.tool.common.util.ResultUtils;
import com.dev.tool.jms.rocketmq.configuration.RocketMqConfigProducerProperties;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * rocketmq工具
 */
public class RocketMqProducerToolProcessor extends AbstractClassSensitiveProcessor {

    private final String CLASS_DIR = "classes";
    private Logger logger = LoggerFactory.getLogger(RocketMqProducerToolProcessor.class);
    private DefaultMQProducer defaultMQProducer;
    private DevToolObjectSerializer devToolObjectSerializer;
    private RocketMqConfigProducerProperties rocketMqConfigProducerProperties;

    @Override
    public synchronized Result before(Event event) {
        return ResultUtils.successResult(null);
    }

    @Override
    public Result finish(Event event, Result result) {
        return null;
    }

    @Override
    public synchronized Result pageLoad(Event event) {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("notJdkClassSupport", rocketMqConfigProducerProperties.isnotJdkClassSupport());
            map.put("classes", CacheUtils.getJmsClassLoadedSet());
            return ResultUtils.successResult(map);
        } catch (Exception e) {
            return ResultUtils.errorResult(e.getMessage());
        }
    }

    @Override
    public synchronized Result dataLoad(Event event) {
        try {
            switch (event.getEventSource()) {
                case "send":
                    return send(event);
            }
            return ResultUtils.errorResult("不支持的eventSource:" + event.getEventSource());
        } catch (Exception e) {
            logger.error("提交rocketmq操作异常", e);
            return ResultUtils.errorResult("提交redis操作异常");
        }
    }

    @Override
    public Result refresh(Event event) {
        try {
            if (rocketMqConfigProducerProperties.isnotJdkClassSupport()) {
                //1.重新加载class
                Set<String> loadedClassSet = ClassUtils.loadClassByPath(GroupToolEnum.ROCKETMQ, CLASS_DIR);
                //2.重置缓存
                CacheUtils.destoryJmsClassLoadedSet();
                CacheUtils.getJmsClassLoadedSet().addAll(loadedClassSet);
                return ResultUtils.successResult(CacheUtils.getJmsClassLoadedSet());
            }
            return ResultUtils.successResult();
        } catch (Exception e) {
            logger.error("加载异常", e);
            return ResultUtils.errorResult("加载异常", e);
        }
    }


    private synchronized Result send(Event event) {
        try {
            String topic = event.getEventData().get("topic");
            String tags = event.getEventData().get("tags");
            String msg = event.getEventData().get("msg");


            byte[] data = null;
            if (null != devToolObjectSerializer) {
                String valueClass = event.getEventData().get("valueClass");
                valueClass = StringUtils.isBlank(valueClass) ? String.class.getName() : valueClass;
                Class clazz = ClassUtils.forName(valueClass, Thread.currentThread().getContextClassLoader());
                ContextUtils.getContext().setClazz(clazz);
                ContextUtils.getContext().setGroupEnum(GroupEnum.JMS);
                if (msg.startsWith("[") && msg.endsWith("]")) {
                    data = devToolObjectSerializer.serialize(JSONObject.parseArray(msg, clazz));
                } else {
                    data = devToolObjectSerializer.serialize(JSONObject.parseObject(msg, clazz));
                }
            } else {
                data = msg.getBytes(Charset.forName("UTF-8"));
            }

            Message message = new Message(topic, StringUtils.isBlank(tags) ? null : tags, data);
            SendResult sendResult = defaultMQProducer.send(message);
            return ResultUtils.successResult(sendResult.getSendStatus().toString());
        } catch (Exception e) {
            logger.error("发送错误:{}", e.getMessage());
            return ResultUtils.errorResult("发送异常", e);
        }
    }

    @Override
    public GroupToolEnum matchGroupToolEnum() {
        return GroupToolEnum.ROCKETMQ;
    }

    @Override
    public ClassLoadFromConfig classLoadFromConfig() {
        return new ClassLoadFromConfig(ClassLoadFromEnum.LAOD_FROM_STRING, CLASS_DIR);
    }

    public DefaultMQProducer getDefaultMQProducer() {
        return defaultMQProducer;
    }

    public void setDefaultMQProducer(DefaultMQProducer defaultMQProducer) {
        this.defaultMQProducer = defaultMQProducer;
    }

    public DevToolObjectSerializer getDevToolObjectSerializer() {
        return devToolObjectSerializer;
    }

    public void setDevToolObjectSerializer(DevToolObjectSerializer devToolObjectSerializer) {
        this.devToolObjectSerializer = devToolObjectSerializer;
    }

    public RocketMqConfigProducerProperties getRocketMqConfigProducerProperties() {
        return rocketMqConfigProducerProperties;
    }

    public void setRocketMqConfigProducerProperties(RocketMqConfigProducerProperties rocketMqConfigProducerProperties) {
        this.rocketMqConfigProducerProperties = rocketMqConfigProducerProperties;
    }
}
