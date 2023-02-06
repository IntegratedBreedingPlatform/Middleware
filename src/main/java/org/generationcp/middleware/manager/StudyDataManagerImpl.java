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

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.api.location.LocationService;
import org.generationcp.middleware.dao.dms.DmsProjectDao;
import org.generationcp.middleware.dao.dms.InstanceMetadata;
import org.generationcp.middleware.dao.dms.PhenotypeOutlierDao;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.DatasetReference;
import org.generationcp.middleware.domain.dms.DatasetValues;
import org.generationcp.middleware.domain.dms.Experiment;
import org.generationcp.middleware.domain.dms.ExperimentType;
import org.generationcp.middleware.domain.dms.ExperimentValues;
import org.generationcp.middleware.domain.dms.Reference;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.Stocks;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.dms.TrialEnvironments;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.fieldbook.FieldMapDatasetInfo;
import org.generationcp.middleware.domain.fieldbook.FieldMapInfo;
import org.generationcp.middleware.domain.fieldbook.FieldMapLabel;
import org.generationcp.middleware.domain.fieldbook.FieldMapTrialInstanceInfo;
import org.generationcp.middleware.domain.fieldbook.FieldmapBlockInfo;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.sample.SampleDTO;
import org.generationcp.middleware.domain.search.StudyResultSetByNameStartDateSeasonCountry;
import org.generationcp.middleware.domain.search.filter.BrowseStudyQueryFilter;
import org.generationcp.middleware.domain.search.filter.GidStudyQueryFilter;
import org.generationcp.middleware.domain.search.filter.StudyQueryFilter;
import org.generationcp.middleware.domain.study.StudyTypeDto;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.operation.builder.DataSetBuilder;
import org.generationcp.middleware.operation.builder.StandardVariableBuilder;
import org.generationcp.middleware.operation.builder.StockBuilder;
import org.generationcp.middleware.operation.builder.TrialEnvironmentBuilder;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.pojos.dms.PhenotypeOutlier;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.pojos.dms.StudyType;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.service.api.PedigreeService;
import org.generationcp.middleware.service.api.study.StudyMetadata;
import org.generationcp.middleware.service.api.user.UserService;
import org.generationcp.middleware.service.pedigree.PedigreeFactory;
import org.generationcp.middleware.util.CrossExpansionProperties;
import org.generationcp.middleware.util.PlotUtil;
import org.generationcp.middleware.util.Util;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional
public class StudyDataManagerImpl extends DataManager implements StudyDataManager {

	private PedigreeService pedigreeService;
	private DaoFactory daoFactory;
	private StandardVariableBuilder standardVariableBuilder;

	@Resource
	private UserService userService;

	@Resource
	private DataSetBuilder dataSetBuilder;

	@Resource
	private StockBuilder stockBuilder;

	@Resource
	private TrialEnvironmentBuilder trialEnvironmentBuilder;

	@Resource
	private LocationService locationService;

	public StudyDataManagerImpl() {
	}

	private void init(final HibernateSessionProvider sessionProvider) {
		this.pedigreeService = this.getPedigreeService();
		this.daoFactory = new DaoFactory(sessionProvider);
		this.standardVariableBuilder = new StandardVariableBuilder(sessionProvider);
	}

	public StudyDataManagerImpl(final HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
		this.init(sessionProvider);
	}

	private PedigreeService getPedigreeService() {
		if (ManagerFactory.getCurrentManagerFactoryThreadLocal().get() != null) {
			return ManagerFactory.getCurrentManagerFactoryThreadLocal().get().getPedigreeService();
		}
		// we will just return default pedigree service
		return PedigreeFactory.getPedigreeService(this.sessionProvider, null, null);
	}

	@Override
	public Study getStudy(final int studyId) {
		return this.getStudyBuilder().createStudy(studyId);
	}

	@Override
	public Study getStudy(final int studyId, final boolean hasVariableType) {
		return this.getStudyBuilder().createStudy(studyId, hasVariableType);
	}

	@Override
	public Integer getStudyIdByNameAndProgramUUID(final String studyName, final String programUUID) {
		return this.daoFactory.getDmsProjectDAO().getProjectIdByNameAndProgramUUID(studyName, programUUID);
	}

	@Override
	public List<Reference> getRootFolders(final String programUUID) {
		final List<Reference> references = this.daoFactory.getDmsProjectDAO().getRootFolders(programUUID, null);
		this.populateStudyOwnerName(references);
		return references;
	}

	@Override
	public List<Reference> getChildrenOfFolder(final int folderId, final String programUUID) {
		final List<Reference> childrenOfFolder = this.daoFactory.getDmsProjectDAO().getChildrenOfFolder(folderId, programUUID, null);
		this.populateStudyOwnerName(childrenOfFolder);
		return childrenOfFolder;
	}

	@Override
	public List<DatasetReference> getDatasetReferences(final int studyId) {
		return this.daoFactory.getDmsProjectDAO().getDirectChildDatasetsOfStudy(studyId);
	}

	@Override
	public DataSet getDataSet(final int dataSetId) {
		return this.dataSetBuilder.build(dataSetId);
	}

	@Override
	public VariableTypeList getAllStudyFactors(final int studyId) {
		return this.getStudyFactorBuilder().build(studyId);
	}

	@Override
	public VariableTypeList getAllStudyVariates(final int studyId) {
		return this.getStudyVariateBuilder().build(studyId);
	}

