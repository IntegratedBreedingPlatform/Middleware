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
import org.generationcp.middleware.data.initializer.UserTestDataInitializer;
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
	private static UserTestDataInitializer userTDI;
	@BeforeClass
	public static void beforeClass() {
		UserDataManagerImplTest.personTDI = new PersonTestDataInitializer();
		UserDataManagerImplTest.userTDI = new UserTestDataInitializer();
	}

	@Test
	public void testGetAllUsers() throws Exception {
		final List<User> users = this.userDataManager.getAllUsers();
		Assert.assertNotNull(users);
		Debug.println(IntegrationTestBase.INDENT, "testGetAllUsers(): " + users.size());
		Debug.printObjects(IntegrationTestBase.INDENT, users);
	}

	@Test
	public void testCountAllUsers() throws Exception {
		final long count = this.userDataManager.countAllUsers();
		Debug.println(IntegrationTestBase.INDENT, "testCountAllUsers(): " + count);
	}

	@Test
	public void testAddUser() throws MiddlewareQueryException {
		User user = UserDataManagerImplTest.userTDI.createUser();

		this.userDataManager.addUser(user);

		User resultUser = this.userDataManager.getUserById(user.getUserid());
		Assert.assertEquals("The user ids should be equal.", user.getUserid(), resultUser.getUserid());
		Assert.assertEquals("The usernames should be equal.", user.getName(), resultUser.getName());
		Assert.assertEquals("The Passwords should be equal.", user.getPassword(), resultUser.getPassword());
	}

	@Test
	public void testGetAllPersons() throws Exception {
		final List<Person> persons = this.userDataManager.getAllPersons();
		Assert.assertNotNull(persons);
		Debug.printObjects(IntegrationTestBase.INDENT, persons);
	}

	@Test
	public void testGetAllPersonsOrderedByLocalCentral() throws Exception {
		final List<Person> persons = this.userDataManager.getAllPersonsOrderedByLocalCentral();
		Assert.assertNotNull(persons);
		for (final Person person : persons) {
			Debug.println(person.getDisplayName());
		}
	}

	@Test
	public void testCountAllPersons() throws Exception {
		final long count = this.userDataManager.countAllPersons();
		Debug.println(IntegrationTestBase.INDENT, "testCountAllPersons(): " + count);
	}

	@Test
	public void testAddPerson() throws MiddlewareQueryException {
		Person person = UserDataManagerImplTest.personTDI.createPerson();
		// add the person
		this.userDataManager.addPerson(person);

		person = this.userDataManager.getPersonById(person.getId());
		final Person resultPerson = this.userDataManager.getPersonById(person.getId());
		Assert.assertEquals("The first names should be equal.", person.getFirstName(), resultPerson.getFirstName());
		Assert.assertEquals("The last names should be equal.", person.getLastName(), resultPerson.getLastName());
		Assert.assertEquals("The ids should be equal.", person.getId(), resultPerson.getId());
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
		final String userName = "GMCLAREN";
		Debug.println(IntegrationTestBase.INDENT,
				"Existing Username (" + userName + "): " + this.userDataManager.isUsernameExists(userName));
		final String userName2 = "CLAREN";
		Debug.println(IntegrationTestBase.INDENT,
				"Non-existing Username (" + userName2 + "): " + this.userDataManager.isUsernameExists(userName2));
	}

	@Test
	public void testGetPersonById() throws Exception {
		final Person person = UserDataManagerImplTest.personTDI.createPerson();
		this.userDataManager.addPerson(person);

		final Person resultPerson = this.userDataManager.getPersonById(person.getId());
		Assert.assertEquals("The first names should be equal.", person.getFirstName(), resultPerson.getFirstName());
		Assert.assertEquals("The last names should be equal.", person.getLastName(), resultPerson.getLastName());
		Assert.assertEquals("The ids should be equal.", person.getId(), resultPerson.getId());
	}

	@Test
	public void testGetUserById() throws Exception {
		User user = UserDataManagerImplTest.userTDI.createUser();

		this.userDataManager.addUser(user);

		User resultUser = this.userDataManager.getUserById(user.getUserid());
		Assert.assertEquals("The user ids should be equal.", user.getUserid(), resultUser.getUserid());
		Assert.assertEquals("The usernames should be equal.", user.getName(), resultUser.getName());
		Assert.assertEquals("The Passwords should be equal.", user.getPassword(), resultUser.getPassword());
	}

	@Test
	public void testGetUserByUserName() throws Exception {
		final User user = UserDataManagerImplTest.userTDI.createUser();
		user.setUserid(null);
		this.userDataManager.addUser(user);
		
		final User resultUser = this.userDataManager.getUserByUserName(user.getName());
		Assert.assertEquals("The user ids should be equal.", user.getUserid(), resultUser.getUserid());
		Assert.assertEquals("The usernames should be equal.", user.getName(), resultUser.getName());
		Assert.assertEquals("The Passwords should be equal.", user.getPassword(), resultUser.getPassword());
	}
	}
}
