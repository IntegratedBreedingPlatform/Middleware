/*******************************************************************************
 * Copyright (c) 2014, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/

package org.generationcp.middleware.service.api;

import java.util.List;

import org.generationcp.middleware.domain.inventory.InventoryDetails;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;

/**
 * This is the API for inventory management system.
 *  
 */
public interface InventoryService {


	List<InventoryDetails> getInventoryDetailsByGermplasmList(Integer listId) throws MiddlewareQueryException;
	
	List<InventoryDetails> getInventoryDetailsByGids(List<Integer> gids) throws MiddlewareQueryException; 

	List<InventoryDetails> getInventoryDetailsByStudy(Integer studyId) throws MiddlewareQueryException; 

	
}
