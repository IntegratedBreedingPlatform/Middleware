package org.generationcp.middleware.manager;

import java.util.List;

import org.generationcp.middleware.dao.PersonDAO;
import org.generationcp.middleware.exceptions.QueryException;
import org.generationcp.middleware.manager.api.PersonDataManager;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class PersonDataManagerImpl extends DataManager implements PersonDataManager {

    public PersonDataManagerImpl() {
        super();
    }

    public PersonDataManagerImpl(HibernateUtil hibernateUtilForLocal, HibernateUtil hibernateUtilForCentral) {
        super(hibernateUtilForLocal, hibernateUtilForCentral);
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
