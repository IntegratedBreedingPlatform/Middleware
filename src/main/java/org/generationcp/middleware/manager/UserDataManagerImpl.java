
package org.generationcp.middleware.manager;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.dao.InstallationDAO;
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

public class UserDataManagerImpl extends DataManager implements UserDataManager{

    public UserDataManagerImpl() {
        super();
    }

    public UserDataManagerImpl(HibernateSessionProvider sessionProviderForLocal, HibernateSessionProvider sessionProviderForCentral) {
        super(sessionProviderForLocal, sessionProviderForCentral);
    }

    public UserDataManagerImpl(Session sessionForLocal, Session sessionForCentral) {
        super(sessionForLocal, sessionForCentral);
    }

    @Override
    public List<User> getAllUsers() throws MiddlewareQueryException {
        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();

        UserDAO dao = new UserDAO();

        List<User> users = new ArrayList<User>();

        // get the list of Users from the local instance
        if (sessionForLocal != null) {
            dao.setSession(sessionForLocal);
            users.addAll(dao.getAll());
        }

        // get the list of Users from the central instance
        if (sessionForCentral != null) {
            dao.setSession(sessionForCentral);
            users.addAll(dao.getAll());
        }

        return users;
    }

    public long countAllUsers() throws MiddlewareQueryException {
        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();

        long count = 0;

        UserDAO dao = new UserDAO();

        if (sessionForLocal != null) {
            dao.setSession(sessionForLocal);
            count = count + dao.countAll();
        }

        // get the list of Users from the central instance
        if (sessionForCentral != null) {
            dao.setSession(sessionForCentral);
            count = count + dao.countAll();
        }

        return count;
    }

    @Override
    public Integer addUser(User user) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();

        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        Integer idUserSaved;
        try {
            // begin save transaction
            trans = session.beginTransaction();

            UserDAO dao = new UserDAO();
            dao.setSession(session);

            Integer userId = dao.getNegativeId("userid");
            user.setUserid(userId);

            User recordSaved = dao.saveOrUpdate(user);
            idUserSaved = recordSaved.getUserid();

            trans.commit();
        } catch (Exception e) {
            // rollback transaction in case of errors
            if (trans != null) {
                trans.rollback();
            }
            throw new MiddlewareQueryException("Error encountered while saving User: UserDataManager.addUser(user=" + user + "): "
                    + e.getMessage(), e);
        } finally {
            session.flush();
        }
        
