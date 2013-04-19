package org.generationcp.middleware.v2.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.generationcp.commons.util.StringUtil;
import org.generationcp.middleware.dao.CountryDAO;
import org.generationcp.middleware.dao.UserDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DataManager;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.Season;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.v2.dao.CVDao;
import org.generationcp.middleware.v2.dao.CVTermDao;
import org.generationcp.middleware.v2.dao.CVTermRelationshipDao;
import org.generationcp.middleware.v2.dao.DmsProjectDao;
import org.generationcp.middleware.v2.dao.ExperimentDao;
import org.generationcp.middleware.v2.dao.ExperimentProjectDao;
import org.generationcp.middleware.v2.dao.ExperimentPropertyDao;
import org.generationcp.middleware.v2.dao.ExperimentStockDao;
import org.generationcp.middleware.v2.dao.GeolocationPropertyDao;
import org.generationcp.middleware.v2.dao.StockPropertyDao;
import org.generationcp.middleware.v2.factory.FactorDetailsFactory;
import org.generationcp.middleware.v2.factory.ObservationDetailsFactory;
import org.generationcp.middleware.v2.factory.StudyFactory;
import org.generationcp.middleware.v2.factory.VariableDetailsFactory;
import org.generationcp.middleware.v2.manager.api.StudyDataManager;
import org.generationcp.middleware.v2.pojos.AbstractNode;
import org.generationcp.middleware.v2.pojos.CVTerm;
import org.generationcp.middleware.v2.pojos.CVTermId;
import org.generationcp.middleware.v2.pojos.CVTermRelationship;
import org.generationcp.middleware.v2.pojos.DatasetNode;
import org.generationcp.middleware.v2.pojos.DmsProject;
import org.generationcp.middleware.v2.pojos.FactorDetails;
import org.generationcp.middleware.v2.pojos.FolderNode;
import org.generationcp.middleware.v2.pojos.ObservationDetails;
import org.generationcp.middleware.v2.pojos.StudyDetails;
import org.generationcp.middleware.v2.pojos.StudyNode;
import org.generationcp.middleware.v2.pojos.StudyQueryFilter;
import org.generationcp.middleware.v2.pojos.VariableDetails;
import org.generationcp.middleware.v2.util.CVTermRelationshipUtil;
import org.generationcp.middleware.v2.util.ProjectPropertyUtil;
import org.hibernate.Session;

public class StudyDataManagerImpl extends DataManager implements StudyDataManager {

	private DmsProjectDao dmsProjectDao;
	
	private CVTermRelationshipDao cvTermRelationshipDao;
	
	private CVTermDao cvTermDao;

	private UserDAO userDao;
	
	private CountryDAO countryDao;
	
	private CVDao cvDao;
	
	private ExperimentDao experimentDao;
	
	private ExperimentProjectDao experimentProjectDao;
	
	private ExperimentStockDao experimentStockDao;
	
	private GeolocationPropertyDao geolocationPropertyDao;
	
	private StockPropertyDao stockPropertyDao;
	
	private ExperimentPropertyDao experimentPropertyDao;
	
	public StudyDataManagerImpl() { 		
	}

	public StudyDataManagerImpl(HibernateSessionProvider sessionProviderForLocal,
			                    HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}

	public StudyDataManagerImpl(Session sessionForLocal, Session sessionForCentral) {
		super(sessionForLocal, sessionForCentral);
	}

	private DmsProjectDao getDmsProjectDao() {
		if (dmsProjectDao == null) {
			dmsProjectDao = new DmsProjectDao();
		}
		dmsProjectDao.setSession(getActiveSession());
		return dmsProjectDao;
	}
	
	private CVTermRelationshipDao getCVTermRelationshipDao() {
		if (cvTermRelationshipDao == null) {
			cvTermRelationshipDao = new CVTermRelationshipDao();
		}
		cvTermRelationshipDao.setSession(getActiveSession());
		return cvTermRelationshipDao;
	}
	
	private CVTermDao getCVTermDao() {
		if (cvTermDao == null) {
			cvTermDao = new CVTermDao();
		}
		cvTermDao.setSession(getActiveSession());
		return cvTermDao;
	}
	
	private UserDAO getUserDao() {
		if (userDao == null) {
			userDao = new UserDAO();
		}
		userDao.setSession(getActiveSession());
		return userDao;
	}
	
	private CountryDAO getCountryDao() {
		if (countryDao == null) {
			countryDao = new CountryDAO();
		}
		countryDao.setSession(getActiveSession());
		return countryDao;
	}
	
	private CVDao getCVDao() {
		if (cvDao == null) {
			cvDao = new CVDao();
		}
		cvDao.setSession(getActiveSession());
		return cvDao;
	}
	
