package org.generationcp.middleware.api.role;

import org.generationcp.middleware.service.api.user.RoleTypeDto;

import java.util.List;

public interface RoleTypeService {

	/**
	 * Returns list of roleTypes
	 *
	 * @return
	 */
	List<RoleTypeDto> getRoleTypes();

	/**
	 * Get role type
	 *
	 * @param id
	 * @return ROle Type
	 */
	RoleTypeDto getRoleType(Integer id);

}
