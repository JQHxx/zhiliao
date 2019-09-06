package com.dev.rexhuang.zhiliao_core.config;

import android.content.Context;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;

import java.util.HashMap;

/**
 * *  created by RexHuang
 * *  on 2019/7/26
 */
public class Configurator {

    @SuppressWarnings("SpellCheckingInspection")
    private static final HashMap<Object, Object> ZHILIAO_CONFIGS = new HashMap<>();

    private Configurator() {

    }

    public static class ConfiguratorHolder {
        public static final Configurator INSTANCE = new Configurator();
    }

    public static Configurator getInstance() {
        return ConfiguratorHolder.INSTANCE;
    }

    public Configurator withAppContext(Context context) {
        ZHILIAO_CONFIGS.put(ConfigKeys.APPLICATION_CONTEXT.name(), context);
        return this;
    }

    public Configurator withApiHost(String api_host) {
        ZHILIAO_CONFIGS.put(ConfigKeys.API_HOST.name(), api_host);
        return this;
    }

    public Configurator withLogTag(String log_tag) {
        ZHILIAO_CONFIGS.put(ConfigKeys.LOG_TAG.name(), log_tag);
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .tag(log_tag)
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));
        return this;
    }


    public boolean isReady() {
        return (boolean) ZHILIAO_CONFIGS.get(ConfigKeys.CONFIG_READY);
    }

    public HashMap<Object, Object> getConfigs() {

        return ZHILIAO_CONFIGS;
    }

    public Configurator config() {
        ZHILIAO_CONFIGS.put(ConfigKeys.CONFIG_READY, true);
        return this;
    }
}
