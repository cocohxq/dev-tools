package com.dev.tool.rmi.initializer;

import com.dev.tool.common.initializer.Initializer;
import com.dev.tool.rmi.processor.DubboToolProcessor;

public class DubboToolInitializer implements Initializer {

    private DubboToolProcessor processor;

    @Override
    public void init() {
        processor.reLoad(null);
    }

    public DubboToolProcessor getProcessor() {
        return processor;
    }

    public void setProcessor(DubboToolProcessor processor) {
        this.processor = processor;
    }
}
