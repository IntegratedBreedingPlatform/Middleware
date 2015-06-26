package org.generationcp.middleware.service.migrator.api;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;

public interface GermplasmMigratorService {

	/**
	 * Resets the local reference id of udflds
	 * @throws MiddlewareQueryException
	 */
	void resetLocalReferenceIdOfUserDefinedFields() throws MiddlewareQueryException;
	
	/**
	 * Resets the local reference id of germplasms
	 * @throws MiddlewareQueryException
	 */
	void resetLocalReferenceIdOfGermplasms() throws MiddlewareQueryException;
	
}
