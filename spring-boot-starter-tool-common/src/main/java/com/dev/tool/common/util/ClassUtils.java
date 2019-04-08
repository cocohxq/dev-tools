package com.dev.tool.common.util;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.dev.tool.common.model.InterfaceInfo;
import com.dev.tool.common.model.JarInfo;
import com.dev.tool.common.model.MethodInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

public class ClassUtils {

    private static Logger logger = LoggerFactory.getLogger(CacheUtils.class);

    /**
     * 按jar路径加载jar
     * 支持目录、springboot.jar  -api.jar
     *
     * @param jarPath
     * @throws Exception
     */
    public static List<String> loadJarByPath(String jarPath) throws Exception {
        String key = jarPath;
        if (jarPath.indexOf(".jar") != -1 && jarPath.indexOf(File.separator) != -1) {
            key = jarPath.substring(jarPath.lastIndexOf(File.separator) + 1);
        }
        //读取接口类所处的jar文件，此处是path
        File libFile = new File(jarPath);
        List<File> tmpFiles = new ArrayList<>();
        List<URL> urls = new ArrayList<>();
        List<String> jarPaths = new ArrayList<>();
        if (libFile.isDirectory()) {
            //遍历加载所有jar包
            for (File jarFile : libFile.listFiles()) {
                if (!jarFile.getName().endsWith(".jar")) {
                    continue;
                }
                if (new JarFile(jarFile.getPath()).getEntry("BOOT-INF") != null) {
                    continue;
                }
                urls.add(jarFile.toURI().toURL());
                jarPaths.add(jarFile.getPath());
            }
        } else {
            //spring-boot
            JarFile jarFile = new JarFile(jarPath);
            if (jarFile.getEntry("BOOT-INF") != null) {
                String tmpJarPath = jarPath.substring(0, jarPath.lastIndexOf(".jar")) + "/";
                File dir = new File(tmpJarPath);
                if (!dir.exists()) {
                    dir.mkdirs();
                } else {
                    //重置一次
                    Arrays.stream(dir.listFiles()).forEach(l -> l.delete());
                }
                Enumeration<JarEntry> entriesEnum = jarFile.entries();
                String libPath = "BOOT-INF/lib/";
                while (entriesEnum.hasMoreElements()) {
                    JarEntry jarEntry = entriesEnum.nextElement();
                    //只处理/BOOT-INF/lib 以下的文件和目录  不包含目录前缀或者等于目录前缀的都去掉
                    if (!jarEntry.getName().startsWith(libPath) || jarEntry.getName().equals(libPath)) {
                        continue;
                    }
                    copyFileFromInputStream(jarFile.getInputStream(jarEntry), tmpJarPath + jarEntry.getName().replace(libPath, ""));
                }
                jarFile.close();

                for (File file : dir.listFiles()) {
                    tmpFiles.add(file);
                    if (file.getName().endsWith(".jar")) {
                        urls.add(file.toURI().toURL());
                        jarPaths.add(file.getPath());
                    }
                }
            } else {//普通的直接jar包
                File file = new File(jarPath);
                urls.add(file.toURI().toURL());
                jarPaths.add(file.getPath());
            }
        }
        Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        method.setAccessible(true);
        // 获取系统类加载器
        URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        for (URL url : urls) {
            method.invoke(classLoader, url);
        }
        return jarPaths;
    }

    public static List<JarInfo> loadJarInfos(List<String> jarFilePaths, String nameContainStr, String packageName) throws Exception {
        if (null == nameContainStr) {
            nameContainStr = "-client,-api/";
        }
        String[] nameContainStrs = nameContainStr.split("/");
        if (null == nameContainStrs || nameContainStrs.length == 0 || StringUtils.isEmpty(nameContainStrs[0])) {
            throw new RuntimeException("jar包名过滤非法");
        }
        String[] includeStrs = nameContainStrs[0].split(",");
        String[] excludeStrs = new String[0];
        if (nameContainStrs.length > 1) {
            excludeStrs = nameContainStrs[1].split(",");
        }
        if (null == packageName) {
            packageName = "service";
        }
        List<JarInfo> jarInfos = new ArrayList<>();
        for (String path : jarFilePaths) {
            boolean jarPathExclude = false;
            for (String e : excludeStrs) {
                if (path.contains(e)) {
                    jarPathExclude = true;
                    break;
                }
            }

            //jar路径不匹配退出
            if (jarPathExclude) {
                continue;
            }
            boolean jarPathInclude = false;
            for (String s : includeStrs) {
                //如果jar路径匹配
                if (path.contains(s)) {
                    jarPathInclude = true;
                    break;
                }
            }
            //jar路径不匹配退出
            if (!jarPathInclude) {
                continue;
            }
            List<InterfaceInfo> interfaceInfos = loadInterfaceInfo(path, packageName);
            if (null == interfaceInfos || interfaceInfos.size() == 0) {
                continue;
            }
            JarInfo jarInfo = new JarInfo();
            jarInfo.setJarName(path.substring(path.lastIndexOf(File.separator) + 1));
            jarInfo.setInterfaceInfoMap(interfaceInfos.stream().collect(Collectors.toMap(k -> k.getInterfaceName(), v -> v, (v1, v2) -> v1)));
            jarInfos.add(jarInfo);
        }
        return jarInfos;
    }

