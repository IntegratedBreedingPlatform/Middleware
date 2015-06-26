package org.generationcp.middleware.service.migrator.api;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;

public interface MigratorService {

	void resetLocalReferenceId(String tableName, String columnName)
			throws MiddlewareQueryException;
}
