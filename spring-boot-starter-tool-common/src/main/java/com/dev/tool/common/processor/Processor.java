package com.dev.tool.common.processor;

import com.dev.tool.common.model.Event;
import com.dev.tool.common.model.Result;

public interface Processor {


    /**
     * 相应请求处理
     *
     * @return
     */
    Result process(Event event);
}
