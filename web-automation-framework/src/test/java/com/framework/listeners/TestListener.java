package com.framework.listeners;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.framework.config.ConfigManager;
import com.framework.drivers.DriverManager;
import com.framework.reports.ExtentReportManager;
import com.framework.utils.ScreenshotUtils;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {

    private static final ThreadLocal<ExtentTest> TEST_THREAD_LOCAL = new ThreadLocal<>();

    @Override
    public void onStart(ITestContext context) {
        ExtentReportManager.getInstance();
    }

    @Override
    public void onTestStart(ITestResult result) {
        ExtentTest test = ExtentReportManager.getInstance().createTest(result.getMethod().getMethodName());
        TEST_THREAD_LOCAL.set(test);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        TEST_THREAD_LOCAL.get().log(Status.PASS, "Test passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        TEST_THREAD_LOCAL.get().log(Status.FAIL, result.getThrowable());
        if (ConfigManager.getConfig().screenshotOnFailure() && DriverManager.getDriver() != null) {
            String screenshotPath = ScreenshotUtils.capture(DriverManager.getDriver(), result.getMethod().getMethodName());
            TEST_THREAD_LOCAL.get().addScreenCaptureFromPath(screenshotPath);
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        TEST_THREAD_LOCAL.get().log(Status.SKIP, "Test skipped");
    }

    @Override
    public void onFinish(ITestContext context) {
        ExtentReportManager.getInstance().flush();
        TEST_THREAD_LOCAL.remove();
    }
}
