package com.dev.tool.web.service;

import com.dev.tool.common.model.Event;
import com.dev.tool.common.model.Result;
import com.dev.tool.common.model.Tool;
import com.dev.tool.common.model.ToolGroup;
import com.dev.tool.common.processor.Processor;
import com.dev.tool.common.util.GroupEnum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ToolService {


    private final Map<Integer, ToolGroup> groupMap = new HashMap<>();
    private final Map<Integer, Processor> toolProcessorMap = new HashMap<>();

    /**
     * 执行请求
     *
     * @param event
     * @return
     */
    public Result execute(Event event) {
        return toolProcessorMap.get(event.getGroupToolEnum().getIndex()).process(event);
    }

    /**
     * 注册工具
     *
     * @param tool
     */
    public void registryTool(Tool tool) {
        GroupEnum groupEnum = tool.getGroupToolEnum().getGroupEnum();
        ToolGroup toolGroup = groupMap.get(groupEnum.getIndex());
        if(null == toolGroup){
            toolGroup = new ToolGroup(groupEnum,new ArrayList<>());
            groupMap.put(groupEnum.getIndex(),toolGroup);
        }
        toolGroup.getToolList().add(tool);
        toolProcessorMap.put(tool.getGroupToolEnum().getIndex(), tool.getProcessor());
    }

    /**
     * 获取工具列表
     *
     * @return
     */
    public List<ToolGroup> getGroupList() {
        List<ToolGroup> list = new ArrayList<>(groupMap.values());
        list.sort((o1, o2) -> o1.getGroupEnum().getOrder() < o2.getGroupEnum().getOrder() ? -1 : 1);
        list.stream().forEach(l -> {
            l.getToolList().sort((o1, o2) -> o1.getGroupToolEnum().getOrder() < o2.getGroupToolEnum().getOrder() ? -1 : 1);
        });
        return list;
    }
}
