package org.apache.catalina.realm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.XMLConstants;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.apache.catalina.LifecycleException;
import org.xml.sax.SAXException;


/**
 * Role to groups mappings.
 * 
 * @author  Ron Rickard
 */
public class RoleToGroupsMappings {

    private static final String SCHEMA_PATHNAME = "META-INF/xsd/role-to-groups.xsd";
    private static enum ELEMENT_NAME {
        ROLE_TO_GROUPS_MAPPINGS,
        ROLE_TO_GROUPS_MAPPING,
        ROLE_NAME,
        GROUP_DN;
    };
    private Map<String,List<String>> roleGroupsMap;

    /**
     * Private constructor.
     *
     * @param  pathname  the pathname (absolute or relative to Catalina's
     * current working directory) of the XML file containing the role to groups
     * mappings information.
     * 
     * @throws  LifecycleException  if unable to validate and parse the
     *                              pathname.
     */
    private RoleToGroupsMappings(String pathname)
            throws LifecycleException {

        // Declare.
        InputStream inputStream;

        // Initialize.
        inputStream = null;

        try {

            // Get the input stream for the pathname.
            inputStream = getInputStream(pathname);

            // Validate the input stream.
            validate(inputStream);
        }
        catch (Exception e) {
            throw new LifecycleException(
                    "Unable to validate the pathname " + pathname, e);
        }
        finally {
            
            // Check if the input stream exists.
            if (inputStream != null) {
                try {
                    
                    // Close the input stream.
                    inputStream.close();
                }
                catch (IOException e) {
                    // Ignore.
                }
            }
        }

        try {

            // Get the input stream for the pathname.
            inputStream = getInputStream(pathname);

            // Parse the input stream.
            this.roleGroupsMap = parse(inputStream);
        }
        catch (Exception e) {
            throw new LifecycleException(
                    "Unable to parse the pathname " + pathname, e);
        }
        finally {

            // Check if the input stream exists.
            if (inputStream != null) {
                try {

                    // Close the input stream.
                    inputStream.close();
                }
                catch (IOException e) {
                    // Ignore.
                }
            }
        }
    }

    /**
     * Get the element data.
     *
     * @param  event  the XML event.
     *
     * @return  the element data.
     */
    private static String getElementData(XMLEvent event) {
        return event.asCharacters().getData();
    }

    /**
     * Get the element name.
     *
     * @param  element  the end element.
     *
     * @return  the element name.
     */
    private static ELEMENT_NAME getElementName(EndElement element) {
        return getElementName(element.getName().getLocalPart());
    }

    /**
     * Get the element name.
     *
     * @param  element  the start element.
     *
     * @return  the element name.
     */
    private static ELEMENT_NAME getElementName(StartElement element) {
        return getElementName(element.getName().getLocalPart());
    }

    /**
     * Get the element name.
     *
     * @param  name  the name.
     *
     * @return  the element name.
     */
    private static ELEMENT_NAME getElementName(String name) {
        return ELEMENT_NAME.valueOf(name.toUpperCase().replaceAll("-", "_"));
    }

    /**
     * Get the group DNs for the role.
     * 
     * @param  role  the role.
     * 
     * @return  the group DNs for the role.
     */
    protected List<String> getGroupDNs(String role) {
        return (roleGroupsMap.get(role) != null) ?
                roleGroupsMap.get(role) : new ArrayList<String>();
    }

    /**
     * Get the roles for the group DN.
     * 
     * @param  groupDN  the group DN.
     * 
     * @return  the roles for the group DN.
     */
    protected List<String> getRoles(String groupDN) {

        // Declare.
        List<String> roles;
        Set<String> rolesInMap;

        // Initialize.
        roles = new ArrayList<String>();

        // Get the roles in the map.
        rolesInMap = roleGroupsMap.keySet();

        // Loop through the roles in the map.
        for (String roleInMap : rolesInMap) {

            // Declare.
            List<String> groupDNs;

            // Get the group DNs for the role in the map.
            groupDNs = getGroupDNs(roleInMap);

            // Check if the desired groupDN is in the group DNs.
            if (groupDNs.contains(groupDN)) {

                // Add the role in map to the roles to return.
                roles.add(roleInMap);
            }
        }

        return roles;
    }