	@Override
	public List<StudyReference> searchStudies(final StudyQueryFilter filter) {
		final List<StudyReference> studyReferences = new ArrayList<>();
		if (filter instanceof GidStudyQueryFilter) {
			final int gid = ((GidStudyQueryFilter) filter).getGid();
			studyReferences.addAll(this.daoFactory.getStockDao().getStudiesByGid(gid));

		} else if (filter instanceof BrowseStudyQueryFilter) {
			final StudyResultSetByNameStartDateSeasonCountry studyResultSet =
				new StudyResultSetByNameStartDateSeasonCountry((BrowseStudyQueryFilter) filter, this.sessionProvider);
			studyReferences.addAll(studyResultSet.getMatchingStudies());
		}

		// Retrieve study owner names from workbench DB
		this.populateStudyOwnerName(studyReferences);
		return studyReferences;
	}

	private void populateStudyOwnerName(final List<? extends Reference> references) {
		final List<StudyReference> studyReferences = new ArrayList<>();
		for (final Reference reference : references) {
			if (reference instanceof StudyReference) {
				studyReferences.add((StudyReference) reference);
			}
		}
		if (!studyReferences.isEmpty()) {
			final List<Integer> userIds = Lists.transform(studyReferences, new Function<StudyReference, Integer>() {

				@Nullable
				@Override
				public Integer apply(@Nullable final StudyReference input) {
					return input.getOwnerId();
				}
			});
			if (!userIds.isEmpty()) {
				final Map<Integer, String> userIDFullNameMap = this.userService.getUserIDFullNameMap(userIds);
				for (final StudyReference study : studyReferences) {
					if (study.getOwnerId() != null) {
						study.setOwnerName(userIDFullNameMap.get(study.getOwnerId()));
					}
				}
			}
		}
	}

	@Override
	public DatasetReference addDataSet(
		final int studyId, final VariableTypeList variableTypeList, final DatasetValues datasetValues,
		final String programUUID, final int datasetTypeId) {

		try {

			final DmsProject datasetProject =
				this.getDatasetProjectSaver().addDataSet(studyId, variableTypeList, datasetValues, programUUID, datasetTypeId);

			return new DatasetReference(datasetProject.getProjectId(), datasetProject.getName(), datasetProject.getDescription());

		} catch (final Exception e) {

			throw new MiddlewareQueryException("error in addDataSet " + e.getMessage(), e);
		}
	}

	@Override
	public List<Experiment> getExperiments(final int dataSetId, final int start, final int numRows) {
		final VariableTypeList variableTypes = this.dataSetBuilder.getVariableTypes(dataSetId);
		return this.getExperimentBuilder().build(dataSetId, PlotUtil.getAllPlotTypes(), start, numRows, variableTypes);
	}

	@Override
	public List<Experiment> getExperiments(final int dataSetId, final List<Integer> instanceNumbers, final List<Integer> repNumbers) {
		final VariableTypeList variableTypes = this.dataSetBuilder.getVariableTypes(dataSetId);
		return this.getExperimentBuilder().build(dataSetId, PlotUtil.getAllPlotTypes(), variableTypes, instanceNumbers, repNumbers);
	}

	@Override
	public VariableTypeList getTreatmentFactorVariableTypes(final int dataSetId) {
		return this.dataSetBuilder.getTreatmentFactorVariableTypes(dataSetId);
	}

	@Override
	public List<Experiment> getExperimentsWithTrialEnvironment(
		final int trialDataSetId, final int dataSetId, final int start,
		final int numRows) {
		final VariableTypeList trialVariableTypes = this.dataSetBuilder.getVariableTypes(trialDataSetId);
		final VariableTypeList variableTypes = this.dataSetBuilder.getVariableTypes(dataSetId);

		variableTypes.addAll(trialVariableTypes);

		return this.getExperimentBuilder().build(dataSetId, PlotUtil.getAllPlotTypes(), start, numRows, variableTypes);
	}

	@Override
	public List<Experiment> getExperiments(final int dataSetId, final int start, final int numOfRows, final VariableTypeList varTypeList) {
		if (varTypeList == null) {
			return this.getExperiments(dataSetId, start, numOfRows);
		} else {
			return this.getExperimentBuilder().build(dataSetId, PlotUtil.getAllPlotTypes(), start, numOfRows, varTypeList);
		}
	}

	@Override
	public long countExperiments(final int dataSetId) {
		return this.getExperimentBuilder().count(dataSetId);
	}

	@Override
	public void addOrUpdateExperiment(
		final CropType crop, final int dataSetId, final ExperimentType experimentType,
		final List<ExperimentValues> experimentValuesList) {

		try {
			final Integer userId = this.userService.getCurrentlyLoggedInUserId();
			for (final ExperimentValues experimentValues : experimentValuesList) {
				this.getExperimentModelSaver().addOrUpdateExperiment(crop, dataSetId, experimentType, experimentValues, userId);
			}

		} catch (final Exception e) {

			throw new MiddlewareQueryException("error in addOrUpdateExperiment " + e.getMessage(), e);
		}

	}

	@Override
	public List<DataSet> getDataSetsByType(final int studyId, final int datasetTypeId) {

		final List<DmsProject> datasetProjects = this.daoFactory.getDmsProjectDAO().getDatasetsByTypeForStudy(studyId, datasetTypeId);
		final List<DataSet> datasets = new ArrayList<>();

		for (final DmsProject datasetProject : datasetProjects) {
			datasets.add(this.dataSetBuilder.build(datasetProject.getProjectId()));
		}

		return datasets;
	}

	@Override
	public void addDataSetVariableType(final int datasetId, final DMSVariableType variableType) {

		try {

			this.getDatasetProjectSaver().addDatasetVariableType(datasetId, variableType);

		} catch (final Exception e) {

			throw new MiddlewareQueryException("error in addDataSetVariableType " + e.getMessage(), e);
		}
	}

	@Override
	public TrialEnvironments getTrialEnvironmentsInDataset(final int datasetId) {
		final DmsProject study = this.daoFactory.getDmsProjectDAO().getById(datasetId).getStudy();
		return this.trialEnvironmentBuilder.getTrialEnvironmentsInDataset(study.getProjectId(), datasetId);
	}

