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

import org.generationcp.middleware.domain.gms.GermplasmListType;
import org.generationcp.middleware.domain.inventory.InventoryDetails;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.ListDataProject;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.workbench.CropType;

import java.util.List;

/**
 * This is the API for inventory management system.
 *
 */
public interface InventoryService {


	/**
	 * Checks if the study has inventory.
	 *
	 * @param studyId the study id
	 * @return boolean
	 */
	boolean hasInventoryDetails(Integer studyId);

	/**
	 * Gets the inventory details by studyId.
	 *
	 * @param studyId the study id
	 * @return the inventory details by study id
	 */
	List<InventoryDetails> getInventoryDetails(Integer studyId);

	/**
	 * Gets the inventory details by germplasm list.
	 *
	 * @param listId the list id
	 * @return the inventory details by germplasm list
	 */
	List<InventoryDetails> getInventoryDetailsByGermplasmList(Integer listId, String germplasmListType);

	/**
	 * Gets the inventory details by germplasm list.
	 *
	 * @param listId the list id
	 * @return the inventory details by germplasm list
	 */
	List<InventoryDetails> getInventoryDetailsByGermplasmList(Integer listId);

	/**
	 * This method gets the maximum notation number of the existing stock IDs. For example, if there are existing stock IDs: SID1-1, SID1-2,
	 * SID2-1, SID2-2, SID2-3, SID3-1, SID3-2, and the breeder identifier is SID, this method returns 3, from SID3-1 or SID3-2. If there no
	 * existing stock IDs with matching breeder identifier, 0 is returned.
	 */
	Integer getCurrentNotationNumberForBreederIdentifier(String breederIdentifier);

	Lot getLotByEntityTypeAndEntityIdAndLocationIdAndScaleId(String entityType, Integer entityId, Integer locationId, Integer scaleId);
}
