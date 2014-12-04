/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
package org.generationcp.middleware.manager;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.generationcp.middleware.DataManagerIntegrationTest;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.UserDataManager;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.utils.test.Debug;
import org.junit.BeforeClass;
import org.junit.Test;

public class UserDataManagerImplTest extends DataManagerIntegrationTest {

    private static UserDataManager manager;

    @BeforeClass
    public static void setUp() throws Exception {
        manager = managerFactory.getUserDataManager();
    }

    @Test
    public void testGetAllUsers() throws Exception {
        List<User> users = manager.getAllUsers();
        assertNotNull(users);
        Debug.println(INDENT, "testGetAllUsers(): " + users.size());
        Debug.printObjects(INDENT, users);
    }

    @Test
    public void testCountAllUsers() throws Exception {
        long count = manager.countAllUsers();
        Debug.println(INDENT, "testCountAllUsers(): " + count);
    }

    @Test
    public void testAddUser() throws MiddlewareQueryException {
        User user = new User();
        user.setUserid(-1);
        user.setInstalid(-1);
        user.setStatus(-1);
        user.setAccess(-1);
        user.setUserid(-1);
        user.setType(-1);
        user.setName("user_test");
        user.setPassword("user_password");
        user.setPersonid(-1);
        user.setAdate(20120101);
        user.setCdate(20120101);

        manager.addUser(user);

        user = manager.getUserById(user.getUserid());
        assertNotNull(user);
        Debug.println(INDENT, "testAddUser() ADDED: " + user);

        // cleanup
        manager.deleteUser(user);
    }

    @Test
    public void testGetAllPersons() throws Exception{
        List<Person> persons = manager.getAllPersons();
        assertNotNull(persons);
        Debug.printObjects(INDENT, persons);
    }
    
    @Test
    public void testGetAllPersonsOrderedByLocalCentral() throws Exception{
        List<Person> persons = manager.getAllPersonsOrderedByLocalCentral();
        assertNotNull(persons);
        for (Person person : persons){
            Debug.println(person.getDisplayName());
        }
    }    

    @Test
    public void testCountAllPersons() throws Exception{
        long count = manager.countAllPersons();
        Debug.println(INDENT, "testCountAllPersons(): " + count);
    }

    @Test
    public void testAddPerson() throws MiddlewareQueryException {
        Person person = new Person();
        person.setId(-1);
        person.setInstituteId(1);
        person.setFirstName("Lich");
        person.setMiddleName("Frozen");
        person.setLastName("King");
        person.setPositionName("King of Icewind Dale");
        person.setTitle("His Highness");
        person.setExtension("1");
        person.setFax("2");
        person.setEmail("lichking@blizzard.com");
        person.setNotes("notes");
        person.setContact("3");
        person.setLanguage(-1);
        person.setPhone("4");

        // add the person
        manager.addPerson(person);

        person = manager.getPersonById(person.getId());
        assertNotNull(person);
        Debug.println(INDENT, "testAddPerson() ADDED: " + person);

        // delete the person
        manager.deletePerson(person);
    }

    @Test
    public void testIsPersonExists() throws MiddlewareQueryException {
        String firstName = "PATERNO";
        String lastName = "BORLAGDAN";
        Debug.println(INDENT, "testIsPersonExists(firstName=" + firstName + ", lastName=" + lastName + "): "
                + manager.isPersonExists(firstName, lastName));

        firstName = "PATTY";
        lastName = "Borly".toUpperCase();
        Debug.println(INDENT, "testIsPersonExists(firstName=" + firstName + ", lastName=" + lastName + "): "
                + manager.isPersonExists(firstName, lastName));
    }

    @Test
    public void testIsUsernameExists() throws MiddlewareQueryException {
    	Debug.println(INDENT, "testIsUsernameExists() ");
        String userName = "GMCLAREN";
        Debug.println(INDENT, "Existing Username (" + userName + "): " + manager.isUsernameExists(userName));
        String userName2 = "CLAREN";
        Debug.println(INDENT, "Non-existing Username (" + userName2 + "): " + manager.isUsernameExists(userName2));
    }

    @Test
	public void testGetPersonById() throws Exception {

    	//central database
		int id = 1;
		Person personid = manager.getPersonById(id);
		assertNotNull(personid);
		Debug.println(INDENT, "Central database: " + personid);

		//local database
        Person person = new Person();
        person.setId(-1);
        person.setInstituteId(1);
        person.setFirstName("Lich");
        person.setMiddleName("Frozen");
        person.setLastName("King");
        person.setPositionName("King of Icewind Dale");
        person.setTitle("His Highness");
        person.setExtension("1");
        person.setFax("2");
        person.setEmail("lichking@blizzard.com");
        person.setNotes("notes");
        person.setContact("3");
        person.setLanguage(-1);
        person.setPhone("4");

        manager.addPerson(person);

		Person personid2 = manager.getPersonById(person.getId());
		assertNotNull(personid2);
		Debug.println(INDENT, "Local Database: " + personid2);

        manager.deletePerson(person);
	}

    @Test
	public void testGetUserById() throws Exception {
    	//central database
		int id = 1;
		User userid = manager.getUserById(id);
		assertNotNull(userid);
		Debug.println(INDENT, "Central Database: " + userid);

    	//local database
        User user = new User();
        user.setUserid(-1);
        user.setInstalid(-1);
        user.setStatus(-1);
        user.setAccess(-1);
        user.setUserid(-1);
        user.setType(-1);
        user.setName("user_test");
        user.setPassword("user_password");
        user.setPersonid(-1);
        user.setAdate(20120101);
        user.setCdate(20120101);

        manager.addUser(user);

        User userid2 = manager.getUserById(user.getUserid());
        assertNotNull(userid2);
        Debug.println(INDENT, "Local Database: " + userid2);

        // cleanup
        manager.deleteUser(user);
	}

    @Test
	public void testGetUserByUserName() throws Exception {
        User user = new User();
        user.setUserid(-1);
        user.setInstalid(-1);
        user.setStatus(-1);
        user.setAccess(-1);
        user.setUserid(-1);
        user.setType(-1);
        user.setName("user_test");
        user.setPassword("user_password");
        user.setPersonid(-1);
        user.setAdate(20120101);
        user.setCdate(20120101);

        manager.addUser(user);

		String name = "user_test";
		User userName = manager.getUserByUserName(name);
		assertNotNull(userName);
		Debug.println(INDENT, "testGetUserByUserName: " + userName);

	    manager.deleteUser(user);

	}

    @Test
    public void testIsValidUserLogin() throws MiddlewareQueryException {
    	String validuser = "GMCLAREN"; //enter valid username
    	String validpass = "IR123"; // enter valid password
        Debug.println(INDENT, "testIsValidUserLogin (using valid username & password): " 
                            + manager.isValidUserLogin(validuser, validpass));
        String invaliduser = "username"; //enter invalid username
    	String invalidpass = "password"; // enter invalid password
        Debug.println(INDENT, "testIsValidUserLogin (using invalid username & password): " 
                            + manager.isValidUserLogin(invaliduser, invalidpass));
    }

}
