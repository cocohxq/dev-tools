package com.dev.tool.common.model;

import com.dev.tool.common.util.GroupEnum;

public class Context {

    private GroupEnum groupEnum;
    private Class clazz;

    public GroupEnum getGroupEnum() {
        return groupEnum;
    }

    public void setGroupEnum(GroupEnum groupEnum) {
        this.groupEnum = groupEnum;
    }

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }
}
