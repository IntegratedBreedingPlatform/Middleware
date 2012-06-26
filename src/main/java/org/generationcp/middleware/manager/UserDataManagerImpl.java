package org.generationcp.middleware.manager;

import java.util.List;

import org.generationcp.middleware.dao.PersonDAO;
import org.generationcp.middleware.dao.UserDAO;
import org.generationcp.middleware.exceptions.QueryException;
import org.generationcp.middleware.manager.api.UserDataManager;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class UserDataManagerImpl extends DataManager implements UserDataManager {

    public UserDataManagerImpl() {
        super();
    }

    public UserDataManagerImpl(HibernateUtil hibernateUtilForLocal, HibernateUtil hibernateUtilForCentral) {
        super(hibernateUtilForLocal, hibernateUtilForCentral);
    }

    @Override
    public List<User> getAllUsers() {
        UserDAO dao = new UserDAO();
        
        // get the list of Users from the local instance
        if (hibernateUtilForLocal != null) {
            dao.setSession(hibernateUtilForLocal.getCurrentSession());
        }
        List<User> users = dao.getAll();
        
        // get the list of Users from the central instance
        if (hibernateUtilForCentral != null) {
            dao.setSession(hibernateUtilForCentral.getCurrentSession());
        }
        List<User> centralUsers = dao.getAll();
        
        users.addAll(centralUsers);
        return users;
    }
    
    public int countAllUsers() {
    	
        int count = 0;
        
        UserDAO dao = new UserDAO();
        
        if (hibernateUtilForLocal != null) {
            dao.setSession(hibernateUtilForLocal.getCurrentSession());
        }
        
        count = dao.countAll().intValue();
        
        // get the list of Users from the central instance
        if (hibernateUtilForCentral != null) {
            dao.setSession(hibernateUtilForCentral.getCurrentSession());
        }
        
        count = count + dao.countAll().intValue();

        return count;
    }  

    @Override
    public void addUser(User user) throws QueryException {
        requireLocalDatabaseInstance();
        
        Session session = hibernateUtilForLocal.getCurrentSession();
        Transaction trans = null;
        
        try {
            // begin save transaction
            trans = session.beginTransaction();
            
            UserDAO dao = new UserDAO();
            dao.setSession(session);
            
            dao.saveOrUpdate(user);
            
            trans.commit();
        } catch (Exception ex) {
            // rollback transaction in case of errors
            if (trans != null) {
                trans.rollback();
            }
            throw new QueryException("Error encountered while saving User: " + ex.getMessage(), ex);
        } finally {
            hibernateUtilForLocal.closeCurrentSession();
        }
    }
    
    @Override
    public User getUserById(int id) {
        UserDAO dao = new UserDAO();
        HibernateUtil hibernateUtil = getHibernateUtil(id);

        if (hibernateUtil != null) {
            dao.setSession(hibernateUtil.getCurrentSession());
        } else {
            return null;
        }

        return dao.findById(id, false);
    }

    @Override
    public void deleteUser(User user) throws QueryException {
        requireLocalDatabaseInstance();
        
        Session session = hibernateUtilForLocal.getCurrentSession();
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
            throw new QueryException("Error encountered while saving User: " + ex.getMessage(), ex);
        } finally {
            hibernateUtilForLocal.closeCurrentSession();
        }
    }
    
    @Override
    public List<Person> getAllPersons() {
        PersonDAO dao = new PersonDAO();
        
        // get the list of Persons from the local instance
        if (hibernateUtilForLocal != null) {
            dao.setSession(hibernateUtilForLocal.getCurrentSession());
        }
        List<Person> persons = dao.getAll();
        
        // get the list of Persons from the central instance
        if (hibernateUtilForCentral != null) {
            dao.setSession(hibernateUtilForCentral.getCurrentSession());
        }
        List<Person> centralPersons = dao.getAll();
        
        persons.addAll(centralPersons);
        return persons;
    }
    
    public int countAllPersons() {
    	
        int count = 0;
        
        PersonDAO dao = new PersonDAO();
        
        if (hibernateUtilForLocal != null) {
            dao.setSession(hibernateUtilForLocal.getCurrentSession());
        }
        
        count = dao.countAll().intValue();
        
        // get the list of Users from the central instance
        if (hibernateUtilForCentral != null) {
            dao.setSession(hibernateUtilForCentral.getCurrentSession());
        }
        
        count = count + dao.countAll().intValue();

        return count;
    }    

    @Override
    public void addPerson(Person person) throws QueryException {
        requireLocalDatabaseInstance();
        
        Session session = hibernateUtilForLocal.getCurrentSession();
        Transaction trans = null;
        
        try {
            // begin save transaction
            trans = session.beginTransaction();
            
            PersonDAO dao = new PersonDAO();
            dao.setSession(session);
            
            dao.saveOrUpdate(person);
            
            trans.commit();
        } catch (Exception ex) {
            // rollback transaction in case of errors
            if (trans != null) {
                trans.rollback();
            }
            throw new QueryException("Error encountered while saving Person: " + ex.getMessage(), ex);
        } finally {
            hibernateUtilForLocal.closeCurrentSession();
        }
    }
    
    @Override
    public Person getPersonById(int id) {
        PersonDAO dao = new PersonDAO();
        HibernateUtil hibernateUtil = getHibernateUtil(id);

        if (hibernateUtil != null) {
            dao.setSession(hibernateUtil.getCurrentSession());
        } else {
            return null;
        }

        return dao.findById(id, false);
    }

    @Override
    public void deletePerson(Person person) throws QueryException {
        requireLocalDatabaseInstance();
        
        Session session = hibernateUtilForLocal.getCurrentSession();
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
            throw new QueryException("Error encountered while saving Person: " + ex.getMessage(), ex);
        } finally {
            hibernateUtilForLocal.closeCurrentSession();
        }
    }

}
