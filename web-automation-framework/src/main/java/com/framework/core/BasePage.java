package com.framework.core;

import com.framework.drivers.DriverManager;
import com.framework.utils.WaitUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

public abstract class BasePage {

    protected final WebDriver driver;

    protected BasePage() {
        this.driver = DriverManager.getDriver();
        PageFactory.initElements(driver, this);
    }

    protected void click(WebElement element) {
        WaitUtils.waitForClickable(driver, element);
        element.click();
    }

    protected void type(WebElement element, String value) {
        WaitUtils.waitForVisible(driver, element);
        element.clear();
        element.sendKeys(value);
    }

    protected String getText(WebElement element) {
        WaitUtils.waitForVisible(driver, element);
        return element.getText().trim();
    }

    protected boolean isDisplayed(WebElement element) {
        try {
            WaitUtils.waitForVisible(driver, element);
            return element.isDisplayed();
        } catch (Exception ex) {
            return false;
        }
    }
}
