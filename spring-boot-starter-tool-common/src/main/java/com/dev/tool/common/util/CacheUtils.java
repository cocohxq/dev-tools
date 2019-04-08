package com.dev.tool.common.util;

import com.dev.tool.common.model.JarInfo;

import java.util.*;

public class CacheUtils {

    /**
     * dubbo接口信息
     */
    private static final Map<String, JarInfo> dubboJarInfoHashMap = new HashMap<>();


    /**
     * 已装载的class信息
     */
    private static final Set<String> dubboClassLoadedSet = new HashSet<>();

    /**
     * 已装载的class信息
     */
    private static final Set<String> redisClassLoadedSet = new HashSet<>();

    public static Map<String, JarInfo> getDubboJarInfoHashMap() {
        return dubboJarInfoHashMap;
    }

    public static Set<String> getDubboClassLoadedSet() {
        return dubboClassLoadedSet;
    }

    public static Set<String> getRedisClassLoadedSet() {
        return redisClassLoadedSet;
    }
}
