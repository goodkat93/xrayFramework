package com.ugasoft.xray_helper.kt_helper;

import com.ugasoft.ui.common.core.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.Set;

public class KtTestngXMLBuilder {

    public static String generateTestNGXml(Set<KtMethod> ktMethods) throws ParserConfigurationException, TransformerException {
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
        Document document = documentBuilder.newDocument();

        // Create suite element
        Element suiteElement = document.createElement("suite");
        suiteElement.setAttribute("name", "custom xray suite");
        document.appendChild(suiteElement);

        // Create listeners element
        // Create listeners element
        Element listenersElement = document.createElement("listeners");
        suiteElement.appendChild(listenersElement);

        // Create listener element
        // Создание элемента первого листенера
        Element listenerElement1 = document.createElement("listener");
        listenerElement1.setAttribute("class-name", "com.SiemensXHQ.util.xray_helper.listeners.XrayJsonReporter");
        listenersElement.appendChild(listenerElement1);

        // Создание элемента второго листенера
        Element listenerElement2 = document.createElement("listener");
        listenerElement2.setAttribute("class-name", "com.SiemensXHQ.core.configuration.XHQListener");
        listenersElement.appendChild(listenerElement2);

        // Создание элемента третьего листенера
        Element listenerElement3 = document.createElement("listener");
        listenerElement3.setAttribute("class-name", "com.SiemensXHQ.core.configuration.Listener");
        listenersElement.appendChild(listenerElement3);
        Log.info("Test methods is: " + ktMethods.toString());
        for (KtMethod ktMethod : ktMethods) {
            String description = ktMethod.getDescription();
            // Create test element
            Element testElement = document.createElement("test");
            testElement.setAttribute("name", description);
            suiteElement.appendChild(testElement);

            // Create classes element
            Element classesElement = document.createElement("classes");
            testElement.appendChild(classesElement);

            // Create class element
            Element classElement = document.createElement("class");
            classElement.setAttribute("name", ktMethod.getDeclaringClassName());
            classesElement.appendChild(classElement);

            // Create methods element
            Element methodsElement = document.createElement("methods");
            classElement.appendChild(methodsElement);

            // Create include element
            Element includeElement = document.createElement("include");
            includeElement.setAttribute("name", ktMethod.getMethodName());
            methodsElement.appendChild(includeElement);
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
