package com.dev.tool.common.serializer;

public interface DevToolObjectSerializer {


    Object deserialize(byte[] bytes, Class type) throws Exception;


    public byte[] serialize(Object t) throws Exception;
}
