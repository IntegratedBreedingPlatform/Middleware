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
 * Implementation of the UserDataManager interface. To instantiate this class, a
 * Hibernate Session must be passed to its constructor.
 */
@Transactional
public class UserDataManagerImpl extends DataManager implements UserDataManager {

	/**
	 * Caching all the users in the system. Max is ten because we do not expect
	 * to have more than 10 war filed. Each war file will create on cache.
	 */
	private static Cache<String, List<User>> localUserCache = CacheBuilder.newBuilder().maximumSize(10)
			.expireAfterWrite(60, TimeUnit.MINUTES).build();

	/**
	 * Function to load data into the user local cache when required. Note this
	 * must be a member variable an not a static variable.
	 */
	private FunctionBasedGuavaCacheLoader<String, List<User>> functionBasedLocalUserGuavaCacheLoader;

	/**
	 * Caching all the persons in the system. Max is ten because we do not
	 * expect to have more than 10 war filed. Each war file will create on
	 * cache.
	 */
	private static Cache<String, List<Person>> localPersonCache = CacheBuilder.newBuilder().maximumSize(10)
			.expireAfterWrite(60, TimeUnit.MINUTES).build();

	/**
	 * Function to load data into the user local cache when required. Note this
	 * must be a member variable an not a static variable.
	 */
	private FunctionBasedGuavaCacheLoader<String, List<Person>> functionBasedLocalPersonGuavaCacheLoader;

	public UserDataManagerImpl() {
		super();
		this.bindCacheLoaderFunctionsToLocalUserCache();
	}

	public UserDataManagerImpl(final HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
		this.bindCacheLoaderFunctionsToLocalUserCache();
	}

