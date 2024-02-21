package com.ugasoft.json_helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class JsonTestUpdater {

    public static void main(String[] args) throws Exception {
        String firstRunJson = new String(Files.readAllBytes(Paths.get("test-output/firstRun.json")));
        JSONObject firstRun = new JSONObject(firstRunJson);
        Path xrayReportPath = Paths.get("test-output/xray-report.json");
        try {
            String secondRunJson = new String(Files.readAllBytes(Paths.get("test-output/secondRun.json")));
            JSONObject secondRun = new JSONObject(secondRunJson);

            String thirdRunJson = new String(Files.readAllBytes(Paths.get("test-output/thirdRun.json")));
            JSONObject thirdRun = new JSONObject(thirdRunJson);

            updateTestResults(firstRun, secondRun, thirdRun);

        } finally {
            Files.write(xrayReportPath, firstRun.toString().getBytes());
        }
    }

    private static void updateTestResults(JSONObject firstRun, JSONObject secondRun, JSONObject thirdRun) throws JSONException {
        Map<String, String> testResults = new HashMap<>();

        // Собираем результаты из второго и третьего тест-ранов
        collectTestResults(secondRun, testResults);
        collectTestResults(thirdRun, testResults);

        // Обновляем результаты первого тест-рана
        JSONArray tests = firstRun.getJSONArray("tests");
        for (int i = 0; i < tests.length(); i++) {
            JSONObject test = tests.getJSONObject(i);
            String testKey = test.getString("testKey");
            if ("FAILED".equals(test.getString("status")) && "PASSED".equals(testResults.getOrDefault(testKey, "FAILED"))) {
                test.put("status", "PASSED");
            }
        }
    }

    private static void collectTestResults(JSONObject run, Map<String, String> testResults) throws JSONException {
        JSONArray tests = run.getJSONArray("tests");
        for (int i = 0; i < tests.length(); i++) {
            JSONObject test = tests.getJSONObject(i);
            String testKey = test.getString("testKey");
            String status = test.getString("status");
            if ("PASSED".equals(status)) {
                testResults.put(testKey, status);
            }
        }
    }
}
