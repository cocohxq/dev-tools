package com.dev.tool.common.model;

import java.util.Map;

public class InterfaceInfo {

    private String interfaceName;

    private Map<String, MethodInfo> methodInfoMap;

    private String version;

    private String group;

    private transient Class interfaceClazz;

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public Map<String, MethodInfo> getMethodInfoMap() {
        return methodInfoMap;
    }

    public void setMethodInfoMap(Map<String, MethodInfo> methodInfoMap) {
        this.methodInfoMap = methodInfoMap;
    }

    public Class getInterfaceClazz() {
        return interfaceClazz;
    }

    public void setInterfaceClazz(Class interfaceClazz) {
        this.interfaceClazz = interfaceClazz;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
