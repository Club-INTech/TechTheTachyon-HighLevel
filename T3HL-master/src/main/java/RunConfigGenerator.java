import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class RunConfigGenerator {

    public static void main(String[] args) {
        try {
            // Generation du DOM pour représenter le XML
            String filepath = "./.idea/workspace.xml";
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(filepath);

            // Récupération des configs déjà existantes
            Node configRoot = getRootConfigNode(doc);
            NodeList configs = configRoot.getChildNodes();

            Set<String> alreadyExisting = new HashSet<>();
            for(int i = 0; i < configs.getLength(); i++) {
                Node node = configs.item(i);
                if(node.getAttributes() != null) {
                    Node nameItem = node.getAttributes().getNamedItem("name");
                    if(nameItem != null) {
                        String name = nameItem.toString().substring("name=\"".length());
                        name = name.substring(0, name.length()-1);
                        System.out.println("Found run config: "+name);
                        alreadyExisting.add(name);
                    }
                }
            }

            // Génération des nouvelles configs
            String[] scripts = {"ZoneDeDepart", "PaletsX6", "Accelerateur", "Timeout", "AI", "SICK", "Lidar"};
            String[] configTypes = {"runOnRobot", "simulate"};
            for (String runConfig: scripts) {
                for (String runConfigType : configTypes) {
                    String testClassName = "Test" + runConfig;

                    String configName = testClassName+"."+runConfigType;
                    if(alreadyExisting.contains(configName))
                        continue;
                    System.out.print("Generating "+configName+"... ");
                /*
                Exemple:
                <configuration factoryName="JUnit" name="TestZoneDeDepart.runOnRobot" nameIsGenerated="true" temporary="true" type="JUnit">
                  <module name="TTHL-master"/>
                  <extension name="coverage">
                    <pattern>
                      <option name="PATTERN" value="scripts.*"/>
                      <option name="ENABLED" value="true"/>
                    </pattern>
                  </extension>
                  <option name="PACKAGE_NAME" value="scripts"/>
                  <option name="MAIN_CLASS_NAME" value="scripts.TestZoneDeDepart"/>
                  <option name="METHOD_NAME" value="runOnRobot"/>
                  <option name="TEST_OBJECT" value="method"/>
                  <method v="2">
                    <option enabled="true" name="Make"/>
                  </method>
                </configuration>
                 */
                    // Elément configuration
                    Element configuration = doc.createElement("configuration");
                    configuration.setAttribute("factoryName", "JUnit");
                    configuration.setAttribute("name", configName);
                    configuration.setAttribute("nameIsGenerated", "true");
                    configuration.setAttribute("temporary", "true");
                    configuration.setAttribute("type", "JUnit");

                    // Elément module
                    Element module = doc.createElement("module");
                    module.setAttribute("name", "TTHL-master");

                    // Extension & ses options
                    Element extension = doc.createElement("extension");
                    extension.setAttribute("name", "coverage");
                    Element pattern = doc.createElement("pattern");
                    Element optionPattern = doc.createElement("option");
                    optionPattern.setAttribute("name", "PATTERN");
                    optionPattern.setAttribute("value", "scripts.*");

                    Element optionEnabled = doc.createElement("option");
                    optionEnabled.setAttribute("name", "ENABLED");
                    optionEnabled.setAttribute("value", "true");

                    pattern.appendChild(optionPattern);
                    pattern.appendChild(optionEnabled);
                    extension.appendChild(pattern);

                    // package de la classe de test
                    Element packageName = doc.createElement("option");
                    packageName.setAttribute("name", "PACKAGE_NAME");
                    packageName.setAttribute("value", "scripts");

                    // nom fully-qualified de la classe de test
                    Element mainClassName = doc.createElement("option");
                    mainClassName.setAttribute("name", "MAIN_CLASS_NAME");
                    mainClassName.setAttribute("value", "scripts." + testClassName);

                    // nom de la méthode de test
                    Element methodName = doc.createElement("option");
                    methodName.setAttribute("name", "METHOD_NAME");
                    methodName.setAttribute("value", runConfigType);

                    // nom de la méthode de test
                    Element testObj = doc.createElement("option");
                    testObj.setAttribute("name", "TEST_OBJECT");
                    testObj.setAttribute("value", "method");

                    // méthode de build/run
                    Element method = doc.createElement("method");
                    method.setAttribute("v", "2");
                    Element option = doc.createElement("option");
                    option.setAttribute("enabled", "true");
                    option.setAttribute("name", "Make");
                    method.appendChild(option);

                    configuration.appendChild(module);
                    configuration.appendChild(extension);
                    configuration.appendChild(packageName);
                    configuration.appendChild(mainClassName);
                    configuration.appendChild(methodName);
                    configuration.appendChild(testObj);
                    configuration.appendChild(method);
                    configRoot.appendChild(configuration);

                    System.out.println("Done!");
                }
            }

            // écriture dans le fichier xml
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            //StreamResult result = new StreamResult(new File(".test.xml"));
            StreamResult result = new StreamResult(new File(filepath));
            transformer.transform(source, result);
        } catch (ParserConfigurationException | TransformerException | IOException | SAXException pce) {
            pce.printStackTrace();
        }
    }

    private static Node getRootConfigNode(Document doc) {
        NodeList allComponents = doc.getElementsByTagName("component");
        for (int i = 0; i < allComponents.getLength(); i++) {
            Node component = allComponents.item(i);
            String name = component.getAttributes().getNamedItem("name").toString().substring("name=\"".length());
            name = name.substring(0, name.length()-1);
            if(name.equals("RunManager")) {
                return component;
            }
        }
        return null;
    }
}
