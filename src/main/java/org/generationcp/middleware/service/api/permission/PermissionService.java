package org.generationcp.middleware.service.api.permission;

import org.generationcp.middleware.domain.workbench.PermissionDto;

import java.util.List;
import java.util.Set;

public interface PermissionService {

	List<PermissionDto> getPermissions(Integer userId, String cropName, Integer programId, Boolean skipProgramValidation);

	List<PermissionDto> getPermissions(Integer userId, String cropName, Integer programId);

	List<PermissionDto> getPermissionLinks(Integer userId, String cropName, Integer programId);

	PermissionDto getPermissionTree(Integer roleTypeId);

	List<PermissionDto> getPermissionsDtoByIds(Set<Integer> permissionIds);

}
