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

import org.generationcp.middleware.dao.PersonDAO;
import org.generationcp.middleware.dao.UserDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.UserDataManager;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of the UserDataManager interface. To instantiate this
 * class, a Hibernate Session must be passed to its constructor.
 */ 
public class UserDataManagerImpl extends DataManager implements UserDataManager {

    private static final Logger LOG = LoggerFactory.getLogger(UserDataManagerImpl.class);

    public UserDataManagerImpl() {
        super();
    }

    public UserDataManagerImpl(HibernateSessionProvider sessionProvider) {
        super(sessionProvider);
    }

    @Override
    public List<User> getAllUsers() throws MiddlewareQueryException {
        return (List<User>) getUserDao().getAll();
    }

    @Override
    public long countAllUsers() throws MiddlewareQueryException {
        return countAll(getUserDao());
    }

    @Override
    public Integer addUser(User user) throws MiddlewareQueryException {
        Session session = getActiveSession();
        Transaction trans = null;

        Integer idUserSaved = null;
        try {
            trans = session.beginTransaction();
            UserDAO dao = getUserDao();

            Integer userId = dao.getNextId("userid");
            user.setUserid(userId);

            User recordSaved = dao.saveOrUpdate(user);
            idUserSaved = recordSaved.getUserid();

            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error encountered while saving User: UserDataManager.addUser(user=" + user + "): " + e.getMessage(), e,
                    LOG);
        } finally {
            session.flush();
        }

        return idUserSaved;
    }
    
    @Override
    public Integer updateUser(User user) throws MiddlewareQueryException {
        Session session = getActiveSession();
        Transaction trans = null;

        try {
            trans = session.beginTransaction();
            getUserDao().saveOrUpdate(user);
            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error encountered while saving User: UserDataManager.addUser(user=" + user + "): " + e.getMessage(), e,
                    LOG);
        } finally {
            session.flush();
        }

        return user.getUserid();
    }

    @Override
    public User getUserById(int id) throws MiddlewareQueryException {
        return getUserDao().getById(id, false);
    }

    @Override
    public void deleteUser(User user) throws MiddlewareQueryException {
        Session session = getActiveSession();
        Transaction trans = null;

        try {
            trans = session.beginTransaction();
            getUserDao().makeTransient(user);
            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error encountered while deleting User: UserDataManager.deleteUser(user=" + user + "): " + e.getMessage(),
                    e, LOG);
        } finally {
            session.flush();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Person> getAllPersons() throws MiddlewareQueryException {
        return (List<Person>) getPersonDao().getAll();
    }

    //TODO BMS-148 Rename method. No loger reads from two DBs.
    @Override
    public List<Person> getAllPersonsOrderedByLocalCentral() throws MiddlewareQueryException {
        List<Person> toReturn = new ArrayList<Person>();
        PersonDAO dao = getPersonDao();
        List<Person> persons = dao.getAll();
        Collections.sort(persons);
        toReturn.addAll(persons);
        return toReturn;
    }

    public long countAllPersons() throws MiddlewareQueryException {
        return countAll(getPersonDao());
    }

    @Override
    public Integer addPerson(Person person) throws MiddlewareQueryException {
        Session session = getActiveSession();
        Transaction trans = null;

        Integer idPersonSaved = null;
        try {
            trans = session.beginTransaction();
            PersonDAO dao = getPersonDao();

            Integer personId = dao.getNextId("id");
            person.setId(personId);

            Person recordSaved = dao.saveOrUpdate(person);
            idPersonSaved = recordSaved.getId();

            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException(
                    "Error encountered while saving Person: UserDataManager.addPerson(person=" + person + "): " + e.getMessage(), e, LOG);
        } finally {
            session.flush();
        }
        return idPersonSaved;
    }
    
    @Override
    public Person getPersonById(int id) throws MiddlewareQueryException {
        return getPersonDao().getById(id, false);
    }

    @Override
    public void deletePerson(Person person) throws MiddlewareQueryException {
        Session session = getActiveSession();
        Transaction trans = null;

        try {
            trans = session.beginTransaction();
            getPersonDao().makeTransient(person);
            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException(
                    "Error encountered while deleting Person: UserDataManager.deletePerson(person=" + person + "): " + e.getMessage(), e,
                    LOG);
        } finally {
            session.flush();
        }
    }

    @Override
    public boolean isValidUserLogin(String username, String password) throws MiddlewareQueryException {
        if ((getUserDao().getByUsernameAndPassword(username, password)) != null) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isPersonExists(String firstName, String lastName) throws MiddlewareQueryException {
        if (getPersonDao().isPersonExists(firstName, lastName)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isUsernameExists(String userName) throws MiddlewareQueryException {
        if (getUserDao().isUsernameExists(userName)) {
            return true;
        }
        return false;
    }
    
    @Override
    public User getUserByUserName(String userName) throws MiddlewareQueryException {
        return getUserDao().getUserByUserName(userName);
    }
}
