package org.generationcp.middleware.api.program;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.WorkbenchTestDataUtil;
import org.generationcp.middleware.manager.WorkbenchDaoFactory;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class ProgramServiceImplIntegrationTest extends IntegrationTestBase {

	private ProgramServiceImpl programService;

	private WorkbenchUser workbenchUserTest;

	@Autowired
	private WorkbenchTestDataUtil workbenchTestDataUtil;

	private WorkbenchDaoFactory daoFactory;

	@Before
	public void setUp() {
		this.programService = new ProgramServiceImpl(this.workbenchSessionProvider);
		this.daoFactory = new WorkbenchDaoFactory(this.workbenchSessionProvider);
	}

	@Test
	public void test_saveProjectUserInfo_Ok() {
		final Project testProject1 = this.buildProject("Project1 ");
		final Project testProject2 = this.buildProject("Project2 ");

		final Integer userId = this.findAdminUser();
		this.workbenchUserTest = this.daoFactory.getWorkbenchUserDAO().getById(userId);
		this.sessionProvder.getSession().flush();

		this.programService.saveOrUpdateProjectUserInfo(this.workbenchUserTest.getUserid(), testProject1.getUniqueID());
		final ProgramDTO programDTO = this.programService.getLastOpenedProject(this.workbenchUserTest.getUserid());
		this.sessionProvder.getSession().flush();
		assertEquals(programDTO.getUniqueID(), testProject1.getUniqueID());
	}

	private Project buildProject(final String projectName) {
		final Project project = new Project();
		project.setProjectName(projectName + RandomStringUtils.randomAlphanumeric(10));
		project.setStartDate(new Date());
		project.setUniqueID(UUID.randomUUID().toString());
		project.setLastOpenDate(new Date());
		project.setCropType(new CropType("maize"));
		this.daoFactory.getProjectDAO().save(project);
		this.sessionProvder.getSession().flush();
		return project;
	}
}
