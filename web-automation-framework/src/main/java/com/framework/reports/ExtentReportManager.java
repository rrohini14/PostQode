package com.framework.reports;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.framework.constants.FrameworkConstants;

public final class ExtentReportManager {

    private ExtentReportManager() {}

    private static ExtentReports extentReports;

    public static ExtentReports getInstance() {
        if (extentReports == null) {
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(
                    FrameworkConstants.REPORTS_DIR + "/AutomationReport.html"
            );
            sparkReporter.config().setReportName("Web Automation Execution Report");
            sparkReporter.config().setDocumentTitle("Automation Test Report");

            extentReports = new ExtentReports();
            extentReports.attachReporter(sparkReporter);
            extentReports.setSystemInfo("Framework", "Java + Selenium + TestNG");
        }
        return extentReports;
    }
}
