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

	private static PersonTestDataInitializer personTestDataInitializer;
	private static UserTestDataInitializer userTestDataInitializer;

	@BeforeClass
	public static void beforeClass() {
		UserDataManagerImplTest.personTestDataInitializer = new PersonTestDataInitializer();
		UserDataManagerImplTest.userTestDataInitializer = new UserTestDataInitializer();
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
		final User user = UserDataManagerImplTest.userTestDataInitializer.createUser();

		this.userDataManager.addUser(user);

		final User resultUser = this.userDataManager.getUserById(user.getUserid());
		Assert.assertEquals("The user ids should be equal.", user.getUserid(), resultUser.getUserid());
		Assert.assertEquals("The usernames should be equal.", user.getName(), resultUser.getName());
		Assert.assertEquals("The Passwords should be equal.", user.getPassword(), resultUser.getPassword());
		Assert.assertEquals("The install ids should be equal.", user.getInstalid(), resultUser.getInstalid());
		Assert.assertEquals("The status values should be equal.", user.getStatus(), resultUser.getStatus());
		Assert.assertEquals("The access values should be equal.", user.getAccess(), resultUser.getAccess());
		Assert.assertEquals("The type values should be equal.", user.getType(), resultUser.getType());
		Assert.assertEquals("The assign dates should be equal.", user.getAssignDate(), resultUser.getAssignDate());
		Assert.assertEquals("The close dates should be equal.", user.getCloseDate(), resultUser.getCloseDate());
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
		Person person = UserDataManagerImplTest.personTestDataInitializer.createPerson();
		// add the person
		this.userDataManager.addPerson(person);

		person = this.userDataManager.getPersonById(person.getId());
		final Person resultPerson = this.userDataManager.getPersonById(person.getId());
		Assert.assertEquals("The first names should be equal.", person.getFirstName(), resultPerson.getFirstName());
		Assert.assertEquals("The middle names should be equal.", person.getMiddleName(), resultPerson.getMiddleName());
		Assert.assertEquals("The last names should be equal.", person.getLastName(), resultPerson.getLastName());
		Assert.assertEquals("The ids should be equal.", person.getId(), resultPerson.getId());
		Assert.assertEquals("The institute ids should be equal.", person.getInstituteId(),
				resultPerson.getInstituteId());
		Assert.assertEquals("The position names should be equal.", person.getPositionName(),
				resultPerson.getPositionName());
		Assert.assertEquals("The titles should be equal.", person.getTitle(), resultPerson.getTitle());
		Assert.assertEquals("The extension values should be equal.", person.getExtension(),
				resultPerson.getExtension());
		Assert.assertEquals("The fax values should be equal.", person.getFax(), resultPerson.getFax());
		Assert.assertEquals("The emails should be equal.", person.getEmail(), resultPerson.getEmail());
		Assert.assertEquals("The notes should be equal.", person.getNotes(), resultPerson.getNotes());
		Assert.assertEquals("The contact should be equal.", person.getContact(), resultPerson.getContact());
		Assert.assertEquals("The language values should be equal.", person.getLanguage(), resultPerson.getLanguage());
		Assert.assertEquals("The phone values should be equal.", person.getPhone(), resultPerson.getPhone());
	}

	@Test
	public void testIsPersonExists() throws MiddlewareQueryException {
		String firstName = "PATERNO";
		String lastName = "BORLAGDAN";
		Debug.println(IntegrationTestBase.INDENT, "testIsPersonExists(firstName=" + firstName + ", lastName=" + lastName
				+ "): " + this.userDataManager.isPersonExists(firstName, lastName));

		firstName = "PATTY";
		lastName = "Borly".toUpperCase();
		Debug.println(IntegrationTestBase.INDENT, "testIsPersonExists(firstName=" + firstName + ", lastName=" + lastName
				+ "): " + this.userDataManager.isPersonExists(firstName, lastName));
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
		final Person person = UserDataManagerImplTest.personTestDataInitializer.createPerson();
		this.userDataManager.addPerson(person);

		final Person resultPerson = this.userDataManager.getPersonById(person.getId());
		Assert.assertEquals("The first names should be equal.", person.getFirstName(), resultPerson.getFirstName());
		Assert.assertEquals("The middle names should be equal.", person.getMiddleName(), resultPerson.getMiddleName());
		Assert.assertEquals("The last names should be equal.", person.getLastName(), resultPerson.getLastName());
		Assert.assertEquals("The ids should be equal.", person.getId(), resultPerson.getId());
		Assert.assertEquals("The institute ids should be equal.", person.getInstituteId(),
				resultPerson.getInstituteId());
		Assert.assertEquals("The position names should be equal.", person.getPositionName(),
				resultPerson.getPositionName());
		Assert.assertEquals("The titles should be equal.", person.getTitle(), resultPerson.getTitle());
		Assert.assertEquals("The extension values should be equal.", person.getExtension(),
				resultPerson.getExtension());
		Assert.assertEquals("The fax values should be equal.", person.getFax(), resultPerson.getFax());
		Assert.assertEquals("The emails should be equal.", person.getEmail(), resultPerson.getEmail());
		Assert.assertEquals("The notes should be equal.", person.getNotes(), resultPerson.getNotes());
		Assert.assertEquals("The contact should be equal.", person.getContact(), resultPerson.getContact());
		Assert.assertEquals("The language values should be equal.", person.getLanguage(), resultPerson.getLanguage());
		Assert.assertEquals("The phone values should be equal.", person.getPhone(), resultPerson.getPhone());
	}

	@Test
	public void testGetUserById() throws Exception {
		final User user = UserDataManagerImplTest.userTestDataInitializer.createUser();

		this.userDataManager.addUser(user);

		final User resultUser = this.userDataManager.getUserById(user.getUserid());
		Assert.assertEquals("The user ids should be equal.", user.getUserid(), resultUser.getUserid());
		Assert.assertEquals("The usernames should be equal.", user.getName(), resultUser.getName());
		Assert.assertEquals("The Passwords should be equal.", user.getPassword(), resultUser.getPassword());
		Assert.assertEquals("The install ids should be equal.", user.getInstalid(), resultUser.getInstalid());
		Assert.assertEquals("The status values should be equal.", user.getStatus(), resultUser.getStatus());
		Assert.assertEquals("The access values should be equal.", user.getAccess(), resultUser.getAccess());
		Assert.assertEquals("The type values should be equal.", user.getType(), resultUser.getType());
		Assert.assertEquals("The assign dates should be equal.", user.getAssignDate(), resultUser.getAssignDate());
		Assert.assertEquals("The close dates should be equal.", user.getCloseDate(), resultUser.getCloseDate());
	}

	@Test
	public void testGetUserByUserName() throws Exception {
		final User user = UserDataManagerImplTest.userTestDataInitializer.createUser();
		user.setUserid(null);
		this.userDataManager.addUser(user);

		final User resultUser = this.userDataManager.getUserByUserName(user.getName());
		Assert.assertEquals("The user ids should be equal.", user.getUserid(), resultUser.getUserid());
		Assert.assertEquals("The usernames should be equal.", user.getName(), resultUser.getName());
		Assert.assertEquals("The Passwords should be equal.", user.getPassword(), resultUser.getPassword());
		Assert.assertEquals("The install ids should be equal.", user.getInstalid(), resultUser.getInstalid());
		Assert.assertEquals("The status values should be equal.", user.getStatus(), resultUser.getStatus());
		Assert.assertEquals("The access values should be equal.", user.getAccess(), resultUser.getAccess());
		Assert.assertEquals("The type values should be equal.", user.getType(), resultUser.getType());
		Assert.assertEquals("The assign dates should be equal.", user.getAssignDate(), resultUser.getAssignDate());
		Assert.assertEquals("The close dates should be equal.", user.getCloseDate(), resultUser.getCloseDate());
	}

	@Test
	public void testGetUserByFullname() {
		final Person person = UserDataManagerImplTest.personTestDataInitializer.createPerson();
		this.userDataManager.addPerson(person);

		final User user = UserDataManagerImplTest.userTestDataInitializer.createUser();
		user.setUserid(null);
		user.setPersonid(person.getId());
		user.setPerson(person);
		this.userDataManager.addUser(user);

		final User resultUser = this.userDataManager.getUserByFullname(person.getDisplayName());
		Assert.assertEquals("The user ids should be equal.", user.getUserid(), resultUser.getUserid());
		Assert.assertEquals("The usernames should be equal.", user.getName(), resultUser.getName());
		Assert.assertEquals("The Passwords should be equal.", user.getPassword(), resultUser.getPassword());
		Assert.assertEquals("The personIds should be equal.", person.getId(), resultUser.getPersonid());
		Assert.assertEquals("The install ids should be equal.", user.getInstalid(), resultUser.getInstalid());
		Assert.assertEquals("The status values should be equal.", user.getStatus(), resultUser.getStatus());
		Assert.assertEquals("The access values should be equal.", user.getAccess(), resultUser.getAccess());
		Assert.assertEquals("The type values should be equal.", user.getType(), resultUser.getType());
		Assert.assertEquals("The assign dates should be equal.", user.getAssignDate(), resultUser.getAssignDate());
		Assert.assertEquals("The close dates should be equal.", user.getCloseDate(), resultUser.getCloseDate());
	}

	@Test
	public void testGetPersonByEmail() {
		final Person person = UserDataManagerImplTest.personTestDataInitializer.createPerson();
		this.userDataManager.addPerson(person);

		final Person resultPerson = this.userDataManager.getPersonByEmail(person.getEmail());
		Assert.assertNotNull(resultPerson);
		Assert.assertEquals("The first names should be equal.", person.getFirstName(), resultPerson.getFirstName());
		Assert.assertEquals("The middle names should be equal.", person.getMiddleName(), resultPerson.getMiddleName());
		Assert.assertEquals("The last names should be equal.", person.getLastName(), resultPerson.getLastName());
		Assert.assertEquals("The ids should be equal.", person.getId(), resultPerson.getId());
		Assert.assertEquals("The institute ids should be equal.", person.getInstituteId(),
				resultPerson.getInstituteId());
		Assert.assertEquals("The position names should be equal.", person.getPositionName(),
				resultPerson.getPositionName());
		Assert.assertEquals("The titles should be equal.", person.getTitle(), resultPerson.getTitle());
		Assert.assertEquals("The extension values should be equal.", person.getExtension(),
				resultPerson.getExtension());
		Assert.assertEquals("The fax values should be equal.", person.getFax(), resultPerson.getFax());
		Assert.assertEquals("The emails should be equal.", person.getEmail(), resultPerson.getEmail());
		Assert.assertEquals("The notes should be equal.", person.getNotes(), resultPerson.getNotes());
		Assert.assertEquals("The contact should be equal.", person.getContact(), resultPerson.getContact());
		Assert.assertEquals("The language values should be equal.", person.getLanguage(), resultPerson.getLanguage());
		Assert.assertEquals("The phone values should be equal.", person.getPhone(), resultPerson.getPhone());
	}
}
