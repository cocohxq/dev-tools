package com.dev.tool.common.model;

import com.dev.tool.common.initializer.Initializer;
import com.dev.tool.common.processor.Processor;
import com.dev.tool.common.util.GroupToolEnum;

/**
 * 工具
 */
public class Tool {

    private GroupToolEnum groupToolEnum;
    private Processor processor;
    private String toolName;
    private int toolIndex;
    private Initializer initializer;

    public Tool(GroupToolEnum groupToolEnum, Processor processor,Initializer initializer) {
        this.groupToolEnum = groupToolEnum;
        this.processor = processor;
        this.toolName = groupToolEnum.getName();
        this.toolIndex = groupToolEnum.getIndex();
        this.initializer = initializer;
    }

    public GroupToolEnum getGroupToolEnum() {
        return groupToolEnum;
    }

    public void setGroupToolEnum(GroupToolEnum groupToolEnum) {
        this.groupToolEnum = groupToolEnum;
        this.toolName = groupToolEnum.getName();
        this.toolIndex = groupToolEnum.getIndex();
    }

    public Processor getProcessor() {
        return processor;
    }

    public void setProcessor(Processor processor) {
        this.processor = processor;
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
