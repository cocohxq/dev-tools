package com.dev.tool.common.util;

import com.dev.tool.common.model.JarInfo;

import java.util.*;

public class CacheUtils {

    /**
     * dubbo接口信息
     */
    private static final Map<String, JarInfo> rmiJarInfoHashMap = new HashMap<>();


    /**
     * 已装载的class信息
     */
    private static final Set<String> cacheClassLoadedSet = new HashSet<>();

    public static Map<String, JarInfo> getRmiJarInfoHashMap() {
        return rmiJarInfoHashMap;
    }

    public static boolean destoryRmiJarInfoHashMap() {
        rmiJarInfoHashMap.clear();
        return true;
    }

    public static Set<String> getCacheClassLoadedSet() {
        return cacheClassLoadedSet;
    }

    public static Set<String> destoryCacheClassLoadedSet() {
        return cacheClassLoadedSet;
    }
}
