package com.ugasoft.xray_helper.test_plan_preparer;

import com.ugasoft.ui.common.core.Log;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class XrayTestFinder {

    /**
     * Finds all classes within a given package and its subpackages.
     *
     * @param packageName The name of the package to search within.
     * @return A set of Class<?> objects found within the specified package.
     * @throws IOException If an I/O error occurs while reading classes.
     * @throws ClassNotFoundException If a class cannot be found.
     */
    public static Set<Class<?>> findClassesInPackage(String packageName) throws IOException, ClassNotFoundException {
        Set<Class<?>> classes = new HashSet<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Log.info("Class loader loaded? " + classLoader);
        String packagePath = packageName.replace(".", "/");
        Enumeration<URL> resources = classLoader.getResources(packagePath);
        while (resources.hasMoreElements()) {
            System.out.println(resources);
            URL resource = resources.nextElement();
            Log.info("Class Loader resource: " + resource);
            if (resource.getProtocol().equalsIgnoreCase("FILE")) {
                File directory = new File(resource.getFile());
                findClassesInDir(packageName, directory, classes);
            } else if (resource.getProtocol().equalsIgnoreCase("JAR")) {
                String jarPath = resource.getPath().substring(5, resource.getPath().indexOf("!"));
                JarFile jar = new JarFile(URLDecoder.decode(jarPath, String.valueOf(StandardCharsets.UTF_8)));
                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String entryName = entry.getName();
                    if (entryName.endsWith(".class") && entryName.startsWith(packagePath) && entryName.length() > packagePath.length() + 5) {
                        String className = entryName.substring(0, entryName.length() - 6).replace('/', '.');
                        classes.add(Class.forName(className));
                    }
                }
                jar.close();
            }
        }
        return classes;
    }

    /**
     * Recursively searches for classes in a directory (and subdirectories) that correspond to a package.
     *
     * @param packageName The package name corresponding to the directory being searched.
     * @param dir The directory to search for classes.
     * @param classes A set of classes found in the directory.
     * @throws ClassNotFoundException If a class cannot be found.
     */
    private static void findClassesInDir(String packageName, File dir, Set<Class<?>> classes) throws ClassNotFoundException {
        File[] files = dir.listFiles();
        Log.info("files is: " + files);
        for (File file : Objects.requireNonNull(files)) {
            if (file.isDirectory()) {
                String subPackageName = packageName + "." + file.getName();
                findClassesInDir(subPackageName, file, classes);
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + "." + file.getName().substring(0, file.getName().lastIndexOf("."));
                try {
                    Class<?> clazz = Class.forName(className);
                    classes.add(clazz);
                } catch (ExceptionInInitializerError | NoClassDefFoundError ignored) {
                }
            }
        }
    }

    /**
     * Finds test methods within a specific package that have specific annotations and match given keys.
     *
     * @param keys A set of keys to match against the methods' annotations.
     * @return A set of Method objects that represent the matching test methods.
     * @throws ClassNotFoundException If a class cannot be found.
     * @throws IOException If an I/O error occurs during the search.
     */
    public static Set<Method> findTestMethods(Set<String> keys) throws ClassNotFoundException, IOException {
        String basePackage = "com.SiemensXHQ"; // Это должно быть имя пакета в основном проекте
        Log.info("package is: " + basePackage);
        Set<Method> testMethods = new HashSet<>();
        Set<Class<?>> classesInPackage = findClassesInPackage(basePackage); // Предполагается, что этот метод находит все классы в пакете
        Log.info("classesInPackage: " + classesInPackage);

        for (Class<?> clazz : classesInPackage) {
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                Annotation testAnnotation = getAnnotationByName(method, "Test"); // Замените на реальное имя аннотации
                Annotation xrayTestAnnotation = getAnnotationByName(method, "XrayTest"); // Замените на реальное имя аннотации

                if (testAnnotation != null && xrayTestAnnotation != null) {
                    try {
                        // Доступ к полям аннотации через рефлексию
                        String key = (String) xrayTestAnnotation.getClass().getMethod("key").invoke(xrayTestAnnotation);
                        if (keys.contains(key)) {
                            testMethods.add(method);
                        }
                    } catch (Exception ignored) {

                    }
                }
            }
        }
        return testMethods;
    }

    /**
     * Finds a specific annotation on a method by its name.
     *
     * @param method The method to search for the annotation.
     * @param annotationName The name of the annotation to find.
     * @return The annotation if found, null otherwise.
     */
    private static Annotation getAnnotationByName(Method method, String annotationName) {
        for (Annotation annotation : method.getAnnotations()) {
            if (annotation.annotationType().getSimpleName().equals(annotationName)) {
                return annotation;
            }
        }
        return null;
    }

    /**
     * Extracts the base package name from a full package name.
     *
     * @param packageName The full package name.
     * @return The base package name.
     */
    private static String extractBasePackage(String packageName) {
        return packageName.replaceAll("^(\\w+\\.\\w+).*", "$1");
    }

    /**
     * Determines if the current execution context is running from source code.
     *
     * @return true if running from source code, false otherwise.
     */
    private static boolean isRunningFromSourceCode() {
        String currentDirectory = System.getProperty("user.dir");
        File srcDirectory = new File(currentDirectory, "src");
        return srcDirectory.exists();
    }
}
