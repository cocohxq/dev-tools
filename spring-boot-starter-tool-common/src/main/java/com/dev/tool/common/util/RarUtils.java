package com.dev.tool.common.util;

import com.dev.tool.common.model.JarArtifactInfo;
import com.dev.tool.common.model.JarFileLoadInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 压缩包工具类
 */
public class RarUtils {

    private static Logger logger = LoggerFactory.getLogger(CacheUtils.class);

    private static final String RAR_SUFFIX_WAR = ".war";
    private static final String RAR_SUFFIX_JAR = ".jar";

    private static final String TMP_DIR = "tmp";

    private static final String BOOT_JAR_PATH = "BOOT-INF/lib/";


    /**
     * 从给定的压缩包路径解析出所有jar包列表,并移动到相应工具的工作空间里,相同jar包，高版本覆盖低版本，并装载到jvm
     * 1.支持文件夹路径（解析文件夹及子文件夹下的所有的压缩包）
     * 2.支持springboot的jar包，解析其包含的jar列表
     * 3.支持直接指定jar包
     * 4.不支持war包jar列表解析，TODO;
     * @return
     * @throws Exception
     */
    public static JarFileLoadInfo parseAndInitJarFile(List<String> rarPaths, JarFileLoadInfo jarFileLoadInfo,String targetDataPathKey,Map<String,String> loadConfigMap) throws Exception {
        String tmpPath = null;
        try {
            if(null == rarPaths || rarPaths.isEmpty()){
                throw new RuntimeException("压缩包路径列表为空");
            }

            //1.找到所有jar后缀压缩包，可能包括springboot类型的jar
            List<File> rarFiles = new ArrayList<>();
            Iterator<String> iterator = rarPaths.iterator();
            while(iterator.hasNext()){
                String rarPath = iterator.next();
                File rarFile = new File(rarPath);
                //文件夹解析
                if(rarFile.isDirectory()){
                    List<File> dirRarFiles = parseRarListFromDir(rarFile);
                    if(null != dirRarFiles){
                        rarFiles.addAll(dirRarFiles);
                    }
                }else if(rarFile.getPath().endsWith(RAR_SUFFIX_JAR)){
                    rarFiles.add(rarFile);
                }else{
                    logger.error("暂不支持解析该类型的压缩包,rarPath:"+rarPath);
                }
            }


            //2.解析压缩包成jar包
            if(rarFiles.isEmpty()){
                return null;
            }

            //3.移动到临时目录
            tmpPath = FileUtils.createDirs(FileUtils.concatPath(EnvUtil.getDataActualFilePath(jarFileLoadInfo.getGroupEnum()),TMP_DIR),false);
            List<JarFile> jarFiles = new ArrayList<>();
            for(File file : rarFiles){
                JarFile jarFile = new JarFile(file.getPath());
                //springboot的jar包
                if (jarFile.getEntry("BOOT-INF") != null) {
                    jarFiles.addAll(parseJarFromSpringBootJar(jarFile,tmpPath));
                }else{//普通jar包
                    jarFiles.add(moveJar(jarFile,FileUtils.concatPath(tmpPath,file.getName()),true));
                }
            }

            //4.跟历史已经存在的jar包进行合并存储，返回重复的包和
            List<File> files = EnvUtil.getDataFileList(jarFileLoadInfo.getGroupEnum(),targetDataPathKey);
            List<JarFile> oldJarFiles = files.stream().collect(ArrayList::new,(list,file)->{
                try {
                    list.add(new JarFile(file.getPath()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            },(list1, list2)->list1.addAll(list2));

            //5.按版本合并
            mergeJarFiles(jarFiles,oldJarFiles,jarFileLoadInfo);

            //6.jars整理，删除需要的删除的旧包，新增需要新增的新包
            persistJars(jarFileLoadInfo,targetDataPathKey);
            return jarFileLoadInfo;
        } finally {
            if(null != tmpPath) {
                FileUtils.delete(tmpPath);
            }
        }
    }


    /**
     * 从文件夹下解析压缩包
     * @param dir
     * @return
     */
    private static List<File> parseRarListFromDir(File dir){
        if(!dir.isDirectory()){
            return null;
        }
        List<File> files = new ArrayList<>();
        cascadeParseRarFromDir(files,dir);
        return files;
    }

    /**
     * 级联解析文件夹下的压缩包
     * @param rarFiles
     * @param dir
     */
    private static void cascadeParseRarFromDir(List<File> rarFiles,File dir){
        for (File file : dir.listFiles()) {
            if(file.isDirectory()){
                cascadeParseRarFromDir(rarFiles,file);
            }else if(file.getPath().endsWith(RAR_SUFFIX_JAR)){
                rarFiles.add(file);
            }else{
                logger.error("暂不支持解析该类型的压缩包,rarPath:"+file.getPath());
            }
        }
    }


    /**
     * 从springboot中解析jar包
     * @param bootJarFile
     * @param targetPath
     * @return
     * @throws Exception
     */
    private static List<JarFile> parseJarFromSpringBootJar(JarFile bootJarFile,String targetPath) throws Exception{
        File dir = new File(targetPath);
        if (!dir.exists()) {
            dir.mkdirs();
        } else {
            //重置一次
            Arrays.stream(dir.listFiles()).forEach(l -> l.delete());
        }
        Enumeration<JarEntry> entriesEnum = bootJarFile.entries();
        while (entriesEnum.hasMoreElements()) {
            JarEntry jarEntry = entriesEnum.nextElement();
            //只处理/BOOT-INF/lib 以下的文件和目录  不包含目录前缀或者等于目录前缀的都去掉
            if (!jarEntry.getName().startsWith(BOOT_JAR_PATH) || jarEntry.getName().equals(BOOT_JAR_PATH) || !jarEntry.getName().endsWith(RAR_SUFFIX_JAR)) {
                continue;
            }
            copyFileFromInputStream(bootJarFile.getInputStream(jarEntry), FileUtils.concatPath(targetPath,jarEntry.getName().replace(BOOT_JAR_PATH, "")));
        }
        bootJarFile.close();

        List<JarFile> files = new ArrayList<>();
        for (File file : dir.listFiles()) {
            files.add(new JarFile(file.getPath()));
        }
        return files;
    }

    private static JarFile moveJar(JarFile jarFile,String targetPath,boolean coverOnExisted) throws Exception{
        if(new File(targetPath).exists()){
            if(coverOnExisted){
                FileUtils.delete(targetPath);
            }else{
                throw new RuntimeException("已经存在同名文件");
            }
        }
        copyFileFromInputStream(new FileInputStream(jarFile.getName()),targetPath);
        jarFile.close();
        return new JarFile(targetPath);
    }


    private static void copyFileFromInputStream(InputStream is, String targetPath) throws Exception {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            //字节流
            bis = new BufferedInputStream(is);
            bos = new BufferedOutputStream(new FileOutputStream(new File(targetPath)));
            byte[] buffer = new byte[1024];
            int count = 0;
            while ((count = bis.read(buffer, 0, buffer.length)) > 0) {
                bos.write(buffer, 0, count);
            }
            bos.flush();
        } finally {
            try {
                if (null != bos) {
                    bos.close();
                }
                if (null != bis) {
                    bis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 合并jar包
     * @return
     */
    private static JarFileLoadInfo mergeJarFiles(List<JarFile> newJarFiles,List<JarFile> oldJarFiles,JarFileLoadInfo jarFileLoadInfo){
        Map<String,JarArtifactInfo> oldJarMap = parseJarArtifactInfoMap(oldJarFiles);
        Map<String,JarArtifactInfo> newJarMap = parseJarArtifactInfoMap(newJarFiles);

        jarFileLoadInfo.setNewJarFiles(new ArrayList<>(newJarMap.values()));
        jarFileLoadInfo.setOldJarFiles(new ArrayList<>(oldJarMap.values()));


        for(Map.Entry<String,JarArtifactInfo> newEntry : newJarMap.entrySet()){
            //从旧map中移除
            JarArtifactInfo oldJarArtifactInfo = oldJarMap.remove(newEntry.getKey());
            if(null != oldJarArtifactInfo){
                //新jar包版本大于老的jar包，可以覆盖
                if(newEntry.getValue().getVersion().compareTo(oldJarArtifactInfo.getVersion()) > 0){
                    jarFileLoadInfo.getAddedJarFiles().add(newEntry.getValue());
                    jarFileLoadInfo.getRemainedJarFiles().add(newEntry.getValue());
                    jarFileLoadInfo.getOldRepeatRemovedJarFiles().add(oldJarArtifactInfo);
                    jarFileLoadInfo.getAllRepeatRemovedJarFiles().add(oldJarArtifactInfo);
                }else{
                    jarFileLoadInfo.getAddRepeatRemovedJarFiles().add(newEntry.getValue());
                    jarFileLoadInfo.getAllRepeatRemovedJarFiles().add(newEntry.getValue());
                }
            }else{
                jarFileLoadInfo.getAddedJarFiles().add(newEntry.getValue());
                jarFileLoadInfo.getRemainedJarFiles().add(newEntry.getValue());
            }
        }

        //把剩余的旧的未冲突的jar包统一保留
        if(oldJarMap.size() > 0){
            jarFileLoadInfo.getRemainedJarFiles().addAll(oldJarMap.values());
        }
        return jarFileLoadInfo;
    }


    private static Map<String, JarArtifactInfo> parseJarArtifactInfoMap(List<JarFile> jarFiles){
        Map<String,JarArtifactInfo> jarMap = new HashMap<>(jarFiles.size());
        for(JarFile oldJarFile : jarFiles){
            JarArtifactInfo jarArtifactInfo = new JarArtifactInfo();
            jarArtifactInfo.setJarFile(oldJarFile);
            String fileName = oldJarFile.getName();
            int index  = fileName.lastIndexOf(File.separator);
            if(index != -1){
                fileName = fileName.substring(index+1);
            }
            jarArtifactInfo.setJarName(fileName);
            String[] jarNames = fileName.split("-");
            String artifactId="";
            String version = "";
            Pattern fullVersionPattern = Pattern.compile("\\d+[\\d.]+(SNAPSHOT|RELEASE|FINAL)*",Pattern.CASE_INSENSITIVE);//大小写都可以发现
            Pattern versionSuffixPattern = Pattern.compile("^(SNAPSHOT|RELEASE|FINAL)+");
            for(int i=0;i<jarNames.length;i++){
                String name = jarNames[i];
                name = name.endsWith(".jar")?name.replace(".jar",""):name;
                Matcher fullVersionMatcher = fullVersionPattern.matcher(name);
                Matcher versionSuffixMatcher = versionSuffixPattern.matcher(name);
                if(fullVersionMatcher.matches()){//从字符串头开始找，不是版本号就是名称
                    version = name;
                    if(version.contains("SNAPSHOT") || version.contains("RELEASE")){//一次性找到
                        break;
                    }
                }else if(versionSuffixMatcher.matches()){
                    version+="-";
                    version+=name;
                    break;
                }else {
                    if(i != 0) {
                        artifactId += "-";
                    }
                    artifactId += name;
                }
            }
            jarArtifactInfo.setArtifactId(artifactId);
            jarArtifactInfo.setVersion(version);
            jarMap.put(artifactId,jarArtifactInfo);
        }
        return jarMap;
    }


    /**
     * 获取现成的jar包
     * @param jarFileLoadInfo
     * @param targetDataPathKey
     * @return
     */
    public static JarFileLoadInfo getExistedJarArtifact(JarFileLoadInfo jarFileLoadInfo,String targetDataPathKey){
        File dir = new File(EnvUtil.getDataActualFilePath(jarFileLoadInfo.getGroupEnum(),targetDataPathKey));
        if(!dir.exists() || dir.listFiles().length == 0){
            return jarFileLoadInfo;
        }

        List<JarFile> jarFiles = new ArrayList<>(dir.listFiles().length);
        for(File file : dir.listFiles()){
            try {
                jarFiles.add(new JarFile(file.getPath()));
            } catch (IOException e) {
                logger.error("将file封装成JarFile异常",e);
            }
        }

        if(jarFiles.size() == 0){
            return jarFileLoadInfo;
        }

        Map<String,JarArtifactInfo> newJarMap = parseJarArtifactInfoMap(jarFiles);

        jarFileLoadInfo.setRemainedJarFiles(new ArrayList<>(newJarMap.values()));
        return jarFileLoadInfo;
    }

    /**
     * 持久化包
     * @param jarFileLoadInfo
     * @param targetDataPathKey
     * @return
     */
    public static JarFileLoadInfo persistJars(JarFileLoadInfo jarFileLoadInfo,String targetDataPathKey){
        //删除旧的
        jarFileLoadInfo.getOldRepeatRemovedJarFiles().stream().forEach(l->FileUtils.delete(l.getJarFile().getName()));
        //增加新的
        List<File> fileList = jarFileLoadInfo.getAddedJarFiles().stream().collect(ArrayList::new,(list,j)->list.add(new File(j.getJarFile().getName())),(l1,l2)->l1.addAll(l2));
        EnvUtil.updateDatas(jarFileLoadInfo.getGroupEnum(),targetDataPathKey,fileList,true);
        return jarFileLoadInfo;
    }

}