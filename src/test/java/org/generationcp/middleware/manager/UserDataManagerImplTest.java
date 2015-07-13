/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.manager;

import java.util.List;

import org.generationcp.middleware.DataManagerIntegrationTest;
import org.generationcp.middleware.MiddlewareIntegrationTest;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.UserDataManager;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.utils.test.Debug;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class UserDataManagerImplTest extends DataManagerIntegrationTest {

	private static UserDataManager manager;

	@BeforeClass
	public static void setUp() throws Exception {
		UserDataManagerImplTest.manager = DataManagerIntegrationTest.managerFactory.getUserDataManager();
	}

	@Test
	public void testGetAllUsers() throws Exception {
		List<User> users = UserDataManagerImplTest.manager.getAllUsers();
		Assert.assertNotNull(users);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetAllUsers(): " + users.size());
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, users);
	}

	@Test
	public void testCountAllUsers() throws Exception {
		long count = UserDataManagerImplTest.manager.countAllUsers();
		Debug.println(MiddlewareIntegrationTest.INDENT, "testCountAllUsers(): " + count);
	}

	@Test
	public void testAddUser() throws MiddlewareQueryException {
		User user = new User();
		user.setInstalid(-1);
		user.setStatus(-1);
		user.setAccess(-1);
		user.setType(-1);
		user.setName("user_test");
		user.setPassword("user_password");
		user.setPersonid(-1);
		user.setAdate(20120101);
		user.setCdate(20120101);

		UserDataManagerImplTest.manager.addUser(user);

		user = UserDataManagerImplTest.manager.getUserById(user.getUserid());
		Assert.assertNotNull(user);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testAddUser() ADDED: " + user);

		// cleanup
		UserDataManagerImplTest.manager.deleteUser(user);
	}

	@Test
	public void testGetAllPersons() throws Exception {
		List<Person> persons = UserDataManagerImplTest.manager.getAllPersons();
		Assert.assertNotNull(persons);
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, persons);
	}

	@Test
	public void testGetAllPersonsOrderedByLocalCentral() throws Exception {
		List<Person> persons = UserDataManagerImplTest.manager.getAllPersonsOrderedByLocalCentral();
		Assert.assertNotNull(persons);
		for (Person person : persons) {
			Debug.println(person.getDisplayName());
		}
	}

	@Test
	public void testCountAllPersons() throws Exception {
		long count = UserDataManagerImplTest.manager.countAllPersons();
		Debug.println(MiddlewareIntegrationTest.INDENT, "testCountAllPersons(): " + count);
	}

	@Test
	public void testAddPerson() throws MiddlewareQueryException {
		Person person = new Person();
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
		UserDataManagerImplTest.manager.addPerson(person);

		person = UserDataManagerImplTest.manager.getPersonById(person.getId());
		Assert.assertNotNull(person);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testAddPerson() ADDED: " + person);

		// delete the person
		UserDataManagerImplTest.manager.deletePerson(person);
	}

	@Test
	public void testIsPersonExists() throws MiddlewareQueryException {
		String firstName = "PATERNO";
		String lastName = "BORLAGDAN";
		Debug.println(MiddlewareIntegrationTest.INDENT, "testIsPersonExists(firstName=" + firstName + ", lastName=" + lastName + "): "
				+ UserDataManagerImplTest.manager.isPersonExists(firstName, lastName));

		firstName = "PATTY";
		lastName = "Borly".toUpperCase();
		Debug.println(MiddlewareIntegrationTest.INDENT, "testIsPersonExists(firstName=" + firstName + ", lastName=" + lastName + "): "
				+ UserDataManagerImplTest.manager.isPersonExists(firstName, lastName));
	}

	@Test
	public void testIsUsernameExists() throws MiddlewareQueryException {
		Debug.println(MiddlewareIntegrationTest.INDENT, "testIsUsernameExists() ");
		String userName = "GMCLAREN";
		Debug.println(MiddlewareIntegrationTest.INDENT,
				"Existing Username (" + userName + "): " + UserDataManagerImplTest.manager.isUsernameExists(userName));
		String userName2 = "CLAREN";
		Debug.println(MiddlewareIntegrationTest.INDENT,
				"Non-existing Username (" + userName2 + "): " + UserDataManagerImplTest.manager.isUsernameExists(userName2));
	}

	@Test
	public void testGetPersonById() throws Exception {

		// local database
		Person person = new Person();
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

		UserDataManagerImplTest.manager.addPerson(person);

		Person personid2 = UserDataManagerImplTest.manager.getPersonById(person.getId());
		Assert.assertNotNull(personid2);
		Debug.println(MiddlewareIntegrationTest.INDENT, "Local Database: " + personid2);

		UserDataManagerImplTest.manager.deletePerson(person);
	}

	@Test
	public void testGetUserById() throws Exception {

		// local database
		User user = new User();
		user.setInstalid(-1);
		user.setStatus(-1);
		user.setAccess(-1);
		user.setType(-1);
		user.setName("user_test");
		user.setPassword("user_password");
		user.setPersonid(-1);
		user.setAdate(20120101);
		user.setCdate(20120101);

		UserDataManagerImplTest.manager.addUser(user);

		User userid2 = UserDataManagerImplTest.manager.getUserById(user.getUserid());
		Assert.assertNotNull(userid2);
		Debug.println(MiddlewareIntegrationTest.INDENT, "Local Database: " + userid2);

		// cleanup
		UserDataManagerImplTest.manager.deleteUser(user);
	}

	@Test
	public void testGetUserByUserName() throws Exception {
		User user = new User();
		user.setInstalid(-1);
		user.setStatus(-1);
		user.setAccess(-1);
		user.setType(-1);
		user.setName("user_test");
		user.setPassword("user_password");
		user.setPersonid(-1);
		user.setAdate(20120101);
		user.setCdate(20120101);

		UserDataManagerImplTest.manager.addUser(user);

		String name = "user_test";
		User userName = UserDataManagerImplTest.manager.getUserByUserName(name);
		Assert.assertNotNull(userName);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetUserByUserName: " + userName);

		UserDataManagerImplTest.manager.deleteUser(user);

	}

	@Test
	public void testIsValidUserLogin() throws MiddlewareQueryException {
		String validuser = "GMCLAREN"; // enter valid username
		String validpass = "IR123"; // enter valid password
		Debug.println(MiddlewareIntegrationTest.INDENT, "testIsValidUserLogin (using valid username & password): "
				+ UserDataManagerImplTest.manager.isValidUserLogin(validuser, validpass));
		String invaliduser = "username"; // enter invalid username
		String invalidpass = "password"; // enter invalid password
		Debug.println(MiddlewareIntegrationTest.INDENT, "testIsValidUserLogin (using invalid username & password): "
				+ UserDataManagerImplTest.manager.isValidUserLogin(invaliduser, invalidpass));
	}

}