	private ExperimentDao getExperimentDao() {
		if (experimentDao == null) {
			experimentDao = new ExperimentDao();
		}
		experimentDao.setSession(getActiveSession());
		return experimentDao;
	}
	
	private ExperimentProjectDao getExperimentProjectDao() {
		if (experimentProjectDao == null) {
			experimentProjectDao = new ExperimentProjectDao();
		}
		experimentProjectDao.setSession(getActiveSession());
		return experimentProjectDao;
	}
	
	private ExperimentStockDao getExperimentStockDao() {
		if (experimentStockDao == null) {
			experimentStockDao = new ExperimentStockDao();
		}
		experimentStockDao.setSession(getActiveSession());
		return experimentStockDao;
	}
	
	private GeolocationPropertyDao getGeolocationPropertyDao() {
		if (geolocationPropertyDao == null) {
			geolocationPropertyDao = new GeolocationPropertyDao();
		}
		geolocationPropertyDao.setSession(getActiveSession());
		return geolocationPropertyDao;
	}

	private StockPropertyDao getStockPropertyDao() {
		if (stockPropertyDao == null) {
			stockPropertyDao = new StockPropertyDao();
		}
		stockPropertyDao.setSession(getActiveSession());
		return stockPropertyDao;
	}

	private ExperimentPropertyDao getExperimentPropertyDao() {
		if (experimentPropertyDao == null) {
			experimentPropertyDao = new ExperimentPropertyDao();
		}
		experimentPropertyDao.setSession(getActiveSession());
		return experimentPropertyDao;
	}

	
	private StudyFactory getStudyFactory() {
		return StudyFactory.getInstance();
	}
	
	private FactorDetailsFactory getFactorDetailsFactory() {
		return FactorDetailsFactory.getInstance();
	}
	
	private ObservationDetailsFactory getObservationDetailsFactory() {
		return ObservationDetailsFactory.getInstance();
	}

	@Override
	public StudyDetails getStudyDetails(Integer studyId) throws MiddlewareQueryException {
		if (setWorkingDatabase(studyId)) {
			DmsProject project = getDmsProjectDao().getById(studyId);
			if (project != null) {
				return getStudyFactory().createStudyDetails(project);
			}
		}
		return null;
	}
	
	@Override
	public List<FolderNode> getRootFolders(Database instance) throws MiddlewareQueryException{
		if (setWorkingDatabase(instance, getDmsProjectDao())){
			return getDmsProjectDao().getRootFolders();
		}
		return null;
	}
	
	@Override
	public List<AbstractNode> getChildrenOfFolder(Integer folderId, Database instance) throws MiddlewareQueryException{
		if (setWorkingDatabase(instance, getDmsProjectDao())){
			return getDmsProjectDao().getChildrenOfFolder(folderId);
		}
		return null;
	}
	
	@Override
	public List<DatasetNode> getDatasetNodesByStudyId(Integer studyId, Database instance) throws MiddlewareQueryException{
		if (setWorkingDatabase(instance, getDmsProjectDao())){
			return getDmsProjectDao().getDatasetNodesByStudyId(studyId);
		}
		return null;
	}


	@Override
	public List<FactorDetails> getFactorDetails(Integer studyId) throws MiddlewareQueryException {
		return getDetails(studyId, getFactorDetailsFactory());
	}

	@Override
	public List<ObservationDetails> getObservationDetails(Integer studyId) throws MiddlewareQueryException {
		return getDetails(studyId, getObservationDetailsFactory());
	}

	@Override
	public List<StudyNode> searchStudies(StudyQueryFilter filter) throws MiddlewareQueryException {
		Set<StudyNode> studies = new HashSet<StudyNode>();
		
		studies.addAll(getByStartDate(filter.getStartDate()));
		studies.addAll(getByStudyName(filter.getName()));
		studies.addAll(getByCountry(filter.getCountry()));
		studies.addAll(getBySeason(filter.getSeason()));
		
		return new ArrayList<StudyNode>(studies);

	}
	
	private <T extends VariableDetails> List<T> getDetails(Integer studyId, VariableDetailsFactory<T> factory) throws MiddlewareQueryException {
		List<T> factors = new ArrayList<T>();
		
		if (setWorkingDatabase(studyId)) {
			DmsProject project = getDmsProjectDao().getById(studyId);
			
			//get all datasets in a project
			
			
			Set<Integer> varIds = ProjectPropertyUtil.extractStandardVariableIds(project.getProperties());
			List<CVTermRelationship> relationships = getRelationshipsFromLocalAndCentral(varIds);
			
			Set<Integer> localTermIds = CVTermRelationshipUtil.extractLocalObjectTermIds(relationships);
			Set<Integer> centralTermIds = CVTermRelationshipUtil.extractCentralObjectTermIds(relationships);
			List<CVTerm> terms = getTermsFromLocalAndCentral(localTermIds, centralTermIds);
			
			factors = factory.createDetails(project, relationships, terms);
		}
		
		return factors;	
	}

