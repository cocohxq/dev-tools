package com.dev.tool.common.processor;

import com.dev.tool.common.env.ClassSensitive;
import com.dev.tool.common.model.ClassLoadFromConfig;
import com.dev.tool.common.model.Event;
import com.dev.tool.common.util.ClassLoaderUtils;
import com.dev.tool.common.util.EventEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractClassSensitiveProcessor extends AbstractProcessor implements ClassSensitive {

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Override
    public ClassLoader classLoader(boolean recreate) {
        if (recreate) {
            ClassLoadFromConfig classLoadFromConfig = classLoadFromConfig();
            switch (classLoadFromConfig.getClassLoadFromEnum()) {
                case LOAD_FROM_JAR:
                    return ClassLoaderUtils.initAndGetURLClassLoader(matchGroupToolEnum().getGroupEnum(), classLoadFromConfig.getUrls());
                case LAOD_FROM_STRING:
                case LAOD_FROM_TEXTFILE:
                    return ClassLoaderUtils.initAndGetDevToolClassLoader(matchGroupToolEnum().getGroupEnum(), classLoadFromConfig.getClassPath());
            }
        }
        return ClassLoaderUtils.getClassLoader(matchGroupToolEnum().getGroupEnum());
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

}
