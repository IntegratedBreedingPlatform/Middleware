package org.generationcp.middleware.service.impl.user;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.WorkbenchTestDataUtil;
import org.generationcp.middleware.data.initializer.UserDtoTestDataInitializer;
import org.generationcp.middleware.domain.workbench.CropDto;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.WorkbenchDaoFactory;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.workbench.CropPerson;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectUserInfo;
import org.generationcp.middleware.pojos.workbench.Role;
import org.generationcp.middleware.pojos.workbench.UserRole;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.user.UserDto;
import org.generationcp.middleware.service.api.user.UserService;
import org.generationcp.middleware.utils.test.IntegrationTestDataInitializer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class UserServiceImplTest extends IntegrationTestBase {

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Autowired
	private UserService userService;

	@Autowired
	private WorkbenchTestDataUtil workbenchTestDataUtil;

	private IntegrationTestDataInitializer integrationTestDataInitializer;

	private Project commonTestProject;
	private WorkbenchUser testUser1;
	private WorkbenchDaoFactory workbenchDaoFactory;

	@Before
	public void beforeTest() {

		this.workbenchTestDataUtil.setUpWorkbench();

		if (this.commonTestProject == null) {
			this.commonTestProject = this.workbenchTestDataUtil.getCommonTestProject();
		}

		if (this.testUser1 == null) {
			this.testUser1 = this.workbenchTestDataUtil.getTestUser1();
		}

		this.workbenchDaoFactory = new WorkbenchDaoFactory(this.workbenchSessionProvider);
		this.integrationTestDataInitializer = new IntegrationTestDataInitializer(this.sessionProvder, this.workbenchSessionProvider);
	}

	@Test
	public void testAddUser() {
		final WorkbenchUser user = this.workbenchTestDataUtil.createTestUserData();
		final WorkbenchUser result = this.userService.addUser(user);
		assertNotNull("Expected id of a newly saved record in workbench_user.", result);

		final WorkbenchUser readUser = this.userService.getUserById(result.getUserid());
		assertEquals(user.getName(), readUser.getName());
	}

	@Test
	public void testAddPerson() {
		final Person person = this.workbenchTestDataUtil.createTestPersonData();
		final Person result = this.userService.addPerson(person);
		assertNotNull("Expected id of a newly saved record in persons.", result);

		final Person readPerson = this.userService.getPersonById(result.getId());
		assertEquals(person.getLastName(), readPerson.getLastName());
	}

	@Test
	public void testAddUsersWithRoles() {
		// Admin
		final Person adminPerson = new Person();
		adminPerson.setFirstName("Naymesh");
		adminPerson.setMiddleName("-");
		adminPerson.setLastName("Mistry");
		adminPerson.setEmail("naymesh@leafnode.io");
		adminPerson.setInstituteId(0);
		adminPerson.setPositionName("-");
		adminPerson.setTitle("-");
		adminPerson.setExtension("-");
		adminPerson.setFax("-");
		adminPerson.setNotes("-");
		adminPerson.setContact("-");
		adminPerson.setLanguage(0);
		adminPerson.setPhone("-");
		final Person savedAdminPerson = this.userService.addPerson(adminPerson);
		assertNotNull("Expected id of a newly saved record in persons.", savedAdminPerson);

		final WorkbenchUser adminUser = new WorkbenchUser();
		adminUser.setName("admin");
		adminUser.setPassword("b");
		adminUser.setPerson(savedAdminPerson);
		adminUser.setInstalid(0);
		adminUser.setStatus(0);
		adminUser.setAccess(0);
		adminUser.setType(0);
		adminUser.setAssignDate(20140101);
		adminUser.setCloseDate(20140101);

		final List<UserRole> adminRoles = new ArrayList<>();
		// Role ID 1 = ADMIN
		adminRoles.add(new UserRole(adminUser, 1));
		adminUser.setRoles(adminRoles);
		this.userService.addUser(adminUser);
		assertNotNull("Expected id of a newly saved record in users.", adminUser.getUserid());

		// Breeder
		final Person breederPerson = new Person();
		breederPerson.setFirstName("Matthew");
		breederPerson.setMiddleName("-");
		breederPerson.setLastName("Berrigan");
		breederPerson.setEmail("matthew@leafnode.io");
		breederPerson.setInstituteId(0);
		breederPerson.setPositionName("-");
		breederPerson.setTitle("-");
		breederPerson.setExtension("-");
		breederPerson.setFax("-");
		breederPerson.setNotes("-");
		breederPerson.setContact("-");
		breederPerson.setLanguage(0);
		breederPerson.setPhone("-");
		final Person savedBreederPerson = this.userService.addPerson(breederPerson);
		assertNotNull("Expected newly saved record in persons is not null", savedBreederPerson);

		final WorkbenchUser breederUser = new WorkbenchUser();
		breederUser.setName("breeder");
		breederUser.setPassword("b");
		breederUser.setPerson(savedBreederPerson);
		breederUser.setInstalid(0);
		breederUser.setStatus(0);
		breederUser.setAccess(0);
		breederUser.setType(0);
		breederUser.setAssignDate(20140101);
		breederUser.setCloseDate(20140101);

		final List<UserRole> breederRoles = new ArrayList<>();
		// Role ID 2 = BREEDER
		breederRoles.add(new UserRole(breederUser, 2));
		breederUser.setRoles(breederRoles);
		this.userService.addUser(breederUser);
		assertNotNull("Expected id of a newly saved record in users.", adminUser.getUserid());

		// Technician
		final Person technicianPerson = new Person();
		technicianPerson.setFirstName("Lisa");
		technicianPerson.setMiddleName("-");
		technicianPerson.setLastName("Quayle");
		technicianPerson.setEmail("lisa@leafnode.io");
		technicianPerson.setInstituteId(0);
		technicianPerson.setPositionName("-");
		technicianPerson.setTitle("-");
		technicianPerson.setExtension("-");
		technicianPerson.setFax("-");
		technicianPerson.setNotes("-");
		technicianPerson.setContact("-");
		technicianPerson.setLanguage(0);
		technicianPerson.setPhone("-");
		final Person savedTechnicalPerson = this.userService.addPerson(technicianPerson);
		assertNotNull("Expected id of a newly saved record in persons.", savedTechnicalPerson);

		final WorkbenchUser technicianUser = new WorkbenchUser();
		technicianUser.setName("technician");
		technicianUser.setPassword("b");
		technicianUser.setPerson(savedTechnicalPerson);
		technicianUser.setInstalid(0);
		technicianUser.setStatus(0);
		technicianUser.setAccess(0);
		technicianUser.setType(0);
		technicianUser.setAssignDate(20140101);
		technicianUser.setCloseDate(20140101);

		final List<UserRole> technicianRoles = new ArrayList<>();
		// Role ID 3 = TECHNICIAN
		technicianRoles.add(new UserRole(technicianUser, 3));
		technicianUser.setRoles(technicianRoles);
		this.userService.addUser(technicianUser);
		assertNotNull("Expected id of a newly saved record in users.", technicianUser.getUserid());
	}

	@Test
	public void testGetUserByName() {
		final WorkbenchUser user = this.userService.getUserByName(this.testUser1.getName(), 0, 1, Operation.EQUAL).get(0);
		assertEquals(this.testUser1.getName(), user.getName());
		assertEquals(this.testUser1.getUserid(), user.getUserid());
	}

	@Test
	public void testGetUserByFullname() {
		final Person person = this.workbenchTestDataUtil.createTestPersonData();
		this.userService.addPerson(person);
		final WorkbenchUser user = this.workbenchTestDataUtil.createTestUserData();
		user.setStatus(0);
		user.setPerson(person);
		this.userService.addUser(user);
		final WorkbenchUser retrievedUser = this.userService.getUserByFullname(user.getPerson().getDisplayName());
		Assert.assertEquals(user.getUserid(), retrievedUser.getUserid());

		user.setStatus(1);
		this.userService.addUser(user);
		Assert.assertNull(this.userService.getUserByFullname(user.getPerson().getDisplayName()));
	}

	@Test
	public void testCountUsersByFullname() {
		final Person person = this.workbenchTestDataUtil.createTestPersonData();
		this.userService.addPerson(person);
		final WorkbenchUser user = this.workbenchTestDataUtil.createTestUserData();
		user.setStatus(0);
		user.setPerson(person);
		this.userService.addUser(user);
		final Long count = this.userService.countUsersByFullname(user.getPerson().getDisplayName());

		user.setStatus(1);
		this.userService.addUser(user);
		final Long newCount = this.userService.countUsersByFullname(user.getPerson().getDisplayName());
		Assert.assertEquals(count.toString(), String.valueOf(newCount+1));
	}

	@Test
	public void testCountAllPersons() {
		final long count = this.userService.countAllPersons();
		assertTrue(count > 0);
	}

	@Test
	public void testCountAllUsers() {
		final long count = this.userService.countAllUsers();
		assertTrue(count > 0);
	}

	@Test
	public void testGetAllPersons() {
		final List<Person> results = this.userService.getAllPersons();
		assertNotNull(results);
		assertTrue(!results.isEmpty());
	}

	@Test
	public void testGetAllUsers() {
		final List<WorkbenchUser> results = this.userService.getAllUsers();
		assertNotNull(results);
		assertTrue(!results.isEmpty());
	}

	@Test
	public void testGetUserById() {
		final WorkbenchUser user = this.userService.getUserById(this.testUser1.getUserid());
		assertNotNull(user);
	}

	@Test
	public void testDeletePerson() {
		final Person person = this.workbenchTestDataUtil.createTestPersonData();
		this.userService.addPerson(person);
		this.userService.deletePerson(person);
	}

	@Test
	public void testCreateUser() {
		final UserDto userDto = this.workbenchTestDataUtil.createTestUserDTO(0);
		final Integer result = this.userService.createUser(userDto);

		assertThat("Expected id of a newly saved record in workbench_user.", result != null);
		assertThat("Expected id of new user distinct of 0", !result.equals(0));
	}

	@Test
	public void testUpdateUser() {
		final UserDto userDto = this.workbenchTestDataUtil.createTestUserDTO(0);
		final Integer userId = this.userService.createUser(userDto);
		userDto.setUserId(userId);
		final Integer result = this.userService.updateUser(userDto);

		assertThat("Expected id of userDto saved record in workbench_user.", result != null);
		assertThat("Expected the same id of userDto saved record ", result.equals(userId));
	}

	@Test
	public void testGetAllActiveUsers() {
		final List<WorkbenchUser> prevListOfActiveUsers = this.userService.getAllActiveUsersSorted();
		final UserDto userDto =
			UserDtoTestDataInitializer.createUserDto("FirstName", "LastName", "email@leafnode.io", "password", null, "username");
		final int id = this.userService.createUser(userDto);
		userDto.setUserId(id);
		List<WorkbenchUser> listOfActiveUsers = this.userService.getAllActiveUsersSorted();
		assertEquals("The newly added user should be added in the retrieved list.",
			prevListOfActiveUsers.size() + 1, listOfActiveUsers.size());

		//Deactivate the user to check if it's not retrieved
		userDto.setStatus(1);
		this.userService.updateUser(userDto);
		listOfActiveUsers = this.userService.getAllActiveUsersSorted();
		assertEquals("The newly added user should be added in the retrieved list.",
			prevListOfActiveUsers.size(), listOfActiveUsers.size());

	}

	@Test
	public void testGetUsersByCrop() {
		final String cropName = CropType.CropEnum.MAIZE.toString();
		final List<WorkbenchUser> prevListOfActiveUsers = this.userService.getUsersByCrop(cropName);
		final UserDto userDto =
			UserDtoTestDataInitializer.createUserDto("FirstName", "LastName", "email@leafnode.io", "password", null, "username");
		final Set<CropDto> crops = new HashSet<>();
		final CropDto cropDto = new CropDto();
		cropDto.setCropName(cropName);
		crops.add(cropDto);
		userDto.setCrops(crops);
		final int id = this.userService.createUser(userDto);
		userDto.setUserId(id);
		List<WorkbenchUser> users = this.userService.getUsersByCrop(cropName);
		assertEquals("The newly added user should be added in the retrieved list.", prevListOfActiveUsers.size() + 1, users.size());

		//Deactivate the user to check if it's not retrieved
		userDto.setStatus(1);
		this.userService.updateUser(userDto);
		users = this.userService.getUsersByCrop(cropName);
		assertEquals("The newly added user should be added in the retrieved list.", prevListOfActiveUsers.size(), users.size());

	}

	@Test
	public void testUpdateUserWithPerson() {

		final UserDto userDto = this.workbenchTestDataUtil.createTestUserDTO(0);

		final WorkbenchUser userToBeUpdated = this.userService.getUserById(this.userService.createUser(userDto));

		final String password = "password1111";
		final String firstName = "John";
		final String lastName = "Doe";
		final String email = "John.Doe@email.com";

		userToBeUpdated.setPassword(password);
		userToBeUpdated.getPerson().setFirstName(firstName);
		userToBeUpdated.getPerson().setLastName(lastName);
		userToBeUpdated.getPerson().setEmail(email);

		this.userService.updateUser(userToBeUpdated);

		final WorkbenchUser updatedUser = this.userService.getUserById(userToBeUpdated.getUserid());

		assertEquals(password, updatedUser.getPassword());
		assertEquals(firstName, updatedUser.getPerson().getFirstName());
		assertEquals(lastName, updatedUser.getPerson().getLastName());
		assertEquals(email, updatedUser.getPerson().getEmail());
	}

	@Test
	public void testGetUsersByProjectId() {
		final List<WorkbenchUser> results = this.userService.getUsersByProjectId(this.commonTestProject.getProjectId());
		assertNotNull(results);
		this.userService.addUser(this.workbenchTestDataUtil.createTestUserData());

		this.sessionProvder.getSession().flush();
		final List<WorkbenchUser> newResults = this.userService.getUsersByProjectId(this.commonTestProject.getProjectId());
		assertNotNull(newResults);
		Assert.assertEquals(results.size() + 1, newResults.size());
	}

	@Test
	public void testGetAllUserDtosSorted() {
		final List<UserDto> userDtos = this.userService.getAllUsersSortedByLastName();
		assertThat("Expected list users not null.", userDtos != null);
		assertThat("Expected list users not empty.", !userDtos.isEmpty());
	}

	@Test
	public void testGetSuperAdminUsers() {
		final List<WorkbenchUser> superAdminUsers = this.userService.getSuperAdminUsers();
		int superAdminCountBefore = 0;
		if (superAdminUsers != null) {
			superAdminCountBefore = superAdminUsers.size();
		}
		final WorkbenchUser user = this.workbenchTestDataUtil.createTestUserData();
		user.setRoles(Arrays.asList(new UserRole(user, new Role(5, "SUPERADMIN"))));

		final WorkbenchUser workbenchUser = this.userService.addUser(user);
		final List<WorkbenchUser> latestSuperAdminUsers = this.userService.getSuperAdminUsers();
		assertNotNull(latestSuperAdminUsers);
		assertEquals(latestSuperAdminUsers.size(), superAdminCountBefore + 1);
		assertTrue(latestSuperAdminUsers.contains(workbenchUser));
	}

	@Test
	public void testIsSuperAdminUser() {
		final WorkbenchUser savedUser1 = this.userService.addUser(this.workbenchTestDataUtil.createTestUserData());
		final WorkbenchUser user2 = this.workbenchTestDataUtil.createTestUserData();
		final Role role = new Role();
		role.setId(5);
		user2.setRoles(Arrays.asList(new UserRole(user2, role)));
		final WorkbenchUser savedUser2 = this.userService.addUser(user2);

		Assert.assertFalse(this.userService.isSuperAdminUser(savedUser1.getUserid()));
		assertTrue(this.userService.isSuperAdminUser(savedUser2.getUserid()));
	}

	@Test
	public void testGetProjectUserInfoByProjectIdAndUserId() {
		final Project project = this.workbenchTestDataUtil.createTestProjectData();
		this.workbenchDataManager.addProject(project);
		final WorkbenchUser user1 = this.userService.addUser(this.workbenchTestDataUtil.createTestUserData());

		final ProjectUserInfo pUserInfo = new ProjectUserInfo(project, user1);
		this.userService.saveOrUpdateProjectUserInfo(pUserInfo);
		final ProjectUserInfo result = this.userService.getProjectUserInfoByProjectIdAndUserId(project.getProjectId(), user1.getUserid());
		assertEquals(user1.getUserid(), result.getUser().getUserid());
		assertEquals(project.getProjectId(), result.getProject().getProjectId());
	}

	@Test
	public void testGetPersonsByCrop() {
		final WorkbenchUser workbenchUser = this.integrationTestDataInitializer.createUserForTesting();
		final CropPerson cropPerson = new CropPerson(this.commonTestProject.getCropType(), workbenchUser.getPerson());
		this.userService.saveCropPerson(cropPerson);
		final List<Person> persons = this.userService.getPersonsByCrop(this.commonTestProject.getCropType());

		assertTrue(!persons.isEmpty());
	}

	@Test
	public void testGetPersonNamesByPersonIds() {
		final WorkbenchUser workbenchUser = this.integrationTestDataInitializer.createUserForTesting();
		final Map<Integer, String> result = this.userService.getPersonNamesByPersonIds(Arrays.asList(workbenchUser.getPerson().getId()));
		assertEquals(workbenchUser.getPerson().getDisplayName(), result.get(workbenchUser.getPerson().getId()));
	}

	@Test
	public void testGetPesonName() {
		final WorkbenchUser workbenchUser = this.integrationTestDataInitializer.createUserForTesting();
		assertEquals(workbenchUser.getPerson().getDisplayName(), this.userService.getPersonNameForUserId(workbenchUser.getUserid()));
	}

	@Test
	public void testGetUserIDFullNameMap() {
		final WorkbenchUser workbenchUser = this.integrationTestDataInitializer.createUserForTesting();
		final Map<Integer, String> result = this.userService.getUserIDFullNameMap(Arrays.asList(workbenchUser.getUserid()));
		assertEquals(workbenchUser.getPerson().getFirstName() + " " + workbenchUser.getPerson().getLastName(),
			result.get(workbenchUser.getUserid()));
	}

	@Test
	public void testSaveCropPerson() {
		final WorkbenchUser workbenchUser = this.integrationTestDataInitializer.createUserForTesting();
		final CropPerson cropPerson = new CropPerson(this.commonTestProject.getCropType(), workbenchUser.getPerson());
		this.userService.saveCropPerson(cropPerson);

		final CropPerson savedCropPerson = this.workbenchDaoFactory.getCropPersonDAO().getByCropNameAndPersonId(this.commonTestProject.getCropType().getCropName(), cropPerson.getPerson().getId());
		assertNotNull(savedCropPerson);
	}

	@Test
	public void testRemoveCropPerson() {
		final WorkbenchUser workbenchUser = this.integrationTestDataInitializer.createUserForTesting();
		final CropPerson cropPerson = new CropPerson(this.commonTestProject.getCropType(), workbenchUser.getPerson());
		this.userService.saveCropPerson(cropPerson);
		this.userService.removeCropPerson(cropPerson);

		final CropPerson savedCropPerson = this.workbenchDaoFactory.getCropPersonDAO().getByCropNameAndPersonId(this.commonTestProject.getCropType().getCropName(), cropPerson.getPerson().getId());
		assertNull(savedCropPerson);
	}

}
