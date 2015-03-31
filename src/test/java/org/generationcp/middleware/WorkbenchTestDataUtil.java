package org.generationcp.middleware;

import java.util.Date;
import java.util.Random;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionPerThreadProvider;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.WorkbenchDataManagerImpl;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectActivity;
import org.generationcp.middleware.pojos.workbench.ProjectUserInfo;
import org.generationcp.middleware.pojos.workbench.UserInfo;
import org.generationcp.middleware.pojos.workbench.WorkbenchRuntimeData;

public class WorkbenchTestDataUtil extends DataManagerIntegrationTest {
	
	private static WorkbenchTestDataUtil instance;
	private WorkbenchDataManager workbenchDataManager;
	private Project commonTestProject;
    private User testUser1, testUser2;
    private Person testPerson1, testPerson2;
    private ProjectActivity testProjectActivity1, testProjectActivity2;
	
	private WorkbenchTestDataUtil() {
		HibernateSessionProvider sessionProvider = new HibernateSessionPerThreadProvider(workbenchSessionUtil.getSessionFactory());
		workbenchDataManager = new WorkbenchDataManagerImpl(sessionProvider);   
	}
	
	public static WorkbenchTestDataUtil getInstance() {
		if(instance==null) {
			instance = new WorkbenchTestDataUtil();
		}
		return instance;
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
        person.setLanguage(-1);
        person.setPhone("Phone");
		return person;
	}
	
	public User createTestUserData() {
		User user = new User();
        user.setInstalid(-1);
        user.setStatus(-1);
        user.setAccess(-1);
        user.setType(-1);
        user.setName("user_test" + new Random().nextInt());
        user.setPassword("user_password");
        user.setPersonid(1);
        user.setAdate(20120101);
        user.setCdate(20120101);
		return user;
	}
	


	public Project createTestProjectData() throws MiddlewareQueryException {
		Project project = new Project();
        project.setUserId(1);
        int uniqueId = new Random().nextInt(10000);
        project.setProjectName("Test Project " + uniqueId);
        project.setStartDate(new Date(System.currentTimeMillis()));
        project.setCropType(workbenchDataManager.getCropTypeByName(CropType.CropEnum.RICE.toString()));
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
	
	public void setUpWorkbench() throws MiddlewareQueryException  {
        testPerson1 = createTestPersonData();
        workbenchDataManager.addPerson(testPerson1);
        testPerson2 = createTestPersonData();
        workbenchDataManager.addPerson(testPerson2);
        
        testUser1 = createTestUserData();
        testUser1.setPersonid(testPerson1.getId());
        workbenchDataManager.addUser(testUser1);
        testUser2 = createTestUserData();
        testUser2.setPersonid(testPerson2.getId());
        workbenchDataManager.addUser(testUser2);
        
        commonTestProject = createTestProjectData();
        commonTestProject.setUserId(testUser1.getUserid());
        workbenchDataManager.addProject(commonTestProject);
        
        testProjectActivity1 = createTestProjectActivityData(commonTestProject, testUser1);
        workbenchDataManager.addProjectActivity(testProjectActivity1);
        
        testProjectActivity2 = createTestProjectActivityData(commonTestProject, testUser2);
        workbenchDataManager.addProjectActivity(testProjectActivity2);
        
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(3);
        userInfo.setLoginCount(5);
        workbenchDataManager.insertOrUpdateUserInfo(userInfo);
        
    	ProjectUserInfo pui =  new ProjectUserInfo();
    	pui.setProjectId(new Integer(Integer.parseInt(commonTestProject.getProjectId().toString())));
    	pui.setUserId(commonTestProject.getUserId());
    	pui.setLastOpenDate(new Date());
    	workbenchDataManager.saveOrUpdateProjectUserInfo(pui);
        
        WorkbenchRuntimeData workbenchRuntimeData = new WorkbenchRuntimeData();
        workbenchRuntimeData.setUserId(1);
        workbenchDataManager.updateWorkbenchRuntimeData(workbenchRuntimeData);
        
    }

	public Project getCommonTestProject() throws MiddlewareQueryException {
		if(commonTestProject==null) {
			commonTestProject = createTestProjectData();
		}
		return commonTestProject;
	}

	public User getTestUser1() {
		if(testUser1==null) {
			testUser1 = createTestUserData();
		}
		return testUser1;
	}
	
	
	
}
