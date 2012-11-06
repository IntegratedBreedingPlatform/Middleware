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

package org.generationcp.middleware.manager.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.generationcp.middleware.dao.ToolConfigurationDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionPerThreadProvider;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.WorkbenchDataManagerImpl;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectActivity;
import org.generationcp.middleware.pojos.workbench.ProjectLocationMap;
import org.generationcp.middleware.pojos.workbench.ProjectMethod;
import org.generationcp.middleware.pojos.workbench.ProjectUserRole;
import org.generationcp.middleware.pojos.workbench.Role;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.ToolConfiguration;
import org.generationcp.middleware.pojos.workbench.WorkbenchDataset;
import org.generationcp.middleware.pojos.workbench.WorkflowTemplate;
import org.generationcp.middleware.util.HibernateUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestWorkbenchDataManagerImpl{

    private static WorkbenchDataManager manager;
    private static HibernateUtil hibernateUtil;

    @BeforeClass
    public static void setUp() throws Exception {

        DatabaseConnectionParameters workbenchDb = new DatabaseConnectionParameters("testDatabaseConfig.properties", "workbench");
        hibernateUtil = new HibernateUtil(workbenchDb.getHost(), workbenchDb.getPort(), workbenchDb.getDbName(), 
                                workbenchDb.getUsername(), workbenchDb.getPassword());
        HibernateSessionProvider sessionProvider = new HibernateSessionPerThreadProvider(hibernateUtil.getSessionFactory());
        manager = new WorkbenchDataManagerImpl(sessionProvider);
    }
    
    @Test
    public void testSaveProject() throws MiddlewareQueryException {
        Project project1 = new Project();
        project1.setProjectName("Test Project 1");
        project1.setUserId(1);
        project1.setCropType(manager.getCropTypeByName(CropType.CHICKPEA));
        project1.setStartDate(new GregorianCalendar().getTime());
        project1.setLastOpenDate(new GregorianCalendar().getTime());

        Project project2 = new Project();
        project2.setProjectName("Test Project 2");
        project2.setCropType(manager.getCropTypeByName(CropType.CHICKPEA));
        project2.setStartDate(new GregorianCalendar().getTime());
        project2.setLastOpenDate(new GregorianCalendar().getTime());

        WorkflowTemplate marsTemplate = new WorkflowTemplate();
        marsTemplate.setTemplateId(1L);

        project1.setTemplate(marsTemplate);
        project2.setTemplate(marsTemplate);

        Project projectNew1 = manager.saveOrUpdateProject(project1);
        Project projectNew2 = manager.saveOrUpdateProject(project2);

        // Adding Project Locations
        List<ProjectLocationMap> projectLocationMapList = new ArrayList<ProjectLocationMap>();

        ProjectLocationMap projectLocationMap1 = new ProjectLocationMap();
        projectLocationMap1.setProject(projectNew1);
        projectLocationMap1.setLocationId(new Long(3));

        ProjectLocationMap projectLocationMap2 = new ProjectLocationMap();
        projectLocationMap2.setProject(projectNew2);
        projectLocationMap2.setLocationId(new Long(4));

        projectLocationMapList.add(projectLocationMap1);
        projectLocationMapList.add(projectLocationMap2);

        projectLocationMapList.add(projectLocationMap1);
        projectLocationMapList.add(projectLocationMap2);

        manager.addProjectLocationMap(projectLocationMapList);

        // Adding Project Method
        List<ProjectMethod> projectMethodList = new ArrayList<ProjectMethod>();

        ProjectMethod projectMethod1 = new ProjectMethod();
        projectMethod1.setProject(projectNew1);
        projectMethod1.setMethodId(5);

        ProjectMethod projectMethod2 = new ProjectMethod();
        projectMethod2.setProject(projectNew1);
        projectMethod2.setMethodId(6);

        projectMethodList.add(projectMethod1);
        projectMethodList.add(projectMethod2);

        manager.addProjectMethod(projectMethodList);

        // Adding Project Activity
        List<ProjectActivity> projectActivityList = new ArrayList<ProjectActivity>();

        ProjectActivity projectActivity1 = new ProjectActivity();
        projectActivity1.setProject(projectNew1);
        projectActivity1.setName("Activity 1");
        projectActivity1.setDescription("Test Description for Activity 1");
        projectActivity1.setDate(new Date(System.currentTimeMillis()));
        projectActivity1.setUser(manager.getUserById(new Integer(1)));

        ProjectActivity projectActivity2 = new ProjectActivity();
        projectActivity2.setProject(projectNew1);
        projectActivity2.setName("Activity 2");
        projectActivity2.setDescription("Test Description for Activity 2");
        projectActivity2.setDate(new Date(System.currentTimeMillis()));
        projectActivity2.setUser(manager.getUserById(new Integer(1)));

        projectActivityList.add(projectActivity1);
        projectActivityList.add(projectActivity2);

        manager.addProjectActivity(projectActivityList);

        System.out.println("testSaveProject() RESULTS: ");
        System.out.println("  " + project1);
        System.out.println("  " + project2);

        // cleanup
        deleteProject(project1);
        deleteProject(project2);
        
//        deleteAllProjects();

    }

    // delete dependencies first, before deleting the project
    private void deleteProject(Project project) {

        Long projectId = project.getProjectId();
        try {

            List<ProjectActivity> projectActivities = manager.getProjectActivitiesByProjectId(projectId, 0,
                    (int) manager.countProjectActivitiesByProjectId(projectId));
            for (ProjectActivity projectActivity : projectActivities) {
                manager.deleteProjectActivity(projectActivity);
            }

            List<ProjectMethod> projectMethods = manager.getProjectMethodByProject(project, 0,
                    (int) manager.countMethodIdsByProjectId(projectId));
            for (ProjectMethod projectMethod : projectMethods) {
                manager.deleteProjectMethod(projectMethod);
            }

            List<ProjectUserRole> projectUsers = manager.getProjectUserRolesByProject(project);
            for (ProjectUserRole projectUser : projectUsers) {
                manager.deleteProjectUserRole(projectUser);
            }

            List<WorkbenchDataset> datasets = manager.getWorkbenchDatasetByProjectId(projectId, 0,
                    (int) manager.countWorkbenchDatasetByProjectId(projectId));
            for (WorkbenchDataset dataset : datasets) {
                manager.deleteWorkbenchDataset(dataset);
            }

            List<ProjectLocationMap> projectLocationMaps = manager.getProjectLocationMapByProjectId(projectId, 0,
                    (int) manager.countLocationIdsByProjectId(projectId));
            //manager.deleteProjectLocationMap(projectLocationMaps);
            for (ProjectLocationMap projectLocationMap : projectLocationMaps) {
                manager.deleteProjectLocationMap(projectLocationMap);
            }

            manager.deleteProject(project);

        } catch (MiddlewareQueryException e) {
            System.out.println("Error in deleteProject(): " + e.getMessage());
            e.printStackTrace();
        }

    }

    private void deleteAllProjects() throws MiddlewareQueryException {
        List<Project> projects = manager.getProjects();
        for (Project project : projects) {
            deleteProject(project);
        }
    }

    @Test
    public void testGetProjects() throws MiddlewareQueryException {
        List<Project> projects = manager.getProjects();

        System.out.println("testGetProjects(): ");
        for (Project project : projects) {
            System.out.println("  " + project);
        }
    }

    @Test
    public void testGetToolWithName() throws MiddlewareQueryException {
        String toolName = "fieldbook";
        Tool tool = manager.getToolWithName(toolName);
        System.out.println("testGetToolWithName(" + toolName + "): " + tool);
    }

    @Test
    public void testAddUser() throws MiddlewareQueryException {
        User user = new User();
        user.setUserid(1000);
        user.setInstalid(-1);
        user.setStatus(-1);
        user.setAccess(-1);
        user.setUserid(-1);
        user.setType(-1);
        user.setName("user_test");
        user.setPassword("user_password");
        user.setPersonid(1000);
        user.setAdate(20120101);
        user.setCdate(20120101);

        // add user
        manager.addUser(user);
        System.out.println("testAddUser(): " + user);

        // cleanup
        manager.deleteUser(user);
    }

    @Test
    public void testAddPerson() throws MiddlewareQueryException {
        Person person = new Person();
        //        person.setId(1000);
        person.setInstituteId(1);
        person.setFirstName("Lich");
        person.setMiddleName("Frozen");
        person.setLastName("King");
        person.setPositionName("King of Icewind Dale");
        person.setTitle("His Highness");
        person.setExtension("1");
        person.setFax("2");
        person.setEmail("lichking@blizzard.com");
        person.setNotes("notes");
        person.setContact("3");
        person.setLanguage(-1);
        person.setPhone("4");

        // add the person
        manager.addPerson(person);
        System.out.println("testAddPerson(): " + person);

        // cleanup
        manager.deletePerson(person);
    }

    @Test
    public void testGetProjectById() throws MiddlewareQueryException {
        Long id = Long.valueOf(1);
        Project project = manager.getProjectById(id);
        System.out.println("testGetProjectById(" + id + "): " + project);
    }

    @Test
    public void testGetUserByName() throws MiddlewareQueryException {
        String name = "test";
        User user = (User) manager.getUserByName(name, 0, 1, Operation.EQUAL).get(0);
        System.out.println("testGetUserByName(name=" + name + "):" + user);
    }

    @Test
    public void testAddWorkbenchDataset() throws MiddlewareQueryException {
        // Assumption: There is at least one project in the db
        Project project = manager.getProjects().get(0); // First project in db

        WorkbenchDataset dataset = new WorkbenchDataset();
        dataset.setName("Test Dataset");
        dataset.setDescription("Test Dataset Description");
        dataset.setCreationDate(new Date(System.currentTimeMillis()));
        dataset.setProject(project);
        manager.addWorkbenchDataset(dataset);
        System.out.println("testAddWorkbenchDataset(): " + dataset);

        // clean up
        manager.deleteWorkbenchDataset(dataset);
    }

    @Test
    public void testGetWorkbenchDatasetByProjectId() throws MiddlewareQueryException {
        Long projectId = 1L;
        List<WorkbenchDataset> list = manager.getWorkbenchDatasetByProjectId(projectId, 0, 10);
        System.out.println("testGetWorkbenchDatasetByProjectId(" + projectId + "): ");

        if (list.isEmpty()) {
            System.out.println("  No records found.");
        }

        for (WorkbenchDataset d : list) {
            System.out.println("  " + d.getDatasetId() + ": " + d.getName());
        }
    }

    @Test
    public void testCountWorkbenchDatasetByProjectId() throws MiddlewareQueryException {
        Long projectId = 1L;
        long result = manager.countWorkbenchDatasetByProjectId(projectId);
        System.out.println("testCountWorkbenchDatasetByProjectId(" + projectId + "): " + result);
    }

    @Test
    public void testGetWorkbenchDatasetByName() throws MiddlewareQueryException {
        String name = "D";
        List<WorkbenchDataset> list = manager.getWorkbenchDatasetByName(name, Operation.EQUAL, 0, 10);
        System.out.println("testGetWorkbenchDatasetByName(name=" + name + "): ");

        if (list.isEmpty()) {
            System.out.println("  No records found.");
        }

        for (WorkbenchDataset d : list) {
            System.out.println("  " + d.getDatasetId() + ": " + d.getName());
        }
    }

    @Test
    public void testCountWorkbenchDatasetByName() throws MiddlewareQueryException {
        String name = "a";
        long result = manager.countWorkbenchDatasetByName(name, Operation.EQUAL);
        System.out.println("testCountWorkbenchDatasetByName(name=" + name + "): " + result);
    }

    @Test
    public void testGetLocationIdsByProjectId() throws MiddlewareQueryException {
        Long projectId = 1L;
        List<Long> ids = manager.getLocationIdsByProjectId(projectId, 0, 10);
        System.out.println("testgetLocationIdsByProjectId(" + projectId + "): " + ids);
    }

    @Test
    public void testCountLocationIdsByProjectId() throws MiddlewareQueryException {
        Long projectId = 1L;
        long result = manager.countLocationIdsByProjectId(projectId);
        System.out.println("testCountLocationIdsByProjectId(" + projectId + "): " + result);
    }

    @Test
    public void testGetMethodsByProjectId() throws MiddlewareQueryException {
        Long projectId = 1L;
        List<Integer> list = manager.getMethodIdsByProjectId(projectId, 0, 10);
        System.out.println("testGetMethodsByProjectId(" + projectId + "): ");

        if (list.isEmpty()) {
            System.out.println("  No records found.");
        }

        for (Integer m : list) {
            System.out.println("  " + m);
        }
    }

    @Test
    public void testCountMethodsByProjectId() throws MiddlewareQueryException {
        Long projectId = 1L;
        long result = manager.countMethodIdsByProjectId(projectId);
        System.out.println("testCountMethodsByProjectId(" + projectId + "): " + result);
    }
    
    @Test
    public void testAddProjectUserRoles() throws MiddlewareQueryException {
        Long projectId = manager.getProjects().get(0).getProjectId(); // First project found in db
        List<ProjectUserRole> projectUsers = new ArrayList<ProjectUserRole>();

        // Assumptions: Project with id=1, and Users with id=1 
        Project project1 = manager.getProjectById(projectId);
        User user1 = manager.getUserById(1);
        Role role1 = manager.getAllRoles().get(0);
        Role role2 = manager.getAllRoles().get(1);
        
        ProjectUserRole newRecord1 = new ProjectUserRole(project1, user1, role1);
        ProjectUserRole newRecord2 = new ProjectUserRole(project1, user1, role2);  

        projectUsers.add(newRecord1);
        projectUsers.add(newRecord2);

        // add the projectUsers
        List<Integer> projectUsersAdded = manager.addProjectUserRole(projectUsers);

        System.out.println("testAddProjectUsers(projectId=" + projectId + ") ADDED: " + projectUsersAdded.size());
        
        // clean up
        manager.deleteProjectUserRole(newRecord1);
        manager.deleteProjectUserRole(newRecord2);
    }
    
    @Test
    public void testGetUsersByProjectId() throws MiddlewareQueryException {
        Long projectId = 1L;
        List<User> users = manager.getUsersByProjectId(projectId);
        System.out.println("testGetUsersByProjectId(" + projectId + "): ");

        if (users.isEmpty()) {
            System.out.println("  No records found.");
        }

        for (User u : users) {
            System.out.println("  " + u.getUserid() + ": " + u.getName());
        }
    }

    @Test
    public void testCountUsersByProjectId() throws MiddlewareQueryException {
        Long projectId = 1L;
        long result = manager.countUsersByProjectId(projectId);
        System.out.println("testCountUsersByProjectId(" + projectId + "): " + result);
    }
    
    @Test
    public void testGetActivitiesByProjectId() throws MiddlewareQueryException {
        Long projectId = 20L;
        List<ProjectActivity> list = manager.getProjectActivitiesByProjectId(projectId, 0, 10);
        System.out.println("testGetActivitiesByProjectId(" + projectId + "): ");

        if (list.isEmpty()) {
            System.out.println("  No records found.");
        }

        for (ProjectActivity m : list) {
            System.out.println("  " + m);
        }
    }

    @Test
    public void testCountActivitiesByProjectId() throws MiddlewareQueryException {
        Long projectId = 1L;
        long result = manager.countProjectActivitiesByProjectId(projectId);
        System.out.println("testCountActivitiesByProjectId(" + projectId + "): " + result);
    }

    @Test
    public void testAddToolConfiguration() throws MiddlewareQueryException {
        Long toolId = 1L;
        ToolConfiguration toolConfig = new ToolConfiguration();
        Tool tool = new Tool();
        tool.setToolId(toolId);

        toolConfig.setTool(tool);
        toolConfig.setConfigKey("5th key");
        toolConfig.setConfigValue("test value");

        manager.addToolConfiguration(toolConfig);

        ToolConfigurationDAO dao = new ToolConfigurationDAO();
        dao.setSession(hibernateUtil.getCurrentSession());
        ToolConfiguration result = dao.getById(toolId, false);

        System.out.println("testAddToolConfiguration(toolId=" + toolId + "): " + result);
    }

    @Test
    public void testUpdateToolConfiguration() throws MiddlewareQueryException {
        Long toolId = 1L;
        ToolConfiguration toolConfig = manager.getToolConfigurationByToolIdAndConfigKey(toolId, "5th key");
        
        if (toolConfig != null){
            String oldToolConfigValue = toolConfig.toString();
            toolConfig.setConfigValue("test test");
    
            manager.updateToolConfiguration(toolConfig);
    
            ToolConfigurationDAO dao = new ToolConfigurationDAO();
            dao.setSession(hibernateUtil.getCurrentSession());
            ToolConfiguration result = dao.getById(toolId, false);
    
            System.out.println("testUpdateToolConfiguration(toolId=" + toolId + "): ");
            System.out.println("  FROM: " + oldToolConfigValue);
            System.out.println("    TO: " + result);
        } else {
            System.out.println("testUpdateToolConfiguration(toolId=" + toolId + "): Tool configuration not found.");
        }
    }

    @Test
    public void testDeleteToolConfiguration() throws MiddlewareQueryException {
        Long toolId = 1L;
        ToolConfiguration toolConfig = manager.getToolConfigurationByToolIdAndConfigKey(toolId, "5th key");

        if (toolConfig != null){
            manager.deleteToolConfiguration(toolConfig);
            ToolConfigurationDAO dao = new ToolConfigurationDAO();
            dao.setSession(hibernateUtil.getCurrentSession());
            ToolConfiguration result = dao.getById(toolId, false);

            System.out.println("testDeleteToolConfiguration(toolId=" + toolId + "): " + result);
        } else {
            System.out.println("testDeleteToolConfiguration(toolId=" + toolId + "): Tool Configuration not found");
        }

    }

    @Test
    public void testGetListOfToolConfigurationsByToolId() throws MiddlewareQueryException {
        Long toolId = 1L;
        List<ToolConfiguration> result = manager.getListOfToolConfigurationsByToolId(toolId);
        System.out.println("testGetListOfToolConfigurationsByToolId(" + toolId + "): ");

        if (result.isEmpty()) {
            System.out.println("  No records found.");
        } else {
            for (ToolConfiguration t : result) {
                System.out.println("  " + t);
            }
        }
    }

    @Test
    public void testGetToolConfigurationByToolIdAndConfigKey() throws MiddlewareQueryException {
        Long toolId = 1L;
        String configKey = "test";
        ToolConfiguration toolConfig = manager.getToolConfigurationByToolIdAndConfigKey(toolId, configKey);
        System.out.println("testGetToolConfigurationByToolIdAndConfigKey(toolId=" + toolId + ", configKey=" + configKey + "): "
                + toolConfig);
    }

    @Test
    public void testGetInstalledCentralCrops() throws MiddlewareQueryException {
        ArrayList<CropType> cropTypes = (ArrayList<CropType>) manager.getInstalledCentralCrops();
        System.out.println("testGetInstalledCentralCrops(): " + cropTypes);
    }

    @Test
    public void testGetCropTypeByName() throws MiddlewareQueryException {
        String cropName = CropType.CHICKPEA;
        CropType cropType = manager.getCropTypeByName(cropName);
        System.out.println("testGetCropTypeByName(" + cropName + "): " + cropType);
    }

    @Test
    public void testAddCropType() throws MiddlewareQueryException {
        
        CropType cropType = new CropType("Coconut");
        try{
            String added = manager.addCropType(cropType);
            System.out.println("testAddCropType(" + cropType + "): records added = " + added);
        }catch(MiddlewareQueryException e){
            if (e.getMessage().equals("Crop type already exists.")){
                System.out.println(e.getMessage());
            } else {
                throw e;
            }            
        }
        
    }

    @Test
    public void testGetLocalIbdbUserId() throws MiddlewareQueryException {
        Integer workbenchUserId = Integer.valueOf(1);
        Long projectId = Long.valueOf(3);
        Integer localIbdbUserId = manager.getLocalIbdbUserId(workbenchUserId, projectId);
        System.out.println("testGetLocalIbdbUserId(workbenchUserId=" + workbenchUserId + ", projectId=" + projectId + "): "
                + localIbdbUserId);
    }

    @Test
    public void testGetRoleById() throws MiddlewareQueryException {
        Integer id = Integer.valueOf(1); // Assumption: there is a role with id 1
        Role role = manager.getRoleById(id);
        System.out.println("testGetRoleById(id=" + id + "): \n  " + role);
    }

    @Test
    public void testGetRoleByNameAndWorkflowTemplate() throws MiddlewareQueryException {
        String templateName = "MARS";
        String roleName = "MARS Breeder";
        WorkflowTemplate workflowTemplate = manager.getWorkflowTemplateByName(templateName).get(0);
        Role role = manager.getRoleByNameAndWorkflowTemplate(roleName, workflowTemplate);
        System.out.println("testGetRoleByNameAndWorkflowTemplate(name=" + roleName + ", workflowTemplate=" + workflowTemplate.getName()
                + "): \n  " + role);
    }

    @Test
    public void testGetRolesByWorkflowTemplate() throws MiddlewareQueryException {
        WorkflowTemplate workflowTemplate = manager.getWorkflowTemplates().get(0); // get the first template in the db
        List<Role> roles = manager.getRolesByWorkflowTemplate(workflowTemplate);
        System.out.println("testGetRolesByWorkflowTemplate(workflowTemplate=" + workflowTemplate.getName() + "): " + roles.size());
        for (Role role: roles){
            System.out.println("  "+role);
        }
    }

    @Test
    public void testGetWorkflowTemplateByRole() throws MiddlewareQueryException {
        Integer id = 1; // role with id = 1
        Role role = manager.getRoleById(id);
        WorkflowTemplate template = manager.getWorkflowTemplateByRole(role);
        System.out.println("testGetWorkflowTemplateByRole(role=" + role.getName() + "): \n  " + template);
    }
    
    @Test
    public void testGetRoleByProjectAndUser() throws MiddlewareQueryException {
        // Assumption: first project stored in the db has associated project users with role
        Project project = manager.getProjects().get(0); // get first project
        List<ProjectUserRole> projectUsers = manager.getProjectUserRolesByProject(project); // get project users
        
        if (projectUsers.size()>0){
            ProjectUserRole projectUser = projectUsers.get(0); // get the first user of the project
            User user = manager.getUserById(projectUser.getUserId());   
            List<Role> roles = manager.getRolesByProjectAndUser(project, user); // get the roles
            System.out.println("testGetRoleByProjectAndUser(project=" + project.getProjectName() + ", user=" + user.getName() + "): \n  " + roles);
        } else {
            System.out.println("testGetRoleByProjectAndUser(project=" + project.getProjectName() + "): Error in data - Project has no users. ");
        }
    }
    
    @Test
    public void testGetAllRoles() throws MiddlewareQueryException {
        List<Role> roles = manager.getAllRoles();
        System.out.println("RESULTS:");
        for(Role role : roles) {
            System.out.println(role);
        }
    }
    
    //TODO testAddIbdbUserMap()
    
    //TODO testUpdateWorkbenchRuntimeData()
    
    @AfterClass
    public static void tearDown() throws Exception {
        hibernateUtil.shutdown();
    }
}
