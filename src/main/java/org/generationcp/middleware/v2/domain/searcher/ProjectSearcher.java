package org.generationcp.middleware.v2.domain.searcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.generationcp.commons.util.StringUtil;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.Season;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.v2.domain.TermId;
import org.generationcp.middleware.v2.domain.StudyQueryFilter;
import org.generationcp.middleware.v2.pojos.CVTerm;
import org.generationcp.middleware.v2.pojos.DmsProject;

public class ProjectSearcher extends Searcher {

	public ProjectSearcher(
			HibernateSessionProvider sessionProviderForLocal,
			HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}
	
	
	public List<DmsProject> searchByFilter(StudyQueryFilter filter) throws MiddlewareQueryException {
		Set<DmsProject> studies = new HashSet<DmsProject>();
		
		studies.addAll(searchByStartDate(filter.getStartDate()));
		studies.addAll(searchByStudyName(filter.getName()));
		studies.addAll(searchByCountry(filter.getCountry()));
		studies.addAll(searchBySeason(filter.getSeason()));
		
		int start = filter.getStart();
		int end = filter.getStart() + filter.getNumOfRows();
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
			setWorkingDatabase(Database.CENTRAL);
			studies.addAll(getDmsProjectDao().getStudiesByStartDate(startDate));
			setWorkingDatabase(Database.LOCAL);
			studies.addAll(getDmsProjectDao().getStudiesByStartDate(startDate));
		}
		return studies;
	}
	
	private List<DmsProject> searchByStudyName(String name) throws MiddlewareQueryException {
		List<DmsProject> studies = new ArrayList<DmsProject>();
		if (!StringUtil.isEmpty(name)) {
			setWorkingDatabase(Database.CENTRAL);
			studies.addAll(getDmsProjectDao().getStudiesByName(name));
			setWorkingDatabase(Database.LOCAL);
			studies.addAll(getDmsProjectDao().getStudiesByName(name));
		}
		return studies;
	}
	
	private List<DmsProject> searchByCountry(String countryName) throws MiddlewareQueryException {
		List<DmsProject> studies = new ArrayList<DmsProject>();
		if (!StringUtil.isEmpty(countryName)) {
			setWorkingDatabase(Database.CENTRAL);
			List<Country> countries = getCountryDao().getByIsoFull(countryName);
			setWorkingDatabase(Database.LOCAL);
			countries.addAll(getCountryDao().getByIsoFull(countryName));
			
			if (countries != null && countries.size() > 0) {
				List<Integer> countryIds = new ArrayList<Integer>(); 
				for (Country country : countries) {
					countryIds.add(country.getCntryid());
				}
				
				setWorkingDatabase(Database.CENTRAL);
				List<Integer> userIds = getUserDao().getUserIdsByCountryIds(countryIds);
				setWorkingDatabase(Database.LOCAL);
				userIds.addAll(getUserDao().getUserIdsByCountryIds(countryIds));
				
				setWorkingDatabase(Database.CENTRAL);
				studies = getDmsProjectDao().getStudiesByUserIds(userIds);
				setWorkingDatabase(Database.LOCAL);
				studies.addAll(getDmsProjectDao().getStudiesByUserIds(userIds));
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
				if (TermId.IS_STUDY.getId().equals(project.getRelatedTos().get(0).getTypeId())) {
					studies.add(project);
				
				} else if (TermId.BELONGS_TO_STUDY.getId().equals(project.getRelatedTos().get(0).getTypeId())) {
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
				if (TermId.BELONGS_TO_STUDY.getId().equals(project.getRelatedTos().get(0).getTypeId())) {
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
		setWorkingDatabase(Database.CENTRAL);
		List<Integer> factors = getCvTermRelationshipDao().getSubjectIdsByTypeAndObject(TermId.HAS_PROPERTY.getId(), TermId.SEASON.getId());
		setWorkingDatabase(Database.LOCAL);
		factors.addAll(getCvTermRelationshipDao().getSubjectIdsByTypeAndObject(TermId.HAS_PROPERTY.getId(), TermId.SEASON.getId()));
		
		return factors;
	}

	private CVTerm getDiscreteValueTerm(String name, String definition) throws MiddlewareQueryException {
		CVTerm term = null;
		Integer cvId = null;
		if (setWorkingDatabase(Database.CENTRAL)) {
			cvId = getCvDao().getIdByName(name);
		}
		if (cvId == null && setWorkingDatabase(Database.LOCAL)) {
			cvId = getCvDao().getIdByName(name);
		}
		
		if (setWorkingDatabase(Database.CENTRAL)) {
			term = getCvTermDao().getByCvIdAndDefinition(cvId, definition);
		}
		if (term == null && setWorkingDatabase(Database.CENTRAL)) {
			term = getCvTermDao().getByCvIdAndDefinition(cvId, definition);
		}
		return term;
	}
	
	private List<Integer> getProjectIdsByExperiment(Collection<Integer> experimentIds) throws MiddlewareQueryException {
		Set<Integer> projectIds = new HashSet<Integer>();
		setWorkingDatabase(Database.CENTRAL);
		projectIds.addAll(getExperimentProjectDao().getProjectIdsByExperimentIds(experimentIds));
		setWorkingDatabase(Database.LOCAL);
		projectIds.addAll(getExperimentProjectDao().getProjectIdsByExperimentIds(experimentIds));

		return new ArrayList<Integer>(projectIds);
	}
	
	private Set<DmsProject> getProjectsByIds(Collection<Integer> ids) throws MiddlewareQueryException {
		Set<DmsProject> projects = new HashSet<DmsProject>();
		if (ids != null && ids.size() > 0) {
			List<Integer> positiveIds = new ArrayList<Integer>();
			List<Integer> negativeIds = new ArrayList<Integer>();
			
			for (Integer id : ids) {
				if (id > 0) {
					positiveIds.add(id);
				} else {
					negativeIds.add(id);
				}
			}
			
			if (positiveIds.size() > 0) {
				if (setWorkingDatabase(Database.CENTRAL)) {
					projects.addAll(getDmsProjectDao().getByIds(positiveIds));
				}
			}
			if (negativeIds.size() > 0) {
				if (setWorkingDatabase(Database.LOCAL)) {
					projects.addAll(getDmsProjectDao().getByIds(negativeIds));
				}
			}
		}
		
		return projects;
	}
}
