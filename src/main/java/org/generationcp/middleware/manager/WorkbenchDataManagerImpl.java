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

import org.generationcp.middleware.dao.CropTypeDAO;
import org.generationcp.middleware.dao.IbdbUserMapDAO;
import org.generationcp.middleware.dao.PersonDAO;
import org.generationcp.middleware.dao.ProjectActivityDAO;
import org.generationcp.middleware.dao.ProjectBackupDAO;
import org.generationcp.middleware.dao.ProjectDAO;
import org.generationcp.middleware.dao.ProjectLocationMapDAO;
import org.generationcp.middleware.dao.ProjectMethodDAO;
import org.generationcp.middleware.dao.ProjectUserInfoDAO;
import org.generationcp.middleware.dao.ProjectUserMysqlAccountDAO;
import org.generationcp.middleware.dao.ProjectUserRoleDAO;
import org.generationcp.middleware.dao.RoleDAO;
import org.generationcp.middleware.dao.SecurityQuestionDAO;
import org.generationcp.middleware.dao.ToolConfigurationDAO;
import org.generationcp.middleware.dao.ToolDAO;
import org.generationcp.middleware.dao.UserDAO;
import org.generationcp.middleware.dao.UserInfoDAO;
import org.generationcp.middleware.dao.WorkbenchDatasetDAO;
import org.generationcp.middleware.dao.WorkbenchRuntimeDataDAO;
import org.generationcp.middleware.dao.WorkbenchSettingDAO;
import org.generationcp.middleware.dao.WorkflowTemplateDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.IbdbUserMap;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectActivity;
import org.generationcp.middleware.pojos.workbench.ProjectBackup;
import org.generationcp.middleware.pojos.workbench.ProjectLocationMap;
import org.generationcp.middleware.pojos.workbench.ProjectMethod;
import org.generationcp.middleware.pojos.workbench.ProjectUserInfo;
import org.generationcp.middleware.pojos.workbench.ProjectUserMysqlAccount;
import org.generationcp.middleware.pojos.workbench.ProjectUserRole;
import org.generationcp.middleware.pojos.workbench.Role;
import org.generationcp.middleware.pojos.workbench.SecurityQuestion;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.ToolConfiguration;
import org.generationcp.middleware.pojos.workbench.ToolType;
import org.generationcp.middleware.pojos.workbench.UserInfo;
import org.generationcp.middleware.pojos.workbench.WorkbenchDataset;
import org.generationcp.middleware.pojos.workbench.WorkbenchRuntimeData;
import org.generationcp.middleware.pojos.workbench.WorkbenchSetting;
import org.generationcp.middleware.pojos.workbench.WorkflowTemplate;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the WorkbenchDataManager interface. To instantiate this
 * class, a Hibernate Session must be passed to its constructor.
 */ 
public class WorkbenchDataManagerImpl implements WorkbenchDataManager {

    private static final Logger LOG = LoggerFactory.getLogger(WorkbenchDataManagerImpl.class);

    private HibernateSessionProvider sessionProvider;

    private CropTypeDAO cropTypeDao;
    private IbdbUserMapDAO ibdbUserMapDao;
    private PersonDAO personDao;
    private ProjectActivityDAO projectActivityDao;
    private ProjectDAO projectDao;
    private ProjectLocationMapDAO projectLocationMapDao;
    private ProjectMethodDAO projectMethodDao;
    private ProjectUserMysqlAccountDAO projectUserMysqlAccountDao;
    private ProjectUserRoleDAO projectUserRoleDao;
    private ProjectUserMysqlAccountDAO projectUserMysqlAccountDAO;
    private ProjectUserInfoDAO projectUserInfoDao;
    private RoleDAO roleDao; 
    private SecurityQuestionDAO securityQuestionDao;
    private ToolConfigurationDAO toolConfigurationDao;
    private ToolDAO toolDao;
    private UserDAO userDao;
  
    private UserInfoDAO userInfoDao;
  
    private WorkbenchDatasetDAO workbenchDatasetDao;
    private WorkbenchRuntimeDataDAO workbenchRuntimeDataDao;
    private WorkbenchSettingDAO workbenchSettingDao;
    private WorkflowTemplateDAO workflowTemplateDao;
    private ProjectBackupDAO projectBackupDao;

    public WorkbenchDataManagerImpl(HibernateSessionProvider sessionProvider) {
        this.sessionProvider = sessionProvider;
    }

    public Session getCurrentSession(){
        return sessionProvider.getSession();
    }
    
    private CropTypeDAO getCropTypeDao() {
        if (cropTypeDao == null){
            cropTypeDao = new CropTypeDAO();
        }
        cropTypeDao.setSession(getCurrentSession());
        return cropTypeDao;
    }

    private IbdbUserMapDAO getIbdbUserMapDao() {
        if (ibdbUserMapDao == null){
            ibdbUserMapDao = new IbdbUserMapDAO();
        }
        ibdbUserMapDao.setSession(getCurrentSession());
        return ibdbUserMapDao;
    }

    private PersonDAO getPersonDao() {
        if (personDao == null){
            personDao = new PersonDAO();
        }
        personDao.setSession(getCurrentSession());
        return personDao;
    }
    
    private ProjectActivityDAO getProjectActivityDao() {
        if (projectActivityDao == null){
            projectActivityDao = new ProjectActivityDAO();
        }
        projectActivityDao.setSession(getCurrentSession());
        return projectActivityDao;
    }
    
    private ProjectUserMysqlAccountDAO getProjectUserMysqlAccountDAO() {
        if (projectUserMysqlAccountDAO == null){
        	projectUserMysqlAccountDAO = new ProjectUserMysqlAccountDAO();
        }
        projectUserMysqlAccountDAO.setSession(getCurrentSession());
        return projectUserMysqlAccountDAO;
    }
    private ProjectDAO getProjectDao() {
        if (projectDao == null){
            projectDao = new ProjectDAO();
        }
        projectDao.setSession(getCurrentSession());
        return projectDao;
    }
    
    private ProjectLocationMapDAO getProjectLocationMapDao() {
        if (projectLocationMapDao == null){
            projectLocationMapDao = new ProjectLocationMapDAO();
        }
        projectLocationMapDao.setSession(getCurrentSession());
        return projectLocationMapDao;
    }
    
    private ProjectMethodDAO getProjectMethodDao() {
        if (projectMethodDao == null){
            projectMethodDao = new ProjectMethodDAO();
        }
        projectMethodDao.setSession(getCurrentSession());
        return projectMethodDao;
    }
    
