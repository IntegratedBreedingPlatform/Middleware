package org.generationcp.middleware.v2.domain.searcher;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.v2.domain.TermId;
import org.generationcp.middleware.v2.pojos.DmsProject;

public abstract class ProjectSearcher extends Searcher {

	protected ProjectSearcher(HibernateSessionProvider sessionProviderForLocal,
			HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
		// TODO Auto-generated constructor stub
	}

	/*
	  	public List<DmsProject> searchStudiesByFactor(Database database, Integer factorId, String value, int start, int numRows) throws MiddlewareQueryException {
	 
		Set<DmsProject> projects = searchByFactor(factorId, value, start, numOfRows);
		Set<DmsProject> studies = new HashSet<DmsProject>();
		
		for (DmsProject project : projects) {
			if (project.getRelatedTos() != null && project.getRelatedTos().size() > 0) {
				if (TermId.IS_STUDY.getId() == project.getRelatedTos().get(0).getTypeId()) {
					studies.add(project);
				} 
				else if (TermId.BELONGS_TO_STUDY.getId() == project.getRelatedTos().get(0).getTypeId()) {
					studies.add(project.getRelatedTos().get(0).getObjectProject());
				}
			}		
		}
	}
	
	public List<DmsProject> searchDatasetsByFactor(Integer factorId, String value) throws MiddlewareQueryException {
		Set<DmsProject> projects = searchByFactor(factorId, value);
		List<DmsProject> datasets = new ArrayList<DmsProject>();
		
		for (DmsProject project : projects) {
			if (project.getRelatedTos() != null && project.getRelatedTos().size() > 0) {
				if (TermId.BELONGS_TO_STUDY.getId() == project.getRelatedTos().get(0).getTypeId()) {
					datasets.add(project);
				}
			}
				
		}
		
		return datasets;
	}
	
	public Set<DmsProject> searchByFactor(Integer factorId, String value, int start, int numOfRows) throws MiddlewareQueryException {
		List<Integer> experimentIds = getExperimentSearcher().searchExperimentsByFactor(factorId, value);
		List<Integer> projectIds = getProjectIdsByExperiment(experimentIds);
		return getProjectsByIds(projectIds);
	}
	*/
}