        return idUserSaved;
    }

    @Override
    public Integer updateUser(User user) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();

        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        try {
            // begin save transaction
            trans = session.beginTransaction();
            UserDAO dao = new UserDAO();
            dao.setSession(session);
            dao.saveOrUpdate(user);
            trans.commit();
        } catch (Exception e) {
            // rollback transaction in case of errors
            if (trans != null) {
                trans.rollback();
            }
            throw new MiddlewareQueryException("Error encountered while saving User: UserDataManager.addUser(user=" + user + "): "
                    + e.getMessage(), e);
        } finally {
            session.flush();
        }
        
        return user.getUserid();
    }

    @Override
    public User getUserById(int id) throws MiddlewareQueryException {
        UserDAO dao = new UserDAO();
        Session session = getSession(id);

        if (session != null) {
            dao.setSession(session);
        } else {
            return null;
        }

        return dao.getById(id, false);
    }

    @Override
    public void deleteUser(User user) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();

        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        try {
            // begin save transaction
            trans = session.beginTransaction();

            UserDAO dao = new UserDAO();
            dao.setSession(session);

            dao.makeTransient(user);

            trans.commit();
        } catch (Exception e) {
            // rollback transaction in case of errors
            if (trans != null) {
                trans.rollback();
            }
            throw new MiddlewareQueryException("Error encountered while deleting User: UserDataManager.deleteUser(user=" + user + "): "
                    + e.getMessage(), e);
        } finally {
            session.flush();
        }
    }

    @Override
    public List<Person> getAllPersons() throws MiddlewareQueryException {
        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();

        PersonDAO dao = new PersonDAO();

        List<Person> persons = new ArrayList<Person>();

        // get the list of Persons from the local instance
        if (sessionForLocal != null) {
            dao.setSession(sessionForLocal);
            persons.addAll(dao.getAll());
        }

        // get the list of Persons from the central instance
        if (sessionForCentral != null) {
            dao.setSession(sessionForCentral);
            persons.addAll(dao.getAll());
        }

        return persons;
    }

    public long countAllPersons() throws MiddlewareQueryException {
        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();

        long count = 0;

        PersonDAO dao = new PersonDAO();

        if (sessionForLocal != null) {
            dao.setSession(sessionForLocal);
            count = count + dao.countAll();
        }

        // get the list of Users from the central instance
        if (sessionForCentral != null) {
            dao.setSession(sessionForCentral);
            count = count + dao.countAll();
        }

        return count;
    }

    @Override
    public Integer addPerson(Person person) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();

        Session sessionForLocal = getCurrentSessionForLocal();

        Session session = sessionForLocal;
        Transaction trans = null;

        Integer idPersonSaved;
        try {
            // begin save transaction
            trans = session.beginTransaction();

            PersonDAO dao = new PersonDAO();
            dao.setSession(session);

            Integer personId = dao.getNegativeId("id");
            person.setId(personId);

            Person recordSaved = dao.saveOrUpdate(person);
            idPersonSaved = recordSaved.getId();

            trans.commit();
        } catch (Exception e) {
            // rollback transaction in case of errors
            if (trans != null) {
                trans.rollback();
            }
            throw new MiddlewareQueryException("Error encountered while saving Person: UserDataManager.addPerson(person=" + person + "): "
                    + e.getMessage(), e);
        } finally {
            sessionForLocal.flush();
        }
        return idPersonSaved;
    }

    @Override
    public Person getPersonById(int id) throws MiddlewareQueryException {
        PersonDAO dao = new PersonDAO();
        Session session = getSession(id);

        if (session != null) {
            dao.setSession(session);
        } else {
            return null;
        }

        return dao.getById(id, false);
    }

    @Override
    public void deletePerson(Person person) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();

        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        try {
            // begin save transaction
            trans = session.beginTransaction();

            PersonDAO dao = new PersonDAO();
            dao.setSession(session);

            dao.makeTransient(person);

            trans.commit();
        } catch (Exception e) {
            // rollback transaction in case of errors
            if (trans != null) {
                trans.rollback();
            }
            throw new MiddlewareQueryException("Error encountered while deleting Person: UserDataManager.deletePerson(person=" + person
                    + "): " + e.getMessage(), e);
        } finally {
            session.flush();
        }
    }

    private List<Session> getSessions() {
        List<Session> sessions = new ArrayList<Session>();

        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();

        if (sessionForLocal != null) {
            sessions.add(sessionForLocal);
        }

        if (sessionForCentral != null) {
            sessions.add(sessionForCentral);
        }

        if (sessions.isEmpty()) {
            return null;
        } else {
            return sessions;
        }

    }

    @Override
    public boolean isValidUserLogin(String username, String password) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();

        for (Session session : getSessions()) {
            UserDAO dao = new UserDAO();
            dao.setSession(session);
            User user = dao.getByUsernameAndPassword(username, password);
            if (user != null) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isPersonExists(String firstName, String lastName) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();

        PersonDAO dao = new PersonDAO();
        for (Session session : getSessions()) {
            dao.setSession(session);
            if (dao.isPersonExists(firstName, lastName)) {
                return true;
            }
            dao.clear();
        }

        return false;
    }

    @Override
    public boolean isUsernameExists(String userName) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();

        UserDAO dao = new UserDAO();
        for (Session session : getSessions()) {
            dao.setSession(session);
            if (dao.isUsernameExists(userName)) {
                return true;
            }
            dao.clear();
        }

        return false;
    }

    @Override
    public List<Installation> getAllInstallationRecords(int start, int numOfRows, Database instance) throws MiddlewareQueryException {
        try {
            InstallationDAO dao = new InstallationDAO();
            Session session = getSession(instance);
            dao.setSession(session);

            return dao.getAll(start, numOfRows);
        } catch (Exception e) {
            throw new MiddlewareQueryException(
                    "Error with getting all installation records: UserDataManager.getAllInstallationRecords(database=" + instance + "): "
                            + e.getMessage(), e);
        }
    }

    @Override
    public Installation getInstallationRecordById(Long id) throws MiddlewareQueryException {
        try {
            InstallationDAO dao = new InstallationDAO();

            if (id < 0) {
                dao.setSession(getCurrentSessionForLocal());
            } else if (id > 0) {
                dao.setSession(getCurrentSessionForCentral());
            }

            return dao.getById(id, false);
        } catch (Exception e) {
            throw new MiddlewareQueryException(
                    "Error with getting installation record by id: UserDataManager.getInstallationRecordById(id=" + id + "): "
                            + e.getMessage(), e);
        }
    }

    @Override
    public List<Installation> getInstallationRecordsByAdminId(Long id) throws MiddlewareQueryException {
        try {
            InstallationDAO dao = new InstallationDAO();

            if (id < 0) {
                dao.setSession(getCurrentSessionForLocal());
            } else if (id > 0) {
                dao.setSession(getCurrentSessionForCentral());
            }

            return dao.getByAdminId(id);
        } catch (Exception e) {
            throw new MiddlewareQueryException(
                    "Error with getting installation record by admin id: UserDataManager.getInstallationRecordsByAdminId(id=" + id + "): "
                            + e.getMessage(), e);
        }
    }

    @Override
    public Installation getLatestInstallationRecord(Database instance) throws MiddlewareQueryException {
        try {
            InstallationDAO dao = new InstallationDAO();
            Session session = getSession(instance);
            dao.setSession(session);

            return dao.getLatest(instance);
        } catch (Exception e) {
            throw new MiddlewareQueryException(
                    "Error with getting latest installation record: UserDataManager.getLatestInstallationRecord(database=" + instance
                            + "): " + e.getMessage(), e);
        }
    }
}
