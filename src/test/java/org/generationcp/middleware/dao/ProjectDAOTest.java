package org.generationcp.middleware.dao;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.WorkbenchTestDataUtil;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.workbench.CropPerson;
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

public class ProjectDAOTest  extends IntegrationTestBase {

    @Autowired
    private WorkbenchDataManager workbenchDataManager;

    @Autowired
    private WorkbenchTestDataUtil workbenchTestDataUtil;

    @Autowired
    private UserService userService;

    private ProjectDAO workbenchProjectDao;

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

        final RoleType programAdminRoleType =
                this.workbenchDataManager.getRoleType(org.generationcp.middleware.domain.workbench.RoleType.PROGRAM.getId());
        this.programAdminRole = new Role();
        this.programAdminRole.setName("Test Program Role " + new Random().nextInt());
        this.programAdminRole.setRoleType(programAdminRoleType);
        this.programAdminRole.setActive(true);
        this.workbenchDataManager.saveRole(programAdminRole);

        final org.generationcp.middleware.pojos.workbench.RoleType instanceRoleType =
                this.workbenchDataManager.getRoleType(org.generationcp.middleware.domain.workbench.RoleType.INSTANCE.getId());
        this.instanceAdminRole = new Role();
        this.instanceAdminRole.setName("Test Instance Role " + new Random().nextInt());
        this.instanceAdminRole.setRoleType(instanceRoleType);
        this.instanceAdminRole.setActive(true);
        this.workbenchDataManager.saveRole(instanceAdminRole);

        final org.generationcp.middleware.pojos.workbench.RoleType cropRoleType =
                this.workbenchDataManager.getRoleType(org.generationcp.middleware.domain.workbench.RoleType.CROP.getId());
        this.cropAdminRole = new Role();
        this.cropAdminRole.setName("Test Crop Role " + new Random().nextInt());
        this.cropAdminRole.setRoleType(cropRoleType);
        this.cropAdminRole.setActive(true);
        this.workbenchDataManager.saveRole(cropAdminRole);

        if (this.workbenchProjectDao == null) {
            this.workbenchProjectDao = new ProjectDAO();
            this.workbenchProjectDao.setSession(this.workbenchSessionProvider.getSession());
        }

        if (this.cropType == null) {
            this.cropType = this.workbenchDataManager.getCropTypeByName(CropType.CropEnum.MAIZE.name());
        }

        if (this.project1 == null) {
            this.project1 = this.workbenchTestDataUtil.createTestProjectData();
            this.project1.setUserId(1);
            this.project1.setCropType(this.cropType);
            this.workbenchDataManager.addProject(project1);
        }

        if (this.project2 == null) {
            this.project2 = this.workbenchTestDataUtil.createTestProjectData();
            this.project2.setProjectName("Test Project 2" + new Random().nextInt());
            this.project2.setUserId(1);
            this.project2.setCropType(this.cropType);
            this.workbenchDataManager.addProject(project2);
        }

        if (this.adminInstanceProgram == null) {
            this.adminInstanceProgram = this.createWorkbenchUser();
            this.adminInstanceProgram.setName("InstanceProgram " + RandomStringUtils.randomAlphanumeric(5));
            this.adminInstanceProgram.setPerson(this.createPerson());
            this.adminInstanceProgram.setActive(true);
            this.userService.addPerson(this.adminInstanceProgram.getPerson());
            this.userService.addUser(adminInstanceProgram);

            this.assignRole(this.adminInstanceProgram, Arrays.asList(this.instanceAdminRole, this.programAdminRole));
        }

        if (this.admin == null) {
            this.admin = this.createWorkbenchUser();
            this.admin.setName("Admin " + RandomStringUtils.randomAlphanumeric(5));
            this.admin.setPerson(this.createPerson());
            this.admin.setActive(true);
            this.userService.addPerson(this.admin.getPerson());
            this.userService.addUser(admin);

            this.assignRole(this.admin, Collections.singletonList( this.instanceAdminRole));
        }

