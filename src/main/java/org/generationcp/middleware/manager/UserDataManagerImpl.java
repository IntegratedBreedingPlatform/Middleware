package org.generationcp.middleware.manager;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.dao.InstallationDAO;
import org.generationcp.middleware.dao.PersonDAO;
import org.generationcp.middleware.dao.UserDAO;
import org.generationcp.middleware.exceptions.QueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.UserDataManager;
import org.generationcp.middleware.pojos.Installation;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class UserDataManagerImpl extends DataManager implements UserDataManager {
    
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
    public List<User> getAllUsers() {
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
    
    public int countAllUsers() {
        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();
        
        int count = 0;
        
        UserDAO dao = new UserDAO();
        
        if (sessionForLocal != null) {
            dao.setSession(sessionForLocal);
            count = count + dao.countAll().intValue();
        }
        
        // get the list of Users from the central instance
        if (sessionForCentral != null) {
            dao.setSession(sessionForCentral);
            count = count + dao.countAll().intValue();
        }
        
        return count;
    }  

    @Override
    public void addUser(User user) throws QueryException {
        requireLocalDatabaseInstance();
        
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;
        
        try {
            // begin save transaction
            trans = session.beginTransaction();
            
            UserDAO dao = new UserDAO();
            dao.setSession(session);
            
            Integer userId = dao.getNegativeId("userid");
            user.setUserid(userId);
            
            dao.saveOrUpdate(user);
            
            trans.commit();
        } catch (Exception ex) {
            // rollback transaction in case of errors
            if (trans != null) {
                trans.rollback();
            }
            throw new QueryException("Error encountered while saving User: " + ex.getMessage(), ex);
        } finally {
            session.flush();
        }
    }
    
    @Override
    public User getUserById(int id) {
        UserDAO dao = new UserDAO();
        Session session = getSession(id);

        if (session != null) {
            dao.setSession(session);
        } else {
            return null;
        }

        return dao.findById(id, false);
    }

    @Override
    public void deleteUser(User user) throws QueryException {
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
        } catch (Exception ex) {
            // rollback transaction in case of errors
            if (trans != null) {
                trans.rollback();
            }
            throw new QueryException("Error encountered while deleting User: " + ex.getMessage(), ex);
        } finally {
            session.flush();
        }
    }
    
    @Override
    public List<Person> getAllPersons() {
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
    
    public int countAllPersons() {
        Session sessionForCentral = getCurrentSessionForCentral();
        Session sessionForLocal = getCurrentSessionForLocal();
        
        int count = 0;
        
        PersonDAO dao = new PersonDAO();
        
        if (sessionForLocal != null) {
            dao.setSession(sessionForLocal);
            count = count + dao.countAll().intValue();
        }
        
        // get the list of Users from the central instance
        if (sessionForCentral != null) {
            dao.setSession(sessionForCentral);
            count = count + dao.countAll().intValue();
        }
        
        return count;
    }    

    @Override
    public void addPerson(Person person) throws QueryException {
        requireLocalDatabaseInstance();
        
        Session sessionForLocal = getCurrentSessionForLocal();
        
        Session session = sessionForLocal;
        Transaction trans = null;
        
        try {
            // begin save transaction
            trans = session.beginTransaction();
            
            PersonDAO dao = new PersonDAO();
            dao.setSession(session);
            
            Integer personId = dao.getNegativeId("id");
            person.setId(personId);
            
            dao.saveOrUpdate(person);
            
            trans.commit();
        } catch (Exception ex) {
            // rollback transaction in case of errors
            if (trans != null) {
                trans.rollback();
            }
            throw new QueryException("Error encountered while saving Person: " + ex.getMessage(), ex);
        } finally {
            sessionForLocal.flush();
        }
    }
    
    @Override
    public Person getPersonById(int id) {
        PersonDAO dao = new PersonDAO();
        Session session = getSession(id);

        if (session != null) {
            dao.setSession(session);
        } else {
            return null;
        }

        return dao.findById(id, false);
    }

    @Override
    public void deletePerson(Person person) throws QueryException {
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
        } catch (Exception ex) {
            // rollback transaction in case of errors
            if (trans != null) {
                trans.rollback();
            }
            throw new QueryException("Error encountered while deleting Person: " + ex.getMessage(), ex);
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
        
        if(sessions.isEmpty()) {
            return null; 
        } else {
            return sessions;
        }
        
    }
    
    @Override
    public boolean isValidUserLogin(String username, String password) throws QueryException {
        requireLocalDatabaseInstance();
        
        for (Session session : getSessions()) {
            UserDAO dao = new UserDAO();
            dao.setSession(session);
            User user = dao.findByUsernameAndPassword(username, password);
            if (user != null) {
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public boolean isPersonExists(String firstName, String lastName) throws QueryException {
        requireLocalDatabaseInstance();
        
        PersonDAO dao = new PersonDAO();
        for(Session session : getSessions()) {
            dao.setSession(session);
            if(dao.isPersonExists(firstName, lastName)) {
                return true;
            }   
            dao.clear();
        }
        
        return false;
    }
    
    @Override
    public boolean isUsernameExists(String userName) throws QueryException {
        requireLocalDatabaseInstance();
        
        UserDAO dao = new UserDAO();
        for(Session session : getSessions()) {
            dao.setSession(session);
            if(dao.isUsernameExists(userName)) {
                return true;
            }
            dao.clear();
        }
        
        return false;
    }
    
    @Override
    public List<Installation> getAllInstallationRecords(int start, int numOfRows, Database instance) throws QueryException {
        try{
            InstallationDAO dao = new InstallationDAO();
            Session session = getSession(instance);
            dao.setSession(session);
            
            return dao.getAll(start, numOfRows);
        } catch(Exception ex) {
            throw new QueryException("Error with getting all installation records: "
                    + ex.getMessage(), ex);
        }
    }
}
