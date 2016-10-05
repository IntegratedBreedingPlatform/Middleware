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

import static org.hamcrest.MatcherAssert.assertThat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.WorkbenchTestDataUtil;
import org.generationcp.middleware.dao.ProjectUserInfoDAO;
import org.generationcp.middleware.dao.ToolDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.presets.StandardPreset;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.IbdbUserMap;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectActivity;
import org.generationcp.middleware.pojos.workbench.ProjectUserMysqlAccount;
import org.generationcp.middleware.pojos.workbench.ProjectUserRole;
import org.generationcp.middleware.pojos.workbench.Role;
import org.generationcp.middleware.pojos.workbench.TemplateSetting;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.ToolConfiguration;
import org.generationcp.middleware.pojos.workbench.ToolType;
import org.generationcp.middleware.pojos.workbench.UserRole;
import org.generationcp.middleware.pojos.workbench.WorkbenchDataset;
import org.generationcp.middleware.pojos.workbench.WorkbenchRuntimeData;
import org.generationcp.middleware.pojos.workbench.WorkflowTemplate;
import org.generationcp.middleware.service.api.user.UserDto;
import org.generationcp.middleware.utils.test.Debug;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;

public class WorkbenchDataManagerImplTest extends IntegrationTestBase {

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	private WorkbenchTestDataUtil workbenchTestDataUtil;
	private Project commonTestProject;
	private User testUser1;

	@Before
	public void beforeTest() throws MiddlewareQueryException {
		if (this.workbenchTestDataUtil == null) {
			this.workbenchTestDataUtil = new WorkbenchTestDataUtil(this.workbenchDataManager);
			this.workbenchTestDataUtil.setUpWorkbench();
		}

		if (this.commonTestProject == null) {
			this.commonTestProject = this.workbenchTestDataUtil.getCommonTestProject();
		}

		if (this.testUser1 == null) {
			this.testUser1 = this.workbenchTestDataUtil.getTestUser1();
		}
	}

	@Test
	public void testAddUser() throws MiddlewareQueryException {
		User user = this.workbenchTestDataUtil.createTestUserData();
		Integer result = this.workbenchDataManager.addUser(user);
		Assert.assertNotNull("Expected id of a newly saved record in workbench_user.", result);

		User readUser = this.workbenchDataManager.getUserById(result);
		Assert.assertEquals(user.getName(), readUser.getName());
	}

	@Test
	public void testAddPerson() throws MiddlewareQueryException {
		Person person = this.workbenchTestDataUtil.createTestPersonData();
		Integer result = this.workbenchDataManager.addPerson(person);
		Assert.assertNotNull("Expected id of a newly saved record in persons.", result);

		Person readPerson = this.workbenchDataManager.getPersonById(result);
		Assert.assertEquals(person.getLastName(), readPerson.getLastName());
	}

