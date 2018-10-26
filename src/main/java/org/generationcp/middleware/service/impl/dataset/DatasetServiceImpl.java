package org.generationcp.middleware.service.impl.dataset;

import java.util.List;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.service.api.dataset.DatasetService;


public class DatasetServiceImpl implements DatasetService {
	
	private DaoFactory daoFactory;
	

	public DatasetServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public long countPhenotypes(final Integer datasetId, final List<Integer> traitIds) {
		return this.daoFactory.getPhenotypeDAO().countPhenotypesForDataset(datasetId, traitIds);
	}

	@Override
	public boolean datasetExists(final Integer studyId, final Integer datasetId) {
		final List<DmsProject> studyDatasets = this.daoFactory.getDmsProjectDAO().getDatasetsByStudy(studyId);
		if (studyDatasets != null) {
			for (final DmsProject dataset : studyDatasets) {
				if (dataset.getProjectId().equals(datasetId)) {
					return true;
				}
			}
		}
		return false;	
	}
	
	protected void setDaoFactory(DaoFactory daoFactory) {
		this.daoFactory = daoFactory;
	}

}
