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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.generationcp.middleware.MiddlewareIntegrationTest;
import org.generationcp.middleware.dao.ProjectUserInfoDAO;
import org.generationcp.middleware.dao.ToolConfigurationDAO;
import org.generationcp.middleware.dao.ToolDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionPerThreadProvider;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.presets.StandardPreset;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.IbdbUserMap;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectActivity;
import org.generationcp.middleware.pojos.workbench.ProjectBackup;
import org.generationcp.middleware.pojos.workbench.ProjectUserInfo;
import org.generationcp.middleware.pojos.workbench.ProjectUserMysqlAccount;
import org.generationcp.middleware.pojos.workbench.ProjectUserRole;
import org.generationcp.middleware.pojos.workbench.Role;
import org.generationcp.middleware.pojos.workbench.TemplateSetting;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.ToolConfiguration;
import org.generationcp.middleware.pojos.workbench.ToolType;
import org.generationcp.middleware.pojos.workbench.UserInfo;
import org.generationcp.middleware.pojos.workbench.WorkbenchDataset;
import org.generationcp.middleware.pojos.workbench.WorkbenchRuntimeData;
import org.generationcp.middleware.pojos.workbench.WorkflowTemplate;
import org.generationcp.middleware.utils.test.Debug;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNull;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class WorkbenchDataManagerImplTest extends MiddlewareIntegrationTest {

    private static WorkbenchDataManagerImpl manager;
    
    private static Project commonTestProject;
    private static User testUser1, testUser2;
    private static Person testPerson1, testPerson2;
    private static ProjectActivity testProjectActivity1, testProjectActivity2;

    @BeforeClass
    public static void setUp() throws MiddlewareQueryException  {
        HibernateSessionProvider sessionProvider = new HibernateSessionPerThreadProvider(workbenchSessionUtil.getSessionFactory());
        manager = new WorkbenchDataManagerImpl(sessionProvider);        

        testPerson1 = createTestPersonData();
        manager.addPerson(testPerson1);
        testPerson2 = createTestPersonData();
        manager.addPerson(testPerson2);
        
        testUser1 = createTestUserData();
        testUser1.setPersonid(testPerson1.getId());
        manager.addUser(testUser1);
        testUser2 = createTestUserData();
        testUser2.setPersonid(testPerson2.getId());
        manager.addUser(testUser2);
        
        commonTestProject = createTestProjectData();
        commonTestProject.setUserId(testUser1.getUserid());
        manager.addProject(commonTestProject);
        
        testProjectActivity1 = createTestProjectActivityData(commonTestProject, testUser1);
        manager.addProjectActivity(testProjectActivity1);
        
        testProjectActivity2 = createTestProjectActivityData(commonTestProject, testUser2);
        manager.addProjectActivity(testProjectActivity2);
        
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(3);
        userInfo.setLoginCount(5);
        manager.insertOrUpdateUserInfo(userInfo);
        
    	ProjectUserInfo pui =  new ProjectUserInfo();
    	pui.setProjectId(new Integer(Integer.parseInt(commonTestProject.getProjectId().toString())));
    	pui.setUserId(commonTestProject.getUserId());
    	pui.setLastOpenDate(new Date());
    	manager.saveOrUpdateProjectUserInfo(pui);
        
        WorkbenchRuntimeData workbenchRuntimeData = new WorkbenchRuntimeData();
        workbenchRuntimeData.setUserId(1);
        manager.updateWorkbenchRuntimeData(workbenchRuntimeData);
        
    }
    
    @AfterClass
    public static void tearDown() throws MiddlewareQueryException {

    }
  
    @Test
    public void testAddUser() throws MiddlewareQueryException {
        User user = createTestUserData();
        Integer result = manager.addUser(user);
        Assert.assertNotNull("Expected id of a newly saved record in workbench_user.", result);
        
        User readUser = manager.getUserById(result);
        Assert.assertEquals(user.getName(), readUser.getName());
        manager.deleteUser(readUser);
    }

	private static User createTestUserData() {
		User user = new User();
        user.setInstalid(-1);
        user.setStatus(-1);
        user.setAccess(-1);
        user.setType(-1);
        user.setName("user_test" + new Random().nextInt());
        user.setPassword("user_password");
        user.setPersonid(1);
        user.setAdate(20120101);
        user.setCdate(20120101);
		return user;
	}

    @Test
    public void testAddPerson() throws MiddlewareQueryException {
        Person person = createTestPersonData();
        Integer result = manager.addPerson(person);
        Assert.assertNotNull("Expected id of a newly saved record in persons.", result);
        
        Person readPerson = manager.getPersonById(result);
        Assert.assertEquals(person.getLastName(), readPerson.getLastName());  
        manager.deletePerson(readPerson);
    }

	private static Person createTestPersonData() {
		Person person = new Person();
        person.setInstituteId(1);
        person.setFirstName("Test");
        person.setMiddleName("M");
        person.setLastName("Person " + new Random().nextInt());
        person.setPositionName("King of Icewind Dale");
        person.setTitle("His Highness");
        person.setExtension("Ext");
        person.setFax("Fax");
        person.setEmail("lichking@blizzard.com");
        person.setNotes("notes");
        person.setContact("Contact");
        person.setLanguage(-1);
        person.setPhone("Phone");
		return person;
	}

    @Test
    public void testAddProject() throws MiddlewareQueryException {
        Project project = createTestProjectData();
        manager.addProject(project);
        Assert.assertNotNull("Expected id of a newly saved record in workbench_project.", project.getProjectId());
        manager.deleteProject(project);
    }

	private static Project createTestProjectData() throws MiddlewareQueryException {
		Project project = new Project();
        project.setUserId(1);
        project.setProjectName("Test Project " + new Random().nextInt(10000));
        project.setStartDate(new Date(System.currentTimeMillis()));
        project.setTemplate(manager.getWorkflowTemplateByName("MARS").get(0));
        project.setTemplateModified(Boolean.FALSE);
        project.setCropType(manager.getCropTypeByName(CropType.RICE));
        project.setLocalDbName("ibdbv2_rice_1_local");
        project.setCentralDbName("ibdbv2_rice_central");
        project.setLastOpenDate(new Date(System.currentTimeMillis()));
		return project;
	}

    @Test
    public void testAddProjectActivity() throws MiddlewareQueryException {
        
    	ProjectActivity projectActivity = createTestProjectActivityData(commonTestProject, testUser1);

        Integer result = manager.addProjectActivity(projectActivity);
        Assert.assertNotNull("Expected id of a newly saved record in workbench_project_activity", result);
        
        manager.deleteProjectActivity(projectActivity);
    }

    @Test
    public void testAddIbdbUserMap() throws MiddlewareQueryException {
    	IbdbUserMap userMap = new IbdbUserMap();
    	userMap.setProjectId(commonTestProject.getProjectId());
        userMap.setIbdbUserId(testUser1.getUserid() * -1);
    	userMap.setWorkbenchUserId(testUser1.getUserid());

    	Integer result = manager.addIbdbUserMap(userMap);
        Assert.assertNotNull("Expected id of a newly saved record in workbench_ibdb_user_map", result);
    }
    
    @Test
    public void testGetProjects() throws MiddlewareQueryException {
        List<Project> projects = manager.getProjects();
        Assert.assertNotNull(projects);
        Assert.assertTrue(!projects.isEmpty());
        Debug.printObjects(INDENT, projects);
    }

    @Test
    public void testGetToolWithName() throws MiddlewareQueryException {
        String toolName = "fieldbook_web";
        Tool tool = manager.getToolWithName(toolName);
        Assert.assertNotNull(tool);
        Debug.println(INDENT, "testGetToolWithName(" + toolName + "): " + tool);
    }

    @Test
    public void testGetProjectByName() throws MiddlewareQueryException {
        Project project = manager.getProjectByName(commonTestProject.getProjectName());
        Assert.assertEquals(commonTestProject.getProjectName(), project.getProjectName());
    }

    @Test
    public void testGetUserByName() throws MiddlewareQueryException {
        User user = (User) manager.getUserByName(testUser1.getName(), 0, 1, Operation.EQUAL).get(0);
        Assert.assertEquals(testUser1.getName(), user.getName());
        Assert.assertEquals(testUser1.getUserid(), user.getUserid());
    }

    @Test
    public void testAddWorkbenchDataset() throws MiddlewareQueryException {
        WorkbenchDataset dataset = createTestWorkbenchDataset(commonTestProject);
        Integer result = manager.addWorkbenchDataset(dataset);
        Assert.assertNotNull("Expected id of the newly added record in workbench_dataset", result);
        manager.deleteWorkbenchDataset(dataset);
    }

	private WorkbenchDataset createTestWorkbenchDataset(Project project) {
		WorkbenchDataset dataset = new WorkbenchDataset();
        dataset.setName("Test Dataset" + new Random().nextInt());
        dataset.setDescription("Test Dataset Description");
        dataset.setCreationDate(new Date(System.currentTimeMillis()));
        dataset.setProject(project);
		return dataset;
	}

	@Test
	public void testGetWorkbenchDatasetByProjectId() throws MiddlewareQueryException {
		WorkbenchDataset dataset = createTestWorkbenchDataset(commonTestProject);
		manager.addWorkbenchDataset(dataset);

		List<WorkbenchDataset> list = manager.getWorkbenchDatasetByProjectId(commonTestProject.getProjectId(), 0, 10);
		Assert.assertTrue(list.contains(dataset));
		manager.deleteWorkbenchDataset(dataset);
	}

    @Test
    public void testCountWorkbenchDatasetByProjectId() throws MiddlewareQueryException {
        WorkbenchDataset dataset1 = createTestWorkbenchDataset(commonTestProject);
        manager.addWorkbenchDataset(dataset1);
        
        WorkbenchDataset dataset2 = createTestWorkbenchDataset(commonTestProject);
        manager.addWorkbenchDataset(dataset2);
        
        long result = manager.countWorkbenchDatasetByProjectId(commonTestProject.getProjectId());
        Assert.assertEquals(2, result);
        
        manager.deleteWorkbenchDataset(dataset1);
        manager.deleteWorkbenchDataset(dataset2);
    }

    @Test
    public void testGetWorkbenchDatasetByName() throws MiddlewareQueryException {
        String name = "D";
        List<WorkbenchDataset> list = manager.getWorkbenchDatasetByName(name, Operation.EQUAL, 0, 10);
        Debug.println(INDENT, "testGetWorkbenchDatasetByName(name=" + name + "): ");

        if (list.isEmpty()) {
            Debug.println(INDENT, "  No records found.");
        }

        for (WorkbenchDataset d : list) {
            Debug.println(INDENT, d.getDatasetId() + ": " + d.getName());
        }
    }

    @Test
    public void testCountWorkbenchDatasetByName() throws MiddlewareQueryException {
        String name = "a";
        long result = manager.countWorkbenchDatasetByName(name, Operation.EQUAL);
        Debug.println(INDENT, "testCountWorkbenchDatasetByName(name=" + name + "): " + result);
    }
    
    @Test
    public void testAddProjectUserRoles() throws MiddlewareQueryException {
        
        Role role1 = manager.getAllRoles().get(0);
        Role role2 = manager.getAllRoles().get(1);

        ProjectUserRole projUsrRole1 = new ProjectUserRole(commonTestProject, testUser1, role1);
        ProjectUserRole projUsrRole2 = new ProjectUserRole(commonTestProject, testUser1, role2);

        List<ProjectUserRole> projectUserRoles = new ArrayList<ProjectUserRole>();
		projectUserRoles.add(projUsrRole1);
		projectUserRoles.add(projUsrRole2);

        List<Integer> rolesAdded = manager.addProjectUserRole(projectUserRoles);
        Assert.assertEquals(2, rolesAdded.size());

        Debug.println(INDENT, "testAddProjectUsers(projectId=" + commonTestProject.getProjectId() + ") ADDED: " + rolesAdded.size());
        
        long result = manager.countUsersByProjectId(commonTestProject.getProjectId());
        Assert.assertEquals(1, result);
        
    }

    @Test
    public void testGetUsersByProjectId() throws MiddlewareQueryException {
        List<User> users = manager.getUsersByProjectId(commonTestProject.getProjectId());
        Assert.assertNotNull(users);
        Assert.assertEquals(1, users.size());
        Assert.assertEquals(testUser1, users.get(0));
    }

    @Test
    public void testGetActivitiesByProjectId() throws MiddlewareQueryException {
        List<ProjectActivity> results = manager.getProjectActivitiesByProjectId(commonTestProject.getProjectId(), 0, 10);
        Assert.assertNotNull(results);
        Assert.assertEquals(2, results.size());
    }
    
    @Test
    public void testCountActivitiesByProjectId() throws MiddlewareQueryException {
        long result = manager.countProjectActivitiesByProjectId(commonTestProject.getProjectId());
        Assert.assertEquals(2, result);
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
        Debug.println(INDENT, "testAddToolConfiguration(toolId=" + toolId + "): " + toolConfig);

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
            dao.setSession(workbenchSessionUtil.getCurrentSession());
            ToolConfiguration result = dao.getById(toolId, false);

            Debug.println(INDENT, "testUpdateToolConfiguration(toolId=" + toolId + "): ");
            Debug.println(INDENT, "  FROM: " + oldToolConfigValue);
            Debug.println(INDENT, "    TO: " + result);
        } else {
            Debug.println(INDENT, "testUpdateToolConfiguration(toolId=" + toolId + "): Tool configuration not found.");
        }
    }

    @Test
    public void testDeleteToolConfiguration() throws MiddlewareQueryException {
        Long toolId = 1L;
        ToolConfiguration toolConfig = manager.getToolConfigurationByToolIdAndConfigKey(toolId, "5th key");

        if (toolConfig != null){
            manager.deleteToolConfiguration(toolConfig);
            ToolConfigurationDAO dao = new ToolConfigurationDAO();
            dao.setSession(workbenchSessionUtil.getCurrentSession());
            ToolConfiguration result = dao.getById(toolId, false);

            Debug.println(INDENT, "testDeleteToolConfiguration(toolId=" + toolId + "): " + result);
        } else {
            Debug.println(INDENT, "testDeleteToolConfiguration(toolId=" + toolId + "): Tool Configuration not found");
        }
    }

    @Test
    public void testGetListOfToolConfigurationsByToolId() throws MiddlewareQueryException {
        Long toolId = 1L;
        List<ToolConfiguration> result = manager.getListOfToolConfigurationsByToolId(toolId);
        Debug.println(INDENT, "testGetListOfToolConfigurationsByToolId(" + toolId + "): ");

        if (result.isEmpty()) {
            Debug.println(INDENT, "  No records found.");
        } else {
            for (ToolConfiguration t : result) {
                Debug.println(INDENT, t);
            }
        }
    }

    @Test
    public void testGetToolConfigurationByToolIdAndConfigKey() throws MiddlewareQueryException {
        Long toolId = 1L;
        String configKey = "test";
        ToolConfiguration toolConfig = manager.getToolConfigurationByToolIdAndConfigKey(toolId, configKey);
        Debug.println(INDENT, "testGetToolConfigurationByToolIdAndConfigKey(toolId=" + toolId 
                + ", configKey=" + configKey + "): " + toolConfig);
    }

    @Test
    public void testAddCropType() throws MiddlewareQueryException {
        CropType cropType = new CropType("Coconut");
        
        try{
            String added = manager.addCropType(cropType);
            Assert.assertNotNull(added);            
            Debug.println(INDENT, "testAddCropType(" + cropType + "): records added = " + added);
        }catch(MiddlewareQueryException e){
            if (e.getMessage().equals("Crop type already exists.")){
                Debug.println(INDENT, e.getMessage());
            } else {
                throw e;
            }
        }
    }

    @Test
    public void testGetInstalledCentralCrops() throws MiddlewareQueryException {
        ArrayList<CropType> cropTypes = (ArrayList<CropType>) manager.getInstalledCentralCrops();
        Assert.assertNotNull(cropTypes);
        Debug.println(INDENT, "testGetInstalledCentralCrops(): " + cropTypes);
    }

    @Test
    public void testGetCropTypeByName() throws MiddlewareQueryException {
        String cropName = CropType.CHICKPEA;
        CropType cropType = manager.getCropTypeByName(cropName);
        Assert.assertNotNull(cropName);      
        Debug.println(INDENT, "testGetCropTypeByName(" + cropName + "): " + cropType);
    }

    @Test
    public void testGetLocalIbdbUserId() throws MiddlewareQueryException {
        Integer localIbdbUserId = manager.getLocalIbdbUserId(testUser1.getUserid(), commonTestProject.getProjectId());
        Assert.assertNotNull(localIbdbUserId); 
        Debug.println(INDENT, "testGetLocalIbdbUserId(workbenchUserId=" + testUser1.getUserid() + ", projectId=" + commonTestProject.getProjectId() + "): "
                + localIbdbUserId);
    }

    @Test
    public void testGetRoleById() throws MiddlewareQueryException {
        Integer id = Integer.valueOf(1); // Assumption: there is a role with id 1
        Role role = manager.getRoleById(id);
        Assert.assertNotNull(role); 
        Debug.println(INDENT, "testGetRoleById(id=" + id + "): \n  " + role);
    }

    @Test
    public void testGetRoleByNameAndWorkflowTemplate() throws MiddlewareQueryException {
        String templateName = "MARS";
        String roleName = "MARS Breeder";
        WorkflowTemplate workflowTemplate = manager.getWorkflowTemplateByName(templateName).get(0);
        Role role = manager.getRoleByNameAndWorkflowTemplate(roleName, workflowTemplate);
        Assert.assertNotNull(role); 
        Debug.println(INDENT, "testGetRoleByNameAndWorkflowTemplate(name=" + roleName 
                + ", workflowTemplate=" + workflowTemplate.getName() + "): \n  " + role);
    }

    @Test
    public void testGetRolesByWorkflowTemplate() throws MiddlewareQueryException {
        WorkflowTemplate workflowTemplate = manager.getWorkflowTemplates().get(0); // get the first template in the db
        List<Role> roles = manager.getRolesByWorkflowTemplate(workflowTemplate);
        Assert.assertNotNull(roles); 
        Assert.assertTrue(!roles.isEmpty());
        Debug.println(INDENT, "testGetRolesByWorkflowTemplate(workflowTemplate=" + workflowTemplate.getName() + "): " + roles.size());
        for (Role role: roles){
            Debug.println(INDENT, "  "+role);
        }
    }

    @Test
    public void testGetWorkflowTemplateByRole() throws MiddlewareQueryException {
        Role role = manager.getRoleById(manager.getAllRoles().get(0).getRoleId());
        WorkflowTemplate template = manager.getWorkflowTemplateByRole(role);
        Assert.assertNotNull(template);
        Debug.println(INDENT, "testGetWorkflowTemplateByRole(role=" + role.getName() + "): \n  " + template);
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
            Debug.println(INDENT, "testGetRoleByProjectAndUser(project=" + project.getProjectName() 
                    + ", user=" + user.getName() + "): \n  " + roles);
        } else {
            Debug.println(INDENT, "testGetRoleByProjectAndUser(project=" + project.getProjectName() 
                    + "): Error in data - Project has no users. ");
        }
    }

    @Test
    public void testGetAllRoles() throws MiddlewareQueryException {
        List<Role> roles = manager.getAllRoles();
        Assert.assertNotNull(roles);
        Assert.assertTrue(!roles.isEmpty());
        
        for(Role role : roles) {
            Debug.println(INDENT, role.toString());
        }
    }

    @Test
    public void testAddProjectUserMysqlAccount() throws MiddlewareQueryException {
    	ProjectUserMysqlAccount recordToSave = new ProjectUserMysqlAccount();
        recordToSave.setProject(commonTestProject);
        recordToSave.setUser(testUser1);
        recordToSave.setMysqlUsername("sample " + new Random().nextInt(10000));
        recordToSave.setMysqlPassword("password");

        Integer idSaved = manager.addProjectUserMysqlAccount(recordToSave);
        Assert.assertNotNull("Expected id of the newly saved record in workbench_project_user_mysql_account", idSaved);
        Debug.println(INDENT, "Id of record saved: " + idSaved);
        
    }

    @Test
    public void testGetProjectUserMysqlAccountByProjectIdAndUserId() throws MiddlewareQueryException {
        ProjectUserMysqlAccount record = manager.getProjectUserMysqlAccountByProjectIdAndUserId(
                commonTestProject.getProjectId().intValue(), testUser1.getUserid());
        Assert.assertNotNull(record);
        Assert.assertEquals(Long.valueOf(commonTestProject.getProjectId()), new Long(record.getProject().getProjectId()));
        Assert.assertEquals(testUser1.getUserid(), record.getUser().getUserid());
        Debug.println(INDENT, record.toString());
    }

    @Test
    public void testGetProjectBackups() throws MiddlewareQueryException {
        List<ProjectBackup> projectBackups = manager.getProjectBackups();
        Assert.assertNotNull(projectBackups);
        Assert.assertTrue(!projectBackups.isEmpty());
        
        Debug.println(INDENT, "testGetProjectBackups(): ");
        for (ProjectBackup project : projectBackups) {
            Debug.println(INDENT, project);
        }
    }

    @Test
    public void testAddProjectBackup() throws MiddlewareQueryException {
        ProjectBackup projectBackup = new ProjectBackup();    
        projectBackup.setProjectId(commonTestProject.getProjectId());
        projectBackup.setBackupPath("target/resource" + commonTestProject.getProjectId());
        projectBackup.setBackupTime(Calendar.getInstance().getTime());

        manager.saveOrUpdateProjectBackup(projectBackup);
        Assert.assertNotNull(projectBackup);
    }

    @Test
    public void testGetProjectBackupsByProject() throws MiddlewareQueryException {
        List<ProjectBackup> projectBackups = manager.getProjectBackups(commonTestProject);
        Assert.assertNotNull(projectBackups);
        Assert.assertTrue(!projectBackups.isEmpty());
        
        for (ProjectBackup backup : projectBackups) {
            Debug.println(INDENT, backup);
        }
    }

    @Test
    public void testCountAllPersons() throws MiddlewareQueryException {
    	long count = manager.countAllPersons();
    	Assert.assertNotNull(count);
        Debug.println(INDENT, "testCountAllPersons: " + count );
    }

    @Test
    public void testCountAllUsers() throws MiddlewareQueryException {
    	long count = manager.countAllUsers();
    	Assert.assertNotNull(count);
        Debug.println(INDENT, "testCountAllUsers: " + count );
    }

    @Test
    public void testGetAllPersons() throws MiddlewareQueryException {
    	List<Person> results = manager.getAllPersons();
        Assert.assertNotNull(results);
        Assert.assertTrue(!results.isEmpty());
        
        for (Person result : results){
    	   Debug.println(INDENT, result.toString());
       }
    }

    @Test
    public void testGetAllRolesDesc() throws MiddlewareQueryException {
    	List<Role> results = manager.getAllRolesDesc();
    	Assert.assertNotNull(results);
        Assert.assertTrue(!results.isEmpty());
        
        for (Role result : results){
    	   Debug.println(INDENT, result.toString());
       }
    }

    @Test
    public void testGetAllRolesOrderedByLabel() throws MiddlewareQueryException {
    	List<Role> results = manager.getAllRolesOrderedByLabel();
    	Assert.assertNotNull(results);
        Assert.assertTrue(!results.isEmpty());
        
        for (Role result : results){
    	   Debug.println(INDENT, result.toString());
       }
    }

    @Test
    public void testGetAllTools() throws MiddlewareQueryException {
    	List<Tool> results = manager.getAllTools();
    	Assert.assertNotNull(results);
        Assert.assertTrue(!results.isEmpty());
        
        for (Tool result : results) {
    	   Debug.println(INDENT, result.toString());
       }
    }

    @Test
    public void testGetAllUsers() throws MiddlewareQueryException {
    	List<User> results = manager.getAllUsers();
    	Assert.assertNotNull(results);
        Assert.assertTrue(!results.isEmpty());
        
        for (User result : results){
        	Debug.println(INDENT, result.toString());
        }
    }

    @Test
    public void testGetAllUsersSorted() throws MiddlewareQueryException {
    	List<User> results = manager.getAllUsersSorted();
    	Assert.assertNotNull(results);
        Assert.assertTrue(!results.isEmpty());
        
        for (User result : results) {
        	Debug.println(INDENT, result.toString());
       }
    }

    @Test
    public void testGetLastOpenedProject() throws MiddlewareQueryException {
    	Project results = manager.getLastOpenedProject(testUser1.getUserid());
        Assert.assertNotNull(results);
        Debug.println(INDENT, results.toString());
    }

	private static ProjectActivity createTestProjectActivityData(Project project, User user) {
		ProjectActivity projectActivity = new ProjectActivity();
        projectActivity.setProject(project);
        projectActivity.setName("Project Activity" + new Random().nextInt());
        projectActivity.setDescription("Some project activity");
        projectActivity.setUser(user);
        projectActivity.setCreatedAt(new Date(System.currentTimeMillis()));
		return projectActivity;
	}

    @Test
    public void testGetProjectsList() throws MiddlewareQueryException {
    	List<Project> results = manager.getProjects(0, 100);
    	Assert.assertNotNull(results);
        Assert.assertTrue(!results.isEmpty());
        Debug.printObjects(INDENT, results);
    }

    

    @Test
    public void testGetProjectsByUser() throws MiddlewareQueryException {
    	List<Project> results = manager.getProjectsByUser(testUser1);
    	Assert.assertNotNull(results);
        Assert.assertTrue(!results.isEmpty());
        Debug.printObjects(INDENT, results);
    }

    @Test
    public void testGetProjectUserRolesByProject() throws MiddlewareQueryException {
    	List<ProjectUserRole> results = manager.getProjectUserRolesByProject(commonTestProject);
    	Assert.assertNotNull(results);
        Assert.assertTrue(!results.isEmpty());
        Debug.printObjects(INDENT, results);
    }

    @Test
    public void testGetUserById() throws MiddlewareQueryException {
    	User user = manager.getUserById(testUser1.getUserid());
    	Assert.assertNotNull(user);
    	Debug.println(INDENT, user.toString());
    }

    @Test
    public void testGetWorkbenchDatasetById() throws MiddlewareQueryException  {
    	WorkbenchDataset testDataset = createTestWorkbenchDataset(commonTestProject);
        Integer result = manager.addWorkbenchDataset(testDataset);
               
    	WorkbenchDataset readDataset = manager.getWorkbenchDatasetById(new Long(result));
    	Assert.assertNotNull(readDataset);
    	Assert.assertEquals(testDataset, readDataset);
    	manager.deleteWorkbenchDataset(testDataset);    	
    }

    @Test
    public void testGetWorkbenchRuntimeData() throws MiddlewareQueryException  {
    	WorkbenchRuntimeData result = manager.getWorkbenchRuntimeData();
    	Assert.assertNotNull(result);
    	Debug.println(INDENT, result.toString());
    }