	@Override
	public Stocks getStocksInDataset(final int datasetId) {
		return this.stockBuilder.getStocksInDataset(datasetId);
	}

	@Override
	public long countStocks(final int datasetId, final int trialEnvironmentId, final int variateStdVarId) {
		return this.daoFactory.getStockDao().countStocks(datasetId, trialEnvironmentId, variateStdVarId);
	}

	@Override
	public DataSet findOneDataSetByType(final int studyId, final int datasetTypeId) {
		final List<DataSet> datasets = this.getDataSetsByType(studyId, datasetTypeId);
		if (datasets != null && !datasets.isEmpty()) {
			return datasets.get(0);
		}
		return null;
	}

	@Override
	public DatasetReference findOneDataSetReferenceByType(final int studyId, final int datasetTypeId) {
		final List<DmsProject> datasetProjects = this.daoFactory.getDmsProjectDAO().getDatasetsByTypeForStudy(studyId, datasetTypeId);
		if (datasetProjects != null && !datasetProjects.isEmpty()) {
			final DmsProject dataSetProject = datasetProjects.get(0);
			return new DatasetReference(dataSetProject.getProjectId(), dataSetProject.getName(), dataSetProject.getDescription());
		}
		return null;
	}

	@Override
	public String getLocalNameByStandardVariableId(final Integer projectId, final Integer standardVariableId) {
		final DmsProject dmsProject = new DmsProject();
		dmsProject.setProjectId(projectId);
		final ProjectProperty projectProperty =
			this.daoFactory.getProjectPropertyDAO().getByStandardVariableId(dmsProject, standardVariableId);
		return (projectProperty == null) ? null : projectProperty.getAlias();
	}

	@Override
	public List<FieldMapInfo> getFieldMapInfoOfStudy(
		final List<Integer> studyIdList,
		final CrossExpansionProperties crossExpansionProperties) {
		final List<FieldMapInfo> fieldMapInfos = new ArrayList<>();
		for (final Integer studyId : studyIdList) {
			final FieldMapInfo fieldMapInfo = new FieldMapInfo();

			fieldMapInfo.setFieldbookId(studyId);
			fieldMapInfo.setFieldbookName(this.daoFactory.getDmsProjectDAO().getById(studyId).getName());

			// Retrieve one-off the cross expansions of GIDs of study
			final List<StockModel> stockModelList = this.daoFactory.getStockDao().getStocksForStudy(studyId);
			final Set<Integer> gids = new HashSet<>();
			for (final StockModel stockModel : stockModelList) {
				gids.add(stockModel.getGermplasm().getGid());
			}
			final Map<Integer, String> crossExpansions = this.pedigreeService.getCrossExpansions(gids, null, crossExpansionProperties);
			final List<FieldMapDatasetInfo> fieldMapDatasetInfos = this.daoFactory.getExperimentPropertyDao().getFieldMapLabels(studyId);
			for (final FieldMapDatasetInfo datasetInfo : fieldMapDatasetInfos) {
				for (final FieldMapTrialInstanceInfo instanceInfo : datasetInfo.getTrialInstances()) {
					for (final FieldMapLabel label : instanceInfo.getFieldMapLabels()) {
						label.setPedigree(crossExpansions.get(label.getGid()));
					}
				}
			}
			fieldMapInfo.setDatasets(fieldMapDatasetInfos);

			fieldMapInfos.add(fieldMapInfo);
		}

		this.updateFieldMapInfoWithBlockInfo(fieldMapInfos);

		return fieldMapInfos;
	}

	private void setPedigree(
		final List<FieldMapDatasetInfo> fieldMapDatasetInfos, final CrossExpansionProperties crossExpansionProperties,
		final Map<Integer, String> pedigreeStringMap) {
		//TODO: Caching of the pedigree string is just a temporary fix. This must be properly fixed.
		for (final FieldMapDatasetInfo fieldMapDatasetInfo : fieldMapDatasetInfos) {
			final List<FieldMapTrialInstanceInfo> trialInstances = fieldMapDatasetInfo.getTrialInstances();
			if (trialInstances == null || trialInstances.isEmpty()) {
				continue;
			}
			for (final FieldMapTrialInstanceInfo trialInstance : trialInstances) {
				final List<FieldMapLabel> labels = trialInstance.getFieldMapLabels();
				for (final FieldMapLabel label : labels) {
					this.setPedigree(label, crossExpansionProperties, pedigreeStringMap);
				}
			}
		}
	}

	private void setPedigree(
		final FieldMapLabel label, final CrossExpansionProperties crossExpansionProperties,
		final Map<Integer, String> pedigreeStringMap) {

		final Integer gid = label.getGid();
		final String cachedPedigreeString = pedigreeStringMap.get(gid);
		if (StringUtils.isNotBlank(cachedPedigreeString)) {
			label.setPedigree(cachedPedigreeString);
		} else {
			final String pedigree = this.pedigreeService.getCrossExpansion(gid, crossExpansionProperties);
			label.setPedigree(pedigree);
			pedigreeStringMap.put(gid, pedigree);
		}
	}

	@Override
	public void saveOrUpdateFieldmapProperties(final List<FieldMapInfo> info, final int userId, final boolean isNew) {

		if (info != null && !info.isEmpty()) {

			try {

				if (isNew) {
					this.getLocdesSaver().saveLocationDescriptions(info, userId);
				} else {
					this.getLocdesSaver().updateDeletedPlots(info, userId);
				}
				this.getGeolocationPropertySaver().saveFieldmapProperties(info);
				this.getExperimentPropertySaver().saveFieldmapProperties(info);

			} catch (final Exception e) {

				throw new MiddlewareQueryException("Error encountered with saveOrUpdateFieldmapProperties(): " + e.getMessage(), e);
			}
		}

	}

