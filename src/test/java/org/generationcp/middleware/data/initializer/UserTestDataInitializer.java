package org.generationcp.middleware.data.initializer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.UserRole;

public class UserTestDataInitializer {

	public static User createUser() {
		final User user = new User();
		user.setUserid(1);
		user.setInstalid(-1);
		user.setStatus(-1);
		user.setAccess(-1);
		user.setType(-1);
		user.setName("user_test");
		user.setPassword("user_password");
		user.setPersonid(-1);
		user.setAssignDate(20120101);
		user.setCloseDate(20120101);
		return user;
	}
	
	public static User createActiveUser() {
		final User user = new User();
		user.setInstalid(-1);
		user.setStatus(0);
		user.setAccess(-1);
		user.setType(-1);
		user.setName("user_test");
		user.setPassword("user_password");
		user.setPersonid(-1);
		user.setAssignDate(20120101);
		user.setCloseDate(20120101);
		return user;
	}
	
	public static User createUserWithRole(Integer userid) {
		final User user = new User();
		user.setUserid(userid);
		user.setRoles(Arrays.asList(new UserRole(user, "Admin")));
		return user;
	}

	public static List<User> createUserList() {
		final List<User> users = new ArrayList<User>();
		users.add(UserTestDataInitializer.createUser());
		return users;
	}

	public static User createUserWithPerson(final Integer userId, final String username, final Integer personId,
			final String firstName, final String middleName) {
		final User user = new User(userId);
		user.setName(username);
		user.setPersonid(personId);
		user.setPerson(PersonTestDataInitializer.createPerson(username, userId, firstName, middleName));
		user.setStatus(0);
		return user;
	}
}
