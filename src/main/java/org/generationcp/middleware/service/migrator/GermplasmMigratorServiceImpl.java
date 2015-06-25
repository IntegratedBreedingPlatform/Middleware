package org.generationcp.middleware.service.migrator;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.service.migrator.api.GermplasmMigratorService;

public class GermplasmMigratorServiceImpl extends MigratorServiceImpl implements
		GermplasmMigratorService {

	public GermplasmMigratorServiceImpl(HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
	}

	public GermplasmMigratorServiceImpl(
			HibernateSessionProvider sessionProvider, String localDatabaseName) {
		super(sessionProvider, localDatabaseName);
	}

	@Override
	public void resetLocalReferenceIdOfUserDefinedFields()
			throws MiddlewareQueryException {
		super.resetLocalReferenceId("udflds", "lfldno");
	}

	@Override
	public void resetLocalReferenceIdOfGermplasms()
			throws MiddlewareQueryException {
		super.resetLocalReferenceId("germplsm", "lgid");
	}
}
