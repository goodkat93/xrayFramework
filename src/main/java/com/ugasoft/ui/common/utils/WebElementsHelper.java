package com.ugasoft.ui.common.utils;

import com.ugasoft.ui.common.core.*;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.*;

import java.lang.reflect.Field;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class WebElementsHelper extends BasePageObject {

    public WebElementsHelper(WebDriver driver) {
        super(driver);
    }

    protected WebElementsHelper(WebDriver driver, String expectedUrl, Boolean isURL) throws IllegalStateException {
        super(driver, expectedUrl, isURL);
    }

    protected WebElementsHelper(WebDriver driver, String expectedTitle) throws IllegalStateException {
        super(driver, expectedTitle);
    }


    public static void setImplicitWait(WebDriver driver, int seconds) {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(seconds));
    }

    //workaround for safari
    protected WebElement findElement(By by) {
        return findElement(by, timeout);
    }

    //workaround for safari
    protected WebElement findElement(By by, int timeout) {
        return findElement(this.driver, by, timeout);
    }

    //workaround for safari
    protected static WebElement findElement(WebDriver driver, By by, int timeout) {
        setImplicitWait(driver, 0);
        WebElement we = null;
        long endTimer = System.currentTimeMillis() + timeout * 1000L;
        while (System.currentTimeMillis() <= endTimer && we == null) {
            try {
                we = driver.findElement(by);
            } catch (Exception e) {
                if (System.currentTimeMillis() > endTimer)
                    throw e;
            }
        }
        setImplicitWait(driver, DriverFactory.IMPLICIT_WAIT_TIME_OUT);
        return we;
    }

    //workaround for safari
    protected List<WebElement> findElements(By by) {
        return findElements(by, timeout);
    }

    //workaround for safari
    protected List<WebElement> findElements(By by, int timeout) {
        return findElements(this.driver, by, timeout);
    }

    protected static List<WebElement> findElements(WebDriver driver, By by, int timeout) {
        setImplicitWait(driver, 0);
        List<WebElement> we = new ArrayList<>();
        long endTimer = System.currentTimeMillis() + (timeout * 1000L);
        do {
            try {
                we = driver.findElements(by);
            } catch (Exception e) {
                if (System.currentTimeMillis() > endTimer)
                    throw e;
            }
        } while ((System.currentTimeMillis() <= endTimer) && (we.size() == 0));
        setImplicitWait(driver, DriverFactory.IMPLICIT_WAIT_TIME_OUT);
        return we;
    }

    //workaround for safari
    protected boolean isElementVisible(By by) {
        return isElementVisible(by, timeout);
    }

    //workaround for safari < force using the findElements() method of WebElementsHelper class
    protected boolean isElementVisible(By by, int timeout) {
        return isElementVisible(this.driver, by, timeout);
    }

    protected static boolean isElementVisible(WebDriver driver, By by, int timeout) {
        long timeBeforeWait = System.currentTimeMillis();
        List<WebElement> webElementList = findElements(driver, by, timeout);
        long timeSpent = (System.currentTimeMillis() - timeBeforeWait) / 1000L;
        if (webElementList.size() > 0) {
            try {
                if (timeSpent < (long) timeout) {
                    (new WebDriverWait(driver, Duration.ofSeconds((long) timeout - timeSpent))).until(ExpectedConditions.visibilityOf(webElementList.get(0)));
                }
                return webElementList.get(0).isDisplayed();
            } catch (Exception var9) {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Returns the class field name for xpath
     *
     * @param xpath
     * @return
     * @throws Exception
     */
    private String getXpathName(String xpath) {
        String name = "";
        try {
            Field[] fields = this.getClass().getDeclaredFields();
            for (Field f : fields)
                try {
                    f.setAccessible(true);
                    Object value = f.get(this);
                    if (value != null && value.equals(xpath)) {
                        name = f.getName();
                        break;
                    }
                } finally {
                    f.setAccessible(false);
                }
            return name;
        } catch (Exception ex) {
            return name;
        }
    }

    /**
     * Get Web element
     *
     * @param xpath
     * @return
     * @throws Exception
     */
    public WebElement getWebElement(String xpath) {
        return getWebElement(xpath, timeout);
    }

    /**
     * Get Web element
     *
     * @param xpath
     * @return
     * @throws Exception
     */
    public WebElement getWebElement(String xpath, int timeout) {
        return findElement(By.xpath(xpath), timeout);
    }

    /**
     * Get Web elements list
     *
     * @param xpath
     * @return
     * @throws Exception
     */
    public List<WebElement> getWebElementsList(String xpath) {
        return getWebElementsList(xpath, timeout);
    }

    /**
     * Get Web elements list
     *
     * @param xpath
     * @return
     * @throws Exception
     */
    public List<WebElement> getWebElementsList(String xpath, int timeout) {
        return findElements(By.xpath(xpath), timeout);
    }

    /**
     * Creates WebElementImpl on xpath and index with given timeout
     *
     * @param xpath
     * @param index
     * @param timeout
     * @return
     * @throws Exception
     */
    protected WebElementImpl getWebElementImpl(String xpath, int index, int timeout) throws Exception {
        try {
            return new WebElementImpl(driver, findElements(By.xpath(xpath), timeout).get(index));
        } catch (Exception e) {
            throw new Exception("Unable to find element with index " + index
                    + " using xpath [" + xpath
                    + "] in " + timeout + " seconds.");
        }
    }

    /**
     * Creates WebElementImpl on xpath and text with given timeout
     *
     * @param xpath
     * @param text
     * @param timeout
     * @return
     * @throws Exception
     */
    protected WebElementImpl getWebElementImpl(String xpath, String text, int timeout) throws Exception {
        try {
            return new WebElementImpl(driver, getFirstElementWithText(findElements(By.xpath(xpath), timeout), text));
        } catch (Exception e) {
            throw new Exception("Unable to find element with text [" + text
                    + "] using xpath [" + xpath
                    + "] in " + timeout + " seconds.");
        }
    }

    /**
     * Creates WebEditImpl on xpath and index with given timeout
     *
     * @param xpath
     * @param index
     * @param timeout
     * @return
     * @throws Exception
     */
    protected WebEditImpl getWebEditImpl(String xpath, int index, int timeout) throws Exception {
        try {
            return new WebEditImpl(driver, findElements(By.xpath(xpath), timeout).get(index));
        } catch (Exception e) {
            throw new Exception("Unable to find element with index " + index
                    + " using xpath [" + xpath
                    + "] in " + timeout + " seconds.");
        }
    }

    /**
     * Return web element text by xpath
     *
     * @param xpath
     * @return text
     * @throws Exception
     */
    public String getText(String xpath) throws Exception {
        return getTextByIndex(xpath, 0, timeout);
    }

    /**
     * Return web element text by xpath with a wait
     *
     * @param xpath
     * @param timeout
     * @return text
     * @throws Exception
     */
    public String getText(String xpath, int timeout) throws Exception {
        return getTextByIndex(xpath, 0, timeout);
    }

    /**
     * Return web element text by xpath and index
     *
     * @param xpath
     * @param index
     * @return
     * @throws Exception
     */
    public String getTextByIndex(String xpath, int index) throws Exception {
        return getTextByIndex(xpath, index, timeout);
    }

    /**
     * Return web element text by xpath and index with a wait
     *
     * @param xpath
     * @param index
     * @param timeout
     * @return
     * @throws Exception
     */
    public String getTextByIndex(String xpath, int index, int timeout) throws Exception {
        WebElementImpl webElement = getWebElementImpl(xpath, index, timeout);
        String browser = System.getProperty("mbrowser");
        if (timeout == 0) {
            //workaround for safari browser to take only rendered text, as it done for chrome and firefox
            if (browser != null && browser.equalsIgnoreCase("safari"))
                return StringUtils.stripEnd(webElement.getAttribute("innerText"), "\n").trim();
            return webElement.getText();
        }
        //workaround for safari browser to take only rendered text, as it done for chrome and firefox
        if (browser != null && browser.equalsIgnoreCase("safari"))
            return StringUtils.stripEnd(webElement.getAttribute("innerText", timeout), "\n").trim();
        return webElement.getText(timeout);
    }

    /**
     * Return input web element value by xpath
     *
     * @param xpath
     * @return text
     * @throws Exception
     */
    public String getInputValue(String xpath) throws Exception {
        return getInputValueByIndex(xpath, 0, timeout);
    }

    /**
     * Return input web element value by xpath with a wait
     *
     * @param xpath
     * @param timeout
     * @return text
     * @throws Exception
     */
    public String getInputValue(String xpath, int timeout) throws Exception {
        return getInputValueByIndex(xpath, 0, timeout);
    }

    /**
     * Return input web element value by xpath and index
     *
     * @param xpath
     * @param index
     * @return
     * @throws Exception
     */
    public String getInputValueByIndex(String xpath, int index) throws Exception {
        return getInputValueByIndex(xpath, index, timeout);
    }

    /**
     * Return input web element value by xpath and index with a wait
     *
     * @param xpath
     * @param index
     * @param timeout
     * @return
     * @throws Exception
     */
    public String getInputValueByIndex(String xpath, int index, int timeout) throws Exception {
        WebEditImpl webElement = getWebEditImpl(xpath, index, timeout);
        if (timeout == 0) {
            return webElement.getText();
        }
        return webElement.getText(timeout);
    }

    /**
     * Get text from WebElements list
     *
     * @param xpath
     * @return
     * @throws Exception
     */
    public List<String> getAllText(String xpath, int timeout) {
        try {
            return getWebElementsTextList(getWebElementsList(xpath, timeout));
        } catch (StaleElementReferenceException ex) {
            Log.debug("Method 'getAllText' was interrupted by 'StaleElementReferenceException'. Trying to call the method again");
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {
            }
            return getWebElementsTextList(getWebElementsList(xpath, timeout));
        }
    }

    /**
     * Get text from WebElements list
     *
     * @param xpath
     * @return
     * @throws Exception
     */
    public List<String> getAllText(String xpath) {
        return getAllText(xpath, timeout);
    }

    /**
     * Returns values list of attribute from WebElements list
     *
     * @param xpath
     * @param timeout
     * @return
     * @throws Exception
     */
    public List<String> getAllAttributes(String xpath, String attribute, int timeout) {
        List<String> attributesList = new ArrayList<>();
        List<WebElement> webElementsList = new ArrayList<>(getWebElementsList(xpath, timeout));
        for (WebElement we : webElementsList) {
            attributesList.add(we.getAttribute(attribute));
        }
        return attributesList;
    }

    public List<String> getAllAttributes(String xpath, String attribute) {
        return getAllAttributes(xpath, attribute, short_timeout);
    }

    /**
     * Click web element
     *
     * @param xpath
     * @throws Exception
     */
    public void click(String xpath) throws Exception {
        clickByIndex(xpath, 0, timeout);
    }

    /**
     * Click web element
     *
     * @param xpath
     * @throws Exception
     */
    public void click(String xpath, int timeout) throws Exception {
        clickByIndex(xpath, 0, timeout);
    }

    /**
     * Click web element by index
     *
     * @param xpath
     * @param index
     * @throws Exception
     */
    public void clickByIndex(String xpath, int index) throws Exception {
        clickByIndex(xpath, index, timeout);
    }

    /**
     * Click web element by index
     *
     * @param xpath
     * @param index
     * @param timeout
     * @throws Exception
     */
    public void clickByIndex(String xpath, int index, int timeout) throws Exception {
        WebElementImpl webElement = getWebElementImpl(xpath, index, timeout);
        try {
            if (timeout == 0) {
                webElement.click();
            } else {
                webElement.click(timeout);
            }
        } catch (StaleElementReferenceException ex) {
            Log.debug("Method 'clickJSByIndex' was interrupted by 'StaleElementReferenceException'. Trying to call the method again");
            try {
                Thread.sleep(500);
            } catch (InterruptedException ie) {
            }
            if (timeout == 0) {
                webElement.click();
            } else {
                webElement.click(timeout);
            }
        }
    }

    /**
     * Click java script web element
     *
     * @param xpath
     * @throws Exception
     */
    public void clickJS(String xpath) throws Exception {
        clickJSByIndex(xpath, 0, timeout);
    }

    /**
     * Click java script web element
     *
     * @param xpath
     * @param timeout
     * @throws Exception
     */
    public void clickJS(String xpath, int timeout) throws Exception {
        clickJSByIndex(xpath, 0, timeout);
    }

    /**
     * Click java script web element by index
     *
     * @param xpath
     * @param index
     * @throws Exception
     */
    public void clickJSByIndex(String xpath, int index) throws Exception {
        clickJSByIndex(xpath, index, timeout);
    }

    /**
     * Click java script web element by index
     *
     * @param xpath
     * @param index
     * @param timeout
     * @throws Exception
     */
    public void clickJSByIndex(String xpath, int index, int timeout) throws Exception {
        WebElementImpl webElement = getWebElementImpl(xpath, index, timeout);
        try {
            if (timeout == 0) {
                webElement.clickJS();
            } else {
                webElement.clickJS(timeout);
            }
        } catch (StaleElementReferenceException ex) {
            Log.debug("Method 'clickJSByIndex' was interrupted by 'StaleElementReferenceException'. Trying to call the method again");
            try {
                Thread.sleep(500);
            } catch (InterruptedException ie) {
            }
            if (timeout == 0) {
                webElement.clickJS();
            } else {
                webElement.clickJS(timeout);
            }
        }
    }

    /**
     * Click first element with text
     *
     * @param xpath
     * @param text
     * @throws Exception
     */
    public void clickFirstElementWithText(String xpath, String text) throws Exception {
        clickFirstElementWithText(xpath, text, timeout);
    }

    /**
     * Click first element with text
     *
     * @param xpath
     * @param text
     * @throws Exception
     */
    public void clickFirstElementWithText(String xpath, String text, int timeout) throws Exception {
        try {
            WebElementImpl webElement = getWebElementImpl(xpath, text, timeout);
            if (timeout == 0) {
                webElement.click();
                return;
            }
            webElement.click(timeout);
        } catch (Exception ex) {
            throw new Exception("Unable to click element with text [" + text
                    + "] using xpath [" + xpath
                    + "] in " + timeout + " seconds.");
        }
    }

    /**
     * Click Java Script first element with text
     *
     * @param xpath
     * @param text
     * @throws Exception
     */
    public void clickJSFirstElementWithText(String xpath, String text) throws Exception {
        clickJSFirstElementWithText(xpath, text, timeout);
    }

    /**
     * Click Java Script first element with text
     *
     * @param xpath
     * @param text
     * @throws Exception
     */
    public void clickJSFirstElementWithText(String xpath, String text, int timeout) throws Exception {
        WebElementImpl webElement = getWebElementImpl(xpath, text, timeout);
        try {
            if (timeout == 0) {
                webElement.clickJS();
                return;
            }
            webElement.clickJS(timeout);
        } catch (Exception ex) {
            throw new Exception("Unable to click element with text by JS [" + text
                    + "] using xpath [" + xpath
                    + "] in " + timeout + " seconds.");
        }
    }

    /**
     * Is element visible ?
     *
     * @param xpath
     * @throws Exception
     */
    public boolean isVisible(String xpath) {
        return isVisible(xpath, short_timeout);
    }

    /**
     * Is element visible ?
     *
     * @param xpath
     * @param timeout
     * @throws Exception
     */
    public boolean isVisible(String xpath, int timeout) {
        return isElementVisible(By.xpath(xpath), timeout);
    }

    /**
     * Wrap XPath string with index
     *
     * @param xpath
     * @param index
     * @return
     * @throws Exception
     */
    public String wrapXPathWithIndex(String xpath, int index) {
        return "(" + xpath + ")[" + (index + 1) + "]";
    }

    /**
     * Is element visible by index?
     *
     * @param xpath
     * @param index   - zero-based indexing
     * @param timeout
     * @throws Exception
     */
    public boolean isVisibleByIndex(String xpath, int index, int timeout) {
        return isVisible(wrapXPathWithIndex(xpath, index), timeout);
    }

    /**
     * Is element visible by text?
     *
     * @param xpath
     * @param text
     * @param timeout
     * @throws Exception
     */
    public boolean isVisibleByText(String xpath, String text, int timeout) throws Exception {
        long timeBeforeWait = System.currentTimeMillis();
        WebElement webElement = getFirstElementWithText(getWebElementsList(xpath, timeout), text);
        long timeSpent = (System.currentTimeMillis() - timeBeforeWait) / 1000L;
        if (timeSpent < (long) timeout) {
            return webElement != null && new WebElementImpl(driver, webElement).isDisplayed(timeout - (int) timeSpent);
        }
        return webElement != null && webElement.isDisplayed();
    }

    /**
     * Is all element visible ?
     *
     * @param xpath
     * @throws Exception
     */
    public boolean isAllVisible(String xpath) {
        return isAllWebElementsVisible(getWebElementsList(xpath));
    }

    /**
     * Return elements loading condition
     *
     * @param xPathList
     * @return boolean
     * @throws Exception
     */
    public boolean isLocatorsVisible(String... xPathList) {
        boolean isSuccess = true;
        for (String xpath : xPathList) {
            try {
                boolean isVisible = isVisible(xpath, 2);
                if (!isVisible) {
                    isSuccess = false;
                    Log.warn("Expected condition failed: element '" + getXpathName(xpath) + "' wasn't loaded");
                }
            } catch (Exception ex) {
                return false;
            }
        }
        return isSuccess;
    }

    /**
     * Is element invisible ?
     *
     * @param xpath
     * @throws Exception
     */
    public boolean isInvisible(String xpath) {
        return isInvisible(xpath, short_timeout);
    }

    /**
     * Is element invisible ?
     *
     * @param xpath
     * @param timeout
     * @throws Exception
     */
    public boolean isInvisible(String xpath, int timeout) {
        try {
            waitUntilElementIsInvisible(By.xpath(xpath), timeout);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Check that two rectangles overlap
     *
     * @param rectangle1
     * @param rectangle2
     * @return
     * @throws Exception
     */
    public boolean isRectanglesOverlap(Rectangle rectangle1, Rectangle rectangle2) {
        int r1_left = rectangle1.getX();
        int r1_right = rectangle1.getX() + rectangle1.getWidth() - 1;
        int r1_top = rectangle1.getY();
        int r1_bottom = rectangle1.getY() + rectangle1.getHeight() - 1;

        int r2_left = rectangle2.getX();
        int r2_right = rectangle2.getX() + rectangle2.getWidth() - 1;
        int r2_top = rectangle2.getY();
        int r2_bottom = rectangle2.getY() + rectangle2.getHeight() - 1;

        return !(r1_right <= r2_left || r1_bottom <= r2_top || r1_left >= r2_right || r1_top >= r2_bottom);
    }

    /**
     * Check that two elements overlap
     *
     * @param xpath1
     * @param xpath2
     * @return
     * @throws Exception
     */
    public boolean isElementsOverlap(String xpath1, String xpath2) throws Exception {
        return isRectanglesOverlap(getRectangle(xpath1), getRectangle(xpath2));
    }

    /**
     * Get element attribute
     *
     * @param xpath
     * @param attribute
     * @throws Exception
     */
    public String getAttribute(String xpath, String attribute) throws Exception {
        return getAttributeByIndex(xpath, 0, attribute, timeout);
    }

    /**
     * Get element attribute
     *
     * @param xpath
     * @param attribute
     * @param timeout
     * @throws Exception
     */
    public String getAttribute(String xpath, String attribute, int timeout) throws Exception {
        return getAttributeByIndex(xpath, 0, attribute, timeout);
    }

    /**
     * Get attribute from element with specified text
     *
     * @param xpath
     * @param attribute
     * @param timeout
     * @param text
     * @throws Exception
     */
    public String getAttributeOnElementWithText(String xpath, String text, String attribute, int timeout) throws Exception {
        WebElementImpl webElement = getWebElementImpl(xpath, text, timeout);
        if (timeout == 0) {
            return webElement.getAttribute(attribute);
        }
        return webElement.getAttribute(attribute, timeout);
    }

    /**
     * Get attribute of element with index from web elements list
     *
     * @param xpath
     * @param index
     * @param attribute
     * @param timeout
     * @throws Exception
     */
    public String getAttributeByIndex(String xpath, int index, String attribute, int timeout) throws Exception {
        try {
            WebElementImpl webElement = getWebElementImpl(xpath, index, timeout);
            if (timeout == 0) {
                return webElement.getAttribute(attribute);
            }
            return webElement.getAttribute(attribute, timeout);
        } catch (Exception ex) {
            throw new Exception("Unable to get attribute [" + attribute
                    + "] using xpath [" + xpath
                    + "] in " + timeout + " seconds.");
        }
    }

    /**
     * Get element Css value
     *
     * @param xpath
     * @param cssProperty
     * @throws Exception
     */
    public String getCssValue(String xpath, String cssProperty) throws Exception {
        return getCssValue(xpath, cssProperty, timeout);
    }

    /**
     * Get element Css value
     *
     * @param xpath
     * @param cssProperty
     * @throws Exception
     */
    public String getCssValue(String xpath, String cssProperty, int timeout) throws Exception {
        return getCssValueByIndex(xpath, 0, cssProperty, timeout);
    }

    /**
     * Return web element CSS value by xpath and index with a wait
     *
     * @param xpath
     * @param index
     * @param timeout
     * @return
     * @throws Exception
     */
    public String getCssValueByIndex(String xpath, int index, String cssProperty, int timeout) throws Exception {
        WebElementImpl webElement = getWebElementImpl(xpath, index, timeout);
        if (timeout == 0) {
            return webElement.getCssValue(cssProperty);
        }
        return webElement.getCssValue(cssProperty, timeout);
    }

    /**
     * Get CSS value from element with specified text
     *
     * @param xpath
     * @param cssValue
     * @param timeout
     * @param text
     * @throws Exception
     */
    public String getCssValueOnElementWithText(String xpath, String text, String cssValue, int timeout) throws Exception {
        WebElementImpl webElement = getWebElementImpl(xpath, text, timeout);
        if (timeout == 0) {
            return webElement.getCssValue(cssValue);
        }
        return webElement.getCssValue(cssValue, timeout);
    }

    /**
     * Scroll to element
     *
     * @param xpath
     * @param align
     * @throws Exception
     */
    public void scrollTo(String xpath, boolean align) throws Exception {
        scrollTo(xpath, 0, align, timeout);
    }

    /**
     * Scroll to element
     *
     * @param xpath
     * @param align
     * @throws Exception
     */
    public void scrollTo(String xpath, boolean align, int timeout) throws Exception {
        scrollTo(xpath, 0, align, timeout);
    }

    public void scrollToWithCorrection(String xpath, boolean align, int yCoordinate) throws Exception {
        scrollToWithCorrection(xpath, 0, align, yCoordinate, timeout);
    }

    public void scrollToWithCorrection(String xpath, int index, boolean align, int yCoordinate, int timeout) throws Exception {
        scrollTo(xpath, index, align, timeout);
        ((JavascriptExecutor) driver).executeScript(String.format("window.scrollBy(0, %s)", yCoordinate));
    }

    /**
     * Element centering
     *
     * @param xpath
     * @param index
     * @throws Exception
     */
    public void scrollTo(String xpath, int index) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});",
                getWebElementsList(xpath, short_timeout).get(index));
    }

    /**
     * Scroll to overlapped element (only works when scrolling down)
     *
     * @param xpath1 xpath of the element to scroll to
     * @param xpath2 xpath of the element that overrides
     */
    public void scrollToOverlappedElement(String xpath1, String xpath2) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(false)",
                getWebElementsList(xpath1, short_timeout).get(0));
        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, arguments[0].offsetHeight)",
                getWebElementsList(xpath2, short_timeout).get(0));
    }

    /**
     * Element centering
     *
     * @param xpath
     * @throws Exception
     */
    public void scrollTo(String xpath) {
        scrollTo(xpath, 0);
    }

    /**
     * Scroll to element with specified text
     *
     * @param xpath
     * @param align
     * @param timeout
     * @param text
     * @throws Exception
     */
    public void scrollToElementWithText(String xpath, String text, boolean align, int timeout) throws Exception {
        WebElementImpl webElement = getWebElementImpl(xpath, text, timeout);
        if (timeout == 0) {
            webElement.scrollTo(align);
            return;
        }
        webElement.scrollTo(align, timeout);
    }

    /**
     * Scroll to element by index
     *
     * @param xpath
     * @param index
     * @param align
     * @throws Exception
     */
    public void scrollTo(String xpath, int index, boolean align) throws Exception {
        scrollTo(xpath, index, align, timeout);
    }

    /**
     * Scroll to element by index
     *
     * @param xpath
     * @param index
     * @param align
     * @throws Exception
     */
    public void scrollTo(String xpath, int index, boolean align, int timeout) throws Exception {
        WebElementImpl webElement = getWebElementImpl(xpath, index, timeout);
        if (timeout == 0) {
            webElement.scrollTo(align);
            return;
        }
        webElement.scrollTo(align, timeout);
    }

    /**
     * Send keys element
     * Applies as workaround to solve following issue:
     * <p>
     * https://github.com/appium/appium/issues/9002
     * Appium cannot type value into html input controlled by React (webview in hybrid app).
     * The problem appears after React 15.6.0 release, when was changed implementation of controlled inputs.
     *
     * @param webElement
     * @param value
     */
    public void setNativeValue(WebElement webElement, String value) {
        ((JavascriptExecutor) driver).executeScript(
                "function setNativeValue(element, value) {\n" +
                        "  const { set: valueSetter } = Object.getOwnPropertyDescriptor(element, 'value') || {};\n" +
                        "  const prototype = Object.getPrototypeOf(element);\n" +
                        "  const { set: prototypeValueSetter } = Object.getOwnPropertyDescriptor(prototype, 'value') || {};\n" +
                        "\n" +
                        "  if (prototypeValueSetter && valueSetter !== prototypeValueSetter) {\n" +
                        "    prototypeValueSetter.call(element, value);\n" +
                        "  } else if (valueSetter) {\n" +
                        "    valueSetter.call(element, value);\n" +
                        "  } else {\n" +
                        "    throw new Error('The given element does not have a value setter');\n" +
                        "  }\n" +
                        "  element.dispatchEvent(new Event('input', { bubbles: true }));\n" +
                        "}" +
                        "setNativeValue(arguments[0]," + Quotes.escape(value) + ");",
                webElement);
    }

    public void setNativeValue(String xpath, String value) {
        setNativeValue(getWebElement(xpath), value);
    }

    /**
     * Send keys element
     *
     * @param xpath
     * @param value
     * @throws Exception
     */
    public void sendKeys(String xpath, String value) throws Exception {
        try {
            sendKeys(xpath, value, timeout);
        } catch (StaleElementReferenceException ex) {
            Log.debug("Method 'sendKeys' was interrupted by 'StaleElementReferenceException'. Trying to call the method again");
            try {
                Thread.sleep(500);
            } catch (InterruptedException ie) {
            }
            sendKeys(xpath, value, timeout);
        }
    }

    /**
     * Send keys element
     *
     * @param xpath
     * @param value
     * @param timeout
     * @throws Exception
     */
    public void sendKeys(String xpath, String value, int timeout) throws Exception {
        sendKeysByIndex(xpath, 0, value, true, false, timeout);
    }

    /**
     * Send keys element with imitate typing
     *
     * @param xpath
     * @param value
     * @param imitateTyping
     * @throws Exception
     */
    public void sendKeys(String xpath, String value, boolean imitateTyping) throws Exception {
        sendKeysByIndex(xpath, 0, value, true, imitateTyping, timeout);
    }

    /**
     * Send keys element by index with imitate typing
     *
     * @param xpath
     * @param value
     * @param imitateTyping
     * @throws Exception
     */
    public void sendKeysByIndex(String xpath, int index, String value, boolean imitateTyping) throws Exception {
        sendKeysByIndex(xpath, index, value, true, imitateTyping, timeout);
    }

    /**
     * Send keys element by index
     *
     * @param xpath
     * @param index
     * @param value
     * @throws Exception
     */
    public void sendKeysByIndex(String xpath, int index, String value) throws Exception {
        sendKeysByIndex(xpath, index, value, true, false, timeout);
    }

    /**
     * Send keys element by index
     *
     * @param xpath
     * @param index
     * @param value
     * @throws Exception
     */
    public void sendKeysByIndex(String xpath, int index, String value, boolean clearBefore, boolean imitateTyping, int timeout) throws Exception {
        //workaround for the Appium controlled iOS devices with safari browser
        String browser = System.getProperty("mbrowser");
        String platform = System.getProperty("mplatform");
        if ((browser != null && browser.equalsIgnoreCase("safari")) ||
                (platform != null && (platform.equalsIgnoreCase("iphone") || platform.equalsIgnoreCase("ipad")))) {
            setNativeValue(getWebElementsList(xpath).get(index), value);
            return;
        }

        WebEditImpl webElement = getWebEditImpl(xpath, index, timeout);
        if (timeout == 0) {
            if (clearBefore)
                webElement.clear();
            webElement.fill(value, imitateTyping);
            return;
        }
        if (clearBefore)
            webElement.clear(timeout);
        webElement.fill(value, imitateTyping);
    }

    /**
     * Imitate keyboard keys
     *
     * @param xpath
     * @param keys
     * @throws Exception
     */
    public void sendKeys(String xpath, Keys keys) {
        findElement(By.xpath(xpath), timeout).sendKeys(keys);
    }

    /**
     * Imitate keyboard keys
     *
     * @param xpath
     * @param value
     * @throws Exception
     */
    public void sendKeysWithClear(String xpath, String value) throws Exception {
        clearValue(xpath);
        findElement(By.xpath(xpath), timeout).sendKeys(value);
    }

    /**
     * Imitate keyboard keys by index
     *
     * @param xpath
     * @param index
     * @param keys
     * @throws Exception
     */
    public void sendKeysByIndex(String xpath, int index, Keys keys) {
        findElements(By.xpath(xpath), timeout).get(index).sendKeys(keys);
    }

    /**
     * Clear input element
     *
     * @param xpath
     * @throws Exception
     */
    public void clear(String xpath) throws Exception {
        new WebEditImpl(driver, findElement(By.xpath(xpath), timeout)).clear(timeout);
    }

    /**
     * Clear input element
     *
     * @param xpath
     * @param timeout
     * @throws Exception
     */
    public void clear(String xpath, int timeout) throws Exception {
        clearByIndex(xpath, 0, timeout);
    }

    /**
     * Clear input element by index
     *
     * @param xpath
     * @param index
     * @throws Exception
     */
    public void clearByIndex(String xpath, int index) throws Exception {
        clearByIndex(xpath, index, timeout);
    }

    /**
     * Clear input element by index
     *
     * @param xpath
     * @param index
     * @throws Exception
     */
    public void clearByIndex(String xpath, int index, int timeout) throws Exception {
        WebEditImpl webElement = getWebEditImpl(xpath, index, timeout);
        if (timeout == 0) {
            webElement.clear();
            return;
        }
        webElement.clear(timeout);
    }

    /**
     * Clear input element
     *
     * @param xpath
     * @throws Exception
     */
    public void clearValue(String xpath) throws Exception {
        clearValueByIndex(xpath, 0, timeout);
    }

    /**
     * Clear input element
     *
     * @param timeout
     * @param xpath
     * @throws Exception
     */
    public void clearValue(String xpath, int timeout) throws Exception {
        clearValueByIndex(xpath, 0, timeout);
    }

    /**
     * Clear input element by index
     *
     * @param timeout
     * @param xpath
     * @throws Exception
     */
    public void clearValueByIndex(String xpath, int index, int timeout) throws Exception {
        try {
            String browser = System.getProperty("mbrowser");
            String platform = System.getProperty("mplatform");
            if ((browser != null && browser.equalsIgnoreCase("safari")) ||
                    (platform != null && (platform.equalsIgnoreCase("iphone") || platform.equalsIgnoreCase("ipad")))) {
                setNativeValue(getWebElementsList(xpath).get(index), "");
            } else {
                String chars = getAttributeByIndex(xpath, index, "value", timeout);
                String backSpaceKeys = StringUtils.repeat(String.valueOf(Keys.BACK_SPACE), chars.length());
                sendKeysByIndex(xpath, index, backSpaceKeys, false, false, timeout);
            }
        } catch (
                Exception ex) {
            throw new Exception("Can't clear input. Reason is: " + ex.getMessage());
        }

    }

    /**
     * Hover over element
     *
     * @param xpath
     * @throws Exception
     */
    public void hoverOver(String xpath) throws Exception {
        hoverOverByIndex(xpath, 0, timeout);
    }

    /**
     * Hover over element
     *
     * @param xpath
     * @param timeout
     * @throws Exception
     */
    public void hoverOver(String xpath, int timeout) throws Exception {
        hoverOverByIndex(xpath, 0, timeout);
    }

    public void hoverOverJS(String xpath, int timeout) throws Exception {
        hoverOverByIndexJS(xpath, 0, timeout);
    }

    /**
     * Hover over on element with specified text
     *
     * @param xpath
     * @param text
     * @param timeout
     * @throws Exception
     */
    public void hoverOverOnElementWithText(String xpath, String text, int timeout) throws Exception {
        WebElementImpl webElement = getWebElementImpl(xpath, text, timeout);
        if (timeout == 0) {
            webElement.hoverOver();
            wait(1); //Wait for animation
            return;
        }
        webElement.hoverOver(timeout);
        wait(1); //Wait for animation
    }

    /**
     * Hover over element by index
     *
     * @param xpath
     * @param index
     * @throws Exception
     */
    public void hoverOverByIndex(String xpath, int index) throws Exception {
        hoverOverByIndex(xpath, index, timeout);
    }

    /**
     * Hover over element by index
     *
     * @param xpath
     * @param index
     * @throws Exception
     */
    public void hoverOverByIndex(String xpath, int index, int timeout) throws Exception {
        WebElementImpl webElement = getWebElementImpl(xpath, index, timeout);
        if (timeout == 0) {
            webElement.hoverOver();
            wait(1); //Wait for animation
            return;
        }
        webElement.hoverOver(timeout);
        wait(1); //Wait for animation
    }

    public void hoverOverByIndexJS(String xpath, int index) throws Exception {
        hoverOverByIndexJS(xpath, index, timeout);
    }

    public void hoverOverByIndexJS(String xpath, int index, int timeout) throws Exception {
        if (timeout > 0)
            waitForVisibilityByIndex(xpath, index, timeout);
        Rectangle rect = getRectangleByIndex(xpath, index);
        String mouseOverScript =
                "var evt = document.createEvent('MouseEvents');" +
                        "evt.initMouseEvent(" +
                        "'mouseover', true, true, window, 0, 0, 0, " +
                        (rect.x + rect.width / 2) + ", " +
                        (rect.y + rect.height / 2) +
                        ", false, false, false, false, 0, null);" +
                        "arguments[0].dispatchEvent(evt);";
        ((JavascriptExecutor) driver).executeScript(mouseOverScript, findElements(By.xpath(xpath)).get(index));
        wait(1); //Wait for animation
    }

    /**
     * Get count of elements
     *
     * @param xpath
     * @return
     * @throws Exception
     */
    public int getCount(String xpath) {
        return getWebElementsList(xpath, timeout).size();
    }

    /**
     * Get count of elements
     *
     * @param xpath
     * @param timeout
     * @return
     * @throws Exception
     */
    public int getCount(String xpath, int timeout) {
        return getWebElementsList(xpath, timeout).size();
    }

    /**
     * wait until element is visible
     *
     * @param xpath
     * @throws Exception
     */
    public void waitForVisibility(String xpath) {
        waitForVisibility(xpath, timeout);
    }

    /**
     * wait until element is visible
     *
     * @param xpath
     * @param timeout
     * @throws Exception
     */
    public void waitForVisibility(String xpath, int timeout) {
        waitUntilElementIsVisible(By.xpath(xpath), timeout);
    }

    /**
     * wait until element is visible by index
     *
     * @param xpath
     * @param index - zero-based indexing
     * @throws Exception
     */
    public void waitForVisibilityByIndex(String xpath, int index) {
        waitForVisibilityByIndex(xpath, index, timeout);
    }

    /**
     * wait until element is visible by index
     *
     * @param xpath
     * @param index   - zero-based indexing
     * @param timeout
     * @throws Exception
     */
    public void waitForVisibilityByIndex(String xpath, int index, int timeout) {
        waitUntilElementIsVisible(By.xpath(wrapXPathWithIndex(xpath, index)), timeout);
    }

    /**
     * Wait until element position stops change
     *
     * @param xpath
     * @return
     * @throws Exception
     */
    public boolean waitForElementStopMoving(String xpath) throws Exception {
        return waitForElementStopMoving(xpath, timeout);
    }

    /**
     * Wait until element position stops change
     *
     * @param xpath
     * @param timeout
     * @return
     * @throws Exception
     */
    public boolean waitForElementStopMoving(String xpath, int timeout) throws Exception {
        int sleepInterval = 250;
        long endTimer = System.currentTimeMillis() + timeout * 1000;
        Point previousPosition = getRelativeCoordinates(xpath);
        Point currentPosition;
        Thread.sleep(sleepInterval);
        while (System.currentTimeMillis() < endTimer) {
            currentPosition = getRelativeCoordinates(xpath);
            if (currentPosition.equals(previousPosition))
                return true;
            Thread.sleep(sleepInterval);
            previousPosition = currentPosition;
        }
        Log.warn("Position of element: [" + xpath + "] didn't stop changing in " + timeout + " seconds.");
        return false;
    }

    /**
     * wait until all elements are visible
     *
     * @param xpath
     * @throws Exception
     */
    public void waitForAllVisibility(String xpath) {
        WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(timeout));
        wait.until(ExpectedConditions.visibilityOfAllElements(findElements(By.xpath(xpath))));
    }

    /**
     * wait until all elements are invisible
     *
     * @param xpath
     * @throws Exception
     */
    public void waitForAllInvisibility(String xpath) {
        WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(timeout));
        wait.until(ExpectedConditions.invisibilityOfAllElements(findElements(By.xpath(xpath))));
    }

    /**
     * wait until element is invisible
     *
     * @param xpath
     * @throws Exception
     */
    public void waitForInvisibility(String xpath) {
        try {
            waitForVisibility(xpath, short_timeout);
        } catch (Exception ex) {
        }
        waitUntilElementIsInvisible(By.xpath(xpath), timeout);
    }

    /**
     * wait until element is invisible
     *
     * @param xpath
     * @throws Exception
     */
    public void waitForInvisibility(String xpath, int timeout) {
        if (timeout == 0) {
            waitUntilElementIsInvisible(By.xpath(xpath), short_timeout);
        } else {
            try {
                waitForVisibility(xpath, short_timeout);
            } catch (Exception ex) {
            }
            waitUntilElementIsInvisible(By.xpath(xpath), timeout);
        }
    }

    /**
     * Drag and drop to element
     *
     * @param xpathSource
     * @param xpathTarget
     * @throws Exception
     */
    public void dragAndDropTo(String xpathSource, String xpathTarget) {
        new Actions(driver)
                .dragAndDrop(findElement(By.xpath(xpathSource), timeout), findElement(By.xpath(xpathTarget), timeout))
                .perform();
    }

    /**
     * Drag and drop to coordinates
     *
     * @param xpathSource
     * @param xOffset
     * @param yOffset
     * @throws Exception
     */
    public void dragAndDropTo(String xpathSource, int xOffset, int yOffset) {
        new Actions(driver)
                .dragAndDropBy(findElement(By.xpath(xpathSource), timeout), xOffset, yOffset)
                .perform();
    }

    /**
     * Get rectangle of element
     *
     * @return Rectangle
     * @throws Exception
     */
    public Rectangle getRectangle(String xpath) throws Exception {
        Rectangle rect;
        try {
            rect = getRectangleByIndex(xpath, 0);
        } catch (ElementNotInteractableException err) {
            Log.warn("Element: " + xpath + "\nMethod: getRectangle().\nError: ElementNotInteractableException\nTrying getRectangleJS() method");
            rect = getRectangleJS(xpath);
        }
        return rect;
    }

    /**
     * Get rectangle of element by index
     *
     * @param xpath
     * @param index
     * @return
     * @throws Exception
     */
    public Rectangle getRectangleByIndex(String xpath, int index) throws Exception {
        Rectangle rect;
        try {
            rect = new Rectangle(
                    findElements(By.xpath(xpath), timeout).get(index).getLocation(),
                    findElements(By.xpath(xpath), timeout).get(index).getSize());
        } catch (ElementNotInteractableException err) {
            Log.warn("Element: " + xpath + "\nMethod: getRectangleByIndex().\nError: ElementNotInteractableException\nTrying getRectangleByIndexJS() method");
            rect = getRectangleByIndexJS(xpath, index);
        }
        return rect;
    }

    public Rectangle getRectangleJS(String xpath) throws Exception {
        return getRectangleByIndexJS(xpath, 0, timeout);
    }

    public Rectangle getRectangleJS(String xpath, int timeout) throws Exception {
        return getRectangleByIndexJS(xpath, 0, timeout);
    }

    public Rectangle getRectangleByIndexJS(String xpath, int index) throws Exception {
        return getRectangleByIndexJS(xpath, index, timeout);
    }

    public Rectangle getRectangleByIndexJS(String xpath, int index, int timeout) throws Exception {
        setImplicitWait(0);
        long endTimer = System.currentTimeMillis() + timeout * 1000;
        while (System.currentTimeMillis() <= endTimer) {
            try {
                String js = "return arguments[0].getBoundingClientRect()";
                Map<String, Object> mapRect = (Map<String, Object>) ((JavascriptExecutor) driver).executeScript(js, findElements(By.xpath(xpath)).get(index));
                int x = mapRect.get("x") instanceof Long ? ((Long) mapRect.get("x")).intValue() : ((Double) mapRect.get("x")).intValue();
                int y = mapRect.get("y") instanceof Long ? ((Long) mapRect.get("y")).intValue() : ((Double) mapRect.get("y")).intValue();
                int width = mapRect.get("width") instanceof Long ? ((Long) mapRect.get("width")).intValue() : ((Double) mapRect.get("width")).intValue();
                int height = mapRect.get("height") instanceof Long ? ((Long) mapRect.get("height")).intValue() : ((Double) mapRect.get("height")).intValue();
                setImplicitWait(DriverFactory.IMPLICIT_WAIT_TIME_OUT);
                return new Rectangle(x, y, height, width);
            } catch (StaleElementReferenceException ignored) {
                Thread.sleep(100);
            }
        }
        throw new Exception("Unable to getRectangleJs from [" + xpath + "] in " + timeout + " seconds");
    }

    /**
     * Check if Web element enabled
     *
     * @param xpath
     * @return
     * @throws Exception
     */
    public boolean isEnabled(String xpath) throws Exception {
        return getWebElement(xpath).isEnabled() && getAttribute(xpath, "disabled", 0) == null;
    }

    /**
     * Check if Web element enabled
     *
     * @param xpath
     * @return
     * @throws Exception
     */
    public boolean isEnabled(String xpath, int timeout) throws Exception {
        return new WebElementImpl(driver, getWebElement(xpath)).isEnabled(timeout)
                && getAttribute(xpath, "disabled", 0) == null;
    }

    /**
     * Wait until page document loading is complete
     *
     * @return
     */
    public boolean waitForDOMLoaded() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(1)).until(
                    (ExpectedCondition<Boolean>) driver -> Boolean.valueOf(((JavascriptExecutor) Objects.requireNonNull(driver))
                            .executeScript("return window.performance.timing.loadEventEnd === 0").toString()));
        } catch (Exception ignored) {
        }
        return waitForDocumentReadyStateComplete();
    }

    /**
     * Wait until page document loading is complete
     *
     * @return
     */
    public boolean waitForDocumentReadyStateComplete() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(timeout));
            return wait.until((ExpectedCondition<Boolean>) driverObject ->
                    Boolean.valueOf(((JavascriptExecutor) driver).
                            executeScript("return document.readyState === 'complete'").toString()));
        } catch (Exception ex) {
            Log.error("Waiting for Ready State loaded was failed. Page didn't loaded");
            return false;
        }
    }

    /**
     * Get scroll position (y axis value) on the page
     *
     * @return long
     * @throws Exception
     */
    public long getScrollPosition() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        return (long) js.executeScript("return window.pageYOffset;");
    }

    /**
     * Scroll to top of the page
     *
     * @throws Exception
     */
    public void scrollToTopOfPage() {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 0)");
    }

    /**
     * Scroll to middle of the page
     *
     * @throws Exception
     */
    public void scrollToMiddleOfPage() throws Exception {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, (document.body.scrollHeight)/2)");
        wait(1);
    }

    /**
     * Scroll to bottom of the page
     *
     * @throws Exception
     */
    public void scrollToBottomOfPage() throws Exception {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
        wait(1);
    }

    /**
     * Get relative coordinates WebElement
     *
     * @param xpath
     * @return
     * @throws Exception
     */
    public Point getRelativeCoordinates(String xpath) {
        double x = Double.parseDouble(((JavascriptExecutor) driver).
                executeScript("return arguments[0].getBoundingClientRect().left", getWebElement(xpath)).toString());
        double y = Double.parseDouble(((JavascriptExecutor) driver).
                executeScript("return arguments[0].getBoundingClientRect().top", getWebElement(xpath)).toString());
        return new Point((int) Math.round(x), (int) Math.round(y));
    }

    /**
     * Check element in the screen area
     *
     * @param xpath
     * @param index
     * @return
     */
    public boolean isElementOnTheScreen(String xpath, int index) {
        if (!isVisibleByIndex(xpath, index, 3)) {
            return false;
        }
        Point el = getRelativeCoordinates(wrapXPathWithIndex(xpath, index));
        boolean yCoordinate = 0 <= el.y && el.y <= driver.manage().window().getSize().height;
        boolean xCoordinate = 0 <= el.x && el.x <= driver.manage().window().getSize().width;
        return yCoordinate && xCoordinate;
    }

    public boolean isElementOnTheScreen(String xpath) {
        return isElementOnTheScreen(xpath, 0);
    }

    /**
     * Click on WebElement with offset
     *
     * @param xpath
     * @param xOffset
     * @param yOffset
     * @return
     * @throws Exception
     */
    public void clickOnWebElementWithOffset(String xpath, int xOffset, int yOffset) {
        clickOnWebElementWithOffsetByIndex(xpath, 0, xOffset, yOffset);
    }

    /**
     * Click on WebElement with offset by index
     *
     * @param xpath
     * @param xOffset
     * @param yOffset
     * @param index
     * @return
     * @throws Exception
     */
    public void clickOnWebElementWithOffsetByIndex(String xpath, int index, int xOffset, int yOffset) {
        Actions action = new Actions(driver);
        action.moveToElement(getWebElementsList(xpath).get(index)).moveByOffset(xOffset, yOffset).click().build().perform();
    }


    /**
     * Get relative coordinates WebElement by index
     *
     * @param xpath
     * @param index
     * @return
     * @throws Exception
     */
    public Point getRelativeCoordinatesByIndex(String xpath, int index) {
        double x = Double.parseDouble(((JavascriptExecutor) driver).
                executeScript("return arguments[0].getBoundingClientRect().left",
                        findElements(By.xpath(xpath), timeout).get(index)).toString());
        double y = Double.parseDouble(((JavascriptExecutor) driver).
                executeScript("return arguments[0].getBoundingClientRect().top",
                        findElements(By.xpath(xpath), timeout).get(index)).toString());
        return new Point((int) Math.round(x), (int) Math.round(y));
    }

    /**
     * Is element selected?
     *
     * @param xpath
     * @throws Exception
     */
    public boolean isSelected(String xpath) {
        return isSelectedByIndex(xpath, 0);
    }

    /**
     * Is element selected?
     *
     * @param xpath
     * @throws Exception
     */
    public boolean isSelectedByIndex(String xpath, int index) {
        return getWebElementsList(xpath, 0).get(index).isSelected();
    }

    /**
     * Verify image is loaded
     *
     * @param xpath
     * @return
     * @throws Exception
     */
    public boolean isImageLoaded(String xpath) {
        Object result = ((JavascriptExecutor) driver)
                .executeScript("return arguments[0].complete && typeof arguments[0].naturalWidth != \"undefined\" && arguments[0].naturalWidth > 0", findElement(By.xpath(xpath)));
        if (result instanceof Boolean)
            return (Boolean) result;
        else return false;
    }

    /**
     * Set checkbox value
     *
     * @param xpath
     * @param value
     * @throws Exception
     */
    public void setCheckBox(String xpath, boolean value) throws Exception {
        setCheckBoxByIndex(xpath, 0, value);
    }

    /**
     * Set checkbox value by index
     *
     * @param xpath
     * @param index
     * @param value
     * @throws Exception
     */
    public void setCheckBoxByIndex(String xpath, int index, boolean value) throws Exception {
        try {
            if (isSelectedByIndex(xpath, index) != value) {
                clickJSByIndex(xpath, index, 0);
                Thread.sleep(300);
            }
        } catch (Exception ex) {
            throw new Exception("Can't select checkbox used: \nxpath -> [" + xpath + "]\nindex -> " + index);
        }
    }

    /**
     * Wait for staleness
     *
     * @param xpath
     * @throws Exception
     */
    public void waitForStaleness(String xpath) {
        waitFor(ExpectedConditions.stalenessOf(getWebElement(xpath)), timeout);
    }

    /**
     * Wait for staleness with timeout
     *
     * @param xpath, secs
     */
    public void waitForStaleness(String xpath, int secs) {
        waitFor(ExpectedConditions.stalenessOf(getWebElement(xpath)), secs);
    }

    /**
     * Wait for staleness by index
     *
     * @param xpath
     * @throws Exception
     */
    public void waitForStalenessByIndex(String xpath, int index) {
        waitFor(ExpectedConditions.stalenessOf(getWebElementsList(xpath).get(index)), timeout);
    }

    /**
     * Wait for staleness by index
     *
     * @param xpath
     * @throws Exception
     */
    public void waitForStalenessByIndex(String xpath, int index, int timeout) {
        waitFor(ExpectedConditions.stalenessOf(getWebElementsList(xpath).get(index)), timeout);
    }

    // Browser tabs methods //workaround for safari

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

    //methods for dropdown elements
    public void clickDropDownOptionByText(String xpath, String optionText, int timeout) throws Exception {
        scrollTo(xpath, false, timeout);
        waitForVisibility(xpath);
        new Select(getWebElement(xpath)).selectByVisibleText(optionText);
    }

    public void clickDropDownOptionByText(String xpath, String optionText) throws Exception {
        clickDropDownOptionByText(xpath, optionText, timeout);
    }

    public void clickDropDownOptionByIndex(String xpath, int optionIndex, int timeout) throws Exception {
        scrollTo(xpath, false, timeout);
        new Select(getWebElement(xpath)).selectByIndex(optionIndex);
    }

    public void clickDropDownOptionByIndex(String xpath, int optionIndex) throws Exception {
        clickDropDownOptionByIndex(xpath, optionIndex, timeout);
    }

    public String getDropDownSelectedOption(String xpath, int timeout) throws Exception {
        scrollTo(xpath, false, timeout);
        return new Select(getWebElement(xpath)).getFirstSelectedOption().getText();
    }

    public String getDropDownSelectedOption(String xpath) throws Exception {
        return getDropDownSelectedOption(xpath, timeout);
    }

    public List<String> getDropDownOptions(String xpath, int timeout) throws Exception {
        scrollTo(xpath, false, timeout);
        return new Select(getWebElement(xpath)).getOptions().stream().map(WebElement::getText).collect(Collectors.toList());
    }

    public List<String> getDropDownOptions(String xpath) throws Exception {
        return getDropDownOptions(xpath, timeout);
    }

    /**
     * Injects javaScript to accept/decline any type of alert
     * use this method before alert throwing action
     * > workaround for safari browser
     *
     * @param accept accept or decline the alert popup
     */
    public void injectAutoCloseAlertsScript(boolean accept) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript(String.format("window.confirm = function(){ return %s;}", accept));
        js.executeScript(String.format("window.alert = function(){ return %s;}", accept));
        js.executeScript(String.format("window.prompt = function(){ return %s;}", accept));
    }

    public void injectAcceptAlertsScript() {
        injectAutoCloseAlertsScript(true);
    }

    public void injectDeclineAlertsScript() {
        injectAutoCloseAlertsScript(false);
    }

}