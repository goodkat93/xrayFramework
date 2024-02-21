package com.ugasoft.ui.common.core;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.*;
import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class BasePageObject {

    protected WebDriver driver;
    private static final ExpectedCondition<Boolean> NON_EMPTY_PAGE_TITLE = new ExpectedCondition<Boolean>() {
        public Boolean apply(WebDriver browser) {
            return StringUtils.isNotEmpty(browser.getTitle());
        }

        public String toString() {
            return "Page title to come up";
        }
    };

    protected static int short_timeout = 5;
    protected static int timeout = 30;
    protected static int long_timeout = 60;
    public static final String TRANSITION_TITLE = "loading";
    public static final String TRANSITION_URL = "about:blank";

    public static void initConstants(int defaultTimeout) {
        short_timeout = (int) Math.ceil((double) (defaultTimeout / 10));
        timeout = defaultTimeout;
        long_timeout = defaultTimeout * 3;
    }

    public BasePageObject(WebDriver driver) {
        this.driver = null;
        this.driver = driver;
    }

    protected BasePageObject(WebDriver driver, String expectedUrl, Boolean isURL) throws IllegalStateException {
        this(driver);
        long startTimer = System.currentTimeMillis();
        String actualUrl = null;
        this.waitFor(NON_EMPTY_PAGE_TITLE, (long) timeout);

        for (int i = 0; i < 60; ++i) {
            actualUrl = driver.getCurrentUrl();
            if (actualUrl.contains(expectedUrl)) {
                break;
            }
        }
        long endTimer = System.currentTimeMillis();
        Log.info("URL страницы: " + actualUrl + " Затраченное время: " + (endTimer - startTimer) + "мс");
        if (!actualUrl.contains(expectedUrl)) {
            throw new IllegalStateException("Ожидаемая страница: " + expectedUrl + ", фактическая: " + actualUrl + ", Заголовок: " + driver.getTitle());
        }
    }

    protected BasePageObject(WebDriver driver, String expectedTitle) throws IllegalStateException {
        this(driver);
        long startTimer = System.currentTimeMillis();
        String actualTitle = this.waitForPageTitle(expectedTitle);
        long endTimer = System.currentTimeMillis();
        Log.info("Заголовок страницы: " + actualTitle + " Затраченное время: " + (endTimer - startTimer) + "мс");
        if (!actualTitle.contains(expectedTitle)) {
            throw new IllegalStateException("Ожидаемая страница: " + expectedTitle + ", фактическая: " + actualTitle + ", URL: " + driver.getCurrentUrl());
        }
    }

    protected void waitUntilElementIsVisible(By path, int timeout) {
        try {
            this.setImplicitWait(timeout);
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds((long) timeout));
            wait.until(ExpectedConditions.visibilityOfElementLocated(path));
        } finally {
            this.setImplicitWait(DriverFactory.IMPLICIT_WAIT_TIME_OUT);
        }

    }

    protected void waitUntilElementIsInvisible(By path, int timeout) {
        try {
            this.setImplicitWait(0);
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds((long) timeout));
            wait.until(ExpectedConditions.invisibilityOfElementLocated(path));
        } finally {
            this.setImplicitWait(DriverFactory.IMPLICIT_WAIT_TIME_OUT);
        }

    }

    protected WebElement waitUntilElementIsClickable(By path, int timeout) {
        WebElement webElement;
        try {
            this.setImplicitWait(timeout);
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds((long) timeout));
            webElement = (WebElement) wait.until(ExpectedConditions.elementToBeClickable(path));
        } finally {
            this.setImplicitWait(DriverFactory.IMPLICIT_WAIT_TIME_OUT);
        }

        return webElement;
    }

    protected boolean waitForElementToBeSelected(By path, int timeout) {
        boolean condition;
        try {
            this.setImplicitWait(timeout);
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds((long) timeout));
            condition = (Boolean) wait.until(ExpectedConditions.elementToBeSelected(path));
        } finally {
            this.setImplicitWait(DriverFactory.IMPLICIT_WAIT_TIME_OUT);
        }

        return condition;
    }

    public boolean waitUntilTitleContains(String text, int timeout) {
        WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds((long) timeout));
        return (Boolean) wait.until(ExpectedConditions.titleContains(text));
    }

    public boolean waitUntilTitleIs(String text, int timeout) {
        WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds((long) timeout));
        return (Boolean) wait.until(ExpectedConditions.titleIs(text));
    }

    protected void waitFor(ExpectedCondition<Boolean> condition, long timeoutSecs) {
        String message = "Ожидание для  " + condition + " с таймаутом " + timeoutSecs;
        Log.info(message);
        (new WebDriverWait(this.driver, Duration.ofSeconds(timeoutSecs))).withMessage(message).until(condition);
    }

    protected void waitFor(ExpectedCondition<Boolean> condition, long timeoutSecs, long sleepInSecs) {
        String message = "Ожидание для " + condition + " с таймаутом " + timeoutSecs + " секунд и интервалом опроса " + sleepInSecs + " секунд.";
        (new WebDriverWait(this.driver, Duration.ofSeconds(timeoutSecs), Duration.ofSeconds(sleepInSecs * 1000L))).withMessage(message).until(condition);
    }

    private String waitForPageTitle(String expectedTitle) {
        this.waitUntilTitleIs(expectedTitle, timeout);
        return this.driver.getTitle();
    }

    public static boolean wait(int secs) throws InterruptedException {
        int time = secs * 1000;

        try {
            Thread.sleep((long) time);
            return true;
        } catch (InterruptedException var3) {
            Log.error("Method: wait");
            Log.error("Error: There was a problem forcing to explicit wait");
            Log.error("Exception: " + var3.getMessage());
            throw var3;
        }
    }

    public boolean waitAndTypeOnAlert(String text) throws InterruptedException {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds((long) timeout));
            wait.until(ExpectedConditions.alertIsPresent());
            this.driver.switchTo().alert().sendKeys(text);
            return true;
        } catch (Exception var3) {
            Log.error("Method: waitAndTypeOnAlert");
            Log.error("Error: Cannot type the desired text on the alert. It may not be available");
            Log.error("Exception: " + var3.getMessage());
            throw var3;
        }
    }

    public static String takeScreenShot(WebDriver driver, String fileName) {
        try {
            Log.info("Screen shot FileName: " + fileName);
            File scrFile = (File) ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File destFile = new File(System.getProperty("output.path") + File.separator + fileName);
            FileUtils.copyFile(scrFile, destFile);
            return destFile.getAbsolutePath();
        } catch (Exception var4) {
            var4.printStackTrace();
            return null;
        }
    }

    public String takeScreenShot(String fileName) {
        return takeScreenShot(this.driver, fileName);
    }

    protected boolean jsExecute(String script) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) this.driver;
            js.executeScript(script, new Object[0]);
            return true;
        } catch (Exception var3) {
            Log.error("Method: jsExecute");
            Log.error("Error: There was a problem executing a JavaScript script");
            Log.error("Exception: " + var3.getMessage());
            throw var3;
        }
    }

    public boolean maximizeWindow() throws InterruptedException {
        try {
            this.driver.manage().window().maximize();
            return true;
        } catch (Exception var2) {
            Log.error("Method: maximizeWindow");
            Log.error("Error: There was a problem trying to maximize the browser window");
            Log.error("Exception: " + var2.getMessage());
            throw var2;
        }
    }

    public boolean navigateBack() throws InterruptedException {
        try {
            this.driver.navigate().back();
            return true;
        } catch (Exception var2) {
            Log.error("Method: navigateBack");
            Log.error("Error: There was a problem navigating back on the browser history");
            Log.error("Exception: " + var2.getMessage());
            throw var2;
        }
    }

    public boolean navigateForward() throws InterruptedException {
        try {
            this.driver.navigate().forward();
            return true;
        } catch (Exception var2) {
            Log.error("Method: navigateForward");
            Log.error("Error: There was a problem navigating forward on the browser history");
            Log.error("Exception: " + var2.getMessage());
            throw var2;
        }
    }

    public boolean navigateTo(String url) throws InterruptedException {
        try {
            this.driver.get(url);
            return true;
        } catch (Exception var3) {
            Log.error("Method: navigateTo");
            Log.error("Error: [FATAL] There was a problem navigating to " + url);
            Log.error("Exception: " + var3.getMessage());
            throw var3;
        }
    }

    public boolean resizeWindow(Dimension size) throws InterruptedException {
        try {
            this.driver.manage().window().setSize(size);
            return true;
        } catch (Exception var3) {
            Log.error("Method: resizeWindow");
            Log.error("Error: There was a problem trying to resize the browser window");
            Log.error("Exception: " + var3.getMessage());
            throw var3;
        }
    }

    protected void setImplicitWait(int seconds) {
        this.driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    public boolean switchToDefaultContent() throws InterruptedException {
        try {
            this.driver.switchTo().defaultContent();
            return true;
        } catch (Exception var2) {
            Log.error("Method: switchToDefaultContent");
            Log.error("Error: There was a problem switching the focus of the window to the default content");
            Log.error("Exception: " + var2.getMessage());
            throw var2;
        }
    }

    public boolean switchToFrame(String handle) throws InterruptedException {
        try {
            this.driver.switchTo().frame(handle);
            return true;
        } catch (Exception var3) {
            Log.error("Method: switchToFrame");
            Log.error("Error: There was a problem switching the focus to a specific frame");
            Log.error("Exception: " + var3.getMessage());
            throw var3;
        }
    }

    public boolean switchToFrame(int index) throws InterruptedException {
        try {
            this.driver.switchTo().frame(index);
            return true;
        } catch (Exception var3) {
            Log.error("Method: switchToFrame");
            Log.error("Error: There was a problem switching the focus to the specified frame");
            Log.error("Exception: " + var3.getMessage());
            throw var3;
        }
    }

    public boolean switchToFrame(WebElement webElement) throws InterruptedException {
        try {
            this.driver.switchTo().frame(webElement);
            return true;
        } catch (Exception var3) {
            Log.error("Method: switchToFrame");
            Log.error("Error: There was a problem switching the focus to the specified frame");
            Log.error("Exception: " + var3.getMessage());
            throw var3;
        }
    }

    public boolean switchToWindow(int windowNumber) throws InterruptedException {
        try {
            return this.switchToWindow(windowNumber, timeout);
        } catch (Exception var3) {
            Log.error("Method: switchToWindow");
            Log.error("Error: There was a problem switching the focus to a specific window in " + timeout + " seconds");
            Log.error("Exception: " + var3.getMessage());
            throw var3;
        }
    }

    public boolean switchToWindow(int windowNumber, int timeout) throws InterruptedException {
        try {
            if (timeout > 0) {
                (new WebDriverWait(this.driver, Duration.ofSeconds((long) timeout))).until((driver1) -> {
                    return this.driver.getWindowHandles().size() - 1 >= windowNumber;
                });
            }

            Object[] handles = this.driver.getWindowHandles().toArray();
            this.driver.switchTo().window(handles[windowNumber].toString());
            return true;
        } catch (Exception var4) {
            Log.error("Method: switchToWindow");
            Log.error("Error: There was a problem switching the focus to a specific window in " + timeout + " seconds");
            Log.error("Exception: " + var4.getMessage());
            throw var4;
        }
    }

    public boolean switchToWindow(String handle) throws InterruptedException {
        try {
            return this.switchToWindow(handle, timeout);
        } catch (Exception var3) {
            Log.error("Method: switchToWindow");
            Log.error("Error: There was a problem switching the focus to a specific window in \" + timeout + \" seconds");
            Log.error("Exception: " + var3.getMessage());
            throw var3;
        }
    }

    public boolean switchToWindow(String handle, int timeout) throws InterruptedException {
        try {
            if (timeout > 0) {
                (new WebDriverWait(this.driver, Duration.ofSeconds((long) timeout))).until((driver1) -> {
                    return this.driver.getWindowHandles().contains(handle);
                });
            }

            this.driver.switchTo().window(handle);
            return true;
        } catch (Exception var4) {
            Log.error("Method: switchToWindow");
            Log.error("Error: There was a problem switching the focus to a specific window in \" + timeout + \" seconds");
            Log.error("Exception: " + var4.getMessage());
            throw var4;
        }
    }

    public boolean isTextPresentOnSource(String text) {
        return this.driver.getPageSource().contains(text);
    }

    public int getWindowHandles() throws InterruptedException {
        try {
            Set<String> handles = this.driver.getWindowHandles();
            return handles.toArray().length;
        } catch (Exception var2) {
            Log.error("Method: getWindowHandles");
            Log.error("Error: There was a problem getting the number of frames available to be used");
            Log.error("Exception: " + var2.getMessage());
            throw var2;
        }
    }

    public Dimension getWindowSize() throws InterruptedException {
        try {
            this.driver.manage().window().setPosition(new Point(0, 0));
            java.awt.Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            Dimension dim = new Dimension((int) screenSize.getWidth(), (int) screenSize.getHeight());
            return dim;
        } catch (Exception var3) {
            Log.error("Method: getWindowSize");
            Log.error("Error: There was a problem trying to get the browser window size");
            Log.error("Exception: " + var3.getMessage());
            throw var3;
        }
    }

    public String getCurrentTitle() {
        return this.driver.getTitle().trim();
    }

    public String waitAndGetTitle(int sec) throws InterruptedException {
        try {
            Thread.sleep(500L);
            long timeOutMilliSecond = (long) (sec * 1000);
            long startTimer = System.currentTimeMillis();

            for (long endTimer = startTimer; endTimer - startTimer < timeOutMilliSecond; endTimer = System.currentTimeMillis()) {
                String title = this.getCurrentTitle();
                if (title != "") {
                    return title;
                }
            }

            return this.getCurrentTitle();
        } catch (Exception var9) {
            Log.error("Method: waitAndGetTitle");
            Log.error("Error: There was a problem getting the current page title");
            Log.error("Exception: " + var9.getMessage());
            throw var9;
        }
    }

    public String waitAndGetUntilTitleIsDifferent(String title) throws InterruptedException {
        return this.waitAndGetUntilTitleIsDifferent(title, timeout);
    }

    public String waitAndGetUntilTitleIsDifferent(String title, int seconds) throws InterruptedException {
        try {
            String actualTitle = this.getCurrentTitle();
            long startTimer = System.currentTimeMillis();
            long endTimer = startTimer + (long) (seconds * 1000);

            while (actualTitle.equals(title) || actualTitle.equals("loading") || actualTitle.isEmpty()) {
                actualTitle = this.getCurrentTitle();
                if (System.currentTimeMillis() > endTimer) {
                    Log.info("Time out and could not find title so terminating while loop");
                    break;
                }
            }

            return actualTitle;
        } catch (Exception var8) {
            Log.error("Method: waitAndGetUntilTitleIsDifferent");
            Log.error("Error: Unable to wait for different title");
            Log.error("Exception: " + var8.getMessage());
            throw var8;
        }
    }

    public String getCurrentUrl() throws InterruptedException {
        try {
            return this.driver.getCurrentUrl();
        } catch (Exception var2) {
            Log.error("Method: getCurrentUrl");
            Log.error("Error: [FATAL] There was a problem getting current browser URL");
            Log.error("Exception: " + var2.getMessage());
            throw var2;
        }
    }

    public boolean waitUntilUrlContains(String subString) {
        return this.waitUntilUrlContains(subString, timeout);
    }

    public boolean waitUntilUrlContains(String subString, int timeout) {
        WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds((long) timeout));
        return (Boolean) wait.until(ExpectedConditions.urlContains(subString));
    }

    public boolean waitUntilUrlIs(String url) {
        return this.waitUntilUrlIs(url, timeout);
    }

    public boolean waitUntilUrlIs(String url, int timeout) {
        WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds((long) timeout));
        return (Boolean) wait.until(ExpectedConditions.urlToBe(url));
    }

    public String waitAndGetUntilURLIsDifferent(String URL) throws InterruptedException {
        return this.waitAndGetUntilURLIsDifferent(URL, timeout);
    }

    public String waitAndGetUntilURLIsDifferent(String URL, int timeout) throws InterruptedException {
        try {
            String actualURL = this.getCurrentUrl();
            long endTimer = System.currentTimeMillis() + (long) (timeout * 1000);

            while (actualURL.equals(URL) || actualURL.equals("about:blank") || actualURL.isEmpty()) {
                actualURL = this.getCurrentUrl();
                if (System.currentTimeMillis() > endTimer) {
                    Log.info("Time out and could not find URL so terminating while loop");
                    break;
                }
            }

            return actualURL;
        } catch (Exception var6) {
            Log.error("Method: waitAndGetUntilURLIsDifferent");
            Log.error("Error: Unable to wait for different URL");
            Log.error("Exception: " + var6.getMessage());
            throw var6;
        }
    }

    public boolean close() throws InterruptedException {
        try {
            this.driver.close();
            return true;
        } catch (Exception var2) {
            Log.error("Method: close");
            Log.error("Error: There was a problem closing the browser window. It may not be available");
            Log.error("Exception: " + var2.getMessage());
            throw var2;
        }
    }

    public boolean closeAll() throws InterruptedException {
        try {
            this.driver.quit();
            return true;
        } catch (Exception var2) {
            Log.error("Method: closeAll");
            Log.error("There was a problem closing all the browser instances. They may not be available");
            Log.error("Exception: " + var2.getMessage());
            throw var2;
        }
    }

    public void dismissAlert() {
        this.dismissAlert(timeout);
    }

    public void dismissAlert(int timeout) {
        WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds((long) timeout));
        wait.until(ExpectedConditions.alertIsPresent());
        this.driver.switchTo().alert().dismiss();
    }

    public void dismissAlertIfPresent() {
        try {
            this.dismissAlert();
        } catch (Exception var2) {
            Log.info("Expected alert was not present");
        }

    }

    public void acceptAlert() {
        this.acceptAlert(timeout);
    }

    public void acceptAlert(int timeout) {
        WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds((long) timeout));
        wait.until(ExpectedConditions.alertIsPresent());
        this.driver.switchTo().alert().accept();
    }

    public void acceptAlertIfPresent() {
        try {
            this.acceptAlert();
        } catch (Exception var2) {
            Log.info("Expected alert was not present");
        }
    }

    protected WebElement findElement(By by) {
        return this.driver.findElement(by);
    }

    protected WebElement findElement(By by, int timeout) {
        this.setImplicitWait(timeout);
        WebElement we = this.driver.findElement(by);
        this.setImplicitWait(DriverFactory.IMPLICIT_WAIT_TIME_OUT);
        return we;
    }

    protected WebElement findElement(String xpath, Object... args) {
        return this.driver.findElement(By.xpath(String.format(xpath, args)));
    }

    protected List<WebElement> findElements(By by) {
        return this.driver.findElements(by);
    }

    protected List<WebElement> findElements(By by, int timeout) {
        this.setImplicitWait(timeout);
        List<WebElement> we = this.driver.findElements(by);
        this.setImplicitWait(DriverFactory.IMPLICIT_WAIT_TIME_OUT);
        return we;
    }

    protected List<WebElement> findElements(String xpath, Object... args) {
        return this.driver.findElements(By.xpath(String.format(xpath, args)));
    }

    public WebDriver getWebDriver() {
        return this.driver;
    }

    public void setWebDriver(WebDriver driver) {
        this.driver = driver;
    }

    public void clickByLinkText(String linkName) throws Exception {
        Log.info("Going to Click: " + linkName);
        (new WebLinkImpl(this.driver, this.findElement(By.linkText(linkName)))).click();
    }

    protected boolean isElementPresent(By by) {
        this.setImplicitWait(0);
        boolean isPresent = this.driver.findElements(by).size() > 0;
        this.setImplicitWait(DriverFactory.IMPLICIT_WAIT_TIME_OUT);
        return isPresent;
    }

    protected boolean isElementPresent(By by, int timeout) {
        this.setImplicitWait(timeout);
        boolean isPresent = this.driver.findElements(by).size() > 0;
        this.setImplicitWait(DriverFactory.IMPLICIT_WAIT_TIME_OUT);
        return isPresent;
    }

    protected boolean isElementVisible(By by) {
        this.setImplicitWait(0);
        List<WebElement> webElementList = this.driver.findElements(by);
        this.setImplicitWait(DriverFactory.IMPLICIT_WAIT_TIME_OUT);
        return webElementList.size() > 0 && ((WebElement) webElementList.get(0)).isDisplayed();
    }

    protected boolean isElementVisible(By by, int timeout) {
        long timeBeforeWait = System.currentTimeMillis();
        this.setImplicitWait(timeout);
        List<WebElement> webElementList = this.driver.findElements(by);
        this.setImplicitWait(DriverFactory.IMPLICIT_WAIT_TIME_OUT);
        long timeSpent = (System.currentTimeMillis() - timeBeforeWait) / 1000L;
        if (webElementList.size() > 0) {
            try {
                if (timeSpent < (long) timeout) {
                    (new WebDriverWait(this.driver, Duration.ofSeconds((long) timeout - timeSpent)))
                            .until(ExpectedConditions.visibilityOf((WebElement) webElementList.get(0)));
                }

                return ((WebElement) webElementList.get(0)).isDisplayed();
            } catch (Exception var9) {
                return false;
            }
        } else {
            return false;
        }
    }

    public void reloadPage() {
        try {
            this.driver.navigate().refresh();
        } catch (Exception var2) {
            var2.printStackTrace();
        }

    }

    public void deleteAllCookiesJs() throws InterruptedException {
        try {
            this.jsExecute("document.cookie.split(\";\").forEach(function(c) " +
                    "{ document.cookie = c.replace(/^ +/, \"\").replace(/=.*/, \"=;expires=\"" +
                    " + new Date().toUTCString() + \";path=/\"); });");
        } catch (Exception var2) {
            Log.error("Method: deleteAllCookiesJs");
            Log.error("There was a problem deleting all cookies.");
            Log.error("Exception: " + var2.getMessage());
            throw var2;
        }
    }

    public String getAlertText() {
        return this.getAlertText(timeout);
    }

    public String getAlertText(int timeout) {
        WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds((long) timeout));
        wait.until(ExpectedConditions.alertIsPresent());
        return this.driver.switchTo().alert().getText();
    }

    protected WebElement getFirstVisibleWebElement(List<WebElement> listOfWebElements) {
        try {
            Iterator var2 = listOfWebElements.iterator();
            while (var2.hasNext()) {
                WebElement we = (WebElement) var2.next();
                if (we.isDisplayed()) {
                    return we;
                }
            }
        } catch (Exception var4) {
        }
        return null;
    }

    protected List<WebElement> getVisibleWebElementsList(List<WebElement> listOfWebElements) {
        try {
            List<WebElement> visibleWebElementsList = new ArrayList();
            Iterator var3 = listOfWebElements.iterator();

            while (var3.hasNext()) {
                WebElement we = (WebElement) var3.next();
                if (we.isDisplayed()) {
                    visibleWebElementsList.add(we);
                }
            }
            return visibleWebElementsList;
        } catch (Exception var5) {
            return null;
        }
    }

    protected int getCountOfVisibleWebElements(List<WebElement> listOfWebElements) {
        int count = 0;

        try {
            Iterator var3 = listOfWebElements.iterator();

            while (var3.hasNext()) {
                WebElement we = (WebElement) var3.next();
                if (we.isDisplayed()) {
                    ++count;
                }
            }
        } catch (Exception var5) {
        }
        return count;
    }

    protected boolean isAllWebElementsVisible(List<WebElement> listOfWebElements) {
        if (listOfWebElements.size() == 0) {
            return false;
        } else {
            Iterator var2 = listOfWebElements.iterator();

            while (var2.hasNext()) {
                WebElement we = (WebElement) var2.next();

                try {
                    if (!we.isDisplayed()) {
                        return false;
                    }
                } catch (Exception var5) {
                    return false;
                }
            }

            return true;
        }
    }

    protected boolean isAtLeastOneOfWebElementsVisible(List<WebElement> listOfWebElements) {
        if (listOfWebElements.size() == 0) {
            return false;
        } else {
            Iterator var2 = listOfWebElements.iterator();

            while (var2.hasNext()) {
                WebElement we = (WebElement) var2.next();

                try {
                    if (we.isDisplayed()) {
                        return true;
                    }
                } catch (Exception var5) {
                }
            }

            return false;
        }
    }

    protected List<String> getWebElementsTextList(List<WebElement> listOfWebElements) {
        return this.getWebElementsTextList(listOfWebElements, true);
    }

    protected List<String> getWebElementsTextList(List<WebElement> listOfWebElements, boolean trim) {
        List<String> textList = new ArrayList();
        Iterator var4 = listOfWebElements.iterator();

        while (var4.hasNext()) {
            WebElement we = (WebElement) var4.next();
            if (trim) {
                textList.add(we.getAttribute("textContent").trim());
            } else {
                textList.add(we.getAttribute("textContent"));
            }
        }

        return textList;
    }

    protected BasePageObject clickAllWebElementsList(List<WebElement> listOfWebElements) {
        Iterator var2 = listOfWebElements.iterator();

        while (var2.hasNext()) {
            WebElement we = (WebElement) var2.next();
            we.click();
        }

        return this;
    }

    protected WebElement getFirstElementWithText(List<WebElement> listOfWebElements, String text) {
        return this.getFirstElementWithText(listOfWebElements, text, false);
    }

    protected WebElement getFirstElementWithText(List<WebElement> listOfWebElements, String text, boolean caseSensitive) {
        Iterator var4 = listOfWebElements.iterator();

        while (var4.hasNext()) {
            WebElement we = (WebElement) var4.next();
            if (caseSensitive) {
                if (we.getText().trim().equals(text)) {
                    return we;
                }
            } else if (we.getText().trim().equalsIgnoreCase(text)) {
                return we;
            }
        }

        return null;
    }

    protected String waitAndGetWhileCssAttributeValueStabilize(final WebElement webElement, final String cssAttributeName) throws Exception {
        (new WebDriverWait(this.driver, Duration.ofSeconds((long) timeout))).until(new ExpectedCondition<Boolean>() {
            String oldText = webElement.getCssValue(cssAttributeName);

            public Boolean apply(WebDriver driver) {
                Boolean var3;
                try {
                    Boolean var2 = webElement.getCssValue(cssAttributeName).equals(this.oldText);
                    return var2;
                } catch (Exception var7) {
                    var3 = false;
                } finally {
                    this.oldText = webElement.getCssValue(cssAttributeName);
                }

                return var3;
            }
        });
        return webElement.getCssValue(cssAttributeName);
    }

    protected String waitAndGetWhileWebElementImplTextIsNotEmpty(WebElementImpl webElementImpl) throws Exception {
        (new WebDriverWait(this.driver, Duration.ofSeconds((long) timeout))).until((driver) -> {
            try {
                return webElementImpl.getText(timeout).equals("");
            } catch (Exception var3) {
                return false;
            }
        });
        return "";
    }

    protected String waitAndGetUntilTextIsDifferent(WebElementImpl webElement) throws Exception {
        String oldText = webElement.getText(timeout);
        (new WebDriverWait(this.driver, Duration.ofSeconds((long) timeout))).until((driver) -> {
            try {
                String actualText = webElement.getText(timeout);
                return !oldText.equals(actualText) && !"".equals(actualText);
            } catch (Exception var4) {
                return false;
            }
        });
        return webElement.getText(timeout);
    }

    public String getCookieValue(String name) throws Exception {
        return URLDecoder.decode(this.driver.manage().getCookieNamed(name).getValue(), String.valueOf(StandardCharsets.UTF_8));
    }

    protected WebElementImpl getWebElementImplBySpecificXpath(String xpath, String... params) {
        return new WebElementImpl(this.driver, this.findElement(By.xpath(String.format(xpath, (Object) params))));
    }
}

