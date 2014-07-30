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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.dao.PersonDAO;
import org.generationcp.middleware.dao.UserDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.UserDataManager;
import org.generationcp.middleware.pojos.Installation;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the UserDataManager interface. To instantiate this
 * class, a Hibernate Session must be passed to its constructor.
 */ 
public class UserDataManagerImpl extends DataManager implements UserDataManager{

    private static final Logger LOG = LoggerFactory.getLogger(UserDataManagerImpl.class);

    public UserDataManagerImpl() {
        super();
    }

    public UserDataManagerImpl(HibernateSessionProvider sessionProviderForLocal, HibernateSessionProvider sessionProviderForCentral) {
        super(sessionProviderForLocal, sessionProviderForCentral);
    }

    public UserDataManagerImpl(Session sessionForLocal, Session sessionForCentral) {
        super(sessionForLocal, sessionForCentral);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<User> getAllUsers() throws MiddlewareQueryException {
        return (List<User>) getAllFromCentralAndLocal(getUserDao());
    }

    @Override
    public long countAllUsers() throws MiddlewareQueryException {
        return countAllFromCentralAndLocal(getUserDao());
    }

    @Override
    public Integer addUser(User user) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        Session session = getActiveSession();
        Transaction trans = null;

        Integer idUserSaved = null;
        try {
            trans = session.beginTransaction();
            UserDAO dao = getUserDao();

            Integer userId = dao.getNegativeId("userid");
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
    public Integer addUserToCentral(User user) throws MiddlewareQueryException {
        requireCentralDatabaseInstance();
        Session session = getActiveSession();
        Transaction trans = null;

        Integer idUserSaved = null;
        try {
            trans = session.beginTransaction();
            UserDAO dao = getUserDao();
            
            Integer userId = GenericDAO.getLastId(getActiveSession(), Database.CENTRAL, "users", "userid");
            user.setUserid(userId == null ? 1 : userId + 1);

            User recordSaved = dao.saveOrUpdate(user);
            idUserSaved = recordSaved.getUserid();

            trans.commit();
            
            session.flush();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error encountered while saving User to central database: UserDataManager.addUserToCentral(user=" + user + "): " + e.getMessage(), e,
                    LOG);
        }

        return idUserSaved;
    }


    @Override
    public Integer updateUser(User user) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
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
        if (setWorkingDatabase(id)) {
            return getUserDao().getById(id, false);
        }
        return null;
    }

    @Override
    public void deleteUser(User user) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
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
        return (List<Person>) getAllFromCentralAndLocal(getPersonDao());
    }

    @Override
    public List<Person> getAllPersonsOrderedByLocalCentral() throws MiddlewareQueryException {
        List<Person> toReturn = new ArrayList<Person>();
        PersonDAO dao = getPersonDao();
        if (setDaoSession(dao, getCurrentSessionForLocal())) {
            List<Person> localPersons = dao.getAll();
            Collections.sort(localPersons);
            toReturn.addAll(localPersons);
        }
        if (setDaoSession(dao, getCurrentSessionForCentral())) {
            List<Person> centralPersons = dao.getAll();
            List<Person> centralPersonsNew = new ArrayList<Person>();
            for(Person person : centralPersons){
            	if(person != null && !"".equalsIgnoreCase(person.getDisplayName())){
            		centralPersonsNew.add(person);
            	}
            }
            Collections.sort(centralPersonsNew);
            toReturn.addAll(centralPersonsNew);
        }
        return toReturn;
    }


    public long countAllPersons() throws MiddlewareQueryException {
        return countAllFromCentralAndLocal(getPersonDao());
    }

    @Override
    public Integer addPerson(Person person) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        Session session = getActiveSession();
        Transaction trans = null;

        Integer idPersonSaved = null;
        try {
            trans = session.beginTransaction();
            PersonDAO dao = getPersonDao();

            Integer personId = dao.getNegativeId("id");
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
    public Integer addPersonToCentral(Person person) throws MiddlewareQueryException {
        requireCentralDatabaseInstance();
        
        Session session = getActiveSession();
        Transaction trans = null;

        Integer idPersonSaved = null;
        try {
            trans = session.beginTransaction();
            PersonDAO dao = getPersonDao();
            
            Integer personId = GenericDAO.getLastId(getActiveSession(), Database.CENTRAL, "persons", "personid");
            person.setId(personId == null ? 1 : personId + 1);

            Person recordSaved = dao.saveOrUpdate(person);
            idPersonSaved = recordSaved.getId();

            trans.commit();
            
            session.flush();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException(
                    "Error encountered while saving Person to central database: UserDataManager.addPersonToCentral(person=" + person + "): " + e.getMessage(), e, LOG);
        }
        
        return idPersonSaved;
    }

    @Override
    public Person getPersonById(int id) throws MiddlewareQueryException {
        if (setWorkingDatabase(id)) {
            return getPersonDao().getById(id, false);
        }
        return null;
    }

    @Override
    public void deletePerson(Person person) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
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
        requireLocalDatabaseInstance();
        if ((getUserDao().getByUsernameAndPassword(username, password)) != null) {
            return true;
        }

        if (setWorkingDatabase(Database.CENTRAL)) {
            if ((getUserDao().getByUsernameAndPassword(username, password)) != null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isPersonExists(String firstName, String lastName) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        if (getPersonDao().isPersonExists(firstName, lastName)) {
            return true;
        }
        if (setWorkingDatabase(Database.CENTRAL)) {
            if (getPersonDao().isPersonExists(firstName, lastName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isUsernameExists(String userName) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        if (getUserDao().isUsernameExists(userName)) {
            return true;
        }
        if (setWorkingDatabase(Database.CENTRAL)) {
            if (getUserDao().isUsernameExists(userName)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public User getUserByUserName(String userName) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        return getUserDao().getUserByUserName(userName);
    }

    @Override
    public List<Installation> getAllInstallationRecords(int start, int numOfRows, Database instance) throws MiddlewareQueryException {
        if (setWorkingDatabase(instance)) {
            return getInstallationDao().getAll(start, numOfRows);
        }
        return new ArrayList<Installation>();
    }

    @Override
    public Installation getInstallationRecordById(Long id) throws MiddlewareQueryException {
        if (setWorkingDatabase(id.intValue())) {
            return getInstallationDao().getById(id, false);
        }
        return null;
    }

    @Override
    public List<Installation> getInstallationRecordsByAdminId(Long id) throws MiddlewareQueryException {
        if (setWorkingDatabase(id.intValue())) {
            return getInstallationDao().getByAdminId(id);
        }
        return null;
    }

    @Override
    public Installation getLatestInstallationRecord(Database instance) throws MiddlewareQueryException {
        if (setWorkingDatabase(instance)) {
            return getInstallationDao().getLatest(instance);
        }
        return null;
    }
}
