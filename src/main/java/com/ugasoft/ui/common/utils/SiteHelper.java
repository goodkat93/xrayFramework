package com.ugasoft.ui.common.utils;

import com.ugasoft.ui.common.core.Log;
import com.ugasoft.ui.common.enums.Color;
import org.apache.commons.text.WordUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.logging.LogType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SiteHelper extends WebElementsHelper{

    public SiteHelper(WebDriver driver) {
        super(driver);
    }

    /**
     * Return test environment is safari or Apple mobile device
     *
     * @return
     */
    public boolean isApplePlatform() {
        String browser = System.getProperty("mbrowser");
        String platform = System.getProperty("mplatform");
        return ((browser != null && browser.equalsIgnoreCase("safari")) ||
                (platform != null && (platform.equalsIgnoreCase("iphone") || platform.equalsIgnoreCase("ipad"))));
    }

    /**
     * Check load not the error page
     *
     * @throws ExceptionInInitializerError
     */
    public void checkThisIsNotErrorPage() throws ExceptionInInitializerError {
        //skip browser errors checking if it's not chrome
        if (System.getProperty("mbrowser").toLowerCase().contains("chrome")) {
            String url = driver.getCurrentUrl();
            url = url.replaceAll("\\?", "\\\\?").replaceAll("\\[", "\\\\[");
            String patternString = url + " - Failed to load resource: the server responded with a status of (\\d+).+";
            String errorStatusCode = BrowserLogsHelper.getLogs(driver, LogType.BROWSER).stream()
                    .filter(logEntry -> logEntry.getMessage().matches(patternString))
                    .map(logEntry -> {
                        Pattern pattern = Pattern.compile(patternString);
                        Matcher matcher = pattern.matcher(logEntry.getMessage());
                        if (matcher.find())
                            return matcher.group(1);
                        return null;
                    })
                    .findFirst().orElse(null);
            if (errorStatusCode != null)
                throw new ExceptionInInitializerError("Routed to the " + errorStatusCode + " error page");
        }

        String pageSource = driver.getPageSource().toLowerCase();
        if (pageSource.contains("does not exist</h")
                || pageSource.contains("was not found</h")) {
            throw new ExceptionInInitializerError("Routed to the 404 error page");
        }
        if (pageSource.contains("<center><h1>429 too many requests</h1></center>")) {
            throw new ExceptionInInitializerError("Routed to the 429 error page");
        }
        if (pageSource.contains("internal error")
                || pageSource.contains("internal server error</h3>")
                || pageSource.contains("<center><h1>500 internal server error</h1></center>")) {
            throw new ExceptionInInitializerError("Routed to the 500 error page");
        }
        if (pageSource.contains("<center><h1>502 bad gateway</h1></center>")) {
            throw new ExceptionInInitializerError("Routed to the 502 error page");
        }
        if (pageSource.contains("<h1>error 503 first byte timeout</h1>")) {
            throw new ExceptionInInitializerError("Routed to the 503 error page");
        }
        if (pageSource.contains("<center><h1>504 gateway timeout</h1></center>")) {
            throw new ExceptionInInitializerError("Routed to the 504 error page");
        }
        if (pageSource.contains("something went wrong. please refresh and try again")) {
            throw new ExceptionInInitializerError("Routed to the 200 error page");
        }
    }

    /**
     * Click navigation tab and wait nProgress ending
     *
     * @param xpath
     * @param tab
     * @throws Exception
     */
    protected void clickNavigationTab(String xpath, String tab) throws Exception {
        try {
            Log.action("Try to click on the navigation tab: " + tab);
            scrollTo(xpath);
            List<String> tabs = getNavigationTabs(xpath);
            for (int i = 0; i < tabs.size(); i++) {
                if (tabs.get(i).equalsIgnoreCase(tab)) {
                    if (isApplePlatform()) {
                        String[] loc = xpath.split("/");
                        if (!loc[loc.length - 1].startsWith("a"))
                            xpath = xpath + "//a";
                    }
                    clickByIndex(xpath, i);
                    checkThisIsNotErrorPage();
                    return;
                }
            }
            throw new Exception("There is no tab: " + tab);
        } catch (Exception ex) {
            throw new Exception("Can't select '" + tab + "' tab. Reason is: " + ex.getMessage());
        }
    }

    /**
     * Verify 'border-bottom' property has correct value on the navigation tab
     *
     * @param xpath
     * @param tab
     * @return
     * @throws Exception
     */
    protected boolean isNavigationTabActive(String xpath, String tab) throws Exception {
        try {
            List<String> tabs = getNavigationTabs(xpath);
            for (int i = 0; i < tabs.size(); i++) {
                if (tabs.get(i).split("\n")[0].equalsIgnoreCase(tab)) {
                    return getCssValueByIndex(xpath, i, "border-bottom", 0).contains(Color.C_0_0_0.getNumeric());
                }
            }
            return false;
        } catch (Exception ex) {
            throw new Exception("Can't check '" + tab + "' tab is active. Reason is: " + ex.getMessage());
        }
    }

    /**
     * Return list of navigation tabs
     *
     * @return
     * @throws Exception
     */
    protected List<String> getNavigationTabs(String xpath) throws Exception {
        List<String> tabs = new ArrayList<>();
        int tabsCount = getCount(xpath, short_timeout);
        for (int i = 0; i < tabsCount; i++) {
            if (isVisibleByIndex(xpath, i, 1)) {
                tabs.add(WordUtils.capitalizeFully(getTextByIndex(xpath, i, 3).contains("\n") ? getTextByIndex(xpath, i, 1).split("\n")[1] : getTextByIndex(xpath, i, 1)));
            }
        }
        return tabs;
    }

    /**
     * Reload page until timeout is over or waiting condition is correct
     *
     * @param function
     * @param timeout
     * @return
     */
    public boolean reloadPageAndWait(Supplier<Boolean> function, int timeout) {
        long timeBeforeWait = System.currentTimeMillis();
        while ((timeBeforeWait + (1000 * timeout)) >= System.currentTimeMillis()) {
            if (function.get()) {
                return true;
            }
            reloadPage();
            waitForDOMLoaded();
        }
        return false;
    }

    /**
     * Revert result of input function
     *
     * @param function
     * @return
     */
    public Supplier<Boolean> functionRevert(Supplier<Boolean> function) {
        return () -> function.get().equals(Boolean.FALSE);
    }

    /**
     * Visibility element waiting condition
     *
     * @param xpath
     * @return function for reloadPageAndWait method
     */
    public Supplier<Boolean> visibleFunction(String xpath) {
        return () -> {
            try {
                return isVisible(xpath, short_timeout);
            } catch (Exception ex) {
                return false;
            }
        };
    }

    /**
     * Text is correct waiting condition
     *
     * @param xpath
     * @return function for reloadPageAndWait method
     */
    public Supplier<Boolean> textIsFunction(String xpath, String text) {
        return () -> {
            try {
                return getText(xpath, 3).equals(text);
            } catch (Exception ex) {
                return false;
            }
        };
    }

    /**
     * String contains in text list waiting condition
     *
     * @param xpath
     * @return function for reloadPageAndWait method
     */
    public Supplier<Boolean> textContainsInListFunction(String xpath, String text) {
        return () -> {
            try {
                return getAllText(xpath, 3).contains(text);
            } catch (Exception ex) {
                return false;
            }
        };
    }

    /**
     * Attribute contains in element waiting condition
     *
     * @param xpath
     * @return function for reloadPageAndWait method
     */
    public Supplier<Boolean> attributeContainsInElementFunction(String xpath, String attribute, String value) {
        return () -> {
            try {
                return getAttribute(xpath, attribute, 3).contains(value);
            } catch (Exception ex) {
                return false;
            }
        };
    }

    /**
     * Get text from selected option for typical drop-down ('select' tag -> 'option' tags)
     *
     * @param xpath must be an 'option' tag
     * @return String The text from an option has the attribute 'selected'
     * @throws Exception
     */
    public String getSelectedOption(String xpath) throws Exception {
        String selectedOptions = null;
        int options = getCount(xpath, short_timeout);
        for (int i = 0; i < options; i++) {
            if (getAttributeByIndex(xpath, i, "selected", 0) != null) {
                selectedOptions = getTextByIndex(xpath, i, 0);
                break;
            }
        }
        if (selectedOptions == null) {
            throw new Exception("Can't get selected option for element: " + xpath);
        }
        return selectedOptions;
    }

    /**
     * Return first 'href' attribute from element container
     *
     * @param xpath
     * @return
     * @throws Exception
     */
    public String getElementHref(String xpath, int elementIndex) throws Exception {
        try {
            String locator = wrapXPathWithIndex(xpath, elementIndex);
            if (getWebElement(locator, 0).getTagName().equalsIgnoreCase("a"))
                return getAttribute(locator, "href", 0);
            else {
                List<WebElement> wel = getWebElementsList(locator + "//a", 0);
                for (WebElement we : wel) {
                    String href = we.getAttribute("href");
                    if (href != null) {
                        return href;
                    }
                }
                throw new Exception("Element has no 'href' attribute");
            }
        } catch (Exception ex) {
            throw new Exception("No ability to get href attribute from element");
        }
    }

    /**
     * Return first 'href' attribute from element container
     *
     * @param xpath
     * @return
     * @throws Exception
     */
    public String getElementHref(String xpath) throws Exception {
        return getElementHref(xpath, 0);
    }
}
