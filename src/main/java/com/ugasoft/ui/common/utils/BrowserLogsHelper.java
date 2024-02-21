package com.ugasoft.ui.common.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LogEntry;

import java.util.List;

public class BrowserLogsHelper {
    /**
     * Gets all the browser logs by the specified log type.
     * All the log types are placed in LogType class as a static members.
     *
     * @param driver
     * @param logType (can be taken from LogType)
     * @return List<LogEntry>
     * @throws Exception
     */
    public static List<LogEntry> getLogs(WebDriver driver, String logType) {
        return driver.manage().logs().get(logType).getAll();
    }
}
