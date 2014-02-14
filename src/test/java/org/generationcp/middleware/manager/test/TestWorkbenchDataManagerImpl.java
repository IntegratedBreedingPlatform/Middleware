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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.*;

import org.junit.Assert;

import org.generationcp.middleware.dao.ToolConfigurationDAO;
import org.generationcp.middleware.dao.ProjectUserInfoDAO;
import org.generationcp.middleware.dao.ToolDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionPerThreadProvider;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.hibernate.HibernateUtil;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.WorkbenchDataManagerImpl;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.IbdbUserMap;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectActivity;
import org.generationcp.middleware.pojos.workbench.ProjectLocationMap;
import org.generationcp.middleware.pojos.workbench.ProjectMethod;
import org.generationcp.middleware.pojos.workbench.ProjectUserMysqlAccount;
import org.generationcp.middleware.pojos.workbench.ProjectUserRole;
import org.generationcp.middleware.pojos.workbench.TemplateSetting;
import org.generationcp.middleware.pojos.workbench.WorkbenchSetting;
import org.generationcp.middleware.pojos.workbench.Role;
import org.generationcp.middleware.pojos.workbench.UserInfo;
import org.generationcp.middleware.pojos.workbench.SecurityQuestion;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.ToolConfiguration;
import org.generationcp.middleware.pojos.workbench.ToolType;
import org.generationcp.middleware.pojos.workbench.WorkbenchDataset;
import org.generationcp.middleware.pojos.workbench.WorkflowTemplate;
import org.generationcp.middleware.pojos.workbench.ProjectBackup;
import org.generationcp.middleware.pojos.workbench.WorkbenchRuntimeData;
import org.generationcp.middleware.util.Debug;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
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
  
    private long startTime;

    @Rule
    public TestName name = new TestName();



    @Before
    public void beforeEachTest() {
        Debug.println(0, "#####" + name.getMethodName() + " Start: ");
        startTime = System.nanoTime();
    }

    @After
    public void afterEachTest() {
        long elapsedTime = System.nanoTime() - startTime;
        Debug.println(0, "#####" + name.getMethodName() + " End: Elapsed Time = " + elapsedTime + " ns = " + ((double) elapsedTime / 1000000000) + " s");
    }


    @Test
    public void testAddUser() throws MiddlewareQueryException {
        User user = new User();
        //user.setUserid(1000);
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
        Debug.println(0, "testAddUser(): " + user);

    }

    @Test
    public void testAddPerson() throws MiddlewareQueryException {
        Person person = new Person();
        
        //person.setId(1000);
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
        Debug.println(0, "testAddPerson(): " + person);

    }

    @Test
    public void testAddProject() throws MiddlewareQueryException {
        Project project = new Project();

        project.setUserId(1);
        project.setProjectName("Project Name " + new Random().nextInt(10000));
        project.setStartDate(new Date(System.currentTimeMillis()));
        project.setTemplate(manager.getWorkflowTemplateByName("MARS").get(0));
        project.setTemplateModified(Boolean.FALSE);
        project.setCropType(manager.getCropTypeByName(CropType.MAIZE));
        project.setLocalDbName("ibdbv2_maize_1_local");
        project.setCentralDbName("ibdbv2_maize_central");
        project.setLastOpenDate(new Date(System.currentTimeMillis()));

        // add the project
        manager.addProject(project);
        Debug.println(0, "testAddProject(): " + project);

    }

    @Test
    public void testAddProjectActivity() throws MiddlewareQueryException {
        ProjectActivity projectActivity = new ProjectActivity();

        projectActivity.setProject(manager.getProjectById(41L));
        projectActivity.setName("Fieldbook");
        projectActivity.setDescription("Launch FieldBook");
        projectActivity.setUser(manager.getUserById(1));
        projectActivity.setCreatedAt(new Date(System.currentTimeMillis()));

        // add the project activity
        manager.addProjectActivity(projectActivity);
        Debug.println(0, "testAddProjectActivity(): " + projectActivity);

    }

    @Test
    public void testAddIbdbUserMap() throws MiddlewareQueryException {

        User u = manager.getAllUsers().get(0);

        Assert.assertNotNull("there should be at least 1 user",u);

        Project p = manager.getProjectsByUser(u).get(0);

        Assert.assertNotNull("there should be at least 1 project in user_id " + u.getUserid(),p);


        IbdbUserMap userMap = new IbdbUserMap();

    	userMap.setProjectId(p.getProjectId());
        userMap.setIbdbUserId(u.getUserid() * -1);
    	userMap.setWorkbenchUserId(u.getUserid());

        // add the IBDB User Map
        Integer result = manager.addIbdbUserMap(userMap);
        Assert.assertNotNull("Should return a new user_map id",result);

        Debug.println(0, "testAddIbdbUserMap(): " + userMap);

    }

    @Test
    public void testAddProjectLocationMap() throws MiddlewareQueryException {
        ProjectLocationMap projectLocationMap = new ProjectLocationMap();

    	projectLocationMap.setLocationId(1L);
    	projectLocationMap.setProject(manager.getProjectById(1L));

        // add the Add Project Location Map
        Integer result = manager.addProjectLocationMap(projectLocationMap);

        Assert.assertNotNull("Should return a result, id of the newly added projectlocationmap",result);

        Debug.println(0, "testAddProjectLocationMap(): " + projectLocationMap);

    }
    
    @Test
    public void testGetProjects() throws MiddlewareQueryException {
        List<Project> projects = manager.getProjects();

        Debug.println(0, "testGetProjects(): ");
        for (Project project : projects) {
            Debug.println(0, "  " + project);
        }
    }

    @Test
    public void testGetToolWithName() throws MiddlewareQueryException {
        String toolName = "fieldbook";
        Tool tool = manager.getToolWithName(toolName);
        Debug.println(0, "testGetToolWithName(" + toolName + "): " + tool);
    }

    @Test
    public void testGetProjectById() throws MiddlewareQueryException {
        Long id = manager.getProjects().get(0).getProjectId();
        Project project = manager.getProjectById(id);
        Debug.println(0, "testGetProjectById(" + id + "): " + project);
    }

    @Test
    public void testGetProjectByName() throws MiddlewareQueryException {
        String name = "Test Cowpea 1";// "Replace with project name to search";
        Project project = manager.getProjectByName(name);
        Debug.println(0, "testGetProjectByName(" + name + "): " + project);
    }

    @Test
    public void testGetUserByName() throws MiddlewareQueryException {
        String name = "user_test";
        User user = (User) manager.getUserByName(name, 0, 1, Operation.EQUAL).get(0);
        Debug.println(0, "testGetUserByName(name=" + name + "):" + user);
    }

    @Test
    public void testAddWorkbenchDataset() throws MiddlewareQueryException {
        // Assumption: There is at least one project in the db
        Project project = manager.getProjectById(1L); // First project in db

        WorkbenchDataset dataset = new WorkbenchDataset();
        dataset.setName("Test Dataset");
        dataset.setDescription("Test Dataset Description");
        dataset.setCreationDate(new Date(System.currentTimeMillis()));
        dataset.setProject(project);
        manager.addWorkbenchDataset(dataset);
        Debug.println(0, "testAddWorkbenchDataset(): " + dataset);

    }

    @Test
    public void testGetWorkbenchDatasetByProjectId() throws MiddlewareQueryException {
        Long projectId = 1L;
        List<WorkbenchDataset> list = manager.getWorkbenchDatasetByProjectId(projectId, 0, 10);
        Debug.println(0, "testGetWorkbenchDatasetByProjectId(" + projectId + "): ");

        if (list.isEmpty()) {
            Debug.println(0, "  No records found.");
        }

        for (WorkbenchDataset d : list) {
            Debug.println(0, "  " + d.getDatasetId() + ": " + d.getName());
        }
    }

    @Test
    public void testCountWorkbenchDatasetByProjectId() throws MiddlewareQueryException {
        Long projectId = 1L;
        long result = manager.countWorkbenchDatasetByProjectId(projectId);
        Debug.println(0, "testCountWorkbenchDatasetByProjectId(" + projectId + "): " + result);
    }

    @Test
    public void testGetWorkbenchDatasetByName() throws MiddlewareQueryException {
        String name = "D";
        List<WorkbenchDataset> list = manager.getWorkbenchDatasetByName(name, Operation.EQUAL, 0, 10);
        Debug.println(0, "testGetWorkbenchDatasetByName(name=" + name + "): ");

        if (list.isEmpty()) {
            Debug.println(0, "  No records found.");
        }

        for (WorkbenchDataset d : list) {
            Debug.println(0, "  " + d.getDatasetId() + ": " + d.getName());
        }
    }

    @Test
    public void testCountWorkbenchDatasetByName() throws MiddlewareQueryException {
        String name = "a";
        long result = manager.countWorkbenchDatasetByName(name, Operation.EQUAL);
        Debug.println(0, "testCountWorkbenchDatasetByName(name=" + name + "): " + result);
    }

    @Test
    public void testGetLocationIdsByProjectId() throws MiddlewareQueryException {
        Long projectId = 1L;
        List<Long> ids = manager.getLocationIdsByProjectId(projectId, 0, 10);
        Debug.println(0, "testgetLocationIdsByProjectId(" + projectId + "): " + ids);
    }

    @Test
    public void testCountLocationIdsByProjectId() throws MiddlewareQueryException {
        Long projectId = 1L;
        long result = manager.countLocationIdsByProjectId(projectId);
        Debug.println(0, "testCountLocationIdsByProjectId(" + projectId + "): " + result);
    }

    @Test
    public void testGetMethodsByProjectId() throws MiddlewareQueryException {
        Long projectId = 1L;
        List<Integer> list = manager.getMethodIdsByProjectId(projectId, 0, 10);
        Debug.println(0, "testGetMethodsByProjectId(" + projectId + "): ");

        if (list.isEmpty()) {
            Debug.println(0, "  No records found.");
        }

        for (Integer m : list) {
            Debug.println(0, "  " + m);
        }
    }

    @Test
    public void testCountMethodsByProjectId() throws MiddlewareQueryException {
        Long projectId = 1L;
        long result = manager.countMethodIdsByProjectId(projectId);
        Debug.println(0, "testCountMethodsByProjectId(" + projectId + "): " + result);
    }

    @Test
    public void testAddProjectUserRoles() throws MiddlewareQueryException {
        
        Long projectId = manager.getProjects().get((manager.getProjects().size())-1).getProjectId();
        int userId = manager.getAllUsers().get((manager.getAllUsers().size())-1).getUserid();
        List<ProjectUserRole> projectUsers = new ArrayList<ProjectUserRole>();

        Project project1 = manager.getProjectById(projectId);
        User user1 = manager.getUserById(userId);
        Role role1 = manager.getAllRoles().get(0);
        Role role2 = manager.getAllRoles().get(1);

        ProjectUserRole newRecord1 = new ProjectUserRole(project1, user1, role1);
        ProjectUserRole newRecord2 = new ProjectUserRole(project1, user1, role2);

        projectUsers.add(newRecord1);
        projectUsers.add(newRecord2);

        // add the projectUsers
        List<Integer> projectUsersAdded = manager.addProjectUserRole(projectUsers);

        Debug.println(0, "testAddProjectUsers(projectId=" + projectId + ") ADDED: " + projectUsersAdded.size());
    }

    @Test
    public void testGetUsersByProjectId() throws MiddlewareQueryException {
        Long projectId = manager.getProjects().get((manager.getProjects().size())-1).getProjectId();
        List<User> users = manager.getUsersByProjectId(projectId);
        Debug.println(0, "testGetUsersByProjectId(" + projectId + "): ");

        if (users.isEmpty()) {
            Debug.println(0, "  No records found.");
        }

        for (User u : users) {
            Debug.println(0, "  " + u.getUserid() + ": " + u.getName());
        }
    }

    @Test
    public void testCountUsersByProjectId() throws MiddlewareQueryException {
    	Long projectId = manager.getProjects().get((manager.getProjects().size())-1).getProjectId();
        long result = manager.countUsersByProjectId(projectId);
        Debug.println(0, "testCountUsersByProjectId(" + projectId + "): " + result);
    }

    @Test
    public void testGetActivitiesByProjectId() throws MiddlewareQueryException {
    	Long projectId = manager.getProjects().get((manager.getProjects().size())-1).getProjectId();
        List<ProjectActivity> list = manager.getProjectActivitiesByProjectId(projectId, 0, 10);
        Debug.println(0, "testGetActivitiesByProjectId(" + projectId + "): ");

        if (list.isEmpty()) {
            Debug.println(0, "  No records found.");
        }

        for (ProjectActivity m : list) {
            Debug.println(0, "  " + m);
        }
    }

    @Test
    public void testCountActivitiesByProjectId() throws MiddlewareQueryException {
    	Long projectId = manager.getProjects().get((manager.getProjects().size())-1).getProjectId();
        long result = manager.countProjectActivitiesByProjectId(projectId);
        Debug.println(0, "testCountActivitiesByProjectId(" + projectId + "): " + result);
    }

    @Test
    public void testAddToolConfiguration() throws MiddlewareQueryException {
        Long toolId = 1L;
        ToolConfiguration toolConfig = new ToolConfiguration();
        Tool tool = new Tool();
        tool.setToolId(toolId);

        toolConfig.setTool(tool);
        toolConfig.setConfigKey("6th key");
        toolConfig.setConfigValue("test value");

        manager.addToolConfiguration(toolConfig);
        Debug.println(0, "testAddToolConfiguration(toolId=" + toolId + "): " + toolConfig);

        // clean up
        manager.deleteToolConfiguration(toolConfig);

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

            Debug.println(0, "testUpdateToolConfiguration(toolId=" + toolId + "): ");
            Debug.println(0, "  FROM: " + oldToolConfigValue);
            Debug.println(0, "    TO: " + result);
        } else {
            Debug.println(0, "testUpdateToolConfiguration(toolId=" + toolId + "): Tool configuration not found.");
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

            Debug.println(0, "testDeleteToolConfiguration(toolId=" + toolId + "): " + result);
        } else {
            Debug.println(0, "testDeleteToolConfiguration(toolId=" + toolId + "): Tool Configuration not found");
        }

    }

    @Test
    public void testGetListOfToolConfigurationsByToolId() throws MiddlewareQueryException {
        Long toolId = 1L;
        List<ToolConfiguration> result = manager.getListOfToolConfigurationsByToolId(toolId);
        Debug.println(0, "testGetListOfToolConfigurationsByToolId(" + toolId + "): ");

        if (result.isEmpty()) {
            Debug.println(0, "  No records found.");
        } else {
            for (ToolConfiguration t : result) {
                Debug.println(0, "  " + t);
            }
        }
    }

    @Test
    public void testGetToolConfigurationByToolIdAndConfigKey() throws MiddlewareQueryException {
        Long toolId = 1L;
        String configKey = "test";
        ToolConfiguration toolConfig = manager.getToolConfigurationByToolIdAndConfigKey(toolId, configKey);
        Debug.println(0, "testGetToolConfigurationByToolIdAndConfigKey(toolId=" + toolId + ", configKey=" + configKey + "): "
                + toolConfig);
    }

 @Test
    public void testAddCropType() throws MiddlewareQueryException {
        CropType cropType = new CropType("Coconut");
        
        try{
            String added = manager.addCropType(cropType);
            Assert.assertNotNull(added);            
            Debug.println(0, "testAddCropType(" + cropType + "): records added = " + added);
        }catch(MiddlewareQueryException e){
            if (e.getMessage().equals("Crop type already exists.")){
                Debug.println(0, e.getMessage());
            } else {
                throw e;
            }
        }

    }

    @Test
    public void testGetInstalledCentralCrops() throws MiddlewareQueryException {
        ArrayList<CropType> cropTypes = (ArrayList<CropType>) manager.getInstalledCentralCrops();
        Assert.assertNotNull(cropTypes);
        Debug.println(0, "testGetInstalledCentralCrops(): " + cropTypes);
    }

    @Test
    public void testGetCropTypeByName() throws MiddlewareQueryException {
        String cropName = CropType.CHICKPEA;
        CropType cropType = manager.getCropTypeByName(cropName);
        Assert.assertNotNull(cropName);      
        Debug.println(0, "testGetCropTypeByName(" + cropName + "): " + cropType);
    }


    @Test
    public void testGetLocalIbdbUserId() throws MiddlewareQueryException {
        Integer workbenchUserId = Integer.valueOf(1);
        Long projectId = Long.valueOf(3);
        Integer localIbdbUserId = manager.getLocalIbdbUserId(workbenchUserId, projectId);
        Assert.assertNotNull(localIbdbUserId); 
        Debug.println(0, "testGetLocalIbdbUserId(workbenchUserId=" + workbenchUserId + ", projectId=" + projectId + "): "
                + localIbdbUserId);
    }

    @Test
    public void testGetRoleById() throws MiddlewareQueryException {
        Integer id = Integer.valueOf(1); // Assumption: there is a role with id 1
        Role role = manager.getRoleById(id);
        Assert.assertNotNull(role); 
        Debug.println(0, "testGetRoleById(id=" + id + "): \n  " + role);
    }

    @Test
    public void testGetRoleByNameAndWorkflowTemplate() throws MiddlewareQueryException {
        String templateName = "MARS";
        String roleName = "MARS Breeder";
        WorkflowTemplate workflowTemplate = manager.getWorkflowTemplateByName(templateName).get(0);
        Role role = manager.getRoleByNameAndWorkflowTemplate(roleName, workflowTemplate);
        Assert.assertNotNull(role); 
        Debug.println(0, "testGetRoleByNameAndWorkflowTemplate(name=" + roleName + ", workflowTemplate=" + workflowTemplate.getName()
                + "): \n  " + role);
    }

    @Test
    public void testGetRolesByWorkflowTemplate() throws MiddlewareQueryException {
        WorkflowTemplate workflowTemplate = manager.getWorkflowTemplates().get(0); // get the first template in the db
        List<Role> roles = manager.getRolesByWorkflowTemplate(workflowTemplate);
        Assert.assertNotNull(roles); 
        Assert.assertTrue(!roles.isEmpty());
        Debug.println(0, "testGetRolesByWorkflowTemplate(workflowTemplate=" + workflowTemplate.getName() + "): " + roles.size());
        for (Role role: roles){
            Debug.println(0, "  "+role);
        }
    }

    @Test
    public void testGetWorkflowTemplateByRole() throws MiddlewareQueryException {
        Role role = manager.getRoleById(manager.getAllRoles().get(0).getRoleId());
        WorkflowTemplate template = manager.getWorkflowTemplateByRole(role);
        Assert.assertNotNull(template);
        Debug.println(0, "testGetWorkflowTemplateByRole(role=" + role.getName() + "): \n  " + template);
    }

    @Test
    public void testGetRoleByProjectAndUser() throws MiddlewareQueryException {
        // Assumption: first project stored in the db has associated project users with role
        Project project = manager.getProjects().get(0); // get first project
        List<ProjectUserRole> projectUsers = manager.getProjectUserRolesByProject(project); // get project users
        Assert.assertNotNull(projectUsers);
        Assert.assertTrue(!projectUsers.isEmpty());
        
        if (projectUsers.size()>0){
            ProjectUserRole projectUser = projectUsers.get(0); // get the first user of the project
            User user = manager.getUserById(projectUser.getUserId());
            List<Role> roles = manager.getRolesByProjectAndUser(project, user); // get the roles
            Debug.println(0, "testGetRoleByProjectAndUser(project=" + project.getProjectName() + ", user=" + user.getName() + "): \n  " + roles);
        } else {
            Debug.println(0, "testGetRoleByProjectAndUser(project=" + project.getProjectName() + "): Error in data - Project has no users. ");
        }
    }

    @Test
    public void testGetAllRoles() throws MiddlewareQueryException {
        List<Role> roles = manager.getAllRoles();
        Assert.assertNotNull(roles);
        Assert.assertTrue(!roles.isEmpty());
        
        for(Role role : roles) {
            Debug.println(0, role.toString());
        }
    }

    @Test
    public void testAddProjectUserMysqlAccount() throws MiddlewareQueryException {
    	Project project = manager.getProjects().get((manager.getProjects().size())-1);
        User user = manager.getAllUsers().get((manager.getAllUsers().size())-1);

        ProjectUserMysqlAccount recordToSave = new ProjectUserMysqlAccount();
        recordToSave.setProject(project);
        recordToSave.setUser(user);
        recordToSave.setMysqlUsername("username " + new Random().nextInt(10000));
        recordToSave.setMysqlPassword("password");

        Integer idSaved = manager.addProjectUserMysqlAccount(recordToSave);
        Assert.assertNotNull(idSaved);
        Debug.println(0, "Id of record saved: " + idSaved);

    }

    @Test
    public void testAddProjectUserMysqlAccounts() throws MiddlewareQueryException {

        Project project1 = new Project();
        project1.setUserId(1);
        project1.setProjectName("Project Name " + new Random().nextInt(10000));
        project1.setStartDate(new Date(System.currentTimeMillis()));
        project1.setTemplate(manager.getWorkflowTemplateByName("MARS").get(0));
        project1.setTemplateModified(Boolean.FALSE);
        project1.setCropType(manager.getCropTypeByName(CropType.MAIZE));
        project1.setLocalDbName("ibdbv2_maize_1_local");
        project1.setCentralDbName("ibdbv2_maize_central");
        project1.setLastOpenDate(new Date(System.currentTimeMillis()));
        manager.addProject(project1);

        Project project2 = new Project();
        project2.setUserId(1);
        project2.setProjectName("Project Name " + new Random().nextInt(10000));
        project2.setStartDate(new Date(System.currentTimeMillis()));
        project2.setTemplate(manager.getWorkflowTemplateByName("MARS").get(0));
        project2.setTemplateModified(Boolean.FALSE);
        project2.setCropType(manager.getCropTypeByName(CropType.MAIZE));
        project2.setLocalDbName("ibdbv2_maize_1_local");
        project2.setCentralDbName("ibdbv2_maize_central");
        project2.setLastOpenDate(new Date(System.currentTimeMillis()));
        manager.addProject(project2);

        User user = manager.getAllUsers().get((manager.getAllUsers().size())-1);

        ProjectUserMysqlAccount recordToSave1 = new ProjectUserMysqlAccount();
        recordToSave1.setProject(project1);
        recordToSave1.setUser(user);
        recordToSave1.setMysqlUsername("sample "+ new Random().nextInt(10000));
        recordToSave1.setMysqlPassword("password");

        ProjectUserMysqlAccount recordToSave2 = new ProjectUserMysqlAccount();
        recordToSave2.setProject(project2);
        recordToSave2.setUser(user);
        recordToSave2.setMysqlUsername("sample" + new Random().nextInt(10000));
        recordToSave2.setMysqlPassword("password");

        List<ProjectUserMysqlAccount> records = new ArrayList<ProjectUserMysqlAccount>();
        records.add(recordToSave1);
        records.add(recordToSave2);
        List<Integer> idsSaved = manager.addProjectUserMysqlAccounts(records);
        Assert.assertNotNull(idsSaved);
        Assert.assertTrue(!idsSaved.isEmpty());

        Debug.println(0, "Ids of records saved:");
        for(Integer id : idsSaved){
            Debug.println(0, id.toString());
        }

        manager.deleteProject(project1);
        manager.deleteProject(project2);
    }

    @Test
    public void testGetProjectUserMysqlAccountByProjectIdAndUserId() throws MiddlewareQueryException {
        //This test assumes that there is a record in workbench_project_user_mysql_account
        //with project id = 1 and user id = 1
        ProjectUserMysqlAccount record = manager.getProjectUserMysqlAccountByProjectIdAndUserId(Integer.valueOf(1), Integer.valueOf(1));
        Assert.assertNotNull(record);
        Debug.println(0, record.toString());
    }

    @Test
    public void testGetProjectBackups() throws MiddlewareQueryException {
        List<ProjectBackup> projectBackups = manager.getProjectBackups();
        Assert.assertNotNull(projectBackups);
        Assert.assertTrue(!projectBackups.isEmpty());
        
        Debug.println(0, "testGetProjectBackups(): ");
        for (ProjectBackup project : projectBackups) {
            Debug.println(0, "  " + project);
        }
    }

   @Test
    public void testAddProjectBackup() throws MiddlewareQueryException {
        ProjectBackup projectBackup = new ProjectBackup();    
        
        //projectBackup.setProjectBackupId(1L);
        projectBackup.setProjectId(1L);
        projectBackup.setBackupPath("target/resource");
        projectBackup.setBackupTime(Calendar.getInstance().getTime());
        // add user
        manager.saveOrUpdateProjectBackup(projectBackup);
        Assert.assertNotNull(projectBackup);
        
        Debug.println(0, "testAddProjectBackup(): " + projectBackup);

    }

    @Test
    public void testGetProjectBackupsByProject() throws MiddlewareQueryException {
        Project project = manager.getProjectById(1L);
        List<ProjectBackup> projectBackups = manager.getProjectBackups(project);
        Assert.assertNotNull(projectBackups);
        Assert.assertTrue(!projectBackups.isEmpty());
        
        for (ProjectBackup backup : projectBackups) {
            Debug.println(0, "  " + backup);
        }
    }

    @Test
    public void testCountAllPersons() throws MiddlewareQueryException {
    	long count = manager.countAllPersons();
    	Assert.assertNotNull(count);
        Debug.println(0, "testCountAllPersons: " + count );
    }

    @Test
    public void testCountAllUsers() throws MiddlewareQueryException {
    	long count = manager.countAllUsers();
    	Assert.assertNotNull(count);
        Debug.println(0, "testCountAllUsers: " + count );
    }

    @Test
    public void testGetAllPersons() throws MiddlewareQueryException {
    	List<Person> results = manager.getAllPersons();
        Assert.assertNotNull(results);
        Assert.assertTrue(!results.isEmpty());
        
        for (Person result : results){
    	   Debug.println(0, result.toString());
       }
    }

    @Test
    public void testGetAllRolesDesc() throws MiddlewareQueryException {
    	List<Role> results = manager.getAllRolesDesc();
    	Assert.assertNotNull(results);
        Assert.assertTrue(!results.isEmpty());
        
        for (Role result : results){
    	   Debug.println(0, result.toString());
       }
    }

    @Test
    public void testGetAllRolesOrderedByLabel() throws MiddlewareQueryException {
    	List<Role> results = manager.getAllRolesOrderedByLabel();
    	Assert.assertNotNull(results);
        Assert.assertTrue(!results.isEmpty());
        
        for (Role result : results){
    	   Debug.println(0, result.toString());
       }
    }

    @Test
    public void testGetAllTools() throws MiddlewareQueryException {
    	List<Tool> results = manager.getAllTools();
    	Assert.assertNotNull(results);
        Assert.assertTrue(!results.isEmpty());
        
        for (Tool result : results) {
    	   Debug.println(0, result.toString());
       }
    }

    @Test
    public void testGetAllUsers() throws MiddlewareQueryException {
    	List<User> results = manager.getAllUsers();
    	Assert.assertNotNull(results);
        Assert.assertTrue(!results.isEmpty());
        
        for (User result : results){
        	Debug.println(0, result.toString());
        }
    }

    @Test
    public void testGetAllUsersSorted() throws MiddlewareQueryException {
    	List<User> results = manager.getAllUsersSorted();
    	Assert.assertNotNull(results);
        Assert.assertTrue(!results.isEmpty());
        
        for (User result : results) {
        	Debug.println(0, result.toString());
       }
    }

    @Test
    public void testGetLastOpenedProject() throws MiddlewareQueryException {
    	Integer userId = manager.getAllUsers().get(0).getUserid();
    	Project results = manager.getLastOpenedProject(userId);
        Assert.assertNotNull(results);
        Debug.println(0, results.toString());
    }

    @Test
    public void testGetPersonById() throws MiddlewareQueryException {
    	int id = manager.getAllPersons().get(0).getId();
    	Person results = manager.getPersonById(id);
        Assert.assertNotNull(results);
        Debug.println(0, results.toString());
    }

    @Test
    public void testGetProjectActivitiesByProjectId() throws MiddlewareQueryException {
    	long projectId = manager.getProjectById(41L).getProjectId();
    	List<ProjectActivity> results = manager.getProjectActivitiesByProjectId(projectId, 1, 50);
    	Assert.assertNotNull(results);
        Assert.assertTrue(!results.isEmpty());
        
        for (ProjectActivity result : results){
        	Debug.println(0, result.toString());
       }
    }

    @Test
    public void testGetProjectLocationMapByProjectId() throws MiddlewareQueryException {
    	long projectId = manager.getProjects().get(0).getProjectId();
    	List<ProjectLocationMap> results = manager.getProjectLocationMapByProjectId(projectId, 1, 50);
    	Assert.assertNotNull(results);
        Assert.assertTrue(!results.isEmpty());
        
        for (ProjectLocationMap result : results){
        	Debug.println(0, result.toString());
       }
    }

    @Test
    public void testGetProjectUserRoleById() throws MiddlewareQueryException {
    	Integer id = manager.getProjects().get(0).getUserId();
    	ProjectUserRole userrole = manager.getProjectUserRoleById(id);
    	Assert.assertNotNull(userrole);
        Debug.println(0, userrole.toString());
    }

    @Test
    public void testGetQuestionsByUserId() throws MiddlewareQueryException {
    	//Integer userId = Integer.valueOf(1); //change the user id
    	Integer userId = manager.getProjects().get(0).getUserId();
    	List<SecurityQuestion> results = manager.getQuestionsByUserId(userId);

    	Assert.assertNotNull(results);
        Assert.assertTrue(!results.isEmpty());
        
        for (SecurityQuestion result : results) {
        	Debug.println(0, result.toString());
        }
    }

    @Test
    public void testGetProjectsList() throws MiddlewareQueryException {
    	List<Project> results = manager.getProjects(0, 100);
    	Assert.assertNotNull(results);
        Assert.assertTrue(!results.isEmpty());
        
        Debug.println(0, "testGetProjectsList(): ");
        for (Project result : results) {
            Debug.println(0, "  " + result);

        }
    }

    @Test
    public void testGetProjectMethodByProject() throws MiddlewareQueryException {
    	Project project = manager.getProjectById(41L);
    	List<ProjectMethod> results = manager.getProjectMethodByProject(project, 0, 100);
    	Assert.assertNotNull(results);
        Assert.assertTrue(!results.isEmpty());
        
        Debug.println(0, "testGetProjectMethodByProject(): ");
        for (ProjectMethod result : results) {
            Debug.println(0, "  " + result);

        }
    }

    @Test
    public void testGetProjectsByUser() throws MiddlewareQueryException {
    	User user = manager.getAllUsers().get(0);
    	List<Project> results = manager.getProjectsByUser(user);
    	Assert.assertNotNull(results);
        Assert.assertTrue(!results.isEmpty());
        
        Debug.println(0, "testGetProjectsByUser(): ");
        for (Project result : results) {
            Debug.println(0, "  " + result);

        }
        Debug.println(0, "Number of record/s: "+results.size());
    }

    @Test
    public void testGetProjectUserRolesByProject() throws MiddlewareQueryException {
    	Project project = manager.getProjects().get(0);
    	List<ProjectUserRole> results = manager.getProjectUserRolesByProject(project);
    	Assert.assertNotNull(results);
        Assert.assertTrue(!results.isEmpty());
        
    	Debug.println(0, "testGetProjectUserRolesByProject(): ");
        for (ProjectUserRole result : results) {
            Debug.println(0, "  " + result);
        }
        Debug.println(0, "Number of record/s: "+results.size());
    }

    @Test
    public void testGetUserById() throws MiddlewareQueryException {
    	int id = 1;
    	User user = manager.getUserById(id);
    	Assert.assertNotNull(user);
    	Debug.println(0, "testGetUserById("+id+"): ");
    	Debug.println(0, user.toString());
    }

    @Test
    public void testGetWorkbenchDatasetById() throws MiddlewareQueryException  {
    	Long datasetId = 1L; //change datasetId value
    	WorkbenchDataset result = manager.getWorkbenchDatasetById(datasetId);
    	Assert.assertNotNull(result);
    	
    	Debug.println(0, "testGetWorkbenchDatasetById("+datasetId+"): ");
    	Debug.println(0, result.toString());
    }

    @Test
    public void testGetWorkbenchRuntimeData() throws MiddlewareQueryException  {
    	WorkbenchRuntimeData result = manager.getWorkbenchRuntimeData();
    	Assert.assertNotNull(result);
    	
    	Debug.println(0, result.toString());
    }

    @Test
    public void testGetWorkbenchSetting() throws MiddlewareQueryException  {
    	WorkbenchSetting result = manager.getWorkbenchSetting();
    	Assert.assertNotNull(result);
    	
    	Debug.println(0, result.toString());
    }

    @Test
    public void testGetWorkflowTemplateByName() throws MiddlewareQueryException  {
    	String name ="Manager";
    	List<WorkflowTemplate> results = manager.getWorkflowTemplateByName(name);
    	Assert.assertNotNull(results);
        Assert.assertTrue(!results.isEmpty());
        
        for (WorkflowTemplate result : results) {
        	Debug.println(0, result.toString());
        }
    }

    @Test
    public void testGetWorkflowTemplatesList() throws MiddlewareQueryException  {
    	List<WorkflowTemplate> results = manager.getWorkflowTemplates();
    	Assert.assertNotNull(results);
        Assert.assertTrue(!results.isEmpty());
        
        for (WorkflowTemplate result : results) {
        	Debug.println(0, result.toString());
        }
        Debug.println(0, "Number of record/s: "+results.size());
    }

    @Test
    public void testDeletePerson() throws MiddlewareQueryException  {
        Person person = manager.getAllPersons().get((manager.getAllPersons().size())-1);

    	manager.deletePerson(person);
        Debug.println(0, "Record is successfully deleted");
    }

    /* TODO: Lets disable this test case for the meantime as the delete fxn left behind by abro differs from the ff transaction, (Please add a separate jira ticket for this) */
    /*
    @Test
    public void testDeleteProject() throws MiddlewareQueryException  {
        Project project = manager.getProjects().get((manager.getProjects().size())-1);

    	manager.deleteProject(project);
        Debug.println(0, "Record is successfully deleted");
    } */

    @Test
    public void testDeleteProjectActivity() throws MiddlewareQueryException  {
    	
        ProjectActivity projectActivity = new ProjectActivity();

        projectActivity.setProject(manager.getProjectById(1L));
        projectActivity.setName("Fieldbook");
        projectActivity.setDescription("Launch FieldBook");
        projectActivity.setUser(manager.getUserById(1));
        projectActivity.setCreatedAt(new Date(System.currentTimeMillis()));

        // add the project activity
        manager.addProjectActivity(projectActivity);
    	manager.deleteProjectActivity(projectActivity);
        Debug.println(0, "Record is successfully deleted");
    }

    @Test
    public void testGetWorkflowTemplates() throws MiddlewareQueryException  {
    	List<WorkflowTemplate> results = manager.getWorkflowTemplates(0, 100);
    	Assert.assertNotNull(results);
        Assert.assertTrue(!results.isEmpty());
        
        for (WorkflowTemplate result : results) {
        	Debug.println(0, result.toString());
        }
        Debug.println(0, "Number of record/s: "+results.size());
    }

    @Test
    public void testGetProjectUserInfoDao() throws MiddlewareQueryException  {
    	ProjectUserInfoDAO results = manager.getProjectUserInfoDao();
    	Assert.assertNotNull(results);
    	
        Debug.println(0, results.toString());

    }

    @Test
    public void testGetToolDao() throws MiddlewareQueryException  {
    	ToolDAO results = manager.getToolDao();
    	Assert.assertNotNull(results);
    	Debug.println(0, results.toString());
    }

    @Test
    public void testGetUserInfo() throws MiddlewareQueryException  {
    	UserInfo results = manager.getUserInfo(manager.getAllUsers().get(0).getUserid());
    	Assert.assertNotNull(results);
        Debug.println(0, results.toString());
    }

    @Test
    public void testGetToolsWithType() throws MiddlewareQueryException  {
    	List<Tool> results = manager.getToolsWithType(ToolType.NATIVE);
    	Assert.assertNotNull(results);
        Assert.assertTrue(!results.isEmpty());
        
        for (Tool result : results) {
        	Debug.println(0, result.toString());
        }
        Debug.println(0, "Number of record/s: "+results.size());
    }

    @Test
    public void testGetBreedingMethodIdsByWorkbenchProjectId() throws MiddlewareQueryException {
        Integer projectId = 1;
        List<Integer> ids = manager.getBreedingMethodIdsByWorkbenchProjectId(projectId);
        Debug.println(0, "testGetBreedingMethodIdsByWorkbenchProjectId(projectId=" + projectId + "): " + ids.size());
        for (Integer id : ids){
            Debug.println(0, " ID = " + id);
        }
    }

    @Test
    public void testSaveProject() throws MiddlewareQueryException {
      Project project1 = new Project();

      project1.setProjectId((long)manager.getProjects().get(manager.getProjects().size()-1).getProjectId()+1);
      project1.setUserId(1);
      project1.setProjectName("Project Name " + new Random().nextInt(10000));
      project1.setStartDate(new Date(System.currentTimeMillis()));
      project1.setTemplate(manager.getWorkflowTemplateByName("MARS").get(0));
      project1.setTemplateModified(Boolean.FALSE);
      project1.setCropType(manager.getCropTypeByName(CropType.MAIZE));
      project1.setLocalDbName("ibdbv2_maize_1_local");
      project1.setCentralDbName("ibdbv2_maize_central");
      project1.setLastOpenDate(new Date(System.currentTimeMillis()));

      Project project2 = new Project();
      project2.setProjectId((long)manager.getProjects().get(manager.getProjects().size()-1).getProjectId()+2);
      project2.setUserId(1);
      project2.setProjectName("Project Name " + new Random().nextInt(10000));
      project2.setStartDate(new Date(System.currentTimeMillis()));
      project2.setTemplate(manager.getWorkflowTemplateByName("MARS").get(0));
      project2.setTemplateModified(Boolean.FALSE);
      project2.setCropType(manager.getCropTypeByName(CropType.MAIZE));
      project2.setLocalDbName("ibdbv2_maize_1_local");
      project2.setCentralDbName("ibdbv2_maize_central");
      project2.setLastOpenDate(new Date(System.currentTimeMillis()));

      WorkflowTemplate marsTemplate = new WorkflowTemplate();
      marsTemplate.setTemplateId(1L);

      project1.setTemplate(marsTemplate);
      project2.setTemplate(marsTemplate);

      manager.saveOrUpdateProject(project1);
      manager.saveOrUpdateProject(project2);
      
      // Adding Project Locations
      List<ProjectLocationMap> projectLocationMapList = new ArrayList<ProjectLocationMap>();

      ProjectLocationMap projectLocationMap1 = new ProjectLocationMap();
      projectLocationMap1.setProject(manager.getProjects().get(manager.getProjects().size()-2));
      projectLocationMap1.setLocationId(Long.valueOf(3));

      ProjectLocationMap projectLocationMap2 = new ProjectLocationMap();
      projectLocationMap2.setProject(manager.getProjects().get(manager.getProjects().size()-1));
      projectLocationMap2.setLocationId(Long.valueOf(4));

      projectLocationMapList.add(projectLocationMap1);
      projectLocationMapList.add(projectLocationMap2);

      projectLocationMapList.add(projectLocationMap1);
      projectLocationMapList.add(projectLocationMap2);

      manager.addProjectLocationMap(projectLocationMapList);
      
      // Adding Project Method
      List<ProjectMethod> projectMethodList = new ArrayList<ProjectMethod>();

      ProjectMethod projectMethod1 = new ProjectMethod();
      projectMethod1.setProject(manager.getProjects().get(manager.getProjects().size()-2));
      projectMethod1.setMethodId(5);

      ProjectMethod projectMethod2 = new ProjectMethod();
      projectMethod2.setProject(manager.getProjects().get(manager.getProjects().size()-1));
      projectMethod2.setMethodId(6);

      projectMethodList.add(projectMethod1);
      projectMethodList.add(projectMethod2);

      manager.addProjectMethod(projectMethodList);

      // Adding Project Activity
      List<ProjectActivity> projectActivityList = new ArrayList<ProjectActivity>();

      ProjectActivity projectActivity1 = new ProjectActivity();
      projectActivity1.setProject(manager.getProjects().get(manager.getProjects().size()-2));
      projectActivity1.setName("Activity 1");
      projectActivity1.setDescription("Test Description for Activity 1");
      projectActivity1.setCreatedAt(new Date(System.currentTimeMillis()));
      projectActivity1.setUser(manager.getUserById(Integer.valueOf(1)));

      ProjectActivity projectActivity2 = new ProjectActivity();
      projectActivity2.setProject(manager.getProjects().get(manager.getProjects().size()-1));
      projectActivity2.setName("Activity 2");
      projectActivity2.setDescription("Test Description for Activity 2");
      projectActivity2.setCreatedAt(new Date(System.currentTimeMillis()));
      projectActivity2.setUser(manager.getUserById(Integer.valueOf(1)));

      projectActivityList.add(projectActivity1);
      projectActivityList.add(projectActivity2);

      manager.addProjectActivity(projectActivityList);

      Debug.println(0, "  " + project1);
      Debug.println(0, "  " + project2);

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
            Debug.println(0, "Error in deleteProject(): " + e.getMessage());
        }

    }

    @SuppressWarnings("unused")
    private void deleteAllProjects() throws MiddlewareQueryException {
        List<Project> projects = manager.getProjects();
        for (Project project : projects) {
            deleteProject(project);
        }
    }
    

    @Test
    public void testGetTemplateSettings() throws MiddlewareQueryException {
        
        TemplateSetting templateSetting = createTemplateSetting();

        manager.addTemplateSetting(templateSetting);
        Debug.println(3, "Added TemplateSetting: " + templateSetting);
        
        Integer projectId = templateSetting.getProjectId();
        String name = templateSetting.getName();
        Tool tool = templateSetting.getTool();
        String configuration = templateSetting.getConfiguration();
        Boolean isDefault = templateSetting.isDefault();

        getTemplateSetting("project_id, name, tool, configuration", new TemplateSetting(null, projectId, name, tool, configuration, null));
        getTemplateSetting("project_id, tool, name", new TemplateSetting(null, projectId, name, tool, null, null));
        getTemplateSetting("project_id, tool, configuration", new TemplateSetting(null, projectId, null, tool, configuration, null));
        getTemplateSetting("project_id, tool, isDefault", new TemplateSetting(null, projectId, null, tool, null, isDefault));
        getTemplateSetting("name, tool, configuration", new TemplateSetting(null, null, name, tool, configuration, null));
        
        manager.deleteTemplateSetting(templateSetting);
        Debug.println(3, "Database cleanup: templateSetting deleted.");


    }
    
    private void getTemplateSetting(String filterDescription, TemplateSetting templateSettingFilter) throws MiddlewareQueryException{

        List<TemplateSetting> settings = manager.getTemplateSettings(templateSettingFilter);
        
        assertTrue(settings.size() > 0);
        
        Debug.println(3, "Retrieve records by " + filterDescription + ": #records = " + settings.size());
        for (TemplateSetting setting : settings){
            Debug.println(6, setting.toString());
        }

    }
    
    
    @Test
    public void testAddAndDeleteTemplateSettings() throws MiddlewareQueryException {

        TemplateSetting templateSetting = createTemplateSetting();

        manager.addTemplateSetting(templateSetting);
        Debug.println(3, "testAddTemplateSettings: " + templateSetting);
        
        assertNotNull(templateSetting.getTemplateSettingId());
        
        manager.deleteTemplateSetting(templateSetting);
        Debug.println(3, "testDeleteTemplateSettings: " + templateSetting);
    }
    

    
    @Test
    public void testUpdateTemplateSettings() throws MiddlewareQueryException {

        TemplateSetting templateSetting = createTemplateSetting();

        manager.addTemplateSetting(templateSetting);
        Debug.println(3, "TemplateSetting added: " + templateSetting);
        
        assertNotNull(templateSetting.getTemplateSettingId());
        
        templateSetting.setIsDefault(!templateSetting.isDefault());
        templateSetting.setName(templateSetting.getName() + (int) (Math.random() * 100));
        
        manager.updateTemplateSetting(templateSetting);
        Debug.println(3, "TemplateSetting updated: " + templateSetting);
        
        manager.deleteTemplateSetting(templateSetting);
        Debug.println(3, "Database cleanup: templateSetting deleted.");

    }
    
    
    private TemplateSetting createTemplateSetting() throws MiddlewareQueryException{
        Integer templateSettingId = null;
        Integer projectId = -1; 
        Tool tool = manager.getToolWithName("nursery_manager_fieldbook_web"); 
        String name = "S9801-PLOT DATA_" + (int) (Math.random() * 1000); 
        String configuration = (new StringBuffer("<?xml version=\"1.0\"?>")
                .append("<dataset>")
                .append("<name>").append(name).append("</name>                        ")
                .append("<description>PLOT DATA FOR STUDY 1 OF 1998</description>  ")
                .append("<condition role=\"Study Information\" datatype=\"Character Variable\">")
                .append("<name>PI</name>")
                .append("<description>PRINCIPAL INVESTIGATOR</description>")
                .append("<property>PERSON</property>")
                .append("<method>ASSIGNED</method>")
                .append("        <scale>DBCV</scale>")
                .append("    </condition>")
                .append("    <factor role=\"Trial design information\" datatype=\"Numeric variable\">")
                .append("        <name>PLOT</name>")
                .append("        <description>PLOT NUMBER</description>")
                .append("        <property>PLOT NUMBER</property>")
                .append("        <method>ENUMERATED</method>")
                .append("        <scale>NUMBER</scale>")
                .append("        <nestedin></nestedin>")
                .append("    </factor>")
                .append("    <variate role=\"Observational variate\" datatype=\"Numeric variable\">")
                .append("        <name>YIELD</name>")
                .append("        <description>GRAIN YIELD</description>")
                .append("        <property>GRAIN YIELD</property>")
                .append("        <method>PADDY RICE</method>")
                .append("        <scale>kg/ha</scale>")
                .append("        <samplelevel>PLOT</samplelevel>")
                .append("    </variate>")
                .append("</dataset>")).toString();
        Boolean isDefault = false;
        
        return new TemplateSetting(templateSettingId, projectId, name, tool, configuration, isDefault);
    }

    

    @AfterClass
    public static void tearDown() throws Exception {
        hibernateUtil.shutdown();
    }
}
