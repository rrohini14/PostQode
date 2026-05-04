package com.framework.base;

import com.framework.config.ConfigManager;
import com.framework.drivers.DriverManager;
import com.framework.factories.DriverFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public abstract class BaseTest {

    protected final Logger logger = LogManager.getLogger(getClass());

    @BeforeMethod(alwaysRun = true)
    public void setup() {
        WebDriver driver = DriverFactory.createDriver();
        DriverManager.setDriver(driver);
        driver.get(ConfigManager.getConfig().baseUrl());
        logger.info("Launched application URL: {}", ConfigManager.getConfig().baseUrl());
    }

    @AfterMethod(alwaysRun = true)
    public void teardown() {
        WebDriver driver = DriverManager.getDriver();
        if (driver != null) {
            driver.quit();
        }
        DriverManager.unload();
        logger.info("Closed browser session");
    }
}