	void updateExperimentValues(final List<ExperimentValues> experimentValues, final Integer projectId, final Integer loggedInUserId) {
		for (final ExperimentValues exp : experimentValues) {
			if (exp.getVariableList() != null && !exp.getVariableList().isEmpty()) {
				final ExperimentModel experimentModel =
					this.daoFactory.getExperimentDao().getExperimentByProjectIdAndLocation(projectId, exp.getLocationId());
				for (final Variable variable : exp.getVariableList().getVariables()) {
					final int val =
						this.daoFactory.getPhenotypeDAO().updatePhenotypesByExperimentIdAndObervableId(experimentModel.getNdExperimentId(),
							variable.getVariableType().getId(), variable.getValue(), loggedInUserId);
					if (val == 0) {
						this.getPhenotypeSaver().save(experimentModel.getNdExperimentId(), variable,
							this.userService.getCurrentlyLoggedInUserId());
					}
				}
			}
		}
	}

	@Override
	public List<FieldMapInfo> getAllFieldMapsInBlockByTrialInstanceId(
		final int datasetId, final int geolocationId,
		final CrossExpansionProperties crossExpansionProperties) {
		final List<FieldMapInfo> fieldMapInfos =
			this.daoFactory.getExperimentPropertyDao().getAllFieldMapsInBlockByTrialInstanceId(datasetId, geolocationId, null);

		this.updateFieldMapWithBlockInformation(fieldMapInfos, true);
		final Map<Integer, String> pedigreeStringMap = new HashMap<>();
		//		 Filter those belonging to the given geolocationId
		for (final FieldMapInfo fieldMapInfo : fieldMapInfos) {
			final List<FieldMapDatasetInfo> datasetInfoList = fieldMapInfo.getDatasets();
			if (datasetInfoList != null) {
				this.setPedigree(datasetInfoList, crossExpansionProperties, pedigreeStringMap);
			}
		}

		return fieldMapInfos;
	}

	@Override
	public List<FieldMapInfo> getAllFieldMapsInBlockByBlockId(final int blockId) {

		final List<FieldMapInfo> fieldMapInfos =
			this.daoFactory.getExperimentPropertyDao().getAllFieldMapsInBlockByTrialInstanceId(0, 0, blockId);

		this.updateFieldMapWithBlockInformation(fieldMapInfos);

		return fieldMapInfos;
	}

	@Override
	public boolean isStudy(final int id) {
		return this.daoFactory.getDmsProjectDAO().getById(id).getStudyType() != null;
	}

	@Override
	public boolean renameSubFolder(final String newFolderName, final int folderId, final String programUUID) {

		// check for existing folder label
		final boolean isExisting = this.daoFactory.getDmsProjectDAO().checkIfProjectNameIsExistingInProgram(newFolderName, programUUID);
		if (isExisting) {
			throw new MiddlewareQueryException("Folder label is not unique");
		}

		try {

			final DmsProject currentFolder = this.daoFactory.getDmsProjectDAO().getById(folderId);
			currentFolder.setName(newFolderName);
			this.daoFactory.getDmsProjectDAO().saveOrUpdate(currentFolder);

			return true;
		} catch (final Exception e) {

			throw new MiddlewareQueryException(
				"Error encountered with renameFolder(folderId=" + folderId + ", label=" + newFolderName + ": " + e.getMessage(), e);
		}
	}

	@Override
	public boolean renameStudy(final String newStudyName, final int studyId, final String programUUID) {

		// check for existing study name
		final boolean isExisting = this.daoFactory.getDmsProjectDAO().checkIfProjectNameIsExistingInProgram(newStudyName, programUUID);
		if (isExisting) {
			throw new MiddlewareQueryException("Study name is not unique");
		}

		try {

			final DmsProject currentStudy = this.daoFactory.getDmsProjectDAO().getById(studyId);
			final String oldName = currentStudy.getName();
			currentStudy.setName(newStudyName);
			this.daoFactory.getDmsProjectDAO().saveOrUpdate(currentStudy);

			final List<DmsProject> datasets = this.daoFactory.getDmsProjectDAO().getDatasetsByParent(studyId);
			for (final DmsProject dataset : datasets) {
				dataset.setName(dataset.getName().replace(oldName, newStudyName));
				this.daoFactory.getDmsProjectDAO().saveOrUpdate(dataset);
			}
			return true;
		} catch (final Exception e) {

			throw new MiddlewareQueryException(
				"Error encountered with renameStudy(studyId=" + studyId + ", label=" + newStudyName + ": " + e.getMessage(), e);
		}
	}

	@Override
	public List<Experiment> getExperimentsWithGidAndCross(final int dataSetId, final List<Integer> instanceNumbers,
		final List<Integer> repNumbers) {
		final VariableTypeList variableTypes = this.dataSetBuilder.getVariableTypes(dataSetId);
		this.addVariableIfNotPresent(variableTypes, TermId.GID);
		// Forcing to add CROSS variable because we need cross values to show them in Advance Study > REVIEW ADVANCED LINES
		this.addVariableIfNotPresent(variableTypes, TermId.CROSS);

		return this.getExperimentBuilder().build(dataSetId, PlotUtil.getAllPlotTypes(), variableTypes, instanceNumbers, repNumbers);
	}

