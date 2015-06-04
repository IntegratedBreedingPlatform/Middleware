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
	 * Gets the inventory details by gids.
	 *
	 * @param gids the gids
	 * @return the inventory details by gids
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<InventoryDetails> getInventoryDetailsByGids(List<Integer> gids) throws MiddlewareQueryException;

	/**
	 * Gets the inventory details by study. Inventory details to retrieve are based on nd_experiment_id of the study matching
	 * ims_transaction.source_rec_id
	 *
	 *
	 *
	 * @param studyId the study id
	 * @return the inventory details by study
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<InventoryDetails> getInventoryDetailsByStudy(Integer studyId) throws MiddlewareQueryException;

	Integer getCurrentNotationNumberForBreederIdentifier(String breederIdentifier) throws MiddlewareQueryException;

	void addLotAndTransaction(InventoryDetails details, GermplasmListData listData, ListDataProject listDataProject)
			throws MiddlewareQueryException;

	List<InventoryDetails> getInventoryListByListDataProjectListId(Integer listDataProjectListId, GermplasmListType type)
			throws MiddlewareQueryException;

	List<InventoryDetails> getSummedInventoryListByListDataProjectListId(Integer listDataProjectListId, GermplasmListType type)
			throws MiddlewareQueryException;

	boolean stockHasCompletedBulking(Integer listId) throws MiddlewareQueryException;
}
