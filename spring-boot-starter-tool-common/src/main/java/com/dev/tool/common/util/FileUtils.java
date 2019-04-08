package com.dev.tool.common.util;

import java.io.*;
import java.util.stream.Collectors;

public class FileUtils {

    public static String concatPath(String... paths){
        if(paths.length == 0){
            return "";
        }
        StringBuffer sb = new StringBuffer();
        int index = 0;
        for(String path : paths){
            //去除每个path的首尾的路径分隔符  首path不去头/
            if(path.startsWith(File.separator) && index != 0){
                path = path.substring(1);
            }
            //尾path不去末尾/
            if(path.endsWith(File.separator) && index != paths.length-1){
                path = path.substring(0,path.length()-1);
            }
            //添加分隔符
            if(index != 0) {
                sb.append(File.separator);
            }
            sb.append(path);
            index++;
        }
        return sb.toString();
    }

    public static boolean createFile(String filePath) throws Exception{
        File file = new File(filePath);
        String dirPath = file.getParent();
        new File(dirPath).mkdirs();
        file.createNewFile();
        return true;
    }

    public static boolean createDirs(String filePath){
        File file = new File(filePath);
        file.mkdirs();
        return true;
    }

    /**
     * 读文件
     *
     * @param filePath
     * @return
     * @throws Exception
     */
    public static String readFileContent(String filePath) throws Exception {
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        return br.lines().collect(Collectors.joining());
    }

    /**
     * 写文件
     *
     * @param filePath
     * @param info
     * @throws Exception
     */
    public static void writeFileContent(String filePath, String info) throws Exception {
        File file = new File(filePath);
        if (file.exists()) {
            throw new RuntimeException("该配置已存在，不能重复保存");
        }
        file.createNewFile();
        FileWriter fw = new FileWriter(filePath);
        fw.write(info);
        fw.flush();
    }

    /**
     * 更新文件
     *
     * @param filePath
     * @param info
     * @throws Exception
     */
    public static void updateFileContent(String filePath, String info, boolean insertIfAbsent) throws Exception {
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        } else {
            //没有也可以更新
            if (insertIfAbsent) {
                file.createNewFile();
            } else {
                throw new FileNotFoundException("找不到配置");
            }
        }
        FileWriter fw = new FileWriter(filePath);
        fw.write(info);
        fw.flush();
    }

    /**
     * 更新文件
     *
     * @param filePath
     * @throws Exception
     */
    public static void updateFile(String filePath, File file, boolean insertIfAbsent) throws Exception {
        File originFile = new File(filePath);
        if (originFile.exists()) {
            originFile.delete();
        } else {
            //没有也可以更新
            if (insertIfAbsent) {
                new File(originFile.getParent()).mkdirs();
            } else {
                throw new FileNotFoundException("找不到配置");
            }
        }

        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(originFile));

        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = bis.read(buffer,0,buffer.length)) > 0){
            bos.write(buffer,0,length);
        }
        bos.flush();
    }

    public static boolean isFileExists(String filePath){
        return new File(filePath).exists();
    }

}
