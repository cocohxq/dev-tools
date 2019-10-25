package com.dev.tool.common.processor;

import com.dev.tool.common.model.Event;
import com.dev.tool.common.model.Result;
import com.dev.tool.common.util.ResultUtils;

public abstract class AbstractProcessor implements Processor {


    /**
     * 相应请求处理
     *
     * @return
     */
    public Result process(Event event) {
        Result result = null;
        try {
            if(!(result=before(event)).isSuccess()){
                return result;
            }
            switch (event.getEventEnum()){
                case PAGELOAD:result= pageLoad(event);break;
                case DATALOAD:result = dataLoad(event);break;
                case RELOAD:result = reLoad(event);break;
                default:ResultUtils.errorResult("不支持的event:" + event.getEventEnum().getName());
            }
            return result;
        } finally {
            finish(event,result);
        }
    }


    /**
     * 页面初始化需要拿取的数据
     *
     * @return
     */
    public abstract Result pageLoad(Event event);

    /**
     * 页面中按钮异步交互
     *
     * @return
     */
    public abstract Result dataLoad(Event event);

    /**
     * 重新加载
     * @param event
     * @return
     */
    public abstract Result reLoad(Event event);


    /**
     * 进入前做的动作
     * @param event
     * @return
     */
    public abstract Result before(Event event);

    /**
     * 退出时做的动作
     * @param event
     * @param result
     * @return
     */
    public abstract Result finish(Event event,Result result);
}
