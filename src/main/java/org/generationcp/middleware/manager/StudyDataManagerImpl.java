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
package org.generationcp.middleware.manager;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.DataSetType;
import org.generationcp.middleware.domain.dms.DatasetReference;
import org.generationcp.middleware.domain.dms.DatasetValues;
import org.generationcp.middleware.domain.dms.Experiment;
import org.generationcp.middleware.domain.dms.ExperimentType;
import org.generationcp.middleware.domain.dms.ExperimentValues;
import org.generationcp.middleware.domain.dms.FolderReference;
import org.generationcp.middleware.domain.dms.Reference;
import org.generationcp.middleware.domain.dms.Stocks;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.dms.StudyValues;
import org.generationcp.middleware.domain.dms.TrialEnvironments;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableType;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.fieldbook.FieldMapInfo;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.search.StudyResultSet;
import org.generationcp.middleware.domain.search.StudyResultSetByGid;
import org.generationcp.middleware.domain.search.StudyResultSetByNameStartDateSeasonCountry;
import org.generationcp.middleware.domain.search.StudyResultSetByParentFolder;
import org.generationcp.middleware.domain.search.filter.BrowseStudyQueryFilter;
import org.generationcp.middleware.domain.search.filter.GidStudyQueryFilter;
import org.generationcp.middleware.domain.search.filter.ParentFolderStudyQueryFilter;
import org.generationcp.middleware.domain.search.filter.StudyQueryFilter;
import org.generationcp.middleware.domain.workbench.StudyNode;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.util.PlotUtil;
import org.hibernate.HibernateException;
import org.hibernate.Query;
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
    public int getStudyIdByName(String studyName) throws MiddlewareQueryException {
        Integer id = null;
        setWorkingDatabase(Database.CENTRAL);
        id = getDmsProjectDao().getProjectIdByName(studyName, TermId.IS_STUDY);
        if (id == null) {
            setWorkingDatabase(Database.LOCAL);
            id = getDmsProjectDao().getProjectIdByName(studyName, TermId.IS_STUDY);
        }
        return id;
    }

    @Override
    public boolean checkIfProjectNameIsExisting(String name) throws MiddlewareQueryException {
        boolean isExisting = false;
        setWorkingDatabase(Database.CENTRAL);
        isExisting = getDmsProjectDao().checkIfProjectNameIsExisting(name);
        if (!isExisting) {
            setWorkingDatabase(Database.LOCAL);
            isExisting = getDmsProjectDao().checkIfProjectNameIsExisting(name);
        }
        return isExisting;
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
    public StudyResultSet searchStudies(StudyQueryFilter filter, int numOfRows) throws MiddlewareQueryException {
        if (filter instanceof ParentFolderStudyQueryFilter) {
            return new StudyResultSetByParentFolder((ParentFolderStudyQueryFilter) filter, numOfRows, this.sessionProviderForLocal, this.sessionProviderForCentral);
        } else if (filter instanceof GidStudyQueryFilter) {
            return new StudyResultSetByGid((GidStudyQueryFilter) filter, numOfRows, this.sessionProviderForLocal, this.sessionProviderForCentral);
        } else if (filter instanceof BrowseStudyQueryFilter) {
            return new StudyResultSetByNameStartDateSeasonCountry((BrowseStudyQueryFilter) filter, numOfRows, this.sessionProviderForLocal, this.sessionProviderForCentral);
        }
        return null;
    }

    @Override
    public StudyReference addStudy(int parentFolderId, VariableTypeList variableTypeList, StudyValues studyValues) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        try {
            trans = session.beginTransaction();
            DmsProject project = getStudySaver().saveStudy(parentFolderId, variableTypeList, studyValues);
            trans.commit();
            return new StudyReference(project.getProjectId(), project.getName(), project.getDescription());

        } catch (Exception e) {
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
    public List<Experiment> getExperiments(int dataSetId, int start, int numRows) throws MiddlewareQueryException {
        clearSessions();
        VariableTypeList variableTypes = getDataSetBuilder().getVariableTypes(dataSetId);
        return getExperimentBuilder().build(dataSetId, PlotUtil.getAllPlotTypes(), start, numRows, variableTypes);
    }

    @Override
    public long countExperiments(int dataSetId) throws MiddlewareQueryException {
        return getExperimentBuilder().count(dataSetId);
    }

    @Override
    public void addExperiment(int dataSetId, ExperimentType experimentType, ExperimentValues experimentValues) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        try {
            trans = session.beginTransaction();
            getExperimentModelSaver().addExperiment(dataSetId, experimentType, experimentValues);
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

    @Override
    public List<DataSet> getDataSetsByType(int studyId, DataSetType dataSetType) throws MiddlewareQueryException {
        setWorkingDatabase(studyId);

        List<DmsProject> datasetProjects = getDmsProjectDao().getDataSetsByStudyAndProjectProperty(
                studyId, TermId.DATASET_TYPE.getId(), String.valueOf(dataSetType.getId()));
        List<DataSet> datasets = new ArrayList<DataSet>();

        for (DmsProject datasetProject : datasetProjects) {
            datasets.add(getDataSetBuilder().build(datasetProject.getProjectId()));
        }

        return datasets;
    }

    @Override
    public long countExperimentsByTrialEnvironmentAndVariate(int trialEnvironmentId, int variateVariableId) throws MiddlewareQueryException {
        long count = 0;
        if (this.setWorkingDatabase(trialEnvironmentId)) {
            count = getExperimentDao().countByTrialEnvironmentAndVariate(trialEnvironmentId, variateVariableId);
        }
        return count;
    }

    @Override
    public void addDataSetVariableType(int datasetId, VariableType variableType) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        try {
            trans = session.beginTransaction();
            this.getDatasetProjectSaver().addDatasetVariableType(datasetId, variableType);
            trans.commit();

        } catch (Exception e) {
            rollbackTransaction(trans);
            throw new MiddlewareQueryException("error in addDataSetVariableType " + e.getMessage(), e);
        }
    }

    @Override
    public void setExperimentValue(int experimentId, int variableId, String value) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        try {
            trans = session.beginTransaction();
            this.getExperimentModelSaver().setExperimentValue(experimentId, variableId, value);
            trans.commit();

        } catch (Exception e) {
            rollbackTransaction(trans);
            throw new MiddlewareQueryException("error in addDataSetVariableType " + e.getMessage(), e);
        }
    }

    @Override
    public TrialEnvironments getTrialEnvironmentsInDataset(int datasetId) throws MiddlewareQueryException {
        return getTrialEnvironmentBuilder().getTrialEnvironmentsInDataset(datasetId);
    }

    @Override
    public Stocks getStocksInDataset(int datasetId) throws MiddlewareQueryException {
        return getStockBuilder().getStocksInDataset(datasetId);
    }

    @Override
    public long countStocks(int datasetId, int trialEnvironmentId, int variateStdVarId) throws MiddlewareQueryException {
        if (this.setWorkingDatabase(datasetId)) {
            return getStockDao().countStocks(datasetId, trialEnvironmentId, variateStdVarId);
        }
        return 0;
    }

    @Override
    public DataSet findOneDataSetByType(int studyId, DataSetType dataSetType) throws MiddlewareQueryException {
        List<DataSet> datasets = getDataSetsByType(studyId, dataSetType);
        if (datasets != null && datasets.size() >= 1) {
            return datasets.get(0);
        }
        return null;
    }

    @Override
    public void deleteDataSet(int datasetId) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        try {
            trans = session.beginTransaction();
            getDataSetDestroyer().deleteDataSet(datasetId);
            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            throw new MiddlewareQueryException("error in deleteDataSet " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteExperimentsByLocation(int datasetId, int locationId) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        try {
            trans = session.beginTransaction();
            getDataSetDestroyer().deleteExperimentsByLocation(datasetId, locationId);
            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            throw new MiddlewareQueryException("error in deleteExperimentsByLocation " + e.getMessage(), e);
        }
    }

    @Override
    public String getLocalNameByStandardVariableId(Integer projectId, Integer standardVariableId) throws MiddlewareQueryException {
        setWorkingDatabase(projectId);
        Session session = getActiveSession();

        try {

            String sql = "select DISTINCT pp.value " +
                    "from projectprop pp " +
                    "inner join projectprop pp2 on pp.rank = pp2.rank and pp.type_id = 1041 " +
                    "where pp.project_id = :projectId and pp2.value = :standardVariableId LIMIT 0,1";


            Query query = session.createSQLQuery(sql);
            query.setParameter("projectId", projectId);
            query.setParameter("standardVariableId", standardVariableId);

            return (String) query.uniqueResult();

        } catch (HibernateException e) {
            logAndThrowException("Error at getLocalNameByStandardVariableId :" + e.getMessage(), e);
        }
        return null;
    }
    

    @Override
    public List<StudyDetails> getAllStudyDetails(Database instance, StudyType studyType) throws MiddlewareQueryException {
        setWorkingDatabase(instance);
        return getDmsProjectDao().getAllStudyDetails(studyType);
    }

    @Override
	public List<StudyNode> getAllNurseryAndTrialStudyNodes() throws MiddlewareQueryException{
    	List<StudyNode> studyNodes = new ArrayList<StudyNode>();
        studyNodes.addAll(getNurseryAndTrialStudyNodes(Database.LOCAL));
        studyNodes.addAll(getNurseryAndTrialStudyNodes(Database.CENTRAL));
        return studyNodes;
    }

    @Override
	public List<StudyNode> getNurseryAndTrialStudyNodes(Database instance) throws MiddlewareQueryException{
        setWorkingDatabase(instance);
        return getDmsProjectDao().getAllNurseryAndTrialStudyNodes();
    }

    @Override
    public long countProjectsByVariable(int variableId) throws MiddlewareQueryException {
        setWorkingDatabase(Database.LOCAL);
        long count = getDmsProjectDao().countByVariable(variableId);
        if (variableId > 0) {
            setWorkingDatabase(Database.CENTRAL);
            count += getDmsProjectDao().countByVariable(variableId);
        }
        return count;
    }

    @Override
    public long countExperimentsByVariable(int variableId, int storedInId) throws MiddlewareQueryException {
        setWorkingDatabase(Database.LOCAL);
        long count = getExperimentDao().countByObservedVariable(variableId, storedInId);
        if (variableId > 0) {
            setWorkingDatabase(Database.CENTRAL);
            count += getExperimentDao().countByObservedVariable(variableId, storedInId);
        }
        return count;
    }
    
    @Override
    public FieldMapInfo getFieldMapInfoOfStudy(int studyId, StudyType studyType) throws MiddlewareQueryException{
        FieldMapInfo fieldMapInfo = new FieldMapInfo();
        setWorkingDatabase(studyId);
        
        fieldMapInfo.setFieldbookId(studyId);
        fieldMapInfo.setFieldbookName(getDmsProjectDao().getById(studyId).getName());

        if (studyType == StudyType.T){
        	fieldMapInfo.setTrial(true);
        } else {
        	fieldMapInfo.setTrial(false);
        }
        
        fieldMapInfo.setDatasets(getExperimentPropertyDao().getFieldMapLabels(studyId));
        
        return fieldMapInfo;
    }
    
    @Override
    public void saveOrUpdateFieldmapProperties(FieldMapInfo info) throws MiddlewareQueryException {
        /*
        if (info != null && info.getFieldMapLabels() != null && !info.getFieldMapLabels().isEmpty()) {
            requireLocalDatabaseInstance();
            Session session = getCurrentSessionForLocal();
            Transaction trans = null;
    
            try {
                trans = session.beginTransaction();
                
                getExperimentPropertySaver().saveFieldmapProperties(info);
                
                trans.commit();
    
            } catch (Exception e) {
                rollbackTransaction(trans);
                logAndThrowException("Error encountered with saveOrUpdateFieldmapProperties(): " + e.getMessage(),
                        e, LOG);
            }
        }
        */
    }
    
    @Override
    public void saveTrialDatasetSummary(DmsProject project, VariableTypeList variableTypeList, List<ExperimentValues> experimentValues, List<Integer> locationIds) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        try {
            trans = session.beginTransaction();
            if(variableTypeList!=null && variableTypeList.getVariableTypes()!=null && !variableTypeList.getVariableTypes().isEmpty()) {
            	getProjectPropertySaver().saveProjectProperties(project,variableTypeList);
            }
            if(experimentValues!=null && !experimentValues.isEmpty()) {
            	for (Integer locationId : locationIds) {
            		getDataSetDestroyer().deleteExperimentsByLocationAndExperimentType(
            				project.getProjectId(), locationId, TermId.SUMMARY_EXPERIMENT.getId());
				}
            	for (ExperimentValues exp : experimentValues) {
            		if(exp.getVariableList()!=null && exp.getVariableList().size()>0) {
            			getExperimentModelSaver().addExperiment(project.getProjectId(), ExperimentType.SUMMARY, exp);
            		}
				}
            	
            }
            trans.commit();

        } catch (Exception e) {
            rollbackTransaction(trans);
            throw new MiddlewareQueryException("error in addDataSet " + e.getMessage(), e);
        }
    }
    
}
