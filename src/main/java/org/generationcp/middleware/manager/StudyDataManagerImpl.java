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
import org.generationcp.middleware.service.PedigreeServiceImpl;
import org.generationcp.middleware.util.CrossExpansionRule;
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
    private PedigreeServiceImpl pedigreeService;
    
    private LocationDataManager locationDataManager;

    private static final Logger LOG = LoggerFactory.getLogger(StudyDataManagerImpl.class);

    public StudyDataManagerImpl() {
    }
    
    public StudyDataManagerImpl(HibernateSessionProvider sessionProvider, String databaseName) {
		super(sessionProvider, databaseName);
		germplasmDataManager = new GermplasmDataManagerImpl(sessionProvider, databaseName);
		locationDataManager = new LocationDataManagerImpl(sessionProvider);
		pedigreeService = new PedigreeServiceImpl(sessionProvider, databaseName);
	}

    public StudyDataManagerImpl(HibernateSessionProvider sessionProvider) {
        super(sessionProvider);
        germplasmDataManager = new GermplasmDataManagerImpl(sessionProvider);
        locationDataManager = new LocationDataManagerImpl(sessionProvider);
        pedigreeService = new PedigreeServiceImpl(sessionProvider);
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
    public Integer getStudyIdByNameAndProgramUUID(String studyName, String programUUID) throws MiddlewareQueryException {
        return getDmsProjectDao().getProjectIdByNameAndProgramUUID(studyName, programUUID, TermId.IS_STUDY);
    }

    @Override
    public boolean checkIfProjectNameIsExistingInProgram(String name,String programUUID) throws MiddlewareQueryException {
        return getDmsProjectDao().checkIfProjectNameIsExistingInProgram(name,programUUID);
    }

    @Override
    public List<FolderReference> getRootFolders(String programUUID) throws MiddlewareQueryException {
        return getDmsProjectDao().getRootFolders(programUUID);
    }

    @Override
    public List<Reference> getChildrenOfFolder(int folderId, String programUUID) throws MiddlewareQueryException {
        return getDmsProjectDao().getChildrenOfFolder(folderId, programUUID);
    }

    @Override
    public List<DatasetReference> getDatasetReferences(int studyId) throws MiddlewareQueryException {
        return getDmsProjectDao().getDatasetNodesByStudyId(studyId);
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
            return new StudyResultSetByParentFolder((ParentFolderStudyQueryFilter) filter, numOfRows, this.sessionProvider);
        } else if (filter instanceof GidStudyQueryFilter) {
            return new StudyResultSetByGid((GidStudyQueryFilter) filter, numOfRows, this.sessionProvider);
        } else if (filter instanceof BrowseStudyQueryFilter) {
            return new StudyResultSetByNameStartDateSeasonCountry((BrowseStudyQueryFilter) filter, numOfRows, this.sessionProvider);
        }
        return null;
    }

    @Override
    public StudyReference addStudy(int parentFolderId, VariableTypeList variableTypeList, 
            StudyValues studyValues, String programUUID) throws MiddlewareQueryException {
        Session session = getCurrentSession();
        Transaction trans = null;

        try {
            trans = session.beginTransaction();
            DmsProject project = getStudySaver().saveStudy(parentFolderId, variableTypeList, studyValues, true, programUUID);
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
    		DatasetValues datasetValues, String programUUID) throws MiddlewareQueryException {
        Session session = getCurrentSession();
        Transaction trans = null;

        try {
            trans = session.beginTransaction();
            DmsProject datasetProject = getDatasetProjectSaver()
                    .addDataSet(studyId, variableTypeList, datasetValues, programUUID);
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
        Session session = getCurrentSession();
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
        Session session = getCurrentSession();
        Transaction trans = null;

        try {
            trans = session.beginTransaction();
            getExperimentModelSaver().addOrUpdateExperiment(dataSetId, experimentType, experimentValues);
            trans.commit();

        } catch (Exception e) {
            rollbackTransaction(trans);
            throw new MiddlewareQueryException("error in addOrUpdateExperiment " + e.getMessage(), e);
        }
    }
    
    @Override
	public void addOrUpdateExperiment(int dataSetId,
			ExperimentType experimentType,
			List<ExperimentValues> experimentValuesList)
			throws MiddlewareQueryException {
          Session session = getCurrentSession();
          Transaction trans = null;

          try {
              trans = session.beginTransaction();
              
              for (ExperimentValues experimentValues : experimentValuesList){
            	  getExperimentModelSaver().addOrUpdateExperiment(dataSetId, experimentType, experimentValues);
              }
              
              trans.commit();

          } catch (Exception e) {
              rollbackTransaction(trans);
              throw new MiddlewareQueryException("error in addOrUpdateExperiment " + e.getMessage(), e);
          }
		
	}

    @Override
    public int addTrialEnvironment(VariableList variableList) throws MiddlewareQueryException {
        Session session = getCurrentSession();
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
        Session session = getCurrentSession();
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
        count = getExperimentDao().countByTrialEnvironmentAndVariate(
                trialEnvironmentId, variateVariableId);
        return count;
    }

    @Override
    public void addDataSetVariableType(int datasetId, VariableType variableType) 
            throws MiddlewareQueryException {
        Session session = getCurrentSession();
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
        Session session = getCurrentSession();
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
    	DmsProject study = getProjectRelationshipDao().
    			getObjectBySubjectIdAndTypeId(datasetId, TermId.BELONGS_TO_STUDY.getId());
        return getTrialEnvironmentBuilder().getTrialEnvironmentsInDataset(study.getProjectId(),datasetId);
    }

    @Override
    public Stocks getStocksInDataset(int datasetId) throws MiddlewareQueryException {
        return getStockBuilder().getStocksInDataset(datasetId);
    }

    @Override
    public long countStocks(int datasetId, int trialEnvironmentId, int variateStdVarId) 
            throws MiddlewareQueryException {
        return getStockDao().countStocks(datasetId, trialEnvironmentId, variateStdVarId);
    }

    @Override
    public long countObservations(int datasetId, int trialEnvironmentId, int variateStdVarId) 
            throws MiddlewareQueryException {
        return getStockDao().countObservations(datasetId, trialEnvironmentId, variateStdVarId);
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
        Session session = getCurrentSession();
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
        Session session = getCurrentSession();
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
    public List<StudyNode> getAllNurseryAndTrialStudyNodes(String programUUID) throws MiddlewareQueryException {
    	return getDmsProjectDao().getAllNurseryAndTrialStudyNodes(programUUID);
    }

    @Override
    public long countProjectsByVariable(int variableId) throws MiddlewareQueryException {
        return getDmsProjectDao().countByVariable(variableId);
    }

    @Override
    public long countExperimentsByVariable(int variableId, int storedInId) throws MiddlewareQueryException {
        return getExperimentDao().countByObservedVariable(variableId, storedInId);
    }

    @Override
    public List<FieldMapInfo> getFieldMapInfoOfStudy(List<Integer> studyIdList, StudyType studyType, CrossExpansionRule crossExpansionRule) 
            throws MiddlewareQueryException {
        List<FieldMapInfo> fieldMapInfos = new ArrayList<FieldMapInfo>();

        for (Integer studyId : studyIdList) {
            FieldMapInfo fieldMapInfo = new FieldMapInfo();

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
            
            if (fieldMapDatasetInfos != null) {
            	setPedigree(fieldMapDatasetInfos, crossExpansionRule);
            }
            
            fieldMapInfos.add(fieldMapInfo);
        }
        
        updateFieldMapInfoWithBlockInfo(fieldMapInfos);

        return fieldMapInfos;
    }

    private void setPedigree(List<FieldMapDatasetInfo> fieldMapDatasetInfos, CrossExpansionRule crossExpansionRule) {
    	for (FieldMapDatasetInfo fieldMapDatasetInfo : fieldMapDatasetInfos) {
            List<FieldMapTrialInstanceInfo> trialInstances =
                    fieldMapDatasetInfo.getTrialInstances();
            if (trialInstances == null || trialInstances.isEmpty()) {
            	continue;
            }
            for (FieldMapTrialInstanceInfo trialInstance : trialInstances) {
                List<FieldMapLabel> labels = trialInstance.getFieldMapLabels();
                for (FieldMapLabel label : labels) {
                    setPedigree(label, crossExpansionRule);
                }
            }
        }
	}

	private void setPedigree(FieldMapLabel label, CrossExpansionRule crossExpansionRule) {
		String pedigree = null;
        try {
            pedigree = pedigreeService.getCrossExpansion(label.getGid(), crossExpansionRule);
        } catch (MiddlewareQueryException e) {
            LOG.error(e.getMessage(),e);
        }
        label.setPedigree(pedigree);
	}

	@Override
    public void saveOrUpdateFieldmapProperties(List<FieldMapInfo> info, int userId, boolean isNew) 
            throws MiddlewareQueryException {

        if (info != null && !info.isEmpty()) {

            Session session = getCurrentSession();
            Transaction trans = null;

            try {
                trans = session.beginTransaction();

                if (isNew) {
                    getLocdesSaver().saveLocationDescriptions(info, userId);
                } else {
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
        Session session = getCurrentSession();
        Transaction trans = null;

        try {
            trans = session.beginTransaction();
            if (variableTypeList != null 
                    && variableTypeList.getVariableTypes() != null 
                    && !variableTypeList.getVariableTypes().isEmpty()) {
                getProjectPropertySaver().saveProjectProperties(project, variableTypeList);
            }
            if (experimentValues != null && !experimentValues.isEmpty()) {
            	updateExperimentValues(experimentValues,project.getProjectId(),locationIds);
            }
            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            throw new MiddlewareQueryException("error in saveTrialDatasetSummary " + e.getMessage(), e);
        }
    }

    private void updateExperimentValues(
    		List<ExperimentValues> experimentValues,
    		Integer projectId,List<Integer> locationIds) throws MiddlewareQueryException {
    	for (Integer locationId : locationIds) {
            //delete phenotypes by project id and locationId
            getPhenotypeDao().deletePhenotypesByProjectIdAndLocationId(
            		projectId, locationId);
        }
        for (ExperimentValues exp : experimentValues) {
            if (exp.getVariableList() != null && !exp.getVariableList().isEmpty()) {
                ExperimentModel experimentModel = getExperimentDao()
                        .getExperimentByProjectIdAndLocation(
                        		projectId, exp.getLocationId());
                getPhenotypeSaver().savePhenotypes(experimentModel, exp.getVariableList());
            }
        }
	}

	@Override
    public List<FieldMapInfo> getAllFieldMapsInBlockByTrialInstanceId(int datasetId, int geolocationId, CrossExpansionRule crossExpansionRule)
            throws MiddlewareQueryException {
        List<FieldMapInfo> fieldMapInfos = new ArrayList<FieldMapInfo>();
        
        fieldMapInfos = getExperimentPropertyDao()
                .getAllFieldMapsInBlockByTrialInstanceId(datasetId, geolocationId, null);
        
        int blockId = getBlockId(fieldMapInfos);
        FieldmapBlockInfo blockInfo = locationDataManager.getBlockInformation(blockId);
        updateFieldMapWithBlockInformation(fieldMapInfos, blockInfo, true);
        
        // Filter those belonging to the given geolocationId
        for (FieldMapInfo fieldMapInfo : fieldMapInfos) {
            List<FieldMapDatasetInfo> datasetInfoList = fieldMapInfo.getDatasets();
            if (datasetInfoList != null){
            	setPedigree(datasetInfoList, crossExpansionRule);
            }
        }

        return fieldMapInfos;
    }

    @Override
    public List<FieldMapInfo> getAllFieldMapsInBlockByBlockId(int blockId)
            throws MiddlewareQueryException {

        List<FieldMapInfo> fieldMapInfos = new ArrayList<FieldMapInfo>();
        
        fieldMapInfos = getExperimentPropertyDao()
                .getAllFieldMapsInBlockByTrialInstanceId(0, 0, blockId);
        
        FieldmapBlockInfo blockInfo = locationDataManager.getBlockInformation(blockId);
        updateFieldMapWithBlockInformation(fieldMapInfos, blockInfo);

        return fieldMapInfos;
    }
    
    
    @Override
    public boolean isStudy(int id) throws MiddlewareQueryException {
        return getProjectRelationshipDao().isSubjectTypeExisting(id, TermId.STUDY_HAS_FOLDER.getId());
    }

    public boolean renameSubFolder(String newFolderName, int folderId, String programUUID) throws MiddlewareQueryException {

        // check for existing folder name
        boolean isExisting = getDmsProjectDao().checkIfProjectNameIsExistingInProgram
        		(newFolderName,programUUID);
        if (isExisting) {
            throw new MiddlewareQueryException("Folder name is not unique");
        }

        Session session = getCurrentSession();
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
    public int addSubFolder(int parentFolderId, String name, String description, String programUUID)
            throws MiddlewareQueryException {
        DmsProject parentProject = getDmsProjectDao().getById(parentFolderId);
        if (parentProject == null) {
            throw new MiddlewareQueryException("DMS Project is not existing");
        }
        boolean isExisting = getDmsProjectDao().
        		checkIfProjectNameIsExistingInProgram(name,programUUID);
        if (isExisting) {
            throw new MiddlewareQueryException("Folder name is not unique");
        }
        Session session = getCurrentSession();
        Transaction trans = null;
        try {
            trans = session.beginTransaction();
            DmsProject project = getProjectSaver().saveFolder(parentFolderId, name, description, programUUID);
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
            Session session = getCurrentSession();

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
    public void deleteEmptyFolder(int id, String programUUID) throws MiddlewareQueryException {
        DmsProjectDao dmsProjectDao = getDmsProjectDao();
        //check if folder is existing
        DmsProject project = dmsProjectDao.getById(id);
        if (project == null) {
            throw new MiddlewareQueryException("Folder is not existing");
        }
        //check if folder has no children
        List<Reference> children = dmsProjectDao.getChildrenOfFolder(id, programUUID);
        if (children != null && !children.isEmpty()) {
            throw new MiddlewareQueryException("Folder is not empty");
        }

        Session session = getCurrentSession();
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
        return getDmsProjectDao().getById(id);
    }

    @Override
    public List<StudyDetails> getStudyDetails(StudyType studyType, String programUUID, int start, int numOfRows) throws MiddlewareQueryException {
        List<StudyDetails> details = getDmsProjectDao().getAllStudyDetails(studyType, programUUID, start, numOfRows);
        populateSiteAndPersonIfNecessary(details);
        return details;
    }
    
    @Override
    public StudyDetails getStudyDetails(StudyType studyType, int studyId) throws MiddlewareQueryException {
        StudyDetails studyDetails = getDmsProjectDao().getStudyDetails(studyType, studyId);
        populateSiteAnPersonIfNecessary(studyDetails);
        return studyDetails;
    }

    @Override
    public List<StudyDetails> getNurseryAndTrialStudyDetails(String programUUID, int start, int numOfRows) throws MiddlewareQueryException {
        List<StudyDetails> list = getDmsProjectDao().getAllNurseryAndTrialStudyDetails(programUUID, start, numOfRows);
        populateSiteAndPersonIfNecessary(list);
        return list;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public List<StudyDetails> getAllStudyDetails(StudyType studyType, String programUUID) throws MiddlewareQueryException {
        List<StudyDetails> list = new ArrayList<StudyDetails>();
        List localList = getDmsProjectDao().getAllStudyDetails(studyType,programUUID);
        if (localList != null) {
            list.addAll(localList);
        }
        populateSiteAndPersonIfNecessary(list);
        return list;
    }

    @Override
    public long countAllStudyDetails(StudyType studyType, String programUUID) throws MiddlewareQueryException {
        long count = 0;
        count += getDmsProjectDao().countAllStudyDetails(studyType,programUUID);
        return count;
    }

    @Override
    public long countStudyDetails(StudyType studyType, String programUUID)
            throws MiddlewareQueryException {
        long count = 0;
        count += getDmsProjectDao().countAllStudyDetails(studyType,programUUID);
        return count;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public List<StudyDetails> getAllNurseryAndTrialStudyDetails(String programUUID)
            throws MiddlewareQueryException {
        List<StudyDetails> list = new ArrayList<StudyDetails>();
        List localList = getDmsProjectDao().getAllNurseryAndTrialStudyDetails(programUUID);
        if (localList != null) {
            list.addAll(localList);
        }
        populateSiteAndPersonIfNecessary(list);
        return list;
    }

    @Override
    public long countAllNurseryAndTrialStudyDetails(String programUUID) throws MiddlewareQueryException {
        long count = 0;
        count += getDmsProjectDao().countAllNurseryAndTrialStudyDetails(programUUID);
        return count;
    }

    @Override
    public List<FolderReference> getFolderTree() throws MiddlewareQueryException {
        return getFolderBuilder().buildFolderTree();
    }
    
    @Override
    public int countPlotsWithRecordedVariatesInDataset(int dataSetId, List<Integer> variateIds) throws MiddlewareQueryException {
        return getPhenotypeDao().countRecordedVariatesOfStudy(dataSetId, variateIds);
    }
    
    @Override
    public String getGeolocationPropValue(int stdVarId, int studyId) throws MiddlewareQueryException {
        return getGeolocationPropertyDao().getGeolocationPropValue(stdVarId, studyId);
    }
    
    @Override
    public String getFolderNameById(Integer folderId) throws MiddlewareQueryException {
        DmsProject currentFolder = getDmsProjectDao().getById(folderId);
        return currentFolder.getName();
    }
    
    @Override
    public boolean checkIfStudyHasMeasurementData(int datasetId, List<Integer> variateIds) throws MiddlewareQueryException {
        if (getPhenotypeDao().countVariatesDataOfStudy(datasetId, variateIds) > 0) {
            return true;
        } 
        return false;
    }
    
    @Override
    public int countVariatesWithData(int datasetId, List<Integer> variateIds) throws MiddlewareQueryException {
    	int variatesWithDataCount = 0;
    	if (variateIds != null && !variateIds.isEmpty()) {
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
				Location loc = getLocationDao().getById(detail.getSiteId());
				if (loc != null) {
					detail.setSiteName(loc.getLname());
				}
			}    	
			if (detail.getPiName() != null && !"".equals(detail.getPiName().trim()) && detail.getPiId() != null) {
				Person person = getPersonDao().getById(detail.getPiId());
				if (person != null) {
					detail.setPiName(person.getDisplayName());
				}
			}
    	}
    }
    
    private void populateSiteAndPersonIfNecessary(List<StudyDetails> studyDetails) throws MiddlewareQueryException {
    	if (studyDetails != null && !studyDetails.isEmpty()) {
	    	Map<Integer, String> siteMap = new HashMap<Integer, String>();
	    	Map<Integer, String> personMap = new HashMap<Integer, String>();
	    	retrieveSitesAndPersonsFromStudyDetails(studyDetails,siteMap,personMap);
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
    
    private void retrieveSitesAndPersonsFromStudyDetails(
    		List<StudyDetails> studyDetails,
    		Map<Integer, String> siteMap, 
    		Map<Integer, String> personMap) throws MiddlewareQueryException {
    	List<Integer> siteIds = new ArrayList<Integer>();
    	List<Integer> personIds = new ArrayList<Integer>();
    	for (StudyDetails detail : studyDetails) {
    		if (detail.getSiteId() != null) {
    			siteIds.add(detail.getSiteId());
    		}
    		if (detail.getPiId() != null) {
    			personIds.add(detail.getPiId());
    		}
    	}
    	if (!siteIds.isEmpty()) {
    		siteMap.putAll(getLocationDao().getLocationNamesByLocationIDs(siteIds));
    	}
    	if (!personIds.isEmpty()) {
    		personMap.putAll(getPersonDao().getPersonNamesByPersonIds(personIds));
    	}
	}

	private Integer getBlockId(List<FieldMapInfo> infos) {
    	if (infos == null) { 
    		return null;
    	}
		for (FieldMapInfo info : infos) {
			if (info == null || info.getDatasets() == null) {
				continue;
			}
			for (FieldMapDatasetInfo dataset : info.getDatasets()) {
				Integer blockId = getBlockId(dataset);
				if(blockId!=null) {
					return blockId;
				}
			}
		}
    	return null;
    }
    
    private Integer getBlockId(FieldMapDatasetInfo dataset) {
    	if (dataset!=null && dataset.getTrialInstances() != null) {
    		for (FieldMapTrialInstanceInfo trial : dataset.getTrialInstances()) {
    			return trial.getBlockId();
    		}
		}
		return null;
	}

	private void updateFieldMapWithBlockInformation(List<FieldMapInfo> infos, FieldmapBlockInfo blockInfo) throws MiddlewareQueryException {
    	updateFieldMapWithBlockInformation(infos, blockInfo, false);
    }
    
    protected void updateFieldMapWithBlockInformation(List<FieldMapInfo> infos, FieldmapBlockInfo blockInfo, boolean isGetLocation) throws MiddlewareQueryException {
    	if (infos == null) {
    		return;
    	}
    	Map<Integer, String> locationMap = new HashMap<Integer, String>();
    	for (FieldMapInfo info : infos) {
			if (info != null && info.getDatasets() != null) {
				for (FieldMapDatasetInfo dataset : info.getDatasets()) {
					updateFieldMapTrialInstanceInfo(dataset,isGetLocation,locationMap);
				}
			}
		}
    }
    
    private void updateFieldMapTrialInstanceInfo(
			FieldMapDatasetInfo dataset, boolean isGetLocation,
			Map<Integer, String> locationMap) throws MiddlewareQueryException {
    	if (dataset != null && dataset.getTrialInstances() != null) {
			for (FieldMapTrialInstanceInfo trial : dataset.getTrialInstances()) {
				if (trial.getBlockId() != null) {
		    		trial.updateBlockInformation(locationDataManager.getBlockInformation(trial.getBlockId()));
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

	private void updateFieldMapInfoWithBlockInfo(List<FieldMapInfo> fieldMapInfos) throws MiddlewareQueryException {
        updateFieldMapWithBlockInformation(fieldMapInfos, null, true);
    }

    private String getLocationName(Map<Integer, String> locationMap, Integer id) throws MiddlewareQueryException {
    	if (id != null) {
	    	String name = locationMap.get(id);
	    	if (name != null) {
	    		return name;
	    	}
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
	   return getPhenotypeDao().getPhenotypeIdsByLocationAndPlotNo(projectId, locationId, plotNos, cvTermIds);
   }
   
   @Override 
   public List<Object[]> getPhenotypeIdsByLocationAndPlotNo(int projectId, int locationId, Integer plotNo, List<Integer> cvTermIds) throws MiddlewareQueryException{
	   return getPhenotypeDao().getPhenotypeIdsByLocationAndPlotNo(projectId, locationId, plotNo, cvTermIds);
   }
   
   @Override
   public void saveOrUpdatePhenotypeOutliers(List<PhenotypeOutlier> phenotyleOutliers)
			throws MiddlewareQueryException {
	   
         Session session = getCurrentSession();
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
            		 phenotypeOutlier.setPhenotypeOutlierId(phenotypeOutlierDao.getNextId("phenotypeOutlierId"));
            		 phenotypeOutlierDao.saveOrUpdate(phenotypeOutlier);
            	 }
            	 if (i % DatabaseBroker.JDBC_BATCH_SIZE == 0){ 
            		 // batch save
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
		
		return getPhenotypeDao().containsAtLeast2CommonEntriesWithValues(projectId, locationId);
	}

	public void setLocationDataManager(LocationDataManager locationDataManager) {
		this.locationDataManager = locationDataManager;
	}

	@Override
	public StudyType getStudyType(int studyId) throws MiddlewareQueryException {
		return getDmsProjectDao().getStudyType(studyId);
	}
	
	@Override
	public void deleteProgramStudies(String programUUID) throws MiddlewareQueryException {
		List<Integer> projectIds = getDmsProjectDao().getAllProgramStudiesAndFolders(programUUID);
		Session session = getCurrentSession();
        Transaction trans = null;
        
        try {
        	trans = session.beginTransaction(); 
        	for (Integer projectId : projectIds) {
        		getStudyDestroyer().deleteStudy(projectId);
        	}    
        	trans.commit();
        } catch (Exception e) {
             rollbackTransaction(trans);
             logAndThrowException("Error encountered with saveMeasurementRows(): " + e.getMessage(), e, LOG);
        }
	}

	@Override
	public void updateVariableOrdering(int datasetId, List<Integer> variableIds) throws MiddlewareQueryException {
        Session session = getCurrentSession();
        Transaction trans = null;

        try {
            trans = session.beginTransaction();
            getProjectPropertySaver().updateVariablesRanking(datasetId, variableIds);
            
            trans.commit();
        } catch (Exception e) {
            rollbackTransaction(trans);
            throw new MiddlewareQueryException("Error in updateVariableOrdering " + e.getMessage(), e);
        }
	}
}