	@Test
	public void testAddUsersWithRoles() throws MiddlewareQueryException {
		// Admin
		Person adminPerson = new Person();
		adminPerson.setFirstName("Naymesh");
		adminPerson.setMiddleName("-");
		adminPerson.setLastName("Mistry");
		adminPerson.setEmail("naymesh@leafnode.io");
		adminPerson.setInstituteId(0);
		adminPerson.setPositionName("-");
		adminPerson.setTitle("-");
		adminPerson.setExtension("-");
		adminPerson.setFax("-");
		adminPerson.setNotes("-");
		adminPerson.setContact("-");
		adminPerson.setLanguage(0);
		adminPerson.setPhone("-");
		Integer adminPersonId = this.workbenchDataManager.addPerson(adminPerson);
		Assert.assertNotNull("Expected id of a newly saved record in persons.", adminPersonId);

		User adminUser = new User();
		adminUser.setName("admin");
		adminUser.setPassword("b");
		adminUser.setPersonid(adminPersonId);
		adminUser.setInstalid(0);
		adminUser.setStatus(0);
		adminUser.setAccess(0);
		adminUser.setType(0);
		adminUser.setAssignDate(20140101);
		adminUser.setCloseDate(20140101);

		List<UserRole> adminRoles = new ArrayList<UserRole>();
		adminRoles.add(new UserRole(adminUser, "ADMIN"));
		adminUser.setRoles(adminRoles);
		this.workbenchDataManager.addUser(adminUser);
		Assert.assertNotNull("Expected id of a newly saved record in users.", adminUser.getUserid());

		// Breeder
		Person breederPerson = new Person();
		breederPerson.setFirstName("Matthew");
		breederPerson.setMiddleName("-");
		breederPerson.setLastName("Berrigan");
		breederPerson.setEmail("matthew@leafnode.io");
		breederPerson.setInstituteId(0);
		breederPerson.setPositionName("-");
		breederPerson.setTitle("-");
		breederPerson.setExtension("-");
		breederPerson.setFax("-");
		breederPerson.setNotes("-");
		breederPerson.setContact("-");
		breederPerson.setLanguage(0);
		breederPerson.setPhone("-");
		Integer breederPersonId = this.workbenchDataManager.addPerson(breederPerson);
		Assert.assertNotNull("Expected id of a newly saved record in persons.", breederPersonId);

		User breederUser = new User();
		breederUser.setName("breeder");
		breederUser.setPassword("b");
		breederUser.setPersonid(breederPersonId);
		breederUser.setInstalid(0);
		breederUser.setStatus(0);
		breederUser.setAccess(0);
		breederUser.setType(0);
		breederUser.setAssignDate(20140101);
		breederUser.setCloseDate(20140101);

		List<UserRole> breederRoles = new ArrayList<UserRole>();
		breederRoles.add(new UserRole(breederUser, "BREEDER"));
		breederUser.setRoles(breederRoles);
		this.workbenchDataManager.addUser(breederUser);
		Assert.assertNotNull("Expected id of a newly saved record in users.", adminUser.getUserid());

		// Technician
		Person technicianPerson = new Person();
		technicianPerson.setFirstName("Lisa");
		technicianPerson.setMiddleName("-");
		technicianPerson.setLastName("Quayle");
		technicianPerson.setEmail("lisa@leafnode.io");
		technicianPerson.setInstituteId(0);
		technicianPerson.setPositionName("-");
		technicianPerson.setTitle("-");
		technicianPerson.setExtension("-");
		technicianPerson.setFax("-");
		technicianPerson.setNotes("-");
		technicianPerson.setContact("-");
		technicianPerson.setLanguage(0);
		technicianPerson.setPhone("-");
		Integer technicianPersonId = this.workbenchDataManager.addPerson(technicianPerson);
		Assert.assertNotNull("Expected id of a newly saved record in persons.", technicianPersonId);

		User technicianUser = new User();
		technicianUser.setName("technician");
		technicianUser.setPassword("b");
		technicianUser.setPersonid(technicianPersonId);
		technicianUser.setInstalid(0);
		technicianUser.setStatus(0);
		technicianUser.setAccess(0);
		technicianUser.setType(0);
		technicianUser.setAssignDate(20140101);
		technicianUser.setCloseDate(20140101);

		List<UserRole> technicianRoles = new ArrayList<UserRole>();
		technicianRoles.add(new UserRole(technicianUser, "TECHNICIAN"));
		technicianUser.setRoles(technicianRoles);
		this.workbenchDataManager.addUser(technicianUser);
		Assert.assertNotNull("Expected id of a newly saved record in users.", technicianUser.getUserid());
	}

	@Test
	public void testAddProject() throws MiddlewareQueryException {
		final Project project = this.workbenchTestDataUtil.createTestProjectData();
		this.workbenchDataManager.addProject(project);
		Assert.assertNotNull("Expected id of a newly saved record in workbench_project.", project.getProjectId());

		Project readProject = this.workbenchDataManager.getProjectById(project.getProjectId());
		Assert.assertEquals(project, readProject);
	}

	@Test
	public void testProjectActivity() throws MiddlewareQueryException {
		ProjectActivity projectActivity = this.workbenchTestDataUtil.createTestProjectActivityData(this.commonTestProject, this.testUser1);
		Integer result = this.workbenchDataManager.addProjectActivity(projectActivity);
		Assert.assertNotNull("Expected id of a newly saved record in workbench_project_activity", result);

		List<ProjectActivity> results =
				this.workbenchDataManager.getProjectActivitiesByProjectId(this.commonTestProject.getProjectId(), 0, 10);
		Assert.assertNotNull(results);
		Assert.assertEquals(3, results.size());

		long count = this.workbenchDataManager.countProjectActivitiesByProjectId(this.commonTestProject.getProjectId());
		Assert.assertEquals(3, count);
	}

