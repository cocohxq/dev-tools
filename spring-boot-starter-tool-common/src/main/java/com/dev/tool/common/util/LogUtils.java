package com.dev.tool.common.util;

public class LogUtils {

    private static ThreadLocal<StringBuffer> logThreadLocal = new ThreadLocal<>();

    public static void error(String error,Exception e){
        if(null == logThreadLocal.get()){
            logThreadLocal.set(new StringBuffer());
        }
        logThreadLocal.get().append(error);
    }


}
