package com.ugasoft.xray_helper.xml_to_csv_parser;

import com.opencsv.CSVWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestRailXmlToCsvConverter {

    /**
     * Converts TestRail XML test cases to a CSV format. This method reads an XML file containing test cases
     * extracted from TestRail, parses each test case, and then writes the relevant details to a CSV file.
     * The output CSV file includes columns for Issue Id, Issue Key, Test Type, Test Summary, Test Priority,
     * Action, Data, Result, Note, Preconditions, Bug Types, CR references, and Analyze flag.
     * <p>
     * The process involves:
     * - Opening and reading an XML file named 'input.xml' located in the user's current working directory.
     * - Creating a CSV file named 'output.csv' and writing the parsed test case data into it.
     * - Using DOM parsing to navigate through the XML structure and extract information about each test case.
     * - Writing each test case's information into the CSV file in the specified format.
     *
     * @param args Command line arguments (not used).
     * @throws Exception If there's an error in parsing the XML, writing to the CSV file, or any other operation.
     */
    public static void main(String[] args) throws Exception {
        // Initialize input XML file path
        File inputFile = new File(System.getProperty("user.dir") + File.separator + "input.xml");

        // Setup CSV file and CSVWriter with custom configurations
        File csvFile = new File("output.csv");
        FileWriter fileWriter = new FileWriter(csvFile);
        CSVWriter csvWriter = new CSVWriter(fileWriter, ';', CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);

        // Write CSV header row
        String[] header = {"Issue Id", "Issue key", "Test type", "Test Summary", "Test Priority", "Action", "Data", "Result", "Note", "Preconditions", "Bug Types", "CR references", "Analyze"};
        csvWriter.writeNext(header);

        // Parse input XML document using DOM parser
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputFile);
        NodeList sections = doc.getElementsByTagName("section");

        // Iterate through each section element in the XML
        for (int i = 0; i < sections.getLength(); i++) {
            Element section = (Element) sections.item(i);
            NodeList sectionChildNodes = section.getChildNodes();

            // Search for 'cases' elements within each section
            for (int j = 0; j < sectionChildNodes.getLength(); j++) {
                Node node = sectionChildNodes.item(j);
                if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals("cases")) {
                    Element cases = (Element) node;
                    NodeList casesChildNodes = cases.getChildNodes();

                    // Process each 'case' element found within 'cases'
                    for (int k = 0; k < casesChildNodes.getLength(); k++) {
                        Node caseNode = casesChildNodes.item(k);
                        if (caseNode.getNodeType() == Node.ELEMENT_NODE && caseNode.getNodeName().equals("case")) {
                            Element caseElement = (Element) caseNode;

                            Element customElement = (Element) caseElement.getElementsByTagName("custom").item(0);

                            // Extract relevant information from each case element
                            String id = getTagContent(caseElement, "id");
                            String title = getTagContent(caseElement, "title");
                            String priority = getTagContent(caseElement, "priority");
                            String automationStatus = getTagContent(customElement, "value");
                            String note = getTagContent(customElement, "note");
                            String precondition = getTagContent(customElement, "preconds");
                            String bugsList = getBugsList(customElement);
                            String references = getTagContent(caseElement, "references");
                            String optionalAnalysis = getTagContent(caseElement, "analyze_needed");

                            // Logic to adjust automation status based on references or bugs
                            if (!references.isEmpty() || !bugsList.isEmpty()) {
                                automationStatus = "Hold";
                            }

                            // Process steps for each test case
                            List<String> stepContents = new ArrayList<>();
                            List<String> expectedResults = new ArrayList<>();
                            try {
                                NodeList steps = customElement.getElementsByTagName("steps_separated").item(0).getChildNodes();
                                for (int l = 0; l < steps.getLength(); l++) {
                                    Node stepNode = steps.item(l);
                                    if (stepNode.getNodeType() == Node.ELEMENT_NODE && stepNode.getNodeName().equals("step")) {
                                        Element step = (Element) stepNode;
                                        stepContents.add(step.getElementsByTagName("content").item(0).getTextContent());
                                        expectedResults.add(step.getElementsByTagName("expected").item(0).getTextContent());
                                    }
                                }
                            } catch (NullPointerException npe) {
                                // Handle cases with missing steps
                            }

                            // Format and write each step as a row in the CSV
                            for (int l = 0; l < stepContents.size(); l++) {
                                int maxLength = 8000;
                                title = getFormattedString(title);
                                automationStatus = getFormattedString(automationStatus);
                                String action = stepContents.get(l).isEmpty() ? "step without action" : getFormattedString(stepContents.get(l));
                                String expectedResult = getFormattedString(expectedResults.get(l));
                                expectedResult = expectedResult.isEmpty() ? "Expection Results without data" : expectedResult.substring(0, Math.min(expectedResult.length(), maxLength));
                                note = getFormattedString(note);
                                note = note.length() > 255 ? note.substring(0, 255) : note;
                                precondition = getFormattedString(precondition);
                                optionalAnalysis = Boolean.parseBoolean(optionalAnalysis) ? "Need to analyze" : "";
                                String[] testCaseRow = {id, id, automationStatus, title, priority, action.trim(), "", expectedResult.trim(), note, precondition, bugsList, references, optionalAnalysis};
                                csvWriter.writeNext(testCaseRow);
                            }
                        }
                    }
                }
            }
        }
        // Close CSVWriter and FileWriter to save the CSV file
        csvWriter.close();
        fileWriter.close();
    }

    /**
     * Formats a given string for CSV output. This includes removing unnecessary spaces, newlines, and replacing
     * certain characters to ensure the string is CSV-friendly.
     *
     * @param string The string to format.
     * @return The formatted string.
     */
    private static String getFormattedString(String string) {
        return string.replaceAll(" ", "").replaceAll("\\n++ ++", " ").replaceAll(";", "--").replaceAll("\"", "'").replaceAll("\n", "");
    }

    /**
     * Retrieves the text content of a specified tag within an XML element. Applies formatting to the content
     * to remove unnecessary spaces and newlines, and replace quotes.
     *
     * @param element The XML element containing the tag.
     * @param tagName The name of the tag whose content is to be retrieved.
     * @return The formatted content of the specified tag, or an empty string if the tag is not found or an error occurs.
     */
    private static String getTagContent(Element element, String tagName) {
        try {
            return Optional.of(element.getElementsByTagName(tagName))
                    .map(n -> n.item(0))
                    .map(Node::getTextContent)
                    .orElse("").trim().replaceAll(" ", "").replaceAll(" ++", " ").replaceAll("\\n++ ++", " ").trim().replaceAll("\"", "'");
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Compiles a list of bugs from a custom XML element by checking specific tags. Each tag represents a different type
     * of bug (e.g., ChromeBug, SafariBug), and if the tag's content is "true", that bug type is added to the list.
     *
     * @param customElement The custom XML element containing the bug tags.
     * @return A comma-separated string listing the types of bugs found.
     */
    private static String getBugsList(Element customElement) {
        StringBuilder bugs = new StringBuilder();
        if ("true".equals(getTagContent(customElement, "chromebug"))) bugs.append("ChromeBug,");
        if ("true".equals(getTagContent(customElement, "edgebug"))) bugs.append("EdgeBug,");
        if ("true".equals(getTagContent(customElement, "safaribug"))) bugs.append("SafariBug,");
        if ("true".equals(getTagContent(customElement, "androidbug"))) bugs.append("AndroidBug,");
        if ("true".equals(getTagContent(customElement, "iosbug"))) bugs.append("IosBug,");

        // Remove trailing comma
        if (bugs.length() > 0) {
            bugs.setLength(bugs.length() - 1);
        }

        return bugs.toString();
    }

    /**
     * Determines whether a case should be skipped based on its automation status and other criteria.
     *
     * @param caseElement The XML element representing the case.
     * @return true if the case should be skipped, false otherwise.
     */
    private static boolean shouldSkipCase(Element caseElement) {
        Element customElement = (Element) caseElement.getElementsByTagName("custom").item(0);
        String automationStatus = getTagContent(customElement, "value");
        String references = getTagContent(caseElement, "references");
        boolean chromeBug = Boolean.parseBoolean(getTagContent(customElement, "chromebug"));
        boolean edgeBug = Boolean.parseBoolean(getTagContent(customElement, "edgebug"));
        boolean safariBug = Boolean.parseBoolean(getTagContent(customElement, "safaribug"));
        boolean androidBug = Boolean.parseBoolean(getTagContent(customElement, "androidbug"));
        boolean iosBug = Boolean.parseBoolean(getTagContent(customElement, "iosbug"));
        boolean optinalAnalyse = Boolean.parseBoolean(getTagContent(customElement, "analyze_needed"));
//        return !optinalAnalyse;
        return automationStatus.equals("Manual");
//        return !automationStatus.equals("Automated") ||
//                !references.isEmpty() ||
//                !(chromeBug ||
//                        edgeBug ||
//                        safariBug ||
//                        androidBug ||
//                        iosBug);
    }

    private static boolean shouldNotSkipCase(Element caseElement) {
        String caseId = getTagContent(caseElement, "id");
        System.out.println(caseId);
        String[] idsOnly = {
                "C431283", "C443976", "C431660", "C255137", "C444022", "C432189", "C431963",
                "C131467", "C131468", "C131469", "C131470", "C131471", "C432475", "C432286",
                "C444038", "C432464", "C444039", "C255951", "C375402", "C375941", "C444021",
                "C431421", "C431781", "C376331", "C431135"};
        return Arrays.asList(idsOnly).contains(caseId);
    }

    private static String getUUID(String input) {
        try {
            Pattern pattern = Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
            Matcher matcher = pattern.matcher(input);

            if (matcher.find()) {
                return matcher.group(0);
            }
        } catch (Exception ignored) {
            return "";
        }
        return "";
    }

    private static void changeIdToNumber() throws Exception {
        // Open the file for reading
        File inputFile = new File(System.getProperty("user.dir") + File.separator + "output.csv");
        BufferedReader br = new BufferedReader(new FileReader(inputFile));

        // Create a new CSV file for output
        File csvFile = new File("output-without-ID.csv");
        FileWriter fileWriter = new FileWriter(csvFile);
        CSVWriter csvWriter = new CSVWriter(fileWriter, ';', CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.DEFAULT_LINE_END);

        // Define the header of the new CSV file
        String[] header = {"Issue Id", "Issue key", "Test type", "Test Summary", "Test Priority", "Action", "Data", "Result"};
        csvWriter.writeNext(header);

        // Skip the first line (header) of the input file
        br.readLine();

        // Initialize a map to keep track of unique keys and their count
        Map<String, Integer> map = new HashMap<>();
        String line;
        int count = 0;

        // Read the file line by line, starting from the second line
        while ((line = br.readLine()) != null) {
            String[] parts = line.split(";;"); // Assuming ';;' is the delimiter
            String key = parts[0];

            // If the key is already in the map, use the existing count value
            if (map.containsKey(key)) {
                count = map.get(key);
            } else {
                // Otherwise, add a new key to the map with the value of 1 (incremented)
                count++;
                map.put(key, count);
            }

            // Prepare and write a new row to the output CSV file
            String[] testCaseRow = {String.valueOf(map.get(key)), ";" + parts[1]};
            csvWriter.writeNext(testCaseRow);
        }

        // Close the resources
        csvWriter.close();
        fileWriter.close();
        br.close();
    }
}