	@Test
	public void testIbdbUserMap() throws MiddlewareQueryException {
		IbdbUserMap userMap = new IbdbUserMap();
		userMap.setProjectId(this.commonTestProject.getProjectId());
		userMap.setIbdbUserId(this.testUser1.getUserid() * -1);
		userMap.setWorkbenchUserId(this.testUser1.getUserid());

		Integer result = this.workbenchDataManager.addIbdbUserMap(userMap);
		Assert.assertNotNull("Expected id of a newly saved record in workbench_ibdb_user_map", result);

		Integer cropDBUserId =
				this.workbenchDataManager.getLocalIbdbUserId(this.testUser1.getUserid(), this.commonTestProject.getProjectId());
		Assert.assertNotNull(cropDBUserId);
		
		// Try to add a duplicate IbdbUserMap (same workbench project and user id).
		IbdbUserMap duplicateUserMap = new IbdbUserMap();
		duplicateUserMap.setProjectId(userMap.getProjectId());
		duplicateUserMap.setIbdbUserId(userMap.getIbdbUserId());
		duplicateUserMap.setWorkbenchUserId(userMap.getWorkbenchUserId());
		Integer result2 = this.workbenchDataManager.addIbdbUserMap(duplicateUserMap);
		Assert.assertEquals("Expected existing record's id being returned when trying to add duplicate entry in workbench_ibdb_user_map",
				result, result2);
		Integer cropDBUserId2 =
				this.workbenchDataManager.getLocalIbdbUserId(this.testUser1.getUserid(), this.commonTestProject.getProjectId());
		Assert.assertEquals(
				"Expected workbench user to map to same crop db record after attempting to add duplicate entry in workbench_ibdb_user_map",
				cropDBUserId, cropDBUserId2);
	
	}

	@Test
	public void testGetProjects() throws MiddlewareQueryException {
		List<Project> projects = this.workbenchDataManager.getProjects();
		Assert.assertNotNull(projects);
		Assert.assertTrue(!projects.isEmpty());
	}

	@Test
	public void testGetToolWithName() throws MiddlewareQueryException {
		String toolName = "fieldbook_web";
		Tool tool = this.workbenchDataManager.getToolWithName(toolName);
		Assert.assertNotNull(tool);
	}

	@Test
	public void testGetProjectByName() throws MiddlewareQueryException {
		final Project project = this.workbenchDataManager.getProjectByNameAndCrop(this.commonTestProject.getProjectName(), this
				.commonTestProject.getCropType());
		Assert.assertEquals(this.commonTestProject.getProjectName(), project.getProjectName());
	}

	@Test
	public void testGetUserByName() throws MiddlewareQueryException {
		User user = this.workbenchDataManager.getUserByName(this.testUser1.getName(), 0, 1, Operation.EQUAL).get(0);
		Assert.assertEquals(this.testUser1.getName(), user.getName());
		Assert.assertEquals(this.testUser1.getUserid(), user.getUserid());
	}

