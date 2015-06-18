package org.generationcp.middleware.service.migrator;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.service.migrator.api.GermplasmListMigratorService;

public class GermplasmListMigratorServiceImpl extends MigratorServiceImpl
		implements GermplasmListMigratorService {

	public GermplasmListMigratorServiceImpl(
			HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
	}

	public GermplasmListMigratorServiceImpl(
			HibernateSessionProvider sessionProvider, String localDatabaseName) {
		super(sessionProvider, localDatabaseName);
	}

	@Override
	public void resetLocalReferenceIdOfGermplasmLists()
			throws MiddlewareQueryException {
		super.resetLocalReferenceId("listnms", "listRef");
	}

	@Override
	public void resetLocalReferenceIdOfGermplasmListData()
			throws MiddlewareQueryException {
		super.resetLocalReferenceId("listdata", "llrecId");
	}

}