	@SuppressWarnings("unchecked")
	private List<CVTermRelationship> getRelationshipsFromLocalAndCentral(Collection<Integer> varIds)
	throws MiddlewareQueryException {

		return getAllFromCentralAndLocalByMethod(
						getCVTermRelationshipDao(), "getBySubjectIds", 
						new Object[] {varIds}, new Class[] {Collection.class});
	}
	
	@SuppressWarnings("unchecked")
	private List<CVTerm> getTermsFromLocalAndCentral(Collection<Integer> localTermIds, Collection<Integer> centralTermIds)
	throws MiddlewareQueryException {
		
		List<CVTerm> terms = 
				getFromInstanceByMethod(
					getCVTermDao(), 
					Database.LOCAL, 
					"getByIds", 
					new Object[] {localTermIds}, 
					new Class[] {Collection.class});
	
		terms.addAll( 
				getFromInstanceByMethod(
					getCVTermDao(), 
					Database.CENTRAL, 
					"getByIds", 
					new Object[] {centralTermIds}, 
					new Class[] {Collection.class}) );
		
		return terms;
	}

	@SuppressWarnings("unchecked")
	private List<StudyNode> getByStartDate(Integer startDate) throws MiddlewareQueryException {
		if (startDate != null) {
			return getAllFromCentralAndLocalByMethod(
						getDmsProjectDao(), 
						"getStudiesByStartDate", 
						new Object[] {startDate}, 
						new Class[] {Integer.class});
		}
		return new ArrayList<StudyNode>();
	}
	
	@SuppressWarnings("unchecked")
	private List<StudyNode> getByStudyName(String name) throws MiddlewareQueryException {
		if (!StringUtil.isEmpty(name)) {
			return getAllFromCentralAndLocalByMethod(
						getDmsProjectDao(),
						"getStudiesByName",
						new Object[] {name},
						new Class[] {String.class});
		}
		return new ArrayList<StudyNode>();
	}
	
	@SuppressWarnings("unchecked")
	private List<StudyNode> getByCountry(String countryName) throws MiddlewareQueryException {
		if (!StringUtil.isEmpty(countryName)) {
			List<Country> countries = getAllFromCentralAndLocalByMethod(
											getCountryDao(), "getByIsoFull", 
											new Object[] {countryName}, 
											new Class[] {String.class});
			
			if (countries != null && countries.size() > 0) {
				List<Integer> countryIds = new ArrayList<Integer>(); 
				for (Country country : countries) {
					countryIds.add(country.getCntryid());
				}
				List<Integer> userIds = getAllFromCentralAndLocalByMethod(
											getUserDao(), "getUserIdsByCountryIds", 
											new Object[] {countryIds}, 
											new Class[] {Collection.class});
				
				return getAllFromCentralAndLocalByMethod(
						getDmsProjectDao(), 
						"getStudiesByUserIds",
						new Object[] {userIds}, 
						new Class[] {Collection.class});
			}
		}
		return new ArrayList<StudyNode>();
	}
	
	private List<StudyNode> getBySeason(Season season) throws MiddlewareQueryException {
		if (season != null && season != Season.GENERAL) {
			List<Object[]> pairs = new ArrayList<Object[]>();
			
			List<Integer> factorIds = getSeasonalFactors();
			
			if (factorIds != null && factorIds.size() > 0) {
			
				//for each factor, find the discrete value for the season
				for (Integer factorId : factorIds) {
					Integer cvId = getCvIdByName(factorId.toString());
					CVTerm value = getCvTermByCvAndDefinition(cvId, season.getDefinition()); 
					if (value != null && !"".equals(value)) {
						//pairs of factorId and value of the given season
						pairs.add(new Object[] {factorId, value.getCvTermId().toString()});
					}
				}
			
				Set<Integer> experimentIds = new HashSet<Integer>();
				
				for (Object[] pair : pairs) {
					Integer determinant = getDeterminantForWhichTableToQuery((Integer) pair[0]);
					
					if (CVTermId.TRIAL_ENVIRONMENT_INFO.getId().equals(determinant)) {
						experimentIds.addAll(findExperimentsByGeolocationFactorValue(pair));
						
					} else if (CVTermId.TRIAL_DESIGN_INFO.getId().equals(determinant)) {
						experimentIds.addAll(findExperimentsByExperimentFactorValue(pair));
						
					} else if (CVTermId.GERMPLASM_ENTRY.getId().equals(determinant)) {
						experimentIds.addAll(findExperimentsByStockFactorValue(pair));
					}
				}
				
				List<Integer> projectIds = getProjectIdsByExperiment(experimentIds);
				
				return getStudiesByProjectIds(projectIds);
			}
		}
		return new ArrayList<StudyNode>();
	}
	
