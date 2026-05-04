package com.framework.drivers;

import org.openqa.selenium.WebDriver;

public final class DriverManager {

    private DriverManager() {}

    private static final ThreadLocal<WebDriver> DRIVER_THREAD_LOCAL = new ThreadLocal<>();

    public static void setDriver(WebDriver driver) {
        DRIVER_THREAD_LOCAL.set(driver);
    }

    public static WebDriver getDriver() {
        return DRIVER_THREAD_LOCAL.get();
    }

    public static void unload() {
        DRIVER_THREAD_LOCAL.remove();
    }
}
