package com.dev.tool.rmi.processor;

import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dev.tool.common.model.*;
import com.dev.tool.common.processor.AbstractProcessor;
import com.dev.tool.common.util.*;
import com.dev.tool.rmi.initializer.DubboToolInitializer;
import com.dev.tool.rmi.model.DubboInvokeConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.*;

/**
 * 工具主页控制器
 */
public class DubboToolProcessor extends AbstractProcessor {

    private Logger logger = LoggerFactory.getLogger(DubboToolProcessor.class);

    //返回已加载好的数据
    @Override
    public Result ready(Event event) {
        try {
            return ResultUtils.successResult(new ArrayList<>(CacheUtils.getDubboJarInfoHashMap().values()));
        } catch (Exception e) {
            logger.error("加载接口，读取方法异常", e);
            return ResultUtils.errorResult("加载接口，读取方法异常," + e.toString());
        }
    }

    @Override
    public Result get(Event event){
        switch (event.getEventSource()){
            case "loadParam" : return load(event.getEventData());
            case "jarName" : return ResultUtils.successResult(new ArrayList(CacheUtils.getDubboJarInfoHashMap().get(event.getEventData().get("jarName")).getInterfaceInfoMap().keySet()));
            case "interfaceClassName" :return ResultUtils.successResult(CacheUtils.getDubboJarInfoHashMap().get(event.getEventData().get("jarName"))
                    .getInterfaceInfoMap().get(event.getEventData().get("interfaceClassName")));
            case "methodName":return ResultUtils.successResult(CacheUtils.getDubboJarInfoHashMap().get(event.getEventData().get("jarName"))
                    .getInterfaceInfoMap().get(event.getEventData().get("interfaceClassName"))
                    .getMethodInfoMap().get(event.getEventData().get("methodName")).getParameters());
        }
        return ResultUtils.errorResult("不支持的eventSource:"+event.getEventSource());
    }

    @Override
    public Result submit(Event event){
        DubboInvokeConfig dubboInvokeConfig = JSONObject.parseObject(event.getEventData().get("param"), DubboInvokeConfig.class);
        return dubboInvoke(dubboInvokeConfig);
    }

    public Result dubboInvoke(DubboInvokeConfig dubboInvokeConfig) {
        if (null == dubboInvokeConfig || null == dubboInvokeConfig.getInterfaceClassName() || null == dubboInvokeConfig.getMethodName() || null == dubboInvokeConfig.getParams() || null == dubboInvokeConfig.getVersion()) {
            return ResultUtils.errorResult("入参非法");
        }

        //缓存service的version和group
        InterfaceInfo info = CacheUtils.getDubboJarInfoHashMap().get(dubboInvokeConfig.getJarName()).getInterfaceInfoMap().get(dubboInvokeConfig.getInterfaceClassName());
        info.setGroup(dubboInvokeConfig.getGroup());
        info.setVersion(dubboInvokeConfig.getVersion());
        try {
            return ResultUtils.successResult(invoke(dubboInvokeConfig));
        } catch (Exception e) {
            logger.error("调用异常", e);
            return ResultUtils.errorResult("调用异常," + e.toString());
        }
    }

