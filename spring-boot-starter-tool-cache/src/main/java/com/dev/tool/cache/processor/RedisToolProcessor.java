package com.dev.tool.cache.processor;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dev.tool.cache.model.RedisInfo;
import com.dev.tool.common.model.Event;
import com.dev.tool.common.model.Result;
import com.dev.tool.common.processor.AbstractProcessor;
import com.dev.tool.common.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 工具主页控制器
 */
public class RedisToolProcessor extends AbstractProcessor {

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
    public synchronized Result ready(Event event) {
        RedisConnection cn = redisTemplate.getConnectionFactory().getConnection();
        try {
            Properties properties = cn.info();
            Map<String,Object> map = new HashMap<>();
            map.put("redisInfo",new RedisInfo(properties));
            map.put("outClassSupply",outClassSupply);
            map.put("classes",CacheUtils.getRedisClassLoadedSet());
            return ResultUtils.successResult(map);
        } finally {
            if (null != cn) {
                cn.close();
            }
        }
    }

    @Override
    public synchronized Result get(Event event) {
        try {
            Set<String> keySet = redisTemplate.keys(event.getEventData().get("keyStr"));
            return ResultUtils.successResult(null != keySet ? new ArrayList(keySet) : null);
        } catch (Exception e) {
            logger.error("请求cache异常,info:" + JSONObject.toJSONString(event), e);
            return ResultUtils.errorResult(e.toString());
        }
    }

    @Override
    public synchronized Result submit(Event event) {
        try {
            switch (event.getEventSource()) {
                case "query":
                    return query(event);
                case "delete":
                    redisTemplate.delete(event.getEventData().get("key"));
                    return ResultUtils.successResult("已删除");
                case "persist":
                    return persistJavaFileAndCompile(event);
            }
            return ResultUtils.errorResult("不支持的eventSource:"+event.getEventSource());
        } catch (Exception e) {
            logger.error("提交redis操作异常",e);
            return ResultUtils.errorResult("提交redis操作异常");
        }
    }

    private Result persistJavaFileAndCompile(Event event){
        try {
            if(null == event.getEventData().get("sources")){
                return ResultUtils.errorResult("没有找到提交的源码");
            }
            List<String> javaSources = JSONArray.parseArray(event.getEventData().get("sources"),String.class);
            //编译源码并存储
            List<String> classNames = CompileUtil.compile(javaSources,EnvUtil.getActualFilePath(GroupEnum.CACHE,CLASS_DIR));

            Set<String> classNameSet = new HashSet<>(classNames);
            //加载class
            Set<String> loadedClassSet =  ClassUtils.loadClassByPath(EnvUtil.getActualFilePath(GroupEnum.CACHE,CLASS_DIR),classNameSet);
            CacheUtils.getRedisClassLoadedSet().addAll(loadedClassSet.stream().filter(c->{
                Class clazz = ClassUtils.forName(c,ClassUtils.getRedisClassLoader());
                if(null == clazz){
                    throw new RuntimeException(String.format("类%s没有成功初始化",c));
                }
                int md = clazz.getModifiers();
                return !Modifier.isAbstract(md) && !Modifier.isInterface(md) && !clazz.isEnum();
            }).collect(Collectors.toSet()));

            return ResultUtils.successResult(CacheUtils.getRedisClassLoadedSet());
        } catch (Exception e) {
            logger.error("加载异常",e);
            return ResultUtils.errorResult("加载异常",e);
        }

    }

    /**
     * 初始化编译class
     * @return
     */
    public Result initCompile(){
        try {
            Set<String> loadedClassSet =  ClassUtils.loadClassByPath(EnvUtil.getActualFilePath(GroupEnum.CACHE,CLASS_DIR),null);
            CacheUtils.getRedisClassLoadedSet().addAll(loadedClassSet);
            return ResultUtils.successResult("已加载");
        } catch (Exception e) {
            logger.error("初始化加载class异常",e);
            return ResultUtils.errorResult("初始化加载class异常",e);
        }

    }


    private synchronized Result query(Event event) {
        try {
            String key = event.getEventData().get("key");
            String valueClass = event.getEventData().get("valueClass");

//            if(!CacheUtils.getRedisClassLoadedSet().contains(valueClass)){
//                return ResultUtils.successResult(valueClass+"对应源码还未提交到系统编译");
//            }
            ContextUtils.getRedisClassthreadLocal().set(valueClass);

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
}
