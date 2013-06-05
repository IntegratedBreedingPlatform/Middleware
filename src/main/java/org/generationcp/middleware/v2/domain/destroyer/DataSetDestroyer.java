package org.generationcp.middleware.v2.domain.destroyer;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.v2.pojos.DmsProject;

public class DataSetDestroyer extends Destroyer {

	public DataSetDestroyer(HibernateSessionProvider sessionProviderForLocal,
			                HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}

	public void deleteDataSet(int datasetId) throws MiddlewareQueryException {
		if (this.setWorkingDatabase(datasetId)) {
			DmsProject project = this.getDmsProjectDao().getById(datasetId);
			this.getDataSetDao().delete(datasetId);
			this.getDmsProjectDao().clear();
		}
	}
}
