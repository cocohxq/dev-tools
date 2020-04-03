package com.dev.tool.common.model;

import com.dev.tool.common.util.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * 基于class文件的classLoader
 */
public class DevToolClassLoader extends ClassLoader {

    private String classPath;

    public DevToolClassLoader(String classPath,ClassLoader parent) {
        super(parent);
        this.classPath = classPath;
    }

    private DevToolClassLoader() {
        super();
    }


    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        FileInputStream fis = null;
        ByteArrayOutputStream baos = null;
        try {
            fis = new FileInputStream(new File(FileUtils.concatPath(classPath, name + ".class")));
            baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length = 0;
            while ((length = fis.read(buffer, 0, buffer.length)) > 0) {
                baos.write(buffer, 0, length);
            }
            baos.flush();
            byte[] bytes = baos.toByteArray();
            return defineClass(name, bytes, 0, bytes.length);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != fis) {
                    fis.close();
                }
                if (null != baos) {
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
