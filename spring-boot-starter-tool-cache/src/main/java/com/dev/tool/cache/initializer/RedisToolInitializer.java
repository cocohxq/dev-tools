package com.dev.tool.cache.initializer;

import com.dev.tool.cache.processor.RedisToolProcessor;
import com.dev.tool.common.initializer.Initializer;
import com.dev.tool.common.model.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 工具主页控制器
 */
public class RedisToolInitializer implements Initializer {

    private Logger logger = LoggerFactory.getLogger(RedisToolInitializer.class);

    private RedisToolProcessor processor;

    @Override
    public void init() {
        processor.initCompile();
    }

    public RedisToolProcessor getProcessor() {
        return processor;
    }

    public void setProcessor(RedisToolProcessor processor) {
        this.processor = processor;
    }
}
