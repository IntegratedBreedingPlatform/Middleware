/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * <p/>
 * Generation Challenge Programme (GCP)
 * <p/>
 * <p/>
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *******************************************************************************/

package org.generationcp.middleware.api.role;

import org.generationcp.middleware.service.api.user.RoleDto;
import org.generationcp.middleware.service.api.user.RoleGeneratorInput;
import org.generationcp.middleware.service.api.user.RoleSearchDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface RoleService {

	long countRoles(RoleSearchDto roleSearchDto);

	List<RoleDto> searchRoles(RoleSearchDto roleSearchDto, Pageable pageable);

	RoleDto saveRole(RoleGeneratorInput dto);

	RoleDto updateRole(RoleGeneratorInput roleGeneratorInput);

	Optional<RoleDto> getRoleByName(String name);

	Optional<RoleDto> getRoleById(Integer id);

	Optional<RoleDto> getRoleWithUsers (Integer id);

	boolean isRoleInUse(Integer roleId);

}
