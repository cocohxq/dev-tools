package com.dev.tool.cache.redis.configuration;

import com.dev.tool.cache.redis.processor.RedisToolProcessor;
import com.dev.tool.common.util.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.StringUtils;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
@ConditionalOnBean(RedisConfigProperties.class)
//spring.tool.enable=true则开启工具
public class RedisToolAutoConfiguration {

    private Logger logger = LoggerFactory.getLogger(RedisToolAutoConfiguration.class);


    @Bean(name = "jedisPoolConfig")
    public JedisPoolConfig initJedisPoolConfig(RedisConfigProperties redisConfigProperties) {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(5);
        config.setTestOnBorrow(true);
        config.setMaxTotal(5);
        config.setMaxWaitMillis(60 * 1000);
        return config;
    }

    @Bean(name = "jedisConnectionFactory")
    public JedisConnectionFactory initJedisConnectionFactory(RedisConfigProperties redisConfigProperties, JedisPoolConfig config) {
        JedisConnectionFactory factory = new JedisConnectionFactory();
        factory.setHostName(redisConfigProperties.getHost());
        factory.setPassword(redisConfigProperties.getPassword());
        factory.setPoolConfig(config);
        factory.setPort(redisConfigProperties.getPort());
        factory.setTimeout(60 * 1000);
        return factory;
    }

    @Bean(name = "redisToolTemplate")
    public RedisTemplate initRedisTemplate(RedisConfigProperties redisConfigProperties, JedisConnectionFactory jedisConnectionFactory) {
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
    public RedisToolProcessor initDubboToolProcessor(@Qualifier("redisToolTemplate") RedisTemplate redisTemplate, RedisConfigProperties redisConfigProperties) {
        RedisToolProcessor redisToolProcessor = new RedisToolProcessor();
        redisToolProcessor.setRedisTemplate(redisTemplate);
        redisToolProcessor.setRedisConfigProperties(redisConfigProperties);
        return redisToolProcessor;
    }

}
