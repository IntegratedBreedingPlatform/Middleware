/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.generationcp.middleware.MiddlewareIntegrationTest;
import org.generationcp.middleware.WorkbenchTestDataUtil;
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
import org.generationcp.middleware.pojos.workbench.ProjectUserMysqlAccount;
import org.generationcp.middleware.pojos.workbench.ProjectUserRole;
import org.generationcp.middleware.pojos.workbench.Role;
import org.generationcp.middleware.pojos.workbench.TemplateSetting;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.ToolConfiguration;
import org.generationcp.middleware.pojos.workbench.ToolType;
import org.generationcp.middleware.pojos.workbench.WorkbenchDataset;
import org.generationcp.middleware.pojos.workbench.WorkbenchRuntimeData;
import org.generationcp.middleware.pojos.workbench.WorkflowTemplate;
import org.generationcp.middleware.util.Util;
import org.generationcp.middleware.utils.test.Debug;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class WorkbenchDataManagerImplTest extends MiddlewareIntegrationTest {

	private static WorkbenchDataManagerImpl manager;
	private static WorkbenchTestDataUtil workbenchTestDataUtil;
	private static Project commonTestProject;
	private static User testUser1;

	@BeforeClass
	public static void setUp() throws MiddlewareQueryException {
		HibernateSessionProvider sessionProvider =
				new HibernateSessionPerThreadProvider(MiddlewareIntegrationTest.workbenchSessionUtil.getSessionFactory());
		WorkbenchDataManagerImplTest.manager = new WorkbenchDataManagerImpl(sessionProvider);
		WorkbenchDataManagerImplTest.workbenchTestDataUtil = WorkbenchTestDataUtil.getInstance();
		WorkbenchDataManagerImplTest.workbenchTestDataUtil.setUpWorkbench();
		WorkbenchDataManagerImplTest.commonTestProject = WorkbenchDataManagerImplTest.workbenchTestDataUtil.getCommonTestProject();
		WorkbenchDataManagerImplTest.testUser1 = WorkbenchDataManagerImplTest.workbenchTestDataUtil.getTestUser1();
	}

	@AfterClass
	public static void tearDown() throws MiddlewareQueryException {

	}

	@Test
	public void testAddUser() throws MiddlewareQueryException {
		User user = WorkbenchDataManagerImplTest.workbenchTestDataUtil.createTestUserData();
		Integer result = WorkbenchDataManagerImplTest.manager.addUser(user);
		Assert.assertNotNull("Expected id of a newly saved record in workbench_user.", result);

		User readUser = WorkbenchDataManagerImplTest.manager.getUserById(result);
		Assert.assertEquals(user.getName(), readUser.getName());
		WorkbenchDataManagerImplTest.manager.deleteUser(readUser);
	}

	@Test
	public void testAddPerson() throws MiddlewareQueryException {
		Person person = WorkbenchDataManagerImplTest.workbenchTestDataUtil.createTestPersonData();
		Integer result = WorkbenchDataManagerImplTest.manager.addPerson(person);
		Assert.assertNotNull("Expected id of a newly saved record in persons.", result);

		Person readPerson = WorkbenchDataManagerImplTest.manager.getPersonById(result);
		Assert.assertEquals(person.getLastName(), readPerson.getLastName());
		WorkbenchDataManagerImplTest.manager.deletePerson(readPerson);
	}

	@Test
	public void testAddProject() throws MiddlewareQueryException {
		Project project = WorkbenchDataManagerImplTest.workbenchTestDataUtil.createTestProjectData();
		WorkbenchDataManagerImplTest.manager.addProject(project);
		Assert.assertNotNull("Expected id of a newly saved record in workbench_project.", project.getProjectId());
		WorkbenchDataManagerImplTest.manager.deleteProject(project);
	}

	@Test
	public void testAddProjectActivity() throws MiddlewareQueryException {

		ProjectActivity projectActivity =
				WorkbenchDataManagerImplTest.workbenchTestDataUtil.createTestProjectActivityData(
						WorkbenchDataManagerImplTest.commonTestProject, WorkbenchDataManagerImplTest.testUser1);

		Integer result = WorkbenchDataManagerImplTest.manager.addProjectActivity(projectActivity);
		Assert.assertNotNull("Expected id of a newly saved record in workbench_project_activity", result);

		WorkbenchDataManagerImplTest.manager.deleteProjectActivity(projectActivity);
	}

	@Test
	public void testAddIbdbUserMap() throws MiddlewareQueryException {
		IbdbUserMap userMap = new IbdbUserMap();
		userMap.setProjectId(WorkbenchDataManagerImplTest.commonTestProject.getProjectId());
		userMap.setIbdbUserId(WorkbenchDataManagerImplTest.testUser1.getUserid() * -1);
		userMap.setWorkbenchUserId(WorkbenchDataManagerImplTest.testUser1.getUserid());

		Integer result = WorkbenchDataManagerImplTest.manager.addIbdbUserMap(userMap);
		Assert.assertNotNull("Expected id of a newly saved record in workbench_ibdb_user_map", result);
	}

	@Test
	public void testGetProjects() throws MiddlewareQueryException {
		List<Project> projects = WorkbenchDataManagerImplTest.manager.getProjects();
		Assert.assertNotNull(projects);
		Assert.assertTrue(!projects.isEmpty());
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, projects);
	}

	@Test
	public void testGetToolWithName() throws MiddlewareQueryException {
		String toolName = "fieldbook_web";
		Tool tool = WorkbenchDataManagerImplTest.manager.getToolWithName(toolName);
		Assert.assertNotNull(tool);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetToolWithName(" + toolName + "): " + tool);
	}

	@Test
	public void testGetProjectByName() throws MiddlewareQueryException {
		Project project =
				WorkbenchDataManagerImplTest.manager.getProjectByName(WorkbenchDataManagerImplTest.commonTestProject.getProjectName());
		Assert.assertEquals(WorkbenchDataManagerImplTest.commonTestProject.getProjectName(), project.getProjectName());
	}

	@Test
	public void testGetUserByName() throws MiddlewareQueryException {
		User user =
				WorkbenchDataManagerImplTest.manager.getUserByName(WorkbenchDataManagerImplTest.testUser1.getName(), 0, 1, Operation.EQUAL)
						.get(0);
		Assert.assertEquals(WorkbenchDataManagerImplTest.testUser1.getName(), user.getName());
		Assert.assertEquals(WorkbenchDataManagerImplTest.testUser1.getUserid(), user.getUserid());
	}

	@Test
	public void testAddWorkbenchDataset() throws MiddlewareQueryException {
		WorkbenchDataset dataset = this.createTestWorkbenchDataset(WorkbenchDataManagerImplTest.commonTestProject);
		Integer result = WorkbenchDataManagerImplTest.manager.addWorkbenchDataset(dataset);
		Assert.assertNotNull("Expected id of the newly added record in workbench_dataset", result);
		WorkbenchDataManagerImplTest.manager.deleteWorkbenchDataset(dataset);
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
		WorkbenchDataset dataset = this.createTestWorkbenchDataset(WorkbenchDataManagerImplTest.commonTestProject);
		WorkbenchDataManagerImplTest.manager.addWorkbenchDataset(dataset);

		List<WorkbenchDataset> list =
				WorkbenchDataManagerImplTest.manager.getWorkbenchDatasetByProjectId(
						WorkbenchDataManagerImplTest.commonTestProject.getProjectId(), 0, 10);
		Assert.assertTrue(list.contains(dataset));
		WorkbenchDataManagerImplTest.manager.deleteWorkbenchDataset(dataset);
	}

	@Test
	public void testCountWorkbenchDatasetByProjectId() throws MiddlewareQueryException {
		WorkbenchDataset dataset1 = this.createTestWorkbenchDataset(WorkbenchDataManagerImplTest.commonTestProject);
		WorkbenchDataManagerImplTest.manager.addWorkbenchDataset(dataset1);

		WorkbenchDataset dataset2 = this.createTestWorkbenchDataset(WorkbenchDataManagerImplTest.commonTestProject);
		WorkbenchDataManagerImplTest.manager.addWorkbenchDataset(dataset2);

		long result =
				WorkbenchDataManagerImplTest.manager.countWorkbenchDatasetByProjectId(WorkbenchDataManagerImplTest.commonTestProject
						.getProjectId());
		Assert.assertEquals(2, result);

		WorkbenchDataManagerImplTest.manager.deleteWorkbenchDataset(dataset1);
		WorkbenchDataManagerImplTest.manager.deleteWorkbenchDataset(dataset2);
	}

	@Test
	public void testGetWorkbenchDatasetByName() throws MiddlewareQueryException {
		String name = "D";
		List<WorkbenchDataset> list = WorkbenchDataManagerImplTest.manager.getWorkbenchDatasetByName(name, Operation.EQUAL, 0, 10);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetWorkbenchDatasetByName(name=" + name + "): ");

		if (list.isEmpty()) {
			Debug.println(MiddlewareIntegrationTest.INDENT, "  No records found.");
		}

		for (WorkbenchDataset d : list) {
			Debug.println(MiddlewareIntegrationTest.INDENT, d.getDatasetId() + ": " + d.getName());
		}
	}

	@Test
	public void testCountWorkbenchDatasetByName() throws MiddlewareQueryException {
		String name = "a";
		long result = WorkbenchDataManagerImplTest.manager.countWorkbenchDatasetByName(name, Operation.EQUAL);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testCountWorkbenchDatasetByName(name=" + name + "): " + result);
	}

	@Test
	public void testAddProjectUserRoles() throws MiddlewareQueryException {

		Role role1 = WorkbenchDataManagerImplTest.manager.getAllRoles().get(0);
		Role role2 = WorkbenchDataManagerImplTest.manager.getAllRoles().get(1);

		ProjectUserRole projUsrRole1 =
				new ProjectUserRole(WorkbenchDataManagerImplTest.commonTestProject, WorkbenchDataManagerImplTest.testUser1, role1);
		ProjectUserRole projUsrRole2 =
				new ProjectUserRole(WorkbenchDataManagerImplTest.commonTestProject, WorkbenchDataManagerImplTest.testUser1, role2);

		List<ProjectUserRole> projectUserRoles = new ArrayList<ProjectUserRole>();
		projectUserRoles.add(projUsrRole1);
		projectUserRoles.add(projUsrRole2);

		List<Integer> rolesAdded = WorkbenchDataManagerImplTest.manager.addProjectUserRole(projectUserRoles);
		Assert.assertEquals(2, rolesAdded.size());

		Debug.println(
				MiddlewareIntegrationTest.INDENT,
				"testAddProjectUsers(projectId=" + WorkbenchDataManagerImplTest.commonTestProject.getProjectId() + ") ADDED: "
						+ rolesAdded.size());

		long result =
				WorkbenchDataManagerImplTest.manager.countUsersByProjectId(WorkbenchDataManagerImplTest.commonTestProject.getProjectId());
		Assert.assertEquals(1, result);

	}

	@Test
	public void testGetUsersByProjectId() throws MiddlewareQueryException {
		List<User> users =
				WorkbenchDataManagerImplTest.manager.getUsersByProjectId(WorkbenchDataManagerImplTest.commonTestProject.getProjectId());
		Assert.assertNotNull(users);
		Assert.assertEquals(1, users.size());
		Assert.assertEquals(WorkbenchDataManagerImplTest.testUser1, users.get(0));
	}

	@Test
	public void testGetActivitiesByProjectId() throws MiddlewareQueryException {
		List<ProjectActivity> results =
				WorkbenchDataManagerImplTest.manager.getProjectActivitiesByProjectId(
						WorkbenchDataManagerImplTest.commonTestProject.getProjectId(), 0, 10);
		Assert.assertNotNull(results);
		Assert.assertEquals(2, results.size());
	}

	@Test
	public void testCountActivitiesByProjectId() throws MiddlewareQueryException {
		long result =
				WorkbenchDataManagerImplTest.manager.countProjectActivitiesByProjectId(WorkbenchDataManagerImplTest.commonTestProject
						.getProjectId());
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

		WorkbenchDataManagerImplTest.manager.addToolConfiguration(toolConfig);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testAddToolConfiguration(toolId=" + toolId + "): " + toolConfig);

		// clean up
		WorkbenchDataManagerImplTest.manager.deleteToolConfiguration(toolConfig);
	}

	@Test
	public void testUpdateToolConfiguration() throws MiddlewareQueryException {
		Long toolId = 1L;
		ToolConfiguration toolConfig = WorkbenchDataManagerImplTest.manager.getToolConfigurationByToolIdAndConfigKey(toolId, "5th key");

		if (toolConfig != null) {
			String oldToolConfigValue = toolConfig.toString();
			toolConfig.setConfigValue("test test");

			WorkbenchDataManagerImplTest.manager.updateToolConfiguration(toolConfig);

			ToolConfigurationDAO dao = new ToolConfigurationDAO();
			dao.setSession(MiddlewareIntegrationTest.workbenchSessionUtil.getCurrentSession());
			ToolConfiguration result = dao.getById(toolId, false);

			Debug.println(MiddlewareIntegrationTest.INDENT, "testUpdateToolConfiguration(toolId=" + toolId + "): ");
			Debug.println(MiddlewareIntegrationTest.INDENT, "  FROM: " + oldToolConfigValue);
			Debug.println(MiddlewareIntegrationTest.INDENT, "    TO: " + result);
		} else {
			Debug.println(MiddlewareIntegrationTest.INDENT, "testUpdateToolConfiguration(toolId=" + toolId
					+ "): Tool configuration not found.");
		}
	}

	@Test
	public void testDeleteToolConfiguration() throws MiddlewareQueryException {
		Long toolId = 1L;
		ToolConfiguration toolConfig = WorkbenchDataManagerImplTest.manager.getToolConfigurationByToolIdAndConfigKey(toolId, "5th key");

		if (toolConfig != null) {
			WorkbenchDataManagerImplTest.manager.deleteToolConfiguration(toolConfig);
			ToolConfigurationDAO dao = new ToolConfigurationDAO();
			dao.setSession(MiddlewareIntegrationTest.workbenchSessionUtil.getCurrentSession());
			ToolConfiguration result = dao.getById(toolId, false);

			Debug.println(MiddlewareIntegrationTest.INDENT, "testDeleteToolConfiguration(toolId=" + toolId + "): " + result);
		} else {
			Debug.println(MiddlewareIntegrationTest.INDENT, "testDeleteToolConfiguration(toolId=" + toolId
					+ "): Tool Configuration not found");
		}
	}

	@Test
	public void testGetListOfToolConfigurationsByToolId() throws MiddlewareQueryException {
		Long toolId = 1L;
		List<ToolConfiguration> result = WorkbenchDataManagerImplTest.manager.getListOfToolConfigurationsByToolId(toolId);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetListOfToolConfigurationsByToolId(" + toolId + "): ");

		if (result.isEmpty()) {
			Debug.println(MiddlewareIntegrationTest.INDENT, "  No records found.");
		} else {
			for (ToolConfiguration t : result) {
				Debug.println(MiddlewareIntegrationTest.INDENT, t);
			}
		}
	}

	@Test
	public void testGetToolConfigurationByToolIdAndConfigKey() throws MiddlewareQueryException {
		Long toolId = 1L;
		String configKey = "test";
		ToolConfiguration toolConfig = WorkbenchDataManagerImplTest.manager.getToolConfigurationByToolIdAndConfigKey(toolId, configKey);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetToolConfigurationByToolIdAndConfigKey(toolId=" + toolId + ", configKey="
				+ configKey + "): " + toolConfig);
	}

	@Test
	public void testAddCropType() throws MiddlewareQueryException {
		CropType cropType = new CropType("Coconut");

		try {
			String added = WorkbenchDataManagerImplTest.manager.addCropType(cropType);
			Assert.assertNotNull(added);
			Debug.println(MiddlewareIntegrationTest.INDENT, "testAddCropType(" + cropType + "): records added = " + added);
		} catch (MiddlewareQueryException e) {
			if (e.getMessage().equals("Crop type already exists.")) {
				Debug.println(MiddlewareIntegrationTest.INDENT, e.getMessage());
			} else {
				throw e;
			}
		}
	}

	@Test
	public void testGetInstalledCentralCrops() throws MiddlewareQueryException {
		ArrayList<CropType> cropTypes = (ArrayList<CropType>) WorkbenchDataManagerImplTest.manager.getInstalledCentralCrops();
		Assert.assertNotNull(cropTypes);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetInstalledCentralCrops(): " + cropTypes);
	}

	@Test
	public void testGetCropTypeByName() throws MiddlewareQueryException {
		String cropName = CropType.CropEnum.CHICKPEA.toString();
		CropType cropType = WorkbenchDataManagerImplTest.manager.getCropTypeByName(cropName);
		Assert.assertNotNull(cropName);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetCropTypeByName(" + cropName + "): " + cropType);
	}

	@Test
	public void testGetLocalIbdbUserId() throws MiddlewareQueryException {
		Integer localIbdbUserId =
				WorkbenchDataManagerImplTest.manager.getLocalIbdbUserId(WorkbenchDataManagerImplTest.testUser1.getUserid(),
						WorkbenchDataManagerImplTest.commonTestProject.getProjectId());
		Assert.assertNotNull(localIbdbUserId);
		Debug.println(MiddlewareIntegrationTest.INDENT,
				"testGetLocalIbdbUserId(workbenchUserId=" + WorkbenchDataManagerImplTest.testUser1.getUserid() + ", projectId="
						+ WorkbenchDataManagerImplTest.commonTestProject.getProjectId() + "): " + localIbdbUserId);
	}

	@Test
	public void testGetRoleById() throws MiddlewareQueryException {
		Integer id = Integer.valueOf(1); // Assumption: there is a role with id 1
		Role role = WorkbenchDataManagerImplTest.manager.getRoleById(id);
		Assert.assertNotNull(role);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetRoleById(id=" + id + "): \n  " + role);
	}

	@Test
	public void testGetRoleByNameAndWorkflowTemplate() throws MiddlewareQueryException {
		String templateName = "MARS";
		String roleName = "MARS Breeder";
		WorkflowTemplate workflowTemplate = WorkbenchDataManagerImplTest.manager.getWorkflowTemplateByName(templateName).get(0);
		Role role = WorkbenchDataManagerImplTest.manager.getRoleByNameAndWorkflowTemplate(roleName, workflowTemplate);
		Assert.assertNotNull(role);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetRoleByNameAndWorkflowTemplate(name=" + roleName + ", workflowTemplate="
				+ workflowTemplate.getName() + "): \n  " + role);
	}

	@Test
	public void testGetRolesByWorkflowTemplate() throws MiddlewareQueryException {
		WorkflowTemplate workflowTemplate = WorkbenchDataManagerImplTest.manager.getWorkflowTemplates().get(0); // get the first template in
																												// the db
		List<Role> roles = WorkbenchDataManagerImplTest.manager.getRolesByWorkflowTemplate(workflowTemplate);
		Assert.assertNotNull(roles);
		Assert.assertTrue(!roles.isEmpty());
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetRolesByWorkflowTemplate(workflowTemplate=" + workflowTemplate.getName()
				+ "): " + roles.size());
		for (Role role : roles) {
			Debug.println(MiddlewareIntegrationTest.INDENT, "  " + role);
		}
	}

	@Test
	public void testGetWorkflowTemplateByRole() throws MiddlewareQueryException {
		Role role = WorkbenchDataManagerImplTest.manager.getRoleById(WorkbenchDataManagerImplTest.manager.getAllRoles().get(0).getRoleId());
		WorkflowTemplate template = WorkbenchDataManagerImplTest.manager.getWorkflowTemplateByRole(role);
		Assert.assertNotNull(template);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetWorkflowTemplateByRole(role=" + role.getName() + "): \n  " + template);
	}

	@Test
	public void testGetRoleByProjectAndUser() throws MiddlewareQueryException {

		List<ProjectUserRole> projectUsers =
				WorkbenchDataManagerImplTest.manager.getProjectUserRolesByProject(WorkbenchDataManagerImplTest.commonTestProject);
		Assert.assertNotNull(projectUsers);
		Assert.assertTrue(!projectUsers.isEmpty());

		if (projectUsers.size() > 0) {
			ProjectUserRole projectUser = projectUsers.get(0); // get the first user of the project
			User user = WorkbenchDataManagerImplTest.manager.getUserById(projectUser.getUserId());
			List<Role> roles =
					WorkbenchDataManagerImplTest.manager.getRolesByProjectAndUser(WorkbenchDataManagerImplTest.commonTestProject, user); // get
																																			// the
																																			// roles
			Debug.println(MiddlewareIntegrationTest.INDENT, "testGetRoleByProjectAndUser(project="
					+ WorkbenchDataManagerImplTest.commonTestProject.getProjectName() + ", user=" + user.getName() + "): \n  " + roles);
		} else {
			Debug.println(MiddlewareIntegrationTest.INDENT, "testGetRoleByProjectAndUser(project="
					+ WorkbenchDataManagerImplTest.commonTestProject.getProjectName() + "): Error in data - Project has no users. ");
		}
	}

	@Test
	public void testGetAllRoles() throws MiddlewareQueryException {
		List<Role> roles = WorkbenchDataManagerImplTest.manager.getAllRoles();
		Assert.assertNotNull(roles);
		Assert.assertTrue(!roles.isEmpty());

		for (Role role : roles) {
			Debug.println(MiddlewareIntegrationTest.INDENT, role.toString());
		}
	}

	@Test
	public void testAddProjectUserMysqlAccount() throws MiddlewareQueryException {
		ProjectUserMysqlAccount recordToSave = new ProjectUserMysqlAccount();
		recordToSave.setProject(WorkbenchDataManagerImplTest.commonTestProject);
		recordToSave.setUser(WorkbenchDataManagerImplTest.testUser1);
		recordToSave.setMysqlUsername("sample " + new Random().nextInt(10000));
		recordToSave.setMysqlPassword("password");

		Integer idSaved = WorkbenchDataManagerImplTest.manager.addProjectUserMysqlAccount(recordToSave);
		Assert.assertNotNull("Expected id of the newly saved record in workbench_project_user_mysql_account", idSaved);
		Debug.println(MiddlewareIntegrationTest.INDENT, "Id of record saved: " + idSaved);

	}

	@Test
	public void testGetProjectUserMysqlAccountByProjectIdAndUserId() throws MiddlewareQueryException {
		ProjectUserMysqlAccount record =
				WorkbenchDataManagerImplTest.manager.getProjectUserMysqlAccountByProjectIdAndUserId(
						WorkbenchDataManagerImplTest.commonTestProject.getProjectId().intValue(),
						WorkbenchDataManagerImplTest.testUser1.getUserid());
		Assert.assertNotNull(record);
		Assert.assertEquals(Long.valueOf(WorkbenchDataManagerImplTest.commonTestProject.getProjectId()), new Long(record.getProject()
				.getProjectId()));
		Assert.assertEquals(WorkbenchDataManagerImplTest.testUser1.getUserid(), record.getUser().getUserid());
		Debug.println(MiddlewareIntegrationTest.INDENT, record.toString());
	}

	@Test
	public void testGetProjectBackups() throws MiddlewareQueryException {
		List<ProjectBackup> projectBackups = WorkbenchDataManagerImplTest.manager.getProjectBackups();
		Assert.assertNotNull(projectBackups);
		Assert.assertTrue(!projectBackups.isEmpty());

		Debug.println(MiddlewareIntegrationTest.INDENT, "testGetProjectBackups(): ");
		for (ProjectBackup project : projectBackups) {
			Debug.println(MiddlewareIntegrationTest.INDENT, project);
		}
	}

	@Test
	public void testAddProjectBackup() throws MiddlewareQueryException {
		ProjectBackup projectBackup = new ProjectBackup();
		projectBackup.setProjectId(WorkbenchDataManagerImplTest.commonTestProject.getProjectId());
		projectBackup.setBackupPath("target/resource" + WorkbenchDataManagerImplTest.commonTestProject.getProjectId());
		projectBackup.setBackupTime(Util.getCurrentDate());

		WorkbenchDataManagerImplTest.manager.saveOrUpdateProjectBackup(projectBackup);
		Assert.assertNotNull(projectBackup);
	}

	@Test
	public void testGetProjectBackupsByProject() throws MiddlewareQueryException {
		List<ProjectBackup> projectBackups =
				WorkbenchDataManagerImplTest.manager.getProjectBackups(WorkbenchDataManagerImplTest.commonTestProject);
		Assert.assertNotNull(projectBackups);
		Assert.assertTrue(!projectBackups.isEmpty());

		for (ProjectBackup backup : projectBackups) {
			Debug.println(MiddlewareIntegrationTest.INDENT, backup);
		}
	}

	@Test
	public void testCountAllPersons() throws MiddlewareQueryException {
		long count = WorkbenchDataManagerImplTest.manager.countAllPersons();
		Assert.assertNotNull(count);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testCountAllPersons: " + count);
	}

	@Test
	public void testCountAllUsers() throws MiddlewareQueryException {
		long count = WorkbenchDataManagerImplTest.manager.countAllUsers();
		Assert.assertNotNull(count);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testCountAllUsers: " + count);
	}

	@Test
	public void testGetAllPersons() throws MiddlewareQueryException {
		List<Person> results = WorkbenchDataManagerImplTest.manager.getAllPersons();
		Assert.assertNotNull(results);
		Assert.assertTrue(!results.isEmpty());

		for (Person result : results) {
			Debug.println(MiddlewareIntegrationTest.INDENT, result.toString());
		}
	}

	@Test
	public void testGetAllRolesDesc() throws MiddlewareQueryException {
		List<Role> results = WorkbenchDataManagerImplTest.manager.getAllRolesDesc();
		Assert.assertNotNull(results);
		Assert.assertTrue(!results.isEmpty());

		for (Role result : results) {
			Debug.println(MiddlewareIntegrationTest.INDENT, result.toString());
		}
	}

	@Test
	public void testGetAllRolesOrderedByLabel() throws MiddlewareQueryException {
		List<Role> results = WorkbenchDataManagerImplTest.manager.getAllRolesOrderedByLabel();
		Assert.assertNotNull(results);
		Assert.assertTrue(!results.isEmpty());

		for (Role result : results) {
			Debug.println(MiddlewareIntegrationTest.INDENT, result.toString());
		}
	}

	@Test
	public void testGetAllTools() throws MiddlewareQueryException {
		List<Tool> results = WorkbenchDataManagerImplTest.manager.getAllTools();
		Assert.assertNotNull(results);
		Assert.assertTrue(!results.isEmpty());

		for (Tool result : results) {
			Debug.println(MiddlewareIntegrationTest.INDENT, result.toString());
		}
	}

	@Test
	public void testGetAllUsers() throws MiddlewareQueryException {
		List<User> results = WorkbenchDataManagerImplTest.manager.getAllUsers();
		Assert.assertNotNull(results);
		Assert.assertTrue(!results.isEmpty());

		for (User result : results) {
			Debug.println(MiddlewareIntegrationTest.INDENT, result.toString());
		}
	}

	@Test
	public void testGetAllUsersSorted() throws MiddlewareQueryException {
		List<User> results = WorkbenchDataManagerImplTest.manager.getAllUsersSorted();
		Assert.assertNotNull(results);
		Assert.assertTrue(!results.isEmpty());

		for (User result : results) {
			Debug.println(MiddlewareIntegrationTest.INDENT, result.toString());
		}
	}

	@Test
	public void testGetLastOpenedProject() throws MiddlewareQueryException {
		Project results = WorkbenchDataManagerImplTest.manager.getLastOpenedProject(WorkbenchDataManagerImplTest.testUser1.getUserid());
		Assert.assertNotNull(results);
		Debug.println(MiddlewareIntegrationTest.INDENT, results.toString());
	}

	@Test
	public void testGetProjectsList() throws MiddlewareQueryException {
		List<Project> results = WorkbenchDataManagerImplTest.manager.getProjects(0, 100);
		Assert.assertNotNull(results);
		Assert.assertTrue(!results.isEmpty());
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, results);
	}

	@Test
	public void testGetProjectsByUser() throws MiddlewareQueryException {
		List<Project> results = WorkbenchDataManagerImplTest.manager.getProjectsByUser(WorkbenchDataManagerImplTest.testUser1);
		Assert.assertNotNull(results);
		Assert.assertTrue(!results.isEmpty());
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, results);
	}

	@Test
	public void testGetProjectUserRolesByProject() throws MiddlewareQueryException {
		List<ProjectUserRole> results =
				WorkbenchDataManagerImplTest.manager.getProjectUserRolesByProject(WorkbenchDataManagerImplTest.commonTestProject);
		Assert.assertNotNull(results);
		Assert.assertTrue(!results.isEmpty());
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, results);
	}

	@Test
	public void testGetUserById() throws MiddlewareQueryException {
		User user = WorkbenchDataManagerImplTest.manager.getUserById(WorkbenchDataManagerImplTest.testUser1.getUserid());
		Assert.assertNotNull(user);
		Debug.println(MiddlewareIntegrationTest.INDENT, user.toString());
	}

	@Test
	public void testGetWorkbenchDatasetById() throws MiddlewareQueryException {
		WorkbenchDataset testDataset = this.createTestWorkbenchDataset(WorkbenchDataManagerImplTest.commonTestProject);
		Integer result = WorkbenchDataManagerImplTest.manager.addWorkbenchDataset(testDataset);

		WorkbenchDataset readDataset = WorkbenchDataManagerImplTest.manager.getWorkbenchDatasetById(new Long(result));
		Assert.assertNotNull(readDataset);
		Assert.assertEquals(testDataset, readDataset);
		WorkbenchDataManagerImplTest.manager.deleteWorkbenchDataset(testDataset);
	}

	@Test
	public void testGetWorkbenchRuntimeData() throws MiddlewareQueryException {
		WorkbenchRuntimeData result = WorkbenchDataManagerImplTest.manager.getWorkbenchRuntimeData();
		Assert.assertNotNull(result);
		Debug.println(MiddlewareIntegrationTest.INDENT, result.toString());
	}

	// FIXME : Seed directly to DB
	// @Test
	// public void testGetWorkbenchSetting() throws MiddlewareQueryException {
	// WorkbenchSetting result = manager.getWorkbenchSetting();
	// Assert.assertNotNull(result);
	// Debug.println(INDENT, result.toString());
	// }

	@Test
	public void testGetWorkflowTemplateByName() throws MiddlewareQueryException {
		String name = "Manager";
		List<WorkflowTemplate> results = WorkbenchDataManagerImplTest.manager.getWorkflowTemplateByName(name);
		Assert.assertNotNull(results);
		Assert.assertTrue(!results.isEmpty());
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, results);
	}

	@Test
	public void testGetWorkflowTemplatesList() throws MiddlewareQueryException {
		List<WorkflowTemplate> results = WorkbenchDataManagerImplTest.manager.getWorkflowTemplates();
		Assert.assertNotNull(results);
		Assert.assertTrue(!results.isEmpty());
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, results);
	}

	@Test
	public void testDeletePerson() throws MiddlewareQueryException {
		Person person = WorkbenchDataManagerImplTest.workbenchTestDataUtil.createTestPersonData();
		WorkbenchDataManagerImplTest.manager.addPerson(person);
		WorkbenchDataManagerImplTest.manager.deletePerson(person);
		Debug.println(MiddlewareIntegrationTest.INDENT, "Record is successfully deleted");
	}

	@Test
	public void testGetWorkflowTemplates() throws MiddlewareQueryException {
		List<WorkflowTemplate> results = WorkbenchDataManagerImplTest.manager.getWorkflowTemplates(0, 100);
		Assert.assertNotNull(results);
		Assert.assertTrue(!results.isEmpty());
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, results);
	}

	@Test
	public void testGetProjectUserInfoDao() throws MiddlewareQueryException {
		ProjectUserInfoDAO results = WorkbenchDataManagerImplTest.manager.getProjectUserInfoDao();
		Assert.assertNotNull(results);
		Debug.println(MiddlewareIntegrationTest.INDENT, results.toString());
	}

	@Test
	public void testGetToolDao() throws MiddlewareQueryException {
		ToolDAO results = WorkbenchDataManagerImplTest.manager.getToolDao();
		Assert.assertNotNull(results);
		Debug.println(MiddlewareIntegrationTest.INDENT, results.toString());
	}

	// FIXME : data will not seed
	// @Test
	// public void testGetUserInfo() throws MiddlewareQueryException {
	//
	// UserInfo results = manager.getUserInfo(testUser1.getUserid());
	// Assert.assertNotNull(results);
	// Debug.println(INDENT, results.toString());
	//
	// }

	@Test
	public void testGetToolsWithType() throws MiddlewareQueryException {
		List<Tool> results = WorkbenchDataManagerImplTest.manager.getToolsWithType(ToolType.NATIVE);
		Assert.assertNotNull(results);
		Assert.assertTrue(!results.isEmpty());
		Debug.printObjects(MiddlewareIntegrationTest.INDENT, results);
	}

	@Test
	public void testGetTemplateSettings() throws MiddlewareQueryException {

		TemplateSetting templateSetting = this.createTemplateSetting();

		WorkbenchDataManagerImplTest.manager.addTemplateSetting(templateSetting);
		Debug.println(MiddlewareIntegrationTest.INDENT, "Added TemplateSetting: " + templateSetting);

		Integer projectId = templateSetting.getProjectId();
		String name = templateSetting.getName();
		Tool tool = templateSetting.getTool();
		String configuration = templateSetting.getConfiguration();
		Boolean isDefault = templateSetting.isDefault();

		this.getTemplateSetting("project_id, name, tool, configuration", new TemplateSetting(null, projectId, name, tool, configuration,
				null));
		this.getTemplateSetting("project_id, tool, name", new TemplateSetting(null, projectId, name, tool, null, null));
		this.getTemplateSetting("project_id, tool, configuration", new TemplateSetting(null, projectId, null, tool, configuration, null));
		this.getTemplateSetting("project_id, tool, isDefault", new TemplateSetting(null, projectId, null, tool, null, isDefault));
		this.getTemplateSetting("name, tool, configuration", new TemplateSetting(null, null, name, tool, configuration, null));

		WorkbenchDataManagerImplTest.manager.deleteTemplateSetting(templateSetting);
		Debug.println(MiddlewareIntegrationTest.INDENT, "Database cleanup: templateSetting deleted.");
	}

	private void getTemplateSetting(String filterDescription, TemplateSetting templateSettingFilter) throws MiddlewareQueryException {
		List<TemplateSetting> settings = WorkbenchDataManagerImplTest.manager.getTemplateSettings(templateSettingFilter);
		Assert.assertTrue(settings.size() > 0);
		Debug.println(MiddlewareIntegrationTest.INDENT, "Retrieve records by " + filterDescription + ": #records = " + settings.size());
		Debug.printObjects(MiddlewareIntegrationTest.INDENT * 2, settings);
	}

	@Test
	public void testAddAndDeleteTemplateSettings() throws MiddlewareQueryException {

		TemplateSetting templateSetting = this.createTemplateSetting();

		WorkbenchDataManagerImplTest.manager.addTemplateSetting(templateSetting);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testAddTemplateSettings: " + templateSetting);

		Assert.assertNotNull(templateSetting.getTemplateSettingId());

		WorkbenchDataManagerImplTest.manager.deleteTemplateSetting(templateSetting);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testDeleteTemplateSettings: " + templateSetting);
	}

	@Test
	public void testDeleteTemplateSettingById() throws MiddlewareQueryException {

		TemplateSetting templateSetting = this.createTemplateSetting();

		WorkbenchDataManagerImplTest.manager.addTemplateSetting(templateSetting);
		Debug.println(MiddlewareIntegrationTest.INDENT, "TemplateSetting Added: " + templateSetting);

		Assert.assertNotNull(templateSetting.getTemplateSettingId());

		WorkbenchDataManagerImplTest.manager.deleteTemplateSetting(templateSetting.getTemplateSettingId());
		Debug.println(MiddlewareIntegrationTest.INDENT, "TemplateSetting Deleted: " + templateSetting);
	}

	@Test
	public void testUpdateTemplateSettings() throws MiddlewareQueryException {

		TemplateSetting templateSetting = this.createTemplateSetting();

		WorkbenchDataManagerImplTest.manager.addTemplateSetting(templateSetting);
		Debug.println(MiddlewareIntegrationTest.INDENT, "TemplateSetting added: " + templateSetting);

		Assert.assertNotNull(templateSetting.getTemplateSettingId());

		templateSetting.setIsDefault(!templateSetting.isDefault());
		templateSetting.setName(templateSetting.getName() + (int) (Math.random() * 100));

		WorkbenchDataManagerImplTest.manager.updateTemplateSetting(templateSetting);
		Debug.println(MiddlewareIntegrationTest.INDENT, "TemplateSetting updated: " + templateSetting);

		WorkbenchDataManagerImplTest.manager.deleteTemplateSetting(templateSetting);
		Debug.println(MiddlewareIntegrationTest.INDENT, "Database cleanup: templateSetting deleted.");

	}

	@Test
	public void testAddTemplateSettingsSameIsDefaultProjectAndTool() throws MiddlewareQueryException {

		TemplateSetting templateSetting1 = this.createTemplateSetting();
		templateSetting1.setIsDefault(Boolean.TRUE);

		TemplateSetting templateSetting2 = this.createTemplateSetting();
		templateSetting2.setIsDefault(Boolean.TRUE);

		WorkbenchDataManagerImplTest.manager.addTemplateSetting(templateSetting1);
		Debug.println(MiddlewareIntegrationTest.INDENT, "TemplateSetting1 added: " + templateSetting1);
		WorkbenchDataManagerImplTest.manager.addTemplateSetting(templateSetting2);
		Debug.println(MiddlewareIntegrationTest.INDENT, "TemplateSetting2 added: " + templateSetting2);
		Debug.println(MiddlewareIntegrationTest.INDENT, "TemplateSetting1 updated: " + templateSetting1);

		Assert.assertFalse(templateSetting1.isDefault());
		Assert.assertTrue(templateSetting2.isDefault());

		WorkbenchDataManagerImplTest.manager.deleteTemplateSetting(templateSetting1);
		WorkbenchDataManagerImplTest.manager.deleteTemplateSetting(templateSetting2);
		Debug.println(MiddlewareIntegrationTest.INDENT, "Database cleanup: template settings deleted.");

	}

	@Test
	public void testUpdateTemplateSettingsSameIsDefaultProjectAndTool() throws MiddlewareQueryException {

		TemplateSetting templateSetting1 = this.createTemplateSetting();
		templateSetting1.setIsDefault(Boolean.FALSE);

		TemplateSetting templateSetting2 = this.createTemplateSetting();
		templateSetting2.setIsDefault(Boolean.TRUE);

		WorkbenchDataManagerImplTest.manager.addTemplateSetting(templateSetting1);
		Debug.println(MiddlewareIntegrationTest.INDENT, "TemplateSetting1 added: " + templateSetting1);
		WorkbenchDataManagerImplTest.manager.addTemplateSetting(templateSetting2);
		Debug.println(MiddlewareIntegrationTest.INDENT, "TemplateSetting2 added: " + templateSetting2);

		templateSetting1.setIsDefault(Boolean.TRUE);
		WorkbenchDataManagerImplTest.manager.updateTemplateSetting(templateSetting1);
		Debug.println(MiddlewareIntegrationTest.INDENT, "TemplateSetting1 update: " + templateSetting1);
		Debug.println(MiddlewareIntegrationTest.INDENT, "TemplateSetting2 update: " + templateSetting2);

		Assert.assertTrue(templateSetting1.isDefault());
		Assert.assertFalse(templateSetting2.isDefault());

		WorkbenchDataManagerImplTest.manager.deleteTemplateSetting(templateSetting1);
		WorkbenchDataManagerImplTest.manager.deleteTemplateSetting(templateSetting2);
		Debug.println(MiddlewareIntegrationTest.INDENT, "Database cleanup: template settings deleted.");

	}

	private TemplateSetting createTemplateSetting() throws MiddlewareQueryException {
		Integer templateSettingId = null;
		Integer projectId = -1;
		Tool tool = WorkbenchDataManagerImplTest.manager.getToolWithName("nursery_manager_fieldbook_web");
		String name = "S9801-PLOT DATA_" + (int) (Math.random() * 1000);
		String configuration =
				new StringBuffer("<?xml version=\"1.0\"?>").append("<dataset>").append("<name>").append(name)
						.append("</name>                        ").append("<description>PLOT DATA FOR STUDY 1 OF 1998</description>  ")
						.append("<condition role=\"Study Information\" datatype=\"Character Variable\">").append("<name>PI</name>")
						.append("<description>PRINCIPAL INVESTIGATOR</description>").append("<property>PERSON</property>")
						.append("<method>ASSIGNED</method>").append("        <scale>DBCV</scale>").append("    </condition>")
						.append("    <factor role=\"Trial design information\" datatype=\"Numeric variable\">")
						.append("        <name>PLOT</name>").append("        <description>PLOT NUMBER</description>")
						.append("        <property>PLOT NUMBER</property>").append("        <method>ENUMERATED</method>")
						.append("        <scale>NUMBER</scale>").append("        <nestedin></nestedin>").append("    </factor>")
						.append("    <variate role=\"Observational variate\" datatype=\"Numeric variable\">")
						.append("        <name>YIELD</name>").append("        <description>GRAIN YIELD</description>")
						.append("        <property>GRAIN YIELD</property>").append("        <method>PADDY RICE</method>")
						.append("        <scale>kg/ha</scale>").append("        <samplelevel>PLOT</samplelevel>").append("    </variate>")
						.append("</dataset>").toString();
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

		StandardPreset results = WorkbenchDataManagerImplTest.manager.saveOrUpdateStandardPreset(preset);

		Assert.assertTrue("we retrieve the saved primary id", results.getStandardPresetId() > 0);

		Integer id = results.getStandardPresetId();

		// test retrieve from database using id
		StandardPreset retrievedResult = WorkbenchDataManagerImplTest.manager.getStandardPresetDAO().getById(id);

		Assert.assertEquals("we retrieved the correct object from database", results, retrievedResult);

		// we test deletion, also serves as cleanup
		WorkbenchDataManagerImplTest.manager.deleteStandardPreset(id);

		Assert.assertNull("standard preset with id=" + id + " should no longer exist", WorkbenchDataManagerImplTest.manager
				.getStandardPresetDAO().getById(id));
	}

	@Test
	public void testGetAllStandardPreset() throws Exception {
		List<StandardPreset> out = WorkbenchDataManagerImplTest.manager.getStandardPresetDAO().getAll();

		// TODO : Are we expecting any preloaded data here ? There is no such insert query in merger-db scripts.
		// Reviewer : Naymesh
		Assert.assertTrue(out.size() == 0);

	}

	@Test
	public void testGetStandardPresetFromCropAndTool() throws Exception {
		List<StandardPreset> fulllist = this.initializeStandardPresets();

		for (int j = 1; j < 3; j++) {
			List<StandardPreset> presetsList = WorkbenchDataManagerImplTest.manager.getStandardPresetFromCropAndTool("crop_name_" + j, j);
			for (StandardPreset p : presetsList) {
				Assert.assertEquals("should only retrieve all standard presets with crop_name_1", "crop_name_" + j, p.getCropName());
				Assert.assertEquals("should be the same tool as requested", Integer.valueOf(j), p.getToolId());
			}
		}

		// cleanup
		for (StandardPreset p : fulllist) {
			WorkbenchDataManagerImplTest.manager.deleteStandardPreset(p.getStandardPresetId());
		}
	}

	@Test
	public void testGetStandardPresetFromCropAndToolAndToolSection() throws Exception {
		List<StandardPreset> fulllist = this.initializeStandardPresets();

		for (int j = 1; j < 3; j++) {
			List<StandardPreset> presetsList =
					WorkbenchDataManagerImplTest.manager.getStandardPresetFromCropAndTool("crop_name_" + j, j, "tool_section_" + j);
			for (StandardPreset p : presetsList) {
				Assert.assertEquals("should only retrieve all standard presets with same crop name", "crop_name_" + j, p.getCropName());
				Assert.assertEquals("should only retrieve all standard presets with same tool section", "tool_section_" + j,
						p.getToolSection());
				Assert.assertEquals("should be the same tool as requested", Integer.valueOf(j), p.getToolId());
			}
		}

		// cleanup
		for (StandardPreset p : fulllist) {
			WorkbenchDataManagerImplTest.manager.deleteStandardPreset(p.getStandardPresetId());
		}
	}

	@Test
	public void testGetStandardPresetFromProgramAndToolByName() throws Exception {
		List<StandardPreset> fullList = this.initializeStandardPresets();

		// this should exists
		List<StandardPreset> result =
				WorkbenchDataManagerImplTest.manager.getStandardPresetFromCropAndToolByName("configuration_1_1", "crop_name_1", 1,
						"tool_section_1");

		Assert.assertTrue("result should not be empty", result.size() > 0);
		Assert.assertEquals("Should return the same name", "configuration_1_1", result.get(0).getName());

		// cleanup
		for (StandardPreset p : fullList) {
			WorkbenchDataManagerImplTest.manager.deleteStandardPreset(p.getStandardPresetId());
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

				fulllist.add(WorkbenchDataManagerImplTest.manager.saveOrUpdateStandardPreset(preset));
			}
		}

		return fulllist;
	}

}
