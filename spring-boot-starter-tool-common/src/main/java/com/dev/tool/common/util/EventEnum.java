package com.dev.tool.common.util;

public enum EventEnum {


    INIT(0,"页面加载"),GET(3,"页面交互"),SUBMIT(5,"提交");

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
