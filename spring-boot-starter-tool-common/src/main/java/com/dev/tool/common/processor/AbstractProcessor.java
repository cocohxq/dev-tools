package com.dev.tool.common.processor;

import com.dev.tool.common.initializer.Initializer;
import com.dev.tool.common.model.Event;
import com.dev.tool.common.model.Result;
import com.dev.tool.common.util.EventEnum;
import com.dev.tool.common.util.LockUtils;
import com.dev.tool.common.util.ResultUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public abstract class AbstractProcessor implements Processor, Initializer {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void init() {
        try {
            Event event = new Event();
            event.setEventData(new HashMap<>());
            event.setGroupToolEnum(this.matchGroupToolEnum());
            event.setEventEnum(EventEnum.RELOAD);
            beforeInit(event);
            process(event);
            afterInit(event);
        } catch (Exception e) {
            logger.error(String.format("工具%s运行环境初始化异常", this.matchGroupToolEnum().getName()), e);
        }
    }

    /**
     * 相应请求处理
     *
     * @return
     */
    public Result process(Event event) {
        Result result = null;
        try {
            //1.准备运行环境
            prepare(event);
            //5.前置处理
            if(!(result=before(event)).isSuccess()){
                return result;
            }
            //10.转发请求
            switch (event.getEventEnum()){
                case PAGELOAD:result= pageLoad(event);break;
                case DATALOAD:result = dataLoad(event);break;
                case RELOAD:result = reLoad(event);break;
                default:ResultUtils.errorResult("不支持的event:" + event.getEventEnum().getName());
            }
            //13.后置处理
            after(event,result);
            return result;
        } finally {
            //15.结束处理
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
    public Result reLoad(Event event){
        try {
            if(!LockUtils.tryLock(event.getGroupToolEnum().getGroupEnum())){
                return ResultUtils.errorResult("正在并发操作中，请稍等");
            }
            return refresh(event);
        } catch (Exception e) {
            logger.error("加载接口，读取方法异常", e);
            return ResultUtils.errorResult("加载接口，读取方法异常," + e.toString());
        }finally {
            LockUtils.unLock(event.getGroupToolEnum().getGroupEnum());
        }
    }

    /**
     * 刷新上下文
     * @param event
     * @return
     */
    public abstract Result refresh(Event event);


    /**
     * 进入前做的动作
     * @param event
     * @return
     */
    public Result before(Event event){return ResultUtils.successResult();}

    public Result after(Event event,Result result){return result;}

    /**
     * 退出时做的动作
     * @param event
     * @param result
     * @return
     */
    public Result finish(Event event,Result result){return result;}

    /**
     * 准备运行环境
     * @param event
     */
    public void prepare(Event event){}

    public void beforeInit(Event event){}

    public void afterInit(Event event){}
}
