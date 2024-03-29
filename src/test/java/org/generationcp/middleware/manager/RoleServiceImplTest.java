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
import org.generationcp.middleware.api.program.ProgramService;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectUserInfo;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.program.ProgramSearchRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class RoleServiceImplTest extends IntegrationTestBase {

	@Autowired
	private WorkbenchTestDataUtil workbenchTestDataUtil;

	@Autowired
	private ProgramService programService;

	private Project commonTestProject;
	private WorkbenchUser testUser1;
	private WorkbenchDaoFactory workbenchDaoFactory;

	@Before
	public void beforeTest() {
		this.workbenchDaoFactory = new WorkbenchDaoFactory(this.workbenchSessionProvider);

		this.workbenchTestDataUtil.setUpWorkbench(workbenchDaoFactory);

		if (this.commonTestProject == null) {
			this.commonTestProject = this.workbenchTestDataUtil.getCommonTestProject();
		}

		if (this.testUser1 == null) {
			this.testUser1 = this.workbenchTestDataUtil.getTestUser1();
		}
	}

	@Test
	public void testAddProject() {
		final Project project = this.workbenchTestDataUtil.createTestProjectData();
		this.programService.addProgram(project);
		Assert.assertNotNull("Expected id of a newly saved record in workbench_project.", project.getProjectId());

		final Project readProject = this.workbenchDaoFactory.getProjectDAO().getById(project.getProjectId());
		Assert.assertEquals(project, readProject);
	}

	@Test
	public void testGetProjects() {
		final List<Project> projects = this.workbenchDaoFactory.getProjectDAO().getAll();
		Assert.assertNotNull(projects);
		Assert.assertFalse(projects.isEmpty());
	}

	@Test
	public void testGetProjectsByFilters() {
		final ProgramSearchRequest programSearchRequest = new ProgramSearchRequest();
		final Project project = this.commonTestProject;

		this.programService.addProgram(project);

		programSearchRequest.setCommonCropName(project.getCropType().getCropName());
		programSearchRequest.setProgramName(project.getProjectName());
		programSearchRequest.setLoggedInUserId(project.getUserId());

		final Pageable pageable = new PageRequest(0, 100);
		final List<Project> projects = this.workbenchDaoFactory.getProjectDAO().getProjectsByFilter(pageable, programSearchRequest);

		assertThat(project.getProjectId(), is(equalTo(projects.get(0).getProjectId())));
		assertThat(project.getCropType().getCropName(), is(equalTo(projects.get(0).getCropType().getCropName())));
		assertThat(project.getProjectName(), is(equalTo(projects.get(0).getProjectName())));
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
