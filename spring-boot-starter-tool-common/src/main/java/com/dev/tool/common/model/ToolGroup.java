package com.dev.tool.common.model;

import com.dev.tool.common.util.GroupEnum;

import java.util.List;

public class ToolGroup {

    private GroupEnum groupEnum;
    private String groupName;
    private int groupIndex;
    private List<Tool> toolList;

    public ToolGroup(GroupEnum groupEnum, List<Tool> toolList) {
        this.groupEnum = groupEnum;
        this.groupName = groupEnum.getName();
        this.groupIndex = groupEnum.getIndex();
        this.toolList = toolList;
    }

    public List<Tool> getToolList() {
        return toolList;
    }

    public void setToolList(List<Tool> toolList) {
        this.toolList = toolList;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public GroupEnum getGroupEnum() {
        return groupEnum;
    }

    public void setGroupEnum(GroupEnum groupEnum) {
        this.groupEnum = groupEnum;
        this.groupIndex = groupEnum.getIndex();
        this.toolList = toolList;
    }

    public int getGroupIndex() {
        return groupIndex;
    }

    public void setGroupIndex(int groupIndex) {
        this.groupIndex = groupIndex;
    }


}
