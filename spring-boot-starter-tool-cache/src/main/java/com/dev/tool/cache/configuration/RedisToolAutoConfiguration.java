package com.dev.tool.cache.configuration;

import com.dev.tool.cache.initializer.RedisToolInitializer;
import com.dev.tool.cache.processor.RedisToolProcessor;
import com.dev.tool.cache.serializer.DevToolObjectSerializer;
import com.dev.tool.common.model.Event;
import com.dev.tool.common.model.Tool;
import com.dev.tool.common.util.ClassUtils;
import com.dev.tool.common.util.GroupToolEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.StringUtils;
import redis.clients.jedis.JedisPoolConfig;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

@Configuration
@EnableConfigurationProperties(RedisConfigProperties.class)
@ConditionalOnBean(RedisConfigProperties.class)
@ConditionalOnProperty(prefix = "spring.dev.tool.cache.redis", matchIfMissing = false, havingValue = "true", value = "enable")
//spring.tool.enable=true则开启工具
public class RedisToolAutoConfiguration {

    private Logger logger = LoggerFactory.getLogger(RedisToolAutoConfiguration.class);


    @Bean(name="jedisPoolConfig")
    @ConditionalOnBean(RedisConfigProperties.class)
    public JedisPoolConfig initJedisPoolConfig(RedisConfigProperties redisConfigProperties){
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(5);
        config.setTestOnBorrow(true);
        config.setMaxTotal(5);
        config.setMaxWaitMillis(60*1000);
        return config;
    }

    @Bean(name="jedisConnectionFactory")
    @ConditionalOnBean(RedisConfigProperties.class)
    public JedisConnectionFactory initJedisConnectionFactory(RedisConfigProperties redisConfigProperties,JedisPoolConfig config){
        JedisConnectionFactory factory = new JedisConnectionFactory();
        factory.setHostName(redisConfigProperties.getHost());
        factory.setPassword(redisConfigProperties.getPassword());
        factory.setPoolConfig(config);
        factory.setPort(redisConfigProperties.getPort());
        factory.setTimeout(60*1000);
        return factory;
    }

    @Bean(name="redisToolTemplate")
    @ConditionalOnBean(RedisConfigProperties.class)
    public RedisTemplate initRedisTemplate(RedisConfigProperties redisConfigProperties,JedisConnectionFactory jedisConnectionFactory){
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(jedisConnectionFactory);
        //设置key 序列化类
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        //设置value 序列化类
        if (StringUtils.isEmpty(redisConfigProperties.getValueSerializerClass())) {
            redisTemplate.setValueSerializer(new StringRedisSerializer());
        } else {
            try {
                redisTemplate.setValueSerializer((RedisSerializer<?>) (ClassUtils.forName(redisConfigProperties.getValueSerializerClass()).newInstance()));
            } catch (Exception e) {
                redisTemplate.setValueSerializer(new StringRedisSerializer());
                logger.error("初始化redis的KeySerializer异常，换成默认的StringRedisSerializer", e);
            }
        }

        return redisTemplate;

    }


    @Bean(name = "redisToolProcessor")
    public RedisToolProcessor initDubboToolProcessor(@Qualifier("redisToolTemplate") RedisTemplate redisTemplate) {
        RedisToolProcessor redisToolProcessor = new RedisToolProcessor();
        redisToolProcessor.setRedisTemplate(redisTemplate);
        RedisSerializer rs = redisTemplate.getValueSerializer();

        //泛型返回，需要外部类支持
        if(rs instanceof DevToolObjectSerializer){
            redisToolProcessor.setOutClassSupply(true);
        }else{
            redisToolProcessor.setOutClassSupply(false);
        }
        return redisToolProcessor;
    }

    @Bean(name = "redisToolInitializer")
    public RedisToolInitializer initDubboToolInitializer(RedisToolProcessor redisToolProcessor) {
        RedisToolInitializer redisToolInitializer = new RedisToolInitializer();
        redisToolInitializer.setProcessor(redisToolProcessor);
        return redisToolInitializer;
    }

    @Bean(name = "redisTool")
    @ConditionalOnBean(RedisConfigProperties.class)
    public Tool initRedisTool(RedisToolProcessor redisToolProcessor,RedisToolInitializer redisToolInitializer) {
        Tool tool = new Tool(GroupToolEnum.REDIS, redisToolProcessor,redisToolInitializer);
        return tool;
    }

}
