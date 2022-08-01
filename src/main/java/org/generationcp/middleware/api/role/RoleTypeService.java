package org.generationcp.middleware.api.role;

import org.generationcp.middleware.pojos.workbench.RoleType;

import java.util.List;

public interface RoleTypeService {

	/**
	 * Returns list of roleTypes
	 *
	 * @return
	 */
	List<RoleType> getRoleTypes();

	/**
	 * Get role type
	 *
	 * @param id
	 * @return ROle Type
	 */
	RoleType getRoleType(Integer id);

}
