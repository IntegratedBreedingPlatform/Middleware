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

import org.generationcp.middleware.dao.dms.DmsProjectDao;
import org.generationcp.middleware.dao.dms.PhenotypeOutlierDao;
import org.generationcp.middleware.domain.dms.*;
import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.fieldbook.*;
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
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.LocationDataManager;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.PhenotypeOutlier;
import org.generationcp.middleware.util.DatabaseBroker;
import org.generationcp.middleware.util.PlotUtil;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class StudyDataManagerImpl extends DataManager implements StudyDataManager {

    private GermplasmDataManagerImpl germplasmDataManager;
    
    private LocationDataManager locationDataManager;

    private static final Logger LOG = LoggerFactory.getLogger(StudyDataManagerImpl.class);

    public StudyDataManagerImpl() {
    }
    
    public StudyDataManagerImpl(HibernateSessionProvider sessionProviderForLocal,
            HibernateSessionProvider sessionProviderForCentral,
            String localDatabaseName, String centralDatabaseName) {
		super(sessionProviderForLocal, sessionProviderForCentral, localDatabaseName, centralDatabaseName);
		germplasmDataManager = new GermplasmDataManagerImpl(sessionProviderForLocal, 
				sessionProviderForCentral, localDatabaseName, centralDatabaseName);
		locationDataManager = new LocationDataManagerImpl(sessionProviderForLocal, sessionProviderForCentral);
	}

    public StudyDataManagerImpl(HibernateSessionProvider sessionProviderForLocal,
                                HibernateSessionProvider sessionProviderForCentral) {
        super(sessionProviderForLocal, sessionProviderForCentral);
        germplasmDataManager = new GermplasmDataManagerImpl(sessionProviderForLocal, 
                sessionProviderForCentral);
        locationDataManager = new LocationDataManagerImpl(sessionProviderForLocal, sessionProviderForCentral);
    }

    public StudyDataManagerImpl(Session sessionForLocal, Session sessionForCentral) {
        super(sessionForLocal, sessionForCentral);
        germplasmDataManager = new GermplasmDataManagerImpl(sessionForLocal, sessionForLocal);
        locationDataManager = new LocationDataManagerImpl(sessionProviderForLocal, sessionProviderForCentral);
    }

    @Override
    public Study getStudy(int studyId) throws MiddlewareQueryException {
        return getStudyBuilder().createStudy(studyId);
    }
    
    

    @Override
	public Study getStudy(int studyId, boolean hasVariableType)
			throws MiddlewareQueryException {
    	 return getStudyBuilder().createStudy(studyId, hasVariableType);
	}

	@Override
    public Integer getStudyIdByName(String studyName) throws MiddlewareQueryException {
        setWorkingDatabase(Database.CENTRAL);
        Integer id = getDmsProjectDao().getProjectIdByName(studyName, TermId.IS_STUDY);
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
    public StudyResultSet searchStudies(StudyQueryFilter filter, int numOfRows) 
            throws MiddlewareQueryException {
        if (filter instanceof ParentFolderStudyQueryFilter) {
            return new StudyResultSetByParentFolder((ParentFolderStudyQueryFilter) filter, numOfRows, 
                    this.sessionProviderForLocal, this.sessionProviderForCentral);
        } else if (filter instanceof GidStudyQueryFilter) {
            return new StudyResultSetByGid((GidStudyQueryFilter) filter, numOfRows, 
                    this.sessionProviderForLocal, this.sessionProviderForCentral);
        } else if (filter instanceof BrowseStudyQueryFilter) {
            return new StudyResultSetByNameStartDateSeasonCountry(
                    (BrowseStudyQueryFilter) filter, numOfRows, this.sessionProviderForLocal, 
                    this.sessionProviderForCentral);
        }
        return null;
    }

    @Override
    public StudyReference addStudy(int parentFolderId, VariableTypeList variableTypeList, 
            StudyValues studyValues) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        try {
            trans = session.beginTransaction();
            DmsProject project = getStudySaver().saveStudy(parentFolderId, variableTypeList, studyValues, true);
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
    public DatasetReference addDataSet(int studyId, VariableTypeList variableTypeList, 
            DatasetValues datasetValues) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        try {
            trans = session.beginTransaction();
            DmsProject datasetProject = getDatasetProjectSaver()
                    .addDataSet(studyId, variableTypeList, datasetValues);
            trans.commit();
            return new DatasetReference(datasetProject.getProjectId(), 
                    datasetProject.getName(), datasetProject.getDescription());

        } catch (Exception e) {
            rollbackTransaction(trans);
            throw new MiddlewareQueryException("error in addDataSet " + e.getMessage(), e);
        }
    }

    @Override
    public List<Experiment> getExperiments(int dataSetId, int start, int numRows) 
            throws MiddlewareQueryException {
        clearSessions();
        VariableTypeList variableTypes = getDataSetBuilder().getVariableTypes(dataSetId);
        return getExperimentBuilder().build(
                dataSetId, PlotUtil.getAllPlotTypes(), start, numRows, variableTypes);
    }

    @Override
    public List<Experiment> getExperimentsWithTrialEnvironment(int trialDataSetId, int dataSetId, int start, int numRows) 
            throws MiddlewareQueryException {
        clearSessions();
        
        VariableTypeList trialVariableTypes = getDataSetBuilder().getVariableTypes(trialDataSetId);
        VariableTypeList variableTypes = getDataSetBuilder().getVariableTypes(dataSetId);
        
        variableTypes.addAll(trialVariableTypes);
        
        return getExperimentBuilder().build(
                dataSetId, PlotUtil.getAllPlotTypes(), start, numRows, variableTypes);
    }

    @Override
	public List<Experiment> getExperiments(int dataSetId, int start,
			int numOfRows, VariableTypeList varTypeList)
			throws MiddlewareQueryException {
    	clearSessions();
		if(varTypeList == null) {
			return getExperiments(dataSetId, start, numOfRows);
		} else {
			return getExperimentBuilder().build(
	                dataSetId, PlotUtil.getAllPlotTypes(), start, numOfRows, varTypeList);
		}
	}
    
	@Override
    public long countExperiments(int dataSetId) throws MiddlewareQueryException {
        return getExperimentBuilder().count(dataSetId);
    }

    @Override
    public void addExperiment(int dataSetId, ExperimentType experimentType, 
            ExperimentValues experimentValues) throws MiddlewareQueryException {
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
    public void addOrUpdateExperiment(int dataSetId, ExperimentType experimentType, 
            ExperimentValues experimentValues) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        try {
            trans = session.beginTransaction();
            getExperimentModelSaver().addOrUpdateExperiment(dataSetId, experimentType, experimentValues);
            trans.commit();

        } catch (Exception e) {
            rollbackTransaction(trans);
            throw new MiddlewareQueryException("error in addExperiment " + e.getMessage(), e);
        }
    }
    
    @Override
	public void addOrUpdateExperiment(int dataSetId,
			ExperimentType experimentType,
			List<ExperimentValues> experimentValuesList)
			throws MiddlewareQueryException {
    	  requireLocalDatabaseInstance();
          Session session = getCurrentSessionForLocal();
          Transaction trans = null;

          try {
              trans = session.beginTransaction();
              
              for (ExperimentValues experimentValues : experimentValuesList){
            	  getExperimentModelSaver().addOrUpdateExperiment(dataSetId, experimentType, experimentValues);
              }
              
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
            Geolocation geolocation = getGeolocationSaver().saveGeolocation(variableList, null, false);
            int id = geolocation.getLocationId();
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
    public List<DataSet> getDataSetsByType(int studyId, DataSetType dataSetType) 
            throws MiddlewareQueryException {
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
    public long countExperimentsByTrialEnvironmentAndVariate(
            int trialEnvironmentId, int variateVariableId) throws MiddlewareQueryException {
        long count = 0;
        if (this.setWorkingDatabase(trialEnvironmentId)) {
            count = getExperimentDao().countByTrialEnvironmentAndVariate(
                    trialEnvironmentId, variateVariableId);
        }
        return count;
    }

    @Override
    public void addDataSetVariableType(int datasetId, VariableType variableType) 
            throws MiddlewareQueryException {
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
    public void setExperimentValue(int experimentId, int variableId, String value) 
            throws MiddlewareQueryException {
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
    public TrialEnvironments getTrialEnvironmentsInDataset(int datasetId) 
            throws MiddlewareQueryException {
    	if (this.setWorkingDatabase(datasetId)) {
	    	DmsProject study = getProjectRelationshipDao().
	    			getObjectBySubjectIdAndTypeId(datasetId, TermId.BELONGS_TO_STUDY.getId());
	        return getTrialEnvironmentBuilder().getTrialEnvironmentsInDataset(study.getProjectId(),datasetId);
    	}
    	return null;
    }

    @Override
    public Stocks getStocksInDataset(int datasetId) throws MiddlewareQueryException {
        return getStockBuilder().getStocksInDataset(datasetId);
    }

    @Override
    public long countStocks(int datasetId, int trialEnvironmentId, int variateStdVarId) 
            throws MiddlewareQueryException {
        if (this.setWorkingDatabase(datasetId)) {
            return getStockDao().countStocks(datasetId, trialEnvironmentId, variateStdVarId);
        }
        return 0;
    }

    @Override
    public long countObservations(int datasetId, int trialEnvironmentId, int variateStdVarId) 
            throws MiddlewareQueryException {
        if (this.setWorkingDatabase(datasetId)) {
            return getStockDao().countObservations(datasetId, trialEnvironmentId, variateStdVarId);
        }
        return 0;
    }

    @Override
    public DataSet findOneDataSetByType(int studyId, DataSetType dataSetType) 
            throws MiddlewareQueryException {
        List<DataSet> datasets = getDataSetsByType(studyId, dataSetType);
        if (datasets != null && !datasets.isEmpty()) {
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
    public String getLocalNameByStandardVariableId(Integer projectId, Integer standardVariableId) 
            throws MiddlewareQueryException {
        setWorkingDatabase(projectId);
        Session session = getActiveSession();

        try {

        	String sql = "select pp.value " +
                    "from projectprop pp " +
                    "inner join projectprop pp2 on pp.rank = pp2.rank and pp.project_id = pp2.project_id " +
                    "where pp.project_id = :projectId and pp2.value = :standardVariableId " + 
                    "and pp.type_id not in (pp2.value, "+ TermId.STANDARD_VARIABLE.getId() + "," + TermId.VARIABLE_DESCRIPTION.getId() + ")";


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
    public List<StudyDetails> getAllStudyDetails(Database instance, StudyType studyType) 
            throws MiddlewareQueryException {
        setWorkingDatabase(instance);
        List<StudyDetails> details = getDmsProjectDao().getAllStudyDetails(studyType);
        populateSiteAndPersonIfNecessary(details);
        return details;
    }

    @Override
    public List<StudyNode> getAllNurseryAndTrialStudyNodes() throws MiddlewareQueryException {
        List<StudyNode> studyNodes = new ArrayList<StudyNode>();
        studyNodes.addAll(getNurseryAndTrialStudyNodes(Database.LOCAL));
        studyNodes.addAll(getNurseryAndTrialStudyNodes(Database.CENTRAL));
        return studyNodes;
    }

    @Override
    public List<StudyNode> getNurseryAndTrialStudyNodes(Database instance) throws MiddlewareQueryException {
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
    public List<FieldMapInfo> getFieldMapInfoOfStudy(List<Integer> studyIdList, StudyType studyType) 
            throws MiddlewareQueryException {
        List<FieldMapInfo> fieldMapInfos = new ArrayList<FieldMapInfo>();

        for (Integer studyId : studyIdList) {
            FieldMapInfo fieldMapInfo = new FieldMapInfo();
            setWorkingDatabase(studyId);

            fieldMapInfo.setFieldbookId(studyId);
            fieldMapInfo.setFieldbookName(getDmsProjectDao().getById(studyId).getName());

            if (studyType == StudyType.T) {
                fieldMapInfo.setTrial(true);
            } else {
                fieldMapInfo.setTrial(false);
            }

            List<FieldMapDatasetInfo> fieldMapDatasetInfos =
                    getExperimentPropertyDao().getFieldMapLabels(studyId);
            fieldMapInfo.setDatasets(fieldMapDatasetInfos);

            // Set pedigree
            if (fieldMapDatasetInfos != null) {
                for (FieldMapDatasetInfo fieldMapDatasetInfo : fieldMapDatasetInfos) {
                    List<FieldMapTrialInstanceInfo> trialInstances =
                            fieldMapDatasetInfo.getTrialInstances();
                    if (trialInstances != null && !trialInstances.isEmpty()) {
                        for (FieldMapTrialInstanceInfo trialInstance : trialInstances) {
                            List<FieldMapLabel> labels = trialInstance.getFieldMapLabels();
                            for (FieldMapLabel label : labels) {
                                String pedigree = null;
                                try {
                                    pedigree = germplasmDataManager.getCrossExpansion(label.getGid(), 1);
                                } catch (MiddlewareQueryException e) {
                                    //do nothing
                                }

                                label.setPedigree(pedigree);
                            }
                        }
                    }
                }
            }

            fieldMapInfos.add(fieldMapInfo);
        }
        
        updateFieldMapInfoWithBlockInfo(fieldMapInfos);

        return fieldMapInfos;
    }

    @Override
    public void saveOrUpdateFieldmapProperties(List<FieldMapInfo> info, int userId, boolean isNew) 
            throws MiddlewareQueryException {

        if (info != null && !info.isEmpty()) {

            requireLocalDatabaseInstance();
            Session session = getCurrentSessionForLocal();
            Transaction trans = null;

            try {
                trans = session.beginTransaction();

                if (isNew) {
                    getLocdesSaver().saveLocationDescriptions(info, userId);
                }
                else {
	                getLocdesSaver().updateDeletedPlots(info, userId);
                }
                getGeolocationPropertySaver().saveFieldmapProperties(info);
                getExperimentPropertySaver().saveFieldmapProperties(info);

                trans.commit();

            } catch (Exception e) {
                rollbackTransaction(trans);
                logAndThrowException("Error encountered with saveOrUpdateFieldmapProperties(): " 
                        + e.getMessage(), e, LOG);
            }
        }

    }

    @Override
    public void saveTrialDatasetSummary(
            DmsProject project, VariableTypeList variableTypeList, 
            List<ExperimentValues> experimentValues, List<Integer> locationIds) 
                    throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        try {
            trans = session.beginTransaction();
            if (variableTypeList != null 
                    && variableTypeList.getVariableTypes() != null 
                    && !variableTypeList.getVariableTypes().isEmpty()) {
                getProjectPropertySaver().saveProjectProperties(project, variableTypeList);
            }
            if (experimentValues != null && !experimentValues.isEmpty()) {
                for (Integer locationId : locationIds) {
                    //delete phenotypes by project id and locationId
                    getPhenotypeDao().deletePhenotypesByProjectIdAndLocationId(
                            project.getProjectId(), locationId);
                }
                for (ExperimentValues exp : experimentValues) {
                    if (exp.getVariableList() != null && exp.getVariableList().size() > 0) {
                        ExperimentModel experimentModel = getExperimentDao()
                                .getExperimentByProjectIdAndLocation(
                                        project.getProjectId(), exp.getLocationId());
                        getPhenotypeSaver().savePhenotypes(experimentModel, exp.getVariableList());
                    }
                }

            }
            trans.commit();

        } catch (Exception e) {
            rollbackTransaction(trans);
            throw new MiddlewareQueryException("error in saveTrialDatasetSummary " + e.getMessage(), e);
        }
    }

    @Override
    public List<FieldMapInfo> getAllFieldMapsInBlockByTrialInstanceId(int datasetId, int geolocationId)
            throws MiddlewareQueryException {
        List<FieldMapInfo> fieldMapInfos = new ArrayList<FieldMapInfo>();
        setWorkingDatabase(datasetId);
        
        fieldMapInfos = getExperimentPropertyDao()
                .getAllFieldMapsInBlockByTrialInstanceId(datasetId, geolocationId, null);
        
        int blockId = getBlockId(fieldMapInfos);
        FieldmapBlockInfo blockInfo = locationDataManager.getBlockInformation(blockId);
        updateFieldMapWithBlockInformation(fieldMapInfos, blockInfo, true);
        
        // Filter those belonging to the given geolocationId
        for (FieldMapInfo fieldMapInfo : fieldMapInfos) {
            List<FieldMapDatasetInfo> datasetInfoList = fieldMapInfo.getDatasets();
            if (datasetInfoList != null){
                for (FieldMapDatasetInfo fieldMapDatasetInfo : datasetInfoList) {
                    List<FieldMapTrialInstanceInfo> trialInstances =
                            fieldMapDatasetInfo.getTrialInstances();
                    if (trialInstances != null && !trialInstances.isEmpty()) {
                        for (FieldMapTrialInstanceInfo trialInstance : trialInstances) {
                            List<FieldMapLabel> labels = trialInstance.getFieldMapLabels();
                            for (FieldMapLabel label : labels) {
                                String pedigree = null;
                                try {
                                    pedigree = germplasmDataManager.getCrossExpansion(label.getGid(), 1);
                                } catch (MiddlewareQueryException e) {
                                    //do nothing
                                }

                                label.setPedigree(pedigree);
                            }
                        }
                    }
                }
            }
        }

        return fieldMapInfos;
    }

    @Override
    public List<FieldMapInfo> getAllFieldMapsInBlockByBlockId(int blockId)
            throws MiddlewareQueryException {

        List<FieldMapInfo> fieldMapInfos = new ArrayList<FieldMapInfo>();
        setWorkingDatabase(Database.LOCAL);
        
        fieldMapInfos = getExperimentPropertyDao()
                .getAllFieldMapsInBlockByTrialInstanceId(0, 0, blockId);
        
        FieldmapBlockInfo blockInfo = locationDataManager.getBlockInformation(blockId);
        updateFieldMapWithBlockInformation(fieldMapInfos, blockInfo);

        return fieldMapInfos;
    }
    
    
    @Override
    public boolean isStudy(int id) throws MiddlewareQueryException {
        setWorkingDatabase(id);
        return getProjectRelationshipDao().isSubjectTypeExisting(id, TermId.STUDY_HAS_FOLDER.getId());
    }

    public boolean renameSubFolder(String newFolderName, int folderId) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();

        // check for existing folder name

        boolean isExisting = getDmsProjectDao().checkIfProjectNameIsExisting(newFolderName);
        if (isExisting) {
            throw new MiddlewareQueryException("Folder name is not unique");
        }

        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        try {
            trans = session.beginTransaction();
            DmsProject currentFolder = getDmsProjectDao().getById(folderId);
            currentFolder.setName(newFolderName);
            getDmsProjectDao().saveOrUpdate(currentFolder);
            trans.commit();
            return true;
        } catch (Exception e) {
            rollbackTransaction(trans);
            throw new MiddlewareQueryException
                    ("Error encountered with renameFolder(folderId="
                            + folderId + ", name=" + newFolderName
                            + ": " + e.getMessage(),
                            e);
        }
    }

    @Override
    public int addSubFolder(int parentFolderId, String name, String description)
            throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        DmsProject parentProject = getDmsProjectDao().getById(parentFolderId);
        if (parentProject == null) {
            throw new MiddlewareQueryException("DMS Project is not existing");
        }
        boolean isExisting = getDmsProjectDao().checkIfProjectNameIsExisting(name);
        if (isExisting) {
            throw new MiddlewareQueryException("Folder name is not unique");
        }
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;
        try {
            trans = session.beginTransaction();
            DmsProject project = getProjectSaver().saveFolder(parentFolderId, name, description);
            trans.commit();
            return project.getProjectId();
        } catch (Exception e) {
            rollbackTransaction(trans);
            throw new MiddlewareQueryException
                    ("Error encountered with addSubFolder(parentFolderId="
                            + parentFolderId + ", name=" + name
                            + ", description=" + description + "): " + e.getMessage(),
                            e);
        }
    }

    public boolean moveDmsProject(int sourceId, int targetId, boolean isAStudy) 
            throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        DmsProject source = getDmsProjectDao().getById(sourceId);
        DmsProject target = getDmsProjectDao().getById(targetId);
        if (source == null) {
            throw new MiddlewareQueryException("Source Project is not existing");
        }

        if (target == null) {
            throw new MiddlewareQueryException("Target Project is not existing");
        }

        Transaction trans = null;
        try {
            Session session = getCurrentSessionForLocal();

            trans = session.beginTransaction();

            // disassociate the source project from any parent it had previously
            getProjectRelationshipDao().deleteChildAssociation(sourceId);

            getProjectRelationshipSaver().saveProjectParentRelationship(source, targetId, isAStudy);
            trans.commit();
            return true;
        } catch (MiddlewareException e) {
            rollbackTransaction(trans);
            LOG.error(e.getMessage(), e);
            return false;
        }
    }


    @Override
    public void deleteEmptyFolder(int id) throws MiddlewareQueryException {
        requireLocalDatabaseInstance();
        DmsProjectDao dmsProjectDao = getDmsProjectDao();
        //check if folder is existing
        DmsProject project = dmsProjectDao.getById(id);
        if (project == null) {
            throw new MiddlewareQueryException("Folder is not existing");
        }
        //check if folder has no children
        List<Reference> children = dmsProjectDao.getChildrenOfFolder(id);
        if (children != null && !children.isEmpty()) {
            throw new MiddlewareQueryException("Folder is not empty");
        }

        Session session = getCurrentSessionForLocal();
        Transaction trans = null;
        try {
            trans = session.beginTransaction();
            //modify the folder name
            String name = project.getName() + "#" + Math.random();
            project.setName(name);
            dmsProjectDao.saveOrUpdate(project);
            getProjectRelationshipDao().deleteByProjectId(project.getProjectId());
            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            throw new MiddlewareQueryException
                    ("Error encountered with deleteEmptyFolder(id=" + id + "): " + e.getMessage(), e);
        }
    }

    @Override
    public DmsProject getParentFolder(int id) throws MiddlewareQueryException {
        if (id > 0) {
            requireCentralDatabaseInstance();
        } else {
            requireLocalDatabaseInstance();
        }
        DmsProject folderParentFolder = getProjectRelationshipDao()
                .getObjectBySubjectIdAndTypeId(id, TermId.HAS_PARENT_FOLDER.getId());
        DmsProject studyParentFolder = getProjectRelationshipDao()
                .getObjectBySubjectIdAndTypeId(id, TermId.STUDY_HAS_FOLDER.getId());
        if (studyParentFolder != null){
            return studyParentFolder;
        }
        return folderParentFolder;
    }

    @Override
    public DmsProject getProject(int id) throws MiddlewareQueryException {
        setWorkingDatabase(id);
        return getDmsProjectDao().getById(id);
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public List<StudyDetails> getStudyDetails(StudyType studyType, int start, int numOfRows) 
            throws MiddlewareQueryException {
        List<String> methods = Arrays.asList("countAllStudyDetails", "getAllStudyDetails");
        Object[] parameters = new Object[]{studyType};
        List<StudyDetails> details = getFromLocalAndCentralByMethod(getDmsProjectDao(), methods, start, numOfRows,
                parameters, new Class[]{StudyType.class});
        populateSiteAndPersonIfNecessary(details);
        return details;
    }

    @Override
    public List<StudyDetails> getStudyDetails(Database instance, StudyType studyType, int start, int numOfRows) throws MiddlewareQueryException {
        setWorkingDatabase(instance);
        List<StudyDetails> details = getDmsProjectDao().getAllStudyDetails(studyType, start, numOfRows);
        populateSiteAndPersonIfNecessary(details);
        return details;
    }
    
    @Override
    public StudyDetails getStudyDetails(Database instance, StudyType studyType, int studyId) throws MiddlewareQueryException {
        setWorkingDatabase(instance);
        StudyDetails studyDetails = getDmsProjectDao().getStudyDetails(studyType, studyId);
        populateSiteAnPersonIfNecessary(studyDetails);
        return studyDetails;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<StudyDetails> getNurseryAndTrialStudyDetails(int start, int numOfRows) throws MiddlewareQueryException {
        List<String> methods = Arrays.asList("countAllNurseryAndTrialStudyDetails", "getAllNurseryAndTrialStudyDetails");
        Object[] parameters = new Object[]{};
        List<StudyDetails> list = getFromLocalAndCentralByMethod(getDmsProjectDao(), methods, start, numOfRows,
                parameters, new Class[]{});
        populateSiteAndPersonIfNecessary(list);
        return list;
    }

    @Override
    public List<StudyDetails> getNurseryAndTrialStudyDetails(Database instance, int start, int numOfRows) throws MiddlewareQueryException {
        setWorkingDatabase(instance);
        List<StudyDetails> list = getDmsProjectDao().getAllNurseryAndTrialStudyDetails(start, numOfRows);
        populateSiteAndPersonIfNecessary(list);
        return list;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public List<StudyDetails> getAllStudyDetails(StudyType studyType) throws MiddlewareQueryException {
        List<StudyDetails> list = new ArrayList<StudyDetails>();
        if (setWorkingDatabase(Database.LOCAL)) {
            List localList = getDmsProjectDao().getAllStudyDetails(studyType);
            if (localList != null) {
                list.addAll(localList);
            }
        }
        if (setWorkingDatabase(Database.CENTRAL)) {
            List centralList = getDmsProjectDao().getAllStudyDetails(studyType);
            if (centralList != null) {
                list.addAll(centralList);
            }
        }
        
        populateSiteAndPersonIfNecessary(list);
        
        return list;
    }

    @Override
    public long countAllStudyDetails(StudyType studyType)
            throws MiddlewareQueryException {
        long count = 0;
        if (setWorkingDatabase(Database.LOCAL)) {
            count += getDmsProjectDao().countAllStudyDetails(studyType);
        }
        if (setWorkingDatabase(Database.CENTRAL)) {
            count += getDmsProjectDao().countAllStudyDetails(studyType);
        }
        return count;
    }

    @Override
    public long countStudyDetails(Database instance, StudyType studyType)
            throws MiddlewareQueryException {
        long count = 0;
        if (setWorkingDatabase(Database.LOCAL)) {
            count += getDmsProjectDao().countAllStudyDetails(studyType);
        }
        return count;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public List<StudyDetails> getAllNurseryAndTrialStudyDetails()
            throws MiddlewareQueryException {
        List<StudyDetails> list = new ArrayList<StudyDetails>();
        if (setWorkingDatabase(Database.LOCAL)) {
            List localList = getDmsProjectDao().getAllNurseryAndTrialStudyDetails();
            if (localList != null) {
                list.addAll(localList);
            }
        }
        if (setWorkingDatabase(Database.CENTRAL)) {
            List centralList = getDmsProjectDao().getAllNurseryAndTrialStudyDetails();
            if (centralList != null) {
                list.addAll(centralList);
            }
        }
        
        populateSiteAndPersonIfNecessary(list);
        
        return list;
    }

    @Override
    public long countAllNurseryAndTrialStudyDetails()
            throws MiddlewareQueryException {
        long count = 0;
        if (setWorkingDatabase(Database.LOCAL)) {
            count += getDmsProjectDao().countAllNurseryAndTrialStudyDetails();
        }
        if (setWorkingDatabase(Database.CENTRAL)) {
            count += getDmsProjectDao().countAllNurseryAndTrialStudyDetails();
        }
        return count;
    }

    @Override
    public long countNurseryAndTrialStudyDetails(Database instance)
            throws MiddlewareQueryException {
        long count = 0;
        if (setWorkingDatabase(Database.LOCAL)) {
            count += getDmsProjectDao().countAllNurseryAndTrialStudyDetails();
        }
        return count;
    }

    @Override
    public List<FolderReference> getFolderTree() throws MiddlewareQueryException {
        return getFolderBuilder().buildFolderTree();
    }
    
    @Override
    public int countPlotsWithRecordedVariatesInDataset(int dataSetId, List<Integer> variateIds) throws MiddlewareQueryException {
        if (setWorkingDatabase(Database.LOCAL)) {
            return getPhenotypeDao().countRecordedVariatesOfStudy(dataSetId, variateIds);
        }
        return 0;
    }
    
    @Override
    public String getGeolocationPropValue(Database instance, int stdVarId, int studyId) throws MiddlewareQueryException {
        setWorkingDatabase(instance);
        return getGeolocationPropertyDao().getGeolocationPropValue(stdVarId, studyId);
    }
    
    @Override
    public String getFolderNameById(Integer folderId) throws MiddlewareQueryException {
        setWorkingDatabase(folderId);
        DmsProject currentFolder = getDmsProjectDao().getById(folderId);
        return currentFolder.getName();
    }
    
    @Override
    public boolean checkIfStudyHasMeasurementData(int datasetId, List<Integer> variateIds) throws MiddlewareQueryException {
        setWorkingDatabase(datasetId);
        if (getPhenotypeDao().countVariatesDataOfStudy(datasetId, variateIds) > 0) {
            return true;
        } 
        return false;
    }
    
    @Override
    public int countVariatesWithData(int datasetId, List<Integer> variateIds) throws MiddlewareQueryException {
    	int variatesWithDataCount = 0;
    	if (variateIds != null && !variateIds.isEmpty()) {
	        setWorkingDatabase(datasetId);
	        Map<Integer, Integer> map = getPhenotypeDao().countVariatesDataOfStudy(datasetId);
	        for (Integer variateId : variateIds) {
	        	Integer count = map.get(variateId);
	        	if (count != null && count > 0) {
	        		variatesWithDataCount++;
	        	}
	        }
    	}
    	return variatesWithDataCount;
    }
    
    private void populateSiteAnPersonIfNecessary(StudyDetails detail) throws MiddlewareQueryException {
    	if (detail != null) {
			if (detail.getSiteName() != null && !"".equals(detail.getSiteName().trim()) && detail.getSiteId() != null) {
				setWorkingDatabase(detail.getSiteId());
				Location loc = getLocationDao().getById(detail.getSiteId());
				if (loc != null) {
					detail.setSiteName(loc.getLname());
				}
			}    	
			if (detail.getPiName() != null && !"".equals(detail.getPiName().trim()) && detail.getPiId() != null) {
				setWorkingDatabase(detail.getPiId());
				Person person = getPersonDao().getById(detail.getPiId());
				if (person != null) {
					detail.setPiName(person.getDisplayName());
				}
			}
    	}
    }
    
    private void populateSiteAndPersonIfNecessary(List<StudyDetails> studyDetails) throws MiddlewareQueryException {
    	if (studyDetails != null && !studyDetails.isEmpty()) {
	    	List<Integer> centralSite = new ArrayList<Integer>();
	    	List<Integer> localSite = new ArrayList<Integer>();
	    	List<Integer> centralPerson = new ArrayList<Integer>();
	    	List<Integer> localPerson = new ArrayList<Integer>();
	    	
	    	for (StudyDetails detail : studyDetails) {
	    		if (detail.getSiteId() != null) {
	    			if (detail.getSiteId() > 0) {
	    				centralSite.add(detail.getSiteId());
	    			}
	    			else {
	    				localSite.add(detail.getSiteId());
	    			}
	    		}
	    		if (detail.getPiId() != null) {
	    			if (detail.getPiId() > 0) {
	    				centralPerson.add(detail.getPiId());
	    			}
	    			else {
	    				localPerson.add(detail.getPiId());
	    			}
	    		}
	    	}
	    	
	    	Map<Integer, String> siteMap = new HashMap<Integer, String>();
	    	Map<Integer, String> personMap = new HashMap<Integer, String>();
	    	
	    	if (!centralSite.isEmpty()) {
	    		setWorkingDatabase(Database.CENTRAL);
	    		siteMap.putAll(getLocationDao().getLocationNamesByLocationIDs(centralSite));
	    	}
	    	if (!localSite.isEmpty()) {
	    		setWorkingDatabase(Database.LOCAL);
	    		siteMap.putAll(getLocationDao().getLocationNamesByLocationIDs(localSite));
	    	}
	    	if (!centralPerson.isEmpty()) {
	    		setWorkingDatabase(Database.CENTRAL);
	    		personMap.putAll(getPersonDao().getPersonNamesByPersonIds(centralPerson));
	    	}
	    	if (!localPerson.isEmpty()) {
	    		setWorkingDatabase(Database.LOCAL);
	    		personMap.putAll(getPersonDao().getPersonNamesByPersonIds(localPerson));
	    	}
	    	
	    	for (StudyDetails detail : studyDetails) {
	    		if (detail.getSiteId() != null) {
	    			detail.setSiteName(siteMap.get(detail.getSiteId()));
	    		}
	    		if (detail.getPiId() != null) {
	    			detail.setPiName(personMap.get(detail.getPiId()));
	    		}
	    	}
    	}
    }
    
    private Integer getBlockId(List<FieldMapInfo> infos) {
    	if (infos != null) { 
    		for (FieldMapInfo info : infos) {
    			if (info.getDatasets() != null) {
    				for (FieldMapDatasetInfo dataset : info.getDatasets()) {
    					if (dataset.getTrialInstances() != null) {
    						for (FieldMapTrialInstanceInfo trial : dataset.getTrialInstances()) {
    							return trial.getBlockId();
    						}
    					}
    				}
    			}
    		}
    	}
    	return null;
    }
    
    private void updateFieldMapWithBlockInformation(List<FieldMapInfo> infos, FieldmapBlockInfo blockInfo) throws MiddlewareQueryException {
    	updateFieldMapWithBlockInformation(infos, blockInfo, false);
    }
    
    protected void updateFieldMapWithBlockInformation(List<FieldMapInfo> infos, FieldmapBlockInfo blockInfo, boolean isGetLocation) throws MiddlewareQueryException {
    	Map<Integer, String> locationMap = new HashMap<Integer, String>();
    	if (infos != null) {
    		for (FieldMapInfo info : infos) {
    			if (info.getDatasets() != null) {
    				for (FieldMapDatasetInfo dataset : info.getDatasets()) {
    					if (dataset.getTrialInstances() != null) {
    						for (FieldMapTrialInstanceInfo trial : dataset.getTrialInstances()) {
                            	if (trial.getBlockId() != null) {
                            		blockInfo = locationDataManager.getBlockInformation(trial.getBlockId());
                            		trial.updateBlockInformation(blockInfo);
                            	}
    							if (isGetLocation) {
	    							trial.setLocationName(getLocationName(locationMap, trial.getLocationId()));
                                    trial.setSiteName(trial.getLocationName());
	    							trial.setFieldName(getLocationName(locationMap, trial.getFieldId()));
	    							trial.setBlockName(getLocationName(locationMap, trial.getBlockId()));
    							}
    						}
    					}
    				}
    			}
    		}
    	}
    }
    
    private void updateFieldMapInfoWithBlockInfo(List<FieldMapInfo> fieldMapInfos) throws MiddlewareQueryException {
        updateFieldMapWithBlockInformation(fieldMapInfos, null, true);
    }

    private String getLocationName(Map<Integer, String> locationMap, Integer id) throws MiddlewareQueryException {
    	if (id != null) {
	    	String name = locationMap.get(id);
	    	if (name != null) {
	    		return name;
	    	}
	    	setWorkingDatabase(id);
	    	Location location = getLocationDAO().getById(id);
	    	if (location != null) {
	    		locationMap.put(id, location.getLname());
	    		return location.getLname();
	    	}
    	}
    	return null;
    }
    
   @Override 
   public List<Object[]> getPhenotypeIdsByLocationAndPlotNo(int projectId, int locationId, List<Integer> plotNos, List<Integer> cvTermIds) throws MiddlewareQueryException{
	   setWorkingDatabase(projectId);
	   return getPhenotypeDao().getPhenotypeIdsByLocationAndPlotNo(projectId, locationId, plotNos, cvTermIds);
	  
   }
   
   @Override 
   public List<Object[]> getPhenotypeIdsByLocationAndPlotNo(int projectId, int locationId, Integer plotNo, List<Integer> cvTermIds) throws MiddlewareQueryException{
	   setWorkingDatabase(projectId);
	   return getPhenotypeDao().getPhenotypeIdsByLocationAndPlotNo(projectId, locationId, plotNo, cvTermIds);
	  
   }
   
   @Override
   public void saveOrUpdatePhenotypeOutliers(List<PhenotypeOutlier> phenotyleOutliers)
			throws MiddlewareQueryException {
	   
   	  	 requireLocalDatabaseInstance();
         Session session = getCurrentSessionForLocal();
         Transaction trans = null;
         PhenotypeOutlierDao phenotypeOutlierDao = getPhenotypeOutlierDao();
         int i = 0;
         
         try {
             trans = session.beginTransaction();
             
             for (PhenotypeOutlier phenotypeOutlier : phenotyleOutliers){
            	 
            	 i++;
            	 
            	 PhenotypeOutlier existingPhenotypeOutlier = phenotypeOutlierDao.getPhenotypeOutlierByPhenotypeId(phenotypeOutlier.getPhenotypeId());
            	
            	 if (existingPhenotypeOutlier != null){
            		 existingPhenotypeOutlier.setValue(phenotypeOutlier.getValue());
            		 phenotypeOutlierDao.saveOrUpdate(existingPhenotypeOutlier);
            	 }else{
            		 phenotypeOutlier.setPhenotypeOutlierId(phenotypeOutlierDao.getNegativeId("phenotypeOutlierId"));
            		 phenotypeOutlierDao.saveOrUpdate(phenotypeOutlier);
            	 }
            	 
            	 if (i % DatabaseBroker.JDBC_BATCH_SIZE == 0){ // batch save
            		 phenotypeOutlierDao.flush();
            		 phenotypeOutlierDao.clear();
                 }
            	 
            
             }
             
             phenotypeOutlierDao.flush();
             phenotypeOutlierDao.clear();
             
             trans.commit();

         } catch (Exception e) {
             rollbackTransaction(trans);
             throw new MiddlewareQueryException("error in savePhenotypeOutlier " + e.getMessage(), e);
         }
		
	}

	
	@Override
	public Boolean containsAtLeast2CommonEntriesWithValues(int projectId, int locationId)
			throws MiddlewareQueryException {
		
		this.setWorkingDatabase(projectId);
		return getPhenotypeDao().containsAtLeast2CommonEntriesWithValues(projectId, locationId);
	}

	public void setLocationDataManager(LocationDataManager locationDataManager) {
		this.locationDataManager = locationDataManager;
	}

	@Override
	public StudyType getStudyType(int studyId) throws MiddlewareQueryException {
		setWorkingDatabase(studyId);
		return getDmsProjectDao().getStudyType(studyId);
	}
}
