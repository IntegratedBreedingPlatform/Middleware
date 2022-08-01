/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * <p/>
 * Generation Challenge Programme (GCP)
 * <p/>
 * <p/>
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *******************************************************************************/

package org.generationcp.middleware.manager.api;

import org.generationcp.middleware.pojos.workbench.Role;
import org.generationcp.middleware.service.api.user.RoleSearchDto;

import java.util.List;

public interface WorkbenchDataManager {

	/**
	 * Close the sessionProvider
	 */
	void close();

	/**
	 * Returns list of roles filtered according to roleSearchDto
	 *
	 * @return
	 */
	List<Role> getRoles(RoleSearchDto roleSearchDto);

	/**
	 * Save role
	 *
	 * @param role
	 * @return Role
	 */
	Role saveRole(Role role);

	/**
	 * Get role by name
	 *
	 * @param name
	 * @return Role
	 */
	Role getRoleByName(String name);

	/**
	 * Get role by id
	 *
	 * @param id
	 * @return Role
	 */
	Role getRoleById(Integer id);

}
