package org.generationcp.middleware.service.impl.study;

import java.util.List;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.service.Service;
import org.generationcp.middleware.service.api.study.StudyDatasetService;


public class StudyDatasetServiceImpl extends Service implements StudyDatasetService {
	

	public StudyDatasetServiceImpl(final HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
	}

	@Override
	public long countPhenotypesForDataset(final Integer datasetId, final List<Integer> traitIds) {
		return this.getPhenotypeDao().countPhenotypesForDataset(datasetId, traitIds);
	}

	@Override
	public boolean datasetExists(final Integer studyId, final Integer datasetId) {
		final List<DmsProject> studyDatasets = this.getDmsProjectDao().getDatasetsByStudy(studyId);
		if (studyDatasets != null) {
			for (final DmsProject dataset : studyDatasets) {
				if (dataset.getProjectId().equals(datasetId)) {
					return true;
				}
			}
		}
		return false;	
	}

}
