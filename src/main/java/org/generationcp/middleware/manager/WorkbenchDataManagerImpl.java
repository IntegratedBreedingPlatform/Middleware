package org.generationcp.middleware.manager;

import java.util.List;

import org.generationcp.middleware.dao.ProjectDAO;
import org.generationcp.middleware.dao.WorkflowTemplateDAO;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.WorkflowTemplate;
import org.generationcp.middleware.util.HibernateUtil;
import org.hibernate.QueryException;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class WorkbenchDataManagerImpl implements WorkbenchDataManager {
    private HibernateUtil hibernateUtil;
    
    public WorkbenchDataManagerImpl(HibernateUtil hibernateUtil) {
        this.hibernateUtil = hibernateUtil;
    }
    
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
        }
        catch (Exception e) {
            if (trans != null) {
                trans.rollback();
            }
            
            throw new QueryException("Cannot save Project", e);
        }
        finally {
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
        }
        catch (Exception e) {
            if (trans != null) {
                trans.rollback();
            }
            
            throw new QueryException("Cannot save Project", e);
        }
        finally {
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
}
