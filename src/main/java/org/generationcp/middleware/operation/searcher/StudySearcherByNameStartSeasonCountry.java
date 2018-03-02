
package org.generationcp.middleware.operation.searcher;

import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.dms.DmsProject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StudySearcherByNameStartSeasonCountry extends Searcher {

	public StudySearcherByNameStartSeasonCountry(HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
	}

	public List<DmsProject> searchStudiesByFactor(Integer factorId, String value) throws MiddlewareQueryException {
		Set<DmsProject> projects = this.searchByFactor(factorId, value);
		Set<DmsProject> studies = new HashSet<DmsProject>();

		for (DmsProject project : projects) {
			if (project.getRelatedTos() != null && !project.getRelatedTos().isEmpty()) {
				if (TermId.IS_STUDY.getId() == project.getRelatedTos().get(0).getTypeId()) {
					studies.add(project);

				} else if (TermId.BELONGS_TO_STUDY.getId() == project.getRelatedTos().get(0).getTypeId()) {
					studies.add(project.getRelatedTos().get(0).getObjectProject());
				}
			}

		}

		return new ArrayList<DmsProject>(studies);
	}

	public Set<DmsProject> searchByFactor(Integer factorId, String value) throws MiddlewareQueryException {
		List<Integer> experimentIds = this.getExperimentSearcher().searchExperimentsByFactor(factorId, value);
		List<Integer> projectIds = this.getProjectIdsByExperiment(experimentIds);
		return this.getProjectsByIds(projectIds);
	}

	private List<Integer> getProjectIdsByExperiment(Collection<Integer> experimentIds) throws MiddlewareQueryException {
		return this.getExperimentProjectDao().getProjectIdsByExperimentIds(experimentIds);
	}

	private Set<DmsProject> getProjectsByIds(Collection<Integer> ids) throws MiddlewareQueryException {
		Set<DmsProject> projects = new HashSet<DmsProject>();
		if (ids != null && !ids.isEmpty()) {
			projects.addAll(this.getDmsProjectDao().getByIds(ids));
		}
		return projects;
	}
}
