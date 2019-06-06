package org.generationcp.middleware.service.api.user;

import org.generationcp.middleware.pojos.workbench.WorkbenchUser;

public interface UserService {

	WorkbenchUser getUserWithAuthorities(final String userName, final String crop, final String program);
}
