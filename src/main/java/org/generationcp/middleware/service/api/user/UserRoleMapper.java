package org.generationcp.middleware.service.api.user;

import org.generationcp.middleware.domain.workbench.CropDto;
import org.generationcp.middleware.pojos.workbench.UserRole;

import java.util.ArrayList;
import java.util.List;

public class UserRoleMapper {

	public static UserRoleDto map (final UserRole userRole){
		final UserRoleDto userRoleDto = new UserRoleDto();
		userRoleDto.setRole(RoleMapper.map(userRole.getRole()));
		userRoleDto.setProgram(ProgramMapper.map(userRole.getWorkbenchProject()));
		userRoleDto.setCrop((userRole.getCropType() != null) ? new CropDto(userRole.getCropType()) : null);
		userRoleDto.setId(userRole.getId());
		return userRoleDto;
	}

	public static List<UserRoleDto> map(final List<UserRole> userRoleList){
		final List<UserRoleDto> userRoleDtos = new ArrayList<>();
		for (final UserRole userRole: userRoleList) {
			final UserRoleDto roleDto = map(userRole);
			userRoleDtos.add(roleDto);
		}
		return userRoleDtos;
	}
}
