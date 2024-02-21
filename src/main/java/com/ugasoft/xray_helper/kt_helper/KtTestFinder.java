package com.ugasoft.xray_helper.kt_helper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;

public class KtTestFinder {

    private static ClassLoader createClassLoader(Set<String> keys) {
        try {
            // Получаем путь к текущему рабочему каталогу
            String currentDir = System.getProperty("user.dir");
            String currentProjectName = currentDir.split("\\\\")[currentDir.split("\\\\").length - 1];
            File folder = new File(currentDir.replaceAll(currentProjectName, "") + "siemensxhq\\src\\test\\java\\com\\SiemensXHQ\\tests\\ui\\consolidated\\controls");
            URL url = folder.toURI().toURL();
            URL[] urls = new URL[]{url};

            ClassLoader cl = new URLClassLoader(urls);
            loadKotlinFilesRecursively(folder, keys, currentProjectName);

            return cl;
        } catch (Exception ignored) {
        }
        return null;
    }

    public static Set<KtMethod> loadKotlinFilesRecursively(File folder, Set<String> keys, String packageName) {
        Set<KtMethod> ktMethods = new HashSet<>();
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    ktMethods.addAll(loadKotlinFilesRecursively(file, keys, packageName + file.getName() + "."));
                } else if (file.getName().endsWith(".kt") || file.getName().endsWith(".java")) {
                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        String line;
                        boolean testAnnotationFound = false;
                        boolean xrayTestAnnotationFound = false;
                        String methodName = null;
                        String xrayKey = null;
                        String description = null;
                        String firstLine = reader.readLine();

                        while ((line = reader.readLine()) != null) {
                            if (line.contains("@Test")) {
                                testAnnotationFound = true;
                                if (line.contains("description = ")) {
                                    description = line.trim().split("\"")[1];  // Assumes the description is in quotes
                                }
                            }
                            if (line.contains("@XrayTest(key =")) {
                                xrayTestAnnotationFound = true;
                                xrayKey = line.split("\"")[1];  // Assumes the key is in quotes
                                if (!keys.contains(xrayKey)) {
                                    xrayTestAnnotationFound = false;
                                }
                            }
                            boolean foundedJavaOrKotlin = line.contains("fun ") || line.contains(" void ");
                            if (foundedJavaOrKotlin && xrayTestAnnotationFound) {
                                int splittedIndex = line.contains("fun ") ? 1 : 2;
                                methodName = line.trim().split(" ")[splittedIndex].replace("()", "");  // Assumes the method name is the second word after "fun"
                            }

                            if (testAnnotationFound && xrayTestAnnotationFound && methodName != null) {
                                String extension = file.getName().endsWith(".kt") ? ".kt" : ".java";
                                firstLine = firstLine.endsWith(";") ? firstLine.replace(";", "") : firstLine;
                                String declaringClassName = firstLine.trim().split(" ")[1] + "." + file.getName().replace(extension, "");
                                KtMethod ktMethod = new KtMethod(methodName, xrayKey, description, declaringClassName, methodName);
                                ktMethods.add(ktMethod);

                                // Reset flags for next method
                                testAnnotationFound = false;
                                xrayTestAnnotationFound = false;
                                methodName = null;
                                description = null;
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return ktMethods;
    }

}
