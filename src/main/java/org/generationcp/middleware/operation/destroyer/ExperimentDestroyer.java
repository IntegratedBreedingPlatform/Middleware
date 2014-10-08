package org.generationcp.middleware.operation.destroyer;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;

import java.util.List;

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
	
	public void deleteTrialExperimentsOfStudy(int trialDatasetId) throws MiddlewareQueryException {
	    requireLocalDatabaseInstance();
        getExperimentDao().deleteTrialExperimentsOfStudy(trialDatasetId);
	}
}
