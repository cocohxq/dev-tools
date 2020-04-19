package com.dev.tool.common.model;

import com.dev.tool.common.util.GroupEnum;
import com.dev.tool.common.util.GroupToolEnum;

import java.util.ArrayList;
import java.util.List;

public class JarFileLoadInfo {

    private GroupToolEnum groupToolEnum;

    private List<JarArtifactInfo> newJarFiles = new ArrayList<>();
    private List<JarArtifactInfo> oldJarFiles = new ArrayList<>();
    //所有因重复而移除的jar包
    private List<JarArtifactInfo> allRepeatRemovedJarFiles = new ArrayList<>();
    //新增的jar包中因重复而移除的jar包
    private List<JarArtifactInfo> addRepeatRemovedJarFiles = new ArrayList<>();
    //旧的jar包因重复而移除的jar包
    private List<JarArtifactInfo> oldRepeatRemovedJarFiles = new ArrayList<>();
    //本次新增成功的jar包
    private List<JarArtifactInfo> addedJarFiles = new ArrayList<>();
    //本次所有成功保留的jar包
    private List<JarArtifactInfo> remainedJarFiles = new ArrayList<>();
    //装载时剔除的不符合装载条件的jar包
    private List<JarArtifactInfo> loadFilteredJarFiles = new ArrayList<>();
    //装载出错的jar包
    private List<JarArtifactInfo> loadErrorJarFiles = new ArrayList<>();

    private List<JarInfo> finalLoadedJarInfos = new ArrayList<>();


    public JarFileLoadInfo() {
    }

    public JarFileLoadInfo(GroupToolEnum groupToolEnum) {
        this.groupToolEnum = groupToolEnum;
    }

    public List<JarArtifactInfo> getNewJarFiles() {
        return newJarFiles;
    }

    public void setNewJarFiles(List<JarArtifactInfo> newJarFiles) {
        this.newJarFiles = newJarFiles;
    }

    public List<JarArtifactInfo> getOldJarFiles() {
        return oldJarFiles;
    }

    public void setOldJarFiles(List<JarArtifactInfo> oldJarFiles) {
        this.oldJarFiles = oldJarFiles;
    }

    public List<JarArtifactInfo> getAllRepeatRemovedJarFiles() {
        return allRepeatRemovedJarFiles;
    }

    public void setAllRepeatRemovedJarFiles(List<JarArtifactInfo> allRepeatRemovedJarFiles) {
        this.allRepeatRemovedJarFiles = allRepeatRemovedJarFiles;
    }

    public List<JarArtifactInfo> getAddRepeatRemovedJarFiles() {
        return addRepeatRemovedJarFiles;
    }

    public void setAddRepeatRemovedJarFiles(List<JarArtifactInfo> addRepeatRemovedJarFiles) {
        this.addRepeatRemovedJarFiles = addRepeatRemovedJarFiles;
    }

    public List<JarArtifactInfo> getOldRepeatRemovedJarFiles() {
        return oldRepeatRemovedJarFiles;
    }

    public void setOldRepeatRemovedJarFiles(List<JarArtifactInfo> oldRepeatRemovedJarFiles) {
        this.oldRepeatRemovedJarFiles = oldRepeatRemovedJarFiles;
    }

    public List<JarArtifactInfo> getAddedJarFiles() {
        return addedJarFiles;
    }

    public void setAddedJarFiles(List<JarArtifactInfo> addedJarFiles) {
        this.addedJarFiles = addedJarFiles;
    }

    public List<JarArtifactInfo> getRemainedJarFiles() {
        return remainedJarFiles;
    }

    public void setRemainedJarFiles(List<JarArtifactInfo> remainedJarFiles) {
        this.remainedJarFiles = remainedJarFiles;
    }

    public List<JarArtifactInfo> getLoadFilteredJarFiles() {
        return loadFilteredJarFiles;
    }

    public void setLoadFilteredJarFiles(List<JarArtifactInfo> loadFilteredJarFiles) {
        this.loadFilteredJarFiles = loadFilteredJarFiles;
    }


    public List<JarArtifactInfo> getLoadErrorJarFiles() {
        return loadErrorJarFiles;
    }

    public void setLoadErrorJarFiles(List<JarArtifactInfo> loadErrorJarFiles) {
        this.loadErrorJarFiles = loadErrorJarFiles;
    }

    public List<JarInfo> getFinalLoadedJarInfos() {
        return finalLoadedJarInfos;
    }

    public void setFinalLoadedJarInfos(List<JarInfo> finalLoadedJarInfos) {
        this.finalLoadedJarInfos = finalLoadedJarInfos;
    }

    public GroupToolEnum getGroupToolEnum() {
        return groupToolEnum;
    }

    public void setGroupToolEnum(GroupToolEnum groupToolEnum) {
        this.groupToolEnum = groupToolEnum;
    }
}
