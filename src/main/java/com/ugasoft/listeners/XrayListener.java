
package com.ugasoft.listeners;

import com.ugasoft.annotations.Requirement;
import com.ugasoft.annotations.XrayTest;
import org.testng.*;
import org.testng.annotations.Test;

/**
 * The listener interface for receiving events related to execution of tests, and process Xray related annotations.
 * The listener can be automatically invoked when TestNG tests are run by using ServiceLoader mechanism.
 * You can also add this listener to a TestNG Test class by adding
 * <code>@Listeners({com.ugasoft.listeners.XrayListener.class})</code>
 * before the test class
 * Created By Oleg Arkhipov
 * @see XrayTest
 * @see Requirement
 */
public class XrayListener implements IInvokedMethodListener, ITestListener {
    boolean testSuccess = true;

    public XrayListener() {
    }

    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
        String summary = null;
        String description = null;
        String testDescription = null;
        String xrayTestDescription = null;
        String xrayTestSummary = null;
        if (method.isTestMethod()) {
            testDescription = ((Test)method.getTestMethod().getConstructorOrMethod().getMethod().getAnnotation(Test.class)).description();
            if (this.annotationPresent(method, XrayTest.class)) {
                xrayTestDescription = ((XrayTest)method.getTestMethod().getConstructorOrMethod().getMethod().getAnnotation(XrayTest.class)).description();
                xrayTestSummary = ((XrayTest)method.getTestMethod().getConstructorOrMethod().getMethod().getAnnotation(XrayTest.class)).summary();
                testResult.setAttribute("test", ((XrayTest)method.getTestMethod().getConstructorOrMethod().getMethod().getAnnotation(XrayTest.class)).key());
                testResult.setAttribute("labels", ((XrayTest)method.getTestMethod().getConstructorOrMethod().getMethod().getAnnotation(XrayTest.class)).labels());
            }

            if (!this.emptyString(xrayTestSummary)) {
                summary = xrayTestSummary;
            } else if (!this.emptyString(xrayTestDescription)) {
                summary = xrayTestDescription;
            } else if (!this.emptyString(testDescription)) {
                summary = xrayTestDescription;
            }

            if (!this.emptyString(xrayTestDescription)) {
                description = xrayTestDescription;
            } else if (!this.emptyString(testDescription)) {
                description = testDescription;
            }

            if (!this.emptyString(summary)) {
                testResult.setAttribute("summary", summary);
            }

            if (!this.emptyString(description)) {
                testResult.setAttribute("description", description);
            }

            if (this.annotationPresent(method, Requirement.class)) {
                testResult.setAttribute("requirement", ((Requirement)method.getTestMethod().getConstructorOrMethod().getMethod().getAnnotation(Requirement.class)).key());
            }
        }

    }

    private boolean annotationPresent(IInvokedMethod method, Class clazz) {
        boolean retVal = method.getTestMethod().getConstructorOrMethod().getMethod().isAnnotationPresent(clazz);
        return retVal;
    }

    private boolean emptyString(String string) {
        return string == null || string.isEmpty() || string.trim().isEmpty();
    }

    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        if (method.isTestMethod() && !this.testSuccess) {
            testResult.setStatus(2);
        }

    }

    public void onTestStart(ITestResult result) {
    }

    public void onTestSuccess(ITestResult result) {
    }

    public void onTestFailure(ITestResult result) {
    }

    public void onTestSkipped(ITestResult result) {
    }

    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
    }

    public void onStart(ITestContext context) {
    }

    public void onFinish(ITestContext context) {
    }
}
