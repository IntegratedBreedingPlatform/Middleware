package org.generationcp.middleware.v2.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DataManager;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.v2.dao.CVTermDao;
import org.generationcp.middleware.v2.dao.CVTermRelationshipDao;
import org.generationcp.middleware.v2.dao.DmsProjectDao;
import org.generationcp.middleware.v2.factory.FactorDetailsFactory;
import org.generationcp.middleware.v2.factory.ObservationDetailsFactory;
import org.generationcp.middleware.v2.factory.StudyFactory;
import org.generationcp.middleware.v2.factory.VariableDetailsFactory;
import org.generationcp.middleware.v2.manager.api.StudyDataManager;
import org.generationcp.middleware.v2.pojos.AbstractNode;
import org.generationcp.middleware.v2.pojos.CVTerm;
import org.generationcp.middleware.v2.pojos.CVTermRelationship;
import org.generationcp.middleware.v2.pojos.DatasetNode;
import org.generationcp.middleware.v2.pojos.DmsProject;
import org.generationcp.middleware.v2.pojos.FolderNode;
import org.generationcp.middleware.v2.pojos.FactorDetails;
import org.generationcp.middleware.v2.pojos.ObservationDetails;
import org.generationcp.middleware.v2.pojos.StudyDetails;
import org.generationcp.middleware.v2.pojos.VariableDetails;
import org.generationcp.middleware.v2.util.CVTermRelationshipUtil;
import org.generationcp.middleware.v2.util.ProjectPropertyUtil;
import org.hibernate.Session;

public class StudyDataManagerImpl extends DataManager implements StudyDataManager {

	private DmsProjectDao dmsProjectDao;
	
	private CVTermRelationshipDao cvTermRelationshipDao;
	
	private CVTermDao cvTermDao;

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

	//@Override
	/*public List<StudyNode> searchStudies(StudyQueryFilter filter) throws MiddlewareQueryException {
		Set<StudyNode> studies = new HashSet<StudyNode>();
		
		if (setWorkingDatabase(filter.getInstance())) {		
			//search by start date - in projectprop
			if (filter.getStartDate() != null) {
				studies.addAll(getDmsProjectDao().getByStudyProperty(CVTermId.START_DATE.getId(), filter.getStartDate().toString()));
			}
			
			//search by name
			if (filter.getName() != null) {
				studies.addAll(getDmsProjectDao().getByName(filter.getName()));
			}
			
			//search by country
			if (filter.getCountry() != null) {
				studies.addAll(getDmsProjectDao().getByCountry(filter.getCountry()));
			}
			
			//search by season
			if (filter.getSeason() != null) {
				
			}
		
		}
		return new ArrayList<StudyNode>(studies);

	}
*/
	
	//===================================  HELPER METHODS ==============================================	
	private <T extends VariableDetails> List<T> getDetails(Integer studyId, VariableDetailsFactory<T> factory) throws MiddlewareQueryException {
		List<T> factors = new ArrayList<T>();
		
		if (setWorkingDatabase(studyId)) {
			DmsProject project = getDmsProjectDao().getById(studyId);
			
			Set<Integer> localVarIds = ProjectPropertyUtil.extractLocalStandardVariableIds(project.getProperties());
			Set<Integer> centralVarIds = ProjectPropertyUtil.extractCentralStandardVariableIds(project.getProperties());
			System.out.println("local " + localVarIds);
			System.out.println("central " + centralVarIds);
			List<CVTermRelationship> relationships = getRelationshipsFromLocalAndCentral(studyId, localVarIds, centralVarIds);
			
			Set<Integer> localTermIds = CVTermRelationshipUtil.extractLocalObjectTermIds(relationships);
			Set<Integer> centralTermIds = CVTermRelationshipUtil.extractCentralObjectTermIds(relationships);
			List<CVTerm> terms = getTermsFromLocalAndCentral(localTermIds, centralTermIds);
			
			factors = factory.createDetails(project, relationships, terms);
		}
		
		return factors;	
	}

	@SuppressWarnings("unchecked")
	private List<CVTermRelationship> getRelationshipsFromLocalAndCentral(Integer studyId, Collection<Integer> localVarIds, Collection<Integer> centralVarIds)
	throws MiddlewareQueryException {
		
		List<CVTermRelationship> relationships = 
				getFromInstanceByIdAndMethod(
					getCVTermRelationshipDao(), 
					studyId, 
					"getBySubjectIds", 
					new Object[] {localVarIds}, 
					new Class[] {Collection.class});
		
		relationships.addAll( 
				getFromInstanceByIdAndMethod(
					getCVTermRelationshipDao(), 
					studyId, 
					"getBySubjectIds", 
					new Object[] {centralVarIds}, 
					new Class[] {Collection.class}));
		
		return relationships;
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

}
