package com.dev.tool.common.util;

public enum GroupEnum {

    RMI(1,"远程调用工具",1),CACHE(5,"缓存工具",5),JMS(10,"消息工具",10);

    GroupEnum(Integer index, String name,Integer order) {
        this.name = name;
        this.index = index;
        this.order = order;
    }

    private String name;
    private Integer index;
    private Integer order;

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

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }
}
