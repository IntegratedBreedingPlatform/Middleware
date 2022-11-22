package org.generationcp.middleware.service.api.user;

import org.generationcp.middleware.pojos.workbench.Role;

import java.util.ArrayList;
import java.util.List;

public class RoleMapper {

	public static RoleDto map(final Role role) {
		final RoleDto roleDto = new RoleDto();
		roleDto.setEditable(role.getEditable());
		roleDto.setName(role.getName());
		roleDto.setDescription(role.getDescription());
		roleDto.setAssignable(role.getAssignable());
		roleDto.setId(role.getId());
		roleDto.setRoleType(new RoleTypeDto(role.getRoleType()));
		roleDto.setActive(role.getActive());
		return roleDto;
	}

	public static List<RoleDto> map(final List<Role> roleList){
		final List<RoleDto> roleDtoList = new ArrayList<>();
		for (final Role role: roleList) {
			final RoleDto roleDto = map(role);
			roleDtoList.add(roleDto);
		}
		return roleDtoList;
	}

}
