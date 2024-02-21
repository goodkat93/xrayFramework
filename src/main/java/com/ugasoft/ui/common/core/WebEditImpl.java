package com.ugasoft.ui.common.core;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Quotes;

public class WebEditImpl extends WebElementImpl {

    public WebEditImpl(WebDriver driver, WebElement element) {
        super(driver, element);
    }

    public WebEditImpl clear() throws Exception {
        try {
            this.WebElement.clear();
            return this;
        } catch (Exception var2) {
            Log.error("Method: clear()");
            Log.error("Error: There was a problem clearing the WebEdit");
            Log.error("Exception: " + var2.getMessage());
            throw var2;
        }
    }

    public WebEditImpl clear(int timeout) throws Exception {
        this.waitForVisibility(timeout);
        return this.clear();
    }

    public String getText() throws Exception {
        try {
            return this.WebElement.getAttribute("value");
        } catch (Exception var2) {
            Log.error("Method: getText()");
            Log.error("Error: There was a problem getting text from the WebEdit");
            Log.error("Exception: " + var2.getMessage());
            throw var2;
        }
    }

    public String getText(int timeout) throws Exception {
        this.waitForVisibility(timeout);
        return this.getText();
    }

    public WebEditImpl fill(String text) throws Exception {
        try {
            this.WebElement.sendKeys(new CharSequence[]{text});
            return this;
        } catch (Exception var3) {
            Log.error("Method: fill()");
            Log.error(String.format("Error: There was a problem sending text '%s' to the WebEdit", text));
            Log.error("Exception: " + var3.getMessage());
            throw var3;
        }
    }

    public WebEditImpl fill(String text, boolean imitateTyping) throws Exception {
        try {
            if (!imitateTyping) {
                this.fill(text);
            } else {
                for(int i = 0; i < text.length(); ++i) {
                    this.fill(Character.toString(text.charAt(i)));
                }
            }

            return this;
        } catch (Exception var4) {
            Log.error("Method: fill()");
            Log.error(String.format("Error: There was a problem sending text '%s' to the WebEdit", text));
            Log.error("Exception: " + var4.getMessage());
            throw var4;
        }
    }

    public WebEditImpl fill(String text, int timeout) throws Exception {
        this.waitForVisibility(timeout);
        return this.fill(text);
    }

    public WebEditImpl fill(String text, boolean imitateTyping, int timeout) throws Exception {
        this.waitForVisibility(timeout);
        return this.fill(text, imitateTyping);
    }

    public WebEditImpl clearAndFill(String text) throws Exception {
        return this.clear().fill(text);
    }

    public WebEditImpl clearAndFill(String text, boolean imitateTyping) throws Exception {
        return this.clear().fill(text, imitateTyping);
    }

    public WebEditImpl clearAndFill(String text, int timeout) throws Exception {
        return this.clear(timeout).fill(text);
    }

    public WebEditImpl clearAndFill(String text, boolean imitateTyping, int timeout) throws Exception {
        return this.clear(timeout).fill(text, imitateTyping);
    }

    public WebEditImpl setValueJS(String value) throws Exception {
        try {
            ((JavascriptExecutor)this.driver).executeScript("arguments[0].value = " + Quotes.escape(value), new Object[]{this.WebElement});
            return this;
        } catch (Exception var3) {
            Log.error("Method: setValueJS()");
            Log.error(String.format("Error: There was a problem setting value '%s' to the WebEdit using JavaScript", value));
            Log.error("Exception: " + var3.getMessage());
            throw var3;
        }
    }

    public WebEditImpl setValueJS(String value, int timeout) throws Exception {
        this.waitForVisibility(timeout);
        return this.setValueJS(value);
    }

    public String waitForTextPresent(int timeout) throws Exception {
        this.waitForVisibility(timeout);

        try {
            long endTimer = System.currentTimeMillis() + (long)(timeout * 1000);

            String value;
            for(value = this.getText(); value.isEmpty(); value = this.getText()) {
                if (System.currentTimeMillis() >= endTimer) {
                    throw new TimeoutException();
                }
            }

            return value;
        } catch (Exception var5) {
            Log.error("Method: waitForTextPresent()");
            Log.error("Error: There was a problem waiting for text to populate in the WebEdit");
            Log.error("Exception: " + var5.getMessage());
            throw var5;
        }
    }

    public boolean isReadOnly() throws Exception {
        try {
            return this.WebElement.getAttribute("readonly") != null;
        } catch (Exception var2) {
            Log.error("Method: isReadOnly()");
            Log.error("Error: There was a problem determining if the WebEdit is read only");
            Log.error("Exception: " + var2.getMessage());
            throw var2;
        }
    }

    public WebEditImpl sendChangeEventJS() throws Exception {
        try {
            ((JavascriptExecutor)this.driver).executeScript("$(arguments[0]).change();", new Object[]{this.WebElement});
            return this;
        } catch (Exception var2) {
            Log.error("Method: setValueJS()");
            Log.error("Error: There was a problem sending change event to the WebEdit using JavaScript");
            Log.error("Exception: " + var2.getMessage());
            throw var2;
        }
    }
}
