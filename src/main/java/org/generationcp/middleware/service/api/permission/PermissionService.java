package org.generationcp.middleware.service.api.permission;

import org.generationcp.middleware.domain.workbench.PermissionDto;

import java.util.List;
import java.util.Set;

public interface PermissionService {

	List<PermissionDto> getPermissions(final Integer userId, final String cropName, final Integer programId);

	List<PermissionDto> getPermissionLinks(final Integer userId, final String cropName, final Integer programId);

	PermissionDto getPermissionTree(Integer roleTypeId);

	List<PermissionDto> getPermissionsDtoByIds(Set<Integer> permissionIds);

}
