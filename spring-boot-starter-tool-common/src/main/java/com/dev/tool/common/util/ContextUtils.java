package com.dev.tool.common.util;

import com.dev.tool.common.model.Context;

public class ContextUtils {

    private static ThreadLocal<Context> contextThreadLocal = new ThreadLocal<>();

    public static Context getContext() {
        Context context = contextThreadLocal.get();
        if(null == context){
            contextThreadLocal.set(context);
        }
        return contextThreadLocal.get();
    }

    public static void remove(){
        contextThreadLocal.remove();
    }

}
