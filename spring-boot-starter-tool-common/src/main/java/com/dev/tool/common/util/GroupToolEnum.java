package com.dev.tool.common.util;

public enum GroupToolEnum {

    DUBBO(10,"dubbo",GroupEnum.RMI,10),REDIS(20,"redis",GroupEnum.CACHE,10);

    GroupToolEnum(Integer index, String name,GroupEnum groupEnum,Integer order) {
        this.name = name;
        this.index = index;
        this.groupEnum = groupEnum;
        this.order = order;

    }

    private String name;
    private Integer index;
    private GroupEnum groupEnum;
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

    public GroupEnum getGroupEnum() {
        return groupEnum;
    }

    public void setGroupEnum(GroupEnum groupEnum) {
        this.groupEnum = groupEnum;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }
}
