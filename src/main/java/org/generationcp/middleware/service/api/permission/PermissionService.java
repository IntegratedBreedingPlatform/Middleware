package org.generationcp.middleware.service.api.permission;

import org.generationcp.middleware.domain.workbench.PermissionDto;
import org.generationcp.middleware.pojos.workbench.Permission;

import java.util.List;
import java.util.Set;

public interface PermissionService {

	List<PermissionDto> getPermissions(final Integer userId, final String cropName, final Integer programId);

	List<PermissionDto> getPermissionLinks(final Integer userId, final String cropName, final Integer programId);

	Permission getPermissionById(Integer permissionId);

	List<Permission> getAllPermissions();

	List<Permission> getPermissionsByIds(Set<Integer> permissionIds);

	/**
	 * Close the sessionProvider
	 */
	void close();

}
