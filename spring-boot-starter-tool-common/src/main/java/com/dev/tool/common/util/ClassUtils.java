package com.dev.tool.common.util;

import com.dev.tool.common.model.InterfaceInfo;
import com.dev.tool.common.model.JarArtifactInfo;
import com.dev.tool.common.model.JarFileLoadInfo;
import com.dev.tool.common.model.JarInfo;
import com.dev.tool.common.model.MethodInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ClassUtils {

    private static Logger logger = LoggerFactory.getLogger(CacheUtils.class);

    /**
     * 加载jar包到
     *
     * @return
     * @throws Exception
     */
    public static JarFileLoadInfo loadJarClassIntoJvm(JarFileLoadInfo jarFileLoadInfo, Map<String, String> loadConfigMap) throws Exception {
        //1.将匹配规则的jar包装载jvm
        loadJarsIntoJvm(jarFileLoadInfo);
        //2.将匹配规则的class信息组装返回
        loadClassWithClassRule(jarFileLoadInfo, loadConfigMap.get("artifactIdIncludeRulePattern"), loadConfigMap.get("artifactIdExcludeRulePattern"), loadConfigMap.get("classRulePattern"));

        return jarFileLoadInfo;
    }

    /**
     * 按规则加载jar包到jvm列表
     *
     * @param jarFileLoadInfo
     * @return
     */
    private static JarFileLoadInfo loadJarsIntoJvm(JarFileLoadInfo jarFileLoadInfo) {
        List<URL> urls = new ArrayList<>();
        for (JarArtifactInfo jarArtifactInfo : jarFileLoadInfo.getRemainedJarFiles()) {
            try {
                urls.add(new File(jarArtifactInfo.getJarFile().getName()).toURI().toURL());
            } catch (Exception e) {
                e.printStackTrace();
                jarFileLoadInfo.getLoadErrorJarFiles().add(jarArtifactInfo);
            }
        }

        try {
            if (urls.size() == 0) {
                return jarFileLoadInfo;
            }
            // 加载jar包到jvm
            URLClassLoader urlClassLoader = (URLClassLoader)Thread.currentThread().getContextClassLoader();
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] { URL.class });
            method.setAccessible(true);
            for(URL url : urls) {
                method.invoke(urlClassLoader, url);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jarFileLoadInfo;
    }


    /**
     * 按规则加载class到jvm:jar包名和class路径符合规则的组装成显示信息
     *
     * @param jarFileLoadInfo
     * @param classRulePattern
     * @return
     */
    private static JarFileLoadInfo loadClassWithClassRule(JarFileLoadInfo jarFileLoadInfo, String artifactIdIncludeRulePattern, String artifactIdExcludeRulePattern, String classRulePattern) {
        if (StringUtils.isEmpty(classRulePattern)) {
            throw new RuntimeException("class装载配置正则表达式不可以为空");
        }
        Pattern includePattern = Pattern.compile(artifactIdIncludeRulePattern, Pattern.CASE_INSENSITIVE);
        Pattern excludePattern = null;
        if (!StringUtils.isEmpty(artifactIdExcludeRulePattern)) {
            excludePattern = Pattern.compile(artifactIdExcludeRulePattern, Pattern.CASE_INSENSITIVE);
        }
        Pattern classPattern = Pattern.compile(classRulePattern);
        Map<String, JarInfo> artifactJarMap = new HashMap<>();
        for (JarArtifactInfo jarArtifactInfo : jarFileLoadInfo.getRemainedJarFiles()) {
            Matcher includeMather = includePattern.matcher(jarArtifactInfo.getArtifactId());
            if ((null != excludePattern && excludePattern.matcher(jarArtifactInfo.getArtifactId()).matches()) || !includeMather.matches()) {
                jarFileLoadInfo.getLoadFilteredJarFiles().add(jarArtifactInfo);
                continue;
            }

            List<InterfaceInfo> interfaceInfos = new ArrayList<>();
            JarFile jarFile = jarArtifactInfo.getJarFile();



            JarInfo jarInfo = null;
            Enumeration<JarEntry> entryEnumeration = jarFile.entries();
            while (entryEnumeration.hasMoreElements()) {
                JarEntry jarEntry = entryEnumeration.nextElement();
                String entryName = jarEntry.getName();
                String classFullName = entryName.replace(".class", "").replace("/", ".");

                if (classPattern.matcher(classFullName).matches()) {
                    try {
                        //类符合规则
                        jarInfo = artifactJarMap.get(jarArtifactInfo.getArtifactId());
                        if (null == jarInfo) {
                            jarInfo = new JarInfo();
                            artifactJarMap.put(jarArtifactInfo.getArtifactId(), jarInfo);
                            jarInfo.setArtifactId(jarArtifactInfo.getArtifactId());
                            jarInfo.setVersion(jarArtifactInfo.getVersion());
                            jarInfo.setJarName(jarArtifactInfo.getJarName());
                            jarFileLoadInfo.getFinalLoadedJarInfos().add(jarInfo);
                        }
                        Class clazz = ClassUtils.forName(classFullName, Thread.currentThread().getContextClassLoader());
                        interfaceInfos.add(loadInterface(clazz));
                    } catch (Throwable e) {
                        logger.error(String.format("class %s load error", classFullName), e);
                    }
                }
            }
            if(null != jarInfo) {
                jarInfo.setInterfaceInfoMap(interfaceInfos.stream().collect(Collectors.toMap(k -> k.getInterfaceName(), v -> v, (v1, v2) -> v1)));
            }
        }
        return jarFileLoadInfo;
    }

    public static InterfaceInfo loadInterface(Class clazz) throws Exception {
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        interfaceInfo.setInterfaceClazz(clazz);
        interfaceInfo.setInterfaceName(clazz.getName());
        Map<String, MethodInfo> methodInfoMap = new HashMap<>();
        interfaceInfo.setMethodInfoMap(methodInfoMap);
        List<Method> methods = loadMethodByClass(interfaceInfo.getInterfaceClazz(), null);
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


    /**
     * 从本地路径加载指定名称的class
     *
     * @throws Exception
     */
    public static Set<String> loadClassByPath(GroupEnum groupEnum, String targetDataPathKey) throws Exception {
        List<String> classNameList = new ArrayList<>();
        String classFilePath = EnvUtil.getDataActualFilePath(groupEnum, targetDataPathKey);
        File file = new File(classFilePath);
        if (file.isDirectory()) {
            for (File classfile : file.listFiles()) {
                if (!classfile.getName().endsWith(".class")) {
                    continue;
                }
                String className = classfile.getName().replace(".class", "");
                classNameList.add(className);
            }
        } else {
            if (!file.getName().endsWith(".class")) {
                return new HashSet<>(0);
            }
            String className = file.getName().replace(".class", "");
        }

        if (classNameList.size() == 0) {
            return new HashSet<>(0);
        }

        Set<String> set = new HashSet<>(classNameList.size());
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        for (String className : classNameList) {
            classLoader.loadClass(className);
            set.add(className);
        }
        return set;
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

    public static Class forName(String fullName, ClassLoader... classLoader) {
        try {
            if (null == classLoader || classLoader.length == 0) {
                return Class.forName(fullName);
            } else {
                return classLoader[0].loadClass(fullName);
            }
        } catch (Exception e) {
            logger.error("获取class异常,fullName:" + fullName, e);
        }
        return null;
    }



}