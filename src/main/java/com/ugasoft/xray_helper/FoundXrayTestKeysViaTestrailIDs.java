package com.ugasoft.xray_helper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FoundXrayTestKeysViaTestrailIDs {

    public static void main(String[] args) throws IOException {
        String[] idsOnly = {
                "C431283", "C443976", "C431660", "C255137", "C444022", "C432189", "C431963",
                "C131467", "C131468", "C131469", "C131470", "C131471", "C432475", "C432286",
                "C444038", "C432464", "C444039", "C255951", "C375402", "C375941", "C444021",
                "C431421", "C431781", "C376331", "C431135"};
        File folder = new File(new File(System.getProperty("user.dir")).getParent() + "\\siemensxhq\\src\\test\\java\\com\\SiemensXHQ\\tests");
        Set<String> xrayKeys = findXrayKeysForIds(folder, idsOnly);
        System.out.println("Found Xray keys: " + xrayKeys);
    }

    public static Set<String> findXrayKeysForIds(File folder, String[] ids) throws IOException {
        Set<String> keys = new HashSet<>();
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                keys.addAll(findXrayKeysForIds(file, ids));
            } else if (file.getName().endsWith(".java") || file.getName().endsWith(".kt")) {
                Path path = Paths.get(file.getAbsolutePath());
                String content = new String(Files.readAllBytes(path));
                String[] lines = content.split("\n");
                boolean insideTestAnnotation = false;
                String testDescription = "";

                for (String line : lines) {
                    if (line.contains("@Test(description = \"")) {
                        insideTestAnnotation = true;
                        testDescription = line;
                    }

                    if (insideTestAnnotation && line.contains("@XrayTest(key = \"")) {
                        String xrayKey = extractXrayKey(line);
                        if (isTestIdInDescription(testDescription, ids)) {
                            keys.add(xrayKey);
                        }
                        insideTestAnnotation = false;
                    }
                }
            }
        }
        return keys;
    }

    private static boolean isTestIdInDescription(String description, String[] ids) {
        for (String id : ids) {
            if (description.contains(id)) {
                return true;
            }
        }
        return false;
    }

    private static String extractXrayKey(String line) {
        Matcher matcher = Pattern.compile("@XrayTest\\(key = \"([^\"]*)\"\\)").matcher(line);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
