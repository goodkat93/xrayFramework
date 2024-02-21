package com.ugasoft.ui.common.utils;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BrowserTabsHelper {

    private static final Object WINDOW_HANDLES_LOCK = new Object();
    public volatile static Map<WebDriver, List<String>> WINDOW_HANDLES;

    public static void initMainTab(WebDriver driver) {
        synchronized (WINDOW_HANDLES_LOCK) {
            if (WINDOW_HANDLES == null)
                WINDOW_HANDLES = new HashMap<>();
            WINDOW_HANDLES.put(driver, new ArrayList<>());
            WINDOW_HANDLES.get(driver).add(driver.getWindowHandle());
        }
    }

    public static int getWindowsCount(WebDriver driver) {
        return WINDOW_HANDLES.get(driver).size();
    }

    /**
     * Waits for driver.getWindowHandle() returns needed windows count
     *
     * @param windowsCount
     * @param timeout
     */
    public static void waitForWindowsCount(WebDriver driver, int windowsCount, int timeout) throws InterruptedException {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(timeout)).until((driver1) -> driver.getWindowHandles().size() >= windowsCount);
        } catch (Exception e) {
            throw new InterruptedException("Unable to wait for windows count to be " + windowsCount);
        }
    }

    /**
     * Creates new browser tab as a copy of current URL and switches driver's activity to this tab
     *
     * @param timeout
     */
    public static void duplicateBrowserTab(WebDriver driver, int timeout) throws InterruptedException {
        createNewBrowserTab(driver, driver.getCurrentUrl(), timeout);
    }

    /**
     * Creates new browser tab, launches the specified URL there and switches driver's activity to this tab
     *
     * @param url
     * @param timeout
     */
    public static void createNewBrowserTab(WebDriver driver, String url, int timeout) throws InterruptedException {
        //create new window
        ((JavascriptExecutor) driver).executeScript("window.open();");
        waitForWindowsCount(driver, getWindowsCount(driver) + 1, timeout);
        //store new window handle
        List<String> newHandles = new ArrayList<>(driver.getWindowHandles());
        newHandles.removeAll(WINDOW_HANDLES.get(driver));
        WINDOW_HANDLES.get(driver).addAll(newHandles);
        //switch to new window
        driver.switchTo().window(WINDOW_HANDLES.get(driver).get(getWindowsCount(driver) - 1));
        if (!url.isEmpty()) {
            driver.get(url);
            new WebDriverWait(driver, Duration.ofSeconds(timeout)).until(ExpectedConditions.not(ExpectedConditions.urlToBe("about:blank"))); //skip blank pages before loading actual url
        }
    }

    public static void createNewBrowserTab(WebDriver driver, int timeout) throws InterruptedException {
        createNewBrowserTab(driver, "", timeout);
    }

    /**
     * Switches driver's activity to tab with index
     *
     * @param windowIndex
     * @param timeout
     */
    public static void switchToWindow(WebDriver driver, int windowIndex, int timeout) throws InterruptedException {
        waitForWindowsCount(driver, windowIndex + 1, timeout);
        //store new window handle if needed
        List<String> currentHandles = new ArrayList<>(driver.getWindowHandles());
        if (currentHandles.size() > getWindowsCount(driver)) {
            currentHandles.removeAll(WINDOW_HANDLES.get(driver));
            WINDOW_HANDLES.get(driver).addAll(currentHandles);
        }
        //switch to window with specified index
        driver.switchTo().window(WINDOW_HANDLES.get(driver).get(windowIndex));
        new WebDriverWait(driver, Duration.ofSeconds(timeout)).until(ExpectedConditions.not(ExpectedConditions.urlToBe("about:blank"))); //skip blank pages before loading actual url
    }

    public static void closeCurrentTab(WebDriver driver) {
        WINDOW_HANDLES.get(driver).remove(driver.getWindowHandle());
        driver.close();
    }

    /**
     * Remove browser tab from WINDOW_HANDLES map
     *
     * @param driver
     * @param windowIndex
     */
    public static void removeTabIndex(WebDriver driver, int windowIndex) {
        WINDOW_HANDLES.get(driver).remove(windowIndex);
    }

}