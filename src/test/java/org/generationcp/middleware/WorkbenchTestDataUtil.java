
package org.generationcp.middleware;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.api.crop.CropService;
import org.generationcp.middleware.api.program.ProgramService;
import org.generationcp.middleware.manager.WorkbenchDaoFactory;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectActivity;
import org.generationcp.middleware.pojos.workbench.ProjectUserInfo;
import org.generationcp.middleware.pojos.workbench.Role;
import org.generationcp.middleware.pojos.workbench.RoleType;
import org.generationcp.middleware.pojos.workbench.UserInfo;
import org.generationcp.middleware.pojos.workbench.UserRole;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.user.RoleDto;
import org.generationcp.middleware.service.api.user.RoleTypeDto;
import org.generationcp.middleware.service.api.user.UserDto;
import org.generationcp.middleware.service.api.user.UserRoleDto;
import org.generationcp.middleware.service.api.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class WorkbenchTestDataUtil {

	@Autowired
	private CropService cropService;

	@Autowired
	private UserService userService;

	@Autowired
	private ProgramService programService;

	private Project commonTestProject;
	private WorkbenchUser testUser1, testUser2;
	private Person testPerson1, testPerson2;
	private ProjectActivity testProjectActivity1, testProjectActivity2;
	private CropType cropType;


	public WorkbenchTestDataUtil() {
		// Do nothing
	}

	public Person createTestPersonData() {
		final Person person = new Person();
		person.setInstituteId(1);
		person.setFirstName("Test");
		person.setMiddleName("M");
		final int randomNumber = new Random().nextInt();
		person.setLastName("Person " + randomNumber);
		person.setPositionName("King of Icewind Dale");
		person.setTitle("His Highness");
		person.setExtension("Ext");
		person.setFax("Fax");
		person.setEmail("lichking" + randomNumber + "@blizzard.com");
		person.setNotes("notes");
		person.setContact("Contact");
		person.setLanguage(1);
		person.setPhone("Phone");
		return person;
	}

	public WorkbenchUser createTestUserData() {
		final WorkbenchUser user = new WorkbenchUser();
		user.setInstalid(1);
		user.setStatus(0);
		user.setAccess(1);
		user.setType(1);
		user.setName("user_test" + new Random().nextInt());
		user.setPassword("user_password");
		final Person person = new Person();
		person.setContact(RandomStringUtils.randomAlphabetic(5));
		person.setEmail(RandomStringUtils.randomAlphabetic(5));
		person.setExtension(RandomStringUtils.randomAlphabetic(5));
		person.setFax(RandomStringUtils.randomAlphabetic(5));
		person.setFirstName(RandomStringUtils.randomAlphabetic(5));
		person.setLastName(RandomStringUtils.randomAlphabetic(5));
		person.setInstituteId(RandomUtils.nextInt());
		person.setLanguage(RandomUtils.nextInt());
		person.setMiddleName(RandomStringUtils.randomAlphabetic(5));
		person.setNotes(RandomStringUtils.randomAlphabetic(5));
		person.setPhone(RandomStringUtils.randomAlphabetic(5));
		person.setPositionName(RandomStringUtils.randomAlphabetic(5));
		person.setTitle(RandomStringUtils.randomAlphabetic(5));

		user.setPerson(person);
		user.setAssignDate(20150101);
		user.setCloseDate(20150101);
		final UserRole userRole = new UserRole();
		userRole.setUser(user);
		userRole.setCropType(this.cropType);
		userRole.setWorkbenchProject(this.commonTestProject);
		final Role role = new Role();
		role.setId(1);
		role.setActive(true);
		final RoleType roleType = new RoleType();
		roleType.setId(1);
		role.setRoleType(roleType);
		userRole.setRole(role);
		final List<UserRole> userRoleDto = new ArrayList<>();
		userRoleDto.add(userRole);
		user.setRoles(userRoleDto);

		final Set<CropType> crops = new HashSet<>();
		crops.add(this.cropType);
		person.setCrops(crops);
		return user;
	}

	public Project createTestProjectData() {
		final Project project = new Project();
		project.setUserId(1);
		final int uniqueId = new Random().nextInt(10000);
		project.setProjectName("Test Project " + uniqueId);
		project.setStartDate(new Date(System.currentTimeMillis()));
		project.setCropType(this.cropType);
		project.setLastOpenDate(new Date(System.currentTimeMillis()));
		project.setUniqueID(Integer.toString(uniqueId));
		return project;
	}

	public ProjectActivity createTestProjectActivityData(final Project project, final WorkbenchUser user) {
		final ProjectActivity projectActivity = new ProjectActivity();
		projectActivity.setProject(project);
		projectActivity.setName("Project Activity" + new Random().nextInt());
		projectActivity.setDescription("Some project activity");
		projectActivity.setUser(user);
		projectActivity.setCreatedAt(new Date(System.currentTimeMillis()));
		return projectActivity;
	}

	public void setUpWorkbench(final WorkbenchDaoFactory workbenchDaoFactory) {
		this.commonTestProject = this.createTestProjectData();
		this.cropType = this.cropService.getCropTypeByName(CropType.CropEnum.MAIZE.toString());
		this.commonTestProject.setCropType(this.cropType);
		final Set<CropType> crops = new HashSet<>();
		crops.add(this.cropType);

		this.testPerson1 = this.createTestPersonData();
		this.testPerson2 = this.createTestPersonData();
		this.testUser1 = this.createTestUserData();
		this.testUser1.setPerson(this.testPerson1);
		this.testPerson1.setCrops(crops);
		workbenchDaoFactory.getWorkbenchUserDAO().save(this.testUser1);
		this.testUser2 = this.createTestUserData();
		this.testUser2.setPerson(this.testPerson2);
		this.testPerson2.setCrops(crops);
		workbenchDaoFactory.getWorkbenchUserDAO().save(this.testUser2);

		this.commonTestProject.setUserId(this.testUser1.getUserid());
		this.programService.addProgram(this.commonTestProject);

		this.testProjectActivity1 = this.createTestProjectActivityData(this.commonTestProject, this.testUser1);
		this.programService.addProjectActivity(this.testProjectActivity1);

		this.testProjectActivity2 = this.createTestProjectActivityData(this.commonTestProject, this.testUser2);
		this.programService.addProjectActivity(this.testProjectActivity2);

		final UserInfo userInfo = new UserInfo();
		//TODO check if this is needed since we are hardcoding to user id 3
		userInfo.setUserId(3);
		userInfo.setLoginCount(5);
		workbenchDaoFactory.getUserInfoDAO().insertOrUpdateUserInfo(userInfo);

		// Save test users 1 and 2 as members of test program
		ProjectUserInfo pui = new ProjectUserInfo();
		final WorkbenchUser workbenchUser = new WorkbenchUser();
		workbenchUser.setUserid(this.commonTestProject.getUserId());
		pui.setProject(this.commonTestProject);
		pui.setUser(workbenchUser);
		pui.setLastOpenDate(new Date());
		workbenchDaoFactory.getProjectUserInfoDAO().saveOrUpdate(pui);

		pui = new ProjectUserInfo();
		pui.setProject(this.commonTestProject);
		pui.setUser(this.testUser2);
		pui.setLastOpenDate(new Date());
		workbenchDaoFactory.getProjectUserInfoDAO().saveOrUpdate(pui);
	}

	public Project getCommonTestProject() {
		if (this.commonTestProject == null) {
			this.commonTestProject = this.createTestProjectData();
		}
		return this.commonTestProject;
	}

	public CropType getCommonCropType() {
		return this.cropType;
	}

	public WorkbenchUser getTestUser1() {
		if (this.testUser1 == null) {
			this.testUser1 = this.createTestUserData();
		}
		return this.testUser1;
	}

	public WorkbenchUser getTestUser2() {
		if (this.testUser2 == null) {
			this.testUser2 = this.createTestUserData();
		}
		return this.testUser2;
	}

	public UserDto createTestUserDTO(final Integer userId) {
		final UserDto userdto = new UserDto();

		if (userId != null && !userId.equals(0)) {
			userdto.setId(userId);
		}

		final String username = RandomStringUtils.randomAlphanumeric(30);
		userdto.setUsername(username);
		final String firstName = RandomStringUtils.randomAlphanumeric(20);
		userdto.setFirstName(firstName);
		final String lastName = RandomStringUtils.randomAlphanumeric(50);
		userdto.setLastName(lastName);
		final UserRoleDto userRoleDto = new UserRoleDto(1,
			new RoleDto(1, "Admin", "",
				new RoleTypeDto(1, "instance"), true, true,
				true), null,
			null, null);
		final List<UserRoleDto> userRoleDtos = new ArrayList<>();
		userRoleDtos.add(userRoleDto);
		userdto.setPassword("fwgtrgrehgewsdsdeferhkjlkjSli");
		final String email = RandomStringUtils.randomAlphanumeric(24);
		userdto.setEmail("test" + email + "@leafnode.io");
		userdto.setStatus("true");
		return userdto;
	}

	public void setCropType(final CropType cropType) {
		this.cropType = cropType;
	}

}
