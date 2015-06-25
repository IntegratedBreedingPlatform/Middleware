package org.generationcp.middleware.service.migrator.api;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;

public interface GermplasmListMigratorService {

	/**
	 * Resets the local reference id of Germplasm Lists (listnms)
	 * @throws MiddlewareQueryException
	 */
	void resetLocalReferenceIdOfGermplasmLists() throws MiddlewareQueryException;
	
	/**
	 * Resets the local reference id of Germplasm List Data (listdata)
	 * @throws MiddlewareQueryException
	 */
	void resetLocalReferenceIdOfGermplasmListData() throws MiddlewareQueryException;
	
}
