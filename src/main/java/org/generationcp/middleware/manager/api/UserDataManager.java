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

package org.generationcp.middleware.manager.api;

import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;

/**
 * The Interface UserDataManager.
 */
public interface UserDataManager {

	/**
	 * Returns all Persons.
	 * 
	 * @return gets all Persons
	 */
	List<User> getAllUsers() throws MiddlewareQueryException;

	/**
	 * Returns number of all Users.
	 * 
	 * @return the number of all Users
	 */
	long countAllUsers() throws MiddlewareQueryException;

	/**
	 * Adds the user.
	 * 
	 * @param user the user
	 * @return Returns the id of the {@code User} record added
	 * @throws MiddlewareQueryException the MiddlewareQueryException
	 */
	Integer addUser(User user) throws MiddlewareQueryException;

	/**
	 * Updates the user
	 * 
	 * @param user the user to update
	 * @return Returns the id of the {@code User} record updated
	 * @throws MiddlewareQueryException the MiddlewareQueryException
	 */
	Integer updateUser(User user) throws MiddlewareQueryException;

	/**
	 * Gets the user by id.
	 * 
	 * @param id the id
	 * @return the user by id
	 */
	User getUserById(int id) throws MiddlewareQueryException;

	/**
	 * Delete user.
	 * 
	 * @param user the user
	 * @throws MiddlewareQueryException the MiddlewareQueryException
	 */
	void deleteUser(User user) throws MiddlewareQueryException;

	/**
	 * Returns all Persons.
	 * 
	 * @return gets all Persons
	 */
	List<Person> getAllPersons() throws MiddlewareQueryException;

	/**
	 * Returns all Persons from local sorted by first-middle-last followed by all persons from local sorted by first-middle-last
	 * 
	 * @return gets all Persons
	 */
	List<Person> getAllPersonsOrderedByLocalCentral() throws MiddlewareQueryException;

	/**
	 * Returns number of all Persons.
	 * 
	 * @return the number of all Persons
	 */
	long countAllPersons() throws MiddlewareQueryException;

	/**
	 * Adds the person.
	 * 
	 * @param person the person
	 * @return Returns the id of the {@code Person} record added
	 * @throws MiddlewareQueryException the MiddlewareQueryException
	 */
	Integer addPerson(Person person) throws MiddlewareQueryException;

	/**
	 * Gets the person by id.
	 * 
	 * @param id the id
	 * @return the person by id
	 */
	Person getPersonById(int id) throws MiddlewareQueryException;

	/**
	 * Delete person.
	 * 
	 * @param person the person
	 * @throws MiddlewareQueryException the MiddlewareQueryException
	 */
	void deletePerson(Person person) throws MiddlewareQueryException;

	/**
	 * Checks if is person exists.
	 * 
	 * @param firstName the first name
	 * @param lastName the last name
	 * @return true, if is person exists
	 * @throws MiddlewareQueryException the MiddlewareQueryException
	 */
	boolean isPersonExists(String firstName, String lastName) throws MiddlewareQueryException;

	/**
	 * Checks if is username exists.
	 * 
	 * @param userName the user name
	 * @return true, if is username exists
	 * @throws MiddlewareQueryException the MiddlewareQueryException
	 */
	boolean isUsernameExists(String userName) throws MiddlewareQueryException;

	/**
	 * Get the User with the specified username.
	 * 
	 * @param userName
	 * @return the user with the given user name
	 * @throws MiddlewareQueryException
	 */
	User getUserByUserName(String userName) throws MiddlewareQueryException;

	/**
	 * Get Person with the specified firstname, middlename and lastname
	 * 
	 * @param firstName
	 * @param middleName
	 * @param lastName
	 * @return
	 */
	Person getPersonByName(String firstName, String middleName, String lastName) throws MiddlewareQueryException;

	/**
	 * Get Person with the specified firstname and lastname
	 *
	 * @param firstName
	 * @param middleName
	 * @param lastName
	 * @return
	 */
	Person getPersonByFirstAndLastName(String firstName, String lastName) throws MiddlewareQueryException;
	
	/**
	 * Get User using the fullname
	 * 
	 * @param fullname
	 * @return
	 */
    User getUserByFullname(String fullname);
}