	@Override
	public int addSubFolder(
		final int parentFolderId, final String name, final String description, final String programUUID,
		final String objective) {
		final DmsProject parentProject = this.daoFactory.getDmsProjectDAO().getById(parentFolderId);
		if (parentProject == null) {
			throw new MiddlewareQueryException("DMS Project is not existing");
		}
		final boolean isExisting = this.daoFactory.getDmsProjectDAO().checkIfProjectNameIsExistingInProgram(name, programUUID);
		if (isExisting) {
			throw new MiddlewareQueryException("Folder label is not unique");
		}

		try {

			final DmsProject project = this.getProjectSaver().saveFolder(parentFolderId, name, description, programUUID, objective);

			return project.getProjectId();
		} catch (final Exception e) {

			throw new MiddlewareQueryException(
				"Error encountered with addSubFolder(parentFolderId=" + parentFolderId + ", label=" + name + ", description="
					+ description + "): " + e.getMessage(), e);
		}
	}

	@Override
	public boolean moveDmsProject(final int sourceId, final int targetId) {

		final DmsProject source = this.daoFactory.getDmsProjectDAO().getById(sourceId);
		final DmsProject target = this.daoFactory.getDmsProjectDAO().getById(targetId);
		if (source == null) {
			throw new MiddlewareQueryException("Source Project is not existing");
		}

		if (target == null) {
			throw new MiddlewareQueryException("Target Project is not existing");
		}

		if (source.getProgramUUID() == null) {
			throw new MiddlewareQueryException("Templates can't be moved");
		}

		source.setParent(target);
		this.daoFactory.getDmsProjectDAO().saveOrUpdate(source);

		return true;
	}

	@Override
	public void deleteEmptyFolder(final int id, final String programUUID) {
		final DmsProjectDao dmsProjectDao = this.daoFactory.getDmsProjectDAO();
		// check if folder is existing
		final DmsProject project = dmsProjectDao.getById(id);
		if (project == null) {
			throw new MiddlewareQueryException("Folder is not existing");
		}
		// check if folder has no children
		final List<Reference> children = dmsProjectDao.getChildrenOfFolder(id, programUUID, null);
		if (children != null && !children.isEmpty()) {
			throw new MiddlewareQueryException("Folder is not empty");
		}

		try {

			// modify the folder label
			final String name = project.getName() + "#" + Math.random();
			project.setName(name);
			project.setDeleted(true);
			dmsProjectDao.saveOrUpdate(project);

		} catch (final Exception e) {
			throw new MiddlewareQueryException("Error encountered with deleteEmptyFolder(id=" + id + "): " + e.getMessage(), e);
		}
	}

	@Override
	public boolean isFolderEmpty(final int id, final String programUUID) {
		final DmsProjectDao dmsProjectDao = this.daoFactory.getDmsProjectDAO();

		// check if folder has no children
		final List<Reference> children = dmsProjectDao.getChildrenOfFolder(id, programUUID, null);
		return (children == null || children.isEmpty());
	}

	@Override
	public DmsProject getParentFolder(final int id) {
		return this.daoFactory.getDmsProjectDAO().getById(id).getParent();
	}

	@Override
	public Integer getProjectIdByStudyDbId(final Integer studyDbId) {
		return this.daoFactory.getDmsProjectDAO().getProjectIdByStudyDbId(studyDbId);
	}

	@Override
	public DmsProject getProject(final int id) {
		return this.daoFactory.getDmsProjectDAO().getById(id);
	}

