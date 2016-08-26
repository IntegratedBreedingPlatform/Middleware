package org.generationcp.middleware.data.initializer;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.pojos.User;

public class UserTestDataInitializer {

	public User createUser() {
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

	public List<User> createUserList() {
		final List<User> users = new ArrayList<User>();
		users.add(this.createUser());
		return users;
	}
}
