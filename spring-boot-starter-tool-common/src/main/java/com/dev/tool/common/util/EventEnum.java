package com.dev.tool.common.util;

public enum EventEnum {


    PAGELOAD(0,"页面加载"),DATALOAD(3,"数据交互"),RELOAD(5,"重新加载");

    EventEnum(Integer index, String name) {
        this.name = name;
        this.index = index;
    }

    private String name;
    private Integer index;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }



}
