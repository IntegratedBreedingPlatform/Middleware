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

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.WorkbenchTestDataUtil;
import org.generationcp.middleware.service.api.program.ProgramSearchRequest;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.presets.StandardPreset;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectActivity;
import org.generationcp.middleware.pojos.workbench.ProjectUserInfo;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.ToolType;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.user.UserService;
import org.generationcp.middleware.utils.test.Debug;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class WorkbenchDataManagerImplTest extends IntegrationTestBase {

	private final static String CROP_NAME = "maize";

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Autowired
	private UserService userService;

	@Autowired
	private WorkbenchTestDataUtil workbenchTestDataUtil;

	private Project commonTestProject;
	private WorkbenchUser testUser1;
	private WorkbenchDaoFactory workbenchDaoFactory;

	@Before
	public void beforeTest() {

		this.workbenchTestDataUtil.setUpWorkbench();

		if (this.commonTestProject == null) {
			this.commonTestProject = this.workbenchTestDataUtil.getCommonTestProject();
		}

		if (this.testUser1 == null) {
			this.testUser1 = this.workbenchTestDataUtil.getTestUser1();
		}

		this.workbenchDaoFactory = new WorkbenchDaoFactory(this.workbenchSessionProvider);
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
	public void testGetProjects() {
		final List<Project> projects = this.workbenchDataManager.getProjects();
		Assert.assertNotNull(projects);
		Assert.assertFalse(projects.isEmpty());
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
	public void testGetAllTools() {
		final List<Tool> results = this.workbenchDataManager.getAllTools();
		Assert.assertNotNull(results);
		Assert.assertFalse(results.isEmpty());
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
		Assert.assertFalse(results.isEmpty());
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
		for (final CropType crop : installedCrops) {
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
	public void testGetToolsWithType() {
		final List<Tool> results = this.workbenchDataManager.getToolsWithType(ToolType.NATIVE);
		Assert.assertNotNull(results);
		Assert.assertFalse(results.isEmpty());
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
		final StandardPreset retrievedResult = this.workbenchDaoFactory.getStandardPresetDAO().getById(id);
		Assert.assertEquals("we retrieved the correct object from database", results, retrievedResult);

		final List<StandardPreset> out = this.workbenchDaoFactory.getStandardPresetDAO().getAll();
		Assert.assertFalse(out.isEmpty());
	}

	@Test
	public void testGetAllStandardPreset() throws Exception {
		final List<StandardPreset> out = this.workbenchDaoFactory.getStandardPresetDAO().getAll();
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
	public void testCountProjectsByFilter() {
		final List<Project> projects = this.workbenchDataManager.getProjects();
		final ProgramSearchRequest programSearchRequest = new ProgramSearchRequest();
		if (!projects.isEmpty()) {
			final Project project = projects.get(0);
			programSearchRequest.setProgramName(project.getProjectName());
			programSearchRequest.setCommonCropName(project.getCropType().getCropName());
			final long count = this.workbenchDataManager.countProjectsByFilter(programSearchRequest);
			assertThat(new Long(1), is(equalTo(count)));

		}
	}

	@Test
	public void testGetProjectsbyFilters() {
		final List<Project> projects = this.workbenchDataManager.getProjects();
		final ProgramSearchRequest programSearchRequest = new ProgramSearchRequest();
		if (!projects.isEmpty()) {
			final Project project = projects.get(0);
			programSearchRequest.setCommonCropName(project.getCropType().getCropName());
			programSearchRequest.setProgramName(project.getProjectName());

			final Pageable pageable = new PageRequest(1, 100);
			final List<Project> Projects = this.workbenchDataManager.getProjects(pageable, programSearchRequest);
			assertThat(project.getProjectId(), is(equalTo(Projects.get(0).getProjectId())));
			assertThat(project.getCropType().getCropName(), is(equalTo(Projects.get(0).getCropType().getCropName())));
			assertThat(project.getProjectName(), is(equalTo(Projects.get(0).getProjectName())));

		}
	}

	@Test
	public void testGetProjectUserInfoByProjectId() {
		final List<ProjectUserInfo> results = this.workbenchDaoFactory.getProjectUserInfoDAO()
			.getByProjectId(this.commonTestProject.getProjectId());

		Assert.assertNotNull(results);
		Assert.assertEquals(2, results.size());
		final ProjectUserInfo userInfo1 = results.get(0);
		Assert.assertEquals(userInfo1.getProject(), this.commonTestProject);
		Assert.assertEquals(userInfo1.getUser().getUserid(), this.testUser1.getUserid());
		final ProjectUserInfo userInfo2 = results.get(1);
		Assert.assertEquals(userInfo2.getProject(), this.commonTestProject);
		Assert.assertEquals(userInfo2.getUser().getUserid(), this.workbenchTestDataUtil.getTestUser2().getUserid());
	}

}
