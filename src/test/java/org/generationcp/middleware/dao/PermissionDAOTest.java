package org.generationcp.middleware.dao;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.WorkbenchTestDataUtil;
import org.generationcp.middleware.api.program.ProgramService;
import org.generationcp.middleware.domain.workbench.PermissionDto;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Permission;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Role;
import org.generationcp.middleware.pojos.workbench.RoleType;
import org.generationcp.middleware.pojos.workbench.UserRole;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.user.UserService;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class PermissionDAOTest extends IntegrationTestBase {

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Autowired
	private WorkbenchTestDataUtil workbenchTestDataUtil;

	@Autowired
	private ProgramService programService;

	@Autowired
	private UserService userService;
	
	private PermissionDAO permissionDAO;
	private UserRoleDao userRoleDao;
	private RoleDAO roleDAO;
	private RoleTypeDAO roleTypeDAO;

	private Role programAdminRole;
	private Role cropAdminRole;
	private Role instanceAdminRole;
	private ProjectDAO workbenchProjectDao;
	private CropType cropType;
	private Project project1;
	private Project project2;
	private Permission cropPermission;
	private Permission programPermission;

	@Before
	public void setUp() throws Exception {
		this.workbenchTestDataUtil.setUpWorkbench();
		
		if (this.permissionDAO == null) {
			this.permissionDAO = new PermissionDAO();
			this.permissionDAO.setSession(this.workbenchSessionProvider.getSession());
		}

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

		final org.generationcp.middleware.pojos.workbench.RoleType cropRoleType =
			roleTypeDAO.getById(org.generationcp.middleware.domain.workbench.RoleType.CROP.getId());
		this.cropAdminRole = new Role();
		this.cropAdminRole.setName("Test Crop Role " + nextInt());
		this.cropAdminRole.setRoleType(cropRoleType);
		this.cropAdminRole.setActive(true);
		this.cropPermission = new Permission();
		this.cropPermission.setPermissionId(nextInt());
		this.cropPermission.setName(randomAlphabetic(10));
		this.cropPermission.setDescription(randomAlphabetic(10));
		this.permissionDAO.save(this.cropPermission);
		this.cropAdminRole.getPermissions().add(this.cropPermission);
		roleDAO.saveOrUpdate(this.cropAdminRole);

		final RoleType programAdminRoleType =
			roleTypeDAO.getById(org.generationcp.middleware.domain.workbench.RoleType.PROGRAM.getId());
		this.programAdminRole = new Role();
		this.programAdminRole.setName("Test Program Role " + nextInt());
		this.programAdminRole.setRoleType(programAdminRoleType);
		this.programAdminRole.setActive(true);
		this.programPermission = new Permission();
		this.programPermission.setPermissionId(nextInt());
		this.programPermission.setName(randomAlphabetic(10));
		this.programPermission.setDescription(randomAlphabetic(10));
		this.permissionDAO.save(this.programPermission);
		this.programAdminRole.getPermissions().add(this.programPermission);
		roleDAO.saveOrUpdate(this.programAdminRole);

		final org.generationcp.middleware.pojos.workbench.RoleType instanceRoleType =
			roleTypeDAO.getById(org.generationcp.middleware.domain.workbench.RoleType.INSTANCE.getId());
		this.instanceAdminRole = new Role();
		this.instanceAdminRole.setName("Test Instance Role " + nextInt());
		this.instanceAdminRole.setRoleType(instanceRoleType);
		this.instanceAdminRole.setActive(true);
		this.instanceAdminRole.getPermissions().add(this.cropPermission);
		this.instanceAdminRole.getPermissions().add(this.programPermission);
		roleDAO.saveOrUpdate(this.instanceAdminRole);

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
	}

	@Test
	public void testGetPermissions_UserWithInstanceRole() {

		final WorkbenchUser admin = this.workbenchTestDataUtil.createTestUserData();
		admin.setName("admin " + RandomStringUtils.randomAlphanumeric(5));
		admin.setRoles(Collections.emptyList());
		this.userService.addUser(admin);

		final UserRole adminUserRole = new UserRole();
		adminUserRole.setUser(admin);
		adminUserRole.setRole(this.instanceAdminRole);
		userRoleDao.saveOrUpdate(adminUserRole);

		this.workbenchSessionProvider.getSession().flush();

		final List<PermissionDto> permissions = this.permissionDAO
			.getPermissions(admin.getUserid(), null, null);

		assertThat("should have all permissions", permissions, hasItems(
			Matchers.hasProperty("id", is(this.cropPermission.getPermissionId())),
			Matchers.hasProperty("id", is(this.programPermission.getPermissionId()))
		));
	}

	@Test
	public void testGetPermissions_UserWithCropRoles() {

		final WorkbenchUser cropAdmin = this.workbenchTestDataUtil.createTestUserData();
		cropAdmin.setName("CropAdmin " + RandomStringUtils.randomAlphanumeric(5));
		cropAdmin.setRoles(Collections.emptyList());
		this.userService.addUser(cropAdmin);

		final UserRole cropUserRole = new UserRole();
		cropUserRole.setUser(cropAdmin);
		cropUserRole.setRole(this.cropAdminRole);
		cropUserRole.setCropType(this.cropType);
		userRoleDao.saveOrUpdate(cropUserRole);

		this.workbenchSessionProvider.getSession().flush();

		final List<PermissionDto> permissions = this.permissionDAO
			.getPermissions(cropAdmin.getUserid(), this.cropType.getCropName(), null);

		assertThat(permissions, hasSize(1));
		assertThat("should have crop permissions", permissions, hasItems(
			Matchers.hasProperty("id", is(this.cropPermission.getPermissionId()))
		));
	}

	@Test
	public void testGetPermissions_UserWithCropAndProgramRoles() {

		final WorkbenchUser programAdmin = this.workbenchTestDataUtil.createTestUserData();
		programAdmin.setName("ProgramAdmin " + RandomStringUtils.randomAlphanumeric(5));
		programAdmin.setRoles(Collections.emptyList());
		this.userService.addUser(programAdmin);

		final UserRole cropUserRole = new UserRole();
		cropUserRole.setUser(programAdmin);
		cropUserRole.setRole(this.cropAdminRole);
		cropUserRole.setCropType(this.cropType);
		userRoleDao.saveOrUpdate(cropUserRole);
		
		final UserRole programUserRole = new UserRole();
		programUserRole.setUser(programAdmin);
		programUserRole.setRole(this.programAdminRole);
		programUserRole.setCropType(this.cropType);
		programUserRole.setWorkbenchProject(this.project1);
		userRoleDao.saveOrUpdate(programUserRole);

		this.workbenchSessionProvider.getSession().flush();

		final List<PermissionDto> permissions = this.permissionDAO
			.getPermissions(programAdmin.getUserid(), this.cropType.getCropName(), this.project1.getProjectId().intValue());

		assertThat("should have both crop and program permissions", permissions, hasItems(
			Matchers.hasProperty("id", is(this.cropPermission.getPermissionId())),
			Matchers.hasProperty("id", is(this.programPermission.getPermissionId()))
		));

		final List<PermissionDto> permissionsForProject2 = this.permissionDAO
			.getPermissions(programAdmin.getUserid(), this.cropType.getCropName(), this.project2.getProjectId().intValue());

		assertThat("should not have access to other programs", permissionsForProject2, empty());
	}
}
