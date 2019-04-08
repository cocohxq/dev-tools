package com.dev.tool.common.util;

public class ContextUtils {

    private static ThreadLocal<String> redisClassthreadLocal = new ThreadLocal<>();

    public static ThreadLocal<String> getRedisClassthreadLocal() {
        return redisClassthreadLocal;
    }

    public static void setRedisClassthreadLocal(ThreadLocal<String> redisClassthreadLocal) {
        ContextUtils.redisClassthreadLocal = redisClassthreadLocal;
    }

    public static void remove(ThreadLocal threadLocal){
        threadLocal.remove();
    }

}
