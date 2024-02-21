package com.ugasoft.xray_helper.jira_helper;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.ugasoft.xray_helper.jira_helper.GetJiraTestCases.readJiraToken;

public class GetJiraTickets {

    public static void main(String[] args) {
        List<String[]> resultArray = fetchAllJiraIssues();
        System.out.println(resultArray);
    }

    /**
     * Fetches Jira issues from a specified project and extracts specific information from custom fields.
     * It paginates through all available issues in batches, as defined by the 'maxResults' variable, until
     * all issues have been processed. For each issue, it searches through the custom fields for values that
     * match a specific pattern (e.g., "C\\d++" to find strings that resemble issue keys), then collects these
     * values along with the issue key.
     * <p>
     * The method constructs a dynamic URL for pagination, sends HTTP GET requests to the Jira REST API, and
     * parses the JSON response to extract and collect data from custom fields of each issue.
     *
     * @return A list of string arrays, where each array contains a pair of strings: the issue key and the
     *         matched value from one of its custom fields.
     */
    public static List<String[]> fetchAllJiraIssues() {
        String baseUrl = "https://ugasoft.atlassian.net/rest/api/3/search";
        String authToken = readJiraToken();

        List<String[]> resultArray = new ArrayList<>();
        int startAt = 0;
        int maxResults = 100;
        int total;

        do {
            String url = baseUrl + "?startAt=" + startAt + "&maxResults=" + maxResults;

            Response response = RestAssured.given()
                    .header("Authorization", "Basic " + authToken)
                    .header("Content-Type", "application/json")
                    .get(url);

            String responseBody = response.getBody().asString();
            JSONObject jsonResponse = new JSONObject(responseBody);
            total = jsonResponse.getInt("total");
            JSONArray issues = jsonResponse.getJSONArray("issues");

            for (int i = 0; i < issues.length(); i++) {
                JSONObject issue = issues.getJSONObject(i);
                String key = issue.getString("key");
                JSONObject fields = issue.getJSONObject("fields");

                for (String fieldName : fields.keySet()) {
                    if (fieldName.startsWith("customfield_")) {
                        Object customValueObj = fields.opt(fieldName);
                        if (customValueObj != null && customValueObj instanceof String) {
                            String customValue = (String) customValueObj;
                            Matcher matcher = Pattern.compile("C\\d++").matcher(customValue);
                            if (matcher.find()) {
                                String[] pair = {key, matcher.group()};
                                resultArray.add(pair);
                            }
                        }
                    }
                }
            }

            startAt += maxResults;

        } while (startAt < total);

        return resultArray;
    }
}
