package org.generationcp.middleware.v2.domain.destroyer;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;

public class DataSetDestroyer extends Destroyer {

	public DataSetDestroyer(HibernateSessionProvider sessionProviderForLocal,
			                HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}

	public void deleteDataSet(int datasetId) throws MiddlewareQueryException {
		if (this.setWorkingDatabase(datasetId)) {
			this.getDataSetDao().delete(datasetId);
			this.getDmsProjectDao().clear();
		}
	}

	public void deleteExperimentsByLocation(int datasetId, int locationId) throws MiddlewareQueryException {
		if (this.setWorkingDatabase(datasetId)) {
			this.getDataSetDao().deleteExperimentsByLocation(datasetId, locationId);
			this.getDmsProjectDao().clear();
		}
	}
}