    //load新环境
    public Result load(Map<String, String> param) {
        try {
            String jarPath = param.get("jarPath");
            String nameContainStr = param.get("nameContainStr");
            String packageName = param.get("packageName");
            //jar需要解析成名称
            final String pathName = jarPath.indexOf(File.separator) != -1?jarPath.substring(jarPath.lastIndexOf(File.separator)+1):jarPath;
            List<String> jarFilePaths = ClassUtils.loadJarByPath(jarPath);//从jar路径下解析的依赖jar包
            if(null == jarFilePaths || 0 == jarFilePaths.size()){
                return ResultUtils.errorResult("没有找到有效接口jar包");
            }
            List<JarInfo> jarInfos = ClassUtils.loadJarInfos(jarFilePaths,nameContainStr,packageName);
            Set<String> jarNameSet = new HashSet<>(jarInfos.size());
            List<String> repeatInterface = new ArrayList<>();//重复加载的class
            jarInfos.stream().forEach(l -> {
                //保存配置
                EnvUtil.updateConfig(GroupEnum.RMI,l.getJarName(),l,true);
                CacheUtils.getDubboJarInfoHashMap().put(l.getJarName(),l);
                jarNameSet.add(l.getJarName());
                Map<String,InterfaceInfo> iiMap =  l.getInterfaceInfoMap();
                iiMap.values().stream().forEach(ii -> {
                    //判断是否已经加载过,加载过的需要重启才能生效
                    if(CacheUtils.getDubboClassLoadedSet().contains(ii.getInterfaceClazz().getName())){
                        repeatInterface.add(ii.getInterfaceClazz().getName());
                    }else{
                        CacheUtils.getDubboClassLoadedSet().add(ii.getInterfaceClazz().getName());
                    }
                });
            });

            if(!param.containsKey("init")){
                for(String jarFilePath : jarFilePaths){
                    String jarName = jarFilePath.substring(jarFilePath.lastIndexOf(File.separator)+1);
                    if(!jarNameSet.contains(jarName)){
                        continue;
                    }
                    EnvUtil.updateData(GroupEnum.RMI,FileUtils.concatPath(pathName,jarName),new File(jarFilePath),true);
                }
                //放入加载配置
                EnvUtil.updateData(GroupEnum.RMI,FileUtils.concatPath(pathName, DubboToolInitializer.LOAD_CFG),param,true);
            }
            if(repeatInterface.size() > 0){
                throw new RuntimeException("以下接口类已加载过，无法重新加载，重启生效:"+JSONArray.toJSONString(repeatInterface));
            }
            return ResultUtils.successResult(new ArrayList<>(CacheUtils.getDubboJarInfoHashMap().keySet()));
        } catch (Exception e) {
            logger.error("加载接口，读取方法异常", e);
            return ResultUtils.errorResult("加载接口，读取方法异常," + e.toString());
        }
    }


    /**
     * 调用
     *
     * @param config
     * @return
     */
    public Object invoke(DubboInvokeConfig config) throws Exception {
        ReferenceConfig referenceConfig = BeanFactoryUtils.getBean("referenceConfig", ReferenceConfig.class);
        referenceConfig.setInterface(ClassUtils.forName(config.getInterfaceClassName()));
        referenceConfig.setGroup(config.getGroup());
        referenceConfig.setVersion(config.getVersion());
        Object service = referenceConfig.get();
        MethodInfo methodInfo = CacheUtils.getDubboJarInfoHashMap().get(config.getJarName()).getInterfaceInfoMap().get(config.getInterfaceClassName()).getMethodInfoMap().get(config.getMethodName());
        return remoteInvoke(service,methodInfo.getMethod(),config.getParams());
    }


    private Object remoteInvoke(Object service, Method callMethod, String[] paramValues) throws Exception {
        if (callMethod.getParameterTypes().length > 0) {
            Object[] params = new Object[callMethod.getParameterTypes().length];
            for (int i = 0, j = callMethod.getParameterTypes().length; i < j; i++) {
                //返回的是java.util.List,可以通过这个判断是否集合
                Class t = callMethod.getParameterTypes()[i];
                String s = paramValues[i];
                //数组或者list
                if (t.isAssignableFrom(List.class) || t.isArray()) {
                    //返回的是带有泛型类型的参数格式,可以取到泛型类型  java.util.List<java.util.Long>
                    ParameterizedType p = (ParameterizedType) callMethod.getGenericParameterTypes()[i];
                    //这里需要强制将Type转为Class，因为json没有提供单个Type的方法
                    List list = JSONArray.parseArray(s, (Class) p.getActualTypeArguments()[0]);
                    params[i] = list;
                    if (t.isArray()) {
                        params[i] = list.toArray();
                    }
                } else {
                    params[i] = JSONObject.parseObject(s, t);
                }
            }
            return callMethod.invoke(service, params);
        } else {
            return callMethod.invoke(service);
        }
    }

    @Override
    public Result before(Event event) {
        return ResultUtils.successResult(null);
    }

    @Override
    public Result finish(Event event, Result result) {
        return result;
    }
}
