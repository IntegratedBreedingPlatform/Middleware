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

import java.util.List;

import org.generationcp.middleware.domain.gms.GermplasmListType;
import org.generationcp.middleware.domain.inventory.InventoryDetails;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.ListDataProject;
import org.generationcp.middleware.pojos.ims.Lot;

/**
 * This is the API for inventory management system.
 *
 */
public interface InventoryService {

	/**
	 * Gets the inventory details by germplasm list.
	 *
	 * @param listId the list id
	 * @return the inventory details by germplasm list
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<InventoryDetails> getInventoryDetailsByGermplasmList(Integer listId, String germplasmListType) throws MiddlewareQueryException;

	/**
	 * Gets the inventory details by germplasm list.
	 *
	 * @param listId the list id
	 * @return the inventory details by germplasm list
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<InventoryDetails> getInventoryDetailsByGermplasmList(Integer listId) throws MiddlewareQueryException;

	/**
	 * This method gets the maximum notation number of the existing stock IDs. For example, if there are existing stock IDs: SID1-1, SID1-2,
	 * SID2-1, SID2-2, SID2-3, SID3-1, SID3-2, and the breeder identifier is SID, this method returns 3, from SID3-1 or SID3-2. If there no
	 * existing stock IDs with matching breeder identifier, 0 is returned.
	 */
	Integer getCurrentNotationNumberForBreederIdentifier(String breederIdentifier) throws MiddlewareQueryException;

	void addLotAndTransaction(InventoryDetails details, GermplasmListData listData, ListDataProject listDataProject)
			throws MiddlewareQueryException;

	List<InventoryDetails> getInventoryListByListDataProjectListId(Integer listDataProjectListId) throws MiddlewareQueryException;

	List<InventoryDetails> getSummedInventoryListByListDataProjectListId(Integer listDataProjectListId, GermplasmListType type)
			throws MiddlewareQueryException;

	boolean stockHasCompletedBulking(Integer listId) throws MiddlewareQueryException;

	Lot getLotByEntityTypeAndEntityIdAndLocationIdAndScaleId(String entityType, Integer entityId, Integer locationId, Integer scaleId);
}
