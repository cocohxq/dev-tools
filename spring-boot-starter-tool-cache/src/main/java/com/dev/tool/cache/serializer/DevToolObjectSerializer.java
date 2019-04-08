package com.dev.tool.cache.serializer;

import com.dev.tool.common.util.ClassUtils;
import com.dev.tool.common.util.ContextUtils;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

public abstract class DevToolObjectSerializer implements RedisSerializer {


    @Override
    public final Object deserialize(byte[] bytes) throws SerializationException {

        try {
            String valueClass = ContextUtils.getRedisClassthreadLocal().get();
            Class clazz = ClassUtils.forName(valueClass,ClassUtils.getRedisClassLoader());
            if(null == clazz){
                throw new RuntimeException(String.format("根据%s找不到已加载的class",clazz));
            }
            return deserialize(bytes,clazz);
        }catch (Exception e){
            return new String(bytes);//如果无法解析，默认按String类型返回
        }finally {
            ContextUtils.remove(ContextUtils.getRedisClassthreadLocal());
        }
    }



    public abstract Object deserialize(byte[] bytes,Class type) throws SerializationException;
}
