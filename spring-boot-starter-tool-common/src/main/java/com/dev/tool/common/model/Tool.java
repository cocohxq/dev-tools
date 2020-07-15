package com.dev.tool.common.model;

import com.dev.tool.common.processor.Processor;
import com.dev.tool.common.util.GroupToolEnum;

/**
 * 工具
 */
public class Tool {

    private GroupToolEnum groupToolEnum;
    private String toolName;
    private int toolIndex;

    public Tool(Processor processor) {
        this.groupToolEnum = processor.matchGroupToolEnum();
        this.toolName = groupToolEnum.getName();
        this.toolIndex = groupToolEnum.getIndex();
    }

    public GroupToolEnum getGroupToolEnum() {
        return groupToolEnum;
    }

    public void setGroupToolEnum(GroupToolEnum groupToolEnum) {
        this.groupToolEnum = groupToolEnum;
        this.toolName = groupToolEnum.getName();
        this.toolIndex = groupToolEnum.getIndex();
    }

    public String getToolName() {
        return toolName;
    }

    public void setToolName(String toolName) {
        this.toolName = toolName;
    }

    public int getToolIndex() {
        return toolIndex;
    }

    public void setToolIndex(int toolIndex) {
        this.toolIndex = toolIndex;
    }


}
