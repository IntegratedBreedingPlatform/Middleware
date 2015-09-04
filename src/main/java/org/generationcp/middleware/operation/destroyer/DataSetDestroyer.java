/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.operation.destroyer;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;

/**
 * Contains delete methods for the dataset.
 *
 */
public class DataSetDestroyer extends Destroyer {

	public DataSetDestroyer(HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
	}

	public void deleteDataSet(int datasetId) throws MiddlewareQueryException {
		this.getDataSetDao().delete(datasetId);
	}

	public void deleteExperimentsByLocation(int datasetId, int locationId) throws MiddlewareQueryException {
		this.getDataSetDao().deleteExperimentsByLocation(datasetId, locationId);
	}

	public void deleteExperimentsByLocationAndExperimentType(int datasetId, int locationId, int typeId) throws MiddlewareQueryException {
		this.getDataSetDao().deleteExperimentsByLocationAndType(datasetId, locationId, typeId);
	}
}
