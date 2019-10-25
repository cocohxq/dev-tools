package com.dev.tool.common.util;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EnvUtil {
    private static Logger logger = LoggerFactory.getLogger(EnvUtil.class);

    private final static String CFG_SUFFIX = ".cfg";

    public static boolean updateConfig(GroupEnum group, String key, Object data, boolean insertIfAbsent) {
        try {
            FileUtils.updateFileContent(FileUtils.concatPath(ConstantUtils.getConfigPath(), group.toString(), generateCfgKey(key)), JSONObject.toJSONString(data), insertIfAbsent);
            return true;
        } catch (Exception e) {
            logger.error("更新配置异常,key:"+key,e);
            return false;
        }
    }

    public static boolean saveConfig(GroupEnum group, String key, Object data) {
        try {
            FileUtils.writeFileContent(FileUtils.concatPath(ConstantUtils.getConfigPath(), group.toString(), generateCfgKey(key)), JSONObject.toJSONString(data));
            return true;
        } catch (Exception e) {
            logger.error("保存配置异常,key:"+key,e);
            return false;
        }
    }


    public static <T> T getConfig(GroupEnum group, String key, Class<T> clazz) {
        try {
            String info = FileUtils.readFileContent(FileUtils.concatPath(ConstantUtils.getConfigPath(), group.toString(), generateCfgKey(key)));
            if (null == info) {
                return null;
            }
            return JSONObject.parseObject(info, clazz);
        } catch (Exception e) {
            logger.error("获取配置异常,key:"+key,e);
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

    public static List<String> getConfigNameList(GroupEnum group) {
        try {
            File configDir = new File(ConstantUtils.getConfigPath(), group.toString());
            if (!configDir.exists()) {
                return new ArrayList<>();
            }
            return Arrays.asList(configDir.listFiles()).stream().filter(f -> !f.isHidden() && f.isFile()).map(f -> generateKey(f.getName())).collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("获取配置列表异常",e);
            return null;
        }
    }

    private static String generateCfgKey(String key){
        return key+CFG_SUFFIX;
    }

    private static String generateKey(String cfgKey){
        return cfgKey.replace(CFG_SUFFIX,"");
    }



    public static boolean isDataExists(GroupEnum group, String pathKey) {
        try {
            return FileUtils.isFileExists(FileUtils.concatPath(ConstantUtils.getDataPath(), group.toString(), pathKey));
        } catch (Exception e) {
            logger.error("判断配置是否存在异常,key:"+pathKey,e);
            return false;
        }
    }

    public static boolean updateDatas(GroupEnum group, String pathKey, List<File> datas, boolean insertIfAbsent) {
        try {
            FileUtils.updateFiles(FileUtils.concatPath(ConstantUtils.getDataPath(), group.toString(), pathKey),datas , insertIfAbsent);
            return true;
        } catch (Exception e) {
            logger.error("更新配置异常,key:"+pathKey,e);
            return false;
        }
    }

    public static List<File> getDataFileList(GroupEnum group,String pathKey) {
        try {
            File dataDir = new File(FileUtils.concatPath(ConstantUtils.getDataPath(), group.toString(),pathKey));
            if (!dataDir.exists()) {
                return new ArrayList<>();
            }
            return Arrays.asList(dataDir.listFiles()).stream().filter(f -> !f.isHidden()).collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("获取配置列表异常",e);
            return null;
        }
    }

    public static List<String> getDataNameList(GroupEnum group,String pathKey) {
        try {
            File dataDir = new File(FileUtils.concatPath(ConstantUtils.getDataPath(), group.toString(),pathKey));
            if (!dataDir.exists()) {
                return new ArrayList<>();
            }
            return Arrays.asList(dataDir.listFiles()).stream().filter(f -> !f.isHidden()).map(f -> f.getName()).collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("获取配置列表异常",e);
            return null;
        }
    }

    public static File getData(GroupEnum group, String pathKey) {
        try {
            return new File(FileUtils.readFileContent(FileUtils.concatPath(ConstantUtils.getDataPath(), group.toString(), pathKey)));
        } catch (Exception e) {
            logger.error("获取配置异常,key:"+pathKey,e);
            return null;
        }
    }

    public static String getDataActualFilePath(GroupEnum group) {
        try {
            return FileUtils.concatPath(ConstantUtils.getDataPath(), group.toString());
        } catch (Exception e) {
            return null;
        }
    }

    public static String getDataActualFilePath(GroupEnum group, String pathKey) {
        try {
            return FileUtils.concatPath(ConstantUtils.getDataPath(), group.toString(), pathKey);
        } catch (Exception e) {
            logger.error("获取实际路径异常,key:"+pathKey,e);
            return null;
        }
    }

}
