package org.apache.catalina.realm;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.catalina.LifecycleException;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 * Role to groups mappings test.
 *
 * @author  Ron Rickard
 */
public class RoleToGroupsMappingsTest {

    private static final String PATHNAME = "META-INF/role-to-groups.xml";
    private static final String MISSING_GROUP_DNS_PATHNAME = "META-INF/role-to-groups-missing-group-dns.xml";
    private static final String MISSING_ROLE_NAME_PATHNAME = "META-INF/role-to-groups-missing-role-name.xml";
    private static final String MISSING_ROLE_TO_GROUPS_MAPPING_PATHNAME = "META-INF/role-to-groups-missing-role-to-groups-mapping.xml";
    private static String pathname;
    private static String missingGroupDNsPathname;
    private static String missingRoleNamePathname;
    private static String missingRoleToGroupsMappingPathname;

    @BeforeClass
    public static void initialize() {

        // Declare.
        URL url;

        url = Thread.currentThread().getContextClassLoader().getResource(PATHNAME);
        pathname = url.getFile();
        
        url = Thread.currentThread().getContextClassLoader().getResource(MISSING_GROUP_DNS_PATHNAME);
        missingGroupDNsPathname = url.getFile();

        url = Thread.currentThread().getContextClassLoader().getResource(MISSING_ROLE_NAME_PATHNAME);
        missingRoleNamePathname = url.getFile();

        url = Thread.currentThread().getContextClassLoader().getResource(MISSING_ROLE_TO_GROUPS_MAPPING_PATHNAME);
        missingRoleToGroupsMappingPathname = url.getFile();
    }

    @Test
    public void testNewInstance() {
        try {
            RoleToGroupsMappings.newInstance(pathname);
        }
        catch (LifecycleException e) {
            fail();
        }
    }

    @Test
    public void testNewInstanceNullPathname() {
        try {
            RoleToGroupsMappings.newInstance(null);
            fail();
        }
        catch (LifecycleException e) { }
    }

    @Test
    public void testNewInstanceEmptyPathname() {
        try {
            RoleToGroupsMappings.newInstance("");
            fail();
        }
        catch (LifecycleException e) { }
    }

    @Test
    public void testNewInstanceMissingGroupDNs() {
        try {
            RoleToGroupsMappings.newInstance(missingGroupDNsPathname);
            fail();
        }
        catch (LifecycleException e) { }
    }

    @Test
    public void testNewInstanceMissingRoleName() {
        try {
            RoleToGroupsMappings.newInstance(missingRoleNamePathname);
            fail();
        }
        catch (LifecycleException e) { }
    }

    @Test
    public void testNewInstanceMissingRoleToGroupsMapping() {
        try {
            RoleToGroupsMappings.newInstance(missingRoleToGroupsMappingPathname);
            fail();
        }
        catch (LifecycleException e) { }
    }

    @Test
    public void testGetGroupDNs() {
        try {
            RoleToGroupsMappings mappings = RoleToGroupsMappings.newInstance(pathname);
            List<String> groupDNs = mappings.getGroupDNs("role1");
            assertEquals(Arrays.asList("cn=group1,ou=groups,dc=lazydog,dc=org","cn=group2,ou=groups,dc=lazydog,dc=org"), groupDNs);
        }
        catch (LifecycleException e) {
            fail();
        }
    }

    @Test
    public void testGetGroupDNsNot() {
        try {
            RoleToGroupsMappings mappings = RoleToGroupsMappings.newInstance(pathname);
            List<String> groupDNs = mappings.getGroupDNs("role");
            assertEquals(new ArrayList<String>(), groupDNs);
        }
        catch (LifecycleException e) {
            fail();
        }
    }

    @Test
    public void testGetGroupDNsNullRole() {
        try {
            RoleToGroupsMappings mappings = RoleToGroupsMappings.newInstance(pathname);
            List<String> groupDNs = mappings.getGroupDNs(null);
            assertEquals(new ArrayList<String>(), groupDNs);
        }
        catch (LifecycleException e) {
            fail();
        }
    }

    @Test
    public void testGetGroupDNsEmptyRole() {
        try {
            RoleToGroupsMappings mappings = RoleToGroupsMappings.newInstance(pathname);
            List<String> groupDNs = mappings.getGroupDNs("");
            assertEquals(new ArrayList<String>(), groupDNs);
        }
        catch (LifecycleException e) {
            fail();
        }
    }

    @Test
    public void testGetRoles() {
        try {
            RoleToGroupsMappings mappings = RoleToGroupsMappings.newInstance(pathname);
            List<String> roles = mappings.getRoles("cn=group1,ou=groups,dc=lazydog,dc=org");
            assertEquals(Arrays.asList("role1"), roles);
        }
        catch (LifecycleException e) {
            fail();
        }
    }

    @Test
    public void testGetRolesNot() {
        try {
            RoleToGroupsMappings mappings = RoleToGroupsMappings.newInstance(pathname);
            List<String> roles = mappings.getGroupDNs("cn=group,ou=groups,dc=lazydog,dc=org");
            assertEquals(new ArrayList<String>(), roles);
        }
        catch (LifecycleException e) {
            fail();
        }
    }

    @Test
    public void testGetRolesNullRole() {
        try {
            RoleToGroupsMappings mappings = RoleToGroupsMappings.newInstance(pathname);
            List<String> roles = mappings.getGroupDNs(null);
            assertEquals(new ArrayList<String>(), roles);
        }
        catch (LifecycleException e) {
            fail();
        }
    }

    @Test
    public void testGetRolesEmptyRole() {
        try {
            RoleToGroupsMappings mappings = RoleToGroupsMappings.newInstance(pathname);
            List<String> roles = mappings.getGroupDNs("");
            assertEquals(new ArrayList<String>(), roles);
        }
        catch (LifecycleException e) {
            fail();
        }
    }
}
