package org.generationcp.middleware.v2.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DataManager;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.v2.dao.CVTermRelationshipDao;
import org.generationcp.middleware.v2.dao.DmsProjectDao;
import org.generationcp.middleware.v2.factory.FactorDetailsFactory;
import org.generationcp.middleware.v2.factory.ObservationDetailsFactory;
import org.generationcp.middleware.v2.factory.StudyFactory;
import org.generationcp.middleware.v2.factory.VariableDetailsFactory;
import org.generationcp.middleware.v2.manager.api.StudyDataManager;
import org.generationcp.middleware.v2.pojos.CVTermRelationship;
import org.generationcp.middleware.v2.pojos.DmsProject;
import org.generationcp.middleware.v2.pojos.FactorDetails;
import org.generationcp.middleware.v2.pojos.Folder;
import org.generationcp.middleware.v2.pojos.ObservationDetails;
import org.generationcp.middleware.v2.pojos.StudyDetails;
import org.generationcp.middleware.v2.pojos.VariableDetails;
import org.generationcp.middleware.v2.util.ProjectPropertyUtil;
import org.hibernate.Session;

public class StudyDataManagerImpl extends DataManager implements StudyDataManager {

	private DmsProjectDao dmsProjectDao;
	
	private CVTermRelationshipDao cvTermRelationshipDao;

	public StudyDataManagerImpl() { }

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
	public StudyDetails getStudyDetails(int studyId) throws MiddlewareQueryException {
		if (setWorkingDatabase(studyId)) {
			DmsProject project = getDmsProjectDao().getById(studyId);
			if (project != null) {
				return getStudyFactory().createStudyDetails(project);
			}
		}
		return null;
	}
	
	@Override
	public List<Folder> getRootFolders(Database instance) throws MiddlewareQueryException{
			
		if (setWorkingDatabase(instance, getDmsProjectDao())){
			return getDmsProjectDao().getRootFolders();
		}
		
		return null;
	}

	@Override
	public List<FactorDetails> getFactorDetails(int studyId) throws MiddlewareQueryException {
		return getDetails(studyId, getFactorDetailsFactory());
	}

	@Override
	public List<ObservationDetails> getObservationDetails(int studyId) throws MiddlewareQueryException {
		return getDetails(studyId, getObservationDetailsFactory());
	}

	private <T extends VariableDetails> List<T> getDetails(int studyId, VariableDetailsFactory<T> factory) throws MiddlewareQueryException {
		List<T> factors = new ArrayList<T>();
		
		if (setWorkingDatabase(studyId)) {
			DmsProject project = getDmsProjectDao().getById(studyId);
			Set<Integer> varIds = ProjectPropertyUtil.extractStandardVariableIds(project.getProperties());
			
			@SuppressWarnings("unchecked")
			List<CVTermRelationship> relationships = 
					getFromInstanceByIdAndMethod(
						getCVTermRelationshipDao(), 
						studyId, 
						"getBySubjectIds", 
						new Object[] {varIds}, 
						new Class[] {Collection.class});

			factors = factory.createDetails(project, relationships);
		}
		
		return factors;	
	}
}
