/*******************************************************************************
 * Copyright (c) 2014, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.service.api;

/**
 * This is the API for inventory management system.
 *
 */
public interface InventoryService {

	/**
	 * This method gets the maximum notation number of the existing stock IDs. For example, if there are existing stock IDs: SID1-1, SID1-2,
	 * SID2-1, SID2-2, SID2-3, SID3-1, SID3-2, and the breeder identifier is SID, this method returns 3, from SID3-1 or SID3-2. If there no
	 * existing stock IDs with matching breeder identifier, 0 is returned.
	 */
	Integer getCurrentNotationNumberForBreederIdentifier(String breederIdentifier);
}
