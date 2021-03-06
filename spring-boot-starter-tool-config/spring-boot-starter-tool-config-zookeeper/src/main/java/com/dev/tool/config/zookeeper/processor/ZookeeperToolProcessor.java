package com.dev.tool.config.zookeeper.processor;

import com.dev.tool.common.model.Event;
import com.dev.tool.common.model.Result;
import com.dev.tool.common.processor.AbstractProcessor;
import com.dev.tool.common.util.GroupToolEnum;
import com.dev.tool.common.util.ResultUtils;
import com.dev.tool.config.zookeeper.model.ZookeeperNode;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 工具主页控制器
 */
public class ZookeeperToolProcessor extends AbstractProcessor {

    private Logger logger = LoggerFactory.getLogger(ZookeeperToolProcessor.class);

    private ZooKeeper zooKeeper;
    private final String ROOT = "/";

    //返回root节点
    @Override
    public Result pageLoad(Event event) {
        try {
            ZookeeperNode node = new ZookeeperNode();
            node.setTitle(ROOT);
            node.setPath(ROOT);

            return ResultUtils.successResult(Arrays.asList(node));
        } catch (Exception e) {
            logger.error("加载接口，读取方法异常", e);
            return ResultUtils.errorResult("加载接口，读取方法异常," + e.toString());
        }
    }

    @Override
    public Result dataLoad(Event event) {
        switch (event.getEventSource()) {
            case "loadValue":
                return loadValue(event.getEventData().get("path"));
            case "loadChildren":
                return loadChildren(event.getEventData().get("path"));
        }
        return ResultUtils.errorResult("不支持的eventSource:" + event.getEventSource());
    }

    @Override
    public Result refresh(Event event) {
        return null;
    }

    private Result loadValue(String path) {
        try {
            return ResultUtils.successResult(getZkValue(path));
        } catch (Exception e) {
            return ResultUtils.errorResult(e.getMessage());
        }
    }

    private Result loadChildren(String path) {
        try {
            List<String> children = zooKeeper.getChildren(path,false);
            List<ZookeeperNode> nodes = null;
            if(null != children && children.size() > 0){
                nodes = children.stream().collect(ArrayList::new,
                        (list,str)->{
                            ZookeeperNode node = new ZookeeperNode();
                            String title = str;
                            try{
                                title = URLDecoder.decode(str,"UTF-8");
                            }catch (Exception e1){logger.error("转码异常",e1);}
                            node.setTitle(title);
                            node.setSpread(false);
                            if(!path.equals(ROOT)){
                                node.setPath(path+"/"+str);
                            }else{
                                node.setPath(path+str);
                            }
                            node.setParentPath(path);
                            list.add(node);
                        },(l1,l2)->l1.addAll(l2));
            }
            return ResultUtils.successResult(nodes);
        } catch (Exception e) {
            return ResultUtils.errorResult(e.getMessage());
        }
    }

    public ZooKeeper getZooKeeper() {
        return zooKeeper;
    }

    public void setZooKeeper(ZooKeeper zooKeeper) {
        this.zooKeeper = zooKeeper;
    }


    private String getZkValue(String path) throws Exception {
        byte[] data = zooKeeper.getData(path, false, null);
        if (null != data) {
            return URLDecoder.decode(new String(data), "UTF-8");
        } else {
            return "";
        }
    }

    @Override
    public GroupToolEnum matchGroupToolEnum() {
        return GroupToolEnum.ZOOKEEPER;
    }
}
