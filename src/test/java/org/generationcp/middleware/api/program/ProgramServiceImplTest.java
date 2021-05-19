package org.generationcp.middleware.api.program;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.WorkbenchTestDataUtil;
import org.generationcp.middleware.manager.WorkbenchDaoFactory;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.user.UserService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ProgramServiceImplTest extends IntegrationTestBase {

	private ProgramServiceImpl programService;

	private Project testProject1;

	private Project testProject2;

	private WorkbenchUser workbenchUserTest;

	@Autowired
	private WorkbenchTestDataUtil workbenchTestDataUtil;

	@Autowired
	private UserService userService;

	private WorkbenchDaoFactory daoFactory;

	@Before
	public void setUp() {
		this.programService = new ProgramServiceImpl(this.workbenchSessionProvider);
		this.daoFactory = new WorkbenchDaoFactory(this.workbenchSessionProvider);

		if (this.testProject1 == null) {
			this.testProject1 = this.buildProject("Project1 ");
		}
		if (this.testProject2 == null) {
			this.testProject2 = this.buildProject("Project2 ");
		}

		final WorkbenchUser user = this.workbenchTestDataUtil.createTestUserData();
		this.workbenchUserTest = this.userService.addUser(user);
		this.sessionProvder.getSession().flush();

	}

	@Test
	public void test_saveProjectUserInfo_Ok() {
		this.programService.saveOrUpdateProjectUserInfo(this.workbenchUserTest.getUserid(), this.testProject1.getUniqueID());
		final ProgramDTO programDTO = this.programService.getLastOpenedProject(this.workbenchUserTest.getUserid());
		this.sessionProvder.getSession().flush();
		assertEquals(programDTO.getUniqueID(), this.testProject1.getUniqueID());
	}

	@Test
	public void test_updateProjectUserInfo_Ok() {
		this.programService.saveOrUpdateProjectUserInfo(this.workbenchUserTest.getUserid(), this.testProject2.getUniqueID());
		this.sessionProvder.getSession().flush();
		final ProgramDTO programDTO1 = this.programService.getLastOpenedProject(this.workbenchUserTest.getUserid());
		assertEquals(programDTO1.getUniqueID(), this.testProject2.getUniqueID());

		this.programService.saveOrUpdateProjectUserInfo(this.workbenchUserTest.getUserid(), this.testProject2.getUniqueID());
		this.sessionProvder.getSession().flush();
		final ProgramDTO programDTO2 = this.programService.getLastOpenedProject(this.workbenchUserTest.getUserid());
		assertEquals(programDTO2.getUniqueID(), this.testProject2.getUniqueID());
	}

	private Project buildProject(final String projectName){
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
