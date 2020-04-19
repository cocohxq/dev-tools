package com.dev.tool.common.processor;

import com.dev.tool.common.env.ClassSensitive;
import com.dev.tool.common.model.ClassLoadFromConfig;
import com.dev.tool.common.model.Event;
import com.dev.tool.common.util.ClassLoaderUtils;
import com.dev.tool.common.util.EventEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;


public abstract class AbstractClassSensitiveProcessor extends AbstractProcessor implements ClassSensitive {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    //记录应用启动时的最原始的类加载器，有些地方懒加载需要使用
    private ClassLoader appClassLoader = ClassLoader.getSystemClassLoader();
    //当前线程类加载器
    private ClassLoader currentThreadClassLoader;

    @Override
    public void afterInit(Event event) {
        this.currentThreadClassLoader = Thread.currentThread().getContextClassLoader();
        super.afterInit(event);
    }

    @Override
    public ClassLoader classLoader(boolean recreate) {
        if (recreate) {
            ClassLoadFromConfig classLoadFromConfig = classLoadFromConfig();
            switch (classLoadFromConfig.getClassLoadFromEnum()) {
                case LOAD_FROM_JAR:
                    return ClassLoaderUtils.initAndGetURLClassLoader(matchGroupToolEnum(), classLoadFromConfig.getUrls(), appClassLoader);
                case LAOD_FROM_STRING:
                case LAOD_FROM_TEXTFILE:
                    return ClassLoaderUtils.initAndGetDevToolClassLoader(matchGroupToolEnum(), classLoadFromConfig.getClassPath(), appClassLoader);
            }
        }
        return ClassLoaderUtils.getClassLoader(matchGroupToolEnum());
    }

    public abstract ClassLoadFromConfig classLoadFromConfig();

    /**
     * 准备运行环境
     *
     * @param event
     */
    public void prepare(Event event) {
        boolean recreated = EventEnum.RELOAD.equals(event.getEventEnum()) ? true : false;//如果是重新加载需要重新创建
        Thread.currentThread().setContextClassLoader(classLoader(recreated));
    }

    public ClassLoader getAppClassLoader() {
        return appClassLoader;
    }

    public ClassLoader getCurrentThreadClassLoader() {
        return currentThreadClassLoader;
    }

    /**
     * JVM启动的时候，有部分逻辑是懒加载，在后文中，已经切换到自定义classLoader，会找不到appClassLoader加载的类,所以这部分逻辑需要在这里执行
     * 由appClassLoader加载，后续的自定义classLoader都是appClassLoader的子，双亲委派可以找到class
     * @param callback
     * @param <R>
     * @return
     * @throws Exception
     */
    public <R> R executeWithAppClassLoader(Callable<R> callback) throws Exception{
        try {
            Thread.currentThread().setContextClassLoader(getAppClassLoader());
            return callback.call();
        } finally {
            Thread.currentThread().setContextClassLoader(getCurrentThreadClassLoader());
        }
    }
}