	@SuppressWarnings("unchecked")
	private List<Integer> getSeasonalFactors() throws MiddlewareQueryException {
		return getAllFromCentralAndLocalByMethod(
				getCVTermRelationshipDao(), "getSubjectIdsByTypeAndObject", 
				new Object[] {CVTermId.HAS_PROPERTY.getId(), CVTermId.SEASON.getId()}, 
				new Class[] {Integer.class, Integer.class});
	}
	
	private Integer getCvIdByName(String name) throws MiddlewareQueryException {
		Integer cvId = null;
		if (setWorkingDatabase(Database.CENTRAL)) {
			cvId = getCVDao().getIdByName(name);
		}
		if (cvId == null && setWorkingDatabase(Database.LOCAL)) {
			cvId = getCVDao().getIdByName(name);
		}
		
		return cvId;
	}
	
	private CVTerm getCvTermByCvAndDefinition(Integer cvId, String definition) throws MiddlewareQueryException {
		CVTerm term = null;
		if (setWorkingDatabase(Database.CENTRAL)) {
			term = getCVTermDao().getByCvIdAndDefinition(cvId, definition);
		}
		if (term == null && setWorkingDatabase(Database.CENTRAL)) {
			term = getCVTermDao().getByCvIdAndDefinition(cvId, definition);
		}
		
		return term;
	}

	@SuppressWarnings("unchecked")
	private List<Integer> findExperimentsByGeolocationFactorValue(Object[] pair) throws MiddlewareQueryException {
		Set<Integer> geolocationIds = new HashSet<Integer>();
		geolocationIds.addAll(getAllFromCentralAndLocalByMethod(
				getGeolocationPropertyDao(), "getGeolocationIdsByPropertyTypeAndValue", 
				new Object[] {(Integer) pair[0], (String) pair[1]}, new Class[] {Integer.class, String.class}) );

		return getAllFromCentralAndLocalByMethod(
				getExperimentDao(), "getExperimentIdsByGeolocationIds",
				new Object[] {geolocationIds}, new Class[] {Collection.class});
	}
	
	@SuppressWarnings("unchecked")
	private List<Integer> findExperimentsByStockFactorValue(Object[] pair) throws MiddlewareQueryException {
		Set<Integer> stockIds = new HashSet<Integer>();
		stockIds.addAll(getAllFromCentralAndLocalByMethod(
				getStockPropertyDao(), "getStockIdsByPropertyTypeAndValue", 
				new Object[] {(Integer) pair[0], (String) pair[1]},
				new Class[] {Integer.class, String.class}));
			
		return getAllFromCentralAndLocalByMethod(
				getExperimentStockDao(), "getExperimentIdsByStockIds",
				new Object[] {stockIds}, new Class[] {Collection.class});
	}
	
	@SuppressWarnings("unchecked")
	private List<Integer> findExperimentsByExperimentFactorValue(Object[] pair) throws MiddlewareQueryException {
		return getAllFromCentralAndLocalByMethod(
				getExperimentPropertyDao(), "getExperimentIdsByPropertyTypeAndValue",
				new Object[] {(Integer) pair[0], (String) pair[1]},
				new Class[] {Integer.class, String.class});
	}
	
	@SuppressWarnings("unchecked")
	private List<Integer> getProjectIdsByExperiment(Collection<Integer> experimentIds) throws MiddlewareQueryException {
		return getAllFromCentralAndLocalByMethod(
				getExperimentProjectDao(), "getProjectIdsByExperimentIds",
				new Object[] {experimentIds}, new Class[] {Collection.class});
	}
	
	private List<StudyNode> getStudiesByProjectIds(Collection<Integer> ids) throws MiddlewareQueryException {
		List<StudyNode> studyNodes = new ArrayList<StudyNode>();
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
					studyNodes.addAll(getDmsProjectDao().getStudiesByIds(positiveIds));
				}
			}
			if (negativeIds.size() > 0) {
				if (setWorkingDatabase(Database.LOCAL)) {
					studyNodes.addAll(getDmsProjectDao().getStudiesByIds(negativeIds));
				}
			}
		}
		return studyNodes;
	}
	
	@SuppressWarnings("unchecked")
	private Integer getDeterminantForWhichTableToQuery(Integer factorId) throws MiddlewareQueryException {
		
		List<Integer> determinants = getAllFromCentralAndLocalByMethod(
				getCVTermRelationshipDao(), "getObjectIdByTypeAndSubject",
				new Object[] {CVTermId.STORED_IN.getId(), factorId}, new Class[] {Integer.class, Integer.class});

		return (determinants != null && determinants.size() > 0 ? determinants.get(0) : null);
	}
}
