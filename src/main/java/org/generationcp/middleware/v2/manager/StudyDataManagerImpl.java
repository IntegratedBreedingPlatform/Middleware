/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
package org.generationcp.middleware.v2.manager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DataManager;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.v2.domain.DataSet;
import org.generationcp.middleware.v2.domain.DatasetReference;
import org.generationcp.middleware.v2.domain.DatasetValues;
import org.generationcp.middleware.v2.domain.Experiment;
import org.generationcp.middleware.v2.domain.ExperimentValues;
import org.generationcp.middleware.v2.domain.FolderReference;
import org.generationcp.middleware.v2.domain.Reference;
import org.generationcp.middleware.v2.domain.Study;
import org.generationcp.middleware.v2.domain.StudyQueryFilter;
import org.generationcp.middleware.v2.domain.StudyReference;
import org.generationcp.middleware.v2.domain.StudyValues;
import org.generationcp.middleware.v2.domain.TermId;
import org.generationcp.middleware.v2.domain.VariableList;
import org.generationcp.middleware.v2.domain.VariableTypeList;
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
	
	@Override
	public Study getStudy(int studyId) throws MiddlewareQueryException {
	    return getStudyBuilder().createStudy(studyId);
	}
	
	@Override
	public List<FolderReference> getRootFolders(Database instance) throws MiddlewareQueryException {
		if (setWorkingDatabase(instance)) {
			return getDmsProjectDao().getRootFolders();
		}
		return new ArrayList<FolderReference>();
	}
	
	@Override
	public List<Reference> getChildrenOfFolder(int folderId) throws MiddlewareQueryException {
		if (setWorkingDatabase(folderId)) {
			return getDmsProjectDao().getChildrenOfFolder(folderId);
		}
		return new ArrayList<Reference>();
	}
	
	@Override
	public List<DatasetReference> getDatasetReferences(int studyId) throws MiddlewareQueryException {
		if (setWorkingDatabase(studyId)) {
			return getDmsProjectDao().getDatasetNodesByStudyId(studyId);
		}
		return new ArrayList<DatasetReference>();
	}

	@Override
	public DataSet getDataSet(int dataSetId) throws MiddlewareQueryException {
		return getDataSetBuilder().build(dataSetId);
	}

	@Override
	public VariableTypeList getAllStudyFactors(int studyId) throws MiddlewareQueryException {
		return getStudyFactorBuilder().build(studyId);
	}
	
	@Override
	public VariableTypeList getAllStudyVariates(int studyId) throws MiddlewareQueryException {
		return getStudyVariateBuilder().build(studyId);
	}
	
	@Override
	public List<Study> getStudiesByFolder(int folderId, int start, int numOfRows) throws MiddlewareQueryException{
		List<Study> studyDetails = new ArrayList<Study>();
		if (setWorkingDatabase(folderId, getDmsProjectDao())) {
			List<DmsProject> projects = (List<DmsProject>) getDmsProjectDao()
					.getProjectsByFolder(folderId, start, numOfRows);

			for (DmsProject project : projects) {
				studyDetails.add(getStudyBuilder().createStudy(project));
			}
		}
		return studyDetails;
	}
	
	@Override
	public long countStudiesByFolder(int folderId) throws MiddlewareQueryException{
		long count = 0;
		if (setWorkingDatabase(folderId, getDmsProjectDao())) {
			count = getDmsProjectDao().countProjectsByFolder(folderId);
		}
		return count;

	}


	@Override
	public List<StudyReference> searchStudies(StudyQueryFilter filter) throws MiddlewareQueryException {
		List<DmsProject> projects = getProjectSearcher().searchByFilter(filter);
		return getStudyNodeBuilder().build(projects);
	}
	
	@Override
	public Set<Study> searchStudiesByGid(int gid) throws MiddlewareQueryException {
		Set<Study> studies = new HashSet<Study>();
		List<DmsProject> projects = getProjectSearcher().searchStudiesByFactor(TermId.GID.getId(), Integer.toString(gid));
		for (DmsProject project : projects)	 {
			studies.add(getStudyBuilder().createStudy(project));
		}
		return studies;
	}

	@Override
    public StudyReference addStudy(int parentFolderId, VariableTypeList variableTypeList, StudyValues studyValues) throws MiddlewareQueryException{
        requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        try {
            trans = session.beginTransaction();

            DmsProject project = getProjectSaver().create(studyValues);
            
            // Save Study (Project, Properties, Relationships)
            project.setProjectId(getStudySaver().saveStudy(parentFolderId, variableTypeList, studyValues, project));
            
            getExperimentModelSaver().addExperiment(project.getProjectId(), studyValues);

			trans.commit();

			return new StudyReference(project.getProjectId(), project.getName(), project.getDescription());
			
        } catch (Exception e) {
        	e.printStackTrace();
        	rollbackTransaction(trans);
			logAndThrowException("Error encountered with addStudy(folderId="
					+ parentFolderId + ", variableTypeList=" + variableTypeList
					+ ", studyValues=" + studyValues + "): " + e.getMessage(),
					e, LOG);
        }   

        return null;
    }
	
	
	@Override
	public DatasetReference addDataSet(int studyId, VariableTypeList variableTypeList, DatasetValues datasetValues) throws MiddlewareQueryException {
		requireLocalDatabaseInstance();
		Session session = getCurrentSessionForLocal();
        Transaction trans = null;
 
        try {
            trans = session.beginTransaction();
			DmsProject datasetProject = getDatasetProjectSaver().addDataSet(studyId, variableTypeList, datasetValues);
			trans.commit();
			return new DatasetReference(datasetProject.getProjectId(), datasetProject.getName(), datasetProject.getDescription());

        } catch (Exception e) {
	    	rollbackTransaction(trans);
	        throw new MiddlewareQueryException("error in addDataSet " + e.getMessage(), e);
	    }
	}

	@Override
	public List<Experiment> getExperiments(int dataSetId, int startIndex, int numRows) throws MiddlewareQueryException {
		VariableTypeList variableTypes = getDataSetBuilder().getVariableTypes(dataSetId);
		return getExperimentBuilder().build(dataSetId, TermId.PLOT_EXPERIMENT, startIndex, numRows, variableTypes);
	}

	@Override
	public long countExperiments(int dataSetId) throws MiddlewareQueryException {
		return getExperimentBuilder().count(dataSetId);
	}

	@Override
	public void addExperiment(int dataSetId, ExperimentValues experimentValues) throws MiddlewareQueryException {
		requireLocalDatabaseInstance();
		Session session = getCurrentSessionForLocal();
        Transaction trans = null;
 
        try {
            trans = session.beginTransaction();
            getExperimentModelSaver().addExperiment(dataSetId, experimentValues);
            trans.commit();
            
        } catch (Exception e) {
	    	rollbackTransaction(trans);
	        throw new MiddlewareQueryException("error in addExperiment " + e.getMessage(), e);
	    }
	}

	@Override
	public int addTrialEnvironment(VariableList variableList) throws MiddlewareQueryException {
		requireLocalDatabaseInstance();
		Session session = getCurrentSessionForLocal();
        Transaction trans = null;
 
        try {
            trans = session.beginTransaction();
			int id = getGeolocationSaver().saveGeolocation(variableList);		
	        trans.commit();
	        return id;
	        
	    } catch (Exception e) {
	    	rollbackTransaction(trans);
	        throw new MiddlewareQueryException("error in addTrialEnvironment " + e.getMessage(), e);
	    }
	}

	@Override
	public int addStock(VariableList variableList) throws MiddlewareQueryException {
		requireLocalDatabaseInstance();
		Session session = getCurrentSessionForLocal();
        Transaction trans = null;
 
        try {
            trans = session.beginTransaction();
			int id = getStockSaver().saveStock(variableList);
	        trans.commit();
	        return id;
        
    } catch (Exception e) {
    	rollbackTransaction(trans);
        throw new MiddlewareQueryException("error in addStock " + e.getMessage(), e);
    }
	}
}
