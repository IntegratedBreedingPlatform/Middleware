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
import java.util.List;

import org.generationcp.middleware.dao.DatasetDAO;
import org.generationcp.middleware.dao.PersonDAO;
import org.generationcp.middleware.dao.ProjectDAO;
import org.generationcp.middleware.dao.ToolDAO;
import org.generationcp.middleware.dao.UserDAO;
import org.generationcp.middleware.dao.WorkflowTemplateDAO;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.WorkbenchDataset;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.WorkflowTemplate;
import org.generationcp.middleware.util.HibernateUtil;
import org.hibernate.QueryException;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class WorkbenchDataManagerImpl implements WorkbenchDataManager{

    private HibernateUtil hibernateUtil;

    public WorkbenchDataManagerImpl(HibernateUtil hibernateUtil) {
        this.hibernateUtil = hibernateUtil;
    }

    @Override
    public List<Project> getProjects() {
        ProjectDAO projectDao = new ProjectDAO();
        projectDao.setSession(hibernateUtil.getCurrentSession());
        return projectDao.findAll();
    }

    @Override
    public List<Project> getProjects(int start, int numOfRows) {
        ProjectDAO projectDao = new ProjectDAO();
        projectDao.setSession(hibernateUtil.getCurrentSession());
        return projectDao.findAll(start, numOfRows);
    }

    @Override
    public Project saveOrUpdateProject(Project project) {
        Session session = hibernateUtil.getCurrentSession();
        Transaction trans = null;

        try {
            trans = session.beginTransaction();

            ProjectDAO projectDao = new ProjectDAO();
            projectDao.setSession(session);
            projectDao.saveOrUpdate(project);

            // TODO: copy the workbench template created by the project into the
            // project_workflow_step table

            // commit transaction
            trans.commit();
        } catch (Exception e) {
            if (trans != null) {
                trans.rollback();
            }

            throw new QueryException("Cannot save Project", e);
        } finally {
            hibernateUtil.closeCurrentSession();
        }

        return project;
    }

    @Override
    public void deleteProject(Project project) {
        Session session = hibernateUtil.getCurrentSession();
        Transaction trans = null;

        try {
            trans = session.beginTransaction();

            ProjectDAO projectDao = new ProjectDAO();
            projectDao.setSession(session);
            projectDao.makeTransient(project);

            trans.commit();
        } catch (Exception e) {
            if (trans != null) {
                trans.rollback();
            }

            throw new QueryException("Cannot save Project", e);
        } finally {
            hibernateUtil.closeCurrentSession();
        }
    }

    @Override
    public List<WorkflowTemplate> getWorkflowTemplates() {
        WorkflowTemplateDAO workflowTemplateDAO = new WorkflowTemplateDAO();
        workflowTemplateDAO.setSession(hibernateUtil.getCurrentSession());
        return workflowTemplateDAO.findAll();
    }

    @Override
    public List<WorkflowTemplate> getWorkflowTemplates(int start, int numOfRows) {
        WorkflowTemplateDAO workflowTemplateDAO = new WorkflowTemplateDAO();
        workflowTemplateDAO.setSession(hibernateUtil.getCurrentSession());
        return workflowTemplateDAO.findAll(start, numOfRows);
    }

    @Override
    public Tool getToolWithName(String toolId) {
        ToolDAO toolDAO = new ToolDAO();
        toolDAO.setSession(hibernateUtil.getCurrentSession());
        return toolDAO.findByToolName(toolId);
    }
    
    public Project getProjectById(Long projectId){
        ProjectDAO projectDao = new ProjectDAO();
        projectDao.setSession(hibernateUtil.getCurrentSession());
        return projectDao.getById(projectId);
    }

    public WorkbenchDataset addDataset(WorkbenchDataset dataset) throws QueryException {
        
        Session session = hibernateUtil.getCurrentSession();
        Transaction trans = null;

        try {
            // begin save transaction
            trans = session.beginTransaction();
            System.out.println("WorkbenchDatamanagerImpl.dataset: " + dataset);
            DatasetDAO datasetDAO = new DatasetDAO();
            datasetDAO.setSession(session);
            datasetDAO.saveOrUpdate(dataset);
            System.out.println("WorkbenchDatamanagerImpl.dataset: " + dataset);

            trans.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
            // rollback transaction in case of errors
            if (trans != null) {
                trans.rollback();
            }
            throw new QueryException("Error encountered while saving workbench dataset: " + ex.getMessage(), ex);
        } finally {
            hibernateUtil.closeCurrentSession();
        }

        return dataset;
    }
    
    @Override
    public List<User> getAllUsers() {
        UserDAO dao = new UserDAO();
        
        List<User> users = new ArrayList<User>();
        
        if (hibernateUtil != null) {
            dao.setSession(hibernateUtil.getCurrentSession());
            users.addAll(dao.getAll());
        }
        
        return users;
    }
    
    public int countAllUsers() {
    	
        int count = 0;
        
        UserDAO dao = new UserDAO();
        
        if (hibernateUtil != null) {
            dao.setSession(hibernateUtil.getCurrentSession());
            count = count + dao.countAll().intValue();
        }
        return count;
    }  

    @Override
    public void addUser(User user) throws QueryException {
        
        Session session = hibernateUtil.getCurrentSession();
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
            hibernateUtil.closeCurrentSession();
        }
    }
    
    @Override
    public User getUserById(int id) {
        UserDAO dao = new UserDAO();
        
        if (hibernateUtil != null) {
            dao.setSession(hibernateUtil.getCurrentSession());
        } else {
            return null;
        }

        return dao.findById(id, false);
    }

    @Override
    public void deleteUser(User user) throws QueryException {
        
        Session session = hibernateUtil.getCurrentSession();
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
            hibernateUtil.closeCurrentSession();
        }
    }
    
    @Override
    public List<Person> getAllPersons() {
        PersonDAO dao = new PersonDAO();
        
        List<Person> persons = new ArrayList<Person>();
        
        // get the list of Persons from the local instance
        if (hibernateUtil != null) {
            dao.setSession(hibernateUtil.getCurrentSession());
            persons.addAll(dao.getAll());
        }
        
        return persons;
    }
    
    public int countAllPersons() {
    	
        int count = 0;
        
        PersonDAO dao = new PersonDAO();
        
        if (hibernateUtil != null) {
            dao.setSession(hibernateUtil.getCurrentSession());
            count = count + dao.countAll().intValue();
        }
        
        return count;
    }    

    @Override
    public void addPerson(Person person) throws QueryException {
        
        Session session = hibernateUtil.getCurrentSession();
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
            hibernateUtil.closeCurrentSession();
        }
    }
    
    @Override
    public Person getPersonById(int id) {
        PersonDAO dao = new PersonDAO();

        if (hibernateUtil != null) {
            dao.setSession(hibernateUtil.getCurrentSession());
        } else {
            return null;
        }

        return dao.findById(id, false);
    }

    @Override
    public void deletePerson(Person person) throws QueryException {
        
        Session session = hibernateUtil.getCurrentSession();
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
            hibernateUtil.closeCurrentSession();
        }
    }
    
    @Override
    public boolean isValidUserLogin(String username, String password) throws QueryException {
        
        UserDAO dao = new UserDAO();
        
        dao.setSession(hibernateUtil.getCurrentSession());
        
        User user = dao.findByUsernameAndPassword(username, password);
        
        if (user != null) {
            return true;
        }
        
        
        return false;
    }
    
    @Override
    public boolean isPersonExists(String firstName, String lastName) throws QueryException {
        
        PersonDAO dao = new PersonDAO();
        
        dao.setSession(hibernateUtil.getCurrentSession());
        if(dao.isPersonExists(firstName, lastName)) {
            return true;
        }   
        
        
        return false;
    }
    
    @Override
    public boolean isUsernameExists(String userName) throws QueryException {
        
        UserDAO dao = new UserDAO();
        
        dao.setSession(hibernateUtil.getCurrentSession());
        
        if(dao.isUsernameExists(userName)) {
            return true;
        }
        
        dao.clear();

        return false;
    }

}
