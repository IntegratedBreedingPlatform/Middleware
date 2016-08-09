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

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.data.initializer.PersonTestDataInitializer;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.UserDataManager;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.utils.test.Debug;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class UserDataManagerImplTest extends IntegrationTestBase {

	@Autowired
	private UserDataManager userDataManager;
	
	private static PersonTestDataInitializer personTDI;
	
	@BeforeClass
	public static void beforeClass() {
		personTDI = new PersonTestDataInitializer();
	}
	
	@Test
	public void testGetAllUsers() throws Exception {
		List<User> users = this.userDataManager.getAllUsers();
		Assert.assertNotNull(users);
		Debug.println(IntegrationTestBase.INDENT, "testGetAllUsers(): " + users.size());
		Debug.printObjects(IntegrationTestBase.INDENT, users);
	}

	@Test
	public void testCountAllUsers() throws Exception {
		long count = this.userDataManager.countAllUsers();
		Debug.println(IntegrationTestBase.INDENT, "testCountAllUsers(): " + count);
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
		user.setAssignDate(20120101);
		user.setCloseDate(20120101);

		this.userDataManager.addUser(user);

		user = this.userDataManager.getUserById(user.getUserid());
		Assert.assertNotNull(user);
		Debug.println(IntegrationTestBase.INDENT, "testAddUser() ADDED: " + user);

		// cleanup
		this.userDataManager.deleteUser(user);
	}

	@Test
	public void testGetAllPersons() throws Exception {
		List<Person> persons = this.userDataManager.getAllPersons();
		Assert.assertNotNull(persons);
		Debug.printObjects(IntegrationTestBase.INDENT, persons);
	}

	@Test
	public void testGetAllPersonsOrderedByLocalCentral() throws Exception {
		List<Person> persons = this.userDataManager.getAllPersonsOrderedByLocalCentral();
		Assert.assertNotNull(persons);
		for (Person person : persons) {
			Debug.println(person.getDisplayName());
		}
	}

	@Test
	public void testCountAllPersons() throws Exception {
		long count = this.userDataManager.countAllPersons();
		Debug.println(IntegrationTestBase.INDENT, "testCountAllPersons(): " + count);
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
		this.userDataManager.addPerson(person);

		person = this.userDataManager.getPersonById(person.getId());
		Assert.assertNotNull(person);
		Debug.println(IntegrationTestBase.INDENT, "testAddPerson() ADDED: " + person);

		// delete the person
		this.userDataManager.deletePerson(person);
	}

	@Test
	public void testIsPersonExists() throws MiddlewareQueryException {
		String firstName = "PATERNO";
		String lastName = "BORLAGDAN";
		Debug.println(IntegrationTestBase.INDENT, "testIsPersonExists(firstName=" + firstName + ", lastName=" + lastName + "): "
				+ this.userDataManager.isPersonExists(firstName, lastName));

		firstName = "PATTY";
		lastName = "Borly".toUpperCase();
		Debug.println(IntegrationTestBase.INDENT, "testIsPersonExists(firstName=" + firstName + ", lastName=" + lastName + "): "
				+ this.userDataManager.isPersonExists(firstName, lastName));
	}

	@Test
	public void testIsUsernameExists() throws MiddlewareQueryException {
		Debug.println(IntegrationTestBase.INDENT, "testIsUsernameExists() ");
		String userName = "GMCLAREN";
		Debug.println(IntegrationTestBase.INDENT,
				"Existing Username (" + userName + "): " + this.userDataManager.isUsernameExists(userName));
		String userName2 = "CLAREN";
		Debug.println(IntegrationTestBase.INDENT,
				"Non-existing Username (" + userName2 + "): " + this.userDataManager.isUsernameExists(userName2));
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

		this.userDataManager.addPerson(person);

		Person personid2 = this.userDataManager.getPersonById(person.getId());
		Assert.assertNotNull(personid2);
		Debug.println(IntegrationTestBase.INDENT, "Local Database: " + personid2);

		this.userDataManager.deletePerson(person);
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
		user.setAssignDate(20120101);
		user.setCloseDate(20120101);

		this.userDataManager.addUser(user);

		User userid2 = this.userDataManager.getUserById(user.getUserid());
		Assert.assertNotNull(userid2);
		Debug.println(IntegrationTestBase.INDENT, "Local Database: " + userid2);

		// cleanup
		this.userDataManager.deleteUser(user);
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
		user.setAssignDate(20120101);
		user.setCloseDate(20120101);

		this.userDataManager.addUser(user);

		String name = "user_test";
		User userName = this.userDataManager.getUserByUserName(name);
		Assert.assertNotNull(userName);
		Debug.println(IntegrationTestBase.INDENT, "testGetUserByUserName: " + userName);

		this.userDataManager.deleteUser(user);

	}
}
