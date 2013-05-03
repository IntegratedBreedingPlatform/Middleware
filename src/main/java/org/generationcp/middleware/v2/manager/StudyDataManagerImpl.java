package org.generationcp.middleware.v2.manager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DataManager;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.pojos.Study;
import org.generationcp.middleware.v2.domain.AbstractNode;
import org.generationcp.middleware.v2.domain.CVTermId;
import org.generationcp.middleware.v2.domain.DataSet;
import org.generationcp.middleware.v2.domain.DatasetNode;
import org.generationcp.middleware.v2.domain.Experiment;
import org.generationcp.middleware.v2.domain.FactorDetails;
import org.generationcp.middleware.v2.domain.FolderNode;
import org.generationcp.middleware.v2.domain.StudyDetails;
import org.generationcp.middleware.v2.domain.StudyNode;
import org.generationcp.middleware.v2.domain.StudyQueryFilter;
import org.generationcp.middleware.v2.domain.VariableTypeList;
import org.generationcp.middleware.v2.domain.VariateDetails;
import org.generationcp.middleware.v2.factory.StudyFactory;
import org.generationcp.middleware.v2.manager.api.StudyDataManager;
import org.generationcp.middleware.v2.pojos.DmsProject;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StudyDataManagerImpl extends DataManager implements StudyDataManager {
	
    private static final Logger LOG = LoggerFactory.getLogger(StudyDataManagerImpl.class);

	public StudyDataManagerImpl() { 		
	}

	public StudyDataManagerImpl(HibernateSessionProvider sessionProviderForLocal,
			                    HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}

	public StudyDataManagerImpl(Session sessionForLocal, Session sessionForCentral) {
		super(sessionForLocal, sessionForCentral);
	}
	
	public StudyFactory getStudyFactory() {
		return StudyFactory.getInstance();
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
		return new ArrayList<FolderNode>();
	}
	
	@Override
	public List<AbstractNode> getChildrenOfFolder(Integer folderId, Database instance) throws MiddlewareQueryException{
		if (setWorkingDatabase(instance, getDmsProjectDao())){
			return getDmsProjectDao().getChildrenOfFolder(folderId);
		}
		return new ArrayList<AbstractNode>();
	}
	
	@Override
	public List<DatasetNode> getDatasetNodesByStudyId(Integer studyId) throws MiddlewareQueryException{
		if (setWorkingDatabase(studyId, getDmsProjectDao())){
			return getDmsProjectDao().getDatasetNodesByStudyId(studyId);
		}
		return new ArrayList<DatasetNode>();
	}

	@Override
	public DataSet getDataSet(int dataSetId) throws MiddlewareQueryException {
		return getDataSetBuilder().build(dataSetId);
	}

	@Override
	public List<FactorDetails> getFactors(Integer projectId) throws MiddlewareQueryException {
		return getFactorDetailsBuilder().build(projectId);
	}
	
	@Override
	public List<VariateDetails> getVariates(Integer projectId) throws MiddlewareQueryException {
		return getVariateDetailsBuilder().build(projectId);
	}
	
	@Override
	public List<StudyDetails> getStudiesByFolder(Integer folderId, int start, int numOfRows) throws MiddlewareQueryException{
		List<StudyDetails> studyDetails = new ArrayList<StudyDetails>();
		if (setWorkingDatabase(folderId, getDmsProjectDao())) {
			List<DmsProject> projects = (List<DmsProject>) getDmsProjectDao()
					.getProjectsByFolder(folderId, start, numOfRows);

			for (DmsProject project : projects) {
				studyDetails.add(getStudyFactory().createStudyDetails(project));
			}
		}
		return studyDetails;
	}
	
	@Override
	public long countStudiesByFolder(Integer folderId) throws MiddlewareQueryException{
		long count = 0;
		if (setWorkingDatabase(folderId, getDmsProjectDao())) {
			count = getDmsProjectDao().countProjectsByFolder(folderId);
		}
		return count;

	}


	@Override
	public List<StudyNode> searchStudies(StudyQueryFilter filter) throws MiddlewareQueryException {
		List<DmsProject> projects = getProjectSearcher().searchByFilter(filter);
		return getStudyNodeBuilder().build(projects);
	}
	
	@Override
	public Set<StudyDetails> searchStudiesByGid(Integer gid) throws MiddlewareQueryException {
		Set<StudyDetails> studies = new HashSet<StudyDetails>();
		List<DmsProject> projects = getProjectSearcher().searchStudiesByFactor(CVTermId.GID.getId(), gid.toString());
		for (DmsProject project : projects)	 {
			studies.add(getStudyFactory().createStudyDetails(project));
		}
		return studies;
	}

	@Override
    public Study addStudy(Study study) throws MiddlewareQueryException{
        requireLocalDatabaseInstance();
        DmsProject parent = getDmsProjectDao().getById(study.getHierarchy()); 
		try {
			study.setId(getStudySaver().saveStudy(study, parent));
        } catch (Exception e) {
        	e.printStackTrace();
            logAndThrowException("Error encountered with addStudy(study=" + study + "): " + e.getMessage(), e, LOG);
		}
		return study;
    }
	
	@Override
    public StudyDetails addStudyDetails(StudyDetails studyDetails) throws MiddlewareQueryException{
        requireLocalDatabaseInstance();
		try {
			studyDetails.setId(getStudySaver().saveStudy(new Study(studyDetails), null));
        } catch (Exception e) {
          logAndThrowException("Error encountered with addStudyDetails(studyDetails=" + studyDetails + "): " + e.getMessage(), e, LOG);
		}
		return studyDetails;	
    }
	

	@Override
	public void addDataSet(DataSet dataset) throws MiddlewareQueryException {
		requireLocalDatabaseInstance();
		Session session = getCurrentSessionForLocal();
        Transaction trans = null;
 
        try {
            trans = session.beginTransaction();
			getDatasetProjectSaver().saveDataSet(dataset);
	        trans.commit();
	        
	    } catch (Exception e) {
	    	rollbackTransaction(trans);
	        throw new MiddlewareQueryException("error in save " + e.getMessage(), e);
	    }
	}

	@Override
	public List<Experiment> getExperiments(int dataSetId, int startIndex, int numRows) throws MiddlewareQueryException {
		VariableTypeList variableTypes = getDataSetBuilder().getVariableTypes(dataSetId);
		return getExperimentBuilder().build(dataSetId, startIndex, numRows, variableTypes);
	}

	@Override
	public int countExperiments(int dataSetId) throws MiddlewareQueryException {
		return getExperimentBuilder().count(dataSetId);
	}
}
