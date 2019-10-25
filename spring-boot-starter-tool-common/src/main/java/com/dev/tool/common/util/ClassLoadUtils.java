package com.dev.tool.common.util;

import com.dev.tool.common.model.DevToolClassLoader;

import java.util.HashMap;
import java.util.Map;

public class ClassLoadUtils {

    private static Map<GroupEnum, ClassLoader> classLoaderMap = new HashMap<>();


    public static ClassLoader getInstance(GroupEnum groupEnum) {
        return classLoaderMap.get(groupEnum);
    }

    public static ClassLoader initAndGetInstance(GroupEnum groupEnum, String classPath) {
        if (null == groupEnum) {
            throw new RuntimeException("工具组枚举不可以为空");
        }
        String classFilePath = EnvUtil.getDataActualFilePath(groupEnum, classPath);
        if (null == classFilePath) {
            throw new RuntimeException("类文件路径不可以为空");
        }
        ClassLoader classLoader = classLoaderMap.get(groupEnum);

        if (null == classLoader) {
            synchronized (DevToolClassLoader.class) {
                if (null == classLoader) {
                    classLoaderMap.put(groupEnum, new DevToolClassLoader(classFilePath));
                }
            }
        }
        return getInstance(groupEnum);
    }


    public static ClassLoader reSetAndGetInstance(GroupEnum groupEnum, ClassLoader classLoader) {
        if (null == groupEnum) {
            throw new RuntimeException("工具组枚举不可以为空");
        }
        if(null == classLoader){
            throw new RuntimeException("classLoader不可以为空");
        }
        synchronized (DevToolClassLoader.class) {
            classLoaderMap.put(groupEnum, classLoader);
        }
        return getInstance(groupEnum);
    }

}
