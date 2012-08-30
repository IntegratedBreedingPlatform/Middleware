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

import org.generationcp.middleware.dao.PersonDAO;
import org.generationcp.middleware.dao.ProjectActivityDAO;
import org.generationcp.middleware.dao.ProjectDAO;
import org.generationcp.middleware.dao.ProjectLocationMapDAO;
import org.generationcp.middleware.dao.ProjectMethodDAO;
import org.generationcp.middleware.dao.ProjectUserDAO;
import org.generationcp.middleware.dao.ToolConfigurationDAO;
import org.generationcp.middleware.dao.ToolDAO;
import org.generationcp.middleware.dao.UserDAO;
import org.generationcp.middleware.dao.WorkbenchDatasetDAO;
import org.generationcp.middleware.dao.WorkflowTemplateDAO;
import org.generationcp.middleware.exceptions.QueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectActivity;
import org.generationcp.middleware.pojos.workbench.ProjectLocationMap;
import org.generationcp.middleware.pojos.workbench.ProjectMethod;
import org.generationcp.middleware.pojos.workbench.ProjectUser;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.ToolConfiguration;
import org.generationcp.middleware.pojos.workbench.ToolType;
import org.generationcp.middleware.pojos.workbench.WorkbenchDataset;
import org.generationcp.middleware.pojos.workbench.WorkflowTemplate;
import org.generationcp.middleware.util.HibernateUtil;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkbenchDataManagerImpl implements WorkbenchDataManager{

	private static final Logger LOG = LoggerFactory.getLogger(WorkbenchDataManagerImpl.class);

	private HibernateUtil hibernateUtil;

	public WorkbenchDataManagerImpl(HibernateUtil hibernateUtil) {
		this.hibernateUtil = hibernateUtil;
	}

	@Override
	public List<Project> getProjects() throws QueryException{
		ProjectDAO projectDao = new ProjectDAO();
		projectDao.setSession(hibernateUtil.getCurrentSession());
		return projectDao.findAll();
	}

	@Override
	public List<Project> getProjects(int start, int numOfRows)  throws QueryException{
		ProjectDAO projectDao = new ProjectDAO();
		projectDao.setSession(hibernateUtil.getCurrentSession());
		return projectDao.findAll(start, numOfRows);
	}

	@Override
	public Project saveOrUpdateProject(Project project)  throws QueryException{
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
	public void deleteProject(Project project)  throws QueryException{
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
	public List<WorkflowTemplate> getWorkflowTemplates()  throws QueryException{
		WorkflowTemplateDAO workflowTemplateDAO = new WorkflowTemplateDAO();
		workflowTemplateDAO.setSession(hibernateUtil.getCurrentSession());
		return workflowTemplateDAO.findAll();
	}

	@Override
	public List<WorkflowTemplate> getWorkflowTemplates(int start, int numOfRows)  throws QueryException{
		WorkflowTemplateDAO workflowTemplateDAO = new WorkflowTemplateDAO();
		workflowTemplateDAO.setSession(hibernateUtil.getCurrentSession());
		return workflowTemplateDAO.findAll(start, numOfRows);
	}

	@Override
	public Tool getToolWithName(String toolId)  throws QueryException{
		ToolDAO toolDAO = new ToolDAO();
		toolDAO.setSession(hibernateUtil.getCurrentSession());
		return toolDAO.findByToolName(toolId);
	}

	@Override
	public List<Tool> getToolsWithType(ToolType toolType) throws QueryException {
		ToolDAO toolDAO = new ToolDAO();
		toolDAO.setSession(hibernateUtil.getCurrentSession());
		return toolDAO.findToolsByToolType(toolType);
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

		return dao.isPersonExists(firstName, lastName);
	}

	@Override
	public boolean isUsernameExists(String userName) throws QueryException {
		UserDAO dao = new UserDAO();
		dao.setSession(hibernateUtil.getCurrentSession());

		return dao.isUsernameExists(userName);
	}

	@Override
	public void addPerson(Person person) throws QueryException {
		Session session = hibernateUtil.getCurrentSession();
		Transaction trans = null;

		try {
			// begin save transaction
			trans = session.beginTransaction();

			SQLQuery q = session.createSQLQuery("SELECT MAX(personid) FROM persons");
			Integer personId = (Integer) q.uniqueResult();

			if(personId == null || personId.intValue() < 0) {
				person.setId(1);
			} else {
				person.setId(personId + 1);
			}

			PersonDAO dao = new PersonDAO();
			dao.setSession(session);

			dao.saveOrUpdate(person);

			trans.commit();
		} catch (Exception ex) {
			LOG.error("Error in addPerson: " + ex.getMessage() + "\n" + ex.getStackTrace());
			ex.printStackTrace();
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
	public void addUser(User user) throws QueryException {
		Session session = hibernateUtil.getCurrentSession();
		Transaction trans = null;

		try {
			// begin save transaction
			trans = session.beginTransaction();

			SQLQuery q = session.createSQLQuery("SELECT MAX(userid) FROM users");
			Integer userId = (Integer) q.uniqueResult();

			if(userId == null || userId.intValue() < 0) {
				user.setUserid(1);
			} else {
				user.setUserid(userId + 1);
			}

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

	public Project getProjectById(Long projectId) throws QueryException{
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
			WorkbenchDatasetDAO datasetDAO = new WorkbenchDatasetDAO();
			datasetDAO.setSession(session);
			datasetDAO.saveOrUpdate(dataset);
			System.out.println("WorkbenchDatamanagerImpl.dataset: " + dataset);

			trans.commit();
		} catch (Exception ex) {
			LOG.error("Error in addDataset: " + ex.getMessage() + "\n" + ex.getStackTrace());
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
	public List<User> getUserByName(String name, int start, int numOfRows, Operation op) throws QueryException {

		UserDAO dao = new UserDAO();

		List<User> users = new ArrayList<User>();

		if (hibernateUtil != null) {
			dao.setSession(hibernateUtil.getCurrentSession());
		} else {
			return users;
		}

		if (op == Operation.EQUAL) {
			users.add(dao.findByNameUsingEqual(name, start, numOfRows));
		} else if (op == Operation.LIKE) {
			users = dao.findByNameUsingLike(name, start, numOfRows);
		}

		return users;

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
	public Project getLastOpenedProject(Integer userId) throws QueryException {
		ProjectDAO dao = new ProjectDAO();
		dao.setSession(hibernateUtil.getCurrentSession());
		return dao.getLastOpenedProject(userId);
	}

	@Override
	public List<WorkbenchDataset> getWorkbenchDatasetByProjectId(Long projectId, int start, int numOfRows) 
			throws QueryException {

		WorkbenchDatasetDAO dao = new WorkbenchDatasetDAO();
		List<WorkbenchDataset> list;

		if(hibernateUtil != null) {
			dao.setSession(hibernateUtil.getCurrentSession());
			list = dao.getByProjectId(projectId, start, numOfRows);
		} else {
			list = new ArrayList<WorkbenchDataset>();
		}

		return list;
	}

	@Override
	public Long countWorkbenchDatasetByProjectId(Long projectId) throws QueryException {
		WorkbenchDatasetDAO dao = new WorkbenchDatasetDAO();
		Long result = 0L;

		if(hibernateUtil != null) {
			dao.setSession(hibernateUtil.getCurrentSession());
			result = dao.countByProjectId(projectId);
		}

		return result;
	}

	@Override
	public List<WorkbenchDataset> getWorkbenchDatasetByName(String name, Operation op, 
			int start, int numOfRows) 
					throws QueryException {

		WorkbenchDatasetDAO dao = new WorkbenchDatasetDAO();
		List<WorkbenchDataset> list;

		if(hibernateUtil != null) {
			dao.setSession(hibernateUtil.getCurrentSession());
			list = dao.getByName(name, op, start, numOfRows);
		} else {
			list = new ArrayList<WorkbenchDataset>();
		}

		return list;
	}

	@Override
	public Long countWorkbenchDatasetByName(String name, Operation op) throws QueryException {
		WorkbenchDatasetDAO dao = new WorkbenchDatasetDAO();
		Long result = 0L;

		if(hibernateUtil != null) {
			dao.setSession(hibernateUtil.getCurrentSession());
			result = dao.countByName(name, op);
		}

		return result;
	}

	@Override
	public List<Long> getLocationIdsByProjectId(Long projectId, int start, int numOfRows) 
			throws QueryException {

		ProjectLocationMapDAO dao = new ProjectLocationMapDAO();
		List<Long> ids;

		if(hibernateUtil != null) {
			dao.setSession(hibernateUtil.getCurrentSession());
			ids = dao.getLocationIdsByProjectId(projectId, start, numOfRows);
		} else {
			ids = new ArrayList<Long>();
		}

		return ids;
	}

	@Override
	public Long countLocationIdsByProjectId(Long projectId) throws QueryException {
		ProjectLocationMapDAO dao = new ProjectLocationMapDAO();
		Long result = 0L;

		if(hibernateUtil != null) {
			dao.setSession(hibernateUtil.getCurrentSession());
			result = dao.countLocationIdsByProjectId(projectId);
		} 

		return result;
	}

	@Override
	public List<Integer> getMethodIdsByProjectId(Long projectId, int start, int numOfRows) throws QueryException{

		ProjectMethodDAO dao = new ProjectMethodDAO();
		List<Integer> list;

		if(hibernateUtil != null) {
			dao.setSession(hibernateUtil.getCurrentSession());
			list = dao.getByProjectId(projectId, start, numOfRows);
		} else {
			list = new ArrayList<Integer>();
		}

		return list;

	}

	@Override
	public Long countMethodIdsByProjectId(Long projectId) throws QueryException {
		ProjectMethodDAO dao = new ProjectMethodDAO();
		Long result = 0L;

		if(hibernateUtil != null) {
			dao.setSession(hibernateUtil.getCurrentSession());
			result = dao.countByProjectId(projectId);
		}

		return result;
	}
	@Override
	public int addProjectUser(Project project, User user) throws QueryException{
		ProjectUser projectUser = new ProjectUser();
		projectUser.setProject(project);
		projectUser.setUserId(user.getUserid());
		return addProjectUser(projectUser);
	}

	@Override
	public int addProjectUser(ProjectUser projectUser) throws QueryException {
		if(hibernateUtil == null) {
			return 0;
		}

		Session session = hibernateUtil.getCurrentSession();
		Transaction trans = null;

		int recordsSaved = 0;
		try {
			// begin save transaction
			trans = session.beginTransaction();            
			ProjectUserDAO dao = new ProjectUserDAO();
			dao.setSession(session);

			dao.saveOrUpdate(projectUser);
			recordsSaved++;

			trans.commit();
		} catch (Exception ex) {
			LOG.error("Error in addProjectUser: " + ex.getMessage() + "\n" + ex.getStackTrace());
			ex.printStackTrace();
			// rollback transaction in case of errors
			if (trans != null) {
				trans.rollback();
			}
			throw new QueryException("Error encountered while saving ProjectUser: " + ex.getMessage(), ex);
		} finally {
			hibernateUtil.closeCurrentSession();
		}
		return recordsSaved;
	}

	@Override
	public int addProjectUsers(List<ProjectUser> projectUsers) throws QueryException{
		if(hibernateUtil == null) {
			return 0;
		}
		Session session = hibernateUtil.getCurrentSession();
		Transaction trans = null;

		int recordsSaved = 0;
		try {            
			// begin save transaction
			trans = session.beginTransaction();            
			ProjectUserDAO dao = new ProjectUserDAO();
			dao.setSession(session);

			for (ProjectUser projectUser : projectUsers){
				dao.saveOrUpdate(projectUser);
				recordsSaved++;
			}

			trans.commit();
		} catch (Exception ex) {
			LOG.error("Error in addProjectUsers: " + ex.getMessage() + "\n" + ex.getStackTrace());
			ex.printStackTrace();
			// rollback transaction in case of errors
			if (trans != null) {
				trans.rollback();
			}
			throw new QueryException("Error encountered while saving ProjectUsers: " + ex.getMessage(), ex);
		} finally {
			hibernateUtil.closeCurrentSession();
		}
		return recordsSaved;
	}

	@Override
	public ProjectUser getProjectUserById(Integer id) throws QueryException {
		ProjectUserDAO dao = new ProjectUserDAO();
		if (hibernateUtil != null) {
			dao.setSession(hibernateUtil.getCurrentSession());
			return dao.getById(id);
		}
		return null;
	}

	@Override
	public ProjectUser getProjectUserByProjectAndUser(Project project, User user) throws QueryException{
		ProjectUserDAO dao = new ProjectUserDAO();
		if (hibernateUtil != null) {
			dao.setSession(hibernateUtil.getCurrentSession());
			return dao.getByProjectAndUser(project, user);
		}
		return null;
	}

	@Override
	public void deleteProjectUser(ProjectUser projectUser) throws QueryException {
		if (hibernateUtil == null) {
			return;
		}

		Session session = hibernateUtil.getCurrentSession();
		Transaction trans = null;

		try {
			// begin save transaction
			trans = session.beginTransaction();

			ProjectUserDAO dao = new ProjectUserDAO();
			dao.setSession(session);

			dao.makeTransient(projectUser);

			trans.commit();
		} catch (Exception ex) {
			// rollback transaction in case of errors
			if (trans != null) {
				trans.rollback();
			}
			throw new QueryException("Error encountered while deleting ProjectUser: " + ex.getMessage(), ex);
		} finally {
			hibernateUtil.closeCurrentSession();
		}
	}

	@Override
	public List<User> getUsersByProjectId(Long projectId) throws QueryException {
		ProjectUserDAO dao = new ProjectUserDAO();
		List<User> users;
		if(hibernateUtil != null) {
			dao.setSession(hibernateUtil.getCurrentSession());
			users = dao.getUsersByProjectId(projectId);
		} else {
			users = new ArrayList<User>();
		}

		return users;
	}

	@Override
	public Long countUsersByProjectId(Long projectId) throws QueryException {
		ProjectUserDAO dao = new ProjectUserDAO();
		Long result = 0L;

		if(hibernateUtil != null) {
			dao.setSession(hibernateUtil.getCurrentSession());
			result = dao.countUsersByProjectId(projectId);
		}

		return result;
	}

	public List<CropType> getInstalledCentralCrops() throws QueryException {
		List<CropType> cropTypes = new ArrayList<CropType>();

		if(hibernateUtil != null) {
			Session session = hibernateUtil.getCurrentSession();

			try {
				@SuppressWarnings("unchecked")
				List<String> cropNames = session.createSQLQuery("SELECT crop_name FROM workbench_crops").list();

				for (String cropName : cropNames) {
					CropType cropType = CropType.valueOf(cropName);
					cropTypes.add(cropType);
				}
			}
			catch (HibernateException e) {
				throw new QueryException("Error getting crop types: " + e.getMessage(), e);
			}

			return cropTypes;
		}

		return cropTypes;
	}

	@Override
	public int addProjectLocationMap(ProjectLocationMap projectLocationMap)
			throws QueryException {
		List<ProjectLocationMap> list = new ArrayList<ProjectLocationMap>();
		list.add(projectLocationMap);
		return addProjectLocationMap(list);
	}

	public int addProjectLocationMap(List<ProjectLocationMap> projectLocationMapDatas) throws QueryException {
		return addOrUpdateProjectLocationMapData(projectLocationMapDatas, Operation.ADD);
	}

	private int addOrUpdateProjectLocationMapData(List<ProjectLocationMap> projectLocationMapDatas, Operation operation) throws QueryException {

		if(hibernateUtil == null) {
			return 0;
		}
		Session session = hibernateUtil.getCurrentSession();
		Transaction trans = null;

		int recordsSaved = 0;
		try {
			// begin save transaction
			trans = session.beginTransaction();

			ProjectLocationMapDAO dao = new ProjectLocationMapDAO();
			dao.setSession(session);

			for (ProjectLocationMap projectLocationMapData : projectLocationMapDatas) {
				dao.saveOrUpdate(projectLocationMapData);
				recordsSaved++;

			}
			// end transaction, commit to database
			trans.commit();
		} catch (Exception ex) {
			LOG.error("Error in addProjectLocation: " + ex.getMessage() + "\n" + ex.getStackTrace());
			ex.printStackTrace();
			// rollback transaction in case of errors
			if (trans != null) {
				trans.rollback();
			}
			throw new QueryException("Error encountered while adding ProjectLocation: " + ex.getMessage(), ex);
		} finally {
			hibernateUtil.closeCurrentSession();
		}

		return recordsSaved;
	}

	@Override
	public int addProjectMethod(ProjectMethod projectMethod) throws QueryException {
		List<ProjectMethod> list = new ArrayList<ProjectMethod>();
		list.add(projectMethod);
		return addProjectMethod(list);
	}

	@Override
	public int addProjectMethod(List<ProjectMethod> projectMethodList) throws QueryException {
		return addOrUpdateProjectMethodData(projectMethodList, Operation.ADD);
	}

	private int addOrUpdateProjectMethodData(List<ProjectMethod> projectMethodList, Operation add) throws QueryException {
		if(hibernateUtil == null) {
			return 0;
		}
		Session session = hibernateUtil.getCurrentSession();
		Transaction trans = null;

		int recordsSaved = 0;
		try {
			// begin save transaction
			trans = session.beginTransaction();

			ProjectMethodDAO dao = new ProjectMethodDAO();
			dao.setSession(session);

			for (ProjectMethod projectMethodListData : projectMethodList) {
				dao.saveOrUpdate(projectMethodListData);
				recordsSaved++;

			}
			// end transaction, commit to database
			trans.commit();
		} catch (Exception ex) {
			LOG.error("Error in addProjectMethod: " + ex.getMessage() + "\n" + ex.getStackTrace());
			ex.printStackTrace();
			// rollback transaction in case of errors
			if (trans != null) {
				trans.rollback();
			}
			throw new QueryException("Error encountered while adding ProjectMethod: " + ex.getMessage(), ex);
		} finally {
			hibernateUtil.closeCurrentSession();
		}

		return recordsSaved;
	}

    @Override
    public int addProjectActivity(ProjectActivity projectActivity) throws QueryException {
        List<ProjectActivity> list = new ArrayList<ProjectActivity>();
        list.add(projectActivity);
        return addProjectActivity(list);
    }

    @Override
    public int addProjectActivity(List<ProjectActivity> projectActivityList) throws QueryException {
        return addOrUpdateProjectActivityData(projectActivityList, Operation.ADD);
    }

    private int addOrUpdateProjectActivityData(List<ProjectActivity> projectActivityList, Operation add) throws QueryException {
        if (hibernateUtil == null) {
            return 0;
        }
        Session session = hibernateUtil.getCurrentSession();
        Transaction trans = null;

        int recordsSaved = 0;
        try {
            // begin save transaction
            trans = session.beginTransaction();

            ProjectActivityDAO dao = new ProjectActivityDAO();
            dao.setSession(session);

            for (ProjectActivity projectActivityListData : projectActivityList) {
                dao.saveOrUpdate(projectActivityListData);
                recordsSaved++;

            }
            // end transaction, commit to database
            trans.commit();
        } catch (Exception ex) {
            LOG.error("Error in addProjectActivity: " + ex.getMessage() + "\n" + ex.getStackTrace());
            ex.printStackTrace();
            // rollback transaction in case of errors
            if (trans != null) {
                trans.rollback();
            }
            throw new QueryException("Error encountered while adding addProjectActivity: " + ex.getMessage(), ex);
        } finally {
            hibernateUtil.closeCurrentSession();
        }

        return recordsSaved;
    }

    @Override
    public List<ProjectActivity> getProjectActivitiesByProjectId(Long projectId, int start, int numOfRows) throws QueryException {
        ProjectActivityDAO dao = new ProjectActivityDAO();
        List<ProjectActivity> projectActivities;
        if (hibernateUtil != null) {
            dao.setSession(hibernateUtil.getCurrentSession());
            projectActivities = (List<ProjectActivity>) dao.getByProjectId(projectId, start, numOfRows);
        } else {
            projectActivities = new ArrayList<ProjectActivity>();
        }

        return projectActivities;
    }

    @Override
    public Long countProjectActivitiesByProjectId(Long projectId) throws QueryException {
        ProjectActivityDAO dao = new ProjectActivityDAO();
        Long result = 0L;

        if (hibernateUtil != null) {
            dao.setSession(hibernateUtil.getCurrentSession());
            result = dao.countByProjectId(projectId);
        }

        return result;
    }

    @Override
    public void addToolConfiguration(ToolConfiguration toolConfig) throws QueryException {
        addOrUpdateToolConfiguration(toolConfig, Operation.ADD);
    }

    @Override
    public void updateToolConfiguration(ToolConfiguration toolConfig) throws QueryException {
        addOrUpdateToolConfiguration(toolConfig, Operation.UPDATE);
    }
    
    private void addOrUpdateToolConfiguration(ToolConfiguration toolConfig, Operation op) throws QueryException {
        Session session = hibernateUtil.getCurrentSession();
        Transaction trans = null;

        try {
            trans = session.beginTransaction();

            ToolConfigurationDAO dao = new ToolConfigurationDAO();
            dao.setSession(session);

//            Do specific add/update operations here
//            if(Operation.ADD.equals(op)) {
//                
//            } else if (Operation.UPDATE.equals(op)) {
//                
//            }
            dao.saveOrUpdate(toolConfig);

            trans.commit();
        } catch (Exception ex) {
            if (trans != null) {
                    trans.rollback();
            }
            throw new QueryException("Error encountered while saving ToolConfiguration: " + ex.getMessage(), ex);
        } finally {
            hibernateUtil.closeCurrentSession();
        }
    }
    
    @Override
    public void deleteToolConfiguration(ToolConfiguration toolConfig) throws QueryException {
        Session session = hibernateUtil.getCurrentSession();
        Transaction trans = null;

        try {
            trans = session.beginTransaction();

            ToolConfigurationDAO dao = new ToolConfigurationDAO();
            dao.setSession(session);

            dao.makeTransient(toolConfig);

            trans.commit();
        } catch (Exception ex) {
            if (trans != null) {
                    trans.rollback();
            }
            throw new QueryException("Error encountered while deleting ToolConfiguration: " + ex.getMessage(), ex);
        } finally {
            hibernateUtil.closeCurrentSession();
        }
    }

    @Override
    public List<ToolConfiguration> getListOfToolConfigurationsByToolId(Long toolId) throws QueryException {
        ToolConfigurationDAO dao = new ToolConfigurationDAO();
        dao.setSession(hibernateUtil.getCurrentSession());
        return dao.getListOfToolConfigurationsByToolId(toolId);
    }

    @Override
    public ToolConfiguration getToolConfigurationByToolIdAndConfigKey(Long toolId, String configKey)
        throws QueryException {
        
        ToolConfigurationDAO dao = new ToolConfigurationDAO();
        dao.setSession(hibernateUtil.getCurrentSession());
        return dao.getToolConfigurationByToolIdAndConfigKey(toolId, configKey);
    }
    
}
