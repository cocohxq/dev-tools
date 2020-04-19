package com.dev.tool.cache.redis.processor;

import com.alibaba.fastjson.JSONArray;
import com.dev.tool.cache.redis.model.RedisInfo;
import com.dev.tool.common.model.ClassLoadFromConfig;
import com.dev.tool.common.model.Event;
import com.dev.tool.common.model.Result;
import com.dev.tool.common.processor.AbstractClassSensitiveProcessor;
import com.dev.tool.common.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.*;

/**
 * redis工具
 */
public class RedisToolProcessor extends AbstractClassSensitiveProcessor {

    private Logger logger = LoggerFactory.getLogger(RedisToolProcessor.class);

    private RedisTemplate redisTemplate;

    private final String CLASS_DIR = "classes";

    private boolean outClassSupply=false;//是否需要外部class来支持序列化，反序列化

    @Override
    public synchronized Result before(Event event) {
        //这里可能不同的请求用的不同的db，需要保证线程安全
        String dbStr = event.getEventData().get("dbIndex");
        if(null != dbStr){
            switchRedisDb(Integer.parseInt(dbStr.substring(2)));
        }
        return ResultUtils.successResult(null);
    }

    @Override
    public Result finish(Event event, Result result) {
        return null;
    }

    @Override
    public synchronized Result pageLoad(Event event) {
        RedisConnection cn = null;
        try {
            cn = getConnection(redisTemplate.getConnectionFactory());
            Properties properties = cn.info();
            Map<String,Object> map = new HashMap<>();
            map.put("redisInfo",new RedisInfo(properties));
            map.put("outClassSupply",outClassSupply);
            map.put("classes",CacheUtils.getCacheClassLoadedSet());
            return ResultUtils.successResult(map);
        } catch (Exception e){
            return ResultUtils.errorResult(e.getMessage());
        }finally {
            if (null != cn) {
                cn.close();
            }
        }
    }

    @Override
    public synchronized Result dataLoad(Event event) {
        try {
            switch (event.getEventSource()) {
                case "query":
                    return query(event);
                case "delete":
                    redisTemplate.delete(event.getEventData().get("key"));
                    return ResultUtils.successResult("已删除");
                case "load":
                    return load(event);
                case "keys":
                    Set<String> keySet = redisTemplate.keys(event.getEventData().get("keyStr"));
                    return ResultUtils.successResult(null != keySet ? new ArrayList(keySet) : null);
            }
            return ResultUtils.errorResult("不支持的eventSource:"+event.getEventSource());
        } catch (Exception e) {
            logger.error("提交redis操作异常",e);
            return ResultUtils.errorResult("提交redis操作异常");
        }
    }


    private Result load(Event event){
        try {
            if(null == event.getEventData().get("sources")){
                return ResultUtils.errorResult("没有找到提交的源码");
            }
            List<String> javaSources = JSONArray.parseArray(event.getEventData().get("sources"),String.class);
            //1.编译源码并覆盖存储
            List<String> classNames = CompileUtil.compile(javaSources,EnvUtil.getDataActualFilePath(GroupToolEnum.REDIS,CLASS_DIR));
            //2.reload
            return reLoad(null);
        } catch (Exception e) {
            logger.error("加载异常",e);
            return ResultUtils.errorResult("加载异常",e);
        }

    }

    @Override
    public Result refresh(Event event) {
        try {
            //1.重新加载class
            Set<String> loadedClassSet =  ClassUtils.loadClassByPath(GroupToolEnum.REDIS, CLASS_DIR);
            //2.重置缓存
            CacheUtils.destoryCacheClassLoadedSet();
            CacheUtils.getCacheClassLoadedSet().addAll(loadedClassSet);
            return ResultUtils.successResult(CacheUtils.getCacheClassLoadedSet());
        } catch (Exception e) {
            logger.error("加载异常",e);
            return ResultUtils.errorResult("加载异常",e);
        }
    }


    private synchronized Result query(Event event) {
        try {
            String key = event.getEventData().get("key");
            String valueClass = event.getEventData().get("valueClass");
            ContextUtils.getContext().setClazz(ClassUtils.forName(valueClass,Thread.currentThread().getContextClassLoader()));
            ContextUtils.getContext().setGroupEnum(GroupEnum.CACHE);
            Object o = null;
            //NONE("none"), STRING("string"), LIST("list"), SET("set"), ZSET("zset"), HASH("hash");
            DataType type = redisTemplate.type(key);
            if (DataType.NONE == type) {
                logger.info("key不存在");
            } else if (DataType.STRING == type) {
                o = redisTemplate.opsForValue().get(key);
            } else if (DataType.LIST == type) {
                o =  redisTemplate.opsForList().range(key, 0, -1);
            } else if (DataType.HASH == type) {
                o =  redisTemplate.opsForHash().entries(key);
            } else if (DataType.SET == type) {
                o =  redisTemplate.opsForSet().members(key);//无序
            } else if (DataType.ZSET == type) {
                o =  redisTemplate.opsForZSet().range(key, 0, -1);//有序
            }
            return ResultUtils.successResult(o);
        } catch (Exception e) {
            logger.error("查询错误:{}", e.getMessage());
            return ResultUtils.errorResult("查询异常",e);
        }
    }

    private void switchRedisDb(int dbIndex){
        JedisConnectionFactory factory = (JedisConnectionFactory) redisTemplate.getConnectionFactory();
        factory.setDatabase(dbIndex);
        redisTemplate.setConnectionFactory(factory);
    }


    public RedisTemplate getRedisTemplate() {
        return redisTemplate;
    }

    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean isOutClassSupply() {
        return outClassSupply;
    }

    public void setOutClassSupply(boolean outClassSupply) {
        this.outClassSupply = outClassSupply;
    }

    @Override
    public GroupToolEnum matchGroupToolEnum() {
        return GroupToolEnum.REDIS;
    }

    @Override
    public ClassLoadFromConfig classLoadFromConfig() {
        return new ClassLoadFromConfig(ClassLoadFromEnum.LAOD_FROM_STRING,CLASS_DIR);
    }

    /**
     * RedisConnection是懒加载的，需要在jvm原始类加载器中初始化加载一下，后面其它自定义classLoader就可以使用 （双亲委派可以发现class）
     * @param connectionFactory
     * @return
     * @throws Exception
     */
    private RedisConnection getConnection(RedisConnectionFactory connectionFactory) throws Exception{
        return executeWithAppClassLoader(()->RedisConnectionUtils.getConnection(connectionFactory));
    }
}