//     FIXME : Seed directly to DB
//    @Test
//    public void testGetWorkbenchSetting() throws MiddlewareQueryException  {
//    	WorkbenchSetting result = manager.getWorkbenchSetting();
//    	Assert.assertNotNull(result);
//    	Debug.println(INDENT, result.toString());
//    }

    @Test
    public void testGetWorkflowTemplateByName() throws MiddlewareQueryException  {
    	String name ="Manager";
    	List<WorkflowTemplate> results = manager.getWorkflowTemplateByName(name);
    	Assert.assertNotNull(results);
        Assert.assertTrue(!results.isEmpty());
        Debug.printObjects(INDENT, results);
    }

    @Test
    public void testGetWorkflowTemplatesList() throws MiddlewareQueryException  {
    	List<WorkflowTemplate> results = manager.getWorkflowTemplates();
    	Assert.assertNotNull(results);
        Assert.assertTrue(!results.isEmpty());
        Debug.printObjects(INDENT, results);
    }

    @Test
    public void testDeletePerson() throws MiddlewareQueryException  {
        Person person = createTestPersonData();
        manager.addPerson(person);
    	manager.deletePerson(person);
        Debug.println(INDENT, "Record is successfully deleted");
    }

    @Test
    public void testGetWorkflowTemplates() throws MiddlewareQueryException  {
    	List<WorkflowTemplate> results = manager.getWorkflowTemplates(0, 100);
    	Assert.assertNotNull(results);
        Assert.assertTrue(!results.isEmpty());
        Debug.printObjects(INDENT, results);
    }

    @Test
    public void testGetProjectUserInfoDao() throws MiddlewareQueryException  {
    	ProjectUserInfoDAO results = manager.getProjectUserInfoDao();
    	Assert.assertNotNull(results);
    	Debug.println(INDENT, results.toString());
    }

    @Test
    public void testGetToolDao() throws MiddlewareQueryException  {
    	ToolDAO results = manager.getToolDao();
    	Assert.assertNotNull(results);
    	Debug.println(INDENT, results.toString());
    }

    // FIXME : data will not seed
