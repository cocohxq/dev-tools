package com.dev.tool.rmi.initializer;

import com.dev.tool.common.initializer.Initializer;
import com.dev.tool.common.util.EnvUtil;
import com.dev.tool.common.util.FileUtils;
import com.dev.tool.common.util.GroupEnum;
import com.dev.tool.rmi.processor.DubboToolProcessor;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DubboToolInitializer implements Initializer {

    private DubboToolProcessor processor;
    public static final String LOAD_CFG = "load.cfg";

    @Override
    public void init() {
        //初始化加载已存在的lib path
        List<String> list = EnvUtil.getDataNameList(GroupEnum.RMI, "");
        if (null == list) {
            return;
        }
        //遍历每个lib path 根据配置进行初始化
        list.stream().forEach(l -> {
            List<String> jarList = EnvUtil.getDataNameList(GroupEnum.RMI, l);
            if (jarList.contains(LOAD_CFG)) {
                Map<String, String> param = EnvUtil.getConfig(EnvUtil.getActualFilePath(GroupEnum.RMI, FileUtils.concatPath(l,LOAD_CFG)), HashMap.class);
                param.put("init","true");
                processor.load(param);
            }
        });
    }

    public DubboToolProcessor getProcessor() {
        return processor;
    }

    public void setProcessor(DubboToolProcessor processor) {
        this.processor = processor;
    }
}
