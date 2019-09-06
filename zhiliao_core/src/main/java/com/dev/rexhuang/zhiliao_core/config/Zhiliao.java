package com.dev.rexhuang.zhiliao_core.config;

import java.util.HashMap;

/**
 * *  created by RexHuang
 * *  on 2019/7/26
 */
public class Zhiliao {
    public static Configurator getConfigurator() {
        return Configurator.getInstance();
    }

    public static HashMap<Object, Object> getConfigs(){
        return getConfigurator().getConfigs();
    }

    public static <T> T getConfig(Object key){
        return (T) getConfigs().get(key);
    }
}
