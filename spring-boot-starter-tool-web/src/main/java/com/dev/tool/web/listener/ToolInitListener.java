package com.dev.tool.web.listener;

import com.dev.tool.common.initializer.Initializer;
import com.dev.tool.common.util.ConstantUtils;
import com.dev.tool.common.util.EnvUtil;
import com.dev.tool.common.util.FileUtils;
import com.dev.tool.common.util.GroupEnum;
import com.dev.tool.common.util.GroupToolEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Arrays;
import java.util.Map;

@Service
public class ToolInitListener implements ApplicationListener<ContextRefreshedEvent> {

    private Logger logger = LoggerFactory.getLogger(ToolInitListener.class);

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        //初始化环境，建各种目录
        initEnv();
        //初始化各个工具的加载
        initTool(event.getApplicationContext());
    }

    /**
     * 初始化环境
     */
    private void initEnv() {
        try {
            //获取springboot的jar包所在路径
            String running_path = new ApplicationHome(ToolInitListener.class).getSource().getPath();
            //如果不是jar运行，修改路径到用户目录下,设置成开发模式
            if (!running_path.contains(".jar")) {
                running_path = System.getProperty("user.home");
            } else {
                int index = running_path.lastIndexOf(File.separator);
                running_path = running_path.substring(index + 1);
                running_path = running_path.substring(0, index);
            }
            running_path = FileUtils.concatPath(running_path, "dev-tools");
            ConstantUtils.initRuningPath(running_path);
            ConstantUtils.initConfigPath(FileUtils.concatPath(running_path, "config"));
            ConstantUtils.initDataPath(FileUtils.concatPath(running_path, "data"));
            //把相应的工具配置和数据文件夹建起来
            EnvUtil.init();
        } catch (Exception e) {
            logger.error("initEnv启动初始化Env执行异常");
        }

    }


    /**
     * 工具特有的初始化工作
     */
    private void initTool(ApplicationContext applicationContext){
        try {
            Map<String, Initializer> result = applicationContext.getBeansOfType(Initializer.class);
            if(null != result){
                result.values().stream().forEach(initializer ->{
                    try {
                        initializer.init();
                    } catch (Exception e) {
                        logger.error(String.format("%s启动初始化执行异常",initializer.getClass().getName()));
                    }
                });
            }
        } catch (BeansException e) {
            logger.error("initTool,启动初始化执行异常");
        }
    }
}
