package org.generationcp.middleware.data.initializer;

import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;

public class WorkbenchUserTestDataInitializer {
	public static WorkbenchUser createWorkbenchUser() {
		final WorkbenchUser user = new WorkbenchUser(1);
		final Person person = new Person();
		person.setId(1);
		user.setPerson(person);
		return user;
	}
}
