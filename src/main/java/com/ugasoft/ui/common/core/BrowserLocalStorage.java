package com.ugasoft.ui.common.core;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

public class BrowserLocalStorage {

    private JavascriptExecutor js;
    private WebDriver driver;

    public BrowserLocalStorage(WebDriver driver) {
        this.js = (JavascriptExecutor)driver;
    }

    public void removeItemFromLocalStorage(String item) {
        this.js.executeScript(String.format("window.localStorage.removeItem('%s');", item), new Object[0]);
    }

    public boolean isItemPresentInLocalStorage(String item) {
        return this.js.executeScript(String.format("return window.localStorage.getItem('%s');", item), new Object[0]) != null;
    }

    public String getItemFromLocalStorage(String key) {
        return (String)this.js.executeScript(String.format("return window.localStorage.getItem('%s');", key), new Object[0]);
    }

    public String getKeyFromLocalStorage(int key) {
        return (String)this.js.executeScript(String.format("return window.localStorage.key('%s');", key), new Object[0]);
    }

    public Long getLocalStorageLength() {
        return (Long)this.js.executeScript("return window.localStorage.length;", new Object[0]);
    }

    public void setItemInLocalStorage(String item, String value) {
        Log.info(String.format("Add to local store : key -[%s]; value -[%s].", item, value));
        this.js.executeScript(String.format("window.localStorage.setItem('%s','%s');", item, value), new Object[0]);
    }

    public void clearLocalStorage() {
        this.js.executeScript(String.format("window.localStorage.clear();"), new Object[0]);
    }
}