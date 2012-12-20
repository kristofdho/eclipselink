/*******************************************************************************
 * Copyright (c) 2012 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      gonural - initial 
 ******************************************************************************/
package org.eclipse.persistence.jpars.test.server;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.jpa.rs.PersistenceContext;
import org.eclipse.persistence.jpa.rs.PersistenceFactoryBase;
import org.eclipse.persistence.jpars.test.model.employee.Employee;
import org.eclipse.persistence.jpars.test.model.employee.EmployeeAddress;
import org.eclipse.persistence.jpars.test.model.employee.EmploymentPeriod;
import org.eclipse.persistence.jpars.test.model.employee.Gender;
import org.eclipse.persistence.jpars.test.model.employee.LargeProject;
import org.eclipse.persistence.jpars.test.model.employee.PhoneNumber;
import org.eclipse.persistence.jpars.test.model.employee.SmallProject;
import org.eclipse.persistence.jpars.test.util.DBUtils;
import org.eclipse.persistence.jpars.test.util.ExamplePropertiesLoader;
import org.eclipse.persistence.jpars.test.util.RestUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class ServerEmployeeTest {
    private static final String DEFAULT_PU = "jpars_employee-static";
    private static PersistenceContext context = null;
    private static PersistenceFactoryBase factory = null;
    private static long THREE_YEARS = 94608000000L;

    /**
     * Setup.
     *
     * @throws URISyntaxException the uRI syntax exception
     */
    @BeforeClass
    public static void setup() throws URISyntaxException {
        Map<String, Object> properties = new HashMap<String, Object>();
        ExamplePropertiesLoader.loadProperties(properties);
        properties.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, null);
        properties.put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.DROP_AND_CREATE);
        properties.put(PersistenceUnitProperties.CLASSLOADER, new DynamicClassLoader(Thread.currentThread().getContextClassLoader()));
        factory = new PersistenceFactoryBase();
        context = factory.bootstrapPersistenceContext(DEFAULT_PU, Persistence.createEntityManagerFactory(DEFAULT_PU, properties), RestUtils.getServerURI(), true);
    }

    /**
     * Tear down.
     */
    @AfterClass
    public static void tearDown() {
        //cleanResources();
    }


    /**
     * Cleanup.
     */
    @After
    public void cleanup() {
        cleanResources();
    }

    private static void cleanResources() {
        if (context != null) {
            if (context.getEmf() != null) {
                EntityManager em = context.getEmf().createEntityManager();
                if (em != null) {
                    em.getTransaction().begin();
                    em.createQuery("delete from EmployeeAddress a").executeUpdate();
                    em.createQuery("delete from PhoneNumber b").executeUpdate();
                    //em.createQuery("delete from Project c").executeUpdate();
                    em.createQuery("delete from LargeProject c").executeUpdate();
                    em.createQuery("delete from SmallProject d").executeUpdate();
                    em.createQuery("delete from Employee e").executeUpdate();
                    em.getTransaction().commit();
                }
            }
        }
    }

    /**
     * Test read employee json.
     *
     * @throws RestCallFailedException the rest call failed exception
     * @throws URISyntaxException the uRI syntax exception
     */
    @Test
    public void testReadEmployeeJSON() throws RestCallFailedException, URISyntaxException {
        readEmployee(MediaType.APPLICATION_JSON_TYPE);
    }

    /**
     * Test read employee xml.
     *
     * @throws RestCallFailedException the rest call failed exception
     * @throws URISyntaxException the uRI syntax exception
     */
    @Test
    public void testReadEmployeeXML() throws RestCallFailedException, URISyntaxException {
        readEmployee(MediaType.APPLICATION_XML_TYPE);
    }

    /**
     * Test update employee with employment period json.
     *
     * @throws RestCallFailedException the rest call failed exception
     * @throws URISyntaxException the uRI syntax exception
     */
    @Test
    public void testUpdateEmployeeWithEmploymentPeriodJSON() throws RestCallFailedException, URISyntaxException {
        updateEmployeeWithEmploymentPeriod(MediaType.APPLICATION_JSON_TYPE);
    }

    /**
     * Test update employee with employment period xml.
     *
     * @throws RestCallFailedException the rest call failed exception
     * @throws URISyntaxException the uRI syntax exception
     */
    @Test
    public void testUpdateEmployeeWithEmploymentPeriodXML() throws RestCallFailedException, URISyntaxException {
        updateEmployeeWithEmploymentPeriod(MediaType.APPLICATION_XML_TYPE);
    }

    /**
     * Test create employee with phone numbers json.
     *
     * @throws RestCallFailedException the rest call failed exception
     * @throws URISyntaxException the uRI syntax exception
     * @throws JAXBException the jAXB exception
     */
    @Test
    public void testCreateEmployeeWithPhoneNumbersJSON() throws RestCallFailedException, URISyntaxException, JAXBException {
        createEmployeeWithPhoneNumbers(MediaType.APPLICATION_JSON_TYPE);
    }

    /**
     * Test create employee with phone numbers xml.
     *
     * @throws RestCallFailedException the rest call failed exception
     * @throws URISyntaxException the uRI syntax exception
     * @throws JAXBException the jAXB exception
     */
    @Test
    public void testCreateEmployeeWithPhoneNumbersXML() throws RestCallFailedException, URISyntaxException, JAXBException {
        createEmployeeWithPhoneNumbers(MediaType.APPLICATION_XML_TYPE);
    }

    /**
     * Test update employee with manager json.
     *
     * @throws RestCallFailedException the rest call failed exception
     * @throws URISyntaxException the uRI syntax exception
     * @throws JAXBException the jAXB exception
     */
    @Test
    public void testUpdateEmployeeWithManagerJSON() throws RestCallFailedException, URISyntaxException, JAXBException {
        updateEmployeeWithManager(MediaType.APPLICATION_JSON_TYPE);
    }

    /**
     * Test update employee with manager xml.
     *
     * @throws RestCallFailedException the rest call failed exception
     * @throws URISyntaxException the uRI syntax exception
     * @throws JAXBException the jAXB exception
     */
    @Test
    public void testUpdateEmployeeWithManagerXML() throws RestCallFailedException, URISyntaxException, JAXBException {
        updateEmployeeWithManager(MediaType.APPLICATION_XML_TYPE);
    }

    /**
     * Test update employee with project json.
     *
     * @throws RestCallFailedException the rest call failed exception
     * @throws URISyntaxException the uRI syntax exception
     * @throws JAXBException the jAXB exception
     */
    @Test
    public void testUpdateEmployeeWithProjectJSON() throws RestCallFailedException, URISyntaxException, JAXBException {
        updateEmployeeWithProject(MediaType.APPLICATION_JSON_TYPE, false);
    }

    /**
     * Test update employee with project xml.
     *
     * @throws RestCallFailedException the rest call failed exception
     * @throws URISyntaxException the uRI syntax exception
     * @throws JAXBException the jAXB exception
     */
    @Test
    public void testUpdateEmployeeWithProjectXML() throws RestCallFailedException, URISyntaxException, JAXBException {
        updateEmployeeWithProject(MediaType.APPLICATION_XML_TYPE, false);
    }

    @Test
    public void testUpdateEmployeeWithProjectRemoveAllProjectsJSON() throws RestCallFailedException, URISyntaxException, JAXBException {
        updateEmployeeWithProject(MediaType.APPLICATION_JSON_TYPE, true);
    }

    /**
     * Test update employee with project xml.
     *
     * @throws RestCallFailedException the rest call failed exception
     * @throws URISyntaxException the uRI syntax exception
     * @throws JAXBException the jAXB exception
     */
    @Test
    public void testUpdateEmployeeWithProjectRemoveAllProjectsXML() throws RestCallFailedException, URISyntaxException, JAXBException {
        updateEmployeeWithProject(MediaType.APPLICATION_XML_TYPE, true);
    }

    /**
     * Test create employee address with binary data json.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws RestCallFailedException the rest call failed exception
     * @throws URISyntaxException the uRI syntax exception
     */
    @Test
    public void testCreateEmployeeAddressWithBinaryDataJSON() throws IOException, RestCallFailedException, URISyntaxException {
        createEmployeeAddressWithBinaryData(MediaType.APPLICATION_JSON_TYPE);
    }

    /**
     * Test create employee address with binary data xml.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws RestCallFailedException the rest call failed exception
     * @throws URISyntaxException the uRI syntax exception
     */
    @Test
    public void testCreateEmployeeAddressWithBinaryDataXML() throws IOException, RestCallFailedException, URISyntaxException {
        createEmployeeAddressWithBinaryData(MediaType.APPLICATION_XML_TYPE);
    }

    @Test
    public void testExecuteQueryGetAllXML() throws IOException, RestCallFailedException, URISyntaxException {
        executeQueryGetAll(MediaType.APPLICATION_XML_TYPE);
    }

    @Test
    public void testExecuteQueryGetAllJSON() throws IOException, RestCallFailedException, URISyntaxException {
        executeQueryGetAll(MediaType.APPLICATION_JSON_TYPE);
    }

    @Test
    public void testExecuteSingleResultQueryXML() throws IOException, RestCallFailedException, URISyntaxException {
        executeSingleResultQuery(MediaType.APPLICATION_XML_TYPE);
    }

    @Test
    public void testExecuteSingleResultQueryJSON() throws IOException, RestCallFailedException, URISyntaxException {
        executeSingleResultQuery(MediaType.APPLICATION_JSON_TYPE);
    }

    @Test
    public void testReadEmployeeWithResponsibilitiesXML() throws RestCallFailedException, URISyntaxException, JAXBException {
        readEmployeeWithResponsibilities(MediaType.APPLICATION_XML_TYPE);
    }

    @Test
    public void testReadEmployeeWithResponsibilitiesJSON() throws RestCallFailedException, URISyntaxException, JAXBException {
        readEmployeeWithResponsibilities(MediaType.APPLICATION_JSON_TYPE);
    }

    @Test
    public void testMultiselectQueryGetEmployeeAddressWithSimpleFieldsXML() throws RestCallFailedException, URISyntaxException, JAXBException {
        executeMultiselectQueryGetEmployeeAddressWithSimpleFields(MediaType.APPLICATION_XML_TYPE);
    }

    @Test
    public void testMultiselectQueryGetEmployeeAddressWithSimpleFieldsJSON() throws RestCallFailedException, URISyntaxException, JAXBException {
        executeMultiselectQueryGetEmployeeAddressWithSimpleFields(MediaType.APPLICATION_JSON_TYPE);
    }

    @Ignore
    // Test case to show that Manager is not marshalled properly, fix the bug and enable the test case
    public void testMultiselectQueryGetEmployeeWithDomainObjectJSON() throws IOException, RestCallFailedException, URISyntaxException, JAXBException {
        executeMultiselectQueryGetEmployeeWithDomainObject(MediaType.APPLICATION_JSON_TYPE);
    }

    @Ignore
    // Test case to show that Manager is not marshalled properly, fix the bug and enable the test case
    public void testMultiselectQueryGetEmployeeWithDomainObjectXML() throws IOException, RestCallFailedException, URISyntaxException, JAXBException {
        executeMultiselectQueryGetEmployeeWithDomainObject(MediaType.APPLICATION_XML_TYPE);
    }

    @Test
    public void testEmployeeAddressNamedQueryWithBinaryDataXML() throws IOException, RestCallFailedException, URISyntaxException, JAXBException {
        getEmployeeAddressNamedQueryWithBinaryData(MediaType.APPLICATION_XML_TYPE);
    }

    @Test
    public void testEmployeeAddressNamedQueryWithBinaryDataJSON() throws IOException, RestCallFailedException, URISyntaxException, JAXBException {
        getEmployeeAddressNamedQueryWithBinaryData(MediaType.APPLICATION_JSON_TYPE);
    }

    private void executeMultiselectQueryGetEmployeeWithDomainObject(MediaType mediaType) throws RestCallFailedException, URISyntaxException, JAXBException {
        // create an employee
        Employee employee = new Employee();
        employee.setId(90909);
        employee.setFirstName("Miles");
        employee.setLastName("Davis");
        employee.setGender(Gender.Male);

        employee = RestUtils.restCreate(context, employee, Employee.class.getSimpleName(), Employee.class, DEFAULT_PU, null, mediaType, true);
        assertNotNull("Employee create failed.", employee);

        // create a manager
        Employee manager = new Employee();
        manager.setId(1010);
        manager.setFirstName("Charlie");
        manager.setLastName("Parker");
        manager.setGender(Gender.Male);

        manager = RestUtils.restCreate(context, manager, Employee.class.getSimpleName(), Employee.class, DEFAULT_PU, null, mediaType, true);
        assertNotNull("Employee manager create failed.", manager);

        // update employee with manager
        RestUtils.restUpdateBidirectionalRelationship(context, String.valueOf(employee.getId()), Employee.class.getSimpleName(), "manager", manager, DEFAULT_PU, mediaType, "managedEmployees", true);

        // read manager and verify that the relationship is set correctly 
        manager = RestUtils.restRead(context, manager.getId(), Employee.class.getSimpleName(), Employee.class, DEFAULT_PU, null, mediaType);
        assertNotNull("Manager read failed.", manager);
        assertNotNull("Manager's managed employee list is null", manager.getManagedEmployees());
        assertTrue("Manager's managed employee list is empty", manager.getManagedEmployees().size() > 0);

        employee = RestUtils.restRead(context, employee.getId(), Employee.class.getSimpleName(), Employee.class, DEFAULT_PU, null, mediaType);
        assertNotNull("Manager read failed.", employee);

        for (Employee emp : manager.getManagedEmployees()) {
            assertNotNull("Managed employee's first name is null", emp.getFirstName());
            assertNotNull("Managed employee's last name is null", emp.getLastName());
        }

        // query
        Object queryResult = RestUtils.restNamedQuery("Employee.getManager", DEFAULT_PU, null, null, mediaType);
        String expected = null;
        if (mediaType == MediaType.APPLICATION_JSON_TYPE) {
            expected = "{\"List\":{\"item\":[{\"firstName\":[\"Miles\"],\"lastName\":[\"Davis\"],\"manager\":[\"Employee(1010: Parker, Charlie)\"]},{\"firstName\":[\"Charlie\"],\"lastName\":[\"Parker\"]}]}}";
        } else {
            expected = "<List>\r\n   <item>\r\n      <province>NY</province>\r\n      <postalcode>10005</postalcode>\r\n   </item>\r\n   <item>\r\n      <province>Ontario</province>\r\n      <postalcode>K1Y 6F7</postalcode>\r\n   </item>\r\n</List>";
        }
        assertTrue(((String) queryResult).contains(expected));

        // delete employee
        RestUtils.restDelete(employee.getId(), Employee.class.getSimpleName(), Employee.class, DEFAULT_PU, null, null, mediaType);

        // delete manager
        RestUtils.restDelete(manager.getId(), Employee.class.getSimpleName(), Employee.class, DEFAULT_PU, null, null, mediaType);
    }

    private void readEmployeeWithResponsibilities(MediaType mediaType) throws RestCallFailedException, URISyntaxException, JAXBException {
        // create an employee
        Employee employee = new Employee();
        employee.setId(1002);
        employee.setFirstName("Miles");
        employee.setLastName("Davis");

        employee.addResponsibility("team lead");
        employee.addResponsibility("standard lead");
        employee.addResponsibility("er team member");

        employee = RestUtils.restCreate(context, employee, Employee.class.getSimpleName(), Employee.class, DEFAULT_PU, null, mediaType, true);
        assertNotNull("Employee create failed.", employee);

        employee = RestUtils.restRead(context, new Integer(1002), Employee.class.getSimpleName(), Employee.class, DEFAULT_PU, null, mediaType);
        assertNotNull(employee.getResponsibilities());
        assertTrue(employee.getResponsibilities().size() == 3);

        // delete employee
        RestUtils.restDelete(new Integer(1002), Employee.class.getSimpleName(), Employee.class, DEFAULT_PU, null, null, mediaType);
    }

    private void executeQueryGetAll(MediaType mediaType) throws URISyntaxException, IOException {
        // create address1
        EmployeeAddress address1 = new EmployeeAddress("Newyork City", "USA", "NY", "10005", "Wall Street");
        address1 = RestUtils.restUpdate(context, address1, EmployeeAddress.class.getSimpleName(), EmployeeAddress.class, DEFAULT_PU, null, mediaType, true);
        assertNotNull("EmployeeAddress create failed.", address1);
        assertTrue("Newyork City".equals(address1.getCity()));
        assertTrue("USA".equals(address1.getCountry()));
        assertTrue("NY".equals(address1.getProvince()));
        assertTrue("10005".equals(address1.getPostalCode()));
        assertTrue("Wall Street".equals(address1.getStreet()));

        // create address2
        EmployeeAddress address2 = new EmployeeAddress("Ottawa", "Canada", "Ontario", "K1Y 6F7", "Main Street");
        address2 = RestUtils.restUpdate(context, address2, EmployeeAddress.class.getSimpleName(), EmployeeAddress.class, DEFAULT_PU, null, mediaType, true);
        assertNotNull("EmployeeAddress create failed.", address2);
        assertTrue("Ottawa".equals(address2.getCity()));
        assertTrue("Canada".equals(address2.getCountry()));
        assertTrue("Ontario".equals(address2.getProvince()));
        assertTrue("K1Y 6F7".equals(address2.getPostalCode()));
        assertTrue("Main Street".equals(address2.getStreet()));

        // query
        Object result = RestUtils.restNamedQuery("EmployeeAddress.getAll", DEFAULT_PU, null, null, mediaType);
        String expected1 = null;
        String expected2 = null;
        if (mediaType == MediaType.APPLICATION_JSON_TYPE) {
            expected1 = "{\"city\":\"Newyork City\",\"country\":\"USA\",\"id\":" + address1.getId() + ",\"postalCode\":\"10005\",\"province\":\"NY\",\"street\":\"Wall Street\",\"_relationships\":[]}";
            expected2 = "{\"city\":\"Ottawa\",\"country\":\"Canada\",\"id\":" + address2.getId()
                    + ",\"postalCode\":\"K1Y 6F7\",\"province\":\"Ontario\",\"street\":\"Main Street\",\"_relationships\":[]}";
        } else {
            expected1 = "<city>Newyork City</city><country>USA</country><id>" + address1.getId() + "</id><postalCode>10005</postalCode><province>NY</province><street>Wall Street</street>";
            expected2 = "<city>Ottawa</city><country>Canada</country><id>" + address2.getId() + "</id><postalCode>K1Y 6F7</postalCode><province>Ontario</province><street>Main Street</street>";
        }

        assertTrue(((String) result).contains(expected1));
        assertTrue(((String) result).contains(expected2));

        // delete employee address
        RestUtils.restDelete(address1.getId(), EmployeeAddress.class.getSimpleName(), EmployeeAddress.class, DEFAULT_PU, null, null, mediaType);
        RestUtils.restDelete(address2.getId(), EmployeeAddress.class.getSimpleName(), EmployeeAddress.class, DEFAULT_PU, null, null, mediaType);
    }

    private void executeMultiselectQueryGetEmployeeAddressWithSimpleFields(MediaType mediaType) throws URISyntaxException {
        // create address1
        EmployeeAddress address1 = new EmployeeAddress("Newyork City", "USA", "NY", "10005", "Wall Street");
        address1 = RestUtils.restUpdate(context, address1, EmployeeAddress.class.getSimpleName(), EmployeeAddress.class, DEFAULT_PU, null, mediaType, true);
        assertNotNull("EmployeeAddress create failed.", address1);
        assertTrue("Newyork City".equals(address1.getCity()));
        assertTrue("USA".equals(address1.getCountry()));
        assertTrue("NY".equals(address1.getProvince()));
        assertTrue("10005".equals(address1.getPostalCode()));
        assertTrue("Wall Street".equals(address1.getStreet()));

        // create address2
        EmployeeAddress address2 = new EmployeeAddress("Ottawa", "Canada", "Ontario", "K1Y 6F7", "Main Street");
        address2 = RestUtils.restUpdate(context, address2, EmployeeAddress.class.getSimpleName(), EmployeeAddress.class, DEFAULT_PU, null, mediaType, true);
        assertNotNull("EmployeeAddress create failed.", address2);
        assertTrue("Ottawa".equals(address2.getCity()));
        assertTrue("Canada".equals(address2.getCountry()));
        assertTrue("Ontario".equals(address2.getProvince()));
        assertTrue("K1Y 6F7".equals(address2.getPostalCode()));
        assertTrue("Main Street".equals(address2.getStreet()));

        // query
        String result = RestUtils.restNamedQuery("EmployeeAddress.getRegion", DEFAULT_PU, null, null, mediaType);
        String expected = null;
        if (mediaType == MediaType.APPLICATION_JSON_TYPE) {
            expected = "[{\"postalCode\":\"10005\",\"province\":\"NY\",\"street\":\"Wall Street\"},{\"postalCode\":\"K1Y 6F7\",\"province\":\"Ontario\",\"street\":\"Main Street\"}]";
        } else {
            expected = "<List><item><postalCode>10005</postalCode><province>NY</province><street>Wall Street</street></item><item><postalCode>K1Y 6F7</postalCode><province>Ontario</province><street>Main Street</street></item></List>";
        }

        assertTrue(((String) result).contains(expected));

        // delete employee address
        RestUtils.restDelete(address1.getId(), EmployeeAddress.class.getSimpleName(), EmployeeAddress.class, DEFAULT_PU, null, null, mediaType);
        RestUtils.restDelete(address2.getId(), EmployeeAddress.class.getSimpleName(), EmployeeAddress.class, DEFAULT_PU, null, null, mediaType);
    }

    private void executeSingleResultQuery(MediaType mediaType) throws URISyntaxException, IOException {
        EmployeeAddress address = new EmployeeAddress("Newyork City", "USA", "NY", "10005", "Wall Street");
        address.setId(9112);
        address.setAreaPicture(RestUtils.convertImageToByteArray("manhattan.png"));

        address = RestUtils.restCreate(context, address, EmployeeAddress.class.getSimpleName(), EmployeeAddress.class, DEFAULT_PU, null, mediaType, true);

        assertNotNull("EmployeeAddress create failed.", address);
        assertNotNull("EmployeeAddress area picture is null", address.getAreaPicture());
        assertTrue("Newyork City".equals(address.getCity()));
        assertTrue("USA".equals(address.getCountry()));
        assertTrue("NY".equals(address.getProvince()));
        assertTrue("10005".equals(address.getPostalCode()));
        assertTrue("Wall Street".equals(address.getStreet()));
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("id", address.getId());

        String result = RestUtils.restNamedSingleResultQuery("EmployeeAddress.getById", DEFAULT_PU, parameters, null, mediaType);
        assertNotNull(result);

        String expected = null;
        if (mediaType == MediaType.APPLICATION_JSON_TYPE) {
            expected = "{\"areaPicture\":\"iVBORw0KGgoAAAANSUhEUgAAAJYAAACWCAMAAAAL34HQAAADAFBMVEXP3uLv7+/m5ub39/f////e3t73/f8AxPFj2/YIxvFT1/bW9v3r+v2Z5/nw/P4wz/S27vshzPMQyPJY2PaL5PnP9Pzf+P194fjm+f687/s70vTD8fyE4/hK1fU60fRr3fcXyfKV5vkZyvJy3veo6/qm6vqs7PopzfNF0PFD1PWG4faI2u5u2fHp9Paj3erV3d+i4O/X8ffP5uoAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA6TXZvAAAKCElEQVR42uVc23LcNhJFN+BYluQkvsiWK0kl2a1N7dP+/2fsW97XTnmTUnzZtWVJg0ZvAwQ5IIcXcIjhqGrHN0kzJg9Pd5++ACQYdR9fqP7/YLHj+wjLaQTiewcL5I9BRzV5dJ98S5tImTPZRoUDRiIn10wA4Vurj86WSylD4NquR4bVgaDvicv34dB8bFj9cecDcxqaWc2GCYcSmQjHisTxExM/cMcwIo+bVxs+Cixl9/S+w8IC/Xf913E+DQxRqg+E61w9/3zl8Eu/zmLFFSqGddl6TFdnl3hFI8HAZtgBDxWID5S60YM+TaISFnymXNeIL3/3pFjuTzgIAkfeo0H1OpAR/WEvvwzaiG2Q1WENOQxbF1dKPfu3IjNcW1Ru71nr092DwELcKLUh1sMfUBBcC3xlvZYRf7gWhbgdOzaF9BMrMl6HrfN33k5EOJovnRCmRbeYcR227ry/345qjxBkoPpXrxWJr+TPrZpqcxzESpFXgiXR//IqR6iBBotYPIBmbdTG0uiB63dhICkegK1nn9XZ7UQRGBkCu16qfq/UiZvoc3iiGyoP6wT82Ti/hVwFFj5zXpZgSZ1/AJfXn7yN7iZOzBPIirP1aJwMqtngVY346HpBy30wWGgmWhzuaOk6sL6+7q0JNFGFjnK6seKwTs8H4l+HSaAOtTuRY2ldWa8G69HbxnE6ZBgESZUAcj7wFTwAOFoJ1vP/xC8+/9wEo7XV2dlXyVIg29jtOOkz3DoTmw+NT23Q+aZLUrJQQ1p+SZls2xEIwKuw9XB7mrcX6skv/qpZfoaMVXGlIybmKa0vqfLppI/v6MPf/tACxw+zQkNR/dx3FhoI4oe5H1tBVC8TF37xSb1wrzGkR2Nq5ybnkIK2GWQIwsX9cl+SrKTkeqsufj/h+gzauDjE0QLTJvmaB4qJcrBCfx9fH9VfPuDD68CIqxRLqUqp9LYvFBOj7g+6crASY0hxeuXO3vmDC0/orLXSsMbGS3f47Z3OFZudniejLJGC829+C/1y3VqzfCN+PtgCdWr6UpF4AslVX1zz3X+rCQR6bZD+WYMf1DgcgEXawSGMeHnTromZ67mIKIEx4uo80BIGXQFxNCwPC//szNzctsSJ63VII17pu2vm4rAep9Xf6eakxUYEaGhA1nV8IwFeBtZFGk5o390NtKwDSdDEnxtXFNbJaepZz/DW9amRJG1t7Whm1vU6aBFYlykLL79Iq+yGekNDoxmibj1KyNaTNwms7/D2tn/Cp4kzVBLD+mwJWCYl693d3dDMVE93PP5oHlcBI55+TL75/saEdYAlV+mKqDwlVeZP1/xZ8cJjSmIoAOtsk7j1p+vhsXd+1ocCRkxi6/v3N2XctQCsrROfvhbBpBJHXA4Lt1J67nzOZVsA2GJciYPfxKSsHC4MouVsnV40X77yxRNw9t6Q4aHcclgnW2e6rQsZsLSIrAKw3Na3zqrCSfKeRreErAKwPlz06DQr2M/xyTpf1i9vMZIVVH3X+L/V+2g91RXXYrZOfkxq1LSggT3cq5nhLIb16l+pNptEY/fYFFiuOk3aw+/+bAiyat5+LdUZ4SyGleRpdu3DLgjGxbCeBp7g28dfff2akx6L9/GuhmBTwog/vbHvFX1K5lvSrkqjg6SPxZZ40fmbx/9gRtNeyteShZDWh/Uw/P3HVz9ubp+G0xvYiat9Nw8Xmdg8vfxVDcxi6lKV8gITXEGXP/t1wBRczduUZZxnz71hPX1S9RQfX6jn34wPPSqfy7InLfatcJonv3mh/+eYWayIJLuZvr93dfol1DM/fHj1lq4G3UaqVOenuKwZIKc2rIeCC13+IdywGz5EJfsctmplNWoOCkXi+MnCANJEm0M+rMWROH5ZSDWZec4FpXTL5eYXcNNWbLaaLO/HcmNsEzWsgA2ylI+ya6v8VFRgYCOxn4PKzJGuxbC0Vi53az6uaEQHOvNzwYhUrWP3/5+mYlvMFuXW7JCs5Wt9sJxYN+ZaHeC1dJ6Yjwrq2n7Q9aWh1nul6nCvWppDbIZv1hrp14F9gmTXT4ZL+nMzIDCwA8hfsL+IdOWGc6oC3dTQ4eMekuvbMgITRTMZ0hbrbeVhLT61FdUlSvaUQaJPfvt5e3MdO4mIWhT2GFFyK/qBh98oICRpcQnTWuurrZI985O2CG04bdLfdv53m79dWM11IIq1EcPG6G5xNy9e0PMa/CpUqtV1AbeAidviiBGnLVOTNWNSRFCh8ku/jI29EktS+74MnI1qd7wy1S/I6aHawGwhFhyVmuvtvg1suzzOPdc2sia1IW4mi2HsncGnReeXf+P6HMCONOwvp9n1ia4qLAljFMFj55yEYxAJ64OS/N0PQp+Dyc4nx4jRuXI0C21cGA9uLv0P+ojz3u00oTgb+CULLpJ85lRORnsDUeUfFHbv22oTv9+x4dnc9A3Lzf4wZ0yJIGgpgGom0AIWA3+iiz33w6Ka7/PxWjJczPu8/5jxptPgjedlQHuhIBdPZzMqCHbZTq+nBUL7nOJb6yqLST5DNDGrYVQuNoaYqT0O2K+wwazZaLzRor5SCC4GGw78oGReskjgrOQ2AVtij02Ir6mFCsFBTLaphC1AqD9cFH4tFQnuFsx9sHJhuupIbmjNKXRpNqyTQCUA3rmNw1g5EoiUAW2SM6JalBM7/o79N5YTb6M8fEKEKoq6UM1GUqP4vZaPNUUTL4zEVvQ4tP0pajv/Tjf2sPAk1hPJEH/yQdqbw7psZZLVzhc9wwVf9kGzIaJZy/GUqXCjRvduDb/1bFhOOUspO3bTvSW92bK40Q0+XRV8aU2zWwV2YXGWcudMaeTKtvO/5AvnywWOPApFD3pr3I4RKxtOkLXr4z1WtKGJRqguoWX0eFVjt6Kb+emu501Ie2uqOicGQ9hJUSIY5PfEBkhjXtxhy4vcRPu3cxeWrwugNYmgqsDz4ELPFFddmqWDaVfpwAJiSJSkd1bqRvq7XUMT1LaKtrOYUah1YDnvi2PTjp471loBQNFBd25e5oqizId6tGFxJR7DuHrvowtnrPDAePDaB5lLn21YzYUP+PyuBbOGmK5yi2xQXVgwEYv7rMNBtp8PVQy823XtkS+3NIGjKFjSCM6ag7XYGh+cE8zY0ZMuuUw+wmNETkUWJvbFzEFFzTBFMO1RatZszVhhyHaoPVjqsDWNatZOMZ9k+MGCAWhgi6ejxM7yWF68scxMUCXZSM8zYImXmRgmEM6mag1YXN3dsfYLp9/nIzyWDkfbUOs0AKjjwBqkjLS0LPZosAbU3WJ203EQWP315XEAjbUYc+uQw8DiHUx45IdCeljdbR/u+A+qxN0lwaOEXnewgjZ96KDrX1M7ghFTboxT94CqYESdirq6Jy9Mlo5J3xdUfnHFm82q/ocCHY8tP0v0C0QA9wfVQR8suuD1P0iRvjbgSsgzAAAAAElFTkSuQmCC\",\"city\":\"Newyork City\",\"country\":\"USA\",\"id\":9112,\"postalCode\":\"10005\",\"province\":\"NY\",\"street\":\"Wall Street\",\"_relationships\":[]}";
        } else {
            expected = "<areaPicture>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</areaPicture><city>Newyork City</city><country>USA</country><id>9112</id><postalCode>10005</postalCode><province>NY</province><street>Wall Street</street>";
        }

        assertTrue(result.contains(expected));

        // delete employee address
        RestUtils.restDelete(address.getId(), EmployeeAddress.class.getSimpleName(), EmployeeAddress.class, DEFAULT_PU, null, null, mediaType);
    }

    private void createEmployeeAddressWithBinaryData(MediaType mediaType) throws IOException, RestCallFailedException, URISyntaxException {
        EmployeeAddress address = new EmployeeAddress("Newyork City", "USA", "NY", "10005", "Wall Street");
        address.setAreaPicture(RestUtils.convertImageToByteArray("manhattan.png"));

        address = RestUtils.restUpdate(context, address, EmployeeAddress.class.getSimpleName(), EmployeeAddress.class, DEFAULT_PU, null, mediaType, true);
        assertNotNull("EmployeeAddress create failed.", address);
        assertNotNull("EmployeeAddress area picture is null", address.getAreaPicture());
        assertTrue("Newyork City".equals(address.getCity()));
        assertTrue("USA".equals(address.getCountry()));
        assertTrue("NY".equals(address.getProvince()));
        assertTrue("10005".equals(address.getPostalCode()));
        assertTrue("Wall Street".equals(address.getStreet()));

        // delete employee address
        RestUtils.restDelete(address.getId(), EmployeeAddress.class.getSimpleName(), EmployeeAddress.class, DEFAULT_PU, null, null, mediaType);
    }

    private void updateEmployeeWithProject(MediaType mediaType, boolean removeAllProjects) throws RestCallFailedException, URISyntaxException, JAXBException {
        // create an employee
        Employee employee = new Employee();
        employee.setId(8809);
        employee.setFirstName("Charles");
        employee.setLastName("Mingus");
        employee.setGender(Gender.Male);

        employee = RestUtils.restCreate(context, employee, Employee.class.getSimpleName(), Employee.class, DEFAULT_PU, null, mediaType, true);
        assertNotNull("Employee create failed.", employee);

        // create a small project
        SmallProject smallProject = new SmallProject("SmallProject", "This is a small project.");
        smallProject.setId(109);

        smallProject = RestUtils.restCreate(context, smallProject, SmallProject.class.getSimpleName(), SmallProject.class, DEFAULT_PU, null, mediaType, true);
        assertNotNull("SmallProject create failed.", smallProject);

        // update employee with small project
        RestUtils.restUpdateBidirectionalRelationship(context, String.valueOf(employee.getId()), Employee.class.getSimpleName(), "projects", smallProject,
                DEFAULT_PU, mediaType, "teamLeader", true);

        // create a large project
        LargeProject largeProject = new LargeProject();
        largeProject.setId(110);
        largeProject.setName("LargeProject");
        largeProject.setBudget(100000);

        largeProject = RestUtils.restCreate(context, largeProject, LargeProject.class.getSimpleName(), LargeProject.class, DEFAULT_PU, null, mediaType, true);
        assertNotNull("LargeProject create failed.", largeProject);

        // update employee with large project
        RestUtils.restUpdateBidirectionalRelationship(context, String.valueOf(employee.getId()), Employee.class.getSimpleName(), "projects", largeProject,
                DEFAULT_PU, mediaType, "teamLeader", true);

        // read employee and verify that the relationship is set correctly for the projects 
        String employeeUpdated = RestUtils.restRead(context, employee.getId(), Employee.class.getSimpleName(), DEFAULT_PU, null, mediaType);
        assertNotNull("Employee read failed.", employeeUpdated);
        assertTrue(employeeUpdated.contains("SmallProject/109"));
        assertTrue(employeeUpdated.contains("LargeProject/110"));

        String expectedEmplWithoutLargeProject = null;
        String expectedEmplWithoutAnyProject = null;

        if (mediaType == MediaType.APPLICATION_JSON_TYPE) {
            expectedEmplWithoutLargeProject = "\"projects\":[{\"_link\":{\"href\":\"" + RestUtils.getServerURI()
                    + "jpars_employee-static/entity/SmallProject/109\",\"method\":\"GET\",\"rel\":\"self\"}}]";

            expectedEmplWithoutAnyProject = ("\"_relationships\":[{\"_link\":{\"href\":\""
                    + RestUtils.getServerURI()
                    + "jpars_employee-static/entity/Employee/8809/manager\",\"rel\":\"manager\"}},{\"_link\":{\"href\":\""
                    + RestUtils.getServerURI()
                    + "jpars_employee-static/entity/Employee/8809/address\",\"rel\":\"address\"}},{\"_link\":{\"href\":\""
                    + RestUtils.getServerURI()
                    + "jpars_employee-static/entity/Employee/8809/responsibilities\",\"rel\":\"responsibilities\"}},{\"_link\":{\"href\":\""
                    + RestUtils.getServerURI()
                    + "jpars_employee-static/entity/Employee/8809/projects\",\"rel\":\"projects\"}},{\"_link\":{\"href\":\""
                    + RestUtils.getServerURI()
                    + "jpars_employee-static/entity/Employee/8809/managedEmployees\",\"rel\":\"managedEmployees\"}},{\"_link\":{\"href\":\"" + RestUtils.getServerURI() + "jpars_employee-static/entity/Employee/8809/phoneNumbers\",\"rel\":\"phoneNumbers\"}}],\"managedEmployees\":[],\"phoneNumbers\":[],\"projects\":[]}");
        } else {
            expectedEmplWithoutLargeProject = "<projects><_link href=\"" + RestUtils.getServerURI() + "jpars_employee-static/entity/SmallProject/109\" method=\"GET\" rel=\"self\"/></projects>";

            expectedEmplWithoutAnyProject = "<_relationships><_link href=\""
                    + RestUtils.getServerURI()
                    + "jpars_employee-static/entity/Employee/8809/manager\" rel=\"manager\"/></_relationships><_relationships><_link href=\""
                    + RestUtils.getServerURI()
                    + "jpars_employee-static/entity/Employee/8809/address\" rel=\"address\"/></_relationships><_relationships><_link href=\""
                    + RestUtils.getServerURI()
                    + "jpars_employee-static/entity/Employee/8809/responsibilities\" rel=\"responsibilities\"/></_relationships><_relationships><_link href=\""
                    + RestUtils.getServerURI()
                    + "jpars_employee-static/entity/Employee/8809/projects\" rel=\"projects\"/></_relationships><_relationships><_link href=\""
                    + RestUtils.getServerURI()
                    + "jpars_employee-static/entity/Employee/8809/managedEmployees\" rel=\"managedEmployees\"/></_relationships><_relationships><_link href=\""
                    + RestUtils.getServerURI() + "jpars_employee-static/entity/Employee/8809/phoneNumbers\" rel=\"phoneNumbers\"/></_relationships></employee>";
        }

        if (removeAllProjects) {
            // remove all projects
            String allProjectsRemoved = RestUtils.restRemoveBidirectionalRelationship(context, String.valueOf(employee.getId()), Employee.class.getSimpleName(), "projects", DEFAULT_PU,
                    mediaType, "teamLeader", null, true);
            System.out.println("allProjects: " + allProjectsRemoved);
            System.out.println("expected: " + expectedEmplWithoutAnyProject);

            assertTrue(allProjectsRemoved.contains(expectedEmplWithoutAnyProject));
        } else {
            // Disassociate large project from the employee
            String largeProjectDisassociated = RestUtils.restRemoveBidirectionalRelationship(context, String.valueOf(employee.getId()), Employee.class.getSimpleName(), "projects", DEFAULT_PU,
                    mediaType, "teamLeader", String.valueOf(largeProject.getId()), true);
            assertTrue(largeProjectDisassociated.contains(expectedEmplWithoutLargeProject));

            // read employee to make sure he has only small project
            String employeeNoLargeProject = RestUtils.restRead(context, employee.getId(), Employee.class.getSimpleName(), DEFAULT_PU, null, mediaType);
            assertTrue(employeeNoLargeProject.contains(expectedEmplWithoutLargeProject));

            // Disassociate small project from the employee
            String smallProjectDisassociated = RestUtils.restRemoveBidirectionalRelationship(context, String.valueOf(employee.getId()), Employee.class.getSimpleName(), "projects", DEFAULT_PU,
                    mediaType, "teamLeader", String.valueOf(smallProject.getId()), true);
            assertTrue(smallProjectDisassociated.contains(expectedEmplWithoutAnyProject));
        }
        // delete projects 
        RestUtils.restDelete(largeProject.getId(), LargeProject.class.getSimpleName(), LargeProject.class, DEFAULT_PU, null, null, mediaType);
        RestUtils.restDelete(smallProject.getId(), SmallProject.class.getSimpleName(), SmallProject.class, DEFAULT_PU, null, null, mediaType);

        //delete employee
        RestUtils.restDelete(employee.getId(), Employee.class.getSimpleName(), Employee.class, DEFAULT_PU, null, null, mediaType);
    }

    /**
     * Update employee with manager.
     *
     * @param mediaType the media type
     * @throws RestCallFailedException the rest call failed exception
     * @throws URISyntaxException the uRI syntax exception
     * @throws JAXBException the jAXB exception
     */
    private void updateEmployeeWithManager(MediaType mediaType) throws RestCallFailedException, URISyntaxException, JAXBException {
        // create an employee
        Employee employee = new Employee();
        employee.setId(90909);
        employee.setFirstName("Miles");
        employee.setLastName("Davis");
        employee.setGender(Gender.Male);

        employee = RestUtils.restCreate(context, employee, Employee.class.getSimpleName(), Employee.class, DEFAULT_PU, null, mediaType, true);
        assertNotNull("Employee create failed.", employee);

        // create a manager
        Employee manager = new Employee();
        manager.setId(1010);
        manager.setFirstName("Charlie");
        manager.setLastName("Parker");
        manager.setGender(Gender.Male);

        manager = RestUtils.restCreate(context, manager, Employee.class.getSimpleName(), Employee.class, DEFAULT_PU, null, mediaType, true);
        assertNotNull("Employee manager create failed.", manager);

        // update employee with manager
        RestUtils.restUpdateBidirectionalRelationship(context, String.valueOf(employee.getId()), Employee.class.getSimpleName(), "manager", manager, DEFAULT_PU, mediaType, "managedEmployees", true);

        // read manager and verify that the relationship is set correctly 
        manager = RestUtils.restRead(context, manager.getId(), Employee.class.getSimpleName(), Employee.class, DEFAULT_PU, null, mediaType);
        assertNotNull("Manager read failed.", manager);
        assertNotNull("Manager's managed employee list is null", manager.getManagedEmployees());
        assertTrue("Manager's managed employee list is empty", manager.getManagedEmployees().size() > 0);

        employee = RestUtils.restRead(context, employee.getId(), Employee.class.getSimpleName(), Employee.class, DEFAULT_PU, null, mediaType);
        assertNotNull("Manager read failed.", employee);

        for (Employee emp : manager.getManagedEmployees()) {
            assertNotNull("Managed employee's first name is null", emp.getFirstName());
            assertNotNull("Managed employee's last name is null", emp.getLastName());
        }

        // delete employee
        RestUtils.restDelete(employee.getId(), Employee.class.getSimpleName(), Employee.class, DEFAULT_PU, null, null, mediaType);

        // delete manager
        RestUtils.restDelete(manager.getId(), Employee.class.getSimpleName(), Employee.class, DEFAULT_PU, null, null, mediaType);
    }
    
    /**
     * Creates the employee with phone numbers.
     *
     * @param mediaType the media type
     * @throws RestCallFailedException the rest call failed exception
     * @throws URISyntaxException the uRI syntax exception
     * @throws JAXBException the jAXB exception
     */
    private void createEmployeeWithPhoneNumbers(MediaType mediaType) throws RestCallFailedException, URISyntaxException, JAXBException {
        // create an employee
        Employee employee = new Employee();
        employee.setId(90909);
        employee.setFirstName("Miles");
        employee.setLastName("Davis");
        EmploymentPeriod employmentPeriod = new EmploymentPeriod();
        Calendar now = GregorianCalendar.getInstance();
        employmentPeriod.setStartDate(now);
        employee.setPeriod(employmentPeriod);

        employee = RestUtils.restCreate(context, employee, Employee.class.getSimpleName(), Employee.class, DEFAULT_PU, null, mediaType, true);
        assertNotNull("Employee create failed.", employee);

        // create a cell phone number for this employee
        PhoneNumber cell = new PhoneNumber();
        cell.setId(employee.getId());
        cell.setNumber("123-123 1234");
        cell.setType("cell");
        cell.setEmployee(employee);
        cell = RestUtils.restCreate(context, cell, PhoneNumber.class.getSimpleName(), PhoneNumber.class, DEFAULT_PU, null, mediaType, true);
        assertNotNull("Phone number create failed.", cell);
        assertTrue("123-123 1234".equals(cell.getNumber()));

        // update employee with cell number
        String result = RestUtils.restUpdateBidirectionalRelationship(context, String.valueOf(employee.getId()), Employee.class.getSimpleName(), "phoneNumbers", cell, DEFAULT_PU, mediaType,
                "employee", true);
        assertNotNull(result);

        // make sure that response from restUpdateBidirectionalRelationship contains newly added cell number in employee object
        String cellLinkHref = RestUtils.getServerURI() + DEFAULT_PU + "/entity/PhoneNumber/90909+cell";
        assertTrue(result.contains(cellLinkHref));

        // create a work phone number
        PhoneNumber workPhone = new PhoneNumber();
        workPhone.setId(employee.getId());
        workPhone.setNumber("987-654 1234");
        workPhone.setType("work");
        workPhone.setEmployee(employee);
        workPhone = RestUtils.restCreate(context, workPhone, PhoneNumber.class.getSimpleName(), PhoneNumber.class, DEFAULT_PU, null, mediaType, true);
        assertNotNull("Phone number create failed.", workPhone);
        assertTrue("987-654 1234".equals(workPhone.getNumber()));

        // update employee with work phone number
        result = RestUtils.restUpdateBidirectionalRelationship(context, String.valueOf(employee.getId()), Employee.class.getSimpleName(), "phoneNumbers", workPhone, DEFAULT_PU, mediaType,
                "employee", true);
        assertNotNull(result);
        // make sure that response from restUpdateBidirectionalRelationship contains work phone number AND cell number in employee object
        String workPhoneLinkHref = RestUtils.getServerURI() + DEFAULT_PU + "/entity/PhoneNumber/90909+work";
        assertTrue(result.contains(workPhoneLinkHref));
        assertTrue(result.contains(cellLinkHref));

        // read employee with phone numbers
        String employeeWithPhoneNumbers = RestUtils.restRead(context, new Integer(90909), Employee.class.getSimpleName(), DEFAULT_PU, null, mediaType);
        assertNotNull("Employee read failed.", employeeWithPhoneNumbers);
        // make sure employee has 2 phone numbers 
        assertTrue(employeeWithPhoneNumbers.contains(workPhoneLinkHref));
        assertTrue(employeeWithPhoneNumbers.contains(cellLinkHref));

        // delete employee (cascade deletes phone numbers)
        RestUtils.restDelete(new Integer(90909), Employee.class.getSimpleName(), Employee.class, DEFAULT_PU, null, null, mediaType);
    }

    /**
     * Update employee with employment period.
     *
     * @param mediaType the media type
     * @throws RestCallFailedException the rest call failed exception
     * @throws URISyntaxException the uRI syntax exception
     */
    private void updateEmployeeWithEmploymentPeriod(MediaType mediaType) throws RestCallFailedException, URISyntaxException {
        Employee employee = new Employee();
        employee.setId(10234);
        employee.setFirstName("John");
        employee.setLastName("Travolta");
        EmploymentPeriod employmentPeriod = new EmploymentPeriod();
        Calendar now = GregorianCalendar.getInstance();
        employmentPeriod.setStartDate(now);
        employee.setPeriod(employmentPeriod);

        employee = RestUtils.restCreate(context, employee, Employee.class.getSimpleName(), Employee.class, DEFAULT_PU, null, mediaType, true);
        assertNotNull("Employee create failed.", employee);

        Calendar threeYearsLater = GregorianCalendar.getInstance();
        long end = (now.getTimeInMillis() + THREE_YEARS);
        threeYearsLater.setTimeInMillis(end);

        employmentPeriod.setEndDate(threeYearsLater);
        employee.setPeriod(employmentPeriod);

        employee = RestUtils.restUpdate(context, employee, Employee.class.getSimpleName(), Employee.class, DEFAULT_PU, null, mediaType, true);

        assertNotNull("Employee update failed.", employee);
        assertNotNull("Employee's employment period update failed", employee.getPeriod());
        assertNotNull("Employee's employment period end date is null", employee.getPeriod().getEndDate());
        assertTrue("Incorrect end date for employee", employee.getPeriod().getEndDate().getTimeInMillis() == threeYearsLater.getTimeInMillis());

        RestUtils.restDelete(new Integer(10234), Employee.class.getSimpleName(), Employee.class, DEFAULT_PU, null, null, mediaType);
    }

    /**
     * Read employee.
     *
     * @param mediaType the media type
     * @throws RestCallFailedException the rest call failed exception
     * @throws URISyntaxException the uRI syntax exception
     */
    private void readEmployee(MediaType mediaType) throws RestCallFailedException, URISyntaxException {
        Employee employee = new Employee();
        employee.setId(18234);
        employee.setFirstName("Pat");
        employee.setLastName("Metheny");
        EmploymentPeriod employmentPeriod = new EmploymentPeriod();
        employmentPeriod.setStartDate(GregorianCalendar.getInstance());
        employee.setPeriod(employmentPeriod);

        Employee employeeCreated = RestUtils.restCreate(context, employee, Employee.class.getSimpleName(), Employee.class, DEFAULT_PU, null, mediaType, true);
        Employee employeeRead = RestUtils.restRead(context, new Integer(18234), Employee.class.getSimpleName(), Employee.class, DEFAULT_PU, null, mediaType);
        assertNotNull("Employee create failed.", employeeCreated);
        assertNotNull("Employee read failed.", employeeRead);
        assertTrue("Employee created and employee read is different", employeeCreated.getLastName().equals(employeeRead.getLastName()));
        RestUtils.restDelete(new Integer(18234), Employee.class.getSimpleName(), Employee.class, DEFAULT_PU, null, null, mediaType);
        Employee emp = DBUtils.dbRead(new Integer(10234), Employee.class, context.getEmf().createEntityManager());
        assertNull("Employee could not be deleted", emp);
    }

    private void getEmployeeAddressNamedQueryWithBinaryData(MediaType mediaType) throws IOException, RestCallFailedException, URISyntaxException {
        EmployeeAddress address = new EmployeeAddress("Newyork City", "USA", "NY", "10005", "Wall Street");
        address.setAreaPicture(RestUtils.convertImageToByteArray("manhattan.png"));

        address = RestUtils.restUpdate(context, address, EmployeeAddress.class.getSimpleName(), EmployeeAddress.class, DEFAULT_PU, null, mediaType, true);
        assertNotNull("EmployeeAddress create failed.", address);
        assertNotNull("EmployeeAddress area picture is null", address.getAreaPicture());
        assertTrue("Newyork City".equals(address.getCity()));
        assertTrue("USA".equals(address.getCountry()));
        assertTrue("NY".equals(address.getProvince()));
        assertTrue("10005".equals(address.getPostalCode()));
        assertTrue("Wall Street".equals(address.getStreet()));

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("id", address.getId());

        String expected = null;
        if (mediaType == MediaType.APPLICATION_XML_TYPE) {
            expected = "<List><item><areaPicture>89504E470D0A1A0A0000000D49484452000000960000009608030000000BDF81D000000300504C5445CFDEE2EFEFEFE6E6E6F7F7F7FFFFFFDEDEDEF7FDFF00C4F163DBF608C6F153D7F6D6F6FDEBFAFD99E7F9F0FCFE30CFF4B6EEFB21CCF310C8F258D8F68BE4F9CFF4FCDFF8FD7DE1F8E6F9FEBCEFFB3BD2F4C3F1FC84E3F84AD5F53AD1F46BDDF717C9F295E6F919CAF272DEF7A8EBFAA6EAFAACECFA29CDF345D0F143D4F586E1F688DAEE6ED9F1E9F4F6A3DDEAD5DDDFA2E0EFD7F1F7CFE6EA0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000003A4D766F00000A084944415478DAE55CDB72DC36124537E05896E424BEC8962B4925D9AD4DEDD3FEFF67EC5BDED74E7993527CD9B5654983466F0304392087177088E1A86AC7374933260F4F779FBE002418751F5FA8FEFF60B1E3FB08CB6904E27B070BE48F41473579749F7C4B9B489933D9468503462227D74C00E15BAB8FCE964B2943E0DAAE4786D581A0EF89CBF7E1D07C6C58FD71E703731A9A59CD860987129908C78AC4F113133F70C730228F9B571B3E0A2C65F7F4BEC3C202FD77FDD7713E0D0C51AA0F84EB5C3DFF7CE5F04BBFCE62C5152A8675D97A4C576797784523C1C066D8010F15880F94BAD1833E4DA212167CA65CD7882F7FF7A458EE4F38080247DEA341F53A9011FD612FBF0CDA886D90D5610D390C5B17574A3DFBB722335C5B546EEF59EBD3DD83C042DC28B521D6C31F50105C0B7C65BD96117FB81685B81D3B3685F4132B325E87ADF377DE4E44389A2F9D10A645B798711DB6EEBCBFDF8E6A8F1064A0FA57AF1589AFE4CFAD9A6A731CC44A91578225D1FFF22A47A881068B583C80666DD4C6D2E881EB776120291E80AD679FD5D9ED4411181902BB5EAA7EAFD4899BE87378A21B2A0FEB04FCD938BF855C05163E735E9660499D7F0097D79FBC8DEE264ECC13C88AB3F5689C0CAAD9E0558DF8E87A41CB7D305868265A1CEE68E93AB0BEBEEEAD093451858E72BAB1E2B04ECF07E25F8749A00EB53B9163695D59AF06EBD1DBC6713A641804499500723EF0153C00385A09D6F3FFC42F3EFFDC04A3B5D5D9D957C95220DBD8ED38E933DC3A139B0F8D4F6DD0F9A64B52B250435A7E49996CDB1108C0ABB0F5707B9AB717EAC92FFEAA597E868C5571A52326E629AD2FA9F2E9A48FEFE8C3DFFED002C70FB3424351FDDC77161A08E287B91F5B41542F13177EF149BD70AF31A447636AE726E79082B6196408C2C5FD725F92ACA4E47AAB2E7E3FE1FA0CDAB838C4D102D326F99A078A8972B0427F1F5F1FD55F3EE0C3EBC088AB144BA94AA9F4B62F1413A3EE0FBA72B0126348717AE5CEDEF9830B4FE8ACB5D2B0C6C64B77F8ED9DCE159B9D9E27A32C9182F36F7E0BFD72DD5AB37C237E3ED802756AFA52917802C9555F5CF3DD7FAB09047A6D90FE59831FD4381C8045DAC1218C7879D3AE8999EBB988288131E2EA3CD012065D0171342C0F0BFFECCCDCDCB6C489EB7548235EE9BB6BE6E2B01EA7D5DFE9E6A4C54604686840D6757C23015E06D6451A4E68DFDD0DB4AC0349D0C49F1B5714D6C969EA59CFF0D6F5A991246D6DED6866D6F53A68115897290B2FBF48ABEC867A4343A319A26E3D4AC8D6933709ACEFF0F6B67FC2A789335412C3FA6C09582625EBDDDDDDD0CC544F773CFE681E5701239E7E4CBEF9FEC68475802557E98AA83C2555E64FD7FC59F1C2634A622800EB6C93B8F5A7EBE1B1777ED68702464C62EBFBF73765DCB500ACAD139FBE16C1A412475C0E0BB7527AEE7CCE655B00D8625C8983DFC4A4AC1C2E0CA2E56C9D5E345FBEF2C51370F6DE90E1A1DC7258275B67BAAD0B19B0B488AC02B0DCD6B7CEAAC249F29E46B784AC02B03E5CF4E8342BD8CFF1C93A5FD62F6F319215547DD7F8BFD5FB683DD515D762B64E7E4C6AD4B4A0813DDCAB99E12C86F5EA5FA9369B4463F7D81458AE3A4DDAC3EFFE6C08B26ADE7E2DD519E12C8695E46976EDC32E08C6C5B09E069EE0DBC75F7DFD9A931E8BF7F1AE866053C2883FBDB1EF157D4AE65BD2AE4AA383A48FC59678D1F99BC7FF6046D35ECAD7928590D687F530FCFDC7573F6E6E9F86D31BD889AB7D370F1799D83CBDFC550DCC62EA5295F202135C41973FFB75C0145CCDDB94659C67CFBD613D7D52F5141F5FA8E7DF8C0F3D2A9FCBB2272DF6AD709A27BF79A1FFE79859AC8824BB99BEBF7775FA25D4333F7C78F596AE06DD46AA54E7A7B8AC1920A736AC87820B5DFE21DCB01B3E4425FB1CB66A65356A0E0A45E2F8C9C200D2449B433EACC591387E5948359979CE05A574CBE5E61770D3566CB69A2CEFC772636C1335AC800DB2948FB26BABFC5454606023B19F83CACC91AEC5B0B4562E776B3EAE6844073AF373C18854AD63F7FF9FA6625BCC16E5D6EC90ACE56B7DB09C5837E65A1DE0B5749E988F0AEADA7ED0F5A5A1D67BA5EA70AF5A9A436C866FD61AE9D7817D8264D74F864BFA73332030B003C85FB0BF8874E58673AA02DDD4D0E1E31E92EBDB320213453319D216EB6DE5612D3EB515D5254AF69441A24F7EFB797B731D3B89885A14F61851722BFA8187DF282024697109D35AEBABAD923DF393B6086D386DD2DF76FE779BBF5D58CD75208AB511C3C6E86E71372F5ED0F31AFC2A54AAD57501B78089DBE28811A72D53933563524450A1F24BBF8C8DBD124B52FBBE0C9C8D6A77BC32D52FC8E9A1DAC06C21161C959AEBEDBE0D6CBB3CCE3DD736B226B5216E268B61EC9DC1A745E7977FE3FA1CC08E34EC2FA7D9F589AE2A2C096314C163E79C84631009EB8392FCDD0F429F83C9CE27C788D1B972340B6D5C180F6E2EFD0FFA88F3DEED34A1381BF8250B2E927CE6544E467B0351E51F1476EFDB6A13BFDFB1E1D9DCF40DCBCDFE30674C89206829806A26D00216037FA28B3DF7C3A29AEFF3F15A325CCCFBBCFF98F1A6D3E08DE765407BA120174F67332A0876D94EAFA70542FB9CE25BEB2A8B493E433431AB61542E368698A93D0ED8AFB0C1ACD968BCD1A2BE52082E061B0EFCA0645EB248E0ACE436015B628F4D88AFA9850AC1414CB6A9842D40A83F5C147E2D1509EE16CC7DB07261BAEA486E68CD29746936AC93402500DEB98DC35839128894016D9233A25A94133BFE8EFD3796136FA33C7C42842A8ABA50CD4652A3F8BD968F3545132F8CC456F438B4FD296A3BFF4E37F6B0F024D613C9107FF241DA9BC3BA6C6592D5CE173DC3055FF641B321A259CBF194A970A346F76E0DBFF56C584E394B293B76D3BD25BDD9B2B8D10D3E5D157C694DB35B057661719672E74C69E4CAB6F3BFE40BE7CB058E3C0A450F7A6BDC8E112B1B4E90B5EBE33D56B4A18946A82EA165F4785563B7A29BF9E9AEE74D487B6BAA3A270643D849512218E4F7C40648635EDC61CB8BDC44FBB7731796AF0BA03589A0AAC0F3E042CF14575D9AA5836957E9C002624894A47756EA46FABB5D4313D4B68AB6B39851A8756039EF8B63D38E9E3BD65A0140D141776E5EE68AA2CC877AB46171251EC3B87AEFA30B67ACF0C078F0DA07994B9F6D58CD850FF8FCAE05B38698AE728B6C505D5830118BFBACC341B69F0F550CBCDB75ED912FB73481A32858D208CE9A83B5D81A1F9C13CCD8D1932EB94C3EC263444E451626F6C5CC4145CD304530ED516AD66CCD5861C876A83D58EAB0358D6AD64E319F64F8C1820168608BA7A3C4CEF2585EBCB1CC4C5025D948CF3360899799182610CEA66A0D585CDDDDB1F60BA7DFE7233C960E47DB50EB3400A8E3C01AA48CB4B42CF668B006D4DD6276D3711058FDF5E571008DB51873EB90C3C0E21D4C78E487427A58DD6D1FEEF80FAAC4DD25C1A3845E77B082367DE8A0EB5F533B8211536E8C53F780AA60449D8ABABA272F4C968E49DF17547E71C59BCDAAFE87021D8F2D3F4BF40B4400F707D5411F2CBAE0F53F4891BE36E04AC8330000000049454E44AE426082</areaPicture></item></List>";
        } else {
            expected = "[{\"areaPicture\":\"89504E470D0A1A0A0000000D49484452000000960000009608030000000BDF81D000000300504C5445CFDEE2EFEFEFE6E6E6F7F7F7FFFFFFDEDEDEF7FDFF00C4F163DBF608C6F153D7F6D6F6FDEBFAFD99E7F9F0FCFE30CFF4B6EEFB21CCF310C8F258D8F68BE4F9CFF4FCDFF8FD7DE1F8E6F9FEBCEFFB3BD2F4C3F1FC84E3F84AD5F53AD1F46BDDF717C9F295E6F919CAF272DEF7A8EBFAA6EAFAACECFA29CDF345D0F143D4F586E1F688DAEE6ED9F1E9F4F6A3DDEAD5DDDFA2E0EFD7F1F7CFE6EA0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000003A4D766F00000A084944415478DAE55CDB72DC36124537E05896E424BEC8962B4925D9AD4DEDD3FEFF67EC5BDED74E7993527CD9B5654983466F0304392087177088E1A86AC7374933260F4F779FBE002418751F5FA8FEFF60B1E3FB08CB6904E27B070BE48F41473579749F7C4B9B489933D9468503462227D74C00E15BAB8FCE964B2943E0DAAE4786D581A0EF89CBF7E1D07C6C58FD71E703731A9A59CD860987129908C78AC4F113133F70C730228F9B571B3E0A2C65F7F4BEC3C202FD77FDD7713E0D0C51AA0F84EB5C3DFF7CE5F04BBFCE62C5152A8675D97A4C576797784523C1C066D8010F15880F94BAD1833E4DA212167CA65CD7882F7FF7A458EE4F38080247DEA341F53A9011FD612FBF0CDA886D90D5610D390C5B17574A3DFBB722335C5B546EEF59EBD3DD83C042DC28B521D6C31F50105C0B7C65BD96117FB81685B81D3B3685F4132B325E87ADF377DE4E44389A2F9D10A645B798711DB6EEBCBFDF8E6A8F1064A0FA57AF1589AFE4CFAD9A6A731CC44A91578225D1FFF22A47A881068B583C80666DD4C6D2E881EB776120291E80AD679FD5D9ED4411181902BB5EAA7EAFD4899BE87378A21B2A0FEB04FCD938BF855C05163E735E9660499D7F0097D79FBC8DEE264ECC13C88AB3F5689C0CAAD9E0558DF8E87A41CB7D305868265A1CEE68E93AB0BEBEEEAD093451858E72BAB1E2B04ECF07E25F8749A00EB53B9163695D59AF06EBD1DBC6713A641804499500723EF0153C00385A09D6F3FFC42F3EFFDC04A3B5D5D9D957C95220DBD8ED38E933DC3A139B0F8D4F6DD0F9A64B52B250435A7E49996CDB1108C0ABB0F5707B9AB717EAC92FFEAA597E868C5571A52326E629AD2FA9F2E9A48FEFE8C3DFFED002C70FB3424351FDDC77161A08E287B91F5B41542F13177EF149BD70AF31A447636AE726E79082B6196408C2C5FD725F92ACA4E47AAB2E7E3FE1FA0CDAB838C4D102D326F99A078A8972B0427F1F5F1FD55F3EE0C3EBC088AB144BA94AA9F4B62F1413A3EE0FBA72B0126348717AE5CEDEF9830B4FE8ACB5D2B0C6C64B77F8ED9DCE159B9D9E27A32C9182F36F7E0BFD72DD5AB37C237E3ED802756AFA52917802C9555F5CF3DD7FAB09047A6D90FE59831FD4381C8045DAC1218C7879D3AE8999EBB988288131E2EA3CD012065D0171342C0F0BFFECCCDCDCB6C489EB7548235EE9BB6BE6E2B01EA7D5DFE9E6A4C54604686840D6757C23015E06D6451A4E68DFDD0DB4AC0349D0C49F1B5714D6C969EA59CFF0D6F5A991246D6DED6866D6F53A68115897290B2FBF48ABEC867A4343A319A26E3D4AC8D6933709ACEFF0F6B67FC2A789335412C3FA6C09582625EBDDDDDDD0CC544F773CFE681E5701239E7E4CBEF9FEC68475802557E98AA83C2555E64FD7FC59F1C2634A622800EB6C93B8F5A7EBE1B1777ED68702464C62EBFBF73765DCB500ACAD139FBE16C1A412475C0E0BB7527AEE7CCE655B00D8625C8983DFC4A4AC1C2E0CA2E56C9D5E345FBEF2C51370F6DE90E1A1DC7258275B67BAAD0B19B0B488AC02B0DCD6B7CEAAC249F29E46B784AC02B03E5CF4E8342BD8CFF1C93A5FD62F6F319215547DD7F8BFD5FB683DD515D762B64E7E4C6AD4B4A0813DDCAB99E12C86F5EA5FA9369B4463F7D81458AE3A4DDAC3EFFE6C08B26ADE7E2DD519E12C8695E46976EDC32E08C6C5B09E069EE0DBC75F7DFD9A931E8BF7F1AE866053C2883FBDB1EF157D4AE65BD2AE4AA383A48FC59678D1F99BC7FF6046D35ECAD7928590D687F530FCFDC7573F6E6E9F86D31BD889AB7D370F1799D83CBDFC550DCC62EA5295F202135C41973FFB75C0145CCDDB94659C67CFBD613D7D52F5141F5FA8E7DF8C0F3D2A9FCBB2272DF6AD709A27BF79A1FFE79859AC8824BB99BEBF7775FA25D4333F7C78F596AE06DD46AA54E7A7B8AC1920A736AC87820B5DFE21DCB01B3E4425FB1CB66A65356A0E0A45E2F8C9C200D2449B433EACC591387E5948359979CE05A574CBE5E61770D3566CB69A2CEFC772636C1335AC800DB2948FB26BABFC5454606023B19F83CACC91AEC5B0B4562E776B3EAE6844073AF373C18854AD63F7FF9FA6625BCC16E5D6EC90ACE56B7DB09C5837E65A1DE0B5749E988F0AEADA7ED0F5A5A1D67BA5EA70AF5A9A436C866FD61AE9D7817D8264D74F864BFA73332030B003C85FB0BF8874E58673AA02DDD4D0E1E31E92EBDB320213453319D216EB6DE5612D3EB515D5254AF69441A24F7EFB797B731D3B89885A14F61851722BFA8187DF282024697109D35AEBABAD923DF393B6086D386DD2DF76FE779BBF5D58CD75208AB511C3C6E86E71372F5ED0F31AFC2A54AAD57501B78089DBE28811A72D53933563524450A1F24BBF8C8DBD124B52FBBE0C9C8D6A77BC32D52FC8E9A1DAC06C21161C959AEBEDBE0D6CBB3CCE3DD736B226B5216E268B61EC9DC1A745E7977FE3FA1CC08E34EC2FA7D9F589AE2A2C096314C163E79C84631009EB8392FCDD0F429F83C9CE27C788D1B972340B6D5C180F6E2EFD0FFA88F3DEED34A1381BF8250B2E927CE6544E467B0351E51F1476EFDB6A13BFDFB1E1D9DCF40DCBCDFE30674C89206829806A26D00216037FA28B3DF7C3A29AEFF3F15A325CCCFBBCFF98F1A6D3E08DE765407BA120174F67332A0876D94EAFA70542FB9CE25BEB2A8B493E433431AB61542E368698A93D0ED8AFB0C1ACD968BCD1A2BE52082E061B0EFCA0645EB248E0ACE436015B628F4D88AFA9850AC1414CB6A9842D40A83F5C147E2D1509EE16CC7DB07261BAEA486E68CD29746936AC93402500DEB98DC35839128894016D9233A25A94133BFE8EFD3796136FA33C7C42842A8ABA50CD4652A3F8BD968F3545132F8CC456F438B4FD296A3BFF4E37F6B0F024D613C9107FF241DA9BC3BA6C6592D5CE173DC3055FF641B321A259CBF194A970A346F76E0DBFF56C584E394B293B76D3BD25BDD9B2B8D10D3E5D157C694DB35B057661719672E74C69E4CAB6F3BFE40BE7CB058E3C0A450F7A6BDC8E112B1B4E90B5EBE33D56B4A18946A82EA165F4785563B7A29BF9E9AEE74D487B6BAA3A270643D849512218E4F7C40648635EDC61CB8BDC44FBB7731796AF0BA03589A0AAC0F3E042CF14575D9AA5836957E9C002624894A47756EA46FABB5D4313D4B68AB6B39851A8756039EF8B63D38E9E3BD65A0140D141776E5EE68AA2CC877AB46171251EC3B87AEFA30B67ACF0C078F0DA07994B9F6D58CD850FF8FCAE05B38698AE728B6C505D5830118BFBACC341B69F0F550CBCDB75ED912FB73481A32858D208CE9A83B5D81A1F9C13CCD8D1932EB94C3EC263444E451626F6C5CC4145CD304530ED516AD66CCD5861C876A83D58EAB0358D6AD64E319F64F8C1820168608BA7A3C4CEF2585EBCB1CC4C5025D948CF3360899799182610CEA66A0D585CDDDDB1F60BA7DFE7233C960E47DB50EB3400A8E3C01AA48CB4B42CF668B006D4DD6276D3711058FDF5E571008DB51873EB90C3C0E21D4C78E487427A58DD6D1FEEF80FAAC4DD25C1A3845E77B082367DE8A0EB5F533B8211536E8C53F780AA60449D8ABABA272F4C968E49DF17547E71C59BCDAAFE87021D8F2D3F4BF40B4400F707D5411F2CBAE0F53F4891BE36E04AC8330000000049454E44AE426082\"}]";
        }
        // query
        String result = RestUtils.restNamedQuery("EmployeeAddress.getPicture", DEFAULT_PU, parameters, null, mediaType);
        assertNotNull(result);
        assertTrue(result.contains(expected));

        // delete employee address
        RestUtils.restDelete(address.getId(), EmployeeAddress.class.getSimpleName(), EmployeeAddress.class, DEFAULT_PU, null, null, mediaType);
    }

}