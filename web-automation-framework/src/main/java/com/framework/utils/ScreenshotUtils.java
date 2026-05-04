package com.framework.utils;

import com.framework.constants.FrameworkConstants;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class ScreenshotUtils {

    private ScreenshotUtils() {}

    public static String capture(WebDriver driver, String testName) {
        File source = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filePath = FrameworkConstants.SCREENSHOTS_DIR + "/" + testName + "_" + timestamp + ".png";

        try {
            FileUtils.copyFile(source, new File(filePath));
        } catch (IOException e) {
            throw new RuntimeException("Unable to capture screenshot: " + filePath, e);
        }

        return filePath;
    }
}
