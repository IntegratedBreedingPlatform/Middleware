package org.generationcp.middleware.dao;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.WorkbenchTestDataUtil;
import org.generationcp.middleware.api.program.ProgramService;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Role;
import org.generationcp.middleware.pojos.workbench.RoleType;
import org.generationcp.middleware.pojos.workbench.UserRole;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.program.ProgramSearchRequest;
import org.generationcp.middleware.service.api.user.UserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class ProjectDAOTest extends IntegrationTestBase {

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Autowired
	private WorkbenchTestDataUtil workbenchTestDataUtil;

	@Autowired
	private UserService userService;

	@Autowired
	private ProgramService programService;

	private ProjectDAO workbenchProjectDao;
	private UserRoleDao userRoleDao;
	private RoleDAO roleDAO;
	private RoleTypeDAO roleTypeDAO;

	private CropType cropType;
	private Project project1;
	private Project project2;
	private WorkbenchUser adminInstanceProgram;
	private WorkbenchUser admin;
	private WorkbenchUser programAdmin;
	private WorkbenchUser cropAdmin;
	private Role programAdminRole;
	private Role instanceAdminRole;
	private Role cropAdminRole;

	@Before
	public void setup() {

		this.workbenchTestDataUtil.setUpWorkbench();

		if (this.roleTypeDAO == null) {
			this.roleTypeDAO = new RoleTypeDAO();
			this.roleTypeDAO.setSession(this.workbenchSessionProvider.getSession());
		}

		if (this.userRoleDao == null) {
			this.userRoleDao = new UserRoleDao();
			this.userRoleDao.setSession(this.workbenchSessionProvider.getSession());
		}

		if (this.roleDAO == null) {
			this.roleDAO = new RoleDAO();
			this.roleDAO.setSession(this.workbenchSessionProvider.getSession());
		}

		final RoleType programAdminRoleType =
			roleTypeDAO.getById(org.generationcp.middleware.domain.workbench.RoleType.PROGRAM.getId());
		this.programAdminRole = new Role();
		this.programAdminRole.setName("Test Program Role " + new Random().nextInt());
		this.programAdminRole.setRoleType(programAdminRoleType);
		this.programAdminRole.setActive(true);
		roleDAO.saveOrUpdate(this.programAdminRole);

		final org.generationcp.middleware.pojos.workbench.RoleType instanceRoleType =
			roleTypeDAO.getById(org.generationcp.middleware.domain.workbench.RoleType.INSTANCE.getId());
		this.instanceAdminRole = new Role();
		this.instanceAdminRole.setName("Test Instance Role " + new Random().nextInt());
		this.instanceAdminRole.setRoleType(instanceRoleType);
		this.instanceAdminRole.setActive(true);
		roleDAO.saveOrUpdate(instanceAdminRole);

		final org.generationcp.middleware.pojos.workbench.RoleType cropRoleType =
			roleTypeDAO.getById(org.generationcp.middleware.domain.workbench.RoleType.CROP.getId());
		this.cropAdminRole = new Role();
		this.cropAdminRole.setName("Test Crop Role " + new Random().nextInt());
		this.cropAdminRole.setRoleType(cropRoleType);
		this.cropAdminRole.setActive(true);
		roleDAO.saveOrUpdate(cropAdminRole);

		if (this.workbenchProjectDao == null) {
			this.workbenchProjectDao = new ProjectDAO();
			this.workbenchProjectDao.setSession(this.workbenchSessionProvider.getSession());
		}

		if (this.cropType == null) {
			this.cropType = this.workbenchDataManager.getCropTypeByName(CropType.CropEnum.MAIZE.name());
		}

		if (this.project1 == null) {
			this.project1 = this.workbenchTestDataUtil.createTestProjectData();
			this.programService.addProgram(this.project1);
		}

		if (this.project2 == null) {
			this.project2 = this.workbenchTestDataUtil.createTestProjectData();
			this.programService.addProgram(this.project2);
		}

		if (this.adminInstanceProgram == null) {
			this.adminInstanceProgram = this.workbenchTestDataUtil.createTestUserData();
			this.adminInstanceProgram.setRoles(Collections.emptyList());
			this.userService.addUser(this.adminInstanceProgram);

			this.assignRole(this.adminInstanceProgram, Arrays.asList(this.instanceAdminRole, this.programAdminRole));
		}

		if (this.admin == null) {
			this.admin = this.workbenchTestDataUtil.createTestUserData();
			this.admin.setName("Admin " + RandomStringUtils.randomAlphanumeric(5));
			this.admin.setRoles(Collections.emptyList());
			this.userService.addUser(this.admin);

			this.assignRole(this.admin, Collections.singletonList(this.instanceAdminRole));
		}

		if (this.programAdmin == null) {
			this.programAdmin = this.workbenchTestDataUtil.createTestUserData();
			this.programAdmin.setName("ProgramAdmin " + RandomStringUtils.randomAlphanumeric(5));
			this.programAdmin.setRoles(Collections.emptyList());
			this.userService.addUser(this.programAdmin);
			this.assignRole(this.programAdmin, Collections.singletonList(this.programAdminRole));

		}

		if (this.cropAdmin == null) {
			this.cropAdmin = this.workbenchTestDataUtil.createTestUserData();
			this.cropAdmin.setName("CropAdmin " + RandomStringUtils.randomAlphanumeric(5));
			this.cropAdmin.setRoles(Collections.emptyList());
			this.userService.addUser(this.cropAdmin);

			this.assignRole(this.cropAdmin, Collections.singletonList(this.cropAdminRole));
		}

	}

	@Test
	public void testGetProgramsByUserIdAdminAndProgramUser() {

		final int count = this.workbenchDataManager.getProjects().size();

		final ProgramSearchRequest programSearchRequest = new ProgramSearchRequest();
		programSearchRequest.setLoggedInUserId(this.adminInstanceProgram.getUserid());
		final Pageable pageable = new PageRequest(0, count);
		final List<Project> projects = this.workbenchProjectDao.getProjectsByFilter(pageable, programSearchRequest);
		final Set<Project> projectSet = Sets.newHashSet(projects);

		Assert.assertEquals(count, projects.size());
		Assert.assertEquals("No Duplicates", projects.size(), projectSet.size());
	}

	@Test
	public void testGetProgramsByUserIdAdminUser() {

		final int count = this.workbenchDataManager.getProjects().size();

		final ProgramSearchRequest programSearchRequest = new ProgramSearchRequest();
		programSearchRequest.setLoggedInUserId(this.admin.getUserid());
		final Pageable pageable = new PageRequest(0, count);
		final List<Project> projects = this.workbenchProjectDao.getProjectsByFilter(pageable, programSearchRequest);
		final Set<Project> projectSet = Sets.newHashSet(projects);

		Assert.assertEquals(count, projects.size());
		Assert.assertEquals("No Duplicates", projects.size(), projectSet.size());
	}

	@Test
	public void testGetProgramsByUserIdProgramAdminUser() {
		final ProgramSearchRequest programSearchRequest = new ProgramSearchRequest();
		programSearchRequest.setLoggedInUserId(this.programAdmin.getUserid());
		final Pageable pageable = new PageRequest(0, 100);
		final List<Project> projects = this.workbenchProjectDao.getProjectsByFilter(pageable, programSearchRequest);
		final Set<Project> projectSet = Sets.newHashSet(projects);

		Assert.assertEquals(1, projects.size());
		Assert.assertEquals("No Duplicates", projects.size(), projectSet.size());
	}

	private void assignRole(final WorkbenchUser user, final List<Role> roles) {
		for (final Role role : roles) {
			final UserRole userRole = new UserRole();
			userRole.setUser(user);
			userRole.setRole(role);
			if (org.generationcp.middleware.domain.workbench.RoleType.CROP.name().equals(role.getRoleType().getName())) {
				userRole.setCropType(this.cropType);
			} else if (org.generationcp.middleware.domain.workbench.RoleType.PROGRAM.name().equals(role.getRoleType().getName())) {
				userRole.setCropType(this.cropType);
				userRole.setWorkbenchProject(this.project1);
			}
			this.userRoleDao.saveOrUpdate(userRole);
		}

	}
}
