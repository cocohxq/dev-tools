package com.dev.tool.serializer;


import com.dev.tool.cache.serializer.DevToolObjectSerializer;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.objenesis.Objenesis;
import org.springframework.objenesis.ObjenesisStd;


public class ProtobufObjectSerializer extends DevToolObjectSerializer {
    private Objenesis objenesis = new ObjenesisStd();

    @Override
    public Object deserialize(byte[] bytes, Class type) throws SerializationException {
        if (bytes == null) {
            return null;
        } else {
            try {
                Object message = this.objenesis.newInstance(type);
                RuntimeSchema schema = RuntimeSchema.createFrom(type);
                ProtostuffIOUtil.mergeFrom(bytes, message, schema);
                return message;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public byte[] serialize(Object t) throws SerializationException {
        Class clz = t.getClass();
        LinkedBuffer buffer = LinkedBuffer.allocate(512);
        byte[] var5;
        try {
            Schema schema = RuntimeSchema.createFrom(clz);
            var5 = ProtostuffIOUtil.toByteArray(t, schema, buffer);
        } finally {
            buffer.clear();
        }
        return var5;
    }
}
