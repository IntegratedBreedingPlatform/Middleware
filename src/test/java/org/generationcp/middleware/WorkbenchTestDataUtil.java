
package org.generationcp.middleware;

import java.util.Date;
import java.util.Random;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectActivity;
import org.generationcp.middleware.pojos.workbench.ProjectUserInfo;
import org.generationcp.middleware.pojos.workbench.UserInfo;
import org.generationcp.middleware.pojos.workbench.WorkbenchRuntimeData;

public class WorkbenchTestDataUtil {

	private final WorkbenchDataManager workbenchDataManager;
	private Project commonTestProject;
	private User testUser1, testUser2;
	private Person testPerson1, testPerson2;
	private ProjectActivity testProjectActivity1, testProjectActivity2;
	private CropType cropType;

	public WorkbenchTestDataUtil(WorkbenchDataManager workbenchDataManager) {
		this.workbenchDataManager = workbenchDataManager;
	}

	public Person createTestPersonData() {
		Person person = new Person();
		person.setInstituteId(1);
		person.setFirstName("Test");
		person.setMiddleName("M");
		person.setLastName("Person " + new Random().nextInt());
		person.setPositionName("King of Icewind Dale");
		person.setTitle("His Highness");
		person.setExtension("Ext");
		person.setFax("Fax");
		person.setEmail("lichking@blizzard.com");
		person.setNotes("notes");
		person.setContact("Contact");
		person.setLanguage(1);
		person.setPhone("Phone");
		return person;
	}

	public User createTestUserData() {
		User user = new User();
		user.setInstalid(1);
		user.setStatus(1);
		user.setAccess(1);
		user.setType(1);
		user.setName("user_test" + new Random().nextInt());
		user.setPassword("user_password");
		user.setPersonid(1);
		user.setAssignDate(20150101);
		user.setCloseDate(20150101);
		return user;
	}

	public Project createTestProjectData() throws MiddlewareQueryException {
		Project project = new Project();
		project.setUserId(1);
		int uniqueId = new Random().nextInt(10000);
		project.setProjectName("Test Project " + uniqueId);
		project.setStartDate(new Date(System.currentTimeMillis()));
		project.setCropType(this.cropType);
		project.setLastOpenDate(new Date(System.currentTimeMillis()));
		project.setUniqueID(Integer.toString(uniqueId));
		return project;
	}

	public ProjectActivity createTestProjectActivityData(Project project, User user) {
		ProjectActivity projectActivity = new ProjectActivity();
		projectActivity.setProject(project);
		projectActivity.setName("Project Activity" + new Random().nextInt());
		projectActivity.setDescription("Some project activity");
		projectActivity.setUser(user);
		projectActivity.setCreatedAt(new Date(System.currentTimeMillis()));
		return projectActivity;
	}

	public void setUpWorkbench() throws MiddlewareQueryException {
		this.testPerson1 = this.createTestPersonData();
		this.workbenchDataManager.addPerson(this.testPerson1);
		this.testPerson2 = this.createTestPersonData();
		this.workbenchDataManager.addPerson(this.testPerson2);

		this.testUser1 = this.createTestUserData();
		this.testUser1.setPersonid(this.testPerson1.getId());
		this.workbenchDataManager.addUser(this.testUser1);
		this.testUser2 = this.createTestUserData();
		this.testUser2.setPersonid(this.testPerson2.getId());
		this.workbenchDataManager.addUser(this.testUser2);

		this.commonTestProject = this.createTestProjectData();
		this.commonTestProject.setUserId(this.testUser1.getUserid());
		this.workbenchDataManager.addProject(this.commonTestProject);

		this.testProjectActivity1 = this.createTestProjectActivityData(this.commonTestProject, this.testUser1);
		this.workbenchDataManager.addProjectActivity(this.testProjectActivity1);

		this.testProjectActivity2 = this.createTestProjectActivityData(this.commonTestProject, this.testUser2);
		this.workbenchDataManager.addProjectActivity(this.testProjectActivity2);

		UserInfo userInfo = new UserInfo();
		userInfo.setUserId(3);
		userInfo.setLoginCount(5);
		this.workbenchDataManager.insertOrUpdateUserInfo(userInfo);

		ProjectUserInfo pui = new ProjectUserInfo();
		pui.setProjectId(new Integer(Integer.parseInt(this.commonTestProject.getProjectId().toString())));
		pui.setUserId(this.commonTestProject.getUserId());
		pui.setLastOpenDate(new Date());
		this.workbenchDataManager.saveOrUpdateProjectUserInfo(pui);

		WorkbenchRuntimeData workbenchRuntimeData = new WorkbenchRuntimeData();
		workbenchRuntimeData.setUserId(1);
		this.workbenchDataManager.updateWorkbenchRuntimeData(workbenchRuntimeData);
		this.cropType = this.workbenchDataManager.getCropTypeByName(CropType.CropEnum.RICE.toString());

	}

	public Project getCommonTestProject() throws MiddlewareQueryException {
		if (this.commonTestProject == null) {
			this.commonTestProject = this.createTestProjectData();
		}
		return this.commonTestProject;
	}

	public CropType getCommonCropType() {
		return this.cropType;
	}

	public User getTestUser1() {
		if (this.testUser1 == null) {
			this.testUser1 = this.createTestUserData();
		}
		return this.testUser1;
	}

}
