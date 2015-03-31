package org.generationcp.middleware.operation.searcher;

import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.search.filter.BrowseStudyQueryFilter;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.Season;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.util.StringUtil;

import java.util.*;

public class StudySearcherByNameStartSeasonCountry extends Searcher {

	public StudySearcherByNameStartSeasonCountry(HibernateSessionProvider sessionProviderForLocal) { 
		super(sessionProviderForLocal);
	}

	public List<DmsProject> searchByFilter(BrowseStudyQueryFilter filter, int start, int numOfRows) throws MiddlewareQueryException {
		Set<DmsProject> studies = new HashSet<DmsProject>();
		
		studies.addAll(searchByStartDate(filter.getStartDate()));
		studies.addAll(searchByStudyName(filter.getName()));
		studies.addAll(searchByCountry(filter.getCountry()));
		studies.addAll(searchBySeason(filter.getSeason()));
		
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
			studies.addAll(getDmsProjectDao().getStudiesByStartDate(startDate));
		}
		return studies;
	}
	
	private List<DmsProject> searchByStudyName(String name) throws MiddlewareQueryException {
		List<DmsProject> studies = new ArrayList<DmsProject>();
		if (!StringUtil.isEmpty(name)) {
			studies.addAll(getDmsProjectDao().getStudiesByName(name));
		}
		return studies;
	}
	
	private List<DmsProject> searchByCountry(String countryName) throws MiddlewareQueryException {
		List<DmsProject> studies = new ArrayList<DmsProject>();
		if (!StringUtil.isEmpty(countryName)) {
			List<Country> countries = getCountryDao().getByIsoFull(countryName);
			if (countries != null && countries.size() > 0) {
				List<Integer> countryIds = new ArrayList<Integer>(); 
				for (Country country : countries) {
					countryIds.add(country.getCntryid());
				}				
				List<Integer> userIds = getUserDao().getUserIdsByCountryIds(countryIds);				
				studies = getDmsProjectDao().getStudiesByUserIds(userIds);
			}
		}
		return studies;
	}

	public List<DmsProject> searchBySeason(Season season) throws MiddlewareQueryException {
		List<DmsProject> studies = new ArrayList<DmsProject>();
		if (season != null && season != Season.GENERAL) {
			List<Integer> factorIds = getSeasonalFactors();
			
			if (factorIds != null && factorIds.size() > 0) {
				//for each seasonal factor, get the value that matches the season parameter from its list of possible values
				for (Integer factorId : factorIds) {
					CVTerm value = getDiscreteValueTerm(factorId.toString(), season.getDefinition());
					if (value != null) {
						studies.addAll(searchStudiesByFactor(factorId, value.getCvTermId().toString()));
					}
				}
				
			}
		}
		return studies;
	}


	public List<DmsProject> searchStudiesByFactor(Integer factorId, String value) throws MiddlewareQueryException {
		Set<DmsProject> projects = searchByFactor(factorId, value);
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
	
	public Set<DmsProject> searchByFactor(Integer factorId, String value) throws MiddlewareQueryException {
		List<Integer> experimentIds = getExperimentSearcher().searchExperimentsByFactor(factorId, value);
		List<Integer> projectIds = getProjectIdsByExperiment(experimentIds);
		return getProjectsByIds(projectIds);
	}
	
	private List<Integer> getSeasonalFactors() throws MiddlewareQueryException {
		return getCvTermRelationshipDao().getSubjectIdsByTypeAndObject(TermId.HAS_PROPERTY.getId(), TermId.SEASON.getId());
	}

	private CVTerm getDiscreteValueTerm(String name, String definition) throws MiddlewareQueryException {
		CVTerm term = null;
		Integer cvId = null;
		cvId = getCvDao().getIdByName(name);
		term = getCvTermDao().getByCvIdAndDefinition(cvId, definition);
		return term;
	}
	
	private List<Integer> getProjectIdsByExperiment(Collection<Integer> experimentIds) throws MiddlewareQueryException {
		return getExperimentProjectDao().getProjectIdsByExperimentIds(experimentIds);
	}
	
	private Set<DmsProject> getProjectsByIds(Collection<Integer> ids) throws MiddlewareQueryException {
		Set<DmsProject> projects = new HashSet<DmsProject>();
		if (ids != null && ids.size() > 0) {
			projects.addAll(getDmsProjectDao().getByIds(ids));
		}
		return projects;
	}
}
