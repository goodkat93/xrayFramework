package com.ugasoft.ui.common.core;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class WebElementImpl {
    
    protected WebElement WebElement = null;
    protected WebDriver driver = null;

    public WebElementImpl(WebDriver driver, WebElement element) {
        this.driver = driver;
        this.WebElement = element;
    }

    public WebElementImpl waitForVisibility(int timeout) throws Exception {
        try {
            (new WebDriverWait(this.driver, Duration.ofSeconds((long) timeout))).until(ExpectedConditions.visibilityOf(this.WebElement));
            return this;
        } catch (Exception var3) {
            Log.error("Method: waitForVisibility()");
            Log.error("Error: There was a problem waiting for WebElement to be visible");
            Log.error("Exception: " + var3.getMessage());
            throw var3;
        }
    }

    public WebElementImpl waitForInvisibility(int timeout) throws Exception {
        try {
            (new WebDriverWait(this.driver, Duration.ofSeconds((long) timeout))).until(ExpectedConditions.invisibilityOf(this.WebElement));
            return this;
        } catch (Exception var3) {
            Log.error("Method: waitForInvisibility()");
            Log.error("Error: There was a problem waiting for WebElement to be not visible");
            Log.error("Exception: " + var3.getMessage());
            throw var3;
        }
    }

    public WebElementImpl waitForClickability(int timeout) throws Exception {
        try {
            (new WebDriverWait(this.driver, Duration.ofSeconds((long) timeout))).until(ExpectedConditions.elementToBeClickable(this.WebElement));
            return this;
        } catch (Exception var3) {
            Log.error("Method: waitForClickability()");
            Log.error("Error: There was a problem waiting for WebElement to be clickable");
            Log.error("Exception: " + var3.getMessage());
            throw var3;
        }
    }

    public WebElementImpl click() throws Exception {
        try {
            this.WebElement.click();
            return this;
        } catch (Exception var2) {
            Log.error("Method: click()");
            Log.error("Error: There was a problem clicking the WebElement");
            Log.error("Exception: " + var2.getMessage());
            throw var2;
        }
    }

    public WebElementImpl click(int timeout) throws Exception {
        return this.waitForClickability(timeout).click();
    }

    public WebElementImpl sendKeys(String keys) throws Exception {
        try {
            this.WebElement.sendKeys(new CharSequence[]{keys});
            return this;
        } catch (Exception var3) {
            Log.error("Method: sendKeys()");
            Log.error("Error: There was a problem sending keys to the WebElement");
            Log.error("Exception: " + var3.getMessage());
            throw var3;
        }
    }

    public WebElementImpl sendKeys(String keys, int timeout) throws Exception {
        return this.waitForVisibility(timeout).sendKeys(keys);
    }

    public String getTagName() throws Exception {
        try {
            return this.WebElement.getTagName();
        } catch (Exception var2) {
            Log.error("Method: getTagName()");
            Log.error("Error: There was a problem getting tag name of the WebElement");
            Log.error("Exception: " + var2.getMessage());
            throw var2;
        }
    }

    public String getTagName(int timeout) throws Exception {
        return this.waitForVisibility(timeout).getTagName();
    }

    public String getAttribute(String attribute) throws Exception {
        try {
            return this.WebElement.getAttribute(attribute);
        } catch (Exception var3) {
            Log.error("Method: getAttribute()");
            Log.error("Error: There was a problem getting attribute from the WebElement");
            Log.error("Exception: " + var3.getMessage());
            throw var3;
        }
    }

    public String getAttribute(String attribute, int timeout) throws Exception {
        return this.waitForVisibility(timeout).getAttribute(attribute);
    }

    public String getCssValue(String cssProperty) throws Exception {
        try {
            return this.WebElement.getCssValue(cssProperty);
        } catch (Exception var3) {
            Log.error("Method: getCSSValue()");
            Log.error("Error: There was a problem getting a value from the WebElement CSS property");
            Log.error("Exception: " + var3.getMessage());
            throw var3;
        }
    }

    public String getCssValue(String cssProperty, int timeout) throws Exception {
        return this.waitForVisibility(timeout).getCssValue(cssProperty);
    }

    public boolean isDisplayed() throws Exception {
        try {
            return this.WebElement.isDisplayed();
        } catch (Exception var2) {
            Log.error("Method: isDisplayed()");
            Log.error("Error: There was a problem determining if the WebElement is visible");
            Log.error("Exception: " + var2.getMessage());
            throw var2;
        }
    }

    public boolean isDisplayed(int timeout) throws Exception {
        try {
            (new WebDriverWait(this.driver, Duration.ofSeconds((long) timeout))).until(ExpectedConditions.visibilityOf(this.WebElement));
            return true;
        } catch (TimeoutException var3) {
            return false;
        } catch (Exception var4) {
            Log.error("Method: isDisplayed()");
            Log.error("Error: There was a problem determining if the WebElement is visible");
            Log.error("Exception: " + var4.getMessage());
            throw var4;
        }
    }

    public boolean isEnabled() throws Exception {
        try {
            return this.WebElement.isEnabled();
        } catch (Exception var2) {
            Log.error("Method: isEnabled()");
            Log.error("Error: There was a problem determining if the WebElement is enabled");
            Log.error("Exception: " + var2.getMessage());
            throw var2;
        }
    }

    public boolean isEnabled(int timeout) throws Exception {
        try {
            (new WebDriverWait(this.driver, Duration.ofSeconds((long) timeout))).until(ExpectedConditions.elementToBeClickable(this.WebElement));
            return true;
        } catch (TimeoutException var3) {
            return false;
        } catch (Exception var4) {
            Log.error("Method: isEnabled()");
            Log.error("Error: There was a problem determining if the WebElement is enabled");
            Log.error("Exception: " + var4.getMessage());
            throw var4;
        }
    }

    public String getText() throws Exception {
        try {
            return this.WebElement.getText();
        } catch (Exception var2) {
            Log.error("Method: getText()");
            Log.error("Error: There was a problem getting text from the WebElement");
            Log.error("Exception: " + var2.getMessage());
            throw var2;
        }
    }

    public String getText(int timeout) throws Exception {
        return this.waitForVisibility(timeout).getText();
    }

    // Данный клик позволяет вызвать скрипт клика без физического нажатия драйвером на элемент
    // Нужен для корректной работы Safari и Apple based платформ, также может использоваться для установки флажка на чекбокс
    public WebElementImpl clickJS() {
        try {
            ((JavascriptExecutor) this.driver).executeScript("var evt = document.createEvent('MouseEvents');evt.initMouseEvent('click',true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);arguments[0].dispatchEvent(evt);", new Object[]{this.WebElement});
            return this;
        } catch (Exception var2) {
            Log.error("Method: clickJS()");
            Log.error("Error: There was a problem clicking the WebElement using JavaScript");
            Log.error("Exception: " + var2.getMessage());
            throw var2;
        }
    }

    public WebElementImpl clickJS(int timeout) throws Exception {
        return this.waitForClickability(timeout).clickJS();
    }

    public WebElementImpl doubleClickJS() throws Exception {
        try {
            ((JavascriptExecutor) this.driver).executeScript("var evt = document.createEvent('MouseEvents');evt.initMouseEvent('dblclick',true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0,null);arguments[0].dispatchEvent(evt);", new Object[]{this.WebElement});
            return this;
        } catch (Exception var2) {
            Log.error("Method: doubleClickJS()");
            Log.error("Error: There was a problem double clicking the WebElement using JavaScript");
            Log.error("Exception: " + var2.getMessage());
            throw var2;
        }
    }

    public WebElementImpl doubleClickJS(int timeout) throws Exception {
        return this.waitForClickability(timeout).doubleClickJS();
    }

    public WebElementImpl rightClick() throws Exception {
        try {
            (new Actions(this.driver)).contextClick(this.WebElement).perform();
            return this;
        } catch (Exception var2) {
            Log.error("Method: rightClick()");
            Log.error("Error: There was a problem right clicking the WebElement");
            Log.error("Exception: " + var2.getMessage());
            throw var2;
        }
    }

    public WebElementImpl rightClick(int timeout) throws Exception {
        return this.waitForVisibility(timeout).rightClick();
    }

    public WebElementImpl rightClickJS() throws Exception {
        try {
            ((JavascriptExecutor) this.driver).executeScript("var evt = document.createEvent('MouseEvents');evt.initMouseEvent('contextmenu',true, true, window, 1, 300, 300, 300, 300, false, false, false, false, 0,null);arguments[0].dispatchEvent(evt);", new Object[]{this.WebElement});
            return this;
        } catch (Exception var2) {
            Log.error("Method: rightClickJS()");
            Log.error("Error: There was a problem right clicking the WebElement using JavaScript");
            Log.error("Exception: " + var2.getMessage());
            throw var2;
        }
    }

    public WebElementImpl rightClickJS(int timeout) throws Exception {
        return this.waitForClickability(timeout).rightClickJS();
    }

    public WebElementImpl clickJQ() throws Exception {
        try {
            ((JavascriptExecutor) this.driver).executeScript("$(arguments[0]).click();", new Object[]{this.WebElement});
            return this;
        } catch (Exception var2) {
            Log.error("Method: clickJQ()");
            Log.error("Error: There was a problem clicking the WebElement using JQuery");
            Log.error("Exception: " + var2.getMessage());
            throw var2;
        }
    }

    public WebElementImpl clickJQ(int timeout) throws Exception {
        return this.waitForClickability(timeout).clickJQ();
    }

    public WebElementImpl scrollTo(boolean align) throws Exception {
        try {
            ((JavascriptExecutor) this.driver).executeScript("arguments[0].scrollIntoView(" + align + ");", new Object[]{this.WebElement});
            return this;
        } catch (Exception var3) {
            Log.error("Method: scrollTo()");
            Log.error("Error: There was a problem scrolling to the WebElement");
            Log.error("Exception: " + var3.getMessage());
            throw var3;
        }
    }

    public void waitForElementIsNotPresent(int timeout) throws Exception {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds((long) timeout));
            if (!(Boolean) wait.until(ExpectedConditions.stalenessOf(this.WebElement))) {
                throw new Exception("Unable to find the WebElement while waiting for " + timeout + " to remove from DOM.");
            }
        } catch (Exception var3) {
            Log.error("There was a problem waiting for element.");
            Log.error(var3.getMessage());
            throw var3;
        }
    }

    public WebElementImpl scrollTo(boolean align, int timeout) throws Exception {
        return this.waitForVisibility(timeout).scrollTo(align);
    }

    public WebElementImpl hoverOver() throws Exception {
        try {
            (new Actions(this.driver)).moveToElement(this.WebElement).perform();
            return this;
        } catch (Exception var2) {
            Log.error("Method: hoverOver()");
            Log.error("Error: There was a problem hovering over the WebElement");
            Log.error("Exception: " + var2.getMessage());
            throw var2;
        }
    }

    public WebElementImpl hoverOver(int timeout) throws Exception {
        return this.waitForVisibility(timeout).hoverOver();
    }

    public WebElementImpl dragAndDropTo(WebElementImpl dropToElement) throws Exception {
        try {
            (new Actions(this.driver)).dragAndDrop(this.WebElement, dropToElement.WebElement).perform();
            return this;
        } catch (Exception var3) {
            Log.error("Method: dragAndDropTo()");
            Log.error("Error: There was a problem performing drag and drop to the dropToElement");
            Log.error("Exception: " + var3.getMessage());
            throw var3;
        }
    }

    public WebElementImpl dragAndDropTo(WebElementImpl dropToElement, int timeout) throws Exception {
        dropToElement.waitForVisibility(timeout);
        return this.waitForVisibility(timeout).dragAndDropTo(dropToElement);
    }

    public Point getLocation() throws Exception {
        try {
            return this.WebElement.getLocation();
        } catch (Exception var2) {
            Log.error("Method: getLocation()");
            Log.error("Error: There was a problem getting location point of the rendered WebElement");
            Log.error("Exception: " + var2.getMessage());
            throw var2;
        }
    }

    public Point getLocation(int timeout) throws Exception {
        return this.waitForVisibility(timeout).getLocation();
    }

    public Dimension getSize() throws Exception {
        try {
            return this.WebElement.getSize();
        } catch (Exception var2) {
            Log.error("Method: getSize()");
            Log.error("Error: There was a problem getting size of the rendered WebElement");
            Log.error("Exception: " + var2.getMessage());
            throw var2;
        }
    }

    public Dimension getSize(int timeout) throws Exception {
        return this.waitForVisibility(timeout).getSize();
    }

    public Rectangle getRect() {
        try {
            return this.WebElement.getRect();
        } catch (Exception var2) {
            Log.error("Method: getRect()");
            Log.error("Error: There was a problem getting location and size of the rendered WebElement");
            Log.error("Exception: " + var2.getMessage());
            throw var2;
        }
    }

    public Rectangle getRect(int timeout) throws Exception {
        return this.waitForVisibility(timeout).getRect();
    }
}

