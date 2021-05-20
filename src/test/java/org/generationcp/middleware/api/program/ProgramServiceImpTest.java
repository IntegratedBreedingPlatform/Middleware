package org.generationcp.middleware.api.program;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.dao.ProjectDAO;
import org.generationcp.middleware.dao.ProjectUserInfoDAO;
import org.generationcp.middleware.dao.WorkbenchUserDAO;
import org.generationcp.middleware.manager.WorkbenchDaoFactory;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectUserInfo;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.Random;
import java.util.UUID;

@RunWith(MockitoJUnitRunner.class)
public class ProgramServiceImpTest {

	private static final Integer USER_ID = new Random().nextInt();

	private Project testProject1;
	private Project testProject2;

	@InjectMocks
	private ProgramServiceImpl programService;

	@Mock
	private WorkbenchDaoFactory workbenchDaoFactory;

	@Mock
	private ProjectUserInfoDAO projectUserInfoDAO;

	@Mock
	private ProjectDAO projectDAO;

	@Mock
	private WorkbenchUserDAO userDAO;

	@Before
	public void init() {
		Mockito.when(this.workbenchDaoFactory.getProjectUserInfoDAO()).thenReturn(this.projectUserInfoDAO);
		Mockito.when(this.workbenchDaoFactory.getProjectDAO()).thenReturn(this.projectDAO);

		final WorkbenchUser person = Mockito.mock(WorkbenchUser.class);
		Mockito.when(person.getUserid()).thenReturn(USER_ID);
		Mockito.when(this.userDAO.getById(USER_ID)).thenReturn(person);
		Mockito.when(this.workbenchDaoFactory.getWorkbenchUserDAO()).thenReturn(this.userDAO);

		ReflectionTestUtils.setField(this.programService, "daoFactory", this.workbenchDaoFactory);

		this.testProject1 = buildProject("Project1 ");
		this.testProject2 = buildProject("Project2 ");

		Mockito.when(this.workbenchDaoFactory.getProjectDAO().getByUuid(this.testProject1.getUniqueID())).thenReturn(this.testProject1);
		Mockito.when(this.workbenchDaoFactory.getProjectDAO().getByUuid(this.testProject2.getUniqueID())).thenReturn(this.testProject2);

	}

	@Test
	public void test_updateProjectUserInfo_Ok() {
		final ProjectUserInfo projectUserInfo = new ProjectUserInfo();
		projectUserInfo.setProject(this.testProject2);
		projectUserInfo.setLastOpenDate(new Date());

		Mockito.when(this.workbenchDaoFactory.getProjectUserInfoDAO().getByProjectIdAndUserId(this.testProject2.getProjectId(), USER_ID))
			.thenReturn(projectUserInfo);

		this.programService.saveOrUpdateProjectUserInfo(USER_ID, this.testProject1.getUniqueID());
		this.programService.saveOrUpdateProjectUserInfo(USER_ID, this.testProject2.getUniqueID());
		Mockito.verify(this.projectUserInfoDAO, Mockito.times(1)).save(Mockito.any());
		Mockito.verify(this.projectUserInfoDAO, Mockito.times(1)).update(Mockito.any());
		Mockito.verify(this.projectDAO, Mockito.times(2)).update(Mockito.any());

	}

	private static final Project buildProject(final String projectName) {
		final Project project = new Project();
		project.setProjectId(new Random().nextLong());
		project.setProjectName(projectName + RandomStringUtils.randomAlphanumeric(10));
		project.setStartDate(new Date());
		project.setUniqueID(UUID.randomUUID().toString());
		project.setLastOpenDate(new Date());
		project.setCropType(new CropType("maize"));
		return project;
	}
}