    //根据jar包后缀来加载相应接口jar包中的接口信息
    public static List<InterfaceInfo> loadInterfaceInfo(String jarPath, String packageName) throws Exception {
        if (null == packageName) {
            packageName = "service";
        }
        List<InterfaceInfo> interfaceInfos = new ArrayList<>();
        String[] packageNames = packageName.split(",");

        JarFile jarFile = new JarFile(jarPath);
        Enumeration<JarEntry> entryEnumeration = jarFile.entries();
        while (entryEnumeration.hasMoreElements()) {
            JarEntry jarEntry = entryEnumeration.nextElement();
            String entryName = jarEntry.getName();
            if (entryName.indexOf("/") != -1 && entryName.indexOf(".class") != -1) {
                String classFileName = entryName.substring(entryName.lastIndexOf("/"));
                boolean classPackageMatch = false;
                //如果末级包路径匹配
                for (String p : packageNames) {
                    String tmpSuffix = "/" + p + "/";//用筛选目录拼一个临时后缀出来用来check
                    if (entryName.indexOf(tmpSuffix) != -1) {
                        classPackageMatch = true;
                        break;
                    }
                }
                //如果包路径不匹配
                if (!classPackageMatch) {
                    continue;
                }
                String classFullName = entryName.replace(".class", "").replace("/", ".");
                try {
                    Class clazz = ClassUtils.forName(classFullName);
                    interfaceInfos.add(loadInterface(clazz));
                } catch (Throwable e) {
                    logger.error(String.format("jar%s加载类%s信息失败,", jarPath, classFullName) + e.toString());
                }
            }
        }
        return interfaceInfos;
    }

    public static InterfaceInfo loadInterface(Class clazz) throws Exception {
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        interfaceInfo.setInterfaceClazz(clazz);
        interfaceInfo.setInterfaceName(clazz.getName());
        Map<String, MethodInfo> methodInfoMap = new HashMap<>();
        interfaceInfo.setMethodInfoMap(methodInfoMap);
        List<Method> methods = loadMethodByClass(clazz, null);
        if (null == methods) {
            return interfaceInfo;
        }
        for (Method method : methods) {
            MethodInfo methodInfo = new MethodInfo();
            methodInfo.setMethod(method);
            methodInfo.setMethodName(method.getName());
            methodInfoMap.put(method.getName(), methodInfo);
            try {
                methodInfo.setParameters(loadMethodParamsByMethod(method));
            } catch (Exception e) {
                methodInfo.setParameters(new Object[0]);
                logger.error(String.format("类%s解析方法%s异常", clazz.getName(), method.getName()), e.toString());
            }
        }
        return interfaceInfo;
    }


    public static List<Method> loadMethodByClass(Class clazz, String methodName) throws Exception {
        return Arrays.stream(clazz.getMethods()).filter(l -> null == methodName || l.getName().equals(methodName)).collect(Collectors.toList());
    }

    public static Object[] loadMethodParamsByMethod(Method method) throws Exception {
        Object[] params = new Object[method.getGenericParameterTypes().length];//可获取带泛型参数的参数
        for (int i = 0, j = method.getGenericParameterTypes().length; i < j; i++) {
            //如果是泛型类型，生成泛型参数
            if (method.getGenericParameterTypes()[i] instanceof ParameterizedType) {
                ParameterizedType t = (ParameterizedType) method.getGenericParameterTypes()[i];
                //泛型一类
                if (null != t.getActualTypeArguments() && t.getActualTypeArguments().length > 0) {
                    Class rawType = (Class) t.getRawType();//type强转为class
                    Class actualType = (Class) t.getActualTypeArguments()[0];
                    if (rawType.getName().endsWith("List")) {
                        List<Object> list = new ArrayList<>();
                        list.add(getInstance(actualType));
                        params[i] = list;
                    } else if (rawType.getName().endsWith("Map")) {
                        Class actualTypeValue = (Class) t.getActualTypeArguments()[1];
                        Map map = new HashMap();
                        map.put(getInstance(actualType), getInstance(actualTypeValue));
                        params[i] = map;
                    } else if (rawType.getName().endsWith("Set")) {
                        Set set = new HashSet();
                        set.add(getInstance(actualType));
                        params[i] = set;
                    } else {
                        //暂时无法解析的参数类型
                        params[i] = rawType.getSimpleName() + "未支持解析";
                    }
                }
            } else {
                params[i] = getInstance((Class) method.getGenericParameterTypes()[i]);
            }
        }
//        System.out.println(method.getName()+":"+JSONObject.toJSONString(params,SerializerFeature.WriteMapNullValue));
        return params;
    }

