package com.dev.tool.common.model;

import java.util.jar.JarFile;

public class JarArtifactInfo {
    private String artifactId;
    private String version;
    private transient JarFile jarFile;
    private String jarName;

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public JarFile getJarFile() {
        return jarFile;
    }

    public void setJarFile(JarFile jarFile) {
        this.jarFile = jarFile;
    }

    @Override
    public int hashCode() {
        return artifactId.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return artifactId.equals(obj);
    }

    public String getJarName() {
        return jarName;
    }

    public void setJarName(String jarName) {
        this.jarName = jarName;
    }
}