        if (this.programAdmin == null) {
            this.programAdmin = this.createWorkbenchUser();
            this.programAdmin.setName("ProgramAdmin " + RandomStringUtils.randomAlphanumeric(5));
            this.programAdmin.setPerson(this.createPerson());
            this.programAdmin.setActive(true);
            this.userService.addPerson(this.programAdmin.getPerson());
            this.userService.addUser(programAdmin);
            this.assignRole(this.programAdmin, Collections.singletonList(this.programAdminRole));

        }

        if (this.cropAdmin == null) {
            this.cropAdmin = this.createWorkbenchUser();
            this.cropAdmin.setName("CropAdmin " + RandomStringUtils.randomAlphanumeric(5));
            this.cropAdmin.setPerson(this.createPerson());
            this.cropAdmin.setActive(true);
            this.userService.addPerson(this.cropAdmin.getPerson());
            this.userService.addUser(cropAdmin);

            this.assignRole(this.cropAdmin, Collections.singletonList(this.cropAdminRole));
        }

    }

    // FIXME
    @Test
    public void testGetProgramsByUserIdAdminAndProgramUser() {

        final int count = this.workbenchDataManager.getProjects().size();

        final ProgramSearchRequest programSearchRequest = new ProgramSearchRequest();
        programSearchRequest.setLoggedInUserId(this.adminInstanceProgram.getUserid());
        final Pageable pageable = new PageRequest(0, 100);
        final List<Project> projects = this.workbenchProjectDao.getProjectsByFilter(pageable, programSearchRequest);
        final Set<Project> projectSet = Sets.newHashSet(projects);

        Assert.assertEquals(count, projects.size());
        Assert.assertEquals("No Duplicates", projects.size(), projectSet.size());
    }

    // FIXME
    @Test
    public void testGetProgramsByUserIdAdminUser() {

        final int count = this.workbenchDataManager.getProjects().size();

        final ProgramSearchRequest programSearchRequest = new ProgramSearchRequest();
        programSearchRequest.setLoggedInUserId(this.admin.getUserid());
        final Pageable pageable = new PageRequest(0, 100);
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

    @Test
    public void testGetProgramsByUserIdCropUser() {
        final int count = this.workbenchDataManager.getProjectsByCrop(this.cropType).size();

        final ProgramSearchRequest programSearchRequest = new ProgramSearchRequest();
        programSearchRequest.setLoggedInUserId(this.cropAdmin.getUserid());
        final Pageable pageable = new PageRequest(0, 100);
        final List<Project> projects = this.workbenchProjectDao.getProjectsByFilter(pageable, programSearchRequest);
        final Set<Project> projectSet = Sets.newHashSet(projects);

        Assert.assertEquals(count, projects.size());
        Assert.assertEquals("No Duplicates", projects.size(), projectSet.size());
    }

    private Person createPerson() {
        final List<CropType> cropTypes = Collections.singletonList(this.cropType);
        Person person = new Person();
        person.setFirstName("user_test" + new Random().nextInt());
        person.setMiddleName("user_test");
        person.setLastName("user_test" + new Random().nextInt());
        person.setEmail(RandomStringUtils.randomAlphabetic(6) +"_user_test@sample.com");
        person.setTitle("-");
        person.setContact("-");
        person.setExtension("-");
        person.setFax("-");
        person.setInstituteId(0);
        person.setLanguage(0);
        person.setNotes("-");
        person.setPositionName("-");
        person.setPhone("-");
        person.setCrops(Sets.newHashSet(cropTypes));
        return person;
    }

    private void assignRole(final WorkbenchUser user, final List<Role> roles) {
        final CropPerson cropPerson = new CropPerson();
        cropPerson.setPerson(user.getPerson());
        cropPerson.setCropType(this.cropType);
        this.userService.saveCropPerson(cropPerson);

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
            this.workbenchDataManager.saveOrUpdateUserRole(userRole);
        }

    }

    private WorkbenchUser createWorkbenchUser() {
        final WorkbenchUser user = new WorkbenchUser();
        user.setInstalid(1);
        user.setStatus(1);
        user.setAccess(1);
        user.setType(1);
        user.setName("user_test" + new Random().nextInt());
        user.setPassword("user_password");
        user.setAssignDate(20150101);
        user.setCloseDate(20150101);
        return user;
    }
}
