
package org.generationcp.middleware.operation.searcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.search.filter.BrowseStudyQueryFilter;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Season;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.util.StringUtil;

public class StudySearcherByNameStartSeasonCountry extends Searcher {

	public StudySearcherByNameStartSeasonCountry(HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
	}

	public List<DmsProject> searchByFilter(BrowseStudyQueryFilter filter, int start, int numOfRows) throws MiddlewareQueryException {
		Set<DmsProject> studies = new HashSet<DmsProject>();

		studies.addAll(this.searchByStartDate(filter.getStartDate()));
		studies.addAll(this.searchByStudyName(filter.getName()));
		studies.addAll(this.searchByCountry(filter.getCountry()));
		studies.addAll(this.searchBySeason(filter.getSeason()));

		int end = start + numOfRows;
		if (end > studies.size()) {
			end = studies.size();
		}
		if (start > studies.size()) {
			return new ArrayList<DmsProject>();
		}

		return new ArrayList<DmsProject>(studies).subList(start, end);
	}

	private List<DmsProject> searchByStartDate(Integer startDate) throws MiddlewareQueryException {
		List<DmsProject> studies = new ArrayList<DmsProject>();
		if (startDate != null) {
			studies.addAll(this.getDmsProjectDao().getStudiesByStartDate(startDate));
		}
		return studies;
	}

	private List<DmsProject> searchByStudyName(String name) throws MiddlewareQueryException {
		List<DmsProject> studies = new ArrayList<DmsProject>();
		if (!StringUtil.isEmpty(name)) {
			studies.addAll(this.getDmsProjectDao().getStudiesByName(name));
		}
		return studies;
	}

	private List<DmsProject> searchByCountry(String countryName) throws MiddlewareQueryException {
		List<DmsProject> studies = new ArrayList<DmsProject>();
		if (!StringUtil.isEmpty(countryName)) {
			List<Country> countries = this.getCountryDao().getByIsoFull(countryName);
			if (countries != null && countries.size() > 0) {
				List<Integer> countryIds = new ArrayList<Integer>();
				for (Country country : countries) {
					countryIds.add(country.getCntryid());
				}
				List<Integer> userIds = this.getUserDao().getUserIdsByCountryIds(countryIds);
				studies = this.getDmsProjectDao().getStudiesByUserIds(userIds);
			}
		}
		return studies;
	}

	public List<DmsProject> searchBySeason(Season season) throws MiddlewareQueryException {
		List<DmsProject> studies = new ArrayList<DmsProject>();
		if (season != null && season != Season.GENERAL) {
			List<Integer> factorIds = this.getSeasonalFactors();

			if (factorIds != null && factorIds.size() > 0) {
				// for each seasonal factor, get the value that matches the season parameter from its list of possible values
				for (Integer factorId : factorIds) {
					CVTerm value = this.getDiscreteValueTerm(factorId.toString(), season.getDefinition());
					if (value != null) {
						studies.addAll(this.searchStudiesByFactor(factorId, value.getCvTermId().toString()));
					}
				}

			}
		}
		return studies;
	}

	public List<DmsProject> searchStudiesByFactor(Integer factorId, String value) throws MiddlewareQueryException {
		Set<DmsProject> projects = this.searchByFactor(factorId, value);
		Set<DmsProject> studies = new HashSet<DmsProject>();

		for (DmsProject project : projects) {
			if (project.getRelatedTos() != null && project.getRelatedTos().size() > 0) {
				if (TermId.IS_STUDY.getId() == project.getRelatedTos().get(0).getTypeId()) {
					studies.add(project);

				} else if (TermId.BELONGS_TO_STUDY.getId() == project.getRelatedTos().get(0).getTypeId()) {
					studies.add(project.getRelatedTos().get(0).getObjectProject());
				}
			}

		}

		return new ArrayList<DmsProject>(studies);
	}

	public List<DmsProject> searchDatasetsByFactor(Integer factorId, String value) throws MiddlewareQueryException {
		Set<DmsProject> projects = this.searchByFactor(factorId, value);
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

	public Set<DmsProject> searchByFactor(Integer factorId, String value) throws MiddlewareQueryException {
		List<Integer> experimentIds = this.getExperimentSearcher().searchExperimentsByFactor(factorId, value);
		List<Integer> projectIds = this.getProjectIdsByExperiment(experimentIds);
		return this.getProjectsByIds(projectIds);
	}

	private List<Integer> getSeasonalFactors() throws MiddlewareQueryException {
		return this.getCvTermRelationshipDao().getSubjectIdsByTypeAndObject(TermId.HAS_PROPERTY.getId(), TermId.SEASON.getId());
	}

	private CVTerm getDiscreteValueTerm(String name, String definition) throws MiddlewareQueryException {
		CVTerm term = null;
		Integer cvId = null;
		cvId = this.getCvDao().getIdByName(name);
		term = this.getCvTermDao().getByCvIdAndDefinition(cvId, definition);
		return term;
	}

	private List<Integer> getProjectIdsByExperiment(Collection<Integer> experimentIds) throws MiddlewareQueryException {
		return this.getExperimentProjectDao().getProjectIdsByExperimentIds(experimentIds);
	}

	private Set<DmsProject> getProjectsByIds(Collection<Integer> ids) throws MiddlewareQueryException {
		Set<DmsProject> projects = new HashSet<DmsProject>();
		if (ids != null && ids.size() > 0) {
			projects.addAll(this.getDmsProjectDao().getByIds(ids));
		}
		return projects;
	}
}