    public static void copyFileFromInputStream(InputStream is, String targetPath) throws Exception {
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
     * 从本地路径加载指定名称的class
     *
     * @param classFilePath
     * @throws Exception
     */
    public static Set<String> loadClassByPath(String classFilePath, Set<String> classNameSet) throws Exception {
        List<String> classNameList = new ArrayList<>();
        File file = new File(classFilePath);
        if (file.isDirectory()) {
            for (File classfile : file.listFiles()) {
                if (!classfile.getName().endsWith(".class")) {
                    continue;
                }
                String className = classfile.getName().replace(".class", "");
                if (null != classNameSet && !classNameSet.contains(className)) {
                    continue;
                }
                classNameList.add(className);
            }
        } else {
            if (!file.getName().endsWith(".class")) {
                return new HashSet<>(0);
            }
            String className = file.getName().replace(".class", "");
            if (null != classNameSet && !classNameSet.contains(className)) {
                return new HashSet<>(0);
            }
        }

        if (classNameList.size() == 0) {
            return new HashSet<>(0);
        }

        Set<String> set = new HashSet<>(classNameList.size());
        //自定义classLoader从本地class目录查找class然后加载到系统类加载器
        DevToolClassLoader classLoader = DevToolClassLoader.instance(classFilePath);
        for (String className : classNameList) {
            classLoader.loadClass(className);
            set.add(className);
        }
        return set;
    }


    /**
     * 从目录class文件中查找，然后加载到系统类classLoader
     */
    private static class DevToolClassLoader extends ClassLoader {

        private String classFilePath;
        private static DevToolClassLoader classLoader;

        private DevToolClassLoader(String classFilePath) {
            super();
            this.classFilePath = classFilePath;
        }

        public static DevToolClassLoader instance(String classFilePath) {
            if (null == classLoader && null != classFilePath) {
                synchronized (DevToolClassLoader.class) {
                    if (null == classLoader) {
                        classLoader = new DevToolClassLoader(classFilePath);
                    }
                }
            }
            return classLoader;
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            FileInputStream fis = null;
            ByteArrayOutputStream baos = null;
            try {
                fis = new FileInputStream(new File(FileUtils.concatPath(classFilePath, name+".class")));
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


    /**
     * 判断一个对象是否是基本类型或基本类型的封装类型
     */
    public static boolean isPrimitive(Class clazz) {
        try {
            return clazz.isPrimitive() || ((Class<?>) clazz.getField("TYPE").get(null)).isPrimitive();
        } catch (Exception e) {
            return false;
        }
    }

    public static Object getInstance(Class clazz) throws Exception {
        //打印基本类型参数
        if (isPrimitive(clazz)) {
            return getBasicInstance(clazz);
        } else {
            if (clazz == BigDecimal.class) {
                return BigDecimal.ZERO;
            } else if (clazz == Date.class) {
                return "2019-10-10 00:00:00";
            }
            try {
                return clazz.newInstance();
            } catch (Exception e) {
                return clazz.getSimpleName();
            }
        }
    }


    /**
     * 基本类型初始化
     * int、short、float、double、long、boolean、byte、char
     *
     * @param type
     * @return
     */
    public static Object getBasicInstance(Class type) {
        if (type == int.class || type == Integer.class) {
            return 0;
        } else if (type == float.class || type == Float.class) {
            return 0f;
        } else if (type == long.class || type == Long.class) {
            return 0l;
        } else if (type == double.class || type == Double.class) {
            return 0d;
        } else if (type == boolean.class || type == Boolean.class) {
            return true;
        } else if (type == byte.class || type == Byte.class) {
            return "0".getBytes()[0];
        } else if (type == char.class || type == Character.class) {
            return "0".toCharArray()[0];
        }
        return new Object();
    }


    /**
     * 获取classLoader，如果自定义的classLoader为空，默认返回系统类加载器
     *
     * @return
     */
    public static ClassLoader getRedisClassLoader() {
        return DevToolClassLoader.instance(null);
    }

    public static Class forName(String fullName, ClassLoader... classLoader) {
        try {
            if (null == classLoader || classLoader.length == 0) {
                return Class.forName(fullName);
            } else {
                return classLoader[0].loadClass(fullName);
            }
        } catch (Exception e) {
            logger.error("获取class异常,fullName:"+fullName,e);
        }
        return null;
    }


}