    /**
     * Get the input stream for the pathname.
     *
     * @param  pathname  the pathname (absolute or relative to Catalina's
     * current working directory) of the XML file containing the role to groups
     * mappings information.
     *
     * @return  the input stream for the pathname.
     *
     * @throws  FileNotFoundException  if unable to get the input stream.
     */
    private static InputStream getInputStream(String pathname)
            throws FileNotFoundException {

        // Declare.
        File file;
        FileInputStream inputStream;

        // Get the file for the pathname.
        file = new File(pathname);

        // Check if the file does not have a absolute pathname.
        if (!file.isAbsolute()) {

            // The file has a relative pathname to Catalina's current
            // working directory.
            file = new File(System.getProperty("catalina.base"), pathname);
        }

        // Get the input stream for the file.
        inputStream = new FileInputStream(file);

        return inputStream;
    }

    /**
     * Get the schema source.
     *
     * @return  the schema source.
     */
    private static Source getSchemaSource() {
        return new StreamSource(Thread.currentThread().getContextClassLoader().getResourceAsStream(SCHEMA_PATHNAME));
    }

    /**
     * Create a new instance of this class.
     *
     * @param  pathname  the pathname (absolute or relative to Catalina's
     * current working directory) of the XML file containing the role to groups
     * mappings information.
     *
     * @return  a new instance of this class.
     * 
     * @throws  LifecycleException  if unable to create a new instance of this
     *                              class.
     */
    protected static RoleToGroupsMappings newInstance(String pathname)
            throws LifecycleException {
        return new RoleToGroupsMappings(pathname);
    }

    /**
     * Parse the configuration file.
     *
     * @param  inputStream  the input stream of the XML file containing the
     *                      role to groups mappings information.
     *
     * @throws  XMLStreamException  if unable to parse the input stream.
     */
    private static Map<String,List<String>> parse(InputStream inputStream)
            throws XMLStreamException {

        // Declare.
        XMLEventReader reader;
        Map<String,List<String>> roleGroupsMap;

        // Initialize.
        reader = null;
        roleGroupsMap = new HashMap<String,List<String>>();

        try {

            // Declare.
            String groupDN;
            List<String> groupDNs;
            String roleName;
            XMLInputFactory factory;

            // Initialize.
            groupDN = null;
            groupDNs = null;
            roleName = null;

            // Get the configuration file reader.
            factory = XMLInputFactory.newInstance();
            reader = factory.createXMLEventReader(inputStream);

            // Loop through the XML events.
            while (reader.hasNext()) {

                // Declare.
                XMLEvent event;

                // Get the next event.
                event = reader.nextEvent();

                // Check if the event is a start element.
                if (event.isStartElement()) {

                    switch(getElementName(event.asStartElement())) {

                        case ROLE_TO_GROUPS_MAPPING:

                            // Clear the role name, group DN, and group DNs.
                            roleName = null;
                            groupDN = null;
                            groupDNs = new ArrayList<String>();
                            break;

                        case ROLE_NAME:

                            // Get the role name.
                            roleName = getElementData(reader.nextEvent());
                            break;

                        case GROUP_DN:

                            // Get the group DN.
                            groupDN = getElementData(reader.nextEvent());

                            // Add the group DN to the group DNs.
                            groupDNs.add(groupDN);
                            break;
                    }
                }

                // Check if the event is an end element.
                else if (event.isEndElement()) {

                    switch(getElementName(event.asEndElement())) {

                        case ROLE_TO_GROUPS_MAPPING:

                            // Add the role to groups map.
                            roleGroupsMap.put(roleName, groupDNs);
                            break;
                    }
                }
            }
        }
        finally {

            // Check if the reader exists.
            if (reader != null) {

                try {
                    
                    // Close the reader.
                    reader.close();
                }
                catch(XMLStreamException e) {
                    // Ignore.
                }
            }

            // Check if the input stream exists.
            if (inputStream != null) {

                try {

                    // Close the input stream.
                    inputStream.close();
                }
                catch (IOException e) {
                    // Ignore.
                }
            }
        }

        return roleGroupsMap;
    }


    /**
     * Validate the input stream.
     *
     * @param  inputStream  the input stream of the XML file containing the
     *                      role to groups mappings information.
     * 
     * @throws  IOException   if unable to validate the input stream
     *                        due to an IO error.
     * @throws  SAXException  if unable to validate the input stream
     *                        due to a STAX parser error.
     */
    private static void validate(InputStream inputStream) 
            throws IOException, SAXException {

        // Declare.
        SchemaFactory factory;
        Schema schema;
        Validator validator;

        // Validate the input stream.
        factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        schema = factory.newSchema(getSchemaSource());
        validator = schema.newValidator();
        validator.validate(new StreamSource(inputStream));
    }
}