	@Test
	public void testWorkbenchDataset() throws MiddlewareQueryException {
		WorkbenchDataset dataset1 = this.createTestWorkbenchDataset(this.commonTestProject);
		Integer result = this.workbenchDataManager.addWorkbenchDataset(dataset1);
		Assert.assertNotNull("Expected id of the newly added record in workbench_dataset", result);

		List<WorkbenchDataset> list =
				this.workbenchDataManager.getWorkbenchDatasetByProjectId(this.commonTestProject.getProjectId(), 0, 10);
		Assert.assertTrue(list.contains(dataset1));

		WorkbenchDataset dataset2 = this.createTestWorkbenchDataset(this.commonTestProject);
		this.workbenchDataManager.addWorkbenchDataset(dataset2);

		long count = this.workbenchDataManager.countWorkbenchDatasetByProjectId(this.commonTestProject.getProjectId());
		Assert.assertEquals(2, count);
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
	public void testProjectUserRoles() throws MiddlewareQueryException {

		Role role1 = this.workbenchDataManager.getAllRoles().get(0);
		Role role2 = this.workbenchDataManager.getAllRoles().get(1);

		ProjectUserRole projUsrRole1 = new ProjectUserRole(this.commonTestProject, this.testUser1, role1);
		ProjectUserRole projUsrRole2 = new ProjectUserRole(this.commonTestProject, this.testUser1, role2);

		List<ProjectUserRole> projectUserRoles = new ArrayList<>();
		projectUserRoles.add(projUsrRole1);
		projectUserRoles.add(projUsrRole2);

		List<Integer> rolesAdded = this.workbenchDataManager.addProjectUserRole(projectUserRoles);
		Assert.assertEquals(2, rolesAdded.size());

		long result = this.workbenchDataManager.countUsersByProjectId(this.commonTestProject.getProjectId());
		Assert.assertEquals(1, result);

		List<Project> results = this.workbenchDataManager.getProjectsByUser(this.testUser1);
		Assert.assertNotNull(results);
		Assert.assertTrue(!results.isEmpty());

		List<ProjectUserRole> results2 = this.workbenchDataManager.getProjectUserRolesByProject(this.commonTestProject);
		Assert.assertNotNull(results2);
		Assert.assertTrue(!results2.isEmpty());

		List<Role> roles = this.workbenchDataManager.getRolesByProjectAndUser(this.commonTestProject, this.testUser1);
		Assert.assertTrue(!roles.isEmpty());
		Assert.assertEquals(2, roles.size());

		List<User> users = this.workbenchDataManager.getUsersByProjectId(this.commonTestProject.getProjectId());
		Assert.assertNotNull(users);
		Assert.assertEquals(1, users.size());
		Assert.assertEquals(this.testUser1, users.get(0));
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

		this.workbenchDataManager.addToolConfiguration(toolConfig);
		Debug.println(IntegrationTestBase.INDENT, "testAddToolConfiguration(toolId=" + toolId + "): " + toolConfig);
	}

	@Test
	public void testGetListOfToolConfigurationsByToolId() throws MiddlewareQueryException {
		Long toolId = 1L;
		List<ToolConfiguration> result = this.workbenchDataManager.getListOfToolConfigurationsByToolId(toolId);
		Debug.println(IntegrationTestBase.INDENT, "testGetListOfToolConfigurationsByToolId(" + toolId + "): ");

		if (result.isEmpty()) {
			Debug.println(IntegrationTestBase.INDENT, "  No records found.");
		} else {
			for (ToolConfiguration t : result) {
				Debug.println(IntegrationTestBase.INDENT, t);
			}
		}
	}

	@Test
	public void testGetToolConfigurationByToolIdAndConfigKey() throws MiddlewareQueryException {
		Long toolId = 1L;
		String configKey = "test";
		ToolConfiguration toolConfig = this.workbenchDataManager.getToolConfigurationByToolIdAndConfigKey(toolId, configKey);
		Debug.println(IntegrationTestBase.INDENT, "testGetToolConfigurationByToolIdAndConfigKey(toolId=" + toolId + ", configKey="
				+ configKey + "): " + toolConfig);
	}

	@Test
	public void testCropType() throws MiddlewareQueryException {
		String cropName = "Coconut";
		CropType cropType = new CropType(cropName);
		String added = this.workbenchDataManager.addCropType(cropType);
		Assert.assertNotNull(added);

		List<CropType> cropTypes = this.workbenchDataManager.getInstalledCropDatabses();
		Assert.assertNotNull(cropTypes);
		Assert.assertTrue(cropTypes.size() >= 1);

		CropType cropTypeRead = this.workbenchDataManager.getCropTypeByName(cropName);
		Assert.assertNotNull(cropTypeRead);
		Assert.assertEquals(cropType, cropTypeRead);
	}

	@Test
	public void testGetRoleById() throws MiddlewareQueryException {
		Integer id = 1; // Assumption: there is a role with id 1
		Role role = this.workbenchDataManager.getRoleById(id);
		Assert.assertNotNull(role);
		Debug.println(IntegrationTestBase.INDENT, "testGetRoleById(id=" + id + "): \n  " + role);
	}

	@Test
	public void testGetRoleByNameAndWorkflowTemplate() throws MiddlewareQueryException {
		String templateName = "MARS";
		String roleName = "MARS Breeder";
		WorkflowTemplate workflowTemplate = this.workbenchDataManager.getWorkflowTemplateByName(templateName).get(0);
		Role role = this.workbenchDataManager.getRoleByNameAndWorkflowTemplate(roleName, workflowTemplate);
		Assert.assertNotNull(role);
		Debug.println(IntegrationTestBase.INDENT, "testGetRoleByNameAndWorkflowTemplate(name=" + roleName + ", workflowTemplate="
				+ workflowTemplate.getName() + "): \n  " + role);
	}

	@Test
	public void testGetRolesByWorkflowTemplate() throws MiddlewareQueryException {
		WorkflowTemplate workflowTemplate = this.workbenchDataManager.getWorkflowTemplates().get(0); // get the
		// first
		// template
		// in
		// the db
		List<Role> roles = this.workbenchDataManager.getRolesByWorkflowTemplate(workflowTemplate);
		Assert.assertNotNull(roles);
		Assert.assertTrue(!roles.isEmpty());
		Debug.println(IntegrationTestBase.INDENT, "testGetRolesByWorkflowTemplate(workflowTemplate=" + workflowTemplate.getName() + "): "
				+ roles.size());
		for (Role role : roles) {
			Debug.println(IntegrationTestBase.INDENT, "  " + role);
		}
	}

	@Test
	public void testGetWorkflowTemplateByRole() throws MiddlewareQueryException {
		Role role = this.workbenchDataManager.getRoleById(this.workbenchDataManager.getAllRoles().get(0).getRoleId());
		WorkflowTemplate template = this.workbenchDataManager.getWorkflowTemplateByRole(role);
		Assert.assertNotNull(template);
		Debug.println(IntegrationTestBase.INDENT, "testGetWorkflowTemplateByRole(role=" + role.getName() + "): \n  " + template);
	}

	@Test
	public void testGetAllRoles() throws MiddlewareQueryException {
		List<Role> roles = this.workbenchDataManager.getAllRoles();
		Assert.assertNotNull(roles);
		Assert.assertTrue(!roles.isEmpty());
	}

	@Test
	public void testProjectUserMysqlAccount() throws MiddlewareQueryException {
		ProjectUserMysqlAccount recordToSave = new ProjectUserMysqlAccount();
		recordToSave.setProject(this.commonTestProject);
		recordToSave.setUser(this.testUser1);
		recordToSave.setMysqlUsername("sample " + new Random().nextInt(10000));
		recordToSave.setMysqlPassword("password");

		Integer idSaved = this.workbenchDataManager.addProjectUserMysqlAccount(recordToSave);
		Assert.assertNotNull("Expected id of the newly saved record in workbench_project_user_mysql_account", idSaved);
		Debug.println(IntegrationTestBase.INDENT, "Id of record saved: " + idSaved);

		ProjectUserMysqlAccount record =
				this.workbenchDataManager.getProjectUserMysqlAccountByProjectIdAndUserId(this.commonTestProject.getProjectId().intValue(),
						this.testUser1.getUserid());
		Assert.assertNotNull(record);
		Assert.assertEquals(this.commonTestProject.getProjectId(), record.getProject().getProjectId());
		Assert.assertEquals(this.testUser1.getUserid(), record.getUser().getUserid());
		Debug.println(IntegrationTestBase.INDENT, record.toString());
	}

	@Test
	public void testCountAllPersons() throws MiddlewareQueryException {
		long count = this.workbenchDataManager.countAllPersons();
		Assert.assertTrue(count > 0);
	}

	@Test
	public void testCountAllUsers() throws MiddlewareQueryException {
		long count = this.workbenchDataManager.countAllUsers();
		Assert.assertTrue(count > 0);
	}

	@Test
	public void testGetAllPersons() throws MiddlewareQueryException {
		List<Person> results = this.workbenchDataManager.getAllPersons();
		Assert.assertNotNull(results);
		Assert.assertTrue(!results.isEmpty());
	}

	@Test
	public void testGetAllRolesDesc() throws MiddlewareQueryException {
		List<Role> results = this.workbenchDataManager.getAllRolesDesc();
		Assert.assertNotNull(results);
		Assert.assertTrue(!results.isEmpty());
	}

	@Test
	public void testGetAllRolesOrderedByLabel() throws MiddlewareQueryException {
		List<Role> results = this.workbenchDataManager.getAllRolesOrderedByLabel();
		Assert.assertNotNull(results);
		Assert.assertTrue(!results.isEmpty());
	}

	@Test
	public void testGetAllTools() throws MiddlewareQueryException {
		List<Tool> results = this.workbenchDataManager.getAllTools();
		Assert.assertNotNull(results);
		Assert.assertTrue(!results.isEmpty());
	}

	@Test
	public void testGetAllUsers() throws MiddlewareQueryException {
		List<User> results = this.workbenchDataManager.getAllUsers();
		Assert.assertNotNull(results);
		Assert.assertTrue(!results.isEmpty());
	}

	@Test
	public void testGetAllUsersSorted() throws MiddlewareQueryException {
		List<User> results = this.workbenchDataManager.getAllUsersSorted();
		Assert.assertNotNull(results);
		Assert.assertTrue(!results.isEmpty());
	}

	@Test
	public void testGetLastOpenedProject() throws MiddlewareQueryException {
		Project results = this.workbenchDataManager.getLastOpenedProject(this.testUser1.getUserid());
		Assert.assertNotNull(results);
	}

	@Test
	public void testGetProjectsList() throws MiddlewareQueryException {
		List<Project> results = this.workbenchDataManager.getProjects(0, 100);
		Assert.assertNotNull(results);
		Assert.assertTrue(!results.isEmpty());
	}

	@Test
	public void testGetUserById() throws MiddlewareQueryException {
		User user = this.workbenchDataManager.getUserById(this.testUser1.getUserid());
		Assert.assertNotNull(user);
	}

	@Test
	public void testGetWorkbenchDatasetById() throws MiddlewareQueryException {
		WorkbenchDataset testDataset = this.createTestWorkbenchDataset(this.commonTestProject);
		Integer result = this.workbenchDataManager.addWorkbenchDataset(testDataset);

		WorkbenchDataset readDataset = this.workbenchDataManager.getWorkbenchDatasetById(new Long(result));
		Assert.assertNotNull(readDataset);
		Assert.assertEquals(testDataset, readDataset);
	}

	@Test
	public void testGetWorkbenchRuntimeData() throws MiddlewareQueryException {
		WorkbenchRuntimeData result = this.workbenchDataManager.getWorkbenchRuntimeData();
		Assert.assertNotNull(result);
	}

	@Test
	public void testGetWorkflowTemplateByName() throws MiddlewareQueryException {
		String name = "Manager";
		List<WorkflowTemplate> results = this.workbenchDataManager.getWorkflowTemplateByName(name);
		Assert.assertNotNull(results);
		Assert.assertTrue(!results.isEmpty());
	}

	@Test
	public void testGetWorkflowTemplatesList() throws MiddlewareQueryException {
		List<WorkflowTemplate> results = this.workbenchDataManager.getWorkflowTemplates();
		Assert.assertNotNull(results);
		Assert.assertTrue(!results.isEmpty());
	}

	@Test
	public void testDeletePerson() throws MiddlewareQueryException {
		Person person = this.workbenchTestDataUtil.createTestPersonData();
		this.workbenchDataManager.addPerson(person);
		this.workbenchDataManager.deletePerson(person);
	}

	@Test
	public void testGetWorkflowTemplates() throws MiddlewareQueryException {
		List<WorkflowTemplate> results = this.workbenchDataManager.getWorkflowTemplates(0, 100);
		Assert.assertNotNull(results);
		Assert.assertTrue(!results.isEmpty());
	}

	@Test
	public void testGetProjectUserInfoDao() throws MiddlewareQueryException {
		ProjectUserInfoDAO results = this.workbenchDataManager.getProjectUserInfoDao();
		Assert.assertNotNull(results);
	}

	@Test
	public void testGetToolDao() throws MiddlewareQueryException {
		ToolDAO results = this.workbenchDataManager.getToolDao();
		Assert.assertNotNull(results);
		Debug.println(IntegrationTestBase.INDENT, results.toString());
	}

	@Test
	public void testGetToolsWithType() throws MiddlewareQueryException {
		List<Tool> results = this.workbenchDataManager.getToolsWithType(ToolType.NATIVE);
		Assert.assertNotNull(results);
		Assert.assertTrue(!results.isEmpty());
		Debug.printObjects(IntegrationTestBase.INDENT, results);
	}

	@Test
	public void testGetTemplateSettings() throws MiddlewareQueryException {

		TemplateSetting templateSetting = this.createTemplateSetting();

		this.workbenchDataManager.addTemplateSetting(templateSetting);
		Debug.println(IntegrationTestBase.INDENT, "Added TemplateSetting: " + templateSetting);

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

	}

	private void getTemplateSetting(String filterDescription, TemplateSetting templateSettingFilter) throws MiddlewareQueryException {
		List<TemplateSetting> settings = this.workbenchDataManager.getTemplateSettings(templateSettingFilter);
		Assert.assertTrue(settings.size() > 0);
		Debug.println(IntegrationTestBase.INDENT, "Retrieve records by " + filterDescription + ": #records = " + settings.size());
		Debug.printObjects(IntegrationTestBase.INDENT * 2, settings);
	}

	@Test
	public void testAddAndDeleteTemplateSettings() throws MiddlewareQueryException {
		TemplateSetting templateSetting = this.createTemplateSetting();

		this.workbenchDataManager.addTemplateSetting(templateSetting);
		Debug.println(IntegrationTestBase.INDENT, "testAddTemplateSettings: " + templateSetting);

		Assert.assertNotNull(templateSetting.getTemplateSettingId());
	}

	@Test
	public void testDeleteTemplateSettingById() throws MiddlewareQueryException {
		TemplateSetting templateSetting = this.createTemplateSetting();
		this.workbenchDataManager.addTemplateSetting(templateSetting);
		Assert.assertNotNull(templateSetting.getTemplateSettingId());
	}

	@Test
	public void testUpdateTemplateSettings() throws MiddlewareQueryException {

		TemplateSetting templateSetting = this.createTemplateSetting();
		this.workbenchDataManager.addTemplateSetting(templateSetting);
		Assert.assertNotNull(templateSetting.getTemplateSettingId());
		templateSetting.setIsDefault(!templateSetting.isDefault());

		String newName = templateSetting.getName() + (int) (Math.random() * 100);
		templateSetting.setName(newName);
		this.workbenchDataManager.updateTemplateSetting(templateSetting);
	}

	@Test
	public void testAddTemplateSettingsSameIsDefaultProjectAndTool() throws MiddlewareQueryException {

		TemplateSetting templateSetting1 = this.createTemplateSetting();
		templateSetting1.setIsDefault(Boolean.TRUE);

		TemplateSetting templateSetting2 = this.createTemplateSetting();
		templateSetting2.setIsDefault(Boolean.TRUE);

		this.workbenchDataManager.addTemplateSetting(templateSetting1);
		Debug.println(IntegrationTestBase.INDENT, "TemplateSetting1 added: " + templateSetting1);

		this.workbenchDataManager.addTemplateSetting(templateSetting2);
		Debug.println(IntegrationTestBase.INDENT, "TemplateSetting2 added: " + templateSetting2);

		// When a new template is added with default flag on, any other templates that are existing for the same project and tool must be
		// set as non-default.
		Assert.assertFalse(templateSetting1.isDefault());
		Assert.assertTrue(templateSetting2.isDefault());
	}

	@Test
	public void testUpdateTemplateSettingsSameIsDefaultProjectAndTool() throws MiddlewareQueryException {

		TemplateSetting templateSetting1 = this.createTemplateSetting();
		templateSetting1.setIsDefault(Boolean.FALSE);

		TemplateSetting templateSetting2 = this.createTemplateSetting();
		templateSetting2.setIsDefault(Boolean.TRUE);

		this.workbenchDataManager.addTemplateSetting(templateSetting1);
		Debug.println(IntegrationTestBase.INDENT, "TemplateSetting1 added: " + templateSetting1);

		this.workbenchDataManager.addTemplateSetting(templateSetting2);
		Debug.println(IntegrationTestBase.INDENT, "TemplateSetting2 added: " + templateSetting2);

		Assert.assertFalse(templateSetting1.isDefault());
		Assert.assertTrue(templateSetting2.isDefault());

		templateSetting1.setIsDefault(Boolean.TRUE);
		this.workbenchDataManager.updateTemplateSetting(templateSetting1);
		Debug.println(IntegrationTestBase.INDENT, "TemplateSetting1 updated: " + templateSetting1);

		// When a new template is added with default flag on, any other templates that are existing for the same project and tool must be
		// set as non-default.
		Assert.assertTrue(templateSetting1.isDefault());
		Assert.assertFalse(templateSetting2.isDefault());

	}

	private TemplateSetting createTemplateSetting() throws MiddlewareQueryException {
		Integer templateSettingId = null;
		Integer projectId = 1;
		Tool tool = this.workbenchDataManager.getToolWithName("nursery_manager_fieldbook_web");
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
	public void testStandardPreset() throws Exception {
		StandardPreset preset = new StandardPreset();
		preset.setConfiguration("<configuration/>");
		preset.setName("configuration_01");
		preset.setToolId(1);
		preset.setCropName("crop_name");

		StandardPreset results = this.workbenchDataManager.saveOrUpdateStandardPreset(preset);
		Assert.assertTrue("we retrieve the saved primary id", results.getStandardPresetId() > 0);

		Integer id = results.getStandardPresetId();
		StandardPreset retrievedResult = this.workbenchDataManager.getStandardPresetDAO().getById(id);
		Assert.assertEquals("we retrieved the correct object from database", results, retrievedResult);

		List<StandardPreset> out = this.workbenchDataManager.getStandardPresetDAO().getAll();
		Assert.assertTrue(!out.isEmpty());
	}

	@Test
	public void testGetAllStandardPreset() throws Exception {
		List<StandardPreset> out = this.workbenchDataManager.getStandardPresetDAO().getAll();
		Assert.assertTrue(out.size() > 0);
	}

	@Test
	public void testGetStandardPresetFromCropAndTool() throws Exception {
		this.initializeStandardPresets();

		for (int j = 1; j < 3; j++) {
			List<StandardPreset> presetsList = this.workbenchDataManager.getStandardPresetFromCropAndTool("crop_name_" + j, j);
			for (StandardPreset p : presetsList) {
				Assert.assertEquals("should only retrieve all standard presets with crop_name_1", "crop_name_" + j, p.getCropName());
				Assert.assertEquals("should be the same tool as requested", Integer.valueOf(j), p.getToolId());
			}
		}

	}

	@Test
	public void testGetStandardPresetFromCropAndToolAndToolSection() throws Exception {
		this.initializeStandardPresets();

		for (int j = 1; j < 3; j++) {
			List<StandardPreset> presetsList =
					this.workbenchDataManager.getStandardPresetFromCropAndTool("crop_name_" + j, j, "tool_section_" + j);
			for (StandardPreset p : presetsList) {
				Assert.assertEquals("should only retrieve all standard presets with same crop name", "crop_name_" + j, p.getCropName());
				Assert.assertEquals("should only retrieve all standard presets with same tool section", "tool_section_" + j,
						p.getToolSection());
				Assert.assertEquals("should be the same tool as requested", Integer.valueOf(j), p.getToolId());
			}
		}
	}

	@Test
	public void testGetStandardPresetFromProgramAndToolByName() throws Exception {
		this.initializeStandardPresets();

		// this should exists
		List<StandardPreset> result =
				this.workbenchDataManager.getStandardPresetFromCropAndToolByName("configuration_1_1", "crop_name_1", 1, "tool_section_1");

		Assert.assertTrue("result should not be empty", result.size() > 0);
		Assert.assertEquals("Should return the same name", "configuration_1_1", result.get(0).getName());
	}

	protected List<StandardPreset> initializeStandardPresets() throws MiddlewareQueryException {
		List<StandardPreset> fulllist = new ArrayList<>();
		for (int j = 1; j < 3; j++) {
			for (int i = 1; i < 6; i++) {
				StandardPreset preset = new StandardPreset();
				preset.setConfiguration("<configuration/>");
				preset.setName("configuration_" + j + "_" + i);
				preset.setToolId(j);
				preset.setCropName("crop_name_" + j);
				preset.setToolSection("tool_section_" + j);

				fulllist.add(this.workbenchDataManager.saveOrUpdateStandardPreset(preset));
			}
		}
		return fulllist;
	}


	@Test
	public void testGetAllUserDtosSorted() throws MiddlewareQueryException {
		final UserDto user = this.workbenchTestDataUtil.createTestUserDTO(25);
		final List<UserDto> users = Lists.newArrayList(user);
		final WorkbenchDataManager workbenchDataManager = Mockito.mock(WorkbenchDataManager.class);

		Mockito.when(workbenchDataManager.getAllUsersSortedByLastName()).thenReturn(users);
		assertThat("Expected list users not null.", users != null);
		assertThat("Expected list users not empty.", !users.isEmpty());
		assertThat("Expected list users size 1.", users.size() == 1);

	}

	@Test
	public void testCreateUser() throws MiddlewareQueryException {
		final UserDto userDto = this.workbenchTestDataUtil.createTestUserDTO(0);
		final Integer result = this.workbenchDataManager.createUser(userDto);

		assertThat("Expected id of a newly saved record in workbench_user.", result != null);
		assertThat("Expected id of new user distinct of 0", !result.equals(0));
	}

	@Test
	public void testUpdateUser() throws MiddlewareQueryException {
		final UserDto userDto = this.workbenchTestDataUtil.createTestUserDTO(0);
		final Integer userId = this.workbenchDataManager.createUser(userDto);
		userDto.setUserId(userId);
		userDto.setRole("BREEDER");
		final Integer result = workbenchDataManager.updateUser(userDto);

		assertThat("Expected id of userDto saved record in workbench_user.", result != null);
		assertThat("Expected the same id of userDto saved record ", result.equals(userId));
	}
}
