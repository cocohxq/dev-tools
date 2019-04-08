package com.dev.tool.common.util;

import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ConstantUtils {

    private static String RUNNING_PATH = null;
    private static String CONFIG_PATH = null;
    private static String DATA_PATH = null;

    public static void initRuningPath(String runningPath){
        if(null == RUNNING_PATH){
            synchronized (ConstantUtils.class){
                if(null == RUNNING_PATH) {
                    RUNNING_PATH = runningPath;
                }
            }
        }else{
            throw new RuntimeException("运行路径不能覆盖");
        }
    }

    public static void initConfigPath(String configPath){
        if(null == CONFIG_PATH){
            synchronized (ConstantUtils.class){
                if(null == CONFIG_PATH) {
                    CONFIG_PATH = configPath;
                }
            }
        }else{
            throw new RuntimeException("基础配置不能覆盖");
        }
    }

    public static void initDataPath(String dataPath){
        if(null == DATA_PATH){
            synchronized (ConstantUtils.class){
                if(null == DATA_PATH) {
                    DATA_PATH = dataPath;
                }
            }
        }else{
            throw new RuntimeException("基础数据不能覆盖");
        }
    }

    public static String getConfigPath() {
        return CONFIG_PATH;
    }

    public static String getDataPath() {
        return DATA_PATH;
    }

    public static String getRunningPath() {
        return RUNNING_PATH;
    }
}
