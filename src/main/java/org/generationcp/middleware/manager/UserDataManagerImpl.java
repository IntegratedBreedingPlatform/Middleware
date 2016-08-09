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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.generationcp.middleware.dao.PersonDAO;
import org.generationcp.middleware.dao.UserDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.UserDataManager;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.util.cache.FunctionBasedGuavaCacheLoader;
import org.hibernate.HibernateException;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Function;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
	
/**
 * Implementation of the UserDataManager interface. To instantiate this class, a Hibernate Session must be passed to its constructor.
 */
@Transactional
public class UserDataManagerImpl extends DataManager implements UserDataManager {

	/**
	 * Caching all the users in the system. Max is ten because we do not expect to have more than 10 war filed.
	 * Each war file will create on cache.
	 */
	private static Cache<String, List<User>> localUserCache =
			CacheBuilder.newBuilder().maximumSize(10).expireAfterWrite(60, TimeUnit.MINUTES).build();

	/**
	 * Function to load data into the user local cache when required. 
	 * Note this must be a member variable an not a static variable.
	 */
	private FunctionBasedGuavaCacheLoader<String, List<User>> functionBasedLocalUserGuavaCacheLoader;
	
	/**
	 * Caching all the persons in the system. Max is ten because we do not expect to have more than 10 war filed.
	 * Each war file will create on cache.
	 */
	private static Cache<String, List<Person>> localPersonCache =
			CacheBuilder.newBuilder().maximumSize(10).expireAfterWrite(60, TimeUnit.MINUTES).build();
	
	/**
	 * Function to load data into the user local cache when required. 
	 * Note this must be a member variable an not a static variable.
	 */
	private FunctionBasedGuavaCacheLoader<String, List<Person>> functionBasedLocalPersonGuavaCacheLoader;


	public UserDataManagerImpl() {
		super();
		bindCacheLoaderFunctionsToLocalUserCache();
	}

	public UserDataManagerImpl(HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
		bindCacheLoaderFunctionsToLocalUserCache();
	}
	
	private void bindCacheLoaderFunctionsToLocalUserCache() {
		functionBasedLocalUserGuavaCacheLoader =
			new FunctionBasedGuavaCacheLoader<String, List<User>>(localUserCache, new Function<String, List<User>>() {
				@Override
				public List<User> apply(final String key) {
					return UserDataManagerImpl.this.getUserDao().getAll();
				}
			});
		
		functionBasedLocalPersonGuavaCacheLoader =
				new FunctionBasedGuavaCacheLoader<String, List<Person>>(localPersonCache, new Function<String, List<Person>>() {
					@Override
					public List<Person> apply(final String key) {
						return UserDataManagerImpl.this.getPersonDao().getAll();
					}
				});
	}



	@SuppressWarnings("deprecation")
	@Override
	public List<User> getAllUsers() throws MiddlewareQueryException {
		try {
			final String databaseConnectionUrl = this.getActiveSession().connection().getMetaData().getURL();
			return functionBasedLocalUserGuavaCacheLoader.get(databaseConnectionUrl).get();
		} catch (HibernateException | SQLException e) {
			throw new MiddlewareQueryException("Unable to connect to the database. Please contact admin for further information.", e);
		}
	}

	@Override
	public long countAllUsers() throws MiddlewareQueryException {
		return this.countAll(this.getUserDao());
	}

	@Override
	public Integer addUser(User user) throws MiddlewareQueryException {
		Integer idUserSaved = null;
		try {
			localUserCache.invalidateAll();
			localPersonCache.invalidateAll();
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
			localUserCache.invalidateAll();
			localPersonCache.invalidateAll();
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
			localUserCache.invalidateAll();
			localPersonCache.invalidateAll();
			this.getUserDao().makeTransient(user);
		} catch (Exception e) {
			throw new MiddlewareQueryException("Error encountered while deleting User: UserDataManager.deleteUser(user=" + user + "): "
					+ e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Person> getAllPersons() throws MiddlewareQueryException {
		try {
			final String databaseConnectionUrl = this.getActiveSession().connection().getMetaData().getURL();
			return functionBasedLocalPersonGuavaCacheLoader.get(databaseConnectionUrl).get();
		} catch (HibernateException | SQLException e) {
			throw new MiddlewareQueryException("Unable to connect to the database. Please contact admin for further information.", e);
		}
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
			localUserCache.invalidateAll();
			localPersonCache.invalidateAll();
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
	public Person getPersonById(final int id) throws MiddlewareQueryException {
		return this.getPersonDao().getById(id, false);
	}

	@Override
	public void deletePerson(Person person) throws MiddlewareQueryException {
		try {
			localUserCache.invalidateAll();
			localPersonCache.invalidateAll();
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
