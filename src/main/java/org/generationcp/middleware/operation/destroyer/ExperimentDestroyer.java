
package org.generationcp.middleware.operation.destroyer;

import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;

public class ExperimentDestroyer extends Destroyer {

	public ExperimentDestroyer(HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
	}

	public void deleteExperimentsByStudy(int datasetId) throws MiddlewareQueryException {
		this.getExperimentDao().deleteExperimentsForDataset(datasetId);
	}

}
