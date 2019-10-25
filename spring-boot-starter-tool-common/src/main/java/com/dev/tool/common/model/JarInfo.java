package com.dev.tool.common.model;

import java.util.Map;

public class JarInfo {

    private String artifactId;

    private String version;

    private String jarName;

    private Map<String,InterfaceInfo> interfaceInfoMap;

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public Map<String, InterfaceInfo> getInterfaceInfoMap() {
        return interfaceInfoMap;
    }

    public void setInterfaceInfoMap(Map<String, InterfaceInfo> interfaceInfoMap) {
        this.interfaceInfoMap = interfaceInfoMap;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getJarName() {
        return jarName;
    }

    public void setJarName(String jarName) {
        this.jarName = jarName;
    }
}