    private ProjectUserMysqlAccountDAO getProjectUserMysqlAccountDao() {
        if (projectUserMysqlAccountDao == null){
            projectUserMysqlAccountDao = new ProjectUserMysqlAccountDAO();
        }
        projectUserMysqlAccountDao.setSession(getCurrentSession());
        return projectUserMysqlAccountDao;
    }
    
    
    
    
    
    @Override
    public ProjectUserInfoDAO getProjectUserInfoDao() {
        if (projectUserInfoDao == null){
        	projectUserInfoDao = new ProjectUserInfoDAO();
        }
        projectUserInfoDao.setSession(getCurrentSession());
        return projectUserInfoDao;
    }
    
    
    
    public void updateProjectsRolesForProject(Project project, List<ProjectUserRole> newRoles) throws MiddlewareQueryException
    {
        List<ProjectUserRole> deleteRoles = this.getProjectUserRolesByProject(project);
        
        // remove all previous roles
        for(ProjectUserRole projectUserRole : deleteRoles){
            this.deleteProjectUserRole(projectUserRole);
        }
        
        // add the new roles
        for(ProjectUserRole projectUserRole : newRoles){
            User user = new User();
            user.setUserid(projectUserRole.getUserId());
            this.addProjectUserRole(project, user, projectUserRole.getRole());
        }
    }
    
    private ProjectUserRoleDAO getProjectUserRoleDao() {
        if (projectUserRoleDao == null){
            projectUserRoleDao = new ProjectUserRoleDAO();
        }
        projectUserRoleDao.setSession(getCurrentSession());
        return projectUserRoleDao;
    }
    
    private RoleDAO getRoleDao() {
        if (roleDao == null){
            roleDao = new RoleDAO();
        }
        roleDao.setSession(getCurrentSession());
        return roleDao;
    }

    private SecurityQuestionDAO getSecurityQuestionDao() {
        if (securityQuestionDao == null){
            securityQuestionDao = new SecurityQuestionDAO();
        }
        securityQuestionDao.setSession(getCurrentSession());
        return securityQuestionDao;
    }

    private ToolConfigurationDAO getToolConfigurationDao() {
        if (toolConfigurationDao == null){
            toolConfigurationDao = new ToolConfigurationDAO();
        }
        toolConfigurationDao.setSession(getCurrentSession());
        return toolConfigurationDao;
    }

    @Override
    public ToolDAO getToolDao() {
        if (toolDao == null){
            toolDao = new ToolDAO();
        }
        toolDao.setSession(getCurrentSession());
        return toolDao;
    }

    private UserDAO getUserDao() {
        if (userDao == null){
            userDao = new UserDAO();
        }
        userDao.setSession(getCurrentSession());
        return userDao;
    }
    
    private UserInfoDAO getUserInfoDao() {
        if (userInfoDao == null){
        	userInfoDao = new UserInfoDAO();
        }
        userInfoDao.setSession(getCurrentSession());
        return userInfoDao;
    }

    private WorkbenchDatasetDAO getWorkbenchDatasetDao() {
        if (workbenchDatasetDao == null){
            workbenchDatasetDao = new WorkbenchDatasetDAO();
        }
        workbenchDatasetDao.setSession(getCurrentSession());
        return workbenchDatasetDao;
    }

    private WorkbenchRuntimeDataDAO getWorkbenchRuntimeDataDao() {
        if (workbenchRuntimeDataDao == null){
            workbenchRuntimeDataDao = new WorkbenchRuntimeDataDAO();
        }
        workbenchRuntimeDataDao.setSession(getCurrentSession());
        return workbenchRuntimeDataDao;
    }
    
    private WorkbenchSettingDAO getWorkbenchSettingDao() {
        if (workbenchSettingDao == null){
            workbenchSettingDao = new WorkbenchSettingDAO();
        }
        workbenchSettingDao.setSession(getCurrentSession());
        return workbenchSettingDao;
    }

    private WorkflowTemplateDAO getWorkflowTemplateDao() {
        if (workflowTemplateDao == null){
            workflowTemplateDao = new WorkflowTemplateDAO();
        }
        workflowTemplateDao.setSession(getCurrentSession());
        return workflowTemplateDao;
    }

    private ProjectBackupDAO getProjectBackupDao() {
        if (projectBackupDao == null){
            projectBackupDao = new ProjectBackupDAO();
        }
        projectBackupDao.setSession(getCurrentSession());
        return projectBackupDao;
    }

    private void rollbackTransaction(Transaction trans){
        if (trans != null){
            trans.rollback();
        }
    }
    
    private void logAndThrowException(String message, Exception e) throws MiddlewareQueryException{
        LOG.error(e.getMessage(), e);
        throw new MiddlewareQueryException(message + e.getMessage(), e);
    }
    
    private void logAndThrowException(String message) throws MiddlewareQueryException{
        LOG.error(message);
        throw new MiddlewareQueryException(message);
    }
    

    @Override
    public List<Project> getProjects() throws MiddlewareQueryException {
        return getProjectDao().getAll();
    }

    @Override
    public List<Project> getProjects(int start, int numOfRows) throws MiddlewareQueryException {
        return getProjectDao().getAll(start, numOfRows);
    }
    
    @Override
    public List<Project> getProjectsByUser(User user) throws MiddlewareQueryException {
        return getProjectUserRoleDao().getProjectsByUser(user);
    }

    @Override
    public Project saveOrUpdateProject(Project project) throws MiddlewareQueryException {
        Transaction trans = null;
        Session session = getCurrentSession();
        
        try {
            trans = session.beginTransaction();
            getProjectDao().merge(project);

            // TODO: copy the workbench template created by the project into the
            // project_workflow_step table

            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Cannot save Project: WorkbenchDataManager.saveOrUpdateProject(project=" + project + "): "
                    + e.getMessage(), e);
        }

