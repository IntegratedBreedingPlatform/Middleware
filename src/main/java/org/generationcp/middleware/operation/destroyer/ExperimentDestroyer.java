package org.generationcp.middleware.operation.destroyer;

import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;

public class ExperimentDestroyer extends Destroyer {

	public ExperimentDestroyer(
			HibernateSessionProvider sessionProviderForLocal,
			HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}

	
	public void deleteExperimentsByIds(List<Integer> experimentIds) throws MiddlewareQueryException {
		requireLocalDatabaseInstance();
		getExperimentDao().deleteExperimentsByIds(experimentIds);
	}
	
	public void deleteExperimentsByStudy(int datasetId) throws MiddlewareQueryException {
	    requireLocalDatabaseInstance();
	    getExperimentDao().deleteExperimentsByStudy(datasetId);
	} 
}
