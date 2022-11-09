package org.generationcp.middleware.dao;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.domain.workbench.ProgramMemberDto;
import org.generationcp.middleware.domain.workbench.RoleType;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.workbench.CropPerson;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Role;
import org.generationcp.middleware.pojos.workbench.UserRole;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.user.UserDto;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class WorkbenchUserDAOIntegrationTest extends IntegrationTestBase {

	private ProjectDAO projectDao;
	private CropTypeDAO cropTypeDAO;
	private WorkbenchUserDAO workbenchUserDAO;
	private PersonDAO personDAO;
	private CropPersonDAO cropPersonDAO;
	private RoleDAO roleDAO;
	private UserRoleDao userRoleDao;

	private final String cropName = RandomStringUtils.randomAlphabetic(10);
	private CropType cropType;

	private WorkbenchUser user4;
	private Project project1;
	private Map<Integer, WorkbenchUser> workbenchUserMap;
	private Map<Integer, Role> roleDtoProject1Map;

	@Before
	public void setUp() {
		this.projectDao = new ProjectDAO();
		this.projectDao.setSession(this.workbenchSessionProvider.getSession());
		this.cropTypeDAO = new CropTypeDAO();
		this.cropTypeDAO.setSession(this.workbenchSessionProvider.getSession());
		this.workbenchUserDAO = new WorkbenchUserDAO();
		this.workbenchUserDAO.setSession(this.workbenchSessionProvider.getSession());
		this.personDAO = new PersonDAO();
		this.personDAO.setSession(this.workbenchSessionProvider.getSession());
		this.cropPersonDAO = new CropPersonDAO();
		this.cropPersonDAO.setSession(this.workbenchSessionProvider.getSession());
		this.roleDAO = new RoleDAO();
		this.roleDAO.setSession(this.workbenchSessionProvider.getSession());
		this.roleDAO = new RoleDAO();
		this.roleDAO.setSession(this.workbenchSessionProvider.getSession());
		this.userRoleDao = new UserRoleDao();
		this.userRoleDao.setSession(this.workbenchSessionProvider.getSession());
	}

	@Test
	public void testCountAllProgramEligibleUsers_Ok() {
		this.prepareTestData();
		final long count = this.workbenchUserDAO.countAllProgramEligibleUsers(project1.getUniqueID(), null);
		assertThat(count, is(1L));
	}

	@Test
	public void testGetAllProgramEligibleUsers_Ok() {
		this.prepareTestData();
		final List<UserDto> users = this.workbenchUserDAO.getAllProgramEligibleUsers(project1.getUniqueID(), null, null);
		assertThat(users, hasSize(1));
		final UserDto userDto = users.get(0);
		assertThat(userDto.getId(), equalTo(user4.getUserid()));
		assertThat(userDto.getFirstName(), equalTo(user4.getPerson().getFirstName()));
		assertThat(userDto.getLastName(), equalTo(user4.getPerson().getLastName()));
		assertThat(userDto.getEmail(), equalTo(user4.getPerson().getEmail()));
		assertThat(userDto.getUsername(), equalTo(user4.getName()));
	}

	@Test
	public void testCountAllProgramMembers_Ok() {
		this.prepareTestData();
		final long count = this.workbenchUserDAO.countAllProgramMembers(project1.getUniqueID(), null);
		assertThat(count, is(4L));
	}

	@Test
	public void testGetAllProgramMembers_Ok() {
		this.prepareTestData();
		final List<ProgramMemberDto> members = this.workbenchUserDAO.getProgramMembers(project1.getUniqueID(), null, null);
		assertThat(members, hasSize(4));
		members.forEach(m -> {
			final WorkbenchUser user = workbenchUserMap.get(m.getUserId());
			assertThat(m.getUserId(), equalTo(user.getUserid()));
			assertThat(m.getFirstName(), equalTo(user.getPerson().getFirstName()));
			assertThat(m.getLastName(), equalTo(user.getPerson().getLastName()));
			assertThat(m.getEmail(), equalTo(user.getPerson().getEmail()));
			assertThat(m.getUsername(), equalTo(user.getName()));
			final Role role = roleDtoProject1Map.get(user.getUserid());
			assertThat(m.getRole().getId(), equalTo(role.getId()));
			assertThat(m.getRole().getName(), equalTo(role.getName()));
			assertThat(m.getRole().getDescription(), equalTo(role.getDescription()));
			assertThat(m.getRole().getType(), equalToIgnoringCase(role.getRoleType().getName()));
		});
	}

	private void prepareTestData() {

		workbenchUserMap = new HashMap<>();
		roleDtoProject1Map = new HashMap<>();

		this.createCropType();

		final Role cropRole = this.createRole(RoleType.CROP);
		final Role programRole = this.createRole(RoleType.PROGRAM);

		project1 = this.saveProject(cropType, RandomStringUtils.randomAlphanumeric(10));
		final Project project2 = this.saveProject(cropType, RandomStringUtils.randomAlphanumeric(10));

		//Giving Admin access to the new crop
		this.assignNewCropTypeToAdminUser();

		// User with Crop Access
		final WorkbenchUser user1 = this.createUser(this.cropType);
		assignRoleToUser(user1, cropRole, cropType, null);
		roleDtoProject1Map.put(user1.getUserid(), cropRole);

		// With Program Access
		final WorkbenchUser user2 = this.createUser(this.cropType);
		assignRoleToUser(user2, programRole, cropType, project1);
		roleDtoProject1Map.put(user2.getUserid(), programRole);

		//User With no Access to the crop
		final WorkbenchUser user3 = this.createUser(new CropType("maize"));
		assignRoleToUser(user3, cropRole, new CropType("maize"), null);

		//User with access to another program in the crop
		user4 = this.createUser(this.cropType);
		assignRoleToUser(user4, programRole, cropType, project2);

		// With Crop and Program Access
		final WorkbenchUser user5 = this.createUser(this.cropType);
		assignRoleToUser(user5, programRole, cropType, project1);
		assignRoleToUser(user5, cropRole, cropType, null);
		roleDtoProject1Map.put(user5.getUserid(), programRole);
	}

	private void createCropType() {
		cropType = new CropType(cropName);
		this.cropTypeDAO.save(cropType);
		this.sessionProvder.getSession().flush();
	}

	private void assignRoleToUser(final WorkbenchUser u, final Role r, final CropType cropType, final Project project) {
		final UserRole userRole = new UserRole();
		userRole.setUser(u);
		userRole.setCropType(cropType);
		userRole.setRole(r);
		userRole.setWorkbenchProject(project);
		u.getRoles().add(userRole);
		this.userRoleDao.save(userRole);
		this.sessionProvder.getSession().flush();
	}

	private Role createRole(final RoleType roleType) {
		final Role role = new Role();
		final org.generationcp.middleware.pojos.workbench.RoleType roleType1 = new org.generationcp.middleware.pojos.workbench.RoleType();
		roleType1.setId(roleType.getId());
		role.setRoleType(roleType1);
		role.setName(RandomStringUtils.randomAlphabetic(10));
		this.roleDAO.save(role);
		this.sessionProvder.getSession().flush();
		this.userRoleDao.getSession().refresh(role);
		return role;
	}

	private void assignNewCropTypeToAdminUser() {
		final Integer adminUserId = this.findAdminUser();
		final WorkbenchUser workbenchUser = this.workbenchUserDAO.getById(adminUserId);
		final CropPerson cropPerson = new CropPerson(cropType, workbenchUser.getPerson());
		workbenchUser.getPerson().getCrops().add(cropType);
		this.cropPersonDAO.save(cropPerson);
		this.sessionProvder.getSession().flush();
		workbenchUserMap.put(adminUserId, workbenchUser);
		roleDtoProject1Map.put(adminUserId, workbenchUser.getInstanceRole().getRole());
	}

	private Project saveProject(final CropType cropType, final String projectName) {
		final Project project = new Project();
		project.setProjectName(projectName + RandomStringUtils.randomAlphanumeric(10));
		project.setStartDate(new Date());
		project.setUniqueID(UUID.randomUUID().toString());
		project.setLastOpenDate(new Date());
		project.setCropType(cropType);
		this.projectDao.save(project);
		this.sessionProvder.getSession().flush();
		this.projectDao.getSession().refresh(project);
		return project;
	}

	public WorkbenchUser createUser(final CropType cropType) {
		final Person person = new Person(RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomAlphabetic(10),
			RandomStringUtils.randomAlphabetic(10));
		person.setEmail(RandomStringUtils.randomAlphabetic(10));
		person.setContact(RandomStringUtils.randomAlphabetic(10));
		person.setExtension(RandomStringUtils.randomAlphabetic(10));
		person.setFax(RandomStringUtils.randomAlphabetic(10));
		person.setInstituteId(0);
		person.setLanguage(0);
		person.setNotes("");
		person.setPhone("");
		person.setPositionName("");
		person.setTitle("");

		final Set<CropType> crops = new HashSet<>();
		person.setCrops(crops);
		this.personDAO.save(person);

		final CropPerson cropPerson = new CropPerson();
		cropPerson.setCropType(cropType);
		cropPerson.setPerson(person);
		person.getCrops().add(cropType);
		this.cropPersonDAO.save(cropPerson);

		final WorkbenchUser user =
			new WorkbenchUser(null, 1, 0, 1, 1, RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomAlphabetic(10), person,
				20150101, 20150101);

		this.workbenchUserDAO.save(user);
		this.sessionProvder.getSession().flush();
		this.workbenchUserDAO.getSession().refresh(user);
		workbenchUserMap.put(user.getUserid(), user);

		return user;
	}

}
