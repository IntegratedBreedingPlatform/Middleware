package org.generationcp.middleware.service.impl.study;

import java.util.List;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.service.api.study.StudyDatasetService;


public class StudyDatasetServiceImpl implements StudyDatasetService {
	
	private DaoFactory daoFactory;
	

	public StudyDatasetServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public long countPhenotypesForDataset(final Integer datasetId, final List<Integer> traitIds) {
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
