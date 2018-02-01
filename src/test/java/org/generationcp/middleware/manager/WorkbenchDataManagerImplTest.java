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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.WorkbenchTestDataUtil;
import org.generationcp.middleware.dao.ProjectUserInfoDAO;
import org.generationcp.middleware.dao.ToolDAO;
import org.generationcp.middleware.data.initializer.UserDtoTestDataInitializer;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.presets.StandardPreset;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.IbdbUserMap;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectActivity;
import org.generationcp.middleware.pojos.workbench.ProjectUserRole;
import org.generationcp.middleware.pojos.workbench.Role;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.ToolConfiguration;
import org.generationcp.middleware.pojos.workbench.ToolType;
import org.generationcp.middleware.pojos.workbench.UserRole;
import org.generationcp.middleware.pojos.workbench.WorkbenchDataset;
import org.generationcp.middleware.pojos.workbench.WorkbenchRuntimeData;
import org.generationcp.middleware.pojos.workbench.WorkflowTemplate;
import org.generationcp.middleware.service.api.program.ProgramFilters;
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
	public void beforeTest() {
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
	public void testAddUser() {
		final User user = this.workbenchTestDataUtil.createTestUserData();
		final Integer result = this.workbenchDataManager.addUser(user);
		Assert.assertNotNull("Expected id of a newly saved record in workbench_user.", result);

		final User readUser = this.workbenchDataManager.getUserById(result);
		Assert.assertEquals(user.getName(), readUser.getName());
	}

	@Test
	public void testAddPerson() {
		final Person person = this.workbenchTestDataUtil.createTestPersonData();
		final Integer result = this.workbenchDataManager.addPerson(person);
		Assert.assertNotNull("Expected id of a newly saved record in persons.", result);

		final Person readPerson = this.workbenchDataManager.getPersonById(result);
		Assert.assertEquals(person.getLastName(), readPerson.getLastName());
	}

	@Test
	public void testAddUsersWithRoles() {
		// Admin
		final Person adminPerson = new Person();
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
		final Integer adminPersonId = this.workbenchDataManager.addPerson(adminPerson);
		Assert.assertNotNull("Expected id of a newly saved record in persons.", adminPersonId);

		final User adminUser = new User();
		adminUser.setName("admin");
		adminUser.setPassword("b");
		adminUser.setPersonid(adminPersonId);
		adminUser.setInstalid(0);
		adminUser.setStatus(0);
		adminUser.setAccess(0);
		adminUser.setType(0);
		adminUser.setAssignDate(20140101);
		adminUser.setCloseDate(20140101);

		final List<UserRole> adminRoles = new ArrayList<UserRole>();
		adminRoles.add(new UserRole(adminUser, "ADMIN"));
		adminUser.setRoles(adminRoles);
		this.workbenchDataManager.addUser(adminUser);
		Assert.assertNotNull("Expected id of a newly saved record in users.", adminUser.getUserid());

		// Breeder
		final Person breederPerson = new Person();
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
		final Integer breederPersonId = this.workbenchDataManager.addPerson(breederPerson);
		Assert.assertNotNull("Expected id of a newly saved record in persons.", breederPersonId);

		final User breederUser = new User();
		breederUser.setName("breeder");
		breederUser.setPassword("b");
		breederUser.setPersonid(breederPersonId);
		breederUser.setInstalid(0);
		breederUser.setStatus(0);
		breederUser.setAccess(0);
		breederUser.setType(0);
		breederUser.setAssignDate(20140101);
		breederUser.setCloseDate(20140101);

		final List<UserRole> breederRoles = new ArrayList<UserRole>();
		breederRoles.add(new UserRole(breederUser, "BREEDER"));
		breederUser.setRoles(breederRoles);
		this.workbenchDataManager.addUser(breederUser);
		Assert.assertNotNull("Expected id of a newly saved record in users.", adminUser.getUserid());

		// Technician
		final Person technicianPerson = new Person();
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
		final Integer technicianPersonId = this.workbenchDataManager.addPerson(technicianPerson);
		Assert.assertNotNull("Expected id of a newly saved record in persons.", technicianPersonId);

		final User technicianUser = new User();
		technicianUser.setName("technician");
		technicianUser.setPassword("b");
		technicianUser.setPersonid(technicianPersonId);
		technicianUser.setInstalid(0);
		technicianUser.setStatus(0);
		technicianUser.setAccess(0);
		technicianUser.setType(0);
		technicianUser.setAssignDate(20140101);
		technicianUser.setCloseDate(20140101);

		final List<UserRole> technicianRoles = new ArrayList<UserRole>();
		technicianRoles.add(new UserRole(technicianUser, "TECHNICIAN"));
		technicianUser.setRoles(technicianRoles);
		this.workbenchDataManager.addUser(technicianUser);
		Assert.assertNotNull("Expected id of a newly saved record in users.", technicianUser.getUserid());
	}

	@Test
	public void testAddProject() {
		final Project project = this.workbenchTestDataUtil.createTestProjectData();
		this.workbenchDataManager.addProject(project);
		Assert.assertNotNull("Expected id of a newly saved record in workbench_project.", project.getProjectId());

		final Project readProject = this.workbenchDataManager.getProjectById(project.getProjectId());
		Assert.assertEquals(project, readProject);
	}

	@Test
	public void testProjectActivity() {
		final ProjectActivity projectActivity =
				this.workbenchTestDataUtil.createTestProjectActivityData(this.commonTestProject, this.testUser1);
		final Integer result = this.workbenchDataManager.addProjectActivity(projectActivity);
		Assert.assertNotNull("Expected id of a newly saved record in workbench_project_activity", result);

		final List<ProjectActivity> results =
				this.workbenchDataManager.getProjectActivitiesByProjectId(this.commonTestProject.getProjectId(), 0, 10);
		Assert.assertNotNull(results);
		Assert.assertEquals(3, results.size());

		final long count = this.workbenchDataManager.countProjectActivitiesByProjectId(this.commonTestProject.getProjectId());
		Assert.assertEquals(3, count);
	}

	@Test
	public void testIbdbUserMap() {
		final IbdbUserMap userMap = new IbdbUserMap();
		userMap.setProjectId(this.commonTestProject.getProjectId());
		userMap.setIbdbUserId(this.testUser1.getUserid() * -1);
		userMap.setWorkbenchUserId(this.testUser1.getUserid());

		final Integer result = this.workbenchDataManager.addIbdbUserMap(userMap);
		Assert.assertNotNull("Expected id of a newly saved record in workbench_ibdb_user_map", result);

		final Integer cropDBUserId =
				this.workbenchDataManager.getLocalIbdbUserId(this.testUser1.getUserid(), this.commonTestProject.getProjectId());
		Assert.assertNotNull(cropDBUserId);

		// Try to add a duplicate IbdbUserMap (same workbench project and user id).
		final IbdbUserMap duplicateUserMap = new IbdbUserMap();
		duplicateUserMap.setProjectId(userMap.getProjectId());
		duplicateUserMap.setIbdbUserId(userMap.getIbdbUserId());
		duplicateUserMap.setWorkbenchUserId(userMap.getWorkbenchUserId());
		final Integer result2 = this.workbenchDataManager.addIbdbUserMap(duplicateUserMap);
		Assert.assertEquals("Expected existing record's id being returned when trying to add duplicate entry in workbench_ibdb_user_map",
				result, result2);
		final Integer cropDBUserId2 =
				this.workbenchDataManager.getLocalIbdbUserId(this.testUser1.getUserid(), this.commonTestProject.getProjectId());
		Assert.assertEquals(
				"Expected workbench user to map to same crop db record after attempting to add duplicate entry in workbench_ibdb_user_map",
				cropDBUserId, cropDBUserId2);

	}

	@Test
	public void testGetProjects() {
		final List<Project> projects = this.workbenchDataManager.getProjects();
		Assert.assertNotNull(projects);
		Assert.assertTrue(!projects.isEmpty());
	}

	@Test
	public void testGetToolWithName() {
		final String toolName = "fieldbook_web";
		final Tool tool = this.workbenchDataManager.getToolWithName(toolName);
		Assert.assertNotNull(tool);
	}

	@Test
	public void testGetProjectByName() {
		final Project project = this.workbenchDataManager.getProjectByNameAndCrop(this.commonTestProject.getProjectName(),
				this.commonTestProject.getCropType());
		Assert.assertEquals(this.commonTestProject.getProjectName(), project.getProjectName());
	}

	@Test
	public void testGetProjectByUUID() {
		final Project project = this.workbenchDataManager.getProjectByUuidAndCrop(this.commonTestProject.getUniqueID(),
				this.commonTestProject.getCropType().getCropName());

		Assert.assertEquals(this.commonTestProject.getUniqueID(), project.getUniqueID());
		Assert.assertEquals(this.commonTestProject.getCropType(), project.getCropType());
	}

	@Test
	public void testGetProjectByUUIDProjectDoesNotExistInTheSpecifiedCrop() {
		final Project project = this.workbenchDataManager.getProjectByUuidAndCrop(this.commonTestProject.getUniqueID(),
				"wheat");
		Assert.assertNull("Expecting a null project because the project's unique id is associated to maize crop.", project);
	}

	@Test
	public void testGetUserByName() {
		final User user = this.workbenchDataManager.getUserByName(this.testUser1.getName(), 0, 1, Operation.EQUAL).get(0);
		Assert.assertEquals(this.testUser1.getName(), user.getName());
		Assert.assertEquals(this.testUser1.getUserid(), user.getUserid());
	}

	@Test
	public void testWorkbenchDataset() {
		final WorkbenchDataset dataset1 = this.createTestWorkbenchDataset(this.commonTestProject);
		final Integer result = this.workbenchDataManager.addWorkbenchDataset(dataset1);
		Assert.assertNotNull("Expected id of the newly added record in workbench_dataset", result);

		final List<WorkbenchDataset> list =
				this.workbenchDataManager.getWorkbenchDatasetByProjectId(this.commonTestProject.getProjectId(), 0, 10);
		Assert.assertTrue(list.contains(dataset1));

		final WorkbenchDataset dataset2 = this.createTestWorkbenchDataset(this.commonTestProject);
		this.workbenchDataManager.addWorkbenchDataset(dataset2);

		final long count = this.workbenchDataManager.countWorkbenchDatasetByProjectId(this.commonTestProject.getProjectId());
		Assert.assertEquals(2, count);
	}

	private WorkbenchDataset createTestWorkbenchDataset(final Project project) {
		final WorkbenchDataset dataset = new WorkbenchDataset();
		dataset.setName("Test Dataset" + new Random().nextInt());
		dataset.setDescription("Test Dataset Description");
		dataset.setCreationDate(new Date(System.currentTimeMillis()));
		dataset.setProject(project);
		return dataset;
	}

	@Test
	public void testProjectUserRoles() {

		final Role role1 = this.workbenchDataManager.getAllRoles().get(0);
		final Role role2 = this.workbenchDataManager.getAllRoles().get(1);

		final ProjectUserRole projUsrRole1 = new ProjectUserRole(this.commonTestProject, this.testUser1, role1);
		final ProjectUserRole projUsrRole2 = new ProjectUserRole(this.commonTestProject, this.testUser1, role2);

		final List<ProjectUserRole> projectUserRoles = new ArrayList<>();
		projectUserRoles.add(projUsrRole1);
		projectUserRoles.add(projUsrRole2);

		final List<Integer> rolesAdded = this.workbenchDataManager.addProjectUserRole(projectUserRoles);
		Assert.assertEquals(2, rolesAdded.size());

		final long result = this.workbenchDataManager.countUsersByProjectId(this.commonTestProject.getProjectId());
		Assert.assertEquals(1, result);

		final List<Project> results = this.workbenchDataManager.getProjectsByUser(this.testUser1);
		Assert.assertNotNull(results);
		Assert.assertTrue(!results.isEmpty());

		final List<ProjectUserRole> results2 = this.workbenchDataManager.getProjectUserRolesByProject(this.commonTestProject);
		Assert.assertNotNull(results2);
		Assert.assertTrue(!results2.isEmpty());

		final List<Role> roles = this.workbenchDataManager.getRolesByProjectAndUser(this.commonTestProject, this.testUser1);
		Assert.assertTrue(!roles.isEmpty());
		Assert.assertEquals(2, roles.size());

		final List<User> users = this.workbenchDataManager.getUsersByProjectId(this.commonTestProject.getProjectId());
		Assert.assertNotNull(users);
		Assert.assertEquals(1, users.size());
		Assert.assertEquals(this.testUser1, users.get(0));
	}

	@Test
	public void testAddToolConfiguration() {
		final Long toolId = 1L;
		final ToolConfiguration toolConfig = new ToolConfiguration();
		final Tool tool = new Tool();
		tool.setToolId(toolId);

		toolConfig.setTool(tool);
		toolConfig.setConfigKey("6th key");
		toolConfig.setConfigValue("test value");

		this.workbenchDataManager.addToolConfiguration(toolConfig);
		Debug.println(IntegrationTestBase.INDENT, "testAddToolConfiguration(toolId=" + toolId + "): " + toolConfig);
	}

	@Test
	public void testGetListOfToolConfigurationsByToolId() {
		final Long toolId = 1L;
		final List<ToolConfiguration> result = this.workbenchDataManager.getListOfToolConfigurationsByToolId(toolId);
		Debug.println(IntegrationTestBase.INDENT, "testGetListOfToolConfigurationsByToolId(" + toolId + "): ");

		if (result.isEmpty()) {
			Debug.println(IntegrationTestBase.INDENT, "  No records found.");
		} else {
			for (final ToolConfiguration t : result) {
				Debug.println(IntegrationTestBase.INDENT, t);
			}
		}
	}

	@Test
	public void testGetToolConfigurationByToolIdAndConfigKey() {
		final Long toolId = 1L;
		final String configKey = "test";
		final ToolConfiguration toolConfig = this.workbenchDataManager.getToolConfigurationByToolIdAndConfigKey(toolId, configKey);
		Debug.println(IntegrationTestBase.INDENT,
				"testGetToolConfigurationByToolIdAndConfigKey(toolId=" + toolId + ", configKey=" + configKey + "): " + toolConfig);
	}

	@Test
	public void testCropType() {
		final String cropName = "Coconut";
		final CropType cropType = new CropType(cropName);
		final String added = this.workbenchDataManager.addCropType(cropType);
		Assert.assertNotNull(added);

		final List<CropType> cropTypes = this.workbenchDataManager.getInstalledCropDatabses();
		Assert.assertNotNull(cropTypes);
		Assert.assertTrue(cropTypes.size() >= 1);

		final CropType cropTypeRead = this.workbenchDataManager.getCropTypeByName(cropName);
		Assert.assertNotNull(cropTypeRead);
		Assert.assertEquals(cropType, cropTypeRead);
	}

	@Test
	public void testGetRoleById() {
		final Integer id = 1; // Assumption: there is a role with id 1
		final Role role = this.workbenchDataManager.getRoleById(id);
		Assert.assertNotNull(role);
		Debug.println(IntegrationTestBase.INDENT, "testGetRoleById(id=" + id + "): \n  " + role);
	}

	@Test
	public void testGetRoleByNameAndWorkflowTemplate() {
		final String templateName = "MARS";
		final String roleName = "MARS Breeder";
		final WorkflowTemplate workflowTemplate = this.workbenchDataManager.getWorkflowTemplateByName(templateName).get(0);
		final Role role = this.workbenchDataManager.getRoleByNameAndWorkflowTemplate(roleName, workflowTemplate);
		Assert.assertNotNull(role);
		Debug.println(IntegrationTestBase.INDENT, "testGetRoleByNameAndWorkflowTemplate(name=" + roleName + ", workflowTemplate="
				+ workflowTemplate.getName() + "): \n  " + role);
	}

	@Test
	public void testGetRolesByWorkflowTemplate() {
		final WorkflowTemplate workflowTemplate = this.workbenchDataManager.getWorkflowTemplates().get(0); // get the
		// first
		// template
		// in
		// the db
		final List<Role> roles = this.workbenchDataManager.getRolesByWorkflowTemplate(workflowTemplate);
		Assert.assertNotNull(roles);
		Assert.assertTrue(!roles.isEmpty());
		Debug.println(IntegrationTestBase.INDENT,
				"testGetRolesByWorkflowTemplate(workflowTemplate=" + workflowTemplate.getName() + "): " + roles.size());
		for (final Role role : roles) {
			Debug.println(IntegrationTestBase.INDENT, "  " + role);
		}
	}

	@Test
	public void testGetWorkflowTemplateByRole() {
		final Role role = this.workbenchDataManager.getRoleById(this.workbenchDataManager.getAllRoles().get(0).getRoleId());
		final WorkflowTemplate template = this.workbenchDataManager.getWorkflowTemplateByRole(role);
		Assert.assertNotNull(template);
		Debug.println(IntegrationTestBase.INDENT, "testGetWorkflowTemplateByRole(role=" + role.getName() + "): \n  " + template);
	}

	@Test
	public void testGetAllRoles() {
		final List<Role> roles = this.workbenchDataManager.getAllRoles();
		Assert.assertNotNull(roles);
		Assert.assertTrue(!roles.isEmpty());
	}
	
	@Test
	public void testCountAllPersons() {
		final long count = this.workbenchDataManager.countAllPersons();
		Assert.assertTrue(count > 0);
	}

	@Test
	public void testCountAllUsers() {
		final long count = this.workbenchDataManager.countAllUsers();
		Assert.assertTrue(count > 0);
	}

	@Test
	public void testGetAllPersons() {
		final List<Person> results = this.workbenchDataManager.getAllPersons();
		Assert.assertNotNull(results);
		Assert.assertTrue(!results.isEmpty());
	}

	@Test
	public void testGetAllRolesDesc() {
		final List<Role> results = this.workbenchDataManager.getAllRolesDesc();
		Assert.assertNotNull(results);
		Assert.assertTrue(!results.isEmpty());
	}

	@Test
	public void testGetAllRolesOrderedByLabel() {
		final List<Role> results = this.workbenchDataManager.getAllRolesOrderedByLabel();
		Assert.assertNotNull(results);
		Assert.assertTrue(!results.isEmpty());
	}

	@Test
	public void testGetAllTools() {
		final List<Tool> results = this.workbenchDataManager.getAllTools();
		Assert.assertNotNull(results);
		Assert.assertTrue(!results.isEmpty());
	}

	@Test
	public void testGetAllUsers() {
		final List<User> results = this.workbenchDataManager.getAllUsers();
		Assert.assertNotNull(results);
		Assert.assertTrue(!results.isEmpty());
	}

	@Test
	public void testGetLastOpenedProject() {
		final Project results = this.workbenchDataManager.getLastOpenedProject(this.testUser1.getUserid());
		Assert.assertNotNull(results);
	}

	@Test
	public void testGetProjectsList() {
		final List<Project> results = this.workbenchDataManager.getProjects(0, 100);
		Assert.assertNotNull(results);
		Assert.assertTrue(!results.isEmpty());
	}

	@Test
	public void testGetProjectsByCrop() {
		// Add another maize project and verify projects retrieved for maize crop
		final CropType maizeCropType = new CropType(CropType.CropEnum.MAIZE.toString());
		final int NUM_NEW_MAIZE_PROJECTS = 2;
		final List<Project> maizeProjectsBeforeChange = this.workbenchDataManager.getProjectsByCrop(maizeCropType);
		this.createTestProjectsForCrop(maizeCropType, NUM_NEW_MAIZE_PROJECTS);
		this.verifyProjectsRetrievedPerCrop(maizeCropType, maizeProjectsBeforeChange, NUM_NEW_MAIZE_PROJECTS);

		// for all other installed crops, except for maize, create projects and retrieve projects for that crop
		final int NUM_NEW_PROJECTS = 3;
		final List<CropType> installedCrops = this.workbenchDataManager.getInstalledCropDatabses();
		for (CropType crop : installedCrops) {
			if (!crop.equals(maizeCropType)) {
				final List<Project> projectsBeforeChange = this.workbenchDataManager.getProjectsByCrop(crop);
				this.createTestProjectsForCrop(crop, NUM_NEW_PROJECTS);
				this.verifyProjectsRetrievedPerCrop(crop, projectsBeforeChange, NUM_NEW_PROJECTS);
			}
		}
		
	}

	// Verify projects were retrieved properly for specified crop
	private void verifyProjectsRetrievedPerCrop(final CropType cropType, final List<Project> projectsBeforeChange,
			final int noOfNewProjects) {
		final List<Project> projects = this.workbenchDataManager.getProjectsByCrop(cropType);
		Assert.assertEquals("Number of " + cropType.getCropName() + " projects should have been incremented by " + noOfNewProjects,
				projectsBeforeChange.size() + noOfNewProjects, projects.size());
		for (final Project project : projects) {
			Assert.assertEquals(cropType, project.getCropType());
		}
	}

	private void createTestProjectsForCrop(final CropType cropType, final int noOfProjects) {
		this.workbenchTestDataUtil.setCropType(cropType);
		for (int i = 0; i < noOfProjects; i++) {
			final Project project = this.workbenchTestDataUtil.createTestProjectData();
			this.workbenchDataManager.addProject(project);
		}
	}

	@Test
	public void testGetUserById() {
		final User user = this.workbenchDataManager.getUserById(this.testUser1.getUserid());
		Assert.assertNotNull(user);
	}

	@Test
	public void testGetWorkbenchDatasetById() {
		final WorkbenchDataset testDataset = this.createTestWorkbenchDataset(this.commonTestProject);
		final Integer result = this.workbenchDataManager.addWorkbenchDataset(testDataset);

		final WorkbenchDataset readDataset = this.workbenchDataManager.getWorkbenchDatasetById(new Long(result));
		Assert.assertNotNull(readDataset);
		Assert.assertEquals(testDataset, readDataset);
	}

	@Test
	public void testGetWorkbenchRuntimeData() {
		final WorkbenchRuntimeData result = this.workbenchDataManager.getWorkbenchRuntimeData();
		Assert.assertNotNull(result);
	}

	@Test
	public void testGetWorkflowTemplateByName() {
		final String name = "Manager";
		final List<WorkflowTemplate> results = this.workbenchDataManager.getWorkflowTemplateByName(name);
		Assert.assertNotNull(results);
		Assert.assertTrue(!results.isEmpty());
	}

	@Test
	public void testGetWorkflowTemplatesList() {
		final List<WorkflowTemplate> results = this.workbenchDataManager.getWorkflowTemplates();
		Assert.assertNotNull(results);
		Assert.assertTrue(!results.isEmpty());
	}

	@Test
	public void testDeletePerson() {
		final Person person = this.workbenchTestDataUtil.createTestPersonData();
		this.workbenchDataManager.addPerson(person);
		this.workbenchDataManager.deletePerson(person);
	}

	@Test
	public void testGetWorkflowTemplates() {
		final List<WorkflowTemplate> results = this.workbenchDataManager.getWorkflowTemplates(0, 100);
		Assert.assertNotNull(results);
		Assert.assertTrue(!results.isEmpty());
	}

	@Test
	public void testGetProjectUserInfoDao() {
		final ProjectUserInfoDAO results = this.workbenchDataManager.getProjectUserInfoDao();
		Assert.assertNotNull(results);
	}

	@Test
	public void testGetToolDao() {
		final ToolDAO results = this.workbenchDataManager.getToolDao();
		Assert.assertNotNull(results);
		Debug.println(IntegrationTestBase.INDENT, results.toString());
	}

	@Test
	public void testGetToolsWithType() {
		final List<Tool> results = this.workbenchDataManager.getToolsWithType(ToolType.NATIVE);
		Assert.assertNotNull(results);
		Assert.assertTrue(!results.isEmpty());
		Debug.printObjects(IntegrationTestBase.INDENT, results);
	}

	@Test
	public void testStandardPreset() throws Exception {
		final StandardPreset preset = new StandardPreset();
		preset.setConfiguration("<configuration/>");
		preset.setName("configuration_01");
		preset.setToolId(1);
		preset.setCropName("crop_name");

		final StandardPreset results = this.workbenchDataManager.saveOrUpdateStandardPreset(preset);
		Assert.assertTrue("we retrieve the saved primary id", results.getStandardPresetId() > 0);

		final Integer id = results.getStandardPresetId();
		final StandardPreset retrievedResult = this.workbenchDataManager.getStandardPresetDAO().getById(id);
		Assert.assertEquals("we retrieved the correct object from database", results, retrievedResult);

		final List<StandardPreset> out = this.workbenchDataManager.getStandardPresetDAO().getAll();
		Assert.assertTrue(!out.isEmpty());
	}

	@Test
	public void testGetAllStandardPreset() throws Exception {
		final List<StandardPreset> out = this.workbenchDataManager.getStandardPresetDAO().getAll();
		Assert.assertTrue(out.size() > 0);
	}

	@Test
	public void testGetStandardPresetFromCropAndTool() throws Exception {
		this.initializeStandardPresets();

		for (int j = 1; j < 3; j++) {
			final List<StandardPreset> presetsList = this.workbenchDataManager.getStandardPresetFromCropAndTool("crop_name_" + j, j);
			for (final StandardPreset p : presetsList) {
				Assert.assertEquals("should only retrieve all standard presets with crop_name_1", "crop_name_" + j, p.getCropName());
				Assert.assertEquals("should be the same tool as requested", Integer.valueOf(j), p.getToolId());
			}
		}

	}

	@Test
	public void testGetStandardPresetFromCropAndToolAndToolSection() throws Exception {
		this.initializeStandardPresets();

		for (int j = 1; j < 3; j++) {
			final List<StandardPreset> presetsList =
					this.workbenchDataManager.getStandardPresetFromCropAndTool("crop_name_" + j, j, "tool_section_" + j);
			for (final StandardPreset p : presetsList) {
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
		final List<StandardPreset> result =
				this.workbenchDataManager.getStandardPresetFromCropAndToolByName("configuration_1_1", "crop_name_1", 1, "tool_section_1");

		Assert.assertTrue("result should not be empty", result.size() > 0);
		Assert.assertEquals("Should return the same name", "configuration_1_1", result.get(0).getName());
	}

	protected List<StandardPreset> initializeStandardPresets() {
		final List<StandardPreset> fulllist = new ArrayList<>();
		for (int j = 1; j < 3; j++) {
			for (int i = 1; i < 6; i++) {
				final StandardPreset preset = new StandardPreset();
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
	public void testGetAllUserDtosSorted() {
		final UserDto user = this.workbenchTestDataUtil.createTestUserDTO(25);
		final List<UserDto> users = Lists.newArrayList(user);
		final WorkbenchDataManager workbenchDataManager = Mockito.mock(WorkbenchDataManager.class);

		Mockito.when(workbenchDataManager.getAllUsersSortedByLastName()).thenReturn(users);
		assertThat("Expected list users not null.", users != null);
		assertThat("Expected list users not empty.", !users.isEmpty());
		assertThat("Expected list users size 1.", users.size() == 1);

	}

	@Test
	public void testCreateUser() {
		final UserDto userDto = this.workbenchTestDataUtil.createTestUserDTO(0);
		final Integer result = this.workbenchDataManager.createUser(userDto);

		assertThat("Expected id of a newly saved record in workbench_user.", result != null);
		assertThat("Expected id of new user distinct of 0", !result.equals(0));
	}
	
	@Test
	public void testGetActiveUserIDsByProjectId() {
		final List<Integer> prevListOfUserIDs = this.workbenchDataManager.getActiveUserIDsByProjectId(this.commonTestProject.getProjectId());
		
		//Set up data
		UserDto userDto = UserDtoTestDataInitializer.createUserDto("USer", "User", "User@leafnode.io", "userPassword", "Breeder", "username");
		final int id = this.workbenchDataManager.createUser(userDto);
		final User user = this.workbenchDataManager.getUserById(id);
		final Role role = this.workbenchDataManager.getAllRoles().get(0);
		this.workbenchDataManager.addProjectUserRole(new ProjectUserRole(this.commonTestProject, user, role));
		
		
		List<Integer> userIDs = this.workbenchDataManager.getActiveUserIDsByProjectId(this.commonTestProject.getProjectId());
		Assert.assertTrue("The newly added member should be added in the retrieved list.", prevListOfUserIDs.size() + 1 == userIDs.size());
	}

	@Test
	public void testUpdateUser() {
		final UserDto userDto = this.workbenchTestDataUtil.createTestUserDTO(0);
		final Integer userId = this.workbenchDataManager.createUser(userDto);
		userDto.setUserId(userId);
		userDto.setRole("BREEDER");
		final Integer result = this.workbenchDataManager.updateUser(userDto);

		assertThat("Expected id of userDto saved record in workbench_user.", result != null);
		assertThat("Expected the same id of userDto saved record ", result.equals(userId));
	}

	@Test
	public void testCountProjectsByFilter() {
		final List<Project> projects = this.workbenchDataManager.getProjects();
		final Map<ProgramFilters, Object> filters = new HashMap<>();
		if(!projects.isEmpty()){
			final  Project project = projects.get(0);
			filters.put(ProgramFilters.CROP_TYPE, project.getCropType());
			filters.put(ProgramFilters.PROGRAM_NAME, project.getProjectName());
			final  long count = this.workbenchDataManager.countProjectsByFilter(filters);
			assertThat(new Long(1),is(equalTo(count)));

		}
	}

	@Test
	public void testGetProjectsbyFilters() {
		final List<Project> projects = this.workbenchDataManager.getProjects();
		final Map<ProgramFilters, Object> filters = new HashMap<>();
		if(!projects.isEmpty()){
			Project project = projects.get(0);
			filters.put(ProgramFilters.CROP_TYPE, project.getCropType());
			filters.put(ProgramFilters.PROGRAM_NAME, project.getProjectName());
			List<Project> Projects = this.workbenchDataManager.getProjects(1, 100, filters);
			assertThat(project.getProjectId(),is(equalTo(Projects.get(0).getProjectId())));
			assertThat(project.getCropType().getCropName(),is(equalTo(Projects.get(0).getCropType().getCropName())));
			assertThat(project.getProjectName(),is(equalTo(Projects.get(0).getProjectName())));

		}
	}
	@Test
	public void testGetAllActiveUsers() {
		final List<User> prevListOfActiveUsers = this.workbenchDataManager.getAllActiveUsersSorted();
		UserDto userDto = UserDtoTestDataInitializer.createUserDto("FirstName", "LastName", "email@leafnode.io", "password", "Breeder", "username");
		final int id = this.workbenchDataManager.createUser(userDto);
		userDto.setUserId(id);
		List<User> listOfActiveUsers = this.workbenchDataManager.getAllActiveUsersSorted();
		Assert.assertTrue("The newly added user should be added in the retrieved list.", prevListOfActiveUsers.size()+1 == listOfActiveUsers.size());
		
		//Deactivate the user to check if it's not retrieved
		userDto.setStatus(1);
		this.workbenchDataManager.updateUser(userDto);
		listOfActiveUsers = this.workbenchDataManager.getAllActiveUsersSorted();
		Assert.assertTrue("The newly added user should be added in the retrieved list.", prevListOfActiveUsers.size() == listOfActiveUsers.size());
		
	}


	@Test
	public void testUpdateUserWithPerson() {

		final UserDto userDto = this.workbenchTestDataUtil.createTestUserDTO(0);

		User userToBeUpdated = workbenchDataManager.getUserById(this.workbenchDataManager.createUser(userDto));

		final String password = "password1111";
		final String firstName = "John";
		final String lastName = "Doe";
		final String email = "John.Doe@email.com";


		userToBeUpdated.setPassword(password);
		userToBeUpdated.getPerson().setFirstName(firstName);
		userToBeUpdated.getPerson().setLastName(lastName);
		userToBeUpdated.getPerson().setEmail(email);

		this.workbenchDataManager.updateUser(userToBeUpdated);

		User updatedUser = workbenchDataManager.getUserById(userToBeUpdated.getUserid());

		Assert.assertEquals(password, updatedUser.getPassword());
		Assert.assertEquals(firstName, updatedUser.getPerson().getFirstName());
		Assert.assertEquals(lastName, updatedUser.getPerson().getLastName());
		Assert.assertEquals(email, updatedUser.getPerson().getEmail());
	}
}
