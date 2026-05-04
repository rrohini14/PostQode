package com.framework.utils;

import com.framework.config.ConfigManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

import java.time.Duration;
import java.util.function.Function;

public final class WaitUtils {

    private WaitUtils() {}

    public static <T> T waitUntil(WebDriver driver, Function<WebDriver, T> condition) {
        Wait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(ConfigManager.getConfig().timeoutSeconds()))
                .pollingEvery(Duration.ofSeconds(ConfigManager.getConfig().pollingSeconds()))
                .ignoring(RuntimeException.class);
        return wait.until(condition);
    }

    public static void waitForVisible(WebDriver driver, WebElement element) {
        waitUntil(driver, d -> element.isDisplayed() ? element : null);
    }

    public static void waitForClickable(WebDriver driver, WebElement element) {
        waitUntil(driver, d -> (element.isDisplayed() && element.isEnabled()) ? element : null);
    }
}
