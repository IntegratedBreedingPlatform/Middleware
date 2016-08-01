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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.generationcp.middleware.dao.PersonDAO;
import org.generationcp.middleware.dao.UserDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.UserDataManager;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the UserDataManager interface. To instantiate this class, a Hibernate Session must be passed to its constructor.
 */
@Transactional
public class UserDataManagerImpl extends DataManager implements UserDataManager {

	public UserDataManagerImpl() {
		super();
	}

	public UserDataManagerImpl(HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
	}

	@Override
	public List<User> getAllUsers() throws MiddlewareQueryException {
		return this.getUserDao().getAll();
	}

	@Override
	public long countAllUsers() throws MiddlewareQueryException {
		return this.countAll(this.getUserDao());
	}

	@Override
	public Integer addUser(User user) throws MiddlewareQueryException {
		Integer idUserSaved = null;
		try {

			UserDAO dao = this.getUserDao();

			User recordSaved = dao.saveOrUpdate(user);
			idUserSaved = recordSaved.getUserid();
		} catch (Exception e) {
			throw new MiddlewareQueryException("Error encountered while saving User: UserDataManager.addUser(user=" + user + "): "
					+ e.getMessage(), e);
		} 

		return idUserSaved;
	}

	@Override
	public Integer updateUser(User user) throws MiddlewareQueryException {
		try {
			this.getUserDao().saveOrUpdate(user);
		} catch (Exception e) {

			throw new MiddlewareQueryException("Error encountered while saving User: UserDataManager.addUser(user=" + user + "): "
					+ e.getMessage(), e);
		}
		return user.getUserid();
	}

	@Override
	public User getUserById(int id) throws MiddlewareQueryException {
		return this.getUserDao().getById(id, false);
	}

	@Override
	public void deleteUser(User user) throws MiddlewareQueryException {
		try {
			this.getUserDao().makeTransient(user);
		} catch (Exception e) {
			throw new MiddlewareQueryException("Error encountered while deleting User: UserDataManager.deleteUser(user=" + user + "): "
					+ e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Person> getAllPersons() throws MiddlewareQueryException {
		return this.getPersonDao().getAll();
	}

	// TODO BMS-148 Rename method. No loger reads from two DBs.
	@Override
	public List<Person> getAllPersonsOrderedByLocalCentral() throws MiddlewareQueryException {
		List<Person> toReturn = new ArrayList<Person>();
		PersonDAO dao = this.getPersonDao();
		List<Person> persons = dao.getAll();
		Collections.sort(persons);
		toReturn.addAll(persons);
		return toReturn;
	}

	@Override
	public long countAllPersons() throws MiddlewareQueryException {
		return this.countAll(this.getPersonDao());
	}

	@Override
	public Integer addPerson(Person person) throws MiddlewareQueryException {
		Integer idPersonSaved = null;
		try {
			PersonDAO dao = this.getPersonDao();
			Person recordSaved = dao.saveOrUpdate(person);
			idPersonSaved = recordSaved.getId();
		} catch (Exception e) {
			throw new MiddlewareQueryException("Error encountered while saving Person: UserDataManager.addPerson(person=" + person + "): "
					+ e.getMessage(), e);
		}
		return idPersonSaved;
	}

	@Override
	public Person getPersonById(int id) throws MiddlewareQueryException {
		return this.getPersonDao().getById(id, false);
	}

	@Override
	public void deletePerson(Person person) throws MiddlewareQueryException {
		try {
			this.getPersonDao().makeTransient(person);
		} catch (Exception e) {
			throw new MiddlewareQueryException("Error encountered while deleting Person: UserDataManager.deletePerson(person=" + person
					+ "): " + e.getMessage(), e);
		}
	}

	@Override
	public boolean isPersonExists(String firstName, String lastName) throws MiddlewareQueryException {
		if (this.getPersonDao().isPersonExists(firstName, lastName)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isUsernameExists(String userName) throws MiddlewareQueryException {
		if (this.getUserDao().isUsernameExists(userName)) {
			return true;
		}
		return false;
	}

	@Override
	public User getUserByUserName(String userName) throws MiddlewareQueryException {
		return this.getUserDao().getUserByUserName(userName);
	}

	@Override
	public Person getPersonByName(String firstName, String middleName, String lastName) throws MiddlewareQueryException {
		return this.getPersonDao().getPersonByName(firstName, middleName, lastName);
	}
}
