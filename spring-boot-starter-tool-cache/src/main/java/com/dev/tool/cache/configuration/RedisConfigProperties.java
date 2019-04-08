package com.dev.tool.cache.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.dev.tool.cache.redis")
public class RedisConfigProperties {

    /**
     * #最大空闲数，数据库连接的最大空闲时间。超过空闲时间，数据库连接将被标记为不可用，然后被释放。设为0表示无限制。
     * redis.maxIdle=300
     * #连接池的最大数据库连接数。设为0表示无限制
     * redis.maxActive=600
     * #最大建立连接等待时间。如果超过此时间将接到异常。设为-1表示无限制。
     * redis.maxWait=1000
     * #在borrow一个jedis实例时，是否提前进行alidate操作；如果为true，则得到的jedis实例均是可用的；
     * redis.testOnBorrow=true
     */

    private String host;
    private int port;
    private String password;

    private String keySerializerClass;
    private String valueSerializerClass;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getKeySerializerClass() {
        return keySerializerClass;
    }

    public void setKeySerializerClass(String keySerializerClass) {
        this.keySerializerClass = keySerializerClass;
    }

    public String getValueSerializerClass() {
        return valueSerializerClass;
    }

    public void setValueSerializerClass(String valueSerializerClass) {
        this.valueSerializerClass = valueSerializerClass;
    }
}
