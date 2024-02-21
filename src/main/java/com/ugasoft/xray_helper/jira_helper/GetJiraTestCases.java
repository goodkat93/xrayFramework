package com.ugasoft.xray_helper.jira_helper;

import com.ugasoft.ui.common.core.Log;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class GetJiraTestCases {

    /**
     * Fetches a set of test case keys from a specified Jira Test Plan.
     * This method authenticates with Jira and Xray APIs to retrieve test cases associated with the given Test Plan key.
     * <p>
     * The process involves encoding the Test Plan key, authenticating with Jira to fetch the issue ID of the Test Plan,
     * and then querying the Xray API with the issue ID to get the associated test cases. It paginates through all test cases
     * if more than the initial limit is set (default 100) until all test cases are fetched.
     *
     * @param jiraTestPlan The key of the Jira Test Plan from which to fetch test case keys.
     * @return A Set<String> containing the keys of all test cases associated with the specified Jira Test Plan.
     * @throws RuntimeException If an UnsupportedEncodingException occurs during the encoding of the Jira Test Plan key.
     */
    public static Set<String> getTestCases(String jiraTestPlan) {

        String token = getAuthToken();
        String jiraToken = readJiraToken();
        Log.info("jira token is: " + jiraToken);
        String encodedJiraTestPlan = null;
        try {
            encodedJiraTestPlan = URLEncoder.encode(jiraTestPlan, String.valueOf(StandardCharsets.UTF_8));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        String responseBody = RestAssured.given()
                .baseUri("https://ugasoft.atlassian.net/rest/api/3/issue/" + encodedJiraTestPlan)
                .header("Authorization", "Basic " + jiraToken)
                .contentType(ContentType.JSON)
                .get().getBody().asString();

        JSONObject jsonObject = new JSONObject(responseBody);
        String issueId = String.valueOf(jsonObject.getInt("id"));
        Log.info("jira issue id is: " + issueId);
        Set<String> testCases = new HashSet<>();

        int start = 0;
        int limit = 100;

        while (true) {
            String query = "{\"query\":\"{\\r\\n getTestPlan(issueId: \\\"" + issueId + "\\\") {\\r\\n issueId\\r\\n tests(limit: " + limit + ", start: " + start + ") {\\r\\n results {\\r\\n jira(fields:[\\\"key\\\"])\\r\\n }\\r\\n }\\r\\n }\\r\\n } \",\"variables\":{}}";

            Response response = RestAssured.given()
                    .baseUri("https://xray.cloud.getxray.app/api/v2/graphql")
                    .header("Authorization", "Bearer " + token)
                    .contentType(ContentType.JSON)
                    .body(query)
                    .post();

            responseBody = response.getBody().asString();
            Log.info("Response Body is " + responseBody);
            jsonObject = new JSONObject(responseBody);
            JSONArray results = jsonObject.getJSONObject("data").getJSONObject("getTestPlan").getJSONObject("tests").getJSONArray("results");

            if (results.isEmpty()) {
                break;
            }

            for (int i = 0; i < results.length(); i++) {
                JSONObject jira = results.getJSONObject(i).getJSONObject("jira");
                testCases.add(jira.getString("key"));
            }

            start += limit;
        }

        return testCases;
    }

    /**
     * Authenticates with the Xray API and retrieves an authentication token. The method reads authentication
     * details from a local file named 'xray_cloud_auth.json', constructs a POST request to the Xray API's
     * authentication endpoint, and returns the obtained token.
     * <p>
     * The authentication token is essential for subsequent API requests to Xray, enabling access to test management
     * and reporting functionalities.
     *
     * @return A string representing the authentication token for the Xray API.
     * @throws AssertionError If there's an error during the authentication process or the token cannot be retrieved.
     */
    public static String getAuthToken() {
        try {
            RestAssured.baseURI = "https://xray.cloud.getxray.app/api/v2/authenticate";

            String requestJson = IOUtils.toString(Files.newInputStream(Paths.get("xray_cloud_auth.json")), StandardCharsets.UTF_8);

            Response response = RestAssured.given()
                    .header("Content-Type", "application/json")
                    .body(requestJson)
                    .post();
            String responseBody = response.getBody().asString();
            return responseBody.replace("\"", "");
        } catch (Exception ex) {
            throw new AssertionError("No way for get token");
        }
    }

    public static String readJiraToken() {
        Properties properties = new Properties();
        try (InputStream inputStream = Files.newInputStream(Paths.get("src/main/resources/PropertyFiles/xray.properties"))) {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return properties.getProperty("jira.token");
    }
}
