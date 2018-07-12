package org.generationcp.middleware.data.initializer;

import org.generationcp.middleware.pojos.workbench.WorkbenchUser;

public class WorkbenchUserTestDataInitializer {
	public static WorkbenchUser createWorkbenchUser() {
		final WorkbenchUser user = new WorkbenchUser(1);
		user.setPersonid(1);
		return user;
	}
}
