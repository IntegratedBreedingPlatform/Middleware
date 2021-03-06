package org.generationcp.middleware.data.initializer;

import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Role;
import org.generationcp.middleware.pojos.workbench.RoleType;
import org.generationcp.middleware.pojos.workbench.UserRole;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserTestDataInitializer {


	public static WorkbenchUser createWorkbenchUser() {
		final WorkbenchUser user = new WorkbenchUser();
		user.setUserid(1);
		user.setInstalid(-1);
		user.setStatus(-1);
		user.setAccess(-1);
		user.setType(-1);
		user.setName("user_test");
		final Person person = new Person();
		person.setId(-1);
		user.setPerson(person);
		user.setPassword("user_password");
		user.setAssignDate(20120101);
		user.setCloseDate(20120101);
		return user;
	}

	public static WorkbenchUser createUserWithRole(final Integer userid) {
		final WorkbenchUser user = new WorkbenchUser();
		user.setUserid(userid);
		// Role ID 1 = ADMIN
		user.setRoles(Arrays.asList(new UserRole(user, new Role(1, "Admin"))));
		return user;
	}
	
	public static List<WorkbenchUser> createWorkbenchUserList() {
		final List<WorkbenchUser> users = new ArrayList<WorkbenchUser>();
		users.add(UserTestDataInitializer.createWorkbenchUser());
		return users;
	}

	public static WorkbenchUser createUserWithPerson(final Integer userId, final String username, final Integer personId,
			final String firstName, final String middleName) {
		final WorkbenchUser user = new WorkbenchUser(userId);
		user.setName(username);
		final Person person = new Person();
		person.setId(personId);
		user.setPerson(person);
		user.setPerson(PersonTestDataInitializer.createPerson(username, userId, firstName, middleName));
		user.setStatus(0);
		
		final List<UserRole> userRoleList = new ArrayList<>();
		// Role ID 1 = ADMIN
		userRoleList.add(new UserRole(user, 1));
		user.setRoles(userRoleList);
		
		return user;
	}

	public static WorkbenchUser createUserWithProjectRole(final Integer userid, final Project project) {
		final WorkbenchUser user = new WorkbenchUser();
		user.setUserid(userid);
		// Role ID 1 = ADMIN
		final Role role = new Role(1, "Admin");
		final RoleType roleType = new RoleType(org.generationcp.middleware.domain.workbench.RoleType.PROGRAM.name());
		roleType.setId(org.generationcp.middleware.domain.workbench.RoleType.PROGRAM.getId());
		role.setRoleType(roleType);
		final UserRole userRole = new UserRole();
		userRole.setUser(user);
		userRole.setCropType(project.getCropType());
		userRole.setWorkbenchProject(project);
		userRole.setRole(role);
		user.setRoles(Arrays.asList(userRole));
		return user;
	}

	public static void addUserRole(final WorkbenchUser workbenchUser, final Integer roleId, final String roleName, final Project project) {
		ArrayList<UserRole> userRoles = new ArrayList<>();
		if(workbenchUser.getRoles()!=null && !workbenchUser.getRoles().isEmpty()){
			userRoles = (ArrayList<UserRole>) workbenchUser.getRoles();
		}
		final RoleType roleType = new RoleType(org.generationcp.middleware.domain.workbench.RoleType.PROGRAM.name());
		roleType.setId(org.generationcp.middleware.domain.workbench.RoleType.PROGRAM.getId());
		final Role role = new Role(roleId, roleName);
		role.setRoleType(roleType);

		final UserRole userRole = new UserRole();
		userRole.setUser(workbenchUser);
		userRole.setWorkbenchProject(project);
		userRole.setRole(role);
		userRole.setCropType(project.getCropType());
		userRoles.add(userRole);
		workbenchUser.setRoles(userRoles);
	}
}
