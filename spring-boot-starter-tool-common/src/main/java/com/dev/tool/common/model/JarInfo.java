package com.dev.tool.common.model;

import java.util.Map;

public class JarInfo {

    private String jarName;

    private Map<String,InterfaceInfo> interfaceInfoMap;

    public String getJarName() {
        return jarName;
    }

    public void setJarName(String jarName) {
        this.jarName = jarName;
    }

    public Map<String, InterfaceInfo> getInterfaceInfoMap() {
        return interfaceInfoMap;
    }

    public void setInterfaceInfoMap(Map<String, InterfaceInfo> interfaceInfoMap) {
        this.interfaceInfoMap = interfaceInfoMap;
    }
}
