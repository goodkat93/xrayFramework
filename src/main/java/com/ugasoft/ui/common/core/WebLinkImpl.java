package com.ugasoft.ui.common.core;

import org.openqa.selenium.WebDriver;

public class WebLinkImpl extends WebElementImpl {
    public WebLinkImpl(WebDriver driver, org.openqa.selenium.WebElement element) {
        super(driver, element);
    }

    public String getHRef() {
        try {
            return this.WebElement.getAttribute("href");
        } catch (Exception var2) {
            Log.error("Method: getHRef()");
            Log.error("Error: There was a problem getting the 'href' attribute for the WebLink");
            Log.error("Exception: " + var2.getMessage());
            throw var2;
        }
    }

    public String getHRef(int timeout) throws Exception {
        this.waitForVisibility(timeout);
        return this.getHRef();
    }
}
