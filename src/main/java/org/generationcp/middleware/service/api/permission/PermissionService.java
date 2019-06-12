package org.generationcp.middleware.service.api.permission;

import org.generationcp.middleware.domain.workbench.PermissionDto;

import java.util.Set;

public interface PermissionService {

	Set<PermissionDto> getSidebarLinks(final Integer userId, final String cropName, final Integer programId);

	/**
	 * Close the sessionProvider
	 */
	void close();

}
