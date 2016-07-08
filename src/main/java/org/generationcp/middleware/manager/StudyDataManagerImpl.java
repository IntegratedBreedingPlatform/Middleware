/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.dao.dms.DmsProjectDao;
import org.generationcp.middleware.dao.dms.PhenotypeOutlierDao;
import org.generationcp.middleware.domain.dms.DMSVariableType;
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
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.fieldbook.FieldMapDatasetInfo;
import org.generationcp.middleware.domain.fieldbook.FieldMapInfo;
import org.generationcp.middleware.domain.fieldbook.FieldMapLabel;
import org.generationcp.middleware.domain.fieldbook.FieldMapTrialInstanceInfo;
import org.generationcp.middleware.domain.fieldbook.FieldmapBlockInfo;
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
import org.generationcp.middleware.service.api.PedigreeService;
import org.generationcp.middleware.service.pedigree.PedigreeFactory;
import org.generationcp.middleware.util.CrossExpansionProperties;
import org.generationcp.middleware.util.PlotUtil;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class StudyDataManagerImpl extends DataManager implements StudyDataManager {

	private static final Logger LOG = LoggerFactory.getLogger(StudyDataManagerImpl.class);
	private PedigreeService pedigreeService;
	private LocationDataManager locationDataManager;

	public StudyDataManagerImpl() {
	}

	public StudyDataManagerImpl(HibernateSessionProvider sessionProvider, String databaseName) {
		super(sessionProvider, databaseName);
		this.locationDataManager = new LocationDataManagerImpl(sessionProvider);
		this.pedigreeService = this.getPedigreeService();
	}

	public StudyDataManagerImpl(HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
		this.locationDataManager = new LocationDataManagerImpl(sessionProvider);
		this.pedigreeService = this.getPedigreeService();
	}

	private PedigreeService getPedigreeService() {
		if (ManagerFactory.getCurrentManagerFactoryThreadLocal().get() != null) {
			return ManagerFactory.getCurrentManagerFactoryThreadLocal().get().getPedigreeService();
		}
		// we will just return default pedigree service
		return PedigreeFactory.getPedigreeService(this.sessionProvider, null, null);
	}

	@Override
	public Study getStudy(int studyId) throws MiddlewareException {
		return this.getStudyBuilder().createStudy(studyId);
	}

	@Override
	public Study getStudy(int studyId, boolean hasVariableType) throws MiddlewareException {
		return this.getStudyBuilder().createStudy(studyId, hasVariableType);
	}

	@Override
	public Integer getStudyIdByNameAndProgramUUID(String studyName, String programUUID) throws MiddlewareQueryException {
		return this.getDmsProjectDao().getProjectIdByNameAndProgramUUID(studyName, programUUID, TermId.IS_STUDY);
	}

	@Override
	public boolean checkIfProjectNameIsExistingInProgram(String name, String programUUID) throws MiddlewareQueryException {
		return this.getDmsProjectDao().checkIfProjectNameIsExistingInProgram(name, programUUID);
	}

	@Override
	public List<Reference> getRootFolders(String programUUID, List<StudyType> studyTypes) {
		return this.getDmsProjectDao().getRootFolders(programUUID, studyTypes);
	}

	@Override
	public List<Reference> getChildrenOfFolder(int folderId, String programUUID, List<StudyType> studyTypes) throws MiddlewareQueryException {
		return this.getDmsProjectDao().getChildrenOfFolder(folderId, programUUID, studyTypes);
	}

	@Override
	public List<DatasetReference> getDatasetReferences(int studyId) throws MiddlewareQueryException {
		return this.getDmsProjectDao().getDatasetNodesByStudyId(studyId);
	}

	@Override
	public DataSet getDataSet(int dataSetId) throws MiddlewareException {
		return this.getDataSetBuilder().build(dataSetId);
	}

	@Override
	public VariableTypeList getAllStudyFactors(int studyId) throws MiddlewareException {
		return this.getStudyFactorBuilder().build(studyId);
	}

	@Override
	public VariableTypeList getAllStudyVariates(int studyId) throws MiddlewareException {
		return this.getStudyVariateBuilder().build(studyId);
	}

	@Override
	public StudyResultSet searchStudies(StudyQueryFilter filter, int numOfRows) throws MiddlewareQueryException {
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
	public StudyReference addStudy(int parentFolderId, VariableTypeList variableTypeList, StudyValues studyValues, String programUUID)
			throws MiddlewareQueryException {
		
		

		try {

			DmsProject project = this.getStudySaver().saveStudy(parentFolderId, variableTypeList, studyValues, true, programUUID);

			return new StudyReference(project.getProjectId(), project.getName(), project.getDescription());

		} catch (Exception e) {

			throw new MiddlewareQueryException("Error encountered with addStudy(folderId=" + parentFolderId + ", variableTypeList="
					+ variableTypeList + ", studyValues=" + studyValues + "): " + e.getMessage(), e);
		}

	}

	@Override
	public DatasetReference addDataSet(int studyId, VariableTypeList variableTypeList, DatasetValues datasetValues, String programUUID)
			throws MiddlewareQueryException {

		try {

			DmsProject datasetProject = this.getDatasetProjectSaver().addDataSet(studyId, variableTypeList, datasetValues, programUUID);

			return new DatasetReference(datasetProject.getProjectId(), datasetProject.getName(), datasetProject.getDescription());

		} catch (Exception e) {

			throw new MiddlewareQueryException("error in addDataSet " + e.getMessage(), e);
		}
	}

	@Override
	public List<Experiment> getExperiments(int dataSetId, int start, int numRows) throws MiddlewareException {
		VariableTypeList variableTypes = this.getDataSetBuilder().getVariableTypes(dataSetId);
		return this.getExperimentBuilder().build(dataSetId, PlotUtil.getAllPlotTypes(), start, numRows, variableTypes);
	}

	@Override
	public List<Experiment> getExperimentsWithTrialEnvironment(int trialDataSetId, int dataSetId, int start, int numRows)
			throws MiddlewareException {
		VariableTypeList trialVariableTypes = this.getDataSetBuilder().getVariableTypes(trialDataSetId);
		VariableTypeList variableTypes = this.getDataSetBuilder().getVariableTypes(dataSetId);

		variableTypes.addAll(trialVariableTypes);

		return this.getExperimentBuilder().build(dataSetId, PlotUtil.getAllPlotTypes(), start, numRows, variableTypes);
	}

	@Override
	public List<Experiment> getExperiments(int dataSetId, int start, int numOfRows, VariableTypeList varTypeList)
			throws MiddlewareException {
		if (varTypeList == null) {
			return this.getExperiments(dataSetId, start, numOfRows);
		} else {
			return this.getExperimentBuilder().build(dataSetId, PlotUtil.getAllPlotTypes(), start, numOfRows, varTypeList);
		}
	}

	@Override
	public long countExperiments(int dataSetId) throws MiddlewareQueryException {
		return this.getExperimentBuilder().count(dataSetId);
	}

	@Override
	public void addExperiment(int dataSetId, ExperimentType experimentType, ExperimentValues experimentValues)
			throws MiddlewareQueryException {

		try {

			this.getExperimentModelSaver().addExperiment(dataSetId, experimentType, experimentValues);

		} catch (Exception e) {

			throw new MiddlewareQueryException("error in addExperiment " + e.getMessage(), e);
		}
	}

	@Override
	public void addOrUpdateExperiment(int dataSetId, ExperimentType experimentType, ExperimentValues experimentValues)
			throws MiddlewareQueryException {
		
		

		try {

			this.getExperimentModelSaver().addOrUpdateExperiment(dataSetId, experimentType, experimentValues);

		} catch (Exception e) {

			throw new MiddlewareQueryException("error in addOrUpdateExperiment " + e.getMessage(), e);
		}
	}

	@Override
	public void addOrUpdateExperiment(int dataSetId, ExperimentType experimentType, List<ExperimentValues> experimentValuesList)
			throws MiddlewareQueryException {

		try {

			for (ExperimentValues experimentValues : experimentValuesList) {
				this.getExperimentModelSaver().addOrUpdateExperiment(dataSetId, experimentType, experimentValues);
			}

		} catch (Exception e) {

			throw new MiddlewareQueryException("error in addOrUpdateExperiment " + e.getMessage(), e);
		}

	}

	@Override
	public int addTrialEnvironment(VariableList variableList) throws MiddlewareQueryException {

		try {

			Geolocation geolocation = this.getGeolocationSaver().saveGeolocation(variableList, null, false);
			int id = geolocation.getLocationId();

			return id;

		} catch (Exception e) {

			throw new MiddlewareQueryException("error in addTrialEnvironment " + e.getMessage(), e);
		}
	}

	@Override
	public int addStock(VariableList variableList) throws MiddlewareQueryException {

		try {

			int id = this.getStockSaver().saveStock(variableList);

			return id;

		} catch (Exception e) {

			throw new MiddlewareQueryException("error in addStock " + e.getMessage(), e);
		}
	}

	@Override
	public List<DataSet> getDataSetsByType(int studyId, DataSetType dataSetType) throws MiddlewareException {

		List<DmsProject> datasetProjects =
				this.getDmsProjectDao().getDataSetsByStudyAndProjectProperty(studyId, TermId.DATASET_TYPE.getId(),
						String.valueOf(dataSetType.getId()));
		List<DataSet> datasets = new ArrayList<DataSet>();

		for (DmsProject datasetProject : datasetProjects) {
			datasets.add(this.getDataSetBuilder().build(datasetProject.getProjectId()));
		}

		return datasets;
	}

	@Override
	public long countExperimentsByTrialEnvironmentAndVariate(int trialEnvironmentId, int variateVariableId) throws MiddlewareQueryException {
		long count = 0;
		count = this.getExperimentDao().countByTrialEnvironmentAndVariate(trialEnvironmentId, variateVariableId);
		return count;
	}

	@Override
	public void addDataSetVariableType(int datasetId, DMSVariableType variableType) throws MiddlewareQueryException {

		try {

			this.getDatasetProjectSaver().addDatasetVariableType(datasetId, variableType);

		} catch (Exception e) {

			throw new MiddlewareQueryException("error in addDataSetVariableType " + e.getMessage(), e);
		}
	}

	@Override
	public TrialEnvironments getTrialEnvironmentsInDataset(int datasetId) throws MiddlewareException {
		DmsProject study = this.getProjectRelationshipDao().getObjectBySubjectIdAndTypeId(datasetId, TermId.BELONGS_TO_STUDY.getId());
		return this.getTrialEnvironmentBuilder().getTrialEnvironmentsInDataset(study.getProjectId(), datasetId);
	}

	@Override
	public Stocks getStocksInDataset(int datasetId) throws MiddlewareException {
		return this.getStockBuilder().getStocksInDataset(datasetId);
	}

	@Override
	public long countStocks(int datasetId, int trialEnvironmentId, int variateStdVarId) throws MiddlewareQueryException {
		return this.getStockDao().countStocks(datasetId, trialEnvironmentId, variateStdVarId);
	}

	@Override
	public long countObservations(int datasetId, int trialEnvironmentId, int variateStdVarId) throws MiddlewareQueryException {
		return this.getStockDao().countObservations(datasetId, trialEnvironmentId, variateStdVarId);
	}

	@Override
	public DataSet findOneDataSetByType(int studyId, DataSetType dataSetType) throws MiddlewareException {
		List<DataSet> datasets = this.getDataSetsByType(studyId, dataSetType);
		if (datasets != null && !datasets.isEmpty()) {
			return datasets.get(0);
		}
		return null;
	}
	
	@Override
	public DatasetReference findOneDataSetReferenceByType(int studyId, DataSetType type) {
		List<DmsProject> datasetProjects =
				this.getDmsProjectDao().getDataSetsByStudyAndProjectProperty(studyId, TermId.DATASET_TYPE.getId(),
						String.valueOf(type.getId()));
		if (datasetProjects != null && !datasetProjects.isEmpty()) {
			DmsProject dataSetProject = datasetProjects.get(0);
			return new DatasetReference(dataSetProject.getProjectId(), dataSetProject.getName(), dataSetProject.getDescription());
		}
		return null;
	}

	@Override
	public void deleteDataSet(int datasetId) throws MiddlewareQueryException {

		try {

			this.getDataSetDestroyer().deleteDataSet(datasetId);

		} catch (Exception e) {

			throw new MiddlewareQueryException("error in deleteDataSet " + e.getMessage(), e);
		}
	}

	@Override
	public void deleteExperimentsByLocation(int datasetId, int locationId) throws MiddlewareQueryException {

		try {

			this.getDataSetDestroyer().deleteExperimentsByLocation(datasetId, locationId);

		} catch (Exception e) {

			throw new MiddlewareQueryException("error in deleteExperimentsByLocation " + e.getMessage(), e);
		}
	}

	@Override
	public String getLocalNameByStandardVariableId(Integer projectId, Integer standardVariableId) throws MiddlewareQueryException {
		Session session = this.getActiveSession();

		try {

			String sql =
					"select pp.value " + "from projectprop pp "
							+ "inner join projectprop pp2 on pp.rank = pp2.rank and pp.project_id = pp2.project_id "
							+ "where pp.project_id = :projectId and pp2.value = :standardVariableId "
							+ "and pp.type_id not in (pp2.value, " + TermId.STANDARD_VARIABLE.getId() + ","
							+ TermId.VARIABLE_DESCRIPTION.getId() + ")";

			Query query = session.createSQLQuery(sql);
			query.setParameter("projectId", projectId);
			query.setParameter("standardVariableId", standardVariableId);

			return (String) query.uniqueResult();

		} catch (HibernateException e) {
			this.logAndThrowException("Error at getLocalNameByStandardVariableId :" + e.getMessage(), e);
		}
		return null;
	}

	@Override
	public List<StudyNode> getAllNurseryAndTrialStudyNodes(String programUUID) throws MiddlewareQueryException {
		return this.getDmsProjectDao().getAllNurseryAndTrialStudyNodes(programUUID);
	}

	@Override
	public long countProjectsByVariable(int variableId) throws MiddlewareQueryException {
		return this.getDmsProjectDao().countByVariable(variableId);
	}

	@Override
	public long countExperimentsByVariable(int variableId, int variableTypeId) throws MiddlewareQueryException {
		return this.getExperimentDao().countByObservedVariable(variableId, variableTypeId);
	}

	@Override
	public List<FieldMapInfo> getFieldMapInfoOfStudy(List<Integer> studyIdList, StudyType studyType,
			CrossExpansionProperties crossExpansionProperties, boolean pedigreeRequired) throws MiddlewareQueryException {
		List<FieldMapInfo> fieldMapInfos = new ArrayList<FieldMapInfo>();
		final Map<Integer, String> pedigreeStringMap = new HashMap<>();
		for (Integer studyId : studyIdList) {
			FieldMapInfo fieldMapInfo = new FieldMapInfo();

			fieldMapInfo.setFieldbookId(studyId);
			fieldMapInfo.setFieldbookName(this.getDmsProjectDao().getById(studyId).getName());

			if (studyType == StudyType.T) {
				fieldMapInfo.setTrial(true);
			} else {
				fieldMapInfo.setTrial(false);
			}

			List<FieldMapDatasetInfo> fieldMapDatasetInfos = this.getExperimentPropertyDao().getFieldMapLabels(studyId);
			fieldMapInfo.setDatasets(fieldMapDatasetInfos);

			if (fieldMapDatasetInfos != null && pedigreeRequired) {
				this.setPedigree(fieldMapDatasetInfos, crossExpansionProperties, pedigreeStringMap);
			}

			fieldMapInfos.add(fieldMapInfo);
		}

		this.updateFieldMapInfoWithBlockInfo(fieldMapInfos);

		return fieldMapInfos;
	}

	private void setPedigree(List<FieldMapDatasetInfo> fieldMapDatasetInfos, CrossExpansionProperties crossExpansionProperties,
			Map<Integer, String> pedigreeStringMap) {
		//TODO: Caching of the pedigree string is just a temporary fix. This must be properly fixed.
		for (FieldMapDatasetInfo fieldMapDatasetInfo : fieldMapDatasetInfos) {
			List<FieldMapTrialInstanceInfo> trialInstances = fieldMapDatasetInfo.getTrialInstances();
			if (trialInstances == null || trialInstances.isEmpty()) {
				continue;
			}
			for (FieldMapTrialInstanceInfo trialInstance : trialInstances) {
				List<FieldMapLabel> labels = trialInstance.getFieldMapLabels();
				for (FieldMapLabel label : labels) {
					this.setPedigree(label, crossExpansionProperties, pedigreeStringMap);
				}
			}
		}
	}

	private void setPedigree(FieldMapLabel label, CrossExpansionProperties crossExpansionProperties, Map<Integer, String> pedigreeStringMap) {
	
		final Integer gid = label.getGid();
		final String cachedPedigreeString = pedigreeStringMap.get(gid);
		if (StringUtils.isNotBlank(cachedPedigreeString)){
			label.setPedigree(cachedPedigreeString);
		} else {
			String pedigree = this.pedigreeService.getCrossExpansion(gid, crossExpansionProperties);
			label.setPedigree(pedigree);
			pedigreeStringMap.put(gid, pedigree);
		}
}

	@Override
	public void saveOrUpdateFieldmapProperties(List<FieldMapInfo> info, int userId, boolean isNew) throws MiddlewareQueryException {

		if (info != null && !info.isEmpty()) {

			try {

				if (isNew) {
					this.getLocdesSaver().saveLocationDescriptions(info, userId);
				} else {
					this.getLocdesSaver().updateDeletedPlots(info, userId);
				}
				this.getGeolocationPropertySaver().saveFieldmapProperties(info);
				this.getExperimentPropertySaver().saveFieldmapProperties(info);

			} catch (Exception e) {

				throw new MiddlewareQueryException("Error encountered with saveOrUpdateFieldmapProperties(): " + e.getMessage(), e);
			}
		}

	}

	@Override
	public void saveTrialDatasetSummary(DmsProject project, VariableTypeList variableTypeList, List<ExperimentValues> experimentValues,
			List<Integer> locationIds) throws MiddlewareQueryException {

		try {

			if (variableTypeList != null && variableTypeList.getVariableTypes() != null && !variableTypeList.getVariableTypes().isEmpty()) {
				this.getProjectPropertySaver().saveProjectProperties(project, variableTypeList);
			}
			if (experimentValues != null && !experimentValues.isEmpty()) {
				this.updateExperimentValues(experimentValues, project.getProjectId(), locationIds);
			}

		} catch (Exception e) {

			throw new MiddlewareQueryException("error in saveTrialDatasetSummary " + e.getMessage(), e);
		}
	}

	private void updateExperimentValues(List<ExperimentValues> experimentValues, Integer projectId, List<Integer> locationIds)
			throws MiddlewareQueryException {

		for (ExperimentValues exp : experimentValues) {
			if (exp.getVariableList() != null && !exp.getVariableList().isEmpty()) {
				ExperimentModel experimentModel =
						this.getExperimentDao().getExperimentByProjectIdAndLocation(projectId, exp.getLocationId());
				this.getPhenotypeSaver().savePhenotypes(experimentModel, exp.getVariableList());
			}
		}
	}

	@Override
	public List<FieldMapInfo> getAllFieldMapsInBlockByTrialInstanceId(int datasetId, int geolocationId,
			CrossExpansionProperties crossExpansionProperties) throws MiddlewareQueryException {
		List<FieldMapInfo> fieldMapInfos = new ArrayList<FieldMapInfo>();

		fieldMapInfos = this.getExperimentPropertyDao().getAllFieldMapsInBlockByTrialInstanceId(datasetId, geolocationId, null);

		int blockId = this.getBlockId(fieldMapInfos);
		FieldmapBlockInfo blockInfo = this.locationDataManager.getBlockInformation(blockId);
		this.updateFieldMapWithBlockInformation(fieldMapInfos, blockInfo, true);
		final Map<Integer, String> pedigreeStringMap = new HashMap<>();
		// Filter those belonging to the given geolocationId
		for (FieldMapInfo fieldMapInfo : fieldMapInfos) {
			List<FieldMapDatasetInfo> datasetInfoList = fieldMapInfo.getDatasets();
			if (datasetInfoList != null) {
				this.setPedigree(datasetInfoList, crossExpansionProperties, pedigreeStringMap);
			}
		}

		return fieldMapInfos;
	}

	@Override
	public List<FieldMapInfo> getAllFieldMapsInBlockByBlockId(int blockId) throws MiddlewareQueryException {

		List<FieldMapInfo> fieldMapInfos = new ArrayList<FieldMapInfo>();

		fieldMapInfos = this.getExperimentPropertyDao().getAllFieldMapsInBlockByTrialInstanceId(0, 0, blockId);

		FieldmapBlockInfo blockInfo = this.locationDataManager.getBlockInformation(blockId);
		this.updateFieldMapWithBlockInformation(fieldMapInfos, blockInfo);

		return fieldMapInfos;
	}

	@Override
	public boolean isStudy(int id) throws MiddlewareQueryException {
		return this.getProjectRelationshipDao().isSubjectTypeExisting(id, TermId.STUDY_HAS_FOLDER.getId());
	}

	@Override
	public boolean renameSubFolder(String newFolderName, int folderId, String programUUID) throws MiddlewareQueryException {

		// check for existing folder name
		boolean isExisting = this.getDmsProjectDao().checkIfProjectNameIsExistingInProgram(newFolderName, programUUID);
		if (isExisting) {
			throw new MiddlewareQueryException("Folder name is not unique");
		}

		try {

			DmsProject currentFolder = this.getDmsProjectDao().getById(folderId);
			currentFolder.setName(newFolderName);
			this.getDmsProjectDao().saveOrUpdate(currentFolder);

			return true;
		} catch (Exception e) {

			throw new MiddlewareQueryException("Error encountered with renameFolder(folderId=" + folderId + ", name=" + newFolderName
					+ ": " + e.getMessage(), e);
		}
	}

	@Override
	public int addSubFolder(int parentFolderId, String name, String description, String programUUID) throws MiddlewareQueryException {
		DmsProject parentProject = this.getDmsProjectDao().getById(parentFolderId);
		if (parentProject == null) {
			throw new MiddlewareQueryException("DMS Project is not existing");
		}
		boolean isExisting = this.getDmsProjectDao().checkIfProjectNameIsExistingInProgram(name, programUUID);
		if (isExisting) {
			throw new MiddlewareQueryException("Folder name is not unique");
		}

		try {

			DmsProject project = this.getProjectSaver().saveFolder(parentFolderId, name, description, programUUID);

			return project.getProjectId();
		} catch (Exception e) {

			throw new MiddlewareQueryException("Error encountered with addSubFolder(parentFolderId=" + parentFolderId + ", name=" + name
					+ ", description=" + description + "): " + e.getMessage(), e);
		}
	}

	@Override
	public boolean moveDmsProject(int sourceId, int targetId, boolean isAStudy) throws MiddlewareQueryException {

		DmsProject source = this.getDmsProjectDao().getById(sourceId);
		DmsProject target = this.getDmsProjectDao().getById(targetId);
		if (source == null) {
			throw new MiddlewareQueryException("Source Project is not existing");
		}

		if (target == null) {
			throw new MiddlewareQueryException("Target Project is not existing");
		}

		try {

			// disassociate the source project from any parent it had previously
			this.getProjectRelationshipDao().deleteChildAssociation(sourceId);

			this.getProjectRelationshipSaver().saveProjectParentRelationship(source, targetId, isAStudy);

			return true;
		} catch (MiddlewareException e) {

			StudyDataManagerImpl.LOG.error(e.getMessage(), e);
			return false;
		}
	}

	@Override
	public void deleteEmptyFolder(int id, String programUUID) throws MiddlewareQueryException {
		DmsProjectDao dmsProjectDao = this.getDmsProjectDao();
		// check if folder is existing
		DmsProject project = dmsProjectDao.getById(id);
		if (project == null) {
			throw new MiddlewareQueryException("Folder is not existing");
		}
		// check if folder has no children
		List<Reference> children = dmsProjectDao.getChildrenOfFolder(id, programUUID, StudyType.nurseriesAndTrials());
		if (children != null && !children.isEmpty()) {
			throw new MiddlewareQueryException("Folder is not empty");
		}

		try {

			// modify the folder name
			String name = project.getName() + "#" + Math.random();
			project.setName(name);
			dmsProjectDao.saveOrUpdate(project);
			this.getProjectRelationshipDao().deleteByProjectId(project.getProjectId());

		} catch (Exception e) {

			throw new MiddlewareQueryException("Error encountered with deleteEmptyFolder(id=" + id + "): " + e.getMessage(), e);
		}
	}

	@Override
	public DmsProject getParentFolder(int id) throws MiddlewareQueryException {

		DmsProject folderParentFolder =
				this.getProjectRelationshipDao().getObjectBySubjectIdAndTypeId(id, TermId.HAS_PARENT_FOLDER.getId());
		DmsProject studyParentFolder = this.getProjectRelationshipDao().getObjectBySubjectIdAndTypeId(id, TermId.STUDY_HAS_FOLDER.getId());
		if (studyParentFolder != null) {
			return studyParentFolder;
		}
		return folderParentFolder;
	}

	@Override
	public DmsProject getProject(int id) throws MiddlewareQueryException {
		return this.getDmsProjectDao().getById(id);
	}

	@Override
	public List<StudyDetails> getStudyDetails(StudyType studyType, String programUUID, int start, int numOfRows)
			throws MiddlewareQueryException {
		List<StudyDetails> details = this.getDmsProjectDao().getAllStudyDetails(studyType, programUUID, start, numOfRows);
		this.populateSiteAndPersonIfNecessary(details);
		return details;
	}

	@Override
	public StudyDetails getStudyDetails(StudyType studyType, int studyId) throws MiddlewareQueryException {
		StudyDetails studyDetails = this.getDmsProjectDao().getStudyDetails(studyType, studyId);
		this.populateSiteAnPersonIfNecessary(studyDetails);
		return studyDetails;
	}

	@Override
	public List<StudyDetails> getNurseryAndTrialStudyDetails(String programUUID, int start, int numOfRows) throws MiddlewareQueryException {
		List<StudyDetails> list = this.getDmsProjectDao().getAllNurseryAndTrialStudyDetails(programUUID, start, numOfRows);
		this.populateSiteAndPersonIfNecessary(list);
		return list;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	public List<StudyDetails> getAllStudyDetails(StudyType studyType, String programUUID) throws MiddlewareQueryException {
		List<StudyDetails> list = new ArrayList<StudyDetails>();
		List localList = this.getDmsProjectDao().getAllStudyDetails(studyType, programUUID);
		if (localList != null) {
			list.addAll(localList);
		}
		this.populateSiteAndPersonIfNecessary(list);
		return list;
	}

	@Override
	public long countAllStudyDetails(StudyType studyType, String programUUID) throws MiddlewareQueryException {
		long count = 0;
		count += this.getDmsProjectDao().countAllStudyDetails(studyType, programUUID);
		return count;
	}

	@Override
	public long countStudyDetails(StudyType studyType, String programUUID) throws MiddlewareQueryException {
		long count = 0;
		count += this.getDmsProjectDao().countAllStudyDetails(studyType, programUUID);
		return count;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	public List<StudyDetails> getAllNurseryAndTrialStudyDetails(String programUUID) throws MiddlewareQueryException {
		List<StudyDetails> list = new ArrayList<StudyDetails>();
		List localList = this.getDmsProjectDao().getAllNurseryAndTrialStudyDetails(programUUID);
		if (localList != null) {
			list.addAll(localList);
		}
		this.populateSiteAndPersonIfNecessary(list);
		return list;
	}

	@Override
	public long countAllNurseryAndTrialStudyDetails(String programUUID) throws MiddlewareQueryException {
		long count = 0;
		count += this.getDmsProjectDao().countAllNurseryAndTrialStudyDetails(programUUID);
		return count;
	}

	@Override
	public List<FolderReference> getFolderTree() throws MiddlewareQueryException {
		return this.getFolderBuilder().buildFolderTree();
	}
	
	@Override
	public List<FolderReference> getAllFolders() {
		return this.getDmsProjectDao().getAllFolders();
	}

	@Override
	public int countPlotsWithRecordedVariatesInDataset(int dataSetId, List<Integer> variateIds) throws MiddlewareQueryException {
		return this.getPhenotypeDao().countRecordedVariatesOfStudy(dataSetId, variateIds);
	}

	@Override
	public String getGeolocationPropValue(int stdVarId, int studyId) throws MiddlewareQueryException {
		return this.getGeolocationPropertyDao().getGeolocationPropValue(stdVarId, studyId);
	}

	@Override
	public String getFolderNameById(Integer folderId) throws MiddlewareQueryException {
		DmsProject currentFolder = this.getDmsProjectDao().getById(folderId);
		return currentFolder.getName();
	}

	@Override
	public boolean checkIfStudyHasMeasurementData(int datasetId, List<Integer> variateIds) throws MiddlewareQueryException {
		return this.getPhenotypeDao().countVariatesDataOfStudy(datasetId, variateIds) > 0;
	}

	@Override
	public int countVariatesWithData(int datasetId, List<Integer> variateIds) throws MiddlewareQueryException {
		int variatesWithDataCount = 0;
		if (variateIds != null && !variateIds.isEmpty()) {
			Map<Integer, Integer> map = this.getPhenotypeDao().countVariatesDataOfStudy(datasetId);
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
				Location loc = this.getLocationDao().getById(detail.getSiteId());
				if (loc != null) {
					detail.setSiteName(loc.getLname());
				}
			}
			if (detail.getPiName() != null && !"".equals(detail.getPiName().trim()) && detail.getPiId() != null) {
				Person person = this.getPersonDao().getById(detail.getPiId());
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
			this.retrieveSitesAndPersonsFromStudyDetails(studyDetails, siteMap, personMap);
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

	private void retrieveSitesAndPersonsFromStudyDetails(List<StudyDetails> studyDetails, Map<Integer, String> siteMap,
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
			siteMap.putAll(this.getLocationDao().getLocationNamesByLocationIDs(siteIds));
		}
		if (!personIds.isEmpty()) {
			personMap.putAll(this.getPersonDao().getPersonNamesByPersonIds(personIds));
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
				Integer blockId = this.getBlockId(dataset);
				if (blockId != null) {
					return blockId;
				}
			}
		}
		return null;
	}

	private Integer getBlockId(FieldMapDatasetInfo dataset) {
		if (dataset != null && dataset.getTrialInstances() != null) {
			for (FieldMapTrialInstanceInfo trial : dataset.getTrialInstances()) {
				return trial.getBlockId();
			}
		}
		return null;
	}

	private void updateFieldMapWithBlockInformation(List<FieldMapInfo> infos, FieldmapBlockInfo blockInfo) throws MiddlewareQueryException {
		this.updateFieldMapWithBlockInformation(infos, blockInfo, false);
	}

	protected void updateFieldMapWithBlockInformation(List<FieldMapInfo> infos, FieldmapBlockInfo blockInfo, boolean isGetLocation)
			throws MiddlewareQueryException {
		if (infos == null) {
			return;
		}
		Map<Integer, String> locationMap = new HashMap<Integer, String>();
		for (FieldMapInfo info : infos) {
			if (info != null && info.getDatasets() != null) {
				for (FieldMapDatasetInfo dataset : info.getDatasets()) {
					this.updateFieldMapTrialInstanceInfo(dataset, isGetLocation, locationMap);
				}
			}
		}
	}

	private void updateFieldMapTrialInstanceInfo(FieldMapDatasetInfo dataset, boolean isGetLocation, Map<Integer, String> locationMap)
			throws MiddlewareQueryException {
		if (dataset != null && dataset.getTrialInstances() != null) {
			for (FieldMapTrialInstanceInfo trial : dataset.getTrialInstances()) {
				if (trial.getBlockId() != null) {
					trial.updateBlockInformation(this.locationDataManager.getBlockInformation(trial.getBlockId()));
				}
				if (isGetLocation) {
					trial.setLocationName(this.getLocationName(locationMap, trial.getLocationId()));
					trial.setSiteName(trial.getLocationName());
					trial.setFieldName(this.getLocationName(locationMap, trial.getFieldId()));
					trial.setBlockName(this.getLocationName(locationMap, trial.getBlockId()));
				}
			}
		}
	}

	private void updateFieldMapInfoWithBlockInfo(List<FieldMapInfo> fieldMapInfos) throws MiddlewareQueryException {
		this.updateFieldMapWithBlockInformation(fieldMapInfos, null, true);
	}

	private String getLocationName(Map<Integer, String> locationMap, Integer id) throws MiddlewareQueryException {
		if (id != null) {
			String name = locationMap.get(id);
			if (name != null) {
				return name;
			}
			Location location = this.getLocationDAO().getById(id);
			if (location != null) {
				locationMap.put(id, location.getLname());
				return location.getLname();
			}
		}
		return null;
	}

	@Override
	public List<Object[]> getPhenotypeIdsByLocationAndPlotNo(int projectId, int locationId, List<Integer> plotNos, List<Integer> cvTermIds)
			throws MiddlewareQueryException {
		return this.getPhenotypeDao().getPhenotypeIdsByLocationAndPlotNo(projectId, locationId, plotNos, cvTermIds);
	}

	@Override
	public List<Object[]> getPhenotypeIdsByLocationAndPlotNo(int projectId, int locationId, Integer plotNo, List<Integer> cvTermIds)
			throws MiddlewareQueryException {
		return this.getPhenotypeDao().getPhenotypeIdsByLocationAndPlotNo(projectId, locationId, plotNo, cvTermIds);
	}

	@Override
	public void saveOrUpdatePhenotypeOutliers(List<PhenotypeOutlier> phenotyleOutliers) throws MiddlewareQueryException {

		PhenotypeOutlierDao phenotypeOutlierDao = this.getPhenotypeOutlierDao();
		try {

			for (PhenotypeOutlier phenotypeOutlier : phenotyleOutliers) {
				PhenotypeOutlier existingPhenotypeOutlier =
						phenotypeOutlierDao.getPhenotypeOutlierByPhenotypeId(phenotypeOutlier.getPhenotypeId());

				if (existingPhenotypeOutlier != null) {
					existingPhenotypeOutlier.setValue(phenotypeOutlier.getValue());
					phenotypeOutlierDao.saveOrUpdate(existingPhenotypeOutlier);
				} else {
					phenotypeOutlierDao.saveOrUpdate(phenotypeOutlier);
				}
			}

		} catch (Exception e) {
			throw new MiddlewareQueryException("error in savePhenotypeOutlier " + e.getMessage(), e);
		}

	}

	@Override
	public Boolean containsAtLeast2CommonEntriesWithValues(int projectId, int locationId, int germplasmTermId)
			throws MiddlewareQueryException {

		return this.getPhenotypeDao().containsAtLeast2CommonEntriesWithValues(projectId, locationId, germplasmTermId);
	}

	public void setLocationDataManager(LocationDataManager locationDataManager) {
		this.locationDataManager = locationDataManager;
	}

	@Override
	public StudyType getStudyType(int studyId) throws MiddlewareQueryException {
		return this.getDmsProjectDao().getStudyType(studyId);
	}

	@Override
	public void deleteProgramStudies(String programUUID) throws MiddlewareQueryException {
		List<Integer> projectIds = this.getDmsProjectDao().getAllProgramStudiesAndFolders(programUUID);

		try {
			for (Integer projectId : projectIds) {
				this.getStudyDestroyer().deleteStudy(projectId);
			}
		} catch (Exception e) {
			throw new MiddlewareQueryException("Error encountered with saveMeasurementRows(): " + e.getMessage(), e);
		}
	}

	@Override
	public void updateVariableOrdering(int datasetId, List<Integer> variableIds) throws MiddlewareQueryException {

		try {
			this.getProjectPropertySaver().updateVariablesRanking(datasetId, variableIds);

		} catch (Exception e) {
			throw new MiddlewareQueryException("Error in updateVariableOrdering " + e.getMessage(), e);
		}
	}

	@Override
	public Integer getGeolocationIdByProjectIdAndTrialInstanceNumber(int projectId, String trialInstanceNumber)
			throws MiddlewareQueryException {
		return this.getExperimentProjectDao().getGeolocationIdByProjectIdAndTrialInstanceNumber(projectId, trialInstanceNumber);
	}

	@Override
	public String getTrialInstanceNumberByGeolocationId(int geolocationId) throws MiddlewareQueryException {
		Geolocation geolocation = this.getGeolocationDao().getById(geolocationId);
		if (geolocation != null) {
			return geolocation.getDescription();
		}
		return null;
	}

	@Override
	public void saveGeolocationProperty(int geolocationId, int typeId, String value) throws MiddlewareQueryException {
		try {
			this.getGeolocationPropertySaver().saveOrUpdate(geolocationId, typeId, value);
		} catch (Exception e) {
			throw new MiddlewareQueryException("Error in saveGeolocationProperty " + e.getMessage(), e);
		}

	}

	@Override
	public List<String> getAllSharedProjectNames() throws MiddlewareQueryException {
		return this.getDmsProjectDao().getAllSharedProjectNames();
	}

	@Override
	public boolean checkIfAnyLocationIDsExistInExperiments(int studyId, DataSetType dataSetType, List<Integer> locationIds) {

		List<DmsProject> datasetProjects =
				this.getDmsProjectDao().getDataSetsByStudyAndProjectProperty(studyId, TermId.DATASET_TYPE.getId(),
						String.valueOf(dataSetType.getId()));

		if (!datasetProjects.isEmpty()) {
			int dataSetId = datasetProjects.get(0).getProjectId();
			return this.getExperimentDao().checkIfAnyLocationIDsExistInExperiments(dataSetId, locationIds);
		} else {
			return false;
		}

	}
}
