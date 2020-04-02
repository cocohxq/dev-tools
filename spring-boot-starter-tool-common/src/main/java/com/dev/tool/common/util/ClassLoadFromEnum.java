package com.dev.tool.common.util;

/**
 * 类加载的位置
 */
public enum ClassLoadFromEnum {

    LOAD_FROM_JAR(1,"从jar包加载类",1),LAOD_FROM_TEXTFILE(5,"从文本文件加载类",5),LAOD_FROM_STRING(10,"从字符串中加载类",10);

    ClassLoadFromEnum(Integer index, String name, Integer order) {
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