        return project;
    }
    
    @Override
    public ProjectUserInfo saveOrUpdateProjectUserInfo(ProjectUserInfo projectUserInfo) throws MiddlewareQueryException {
        Transaction trans = null;
        Session session = getCurrentSession();
        
        try {
            trans = session.beginTransaction();
            getProjectUserInfoDao().merge(projectUserInfo);

            // TODO: copy the workbench template created by the project into the
            // project_workflow_step table

            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Cannot save ProjectUserInfo: WorkbenchDataManager.saveOrUpdateProjectUserInfo(project=" + projectUserInfo + "): "
                    + e.getMessage(), e);
        }

        return projectUserInfo;
    }
    
    public Project addProject(Project project) throws MiddlewareQueryException {
        Session session = getCurrentSession();
        Transaction trans = null;

        try {
            trans = session.beginTransaction();
            getProjectDao().save(project);
            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Cannot save Project: WorkbenchDataManager.addProject(project=" + project + "): "
                + e.getMessage(), e);
        }
        
        return project;
    }
    
    public Project mergeProject(Project project) throws MiddlewareQueryException {
        Session session = getCurrentSession();
        Transaction trans = null;

        try {
            trans = session.beginTransaction();
            getProjectDao().merge(project);
            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Cannot save Project: WorkbenchDataManager.updateProject(project=" + project + "): "
                + e.getMessage(), e);
        }
        
        return project;
    }
    
    @Override
    public void deleteProjectDependencies(Project project) throws MiddlewareQueryException {
    	
    	try {
    		Long projectId = project.getProjectId();
            List<ProjectActivity> projectActivities = getProjectActivitiesByProjectId(projectId, 0,
                    (int) countProjectActivitiesByProjectId(projectId));
            for (ProjectActivity projectActivity : projectActivities) {
               deleteProjectActivity(projectActivity);
            } 

            List<ProjectMethod> projectMethods = getProjectMethodByProject(project, 0,
                    (int) countMethodIdsByProjectId(projectId));
            for (ProjectMethod projectMethod : projectMethods) {
               deleteProjectMethod(projectMethod);
            }

            List<ProjectUserRole> projectUsers = getProjectUserRolesByProject(project);
            for (ProjectUserRole projectUser : projectUsers) {
                deleteProjectUserRole(projectUser);
            }
           
 	    List<ProjectUserMysqlAccount> mysqlaccounts = getProjectUserMysqlAccountDAO().getByProjectId(project.getProjectId().intValue());
            if(mysqlaccounts != null)
            	for (ProjectUserMysqlAccount mysqlaccount : mysqlaccounts) {
            		deleteProjectUserMysqlAccount(mysqlaccount);
                }
           
            		
           
            
            List<WorkbenchDataset> datasets = getWorkbenchDatasetByProjectId(projectId, 0,
                    (int) countWorkbenchDatasetByProjectId(projectId));
            for (WorkbenchDataset dataset : datasets) {
                deleteWorkbenchDataset(dataset);
            }
            
            List<ProjectLocationMap> projectLocationMaps = getProjectLocationMapByProjectId(projectId, 0,
                    (int) countLocationIdsByProjectId(projectId));
            //manager.deleteProjectLocationMap(projectLocationMaps);
            for (ProjectLocationMap projectLocationMap : projectLocationMaps) {
                deleteProjectLocationMap(projectLocationMap);
            } 
            
            List<ProjectUserInfo> projectUserInfos = getByProjectId(projectId.intValue());
            for (ProjectUserInfo projectUserInfo : projectUserInfos) {
            	deleteProjectUserInfoDao(projectUserInfo);
            } 
            
            
            List<ProjectBackup> projectBackups = getProjectBackups(project);
            for (ProjectBackup projectBackup : projectBackups) {
            	deleteProjectBackup(projectBackup);
            } 
            
            List<IbdbUserMap> ibdbUserMaps = getIbdbUserMapsByProjectId(project.getProjectId());
            for (IbdbUserMap ibdbUserMap : ibdbUserMaps) {
            	deleteIbdbProjectBackup(ibdbUserMap);
            } 
            //deleteProject(project);
    	}catch (Exception e) {
              
                logAndThrowException("Cannot delete Project Dependencies: WorkbenchDataManager.deleteProjectDependencies(project=" + project + "): "
                        + e.getMessage(), e);
            }
    }
    public void deleteIbdbProjectBackup(IbdbUserMap ibdbUserMap) throws MiddlewareQueryException
    {
    	Session session = getCurrentSession();
        Transaction trans = null;
        try{
        	trans = session.beginTransaction();
        	getIbdbUserMapDao().makeTransient(ibdbUserMap);
            trans.commit();
            
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Cannot delete Project: WorkbenchDataManager.deleteIbdbProjectBackup(ibdbUserMap=" + ibdbUserMap + "): "
                    + e.getMessage(), e);
        }
    }
    public List<IbdbUserMap> getIbdbUserMapsByProjectId(Long projectId) throws MiddlewareQueryException
    {
    	return getIbdbUserMapDao().getIbdbUserMapByID(projectId);
    }
    
    public void deleteProjectUserInfoDao(ProjectUserInfo projectUserInfo)  throws MiddlewareQueryException
    {
    	Session session = getCurrentSession();
        Transaction trans = null;
        try{
        	trans = session.beginTransaction();
        	getProjectUserInfoDao().makeTransient(projectUserInfo);
            trans.commit();
            
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Cannot delete Project: WorkbenchDataManager.deleteProjectUserInfoDao(projectUserInfo=" + projectUserInfo + "): "
                    + e.getMessage(), e);
        }
    }
    public void deleteProjectUserMysqlAccount(ProjectUserMysqlAccount mysqlaccount) throws MiddlewareQueryException
    {
    	Session session = getCurrentSession();
        Transaction trans = null;
        try{
        	trans = session.beginTransaction();
        	getProjectUserMysqlAccountDAO().makeTransient(mysqlaccount);
            trans.commit();
            
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Cannot delete Project: WorkbenchDataManager.deleteProject(mysqlaccount=" + mysqlaccount + "): "
                    + e.getMessage(), e);
        }
    }
    @Override
    public void deleteProject(Project project) throws MiddlewareQueryException {
    	
        try{
        	getProjectDao().deleteProject(project.getProjectName());
           
            
        } catch (Exception e) {
           
            logAndThrowException("Cannot delete Project: WorkbenchDataManager.deleteProject(project=" + project + "): "
                    + e.getMessage(), e);
            
            
        }
    }

    @Override
    public List<WorkflowTemplate> getWorkflowTemplates() throws MiddlewareQueryException {
        return getWorkflowTemplateDao().getAll();
    }
    
    @Override
    public List<WorkflowTemplate> getWorkflowTemplateByName(String name) throws MiddlewareQueryException {
        return getWorkflowTemplateDao().getByName(name);
    }

    @Override
    public List<WorkflowTemplate> getWorkflowTemplates(int start, int numOfRows) throws MiddlewareQueryException {
        return getWorkflowTemplateDao().getAll(start, numOfRows);
    }
    
    @Override
    public List<Tool> getAllTools() throws MiddlewareQueryException {
        return getToolDao().getAll();
    }

    @Override
    public Tool getToolWithName(String toolId) throws MiddlewareQueryException {
        return getToolDao().getByToolName(toolId);
    }

    @Override
    public List<Tool> getToolsWithType(ToolType toolType) throws MiddlewareQueryException {
        return getToolDao().getToolsByToolType(toolType);
    }

    @Override
    public boolean isValidUserLogin(String username, String password) throws MiddlewareQueryException {
        if (getUserDao().getByUsernameAndPassword(username, password) != null) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isPersonExists(String firstName, String lastName) throws MiddlewareQueryException {
        return getPersonDao().isPersonExists(firstName, lastName);
    }

    @Override
    public boolean isUsernameExists(String userName) throws MiddlewareQueryException {
        return getUserDao().isUsernameExists(userName);
    }

    @Override
    public Integer addPerson(Person person) throws MiddlewareQueryException {
        Transaction trans = null;
        Session session = getCurrentSession();
        
        Integer idPersonSaved = null;
        try {
            // begin save transaction
            trans = session.beginTransaction();

            SQLQuery q = session.createSQLQuery("SELECT MAX(personid) FROM persons");
            Integer personId = (Integer) q.uniqueResult();

            if (personId == null || personId.intValue() < 0) {
                person.setId(1);
            } else {
                person.setId(personId + 1);
            }

            Person recordSaved = getPersonDao().saveOrUpdate(person);
            idPersonSaved = recordSaved.getId();

            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error encountered while saving Person: WorkbenchDataManager.addPerson(person=" + person
                    + "): " + e.getMessage(), e);
        }
        return idPersonSaved;
    }

    @Override
    public Integer addUser(User user) throws MiddlewareQueryException {
        Transaction trans = null;
        Session session = getCurrentSession();
        
        Integer idUserSaved = null;
        try {
            // begin save transaction
            trans = session.beginTransaction();

            SQLQuery q = session.createSQLQuery("SELECT MAX(userid) FROM users");
            Integer userId = (Integer) q.uniqueResult();

            if (userId == null || userId.intValue() < 0) {
                user.setUserid(1);
            } else {
                user.setUserid(userId + 1);
            }

            User recordSaved = getUserDao().saveOrUpdate(user);
            idUserSaved = recordSaved.getUserid();

            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error encountered while saving User: WorkbenchDataManager.addUser(user=" + user + "): "
                    + e.getMessage(), e);
        }
        
        return idUserSaved;

    }

    public Project getProjectById(Long projectId) throws MiddlewareQueryException {
        return getProjectDao().getById(projectId);
    }
    
    public Project getProjectByName(String projectName) throws MiddlewareQueryException{
        return getProjectDao().getByName(projectName);
    }


    public Integer addWorkbenchDataset(WorkbenchDataset dataset) throws MiddlewareQueryException {
        Session session = getCurrentSession();
        Transaction trans = null;
        
        Integer workbenchDatasetSaved = null;
        try {
            // begin save transaction
            trans = session.beginTransaction();
            WorkbenchDataset datasetSaved = getWorkbenchDatasetDao().saveOrUpdate(dataset);
            workbenchDatasetSaved = datasetSaved.getDatasetId().intValue();
            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException(
                    "Error encountered while saving workbench dataset: WorkbenchDataManager.addWorkbenchDataset(dataset=" + dataset + "): "
                            + e.getMessage(), e);
        }

        return workbenchDatasetSaved;
    }

    @Override
    public WorkbenchDataset getWorkbenchDatasetById(Long datasetId) throws MiddlewareQueryException {
        return getWorkbenchDatasetDao().getById(datasetId);
    }

    @Override
    public void deleteWorkbenchDataset(WorkbenchDataset dataset) throws MiddlewareQueryException {
        Transaction trans = null;
        Session session = getCurrentSession();
        
        try {
            trans = session.beginTransaction();
            getWorkbenchDatasetDao().makeTransient(dataset);
            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Cannot delete WorkbenchDataset: WorkbenchDataManager.deleteWorkbenchDataset(dataset="
                    + dataset + "):  " + e.getMessage(), e);
        }
    }

    @Override
    public List<User> getAllUsers() throws MiddlewareQueryException {
        return getUserDao().getAll();
    }
    
    @Override
    public List<User> getAllUsersSorted() throws MiddlewareQueryException {
        return getUserDao().getAllUsersSorted();
    }

    public long countAllUsers() throws MiddlewareQueryException {
        return getUserDao().countAll();
    }

    @Override
    public User getUserById(int id) throws MiddlewareQueryException {
        return getUserDao().getById(id, false);
    }

    @Override
    public List<User> getUserByName(String name, int start, int numOfRows, Operation op) throws MiddlewareQueryException {
        UserDAO dao = getUserDao();
        List<User> users = new ArrayList<User>();
        if (op == Operation.EQUAL) {
            users = dao.getByNameUsingEqual(name, start, numOfRows);
        } else if (op == Operation.LIKE) {
            users = dao.getByNameUsingLike(name, start, numOfRows);
        }
        return users;
    }

    @Override
    public void deleteUser(User user) throws MiddlewareQueryException {
        Transaction trans = null;
        Session session = getCurrentSession();
        
        try {
            trans = session.beginTransaction();
            getUserDao().makeTransient(user);
            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error encountered while deleting User: WorkbenchDataManager.deleteUser(user=" + user
                    + "):  " + e.getMessage(), e);
        }
    }

    @Override
    public List<Person> getAllPersons() throws MiddlewareQueryException {
        return getPersonDao().getAll();
    }

    public long countAllPersons() throws MiddlewareQueryException {
        return getPersonDao().countAll();
    }

    @Override
    public Person getPersonById(int id) throws MiddlewareQueryException {
        return getPersonDao().getById(id, false);
    }

    @Override
    public void deletePerson(Person person) throws MiddlewareQueryException {
        Transaction trans = null;
        Session session = getCurrentSession();
        
        try {
            trans = session.beginTransaction();
            getPersonDao().makeTransient(person);
            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error encountered while deleting Person: WorkbenchDataManager.deletePerson(person="
                    + person + "): " + e.getMessage(), e);
        }
    }

    @Override
    public Project getLastOpenedProject(Integer userId) throws MiddlewareQueryException {
        return getProjectDao().getLastOpenedProject(userId);
    }

    @Override
    public List<WorkbenchDataset> getWorkbenchDatasetByProjectId(Long projectId, int start, int numOfRows) throws MiddlewareQueryException {
        return getWorkbenchDatasetDao().getByProjectId(projectId, start, numOfRows);
    }

    @Override
    public long countWorkbenchDatasetByProjectId(Long projectId) throws MiddlewareQueryException {
        return getWorkbenchDatasetDao().countByProjectId(projectId);
    }

    @Override
    public List<WorkbenchDataset> getWorkbenchDatasetByName(String name, Operation op, int start, int numOfRows)
            throws MiddlewareQueryException {
        return getWorkbenchDatasetDao().getByName(name, op, start, numOfRows);
    }

    @Override
    public long countWorkbenchDatasetByName(String name, Operation op) throws MiddlewareQueryException {
        return getWorkbenchDatasetDao().countByName(name, op);
    }

    @Override
    public List<Long> getLocationIdsByProjectId(Long projectId, int start, int numOfRows) throws MiddlewareQueryException {
        return getProjectLocationMapDao().getLocationIdsByProjectId(projectId, start, numOfRows);
    }

    @Override
    public long countLocationIdsByProjectId(Long projectId) throws MiddlewareQueryException {
        return getProjectLocationMapDao().countLocationIdsByProjectId(projectId);
    }

    @Override
    public List<Integer> getMethodIdsByProjectId(Long projectId, int start, int numOfRows) throws MiddlewareQueryException {
        return getProjectMethodDao().getByProjectId(projectId, start, numOfRows);
    }

    @Override
    public long countMethodIdsByProjectId(Long projectId) throws MiddlewareQueryException {
        return getProjectMethodDao().countByProjectId(projectId);
    }

    @Override
    public Integer addProjectUserRole(Project project, User user, Role role) throws MiddlewareQueryException {
        ProjectUserRole projectUserRole = new ProjectUserRole();
        projectUserRole.setProject(project);
        projectUserRole.setUserId(user.getUserid());
        projectUserRole.setRole(role);
        return addProjectUserRole(projectUserRole);
    }

    @Override
    public Integer addProjectUserRole(ProjectUserRole projectUserRole) throws MiddlewareQueryException {
        Session session = getCurrentSession();
        Transaction trans = null;

        Integer idSaved = null;
        try {
            // begin save transaction
            trans = session.beginTransaction();
            ProjectUserRole recordSaved = getProjectUserRoleDao().saveOrUpdate(projectUserRole);
            idSaved = recordSaved.getProjectUserId();
            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException(
                    "Error encountered while saving ProjectUserRole: WorkbenchDataManager.addProjectUserRole(projectUserRole=" + projectUserRole + "): "
                            + e.getMessage(), e);
        }

        return idSaved;
    }

    @Override
    public List<Integer> addProjectUserRole(List<ProjectUserRole> projectUserRoles) throws MiddlewareQueryException {
        Session session = getCurrentSession();
        if (session == null) {
            return new ArrayList<Integer>();
        }
        Transaction trans = null;

        List<Integer> idsSaved = new ArrayList<Integer>();
        try {
            // begin save transaction
            trans = session.beginTransaction();
            ProjectUserRoleDAO dao = getProjectUserRoleDao();

            for (ProjectUserRole projectUser : projectUserRoles) {
                ProjectUserRole recordSaved = dao.saveOrUpdate(projectUser);
                idsSaved.add(recordSaved.getProjectUserId());
            }

            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException(
                    "Error encountered while saving ProjectUserRoles: WorkbenchDataManager.addProjectUserRoles(projectUserRoles=" + projectUserRoles
                            + "): " + e.getMessage(), e);
        }

        return idsSaved;
    }

    @Override
    public ProjectUserRole getProjectUserRoleById(Integer id) throws MiddlewareQueryException {
        return getProjectUserRoleDao().getById(id);
    }
    
    @Override
    public List<ProjectUserRole> getProjectUserRolesByProject(Project project) throws MiddlewareQueryException{
        return getProjectUserRoleDao().getByProject(project);
    }
    
    @Override
    public void deleteProjectUserRole(ProjectUserRole projectUserRole) throws MiddlewareQueryException {
        Session session = getCurrentSession();
        if (session == null) {
            return;
        }

        Transaction trans = null;

        try {
            trans = session.beginTransaction();
            getProjectUserRoleDao().makeTransient(projectUserRole);
            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException(
                    "Error encountered while deleting ProjectUser: WorkbenchDataManager.deleteProjectUser(projectUser=" + projectUserRole
                            + "): " + e.getMessage(), e);
        }
    }

    @Override
    public List<User> getUsersByProjectId(Long projectId) throws MiddlewareQueryException {
        return getProjectUserRoleDao().getUsersByProjectId(projectId);
    }

    @Override
    public long countUsersByProjectId(Long projectId) throws MiddlewareQueryException {
        return getProjectUserRoleDao().countUsersByProjectId(projectId);
    }

    @Override
    public List<CropType> getInstalledCentralCrops() throws MiddlewareQueryException {
        return getCropTypeDao().getAll();

    }

    @Override
    public CropType getCropTypeByName(String cropName) throws MiddlewareQueryException {
        return getCropTypeDao().getByName(cropName);
    }

    @Override
    public String addCropType(CropType cropType) throws MiddlewareQueryException {
        Session session = getCurrentSession();
        if (session == null) {
            return null;
        }

        CropTypeDAO dao = getCropTypeDao();
        if (getCropTypeDao().getByName(cropType.getCropName()) != null) {
            logAndThrowException("Crop type already exists.");
        }

        Transaction trans = null;
        String idSaved = null;
        try {
            // begin save transaction
            trans = session.beginTransaction();
            CropType recordSaved = dao.saveOrUpdate(cropType);
            idSaved = recordSaved.getCropName();
            // end transaction, commit to database
            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error encountered while adding crop type: WorkbenchDataManager.addCropType(cropType="
                    + cropType + "): " + e.getMessage(), e);
        }

        return idSaved;
    }

    @Override
    public Integer addProjectLocationMap(ProjectLocationMap projectLocationMap) throws MiddlewareQueryException {
        List<ProjectLocationMap> list = new ArrayList<ProjectLocationMap>();
        list.add(projectLocationMap);
        List<Integer> ids = addProjectLocationMap(list);
        return ids.size() > 0 ? ids.get(0) : null;
    }

    public List<Integer> addProjectLocationMap(List<ProjectLocationMap> projectLocationMapDatas) throws MiddlewareQueryException {
        return addOrUpdateProjectLocationMapData(projectLocationMapDatas, Operation.ADD);
    }

    private List<Integer> addOrUpdateProjectLocationMapData(List<ProjectLocationMap> projectLocationMapDatas, Operation operation)
            throws MiddlewareQueryException {
        Session session = getCurrentSession();
        if (session == null) {
            return new ArrayList<Integer>();
        }

        Transaction trans = null;
        List<Integer> idsSaved = new ArrayList<Integer>();
        try {
            // begin save transaction
            trans = session.beginTransaction();

            ProjectLocationMapDAO dao = getProjectLocationMapDao();

            for (ProjectLocationMap projectLocationMapData : projectLocationMapDatas) {
                ProjectLocationMap recordSaved = dao.saveOrUpdate(projectLocationMapData);
                idsSaved.add(recordSaved.getId());
            }

            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException(
                    "Error encountered while adding ProjectLocation: WorkbenchDataManager.addOrUpdateProjectLocationMapData(projectLocationMapDatas="
                            + projectLocationMapDatas + ", operation=" + operation + "): " + e.getMessage(), e);
        }

        return idsSaved;
    }
    

  
    public List<ProjectUserInfo> getByProjectId(Integer projectId)
            throws MiddlewareQueryException {
        return getProjectUserInfoDao().getByProjectId(projectId);

    }

    @Override
    public List<ProjectLocationMap> getProjectLocationMapByProjectId(Long projectId, int start, int numOfRows)
            throws MiddlewareQueryException {
        return getProjectLocationMapDao().getByProjectId(projectId, start, numOfRows);

    }

    @Override
    public void deleteProjectLocationMap(ProjectLocationMap projectLocationMap) throws MiddlewareQueryException {
        Session session = getCurrentSession();
        Transaction trans = null;

        try {
            trans = session.beginTransaction();
            getProjectLocationMapDao().makeTransient(projectLocationMap);
            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException(
                    "Error encountered while deleting ProjectLocationMap: WorkbenchDataManager.deleteProjectLocationMap(projectLocationMap="
                            + projectLocationMap + "): " + e.getMessage(), e);
        }
    }

    @Override
    public Integer addProjectMethod(ProjectMethod projectMethod) throws MiddlewareQueryException {
        List<ProjectMethod> list = new ArrayList<ProjectMethod>();
        list.add(projectMethod);
        List<Integer> ids = addProjectMethod(list);
        return ids.size() > 0 ? ids.get(0) : null;
    }

    @Override
    public List<Integer> addProjectMethod(List<ProjectMethod> projectMethodList) throws MiddlewareQueryException {
        return addOrUpdateProjectMethodData(projectMethodList, Operation.ADD);
    }

    private List<Integer> addOrUpdateProjectMethodData(List<ProjectMethod> projectMethodList, Operation operation) throws MiddlewareQueryException {
        Session session = getCurrentSession();
        if (session == null) {
            return new ArrayList<Integer>();
        }

        Transaction trans = null;

        List<Integer> idsSaved = new ArrayList<Integer>();
        try {
            // begin save transaction
            trans = session.beginTransaction();

            ProjectMethodDAO dao = getProjectMethodDao();

            for (ProjectMethod projectMethodListData : projectMethodList) {
                ProjectMethod recordSaved = dao.saveOrUpdate(projectMethodListData);
                idsSaved.add(recordSaved.getProjectMethodId());
            }
            // end transaction, commit to database
            trans.commit();

            // remove ProjectMethod data from session cache
            for (ProjectMethod projectMethodListData : projectMethodList) {
                session.evict(projectMethodListData);
            }
            session.evict(projectMethodList);

        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException(
                    "Error encountered while adding ProjectMethod: WorkbenchDataManager.addOrUpdateProjectMethodData(projectMethodList="
                            + projectMethodList + ", operation=" + operation + "): " + e.getMessage(), e);
        }

        return idsSaved;
    }

    @Override
    public List<ProjectMethod> getProjectMethodByProject(Project project, int start, int numOfRows) throws MiddlewareQueryException {
        return getProjectMethodDao().getProjectMethodByProject(project, start, numOfRows);

    }

    @Override
    public void deleteProjectMethod(ProjectMethod projectMethod) throws MiddlewareQueryException {
        Session session = getCurrentSession();
        Transaction trans = null;

        try {
            trans = session.beginTransaction();
            getProjectMethodDao().makeTransient(projectMethod);
            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error encountered while deleting ProjectMethod: WorkbenchDataManager.deleteProjectMethod(projectMethod="
                            + projectMethod + "): " + e.getMessage(), e);
        }
    }

    @Override
    public Integer addProjectActivity(ProjectActivity projectActivity) throws MiddlewareQueryException {
        List<ProjectActivity> list = new ArrayList<ProjectActivity>();
        list.add(projectActivity);
        
        List<Integer> ids = addProjectActivity(list);
        
        return ids.size() > 0 ? ids.get(0) : null;
    }

    @Override
    public List<Integer> addProjectActivity(List<ProjectActivity> projectActivityList) throws MiddlewareQueryException {
        
        return addOrUpdateProjectActivityData(projectActivityList, Operation.ADD);
    }
    
    /**
     * drop the projects local database. 
     * 
     * Drops the database of the given project. 
     *
     * @param project - the project to delete the database
     * @throws MiddlewareQueryException
     */
    public void dropLocalDatabase(Project project) throws MiddlewareQueryException
    {
    	getProjectDao().deleteDatabase(project.getLocalDbName());
    }

    private List<Integer> addOrUpdateProjectActivityData(List<ProjectActivity> projectActivityList, Operation operation)
            throws MiddlewareQueryException {
        
        Session session = getCurrentSession();
        if (session == null) {
            return new ArrayList<Integer>();
        }

        Transaction trans = null;
        
        List<Integer> idsSaved = new ArrayList<Integer>();
        try {
        
            trans = session.beginTransaction();
            ProjectActivityDAO dao = getProjectActivityDao();
           
            for (ProjectActivity projectActivityListData : projectActivityList) {
                ProjectActivity recordSaved = dao.save(projectActivityListData);
                idsSaved.add(recordSaved.getProjectActivityId());
            }
            trans.commit();
            
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException(
                    "Error encountered while adding addProjectActivity: WorkbenchDataManager.addOrUpdateProjectActivityData(projectActivityList="
                            + projectActivityList + ", operation=" + operation + "): " + e.getMessage(), e);
        }

        return idsSaved;
    }

    @Override
    public List<ProjectActivity> getProjectActivitiesByProjectId(Long projectId, int start, int numOfRows) throws MiddlewareQueryException {
        return getProjectActivityDao().getByProjectId(projectId, start, numOfRows);
    }

    @Override
    public void deleteProjectActivity(ProjectActivity projectActivity) throws MiddlewareQueryException {
        Session session = getCurrentSession();
        Transaction trans = null;

        try {
            trans = session.beginTransaction();
            getProjectActivityDao().makeTransient(projectActivity);
            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException(
                    "Error encountered while deleting ProjectActivity: WorkbenchDataManager.deleteProjectActivity(projectActivity="
                            + projectActivity + "): " + e.getMessage(), e);
        }
    }

    @Override
    public long countProjectActivitiesByProjectId(Long projectId) throws MiddlewareQueryException {
        return getProjectActivityDao().countByProjectId(projectId);
    }

    @Override
    public Integer addToolConfiguration(ToolConfiguration toolConfig) throws MiddlewareQueryException {
        return addOrUpdateToolConfiguration(toolConfig, Operation.ADD);
    }

    @Override
    public Integer updateToolConfiguration(ToolConfiguration toolConfig) throws MiddlewareQueryException {
        return addOrUpdateToolConfiguration(toolConfig, Operation.UPDATE);
    }

    private Integer addOrUpdateToolConfiguration(ToolConfiguration toolConfig, Operation op) throws MiddlewareQueryException {
        Session session = getCurrentSession();
        Transaction trans = null;

        Integer idSaved = null;
        try {
            trans = session.beginTransaction();
            //            Do specific add/update operations here
            //            if(Operation.ADD.equals(op)) {
            //                
            //            } else if (Operation.UPDATE.equals(op)) {
            //                
            //            }
            ToolConfiguration recordSaved = getToolConfigurationDao().saveOrUpdate(toolConfig);
            idSaved = recordSaved.getConfigId();

            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException(
                    "Error encountered while saving ToolConfiguration: WorkbenchDataManager.addOrUpdateToolConfiguration(toolConfig="
                            + toolConfig + ", operation=" + op + "): " + e.getMessage(), e);
        }
        return idSaved;
    }

    @Override
    public void deleteToolConfiguration(ToolConfiguration toolConfig) throws MiddlewareQueryException {
        Session session = getCurrentSession();
        Transaction trans = null;

        try {
            trans = session.beginTransaction();
            getToolConfigurationDao().makeTransient(toolConfig);
            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException(
                    "Error encountered while deleting ToolConfiguration: WorkbenchDataManager.deleteToolConfiguration(toolConfig="
                            + toolConfig + "): " + e.getMessage(), e);
        }
    }

    @Override
    public List<ToolConfiguration> getListOfToolConfigurationsByToolId(Long toolId) throws MiddlewareQueryException {
        return getToolConfigurationDao().getListOfToolConfigurationsByToolId(toolId);
    }

    @Override
    public ToolConfiguration getToolConfigurationByToolIdAndConfigKey(Long toolId, String configKey) throws MiddlewareQueryException {
        return getToolConfigurationDao().getToolConfigurationByToolIdAndConfigKey(toolId, configKey);
    }

    @Override
    public Integer addIbdbUserMap(IbdbUserMap userMap) throws MiddlewareQueryException {
        Session session = getCurrentSession();
        Transaction trans = null;

        try {
            trans = session.beginTransaction();
            getIbdbUserMapDao().saveOrUpdate(userMap);
            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error encountered while adding IbdbUserMap: WorkbenchDataManager.addIbdbUserMap(userMap="
                    + userMap + "): " + e.getMessage(), e);
        }

        return userMap.getIbdbUserId();
    }

    @Override
    public Integer getLocalIbdbUserId(Integer workbenchUserId, Long projectId) throws MiddlewareQueryException {
        Session session = getCurrentSession();
        Transaction trans = null;

        Integer ibdbUserId = null;
        try {
            trans = session.beginTransaction();
            ibdbUserId = getIbdbUserMapDao().getLocalIbdbUserId(workbenchUserId, projectId);
            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException(
                    "Error encountered while retrieving Local IBDB user id: WorkbenchDataManager.getLocalIbdbUserId(workbenchUserId="
                            + workbenchUserId + ", projectId=" + projectId + "): " + e.getMessage(), e);
        }

        return ibdbUserId;
    }

    @Override
    public Integer updateWorkbenchRuntimeData(WorkbenchRuntimeData workbenchRuntimeData) throws MiddlewareQueryException {
        Session session = getCurrentSession();
        Transaction trans = null;

        try {
            trans = session.beginTransaction();
            getWorkbenchRuntimeDataDao().saveOrUpdate(workbenchRuntimeData);
            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException(
                    "Error encountered while adding IbdbUserMap: WorkbenchDataManager.updateWorkbenchRuntimeData(workbenchRuntimeData="
                            + workbenchRuntimeData + "): " + e.getMessage(), e);
        }

        return workbenchRuntimeData.getId();
    }

    @Override
    public WorkbenchRuntimeData getWorkbenchRuntimeData() throws MiddlewareQueryException {
        List<WorkbenchRuntimeData> list = getWorkbenchRuntimeDataDao().getAll(0, 1);
        return list.size() > 0 ? list.get(0) : null;
    }

    @Override
    public Role getRoleById(Integer id) throws MiddlewareQueryException {
        return getRoleDao().getById(id);
    }

    @Override
    public Role getRoleByNameAndWorkflowTemplate(String name, WorkflowTemplate workflowTemplate) throws MiddlewareQueryException {
        return getRoleDao().getByNameAndWorkflowTemplate(name, workflowTemplate);
    }


    @Override
    public List<Role> getRolesByWorkflowTemplate(WorkflowTemplate workflowTemplate) throws MiddlewareQueryException {
        return getRoleDao().getByWorkflowTemplate(workflowTemplate);
    }

    @Override
    public WorkflowTemplate getWorkflowTemplateByRole(Role role) throws MiddlewareQueryException {
        return role.getWorkflowTemplate();
    }

    @Override
    public List<Role> getRolesByProjectAndUser(Project project, User user) throws MiddlewareQueryException {
        return getProjectUserRoleDao().getRolesByProjectAndUser(project, user);
    }

    @Override
    public List<Role> getAllRoles() throws MiddlewareQueryException {
        return getRoleDao().getAll();
    }

    @Override
    public List<Role> getAllRolesDesc() throws MiddlewareQueryException {
        return getRoleDao().getAllRolesDesc();
    }

    @Override
    public List<Role> getAllRolesOrderedByLabel() throws MiddlewareQueryException {
        try {
            return getRoleDao().getAllRolesOrderedByLabel();
        } catch (Exception e) {
            logAndThrowException("Error encountered while getting all roles (sorted): " + e.getMessage(), e);
        }
        return null;
    }
    
    @Override
    public WorkbenchSetting getWorkbenchSetting() throws MiddlewareQueryException {
        try {
            List<WorkbenchSetting> list = getWorkbenchSettingDao().getAll();
            return list.isEmpty() ? null : list.get(0);
        } catch (Exception e) {
            logAndThrowException("Error encountered while getting workbench setting: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void addSecurityQuestion(SecurityQuestion securityQuestion) throws MiddlewareQueryException {
        Session session = getCurrentSession();
        Transaction trans = null;

        try {
            trans = session.beginTransaction();
            getSecurityQuestionDao().saveOrUpdate(securityQuestion);
            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error encountered while adding Security Question: " +
                    "WorkbenchDataManager.addSecurityQuestion(securityQuestion=" + securityQuestion + "): " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<SecurityQuestion> getQuestionsByUserId(Integer userId) throws MiddlewareQueryException {
        try {
            return getSecurityQuestionDao().getByUserId(userId);
        } catch (Exception e) {
            logAndThrowException("Error encountered while getting Security Questions: " +
                    "WorkbenchDataManager.getQuestionsByUserId(userId=" + userId + "): " + e.getMessage(), e);
        }
        return new ArrayList<SecurityQuestion>();
    }
    
    @Override
    public ProjectUserMysqlAccount getProjectUserMysqlAccountByProjectIdAndUserId(Integer projectId, Integer userId)
            throws MiddlewareQueryException {
        return getProjectUserMysqlAccountDao().getByProjectIdAndUserId(projectId, userId);
    }
    
    @Override
    public Integer addProjectUserMysqlAccount(ProjectUserMysqlAccount record) throws MiddlewareQueryException {
        List<ProjectUserMysqlAccount> tosave = new ArrayList<ProjectUserMysqlAccount>();
        tosave.add(record);
        List<Integer> idsOfRecordsSaved = addProjectUserMysqlAccount(tosave);
        if(!idsOfRecordsSaved.isEmpty()){
            return idsOfRecordsSaved.get(0);
        } else{
            return null;
        }
    }
    
    @Override
    public List<Integer> addProjectUserMysqlAccounts(List<ProjectUserMysqlAccount> records) throws MiddlewareQueryException {
        return addProjectUserMysqlAccount(records);
    }
    
    private List<Integer> addProjectUserMysqlAccount(List<ProjectUserMysqlAccount> records) throws MiddlewareQueryException {
        Session session = getCurrentSession();
        if (session == null) {
            return new ArrayList<Integer>();
        }

        Transaction trans = null;

        List<Integer> idsSaved = new ArrayList<Integer>();
        try {
            // begin save transaction
            trans = session.beginTransaction();

            ProjectUserMysqlAccountDAO dao = getProjectUserMysqlAccountDao();
            
            for(ProjectUserMysqlAccount record : records) {
                ProjectUserMysqlAccount recordSaved = dao.saveOrUpdate(record);
                idsSaved.add(recordSaved.getId());
            }
            
            // end transaction, commit to database
            trans.commit();

            // remove ProjectUserMysqlAccount data from session cache
            for(ProjectUserMysqlAccount record : records) {
                session.evict(record);
            }
            session.evict(records);
            
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException(
                    "Error encountered while adding ProjectUserMysqlAccount: WorkbenchDataManager.addProjectUserMysqlAccount(records="
                            + records + "): " + e.getMessage(), e);
        }

        return idsSaved;
    }

    @Override
    public List<ProjectBackup> getProjectBackups() throws MiddlewareQueryException {
        return getProjectBackupDao().getAllProjectBackups();
    }
    
    @Override
    public List<ProjectBackup> getProjectBackups(Project project) throws MiddlewareQueryException {
        if (project == null || project.getProjectId() == null) return null;
        
        return getProjectBackupDao().getProjectBackups(project.getProjectId());
    }

    @Override
    public ProjectBackup saveOrUpdateProjectBackup(ProjectBackup projectBackup) throws MiddlewareQueryException {
        
    	
    	Transaction trans = null;
        Session session = getCurrentSession();
        
        try {
            trans = session.beginTransaction();
        
            
            if (projectBackup.getBackupPath() != null) {
                
            	List<ProjectBackup> result = getProjectBackupDao().getProjectBackupByBackupPath(projectBackup.getBackupPath());
            	
                if (result != null && result.size() > 0) {
                	//ProjectBackup existingBackup = result.get(0);
                	//existingBackup.setBackupTime(projectBackup.getBackupTime());
                	//projectBackup = existingBackup;
                	
                	result.get(0).setBackupTime(projectBackup.getBackupTime());
                	projectBackup = result.get(0);
                }
            }
            
            
            projectBackup = getProjectBackupDao().saveOrUpdate(projectBackup);

            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Cannot save ProjectBackup: WorkbenchDataManager.saveOrUpdateProjectBackup(projectBackup=" + projectBackup + "): "
                    + e.getMessage(), e);
        }

        return projectBackup;
    }
    
    @Override
    public void deleteProjectBackup(ProjectBackup projectBackup) throws MiddlewareQueryException {
        Transaction trans = null;
        Session session = getCurrentSession();

        try {
            trans = session.beginTransaction();
            getProjectBackupDao().makeTransient(projectBackup);
            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Cannot delete Project: WorkbenchDataManager.deleteProjectBackup(projectBackup=" + projectBackup + "): "
                    + e.getMessage(), e);
        }
    }
    
    @Override
    public UserInfo getUserInfo(int userId) throws MiddlewareQueryException {
        try {
            return getUserInfoDao().getUserInfoByUserId(userId);
        }
        catch (Exception e) {
            logAndThrowException("Cannot increment login count for user_id =" + userId + "): " + e.getMessage(), e);
        }
        finally {
        }
        
        return null;
    }

    @Override
    public void incrementUserLogInCount(int userId) throws MiddlewareQueryException {
        Transaction trans = null;
        Session session = getCurrentSession();
        
        try {
            trans = session.beginTransaction();
            
            UserInfo userdetails = getUserInfoDao().getUserInfoByUserId(userId);
            if (userdetails != null) {
                getUserInfoDao().updateLoginCounter(userdetails);
            }
            
            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Cannot increment login count for user_id =" + userId + "): "
                + e.getMessage(), e);
        }
        finally {
            session.flush();
        }
    }

    @Override
    public void insertOrUpdateUserInfo(UserInfo userDetails) throws MiddlewareQueryException {
        getUserInfoDao().insertOrUpdateUserInfo(userDetails);
    }

    @Override
    public boolean changeUserPassword(String username, String password) throws MiddlewareQueryException {
        return getUserDao().changePassword(username, password);
    }
    
    @Override
    public List<Integer> getBreedingMethodIdsByWorkbenchProjectId(Integer projectId) throws MiddlewareQueryException{
        return getProjectMethodDao().getBreedingMethodIdsByWorkbenchProjectId(projectId);
    }

}
