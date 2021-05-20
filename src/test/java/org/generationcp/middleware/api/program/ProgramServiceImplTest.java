package org.generationcp.middleware.api.program;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.WorkbenchTestDataUtil;
import org.generationcp.middleware.manager.WorkbenchDaoFactory;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.util.Util;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.jayway.awaitility.Awaitility.await;
import static com.jayway.awaitility.Awaitility.with;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ProgramServiceImplTest extends IntegrationTestBase {

	private ProgramServiceImpl programService;

	private Project testProject1;

	private Project testProject2;

	private WorkbenchUser workbenchUserTest;

	@Autowired
	private WorkbenchTestDataUtil workbenchTestDataUtil;

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

		final Integer userId = this.findAdminUser();
		this.workbenchUserTest = this.daoFactory.getWorkbenchUserDAO().getById(userId);
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
	public void test_updateProjectUserInfo_Ok() throws ParseException, InterruptedException {
		this.programService.saveOrUpdateProjectUserInfo(this.workbenchUserTest.getUserid(), this.testProject2.getUniqueID());
		final ProgramDTO programDTO1 = this.programService.getLastOpenedProject(this.workbenchUserTest.getUserid());
		final Date lastOpenDateProgram1 = new SimpleDateFormat(Util.FRONTEND_TIMESTAMP_FORMAT).parse(programDTO1.getLastOpenDate());
		this.sessionProvder.getSession().flush();

		Thread.sleep(2000);

		this.programService.saveOrUpdateProjectUserInfo(this.workbenchUserTest.getUserid(), this.testProject2.getUniqueID());
		this.sessionProvder.getSession().flush();

		final ProgramDTO programDTO2 = this.programService.getLastOpenedProject(this.workbenchUserTest.getUserid());
		final Date lastOpenDateProgram2 = new SimpleDateFormat(Util.FRONTEND_TIMESTAMP_FORMAT).parse(programDTO2.getLastOpenDate());

		assertTrue(lastOpenDateProgram2.after(lastOpenDateProgram1));
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
