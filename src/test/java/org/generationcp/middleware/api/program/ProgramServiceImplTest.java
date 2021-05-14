package org.generationcp.middleware.api.program;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.manager.WorkbenchDaoFactory;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class ProgramServiceImplTest extends IntegrationTestBase {

	private ProgramServiceImpl programService;

	private Project testProject;

	private WorkbenchUser workbenchUser;

	@Before
	public void setUp() {
		this.programService = new ProgramServiceImpl(this.workbenchSessionProvider);
		final WorkbenchDaoFactory daoFactory = new WorkbenchDaoFactory(this.workbenchSessionProvider);

		if (this.testProject == null) {
			this.testProject = new Project();
			this.testProject.setProjectName("Project " + RandomStringUtils.randomAlphanumeric(10));
			this.testProject.setStartDate(new Date());
			this.testProject.setUniqueID(UUID.randomUUID().toString());
			this.testProject.setLastOpenDate(new Date());
			this.testProject.setCropType(new CropType("maize"));
			daoFactory.getProjectDAO().save(this.testProject);
		}

		final Integer userId = this.findAdminUser();
		this.workbenchUser = daoFactory.getWorkbenchUserDAO().getById(userId);
	}

	@Test
	public void saveOrUpdateProjectUserInfo() {
		this.programService.saveOrUpdateProjectUserInfo(this.workbenchUser.getUserid(), this.testProject.getUniqueID());
		final ProgramDTO programDTO = this.programService.getLastOpenedProject(this.workbenchUser.getUserid());
		assertEquals(programDTO.getUniqueID(), this.testProject.getUniqueID());
	}

}
