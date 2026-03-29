package com.w2a.APITestingFramework.listeners;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Date;

import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.Markup;
import com.aventstack.extentreports.markuputils.MarkupHelper;

public class ExtentListeners implements ITestListener, ISuiteListener {

	/*
	 * 📄 ExtentListeners.java Issue 1: ExtentReports version mismatch - Cause: Code
	 * used ExtentHtmlReporter (v4) while project had ExtentReports v5, where
	 * ExtentSparkReporter is required. - Solution: Update ExtentManager and
	 * ExtentListeners to use ExtentSparkReporter. Ensure both classes are aligned
	 * to the same version. Issue 2: NullPointerException in reporting - Cause:
	 * testReport.get() was null when listener wasn’t registered or onTestStart
	 * didn’t initialize the test. - Solution: Register listener in testng.xml or
	 * with @Listeners, and add defensive null checks before calling .pass(),
	 * .fail(), or .skip().
	 */

	static Date d = new Date();
	static String fileName = "Extent_" + d.toString().replace(":", "_").replace(" ", "_") + ".html";

	// Updated ExtentManager call (v5 uses SparkReporter internally)
	private static ExtentReports extent = ExtentManager
			.createInstance(System.getProperty("user.dir") + "/reports/" + fileName);

	public static ThreadLocal<ExtentTest> testReport = new ThreadLocal<>();

	public void onTestStart(ITestResult result) {
		ExtentTest test = extent
				.createTest(result.getTestClass().getName() + " @TestCase : " + result.getMethod().getMethodName());
		testReport.set(test);
	}

	public void onTestSuccess(ITestResult result) {
		String methodName = result.getMethod().getMethodName();
		String logText = "<b>TEST CASE:- " + methodName.toUpperCase() + " PASSED</b>";
		Markup m = MarkupHelper.createLabel(logText, ExtentColor.GREEN);
		if (testReport.get() != null) {
			testReport.get().pass(m);
		}
	}

	public void onTestFailure(ITestResult result) {
		if (testReport.get() != null) {
			testReport.get().fail(result.getThrowable());

			String exceptionMessage = Arrays.toString(result.getThrowable().getStackTrace());
			testReport.get()
					.fail("<details><summary><b><font color=red>Exception Occurred: Click to see</font></b></summary>"
							+ exceptionMessage.replaceAll(",", "<br>") + "</details>");

			String failureLog = "TEST CASE FAILED";
			Markup m = MarkupHelper.createLabel(failureLog, ExtentColor.RED);
			testReport.get().log(Status.FAIL, m);
		}
	}

	public void onTestSkipped(ITestResult result) {
		String methodName = result.getMethod().getMethodName();
		String logText = "<b>Test Case:- " + methodName + " Skipped</b>";
		Markup m = MarkupHelper.createLabel(logText, ExtentColor.YELLOW);
		if (testReport.get() != null) {
			testReport.get().skip(m);
		}
	}

	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
		// Not used
	}

	public void onStart(ITestContext context) {
		// Not used
	}

	public void onFinish(ITestContext context) {
		if (extent != null) {
			extent.flush();
		}
	}

	public void onStart(ISuite suite) {
		// Not used
	}

	public void onFinish(ISuite suite) {
		try {
			String messageBody = "http://" + InetAddress.getLocalHost().getHostAddress()
					+ ":8080/job/APITestingFramework/ExtentReports/" + fileName;

			// Optional: enable once your mail config is ready
			/*
			 * MonitoringMail mail = new MonitoringMail(); mail.sendMail(TestConfig.server,
			 * TestConfig.from, TestConfig.to, TestConfig.subject, messageBody);
			 */
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
}