package org.generationcp.middleware.api.program;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.WorkbenchTestDataUtil;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.WorkbenchDaoFactory;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectActivity;
import org.generationcp.middleware.pojos.workbench.ProjectUserInfo;
import org.generationcp.middleware.pojos.workbench.Role;
import org.generationcp.middleware.pojos.workbench.UserRole;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.user.RoleSearchDto;
import org.generationcp.middleware.util.Util;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ProgramServiceImplIntegrationTest extends IntegrationTestBase {

	private final static String CROP = "maize";

	private ProgramServiceImpl programService;

	private WorkbenchUser workbenchUserTest;

	private WorkbenchDaoFactory daoFactory;

	@Autowired
	private WorkbenchTestDataUtil workbenchTestDataUtil;

	@Before
	public void setUp() {
		this.programService = new ProgramServiceImpl(this.workbenchSessionProvider);
		this.daoFactory = new WorkbenchDaoFactory(this.workbenchSessionProvider);
	}

	@Test
	public void testSaveProjectUserInfo_Ok() {
		final Project testProject1 = this.buildProject("Project1 ");

		final Integer userId = this.findAdminUser();
		this.workbenchUserTest = this.daoFactory.getWorkbenchUserDAO().getById(userId);
		this.sessionProvder.getSession().flush();

		this.programService.saveOrUpdateProjectUserInfo(this.workbenchUserTest.getUserid(), testProject1.getUniqueID());
		final ProgramDTO programDTO = this.programService.getLastOpenedProject(this.workbenchUserTest.getUserid());
		this.sessionProvder.getSession().flush();
		assertEquals(programDTO.getUniqueID(), testProject1.getUniqueID());
	}

	@Test
	public void testAddProgram_Ok() {
		final String programName = RandomStringUtils.randomAlphabetic(20);
		final String date = "10-10-2020";
		final ProgramBasicDetailsDto programBasicDetailsDto = new ProgramBasicDetailsDto();
		programBasicDetailsDto.setName(programName);
		programBasicDetailsDto.setStartDate(date);
		final ProgramDTO programDTO = programService.addProgram(CROP, programBasicDetailsDto);
		assertEquals(programDTO.getName(), programName);
		assertEquals(programDTO.getStartDate(), Util.tryParseDate(date, Util.FRONTEND_DATE_FORMAT));
	}

	@Test
	public void testGetProgram_Ok() {
		final String programName = RandomStringUtils.randomAlphabetic(20);
		final String date = "10-10-2020";
		final ProgramBasicDetailsDto programBasicDetailsDto = new ProgramBasicDetailsDto();
		programBasicDetailsDto.setName(programName);
		programBasicDetailsDto.setStartDate(date);
		programService.addProgram(CROP, programBasicDetailsDto);

		final Optional<ProgramDTO> programDTOOptional = programService.getProgramByCropAndName(CROP, programName);
		assertTrue(programDTOOptional.isPresent());
		assertEquals(programDTOOptional.get().getStartDate(), Util.tryParseDate(date, Util.FRONTEND_DATE_FORMAT));
	}

	@Test
	public void testDeleteProgramAndDependencies_Ok() throws MiddlewareQueryException {
		final String programName = RandomStringUtils.randomAlphabetic(20);
		final Project project = this.buildProject(programName);
		//add dependencies
		final Integer userId = this.findAdminUser();
		final WorkbenchUser adminUser = this.daoFactory.getWorkbenchUserDAO().getById(userId);

		final ProjectActivity projectActivity =
			this.workbenchTestDataUtil.createTestProjectActivityData(project, adminUser);
		this.daoFactory.getProjectActivityDAO().save(projectActivity);

		final ProjectUserInfo projectUserInfo = new ProjectUserInfo(project, adminUser, new Date());
		this.daoFactory.getProjectUserInfoDAO().save(projectUserInfo);

		final RoleSearchDto roleSearchDto = new RoleSearchDto();
		roleSearchDto.setRoleTypeId(3);
		final List<Role> roles = this.daoFactory.getRoleDao().searchRoles(roleSearchDto, null);
		if (!roles.isEmpty()) {
			final UserRole userRole = new UserRole(adminUser, roles.get(0), new CropType(CROP), project);
			this.daoFactory.getUserRoleDao().save(userRole);
		}

		this.programService.deleteProgramAndDependencies(project.getUniqueID());

		final Project program = this.daoFactory.getProjectDAO().getById(project.getProjectId());
		final List<ProjectUserInfo> projectUserInfos = this.daoFactory.getProjectUserInfoDAO().getByProjectId(project.getProjectId());
		final List<UserRole> userRoles = this.daoFactory.getUserRoleDao().getByProgramId(project.getProjectId());
		final List<ProjectActivity> projectActivities =
			this.daoFactory.getProjectActivityDAO().getByProjectId(project.getProjectId(), 0, 1);

		assertNull(program);
		assertTrue(projectUserInfos.isEmpty());
		assertTrue(userRoles.isEmpty());
		assertTrue(projectActivities.isEmpty());
	}

	@Test
	public void testEditProgram_Ok() {
		ProgramBasicDetailsDto programBasicDetailsDto = new ProgramBasicDetailsDto();
		programBasicDetailsDto.setName(RandomStringUtils.randomAlphabetic(20));
		programBasicDetailsDto.setStartDate("10-10-2020");
		final ProgramDTO programDTO = programService.addProgram(CROP, programBasicDetailsDto);

		final String newName = RandomStringUtils.randomAlphabetic(20);
		final String newDate = "11-11-2020";

		programBasicDetailsDto = new ProgramBasicDetailsDto();
		programBasicDetailsDto.setName(newName);
		programBasicDetailsDto.setStartDate(newDate);

		this.programService.editProgram(programDTO.getUniqueID(), programBasicDetailsDto);

		final Project project = this.daoFactory.getProjectDAO().getByUuid(programDTO.getUniqueID());
		assertEquals(project.getProjectName(), newName);
		assertEquals(project.getStartDate(), Util.tryParseDate(newDate, Util.FRONTEND_DATE_FORMAT));
	}

	private Project buildProject(final String projectName) {
		final Project project = new Project();
		project.setProjectName(projectName + RandomStringUtils.randomAlphanumeric(10));
		project.setStartDate(new Date());
		project.setUniqueID(UUID.randomUUID().toString());
		project.setLastOpenDate(new Date());
		project.setCropType(new CropType(CROP));
		this.daoFactory.getProjectDAO().save(project);
		this.sessionProvder.getSession().flush();
		return project;
	}
}
