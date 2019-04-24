package com.dev.tool.config.initializer;

import com.dev.tool.common.initializer.Initializer;
import com.dev.tool.config.processor.ZookeeperToolProcessor;

public class ZookeeperToolInitializer implements Initializer {

    private ZookeeperToolProcessor processor;
    public static final String LOAD_CFG = "load.cfg";

    @Override
    public void init() {

    }

    public ZookeeperToolProcessor getProcessor() {
        return processor;
    }

    public void setProcessor(ZookeeperToolProcessor processor) {
        this.processor = processor;
    }
}
