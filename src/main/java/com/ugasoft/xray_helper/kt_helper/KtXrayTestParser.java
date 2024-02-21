package com.ugasoft.xray_helper.kt_helper;

import com.ugasoft.ui.common.core.Log;
import com.ugasoft.xray_helper.jira_helper.GetJiraTestCases;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;

import static com.ugasoft.xray_helper.kt_helper.KtTestFinder.loadKotlinFilesRecursively;

public class KtXrayTestParser {

    public static void main(String[] args) {
        String jiraTestPlan = "SDX-4435";
        if (args.length > 0) {
            jiraTestPlan = args[0];
        }
        System.out.println("testPlanKey is: " + jiraTestPlan);
        generateXML(jiraTestPlan);
    }

    public static void generateXML(String jiraTestPlan) {
        Set<String> keys = GetJiraTestCases.getTestCases(jiraTestPlan);
        Log.info("Issue keys in testplan are: " + keys);

        // Замените путь на ваш путь к папке с Kotlin файлами
        String currentDir = System.getProperty("user.dir");
        String currentProjectName = currentDir.split("\\\\")[currentDir.split("\\\\").length - 1];
        File folder = new File(currentDir.replaceAll(currentProjectName, "")
                + readProperty("project.package"));
        Set<KtMethod> ktMethods = loadKotlinFilesRecursively(folder, keys, "");

        try {
            String xmlContent = KtTestngXMLBuilder.generateTestNGXml(ktMethods);  // Этот метод нужно адаптировать
            String propertyValue = readProperty("xml.save.dir");
            String saveDir = Optional.ofNullable(propertyValue)
                    .filter(str -> !str.isEmpty())
                    .map(str -> str + "/")
                    .orElse("");
            Log.info("Save xml file into save dir + " + saveDir);
            try (FileWriter fileWriter = new FileWriter(saveDir + "xray-testng.xml")) {
                fileWriter.write(xmlContent);
                fileWriter.flush();
            } catch (IOException e) {
                Log.info("Try to save into src/main/resources folder");
                try (FileWriter fileWriter = new FileWriter("src/main/resources/xray-testng.xml")) {
                    fileWriter.write(xmlContent);
                    fileWriter.flush();
                } catch (IOException ignored) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String readProperty(String property) {
        Properties properties = new Properties();
        try (InputStream inputStream = Files.newInputStream(Paths.get("src/main/resources/PropertyFiles/xray.properties"))) {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return properties.getProperty(property);
    }
}
