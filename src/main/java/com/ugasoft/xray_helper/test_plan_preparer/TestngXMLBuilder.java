package com.ugasoft.xray_helper.test_plan_preparer;

import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import com.ugasoft.ui.common.core.Log;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class TestngXMLBuilder {

    /**
     * Generates a TestNG XML configuration string dynamically based on a set of test methods.
     * This XML can be used to run specified tests with TestNG programmatically.
     *
     * @param testMethods A set of Method objects representing the test methods to include in the TestNG suite.
     * @return A string representation of the TestNG XML configuration.
     * @throws ParserConfigurationException If a DocumentBuilder cannot be created which satisfies the configuration requested.
     * @throws TransformerException If an unrecoverable error occurs during the course of the transformation.
     */
    public static String generateTestNGXml(Set<Method> testMethods) throws ParserConfigurationException, TransformerException {
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
        Document document = documentBuilder.newDocument();

        // Create suite element
        Element suiteElement = document.createElement("suite");
        suiteElement.setAttribute("name", "custom xray suite");
        document.appendChild(suiteElement);

        // Create listeners element
        Element listenersElement = document.createElement("listeners");
        suiteElement.appendChild(listenersElement);

        // Create listener element
        // Create 1st element
        Element listenerElement1 = document.createElement("listener");
        listenerElement1.setAttribute("class-name", "com.SiemensXHQ.util.xray_helper.listeners.XrayJsonReporter");
        listenersElement.appendChild(listenerElement1);

         // Create 2nd element
        Element listenerElement2 = document.createElement("listener");
        listenerElement2.setAttribute("class-name", "com.SiemensXHQ.core.configuration.XHQListener");
        listenersElement.appendChild(listenerElement2);

        // Create 3rd element
        Element listenerElement3 = document.createElement("listener");
        listenerElement3.setAttribute("class-name", "com.SiemensXHQ.core.configuration.Listener");
        listenersElement.appendChild(listenerElement3);
        Log.info("Test methods is: " + testMethods.toString());
        for (Method method : testMethods) {
            Test annotation = method.getAnnotation(Test.class);
            if (annotation != null) {
                // Create test element
                Element testElement = document.createElement("test");
                testElement.setAttribute("name", annotation.description());
                suiteElement.appendChild(testElement);

                // Create classes element
                Element classesElement = document.createElement("classes");
                testElement.appendChild(classesElement);

                // Create class element
                Element classElement = document.createElement("class");
                classElement.setAttribute("name", method.getDeclaringClass().getName());
                classesElement.appendChild(classElement);

                // Create methods element
                Element methodsElement = document.createElement("methods");
                classElement.appendChild(methodsElement);

                // Create include element
                Element includeElement = document.createElement("include");
                includeElement.setAttribute("name", method.getName());
                methodsElement.appendChild(includeElement);
            }
        }

        // Transform the DOM object to an XML string
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        DOMSource domSource = new DOMSource(document);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        transformer.transform(domSource, result);

        return writer.toString();
    }
}
