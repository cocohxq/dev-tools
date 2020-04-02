package com.dev.tool.common.model;

import com.dev.tool.common.util.ClassLoadFromEnum;

import java.net.URL;

public class ClassLoadFromConfig {

    private ClassLoadFromEnum classLoadFromEnum;
    private String classPath;
    private URL[] urls;

    public ClassLoadFromConfig(ClassLoadFromEnum classLoadFromEnum, URL[] urls) {
        this.classLoadFromEnum = classLoadFromEnum;
        this.urls = urls;
    }

    public ClassLoadFromConfig(ClassLoadFromEnum classLoadFromEnum, String classPath) {
        this.classLoadFromEnum = classLoadFromEnum;
        this.classPath = classPath;
    }

    public ClassLoadFromEnum getClassLoadFromEnum() {
        return classLoadFromEnum;
    }

    public void setClassLoadFromEnum(ClassLoadFromEnum classLoadFromEnum) {
        this.classLoadFromEnum = classLoadFromEnum;
    }

    public String getClassPath() {
        return classPath;
    }

    public void setClassPath(String classPath) {
        this.classPath = classPath;
    }

    public URL[] getUrls() {
        return urls;
    }

    public void setUrls(URL[] urls) {
        this.urls = urls;
    }
}
