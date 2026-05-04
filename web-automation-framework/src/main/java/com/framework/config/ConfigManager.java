package com.framework.config;

import org.aeonbits.owner.ConfigCache;

public final class ConfigManager {

    private ConfigManager() {}

    public static FrameworkConfig getConfig() {
        return ConfigCache.getOrCreate(FrameworkConfig.class);
    }
}
