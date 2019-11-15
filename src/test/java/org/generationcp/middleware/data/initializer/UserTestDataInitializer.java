package org.generationcp.middleware.data.initializer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.workbench.Role;
import org.generationcp.middleware.pojos.workbench.UserRole;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;

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
}
