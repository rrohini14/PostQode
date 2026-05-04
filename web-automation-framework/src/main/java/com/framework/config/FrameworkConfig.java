package com.framework.config;

import org.aeonbits.owner.Config;

@Config.Sources({
        "file:${user.dir}/src/main/resources/config.properties"
})
public interface FrameworkConfig extends Config {

    @Config.Key("base.url")
    String baseUrl();

    @Config.Key("browser")
    @Config.DefaultValue("chrome")
    String browser();

    @Config.Key("headless")
    @Config.DefaultValue("false")
    boolean headless();

    @Config.Key("timeout.seconds")
    @Config.DefaultValue("20")
    int timeoutSeconds();

    @Config.Key("polling.seconds")
    @Config.DefaultValue("1")
    int pollingSeconds();

    @Config.Key("screenshot.on.failure")
    @Config.DefaultValue("true")
    boolean screenshotOnFailure();
}