	private void bindCacheLoaderFunctionsToLocalUserCache() {
		this.functionBasedLocalUserGuavaCacheLoader = new FunctionBasedGuavaCacheLoader<String, List<User>>(
				UserDataManagerImpl.localUserCache, new Function<String, List<User>>() {

					@Override
					public List<User> apply(final String key) {
						return UserDataManagerImpl.this.getUserDao().getAll();
					}
				});

		this.functionBasedLocalPersonGuavaCacheLoader = new FunctionBasedGuavaCacheLoader<String, List<Person>>(
				UserDataManagerImpl.localPersonCache, new Function<String, List<Person>>() {

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
			return this.functionBasedLocalUserGuavaCacheLoader.get(databaseConnectionUrl).get();
		} catch (HibernateException | SQLException e) {
			throw new MiddlewareQueryException(
					"Unable to connect to the database. Please contact admin for further information.", e);
		}
	}

	@Override
	public long countAllUsers() throws MiddlewareQueryException {
		return this.countAll(this.getUserDao());
	}

	@Override
	public Integer addUser(final User user) throws MiddlewareQueryException {
		Integer idUserSaved = null;
		try {
			UserDataManagerImpl.localUserCache.invalidateAll();
			UserDataManagerImpl.localPersonCache.invalidateAll();
			final UserDAO dao = this.getUserDao();

			final User recordSaved = dao.saveOrUpdate(user);
			idUserSaved = recordSaved.getUserid();
		} catch (final Exception e) {
			throw new MiddlewareQueryException("Error encountered while saving User: UserDataManager.addUser(user="
					+ user + "): " + e.getMessage(), e);
		}

		return idUserSaved;
	}

	@Override
	public Integer updateUser(final User user) throws MiddlewareQueryException {
		try {
			UserDataManagerImpl.localUserCache.invalidateAll();
			UserDataManagerImpl.localPersonCache.invalidateAll();
			this.getUserDao().saveOrUpdate(user);
		} catch (final Exception e) {

			throw new MiddlewareQueryException("Error encountered while saving User: UserDataManager.addUser(user="
					+ user + "): " + e.getMessage(), e);
		}
		return user.getUserid();
	}

	@Override
	public User getUserById(final int id) throws MiddlewareQueryException {
		return this.getUserDao().getById(id, false);
	}

	@Override
	public void deleteUser(final User user) throws MiddlewareQueryException {
		try {
			UserDataManagerImpl.localUserCache.invalidateAll();
			UserDataManagerImpl.localPersonCache.invalidateAll();
			this.getUserDao().makeTransient(user);
		} catch (final Exception e) {
			throw new MiddlewareQueryException("Error encountered while deleting User: UserDataManager.deleteUser(user="
					+ user + "): " + e.getMessage(), e);
		}
	}

	@Override
	public List<Person> getAllPersons() throws MiddlewareQueryException {
		try {
			final String databaseConnectionUrl = this.getActiveSession().connection().getMetaData().getURL();
			return this.functionBasedLocalPersonGuavaCacheLoader.get(databaseConnectionUrl).get();
		} catch (HibernateException | SQLException e) {
			throw new MiddlewareQueryException(
					"Unable to connect to the database. Please contact admin for further information.", e);
		}
	}

	// TODO BMS-148 Rename method. No loger reads from two DBs.
	@Override
	public List<Person> getAllPersonsOrderedByLocalCentral() throws MiddlewareQueryException {
		final List<Person> toReturn = new ArrayList<Person>();
		final PersonDAO dao = this.getPersonDao();
		final List<Person> persons = dao.getAll();
		Collections.sort(persons);
		toReturn.addAll(persons);
		return toReturn;
	}

	@Override
	public long countAllPersons() throws MiddlewareQueryException {
		return this.countAll(this.getPersonDao());
	}

	@Override
	public Integer addPerson(final Person person) throws MiddlewareQueryException {
		Integer idPersonSaved = null;
		try {
			UserDataManagerImpl.localUserCache.invalidateAll();
			UserDataManagerImpl.localPersonCache.invalidateAll();
			final PersonDAO dao = this.getPersonDao();
			final Person recordSaved = dao.saveOrUpdate(person);
			idPersonSaved = recordSaved.getId();
		} catch (final Exception e) {
			throw new MiddlewareQueryException(
					"Error encountered while saving Person: UserDataManager.addPerson(person=" + person + "): "
							+ e.getMessage(),
					e);
		}
		return idPersonSaved;
	}

	@Override
	public Person getPersonById(final int id) throws MiddlewareQueryException {
		return this.getPersonDao().getById(id, false);
	}

	@Override
	public void deletePerson(final Person person) throws MiddlewareQueryException {
		try {
			UserDataManagerImpl.localUserCache.invalidateAll();
			UserDataManagerImpl.localPersonCache.invalidateAll();
			this.getPersonDao().makeTransient(person);
		} catch (final Exception e) {
			throw new MiddlewareQueryException(
					"Error encountered while deleting Person: UserDataManager.deletePerson(person=" + person + "): "
							+ e.getMessage(),
					e);
		}
	}

	@Override
	public boolean isPersonExists(final String firstName, final String lastName) throws MiddlewareQueryException {
		if (this.getPersonDao().isPersonExists(firstName, lastName)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isUsernameExists(final String userName) throws MiddlewareQueryException {
		if (this.getUserDao().isUsernameExists(userName)) {
			return true;
		}
		return false;
	}

	@Override
	public User getUserByUserName(final String userName) throws MiddlewareQueryException {
		return this.getUserDao().getUserByUserName(userName);
	}

	@Override
	public Person getPersonByName(final String firstName, final String middleName, final String lastName)
			throws MiddlewareQueryException {
		return this.getPersonDao().getPersonByName(firstName, middleName, lastName);
	}

	@Override
	public User getUserByFullname(final String fullname) throws MiddlewareQueryException {
		return this.getUserDao().getUserByFullname(fullname);
	}


	@Override
	public Person getPersonByEmail(final String email) throws MiddlewareQueryException {
		return this.getPersonDao().getPersonByEmail(email);
	}

}
