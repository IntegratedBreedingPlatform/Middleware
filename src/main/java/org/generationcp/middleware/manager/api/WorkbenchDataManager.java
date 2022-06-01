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

import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Role;
import org.generationcp.middleware.pojos.workbench.RoleType;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.service.api.user.RoleSearchDto;

import java.util.List;

/**
 * This is the API used by the Workbench to retrieve Workbench project information.
 */
public interface WorkbenchDataManager {

	/**
	 * Gets the tool with the given name.
	 *
	 * @param toolName - the tool name to match
	 * @return the tool with the given name
	 */
	Tool getToolWithName(String toolName);

	/**
	 * Get the list of all installed central crop databases.
	 *
	 * @return the installed central crops
	 */
	List<CropType> getInstalledCropDatabses();

	List<CropType> getAvailableCropsForUser(int workbenchUserId);

	/**
	 * Get the crop type corresponding to the given name.
	 *
	 * @param cropName - the crop name to match
	 * @return the CropType retrieved
	 */
	CropType getCropTypeByName(String cropName);

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
	 * Returns list of roleTypes
	 *
	 * @return
	 */
	List<RoleType> getRoleTypes();

	// TODO Move role methods to RoleServiceImp

	/**
	 * Get role type
	 *
	 * @param id
	 * @return ROle Type
	 */
	RoleType getRoleType(Integer id);

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
