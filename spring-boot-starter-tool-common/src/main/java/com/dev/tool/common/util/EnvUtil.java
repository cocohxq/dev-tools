package com.dev.tool.common.util;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EnvUtil {
    private static Logger logger = LoggerFactory.getLogger(EnvUtil.class);

    private static final Map<GroupToolEnum, String> groupToolDataDirPathMap = new HashMap<>();
    private static final Map<GroupToolEnum, String> groupToolConfigDirPathMap = new HashMap<>();

    private final static String CFG_SUFFIX = ".cfg";


    public static void init() {
        synchronized (EnvUtil.class) {
            //把相应的工具配置和数据文件夹建起来
            Arrays.stream(GroupToolEnum.values()).forEach(l -> {
                String dataPath = FileUtils.concatPath(ConstantUtils.getDataPath(), l.getGroupEnum().name(), l.toString());
                String configPath = FileUtils.concatPath(ConstantUtils.getConfigPath(), l.getGroupEnum().name(), l.toString());
                new File(dataPath).mkdirs();
                new File(configPath).mkdirs();
                groupToolDataDirPathMap.put(l, dataPath);
                groupToolConfigDirPathMap.put(l, configPath);
            });
        }
    }

    public static boolean updateConfig(GroupToolEnum groupTool, String key, Object data, boolean insertIfAbsent) {
        try {
            FileUtils.updateFileContent(FileUtils.concatPath(groupToolConfigDirPathMap.get(groupTool), generateCfgKey(key)), JSONObject.toJSONString(data), insertIfAbsent);
            return true;
        } catch (Exception e) {
            logger.error("更新配置异常,key:" + key, e);
            return false;
        }
    }

    public static boolean saveConfig(GroupToolEnum groupTool, String key, Object data) {
        try {
            FileUtils.writeFileContent(FileUtils.concatPath(groupToolConfigDirPathMap.get(groupTool), generateCfgKey(key)), JSONObject.toJSONString(data));
            return true;
        } catch (Exception e) {
            logger.error("保存配置异常,key:" + key, e);
            return false;
        }
    }


    public static <T> T getConfig(GroupToolEnum groupTool, String key, Class<T> clazz) {
        try {
            String info = FileUtils.readFileContent(FileUtils.concatPath(groupToolConfigDirPathMap.get(groupTool), generateCfgKey(key)));
            if (null == info) {
                return null;
            }
            return JSONObject.parseObject(info, clazz);
        } catch (Exception e) {
            logger.error("获取配置异常,key:" + key, e);
            return null;
        }
    }


    public static <T> T getConfig(String configPath, Class<T> clazz) {
        try {
            String info = FileUtils.readFileContent(configPath);
            if (null == info) {
                return null;
            }

            return JSONObject.parseObject(info, clazz);
        } catch (Exception e) {
            logger.error("获取配置异常,configPath:"+configPath,e);
            return null;
        }
    }

    public static List<String> getConfigNameList(GroupToolEnum groupTool) {
        try {
            File configDir = new File(groupToolConfigDirPathMap.get(groupTool));
            if (!configDir.exists()) {
                return new ArrayList<>();
            }
            return Arrays.asList(configDir.listFiles()).stream().filter(f -> !f.isHidden() && f.isFile()).map(f -> generateKey(f.getName())).collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("获取配置列表异常", e);
            return null;
        }
    }

    private static String generateCfgKey(String key){
        return key+CFG_SUFFIX;
    }

    private static String generateKey(String cfgKey){
        return cfgKey.replace(CFG_SUFFIX,"");
    }


    public static boolean isDataExists(GroupToolEnum groupTool, String pathKey) {
        try {
            return FileUtils.isFileExists(FileUtils.concatPath(groupToolDataDirPathMap.get(groupTool), pathKey));
        } catch (Exception e) {
            logger.error("判断配置是否存在异常,key:" + pathKey, e);
            return false;
        }
    }

    public static boolean updateDatas(GroupToolEnum groupTool, String pathKey, List<File> datas, boolean insertIfAbsent) {
        try {
            FileUtils.updateFiles(FileUtils.concatPath(groupToolDataDirPathMap.get(groupTool), pathKey), datas, insertIfAbsent);
            return true;
        } catch (Exception e) {
            logger.error("更新配置异常,key:" + pathKey, e);
            return false;
        }
    }

    public static List<File> getDataFileList(GroupToolEnum groupTool, String pathKey) {
        try {
            File dataDir = new File(FileUtils.concatPath(groupToolDataDirPathMap.get(groupTool), pathKey));
            if (!dataDir.exists()) {
                return new ArrayList<>();
            }
            return Arrays.asList(dataDir.listFiles()).stream().filter(f -> !f.isHidden()).collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("获取配置列表异常", e);
            return null;
        }
    }

    public static List<String> getDataNameList(GroupToolEnum groupTool, String pathKey) {
        try {
            File dataDir = new File(FileUtils.concatPath(groupToolDataDirPathMap.get(groupTool), pathKey));
            if (!dataDir.exists()) {
                return new ArrayList<>();
            }
            return Arrays.asList(dataDir.listFiles()).stream().filter(f -> !f.isHidden()).map(f -> f.getName()).collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("获取配置列表异常", e);
            return null;
        }
    }

    public static File getData(GroupToolEnum groupTool, String pathKey) {
        try {
            return new File(FileUtils.readFileContent(FileUtils.concatPath(groupToolDataDirPathMap.get(groupTool), pathKey)));
        } catch (Exception e) {
            logger.error("获取配置异常,key:" + pathKey, e);
            return null;
        }
    }

    public static String getDataActualFilePath(GroupToolEnum groupTool) {
        try {
            return groupToolDataDirPathMap.get(groupTool);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getDataActualFilePath(GroupToolEnum groupTool, String pathKey) {
        try {
            return FileUtils.concatPath(groupToolDataDirPathMap.get(groupTool), pathKey);
        } catch (Exception e) {
            logger.error("获取实际路径异常,key:" + pathKey, e);
            return null;
        }
    }

}