//    @Test
//    public void testGetUserInfo() throws MiddlewareQueryException  {
//    	
//    	UserInfo results = manager.getUserInfo(testUser1.getUserid());
//    	Assert.assertNotNull(results);
//        Debug.println(INDENT, results.toString());
//        
//    }

    @Test
    public void testGetToolsWithType() throws MiddlewareQueryException  {
    	List<Tool> results = manager.getToolsWithType(ToolType.NATIVE);
    	Assert.assertNotNull(results);
        Assert.assertTrue(!results.isEmpty());
        Debug.printObjects(INDENT, results);
    }

    @Test
    public void testGetTemplateSettings() throws MiddlewareQueryException {
        
        TemplateSetting templateSetting = createTemplateSetting();

        manager.addTemplateSetting(templateSetting);
        Debug.println(INDENT, "Added TemplateSetting: " + templateSetting);
        
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
        Debug.println(INDENT, "Database cleanup: templateSetting deleted.");
    }
    
    private void getTemplateSetting(String filterDescription, TemplateSetting templateSettingFilter) throws MiddlewareQueryException{
        List<TemplateSetting> settings = manager.getTemplateSettings(templateSettingFilter);
        assertTrue(settings.size() > 0);
        Debug.println(INDENT, "Retrieve records by " + filterDescription + ": #records = " + settings.size());
        Debug.printObjects(INDENT*2, settings);
    }
    
    
    @Test
    public void testAddAndDeleteTemplateSettings() throws MiddlewareQueryException {

        TemplateSetting templateSetting = createTemplateSetting();

        manager.addTemplateSetting(templateSetting);
        Debug.println(INDENT, "testAddTemplateSettings: " + templateSetting);
        
        assertNotNull(templateSetting.getTemplateSettingId());
        
        manager.deleteTemplateSetting(templateSetting);
        Debug.println(INDENT, "testDeleteTemplateSettings: " + templateSetting);
    }
    

    @Test
    public void testDeleteTemplateSettingById() throws MiddlewareQueryException {

        TemplateSetting templateSetting = createTemplateSetting();

        manager.addTemplateSetting(templateSetting);
        Debug.println(INDENT, "TemplateSetting Added: " + templateSetting);
        
        assertNotNull(templateSetting.getTemplateSettingId());
        
        manager.deleteTemplateSetting(templateSetting.getTemplateSettingId());
        Debug.println(INDENT, "TemplateSetting Deleted: " + templateSetting);
    }
    
    @Test
    public void testUpdateTemplateSettings() throws MiddlewareQueryException {

        TemplateSetting templateSetting = createTemplateSetting();

        manager.addTemplateSetting(templateSetting);
        Debug.println(INDENT, "TemplateSetting added: " + templateSetting);
        
        assertNotNull(templateSetting.getTemplateSettingId());
        
        templateSetting.setIsDefault(!templateSetting.isDefault());
        templateSetting.setName(templateSetting.getName() + (int) (Math.random() * 100));
        
        manager.updateTemplateSetting(templateSetting);
        Debug.println(INDENT, "TemplateSetting updated: " + templateSetting);
        
        manager.deleteTemplateSetting(templateSetting);
        Debug.println(INDENT, "Database cleanup: templateSetting deleted.");

    }
    
    @Test
    public void testAddTemplateSettingsSameIsDefaultProjectAndTool() throws MiddlewareQueryException {

        TemplateSetting templateSetting1 = createTemplateSetting();
        templateSetting1.setIsDefault(Boolean.TRUE);
        
        TemplateSetting templateSetting2 = createTemplateSetting();
        templateSetting2.setIsDefault(Boolean.TRUE);

        manager.addTemplateSetting(templateSetting1);
        Debug.println(INDENT, "TemplateSetting1 added: " + templateSetting1);
        manager.addTemplateSetting(templateSetting2);
        Debug.println(INDENT, "TemplateSetting2 added: " + templateSetting2);
        Debug.println(INDENT, "TemplateSetting1 updated: " + templateSetting1);
        
        assertFalse(templateSetting1.isDefault());
        assertTrue(templateSetting2.isDefault());

        manager.deleteTemplateSetting(templateSetting1);
        manager.deleteTemplateSetting(templateSetting2);
        Debug.println(INDENT, "Database cleanup: template settings deleted.");

    }

    @Test
    public void testUpdateTemplateSettingsSameIsDefaultProjectAndTool() throws MiddlewareQueryException {

        TemplateSetting templateSetting1 = createTemplateSetting();
        templateSetting1.setIsDefault(Boolean.FALSE);
        
        TemplateSetting templateSetting2 = createTemplateSetting();
        templateSetting2.setIsDefault(Boolean.TRUE);

        manager.addTemplateSetting(templateSetting1);
        Debug.println(INDENT, "TemplateSetting1 added: " + templateSetting1);
        manager.addTemplateSetting(templateSetting2);
        Debug.println(INDENT, "TemplateSetting2 added: " + templateSetting2);
        
        templateSetting1.setIsDefault(Boolean.TRUE);
        manager.updateTemplateSetting(templateSetting1);
        Debug.println(INDENT, "TemplateSetting1 update: " + templateSetting1);
        Debug.println(INDENT, "TemplateSetting2 update: " + templateSetting2);
        
        assertTrue(templateSetting1.isDefault());
        assertFalse(templateSetting2.isDefault());

        manager.deleteTemplateSetting(templateSetting1);
        manager.deleteTemplateSetting(templateSetting2);
        Debug.println(INDENT, "Database cleanup: template settings deleted.");

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
        Boolean isDefault = true;
        
        return new TemplateSetting(templateSettingId, projectId, name, tool, configuration, isDefault);
    }

    @Test
    public void testCRUDStandardPresetDAO() throws Exception {
        StandardPreset preset = new StandardPreset();
        preset.setConfiguration("<configuration/>");
        preset.setName("configuration_01");
        preset.setToolId(1);
        preset.setCropName("crop_name");

        StandardPreset results = manager.addStandardPreset(preset);

        assertTrue("we retrieve the saved primary id",results.getStandardPresetId() > 0);

        Integer id = results.getStandardPresetId();

        // test retrieve from database using id
        StandardPreset retrievedResult = manager.getStandardPresetDAO().getById(id);

        assertEquals("we retrieved the correct object from database",results,retrievedResult);

        // we test deletion, also serves as cleanup
        manager.deleteStandardPreset(id);

        assertNull("standard preset with id=" + id + " should no longer exist",manager.getStandardPresetDAO().getById(id));
    }

    @Test
    public void testGetAllStandardPreset() throws Exception {
        List<StandardPreset> out = manager.getStandardPresetDAO().getAll();

        assertTrue(out.size() > 0);

    }

    @Test
    public void testGetStandardPresetFromCropAndTool() throws Exception {
        List<StandardPreset> fulllist = initializeStandardPresets();

        for (int j = 1; j < 3; j++) {
            List<StandardPreset> presetsList = manager.getStandardPresetFromCropAndTool("crop_name_" + j,j);
            for (StandardPreset p : presetsList) {
                assertEquals("should only retrieve all standard presets with crop_name_1","crop_name_" + j,p.getCropName());
                assertEquals("should be the same tool as requested",Integer.valueOf(j),p.getToolId());
            }
        }

        // cleanup
        for (StandardPreset p : fulllist) {
            manager.deleteStandardPreset(p.getStandardPresetId());
        }
    }

    @Test
    public void testGetStandardPresetFromCropAndToolAndToolSection() throws Exception {
        List<StandardPreset> fulllist = initializeStandardPresets();

        for (int j = 1; j < 3; j++) {
            List<StandardPreset> presetsList = manager.getStandardPresetFromCropAndTool(
                    "crop_name_" + j, j, "tool_section_" + j);
            for (StandardPreset p : presetsList) {
                assertEquals("should only retrieve all standard presets with same crop name","crop_name_" + j,p.getCropName());
                assertEquals("should only retrieve all standard presets with same tool section","tool_section_" + j,p.getToolSection());
                assertEquals("should be the same tool as requested",Integer.valueOf(j),p.getToolId());
            }
        }

        // cleanup
        for (StandardPreset p : fulllist) {
            manager.deleteStandardPreset(p.getStandardPresetId());
        }
    }


    protected List<StandardPreset> initializeStandardPresets() throws MiddlewareQueryException {
        List<StandardPreset> fulllist = new ArrayList<StandardPreset>();
        for (int j = 1; j < 3; j++) {
            for (int i = 1; i < 6; i++) {
                StandardPreset preset = new StandardPreset();
                preset.setConfiguration("<configuration/>");
                preset.setName("configuration_" + j + "_" + i);
                preset.setToolId(j);
                preset.setCropName("crop_name_" + j);
                preset.setToolSection("tool_section_" + j);

                fulllist.add(manager.addStandardPreset(preset));
            }
        }

        return fulllist;
    }

}
