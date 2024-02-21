package com.ugasoft.xray_helper.test_plan_preparer;

import com.ugasoft.ui.common.core.Log;
import com.ugasoft.xray_helper.jira_helper.GetJiraTestCases;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.util.Set;

public class TestPlanPreparer {

    /**
     * Generates a TestNG XML configuration file for a given Jira Test Plan. It fetches test cases from Jira,
     * finds corresponding test methods in the project, and then creates an XML file to run these tests with TestNG.
     *
     * The method attempts to save the generated XML to 'src/test/resources'. If this fails, it tries to save to
     * 'src/main/resources' as a fallback.
     *
     * @param jiraTestPlan The Jira Test Plan key from which to fetch test cases.
     */
    public static void generateXML(String jiraTestPlan) {
        try {
            Set<String> keys = GetJiraTestCases.getTestCases(jiraTestPlan);
            Log.info("issue keys in testplan is: " + keys);
            Set<Method> testMethods = XrayTestFinder.findTestMethods(keys);
            try {
                String xmlContent = TestngXMLBuilder.generateTestNGXml(testMethods);
                Log.info("Save xml file into src/test/resources");
                try (FileWriter fileWriter = new FileWriter("src/test/resources/xray-testng.xml")) {
                    fileWriter.write(xmlContent);
                    fileWriter.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.info("Try to save into src/main/resources folder");
                    try (FileWriter fileWriter = new FileWriter("src/main/resources/xray-testng.xml")) {
                        fileWriter.write(xmlContent);
                        fileWriter.flush();
                    } catch (IOException secondE) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
