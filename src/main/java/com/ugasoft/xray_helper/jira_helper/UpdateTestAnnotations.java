package com.ugasoft.xray_helper.jira_helper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class UpdateTestAnnotations {
    
    public static void main(String[] args) throws IOException {
        List<String[]> jiraData = GetJiraTickets.fetchAllJiraIssues();
        File folder = new File(new File(System.getProperty("user.dir")).getParent() + "\\siemensxhq\\src\\test\\java\\com\\SiemensXHQ\\tests"); // Замените на путь к папке с тестами
        removeXrayTestAnnotations(folder);
        addXrayAnnotationsAccordingTestRailSuiteId(folder, jiraData);
    }

    /**
     * Adds XrayTest annotations to test methods in Java or Kotlin files within a specified directory (including subdirectories),
     * based on a mapping provided by a list of Jira issue keys and TestRail case IDs. This method iterates over each file,
     * and when it encounters a @Test annotation with a description that includes a TestRail case ID, it adds an @XrayTest
     * annotation with the corresponding Jira issue key just above the @Test annotation.
     *
     * Before adding the @XrayTest annotation, it checks if the import statement for the annotation is present and adds it
     * if missing. This ensures that the files are ready to use with the added annotations without further modification.
     *
     * @param folder The root folder to start searching for Java or Kotlin files.
     * @param jiraData A list of string arrays, each containing a pair of Jira issue key and the corresponding TestRail case ID.
     * @throws IOException If reading from or writing to the files fails.
     */
    public static void addXrayAnnotationsAccordingTestRailSuiteId(File folder, List<String[]> jiraData) throws IOException {
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                addXrayAnnotationsAccordingTestRailSuiteId(file, jiraData);
            } else if (file.getName().endsWith(".java") || file.getName().endsWith(".kt")) {
                Path path = Paths.get(file.getAbsolutePath());
                String content = new String(Files.readAllBytes(path));
                StringBuilder newContent = new StringBuilder();

                String[] lines = content.split("\n");
                boolean packageFound = false;
                boolean insideTestAnnotation = false;

                for (int i = 0; i < lines.length; i++) {
                    String line = lines[i];
                    newContent.append(line);

                    if (!packageFound && line.startsWith("package ")) {
                        packageFound = true;
                        if (!content.contains("import com.SiemensXHQ.util.xray_helper.annotations.XrayTest")) {
                            newContent.append("\nimport com.SiemensXHQ.util.xray_helper.annotations.XrayTest;");
                        }
                    }

                    if (line.contains("@Test(description = \"")) {
                        insideTestAnnotation = true;
                    }

                    if (insideTestAnnotation && line.contains(")")) {
                        insideTestAnnotation = false;
                        Matcher matcher = Pattern.compile("C\\d++").matcher(line);
                        if (matcher.find()) {
                            String cNumber = matcher.group();

                            for (String[] pair : jiraData) {
                                if (pair[1].equals(cNumber)) {
                                    newContent.append("\n    @XrayTest(key = \"").append(pair[0]).append("\")");
                                    break;
                                }
                            }
                        }
                    }

                    if (i < lines.length - 1) {
                        newContent.append("\n");
                    }
                }

                Files.write(path, newContent.toString().getBytes());
            }
        }
    }

    /**
     * Removes all @XrayTest annotations from Java or Kotlin files within a specified directory (including subdirectories).
     * This method iterates over each file and removes any @XrayTest annotations it finds, cleaning up the test files
     * and potentially preparing them for another process or simply removing the linkage between test cases and Xray tests.
     *
     * @param folder The root folder to start searching for Java or Kotlin files to remove @XrayTest annotations from.
     * @throws IOException If reading from or writing to the files fails.
     */
    public static void removeXrayTestAnnotations(File folder) throws IOException {
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                removeXrayTestAnnotations(file);
            } else if (file.getName().endsWith(".java") || file.getName().endsWith(".kt")) {
                Path path = Paths.get(file.getAbsolutePath());
                String content = new String(Files.readAllBytes(path));

                String regex = "\\s*@XrayTest\\(key = \".*\"\\)";
                String newContent = content.replaceAll(regex, "");

                Files.write(path, newContent.getBytes());
            }
        }
    }
}
