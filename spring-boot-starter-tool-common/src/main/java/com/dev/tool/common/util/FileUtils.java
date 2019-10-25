package com.dev.tool.common.util;

import java.io.*;
import java.util.List;
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

    /**
     * 新建目录
     * @param filePath
     * @return
     */
    public static String createDirs(String filePath,boolean coveredOnExisted){
        File file = new File(filePath);
        if(file.exists()){
            if(coveredOnExisted) {
                delete(file.getPath());
            }else{
                return filePath;
            }
        }
        file.mkdirs();
        return filePath;
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


    public static void updateFiles(String dirPath, List<File> files, boolean insertIfAbsent) throws Exception {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            if(insertIfAbsent){
                dir.mkdirs();
            }else{
                throw new FileNotFoundException("文件不存在");
            }
        }


        for(File file : files){
            try {
                File targetFile = new File(concatPath(dir.getPath(),file.getName()));
                if(targetFile.exists()){
                    delete(targetFile.getPath());
                }
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(targetFile));

                byte[] buffer = new byte[1024];
                int length = 0;
                while ((length = bis.read(buffer,0,buffer.length)) > 0){
                    bos.write(buffer,0,length);
                }
                bos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static boolean isFileExists(String filePath){
        return new File(filePath).exists();
    }

    /**
     * 删除指定path文件或文件夹
     * @param path
     */
    public static void delete(String path){
        File file = new File(path);
        if(!file.exists()){
            return;
        }
        if(file.isDirectory()){
            deleteDir(path,true);
        }else{
            file.delete();
        }

    }

    /**
     * 删除文件夹
     * @param path
     * @param includeSelf
     */
    public static void deleteDir(String path,boolean includeSelf){
        File file = new File(path);
        if(!file.exists()){
            return;
        }
        for(File childFile : file.listFiles()){
            if(childFile.isDirectory()){
                deleteDir(childFile.getPath(),true);
            }else{
                childFile.delete();
            }
        }
        //如果包含自身，也删除掉
        if(includeSelf){
            file.delete();
        }
    }
}
