
package org.generationcp.middleware.operation.destroyer;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;

public class ExperimentDestroyer extends Destroyer {

	private DaoFactory daoFactory;

	public ExperimentDestroyer(HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	public void deleteExperimentsByStudy(int datasetId) throws MiddlewareQueryException {
		this.daoFactory.getExperimentDao().deleteExperimentsForDataset(datasetId);
	}

	public void deleteTrialExperimentsOfStudy(int trialDatasetId) throws MiddlewareQueryException {
		this.daoFactory.getExperimentDao().deleteTrialExperimentsOfStudy(trialDatasetId);
	}
}
