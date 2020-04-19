package com.dev.tool.config.zookeeper.model;

import java.util.List;

public class ZookeeperNode {
    private String title;
    private String value;
    private boolean spread;
    private String path;
    private String parentPath;
    private List<ZookeeperNode> children;

    public ZookeeperNode() {
    }

    public ZookeeperNode(String title, String value, boolean spread, String path, String parentPath, List<ZookeeperNode> children) {
        this.title = title;
        this.value = value;
        this.spread = spread;
        this.path = path;
        this.parentPath = parentPath;
        this.children = children;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isSpread() {
        return spread;
    }

    public void setSpread(boolean spread) {
        this.spread = spread;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<ZookeeperNode> getChildren() {
        return children;
    }

    public void setChildren(List<ZookeeperNode> children) {
        this.children = children;
    }

    public String getParentPath() {
        return parentPath;
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }
}
