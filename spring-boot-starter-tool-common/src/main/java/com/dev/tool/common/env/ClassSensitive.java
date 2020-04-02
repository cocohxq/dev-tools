package com.dev.tool.common.env;

/**
 * 对类(class)敏感
 *
 * 针对dubbo、redis等需要序列化和反序列化的工具，与class需要交互，是类敏感的
 * 针对mysql、zk等，不关心class，不需要交互，非类敏感，不需要加
 *
 *
 * 加上该接口：会切换classLoader
 */
public interface ClassSensitive {


    /**
     * 工具运行的classLoader
     *
     * @return
     */
    ClassLoader classLoader(boolean recreate);

}
