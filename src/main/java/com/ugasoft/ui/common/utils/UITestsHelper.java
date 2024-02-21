package com.ugasoft.ui.common.utils;

import com.ugasoft.ui.common.core.Log;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class UITestsHelper {

    protected int long_timeout = 90;
    protected int timeout = 30;
    protected int short_timeout = 3;
    protected static String screenshotsSubFolderName;

    protected WebDriver driver;

    @AfterMethod(alwaysRun = true)
    protected void tearDown() {
        if (driver != null) {
            Log.info("Shutdown driver");
            driver.quit();
            driver = null; // set driver to null to make sure it's not used after quit
        }
    }

    public void captureScreenshot(String fileName) {
        if (screenshotsSubFolderName == null) {
            LocalDateTime myDateObj = LocalDateTime.now();
            DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd_MM_yyyy_HH_mm_ss");
            screenshotsSubFolderName = myDateObj.format(myFormatObj);
        }

        TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
        File sourceFile = takesScreenshot.getScreenshotAs(OutputType.FILE);
        File destFile = new File("./Screenshots/" + screenshotsSubFolderName + "/" + fileName);
        try {
            FileUtils.copyFile(sourceFile, destFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Screenshot saved successfully");
    }

    @BeforeMethod
    public void setUp() {
        System.setProperty("webdriver.http.factory", "jdk-http-client");
        ChromeOptions options = new ChromeOptions();
        try {
            if (System.getProperty("mbrowser").contains("headless")) {
                Log.info("headless run");
                options.addArguments("--headless=chrome");
                options.addArguments("window-size=1800x1200");
            }
            options.addArguments("--ignore-certificate-errors");
            options.addArguments("--use-fake-device-for-media-stream");
            options.addArguments("--use-fake-ui-for-media-stream");
            options.addArguments("--disable-gpu");
            options.addArguments("--disable-extensions");
            options.addArguments("--proxy-server='direct://'");
            options.addArguments("--proxy-bypass-list=*");
            options.addArguments("--start-maximized");
            options.addArguments("--no-sandbox");
            options.addArguments(
                    "user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36");
            options.addArguments("--remote-allow-origins=*");
        } catch (NullPointerException ex) {
            Log.error("No properties. Launch in normal mode");
        }
        if (System.getProperty("os.name").toLowerCase().contains("linux")) {
            WebDriverManager.chromedriver().linux().setup();
        } else {
            WebDriverManager.chromedriver().setup();
        }
        try {
            driver = new ChromeDriver(options);
            BrowserTabsHelper.initMainTab(driver);
        } catch (Exception ex) {
            Log.error("Error during setup: " + ex.getMessage());
            if (driver != null) {
                driver.quit();
                driver = null;
            }
            throw ex;
        }
    }

    public void createNewBrowserTab(String url, int timeout) throws InterruptedException {
        BrowserTabsHelper.createNewBrowserTab(driver, url, timeout);
    }

    public void createNewBrowserTab(String url) throws InterruptedException {
        BrowserTabsHelper.createNewBrowserTab(driver, url, timeout);
    }

    public void createNewBrowserTab() throws InterruptedException {
        BrowserTabsHelper.createNewBrowserTab(driver, timeout);
    }

    public boolean switchToWindow(int windowIndex, int timeout) throws InterruptedException {
        BrowserTabsHelper.switchToWindow(driver, windowIndex, timeout);
        return true;
    }

    public boolean switchToWindow(int windowIndex) throws InterruptedException {
        BrowserTabsHelper.switchToWindow(driver, windowIndex, timeout);
        return true;
    }

    public boolean closeCurrentTab() {
        BrowserTabsHelper.closeCurrentTab(driver);
        return true;
    }

    public void removeTabIndex(int windowIndex) {
        BrowserTabsHelper.removeTabIndex(driver, windowIndex);
    }
}