	@Override
	public StudyDetails getStudyDetails(final int studyId) {
		final StudyDetails studyDetails = this.daoFactory.getDmsProjectDAO().getStudyDetails(studyId);
		this.populateSiteAnPersonIfNecessary(studyDetails);
		return studyDetails;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	public List<StudyDetails> getAllStudyDetails(final StudyTypeDto studyType, final String programUUID) {
		final List<StudyDetails> list = new ArrayList<>();
		final List localList = this.daoFactory.getDmsProjectDAO().getAllStudyDetails(studyType, programUUID);
		if (localList != null) {
			list.addAll(localList);
		}
		this.populateSiteAndPersonIfNecessary(list);
		return list;
	}

	@Override
	public int countPlotsWithRecordedVariatesInDataset(final int dataSetId, final List<Integer> variateIds) {
		return this.daoFactory.getPhenotypeDAO().countRecordedVariatesOfStudy(dataSetId, variateIds);
	}

	@Override
	public List<Method> getMethodsFromExperiments(final int dataSetId, final Integer variableId, final List<String> trialInstances) {
		return this.daoFactory.getMethodDAO().getMethodsFromExperiments(dataSetId, variableId, trialInstances);
	}

	@Override
	public String getFolderNameById(final Integer folderId) {
		final DmsProject currentFolder = this.daoFactory.getDmsProjectDAO().getById(folderId);
		return currentFolder.getName();
	}

	@Override
	public boolean checkIfStudyHasMeasurementData(final int datasetId, final List<Integer> variateIds) {
		return this.daoFactory.getPhenotypeDAO().countVariatesDataOfStudy(datasetId, variateIds) > 0;
	}

	@Override
	public int countVariatesWithData(final int datasetId, final List<Integer> variateIds) {
		int variatesWithDataCount = 0;
		if (variateIds != null && !variateIds.isEmpty()) {
			final Map<Integer, Integer> map = this.daoFactory.getPhenotypeDAO().countVariatesDataOfStudy(datasetId);
			for (final Integer variateId : variateIds) {
				final Integer count = map.get(variateId);
				if (count != null && count > 0) {
					variatesWithDataCount++;
				}
			}
		}
		return variatesWithDataCount;
	}

	private void populateSiteAnPersonIfNecessary(final StudyDetails detail) {
		if (detail != null) {
			if (detail.getSiteName() != null && !"".equals(detail.getSiteName().trim()) && detail.getSiteId() != null) {
				final Location loc = this.daoFactory.getLocationDAO().getById(detail.getSiteId());
				if (loc != null) {
					detail.setSiteName(loc.getLname());
				}
			}
			if (detail.getPiName() != null && !"".equals(detail.getPiName().trim()) && detail.getPiId() != null) {
				final Person person = this.userService.getPersonById(detail.getPiId());
				if (person != null) {
					detail.setPiName(person.getDisplayName());
				}
			}
		}
	}

	private void populateSiteAndPersonIfNecessary(final List<StudyDetails> studyDetails) {
		if (studyDetails != null && !studyDetails.isEmpty()) {
			final Map<Integer, String> siteMap = new HashMap<>();
			final Map<Integer, String> personMap = new HashMap<>();
			this.retrieveSitesAndPersonsFromStudyDetails(studyDetails, siteMap, personMap);
			for (final StudyDetails detail : studyDetails) {
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
		final List<StudyDetails> studyDetails, final Map<Integer, String> siteMap,
		final Map<Integer, String> personMap) {
		final List<Integer> siteIds = new ArrayList<>();
		final List<Integer> personIds = new ArrayList<>();
		for (final StudyDetails detail : studyDetails) {
			if (detail.getSiteId() != null) {
				siteIds.add(detail.getSiteId());
			}
			if (detail.getPiId() != null) {
				personIds.add(detail.getPiId());
			}
		}
		if (!siteIds.isEmpty()) {
			siteMap.putAll(this.daoFactory.getLocationDAO().getLocationNamesByLocationIDs(siteIds));
		}
		if (!personIds.isEmpty()) {
			personMap.putAll(this.userService.getPersonNamesByPersonIds(personIds));
		}
	}

	private void updateFieldMapWithBlockInformation(final List<FieldMapInfo> infos) {
		this.updateFieldMapWithBlockInformation(infos, false);
	}

	protected void updateFieldMapWithBlockInformation(final List<FieldMapInfo> infos, final boolean isGetLocation) {
		if (infos == null) {
			return;
		}
		final Map<Integer, String> locationMap = new HashMap<>();
		for (final FieldMapInfo info : infos) {
			if (info != null && info.getDatasets() != null) {
				for (final FieldMapDatasetInfo dataset : info.getDatasets()) {
					this.updateFieldMapTrialInstanceInfo(dataset, isGetLocation, locationMap);
				}
			}
		}
	}

	private void updateFieldMapTrialInstanceInfo(
		final FieldMapDatasetInfo dataset, final boolean isGetLocation,
		final Map<Integer, String> locationMap) {
		if (dataset != null && dataset.getTrialInstances() != null) {
			for (final FieldMapTrialInstanceInfo trial : dataset.getTrialInstances()) {
				if (trial.getBlockId() != null) {
					trial.updateBlockInformation(this.locationService.getBlockInformation(trial.getBlockId()));
				} else if (!Util.isEmpty(trial.getFieldMapLabels())) {
					// Row and Column should not be empty
					final List<FieldMapLabel> rows =
						trial.getFieldMapLabels().stream().filter(fieldMapLabel -> Util.getIntValue(fieldMapLabel.getColumn()) > 0).collect(
							Collectors.toList());
					final List<FieldMapLabel> ranges =
						trial.getFieldMapLabels().stream().filter(fieldMapLabel -> Util.getIntValue(fieldMapLabel.getRange()) > 0).collect(
							Collectors.toList());
					if (!Util.isEmpty(rows) && !Util.isEmpty(ranges)) {
						// If fieldMapLabels is not empty but no blockId, set rowsInBlock
						// and rangeInBlock value based fieldMapLabels
						final List<FieldMapLabel> rowsInBlock =
							rows.stream().sorted(Comparator.comparingInt(FieldMapLabel::getColumn).reversed()).collect(
								Collectors.toList());
						final List<FieldMapLabel> range =
							ranges.stream().sorted(Comparator.comparingInt(FieldMapLabel::getRange).reversed()).collect(
								Collectors.toList());
						trial.setRowsInBlock(rowsInBlock.get(0).getColumn());
						trial.setRangesInBlock(range.get(0).getRange());

						// To properly display plot layout, set default values
						trial.setRowsPerPlot(1); //Default
						trial.setMachineRowCapacity(1); //Default
						trial.setPlantingOrder(1); // Default
					}
				}
				trial.setHasOverlappingCoordinates(this.hasOverlappingCoordinates(trial.getFieldMapLabels()));
				trial.setHasInValidValue(this.hasInvalidCoordinateValue(trial.getFieldMapLabels()));
				if (isGetLocation) {
					trial.setLocationName(this.getLocationName(locationMap, trial.getLocationId()));
					trial.setSiteName(trial.getLocationName());
					trial.setFieldName(this.getLocationName(locationMap, trial.getFieldId()));
					trial.setBlockName(this.getLocationName(locationMap, trial.getBlockId()));
				}
			}
		}
	}

	private void updateFieldMapInfoWithBlockInfo(final List<FieldMapInfo> fieldMapInfos) {
		this.updateFieldMapWithBlockInformation(fieldMapInfos, true);
	}

	private String getLocationName(final Map<Integer, String> locationMap, final Integer id) {
		if (id != null) {
			final String name = locationMap.get(id);
			if (name != null) {
				return name;
			}
			final Location location = this.daoFactory.getLocationDAO().getById(id);
			if (location != null) {
				locationMap.put(id, location.getLname());
				return location.getLname();
			}
		}
		return null;
	}

	@Override
	public List<Object[]> getPhenotypeIdsByLocationAndPlotNo(
		final int projectId, final int locationId, final Integer plotNo,
		final List<Integer> cvTermIds) {
		return this.daoFactory.getPhenotypeDAO().getPhenotypeIdsByLocationAndPlotNo(projectId, locationId, plotNo, cvTermIds);
	}

	@Override
	public void saveOrUpdatePhenotypeOutliers(final List<PhenotypeOutlier> phenotyleOutliers) {

		final PhenotypeOutlierDao phenotypeOutlierDao = this.daoFactory.getPhenotypeOutlierDao();
		try {

			for (final PhenotypeOutlier phenotypeOutlier : phenotyleOutliers) {
				final PhenotypeOutlier existingPhenotypeOutlier =
					phenotypeOutlierDao.getPhenotypeOutlierByPhenotypeId(phenotypeOutlier.getPhenotypeId());

				if (existingPhenotypeOutlier != null) {
					existingPhenotypeOutlier.setValue(phenotypeOutlier.getValue());
					phenotypeOutlierDao.saveOrUpdate(existingPhenotypeOutlier);
				} else {
					phenotypeOutlierDao.saveOrUpdate(phenotypeOutlier);
				}
			}

		} catch (final Exception e) {
			throw new MiddlewareQueryException("error in savePhenotypeOutlier " + e.getMessage(), e);
		}

	}

	@Override
	public Boolean containsAtLeast2CommonEntriesWithValues(final int projectId, final int locationId, final int germplasmTermId) {

		return this.daoFactory.getPhenotypeDAO().containsAtLeast2CommonEntriesWithValues(projectId, locationId, germplasmTermId);
	}

	public void setLocationService(final LocationService locationService) {
		this.locationService = locationService;
	}

	public void setUserService(final UserService userService) {
		this.userService = userService;
	}

	@Override
	public void updateVariableOrdering(final int datasetId, final List<Integer> variableIds) {

		try {
			this.getProjectPropertySaver().updateVariablesRanking(datasetId, variableIds);

		} catch (final Exception e) {
			throw new MiddlewareQueryException("Error in updateVariableOrdering " + e.getMessage(), e);
		}
	}

	@Override
	public boolean checkIfAnyLocationIDsExistInExperiments(
		final int studyId, final int datasetTypeId,
		final List<Integer> locationIds) {

		final List<DmsProject> datasetProjects = this.daoFactory.getDmsProjectDAO().getDatasetsByTypeForStudy(studyId, datasetTypeId);

		if (!datasetProjects.isEmpty()) {
			final int dataSetId = datasetProjects.get(0).getProjectId();
			return this.daoFactory.getExperimentDao().checkIfAnyLocationIDsExistInExperiments(dataSetId, locationIds);
		} else {
			return false;
		}

	}

	@Override
	public List<InstanceMetadata> getInstanceMetadata(final int studyId) {
		return this.daoFactory.getGeolocationDao().getInstanceMetadata(Collections.singletonList(studyId), Collections.emptyList());
	}

	@Override
	public StudyMetadata getStudyMetadataForInstance(final Integer instanceId) {
		return this.daoFactory.getDmsProjectDAO().getStudyMetadataForInstanceId(instanceId);
	}

	@Override
	public Map<Integer, String> getExperimentSampleMap(final Integer studyDbId) {
		return this.daoFactory.getSampleDao().getExperimentSampleMap(studyDbId);
	}

	@Override
	public Map<Integer, List<SampleDTO>> getExperimentSamplesDTOMap(final Integer studyId) {
		return this.daoFactory.getExperimentDao().getExperimentSamplesDTOMap(studyId);
	}

	@Override
	public Map<String, Integer> getInstanceGeolocationIdsMap(final Integer studyId) {
		final List<Geolocation> geolocations = this.daoFactory.getGeolocationDao().getEnvironmentGeolocations(studyId);
		final Map<String, Integer> map = new HashMap<>();
		for (final Geolocation geolocation : geolocations) {
			map.put(geolocation.getDescription(), geolocation.getLocationId());
		}
		return map;
	}

	@Override
	public List<StudyTypeDto> getAllStudyTypes() {
		return this.getStudyTypeBuilder().createStudyTypeDto(this.daoFactory.getStudyTypeDao().getAll());
	}

	@Override
	public StudyTypeDto getStudyTypeByName(final String name) {
		final StudyType studyTypeByName = this.daoFactory.getStudyTypeDao().getStudyTypeByName(name);
		if (studyTypeByName != null) {
			return this.getStudyTypeBuilder().createStudyTypeDto(studyTypeByName);
		}
		return null;
	}

	@Override
	public List<StudyTypeDto> getAllVisibleStudyTypes() {
		return this.getStudyTypeBuilder().createStudyTypeDto(this.daoFactory.getStudyTypeDao().getAllVisibleStudyTypes());
	}

	@Override
	public String getProjectStartDateByProjectId(final int projectId) {
		return this.daoFactory.getDmsProjectDAO().getProjectStartDateByProjectId(projectId);
	}

	@Override
	public boolean isLocationIdVariable(final int studyId, final String variableName) {

		final DataSet trialDataSet = this.findOneDataSetByType(studyId, DatasetTypeEnum.SUMMARY_DATA.getId());

		final DMSVariableType dmsVariableType = trialDataSet.findVariableTypeByLocalName(variableName);

		if (dmsVariableType != null) {
			return dmsVariableType.getId() == TermId.LOCATION_ID.getId();
		}

		return false;

	}

	@Override
	public Map<String, String> createInstanceLocationIdToNameMapFromStudy(final int studyId) {
		// Create LocatioName to LocationId Map
		final HashMap<String, String> map = new HashMap();
		final List<InstanceMetadata> metadataList = this.getInstanceMetadata(studyId);
		for (final InstanceMetadata instanceMetadata : metadataList) {
			map.put(String.valueOf(instanceMetadata.getLocationDbId()), instanceMetadata.getLocationName());
		}
		return map;
	}

	public StudyTypeDto getStudyTypeByStudyId(final Integer studyIdentifier) {
		final DmsProject study = this.daoFactory.getDmsProjectDAO().getById(studyIdentifier);
		if (study != null && study.getStudyType() != null) {
			return this.getStudyTypeBuilder().createStudyTypeDto(study.getStudyType());
		}
		return null;

	}

	/**
	 * Returns list of root or top-level folders and studies.
	 *
	 * @param programUUID program's unique id
	 * @param studyTypeId
	 * @return List of Folder POJOs or empty list if none found
	 */
	@Override
	public List<Reference> getRootFoldersByStudyType(final String programUUID, final Integer studyTypeId) {
		return this.daoFactory.getDmsProjectDAO().getRootFolders(programUUID, studyTypeId);
	}

	/**
	 * Returns list of children of a folder given its ID.
	 *
	 * @param folderId    The id of the folder to match
	 * @param programUUID unique id of the program
	 * @param studyTypeId
	 * @return List of containing study (StudyReference) and folder (FolderReference) references or empty list if none found
	 */
	@Override
	public List<Reference> getChildrenOfFolderByStudyType(final int folderId, final String programUUID, final Integer studyTypeId) {
		final List<Reference> children = this.daoFactory.getDmsProjectDAO().getChildrenOfFolder(folderId, programUUID, studyTypeId);
		this.populateStudyOwnerName(children);
		return children;
	}

	@Override
	public void updateStudyLockedStatus(final Integer studyId, final Boolean isLocked) {
		this.daoFactory.getDmsProjectDAO().lockUnlockStudy(studyId, isLocked);

	}

	@Override
	public boolean areAllInstancesExistInDataset(final Integer datasetId, final Set<Integer> instanceIds) {
		return this.daoFactory.getExperimentDao().areAllInstancesExistInDataset(datasetId, instanceIds);
	}

	@Override
	public String getBlockId(final int datasetId, final Integer trialInstance) {
		return this.daoFactory.getGeolocationPropertyDao().getValueOfTrialInstance(datasetId, TermId.BLOCK_ID.getId(), trialInstance);

	}

	@Override
	public FieldmapBlockInfo getBlockInformation(final int blockId) {
		return this.locationService.getBlockInformation(blockId);
	}

	@Override
	public StudyReference getStudyReference(final Integer studyId) {
		final StudyReference studyReference = this.daoFactory.getDmsProjectDAO().getStudyReference(studyId);
		this.populateStudyOwnerName(Collections.singletonList(studyReference));
		return studyReference;
	}

	@Override
	public Map<Integer, String> getGeolocationByInstanceId(final Integer datasetId, final Integer instanceDbId) {
		final Geolocation geoLocation = this.daoFactory.getGeolocationDao().getById(instanceDbId);
		final Map<Integer, String> geoLocationMap =
			this.daoFactory.getGeolocationPropertyDao().getGeoLocationPropertyByVariableId(datasetId, instanceDbId);

		geoLocationMap.put(TermId.TRIAL_INSTANCE_FACTOR.getId(), geoLocation.getDescription());
		if (geoLocation.getLatitude() != null) {
			geoLocationMap.put(TermId.LATITUDE.getId(), geoLocation.getLatitude().toString());
		}

		if (geoLocation.getLongitude() != null) {
			geoLocationMap.put(TermId.LONGITUDE.getId(), geoLocation.getLongitude().toString());
		}

		if (geoLocation.getGeodeticDatum() != null) {
			geoLocationMap.put(TermId.GEODETIC_DATUM.getId(), geoLocation.getGeodeticDatum());
		}

		if (geoLocation.getAltitude() != null) {
			geoLocationMap.put(TermId.ALTITUDE.getId(), geoLocation.getAltitude().toString());
		}

		return geoLocationMap;
	}

	// TODO IBP-3305 Determine if this can be replaced with StudyDataManager#areAllInstancesExistInDataset
	@Override
	public Boolean instancesExist(final Set<Integer> instanceIds) {
		return this.daoFactory.getGeolocationDao().isInstancesExist(instanceIds);
	}

	@Override
	public Map<Integer, String> getPhenotypeByVariableId(final Integer datasetId, final Integer instanceDbId) {
		final Map<Integer, String> phenotypeMap = new HashMap<>();
		final List<Phenotype> phenotypes =
			this.daoFactory.getPhenotypeDAO().getPhenotypeByDatasetIdAndInstanceDbId(datasetId, instanceDbId);
		for (final Phenotype phenotype : phenotypes) {
			phenotypeMap.put(phenotype.getObservableId(), phenotype.getValue());
		}
		return phenotypeMap;
	}

	void setDataSetBuilder(final DataSetBuilder dataSetBuilder) {
		this.dataSetBuilder = dataSetBuilder;
	}

	void setTrialEnvironmentBuilder(final TrialEnvironmentBuilder trialEnvironmentBuilder) {
		this.trialEnvironmentBuilder = trialEnvironmentBuilder;
	}

	private boolean hasInvalidCoordinateValue(final List<FieldMapLabel> labels) {
		if (!CollectionUtils.isEmpty(labels)) {
			return labels.stream().anyMatch(
				fieldMapLabel -> Util.getIntValue(fieldMapLabel.getColumn()) <= 0 || Util.getIntValue(fieldMapLabel.getRange()) <= 0);
		}
		return false;
	}

	private boolean hasOverlappingCoordinates(final List<FieldMapLabel> labels) {
		if (!CollectionUtils.isEmpty(labels)) {
			final List<String> existing = new ArrayList<>();
			for (final FieldMapLabel label : labels) {
				if (existing.contains(label.getRange() + "-" + label.getColumn())) {
					return true;
				} else {
					existing.add(label.getRange() + "-" + label.getColumn());
				}
			}
		}
		return false;
	}

	private void addVariableIfNotPresent(final VariableTypeList variableTypes, final TermId termId) {
		if (variableTypes.findById(termId) == null) {
			final StandardVariable variable = this.standardVariableBuilder.getByName(termId.name(), null);
			variableTypes.add(new DMSVariableType(termId.name(), null, variable, 0));
		}
	}

}
