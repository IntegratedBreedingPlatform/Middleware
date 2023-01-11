
package org.generationcp.middleware.service.impl.study;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.generationcp.middleware.ContextHolder;
import org.generationcp.middleware.api.germplasm.GermplasmStudyDto;
import org.generationcp.middleware.api.study.StudyDTO;
import org.generationcp.middleware.api.study.StudyDetailsDTO;
import org.generationcp.middleware.api.study.StudySearchRequest;
import org.generationcp.middleware.api.study.StudySearchResponse;
import org.generationcp.middleware.domain.dms.DatasetDTO;
import org.generationcp.middleware.domain.dms.FolderReference;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.Reference;
import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.etl.ExperimentalDesignVariable;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.TreatmentVariable;
import org.generationcp.middleware.domain.gms.SystemDefinedEntryType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.operation.builder.WorkbookBuilder;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.generationcp.middleware.service.Service;
import org.generationcp.middleware.service.api.dataset.DatasetService;
import org.generationcp.middleware.service.api.study.StudyEntryService;
import org.generationcp.middleware.service.api.study.StudyService;
import org.generationcp.middleware.service.api.study.germplasm.source.GermplasmStudySourceSearchRequest;
import org.generationcp.middleware.service.impl.dataset.DatasetServiceImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Transactional
public class StudyServiceImpl extends Service implements StudyService {

	// TODO: what are we gonna do with the ff variablesIds? This one is: AppConstants.HIDE_STUDY_ENVIRONMENT_FIELDS.
	//  Should we move the appconstants.properties to middleware?
	private static final List<Integer> HIDE_STUDY_ENVIRONMENT_FIELDS = Arrays.asList(8170);
	// TODO: what are we gonna do with the ff variablesIds? This one is: AppConstants.EXP_DESIGN_VARIABLES
	private static final List<Integer> EXP_DESIGN_VARIABLES =
		Arrays.asList(8135, 8131, 8842, 8132, 8133, 8134, 8136, 8137, 8138, 8139, 8142, 8165, 8831, 8411, 8412, 8413);
	private static final List<Integer> FACTOR_IDS = Arrays.asList(8230, 8210, 8220, 8000, 8200, 8581, 8582);

	@Resource
	private StudyDataManager studyDataManager;

	@Resource
	private DatasetService datasetService;

	@Resource
	private StudyEntryService studyEntryService;

	private static LoadingCache<StudyKey, String> studyIdToProgramIdCache;

	private DaoFactory daoFactory;

	public StudyServiceImpl() {
		super();
	}

	public StudyServiceImpl(final HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
		final CacheLoader<StudyKey, String> studyKeyCacheBuilder = new CacheLoader<StudyKey, String>() {

			@Override
			public String load(final StudyKey key) {
				return StudyServiceImpl.this.studyDataManager.getProject(key.getStudyId()).getProgramUUID();
			}
		};
		StudyServiceImpl.studyIdToProgramIdCache =
			CacheBuilder.newBuilder().expireAfterWrite(100, TimeUnit.MINUTES).build(studyKeyCacheBuilder);
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public boolean hasCrossesOrSelections(final int studyId) {
		final GermplasmStudySourceSearchRequest searchParameters = new GermplasmStudySourceSearchRequest();
		searchParameters.setStudyId(studyId);
		return this.daoFactory.getGermplasmStudySourceDAO().countGermplasmStudySourceList(searchParameters) > 0;
	}

	@Override
	public Map<Integer, String> getGenericGermplasmDescriptors(final int studyIdentifier) {

		final Map<Integer, String> allGermplasmDescriptors =
			this.daoFactory.getProjectPropertyDAO().getGermplasmDescriptors(studyIdentifier);
		/**
		 * Fixed descriptors are the ones that are NOT stored in stockprop or nd_experimentprop. We dont need additional joins to props
		 * table for these as they are available in columns in main entity (e.g. stock or nd_experiment) tables.
		 */
		final List<Integer> fixedGermplasmDescriptors =
			Lists.newArrayList(TermId.GID.getId(), TermId.DESIG.getId(), TermId.ENTRY_NO.getId(), TermId.ENTRY_TYPE.getId(),
				TermId.ENTRY_CODE.getId(), TermId.CROSS.getId());
		final Map<Integer, String> genericGermplasmDescriptors = Maps.newHashMap();

		for (final Map.Entry<Integer, String> gpDescriptor : allGermplasmDescriptors.entrySet()) {
			if (!fixedGermplasmDescriptors.contains(gpDescriptor.getKey())) {
				genericGermplasmDescriptors.put(gpDescriptor.getKey(), gpDescriptor.getValue());
			}
		}
		return genericGermplasmDescriptors;
	}

	@Override
	public Map<Integer, String> getAdditionalDesignFactors(final int studyIdentifier) {

		final Map<Integer, String> allDesignFactors = this.daoFactory.getProjectPropertyDAO().getDesignFactors(studyIdentifier);
		/**
		 * Fixed design factors are already being retrieved individually in Measurements query. We are only interested in additional
		 * EXPERIMENTAL_DESIGN and TREATMENT FACTOR variablesObservationUnitsSearchDTO
		 */
		final List<Integer> fixedDesignFactors =
			Lists.newArrayList(TermId.REP_NO.getId(), TermId.PLOT_NO.getId(), TermId.BLOCK_NO.getId(), TermId.ROW.getId(),
				TermId.COL.getId(), TermId.FIELDMAP_COLUMN.getId(), TermId.FIELDMAP_RANGE.getId(), TermId.OBS_UNIT_ID.getId());
		final Map<Integer, String> additionalDesignFactors = Maps.newHashMap();

		for (final Map.Entry<Integer, String> designFactor : allDesignFactors.entrySet()) {
			if (!fixedDesignFactors.contains(designFactor.getKey())) {
				additionalDesignFactors.put(designFactor.getKey(), designFactor.getValue());
			}
		}
		return additionalDesignFactors;
	}

	@Override
	public Integer getPlotDatasetId(final int studyId) {
		return this.daoFactory.getDmsProjectDAO().getDatasetsByTypeForStudy(studyId, DatasetTypeEnum.PLOT_DATA.getId()).get(0)
			.getProjectId();
	}

	@Override
	public Integer getEnvironmentDatasetId(final int studyId) {
		return this.daoFactory.getDmsProjectDAO().getDatasetsByTypeForStudy(studyId, DatasetTypeEnum.SUMMARY_DATA.getId()).get(0)
			.getProjectId();
	}

	@Override
	public String getProgramUUID(final Integer studyIdentifier) {
		try {
			return StudyServiceImpl.studyIdToProgramIdCache.get(new StudyKey(studyIdentifier, ContextHolder.getCurrentCrop()));
		} catch (final ExecutionException e) {
			throw new MiddlewareQueryException(
				"Unexpected error updating observations. Please contact support for " + "further assistence.", e);
		}
	}

	@Override
	public boolean hasMeasurementDataEntered(final List<Integer> ids, final int studyId) {
		return this.daoFactory.getPhenotypeDAO().hasMeasurementDataEntered(ids, studyId);
	}

	@Override
	public boolean studyHasGivenDatasetType(final Integer studyId, final Integer datasetTypeId) {
		final List<DmsProject> datasets = this.daoFactory.getDmsProjectDAO()
			.getDatasetsByTypeForStudy(studyId, datasetTypeId);
		return (!org.springframework.util.CollectionUtils.isEmpty(datasets));
	}

	@Override
	public List<GermplasmStudyDto> getGermplasmStudies(final Integer gid) {
		return this.daoFactory.getStockDao().getGermplasmStudyDtos(gid);
	}

	@Override
	public List<StudyDTO> getFilteredStudies(final String programUUID, final StudySearchRequest studySearchRequest,
		final Pageable pageable) {
		return this.daoFactory.getDmsProjectDAO().filterStudies(programUUID, studySearchRequest, pageable);
	}

	@Override
	public long countFilteredStudies(final String programUUID, final StudySearchRequest studySearchRequest) {
		return this.daoFactory.getDmsProjectDAO().countFilteredStudies(programUUID, studySearchRequest);
	}

	@Override
	public void deleteProgramStudies(final String programUUID) {
		final List<Integer> studyAndFolderIds = this.daoFactory.getDmsProjectDAO().getAllProgramStudiesAndFolders(programUUID);
		this.daoFactory.getDmsProjectDAO().markProjectsAndChildrenAsDeleted(studyAndFolderIds);
	}

	@Override
	public void deleteStudy(final int studyId) {
		this.daoFactory.getDmsProjectDAO().markProjectsAndChildrenAsDeleted(Arrays.asList(studyId));
	}

	@Override
	public long countStudiesByGids(final List<Integer> gids) {
		return this.daoFactory.getStockDao().countStudiesByGids(gids);
	}

	@Override
	public long countPlotsByGids(final List<Integer> gids) {
		return this.daoFactory.getStockDao().countPlotsByGids(gids);
	}

	@Override
	public boolean isLocationUsedInStudy(final Integer locationId) {
		return this.daoFactory.getGeolocationPropertyDao()
			.getGeolocationIdsByPropertyTypeAndValue(TermId.LOCATION_ID.getId(), locationId.toString()).size() > 0;
	}

	@Override
	public void deleteNameTypeFromStudies(final Integer nameTypeId) {
		this.daoFactory.getProjectPropertyDAO().deleteNameTypeFromStudies(nameTypeId);
	}

	@Override
	public List<StudySearchResponse> searchStudies(final String programUUID, final StudySearchRequest studySearchRequest,
		final Pageable pageable) {
		final Map<Integer, List<Integer>> categoricalValueReferenceIdsByVariablesIds =
			this.getCategoricalValueReferenceIdsByVariablesIds(studySearchRequest.getStudySettings());
		final boolean areCategoricalVariablesNotMatching =
			categoricalValueReferenceIdsByVariablesIds.values().stream().anyMatch(CollectionUtils::isEmpty);
		if (areCategoricalVariablesNotMatching) {
			return new ArrayList<>();
		}
		return this.daoFactory.getDmsProjectDAO()
			.searchStudies(programUUID, studySearchRequest, categoricalValueReferenceIdsByVariablesIds, pageable);
	}

	@Override
	public long countSearchStudies(final String programUUID, final StudySearchRequest studySearchRequest) {
		final Map<Integer, List<Integer>> categoricalValueReferenceIdsByVariablesIds =
			this.getCategoricalValueReferenceIdsByVariablesIds(studySearchRequest.getStudySettings());
		final boolean areCategoricalVariablesNotMatching =
			categoricalValueReferenceIdsByVariablesIds.values().stream().anyMatch(CollectionUtils::isEmpty);
		if (areCategoricalVariablesNotMatching) {
			return 0;
		}
		return this.daoFactory.getDmsProjectDAO()
			.countSearchStudies(programUUID, studySearchRequest, categoricalValueReferenceIdsByVariablesIds);
	}

	@Override
	public Optional<FolderReference> getFolderByParentAndName(final Integer parentId, final String folderName, final String programUUID) {
		return this.daoFactory.getDmsProjectDAO().getFolderByParentAndName(parentId, folderName, programUUID);
	}

	@Override
	public StudyDetailsDTO getStudyDetails(final String programUUID, final Integer studyId) {
		final StudySearchRequest studySearchRequest = new StudySearchRequest();
		studySearchRequest.setStudyIds(Arrays.asList(studyId));
		final List<StudySearchResponse> searchResponse = this.searchStudies(programUUID, studySearchRequest, new PageRequest(0, 1));
		final StudySearchResponse studyData = searchResponse.get(0);

		final StudyDetailsDTO studyDetailsDTO = new StudyDetailsDTO();
		studyDetailsDTO.setId(studyData.getStudyId());
		studyDetailsDTO.setName(studyData.getStudyName());
		studyDetailsDTO.setDescription(studyData.getDescription());
		studyDetailsDTO.setStudyType(studyData.getStudyTypeName());
		studyDetailsDTO.setObjective(studyData.getObjective());
		studyDetailsDTO.setCreatedByName(studyData.getOwnerName());
		studyDetailsDTO.setStartDate(studyData.getStartDate());
		studyDetailsDTO.setEndDate(studyData.getEndDate());
		studyDetailsDTO.setLastUpdateDate(studyData.getUpdateDate());

		// TODO: I added a method to retrieve a dataset with its variables in New Advance process implementation: https://github.com/IntegratedBreedingPlatform/Middleware/blob/0bbe6823d1c72f1cd4b3b14cd62f4e3c4e55fa8f/src/main/java/org/generationcp/middleware/service/api/dataset/DatasetService.java#L149
		final DatasetDTO plotDataset =
			this.datasetService.getDatasets(studyId, Collections.singleton(DatasetTypeEnum.PLOT_DATA.getId())).get(0);
		// TODO: Also added a variableTypeResolver: https://github.com/IntegratedBreedingPlatform/Middleware/blob/0bbe6823d1c72f1cd4b3b14cd62f4e3c4e55fa8f/src/main/java/org/generationcp/middleware/service/impl/dataset/DatasetServiceImpl.java#L686
		final List<Integer> observationDatasetVariableTypes = DatasetServiceImpl.OBSERVATION_DATASET_VARIABLE_TYPES;
		observationDatasetVariableTypes.add(VariableType.EXPERIMENTAL_DESIGN.getId());
		plotDataset.setVariables(this.daoFactory.getDmsProjectDAO()
			.getObservationSetVariables(plotDataset.getDatasetId(), observationDatasetVariableTypes));

		// TODO: same here as above
		final DatasetDTO environmentDataset =
			this.datasetService.getDatasets(studyId, Collections.singleton(DatasetTypeEnum.SUMMARY_DATA.getId())).get(0);
		final List<Integer> environmentDatasetVariableTypes = DatasetServiceImpl.ENVIRONMENT_DATASET_VARIABLE_TYPES;
		environmentDataset.setVariables(this.daoFactory.getDmsProjectDAO()
			.getObservationSetVariables(environmentDataset.getDatasetId(), environmentDatasetVariableTypes));

		final long numberOfEntries = this.daoFactory.getExperimentDao().countStocksByDatasetId(plotDataset.getDatasetId());
		studyDetailsDTO.setNumberOfEntries((int) numberOfEntries);

		final long numberOfPlots = this.daoFactory.getExperimentDao().count(plotDataset.getDatasetId());
		studyDetailsDTO.setNumberOfPlots((int) numberOfPlots);

		final boolean hasFieldLayout = this.daoFactory.getExperimentDao().hasFieldLayout(plotDataset.getDatasetId());
		studyDetailsDTO.setHasFieldLayout(hasFieldLayout);

		final List<Integer> variableIds =
			plotDataset.getVariables().stream().map(MeasurementVariable::getTermId).collect(Collectors.toList());
		final int numberOfVariablesWithData = this.studyDataManager.countVariatesWithData(plotDataset.getDatasetId(), variableIds);
		studyDetailsDTO.setNumberOfVariablesWithData(numberOfVariablesWithData);
		studyDetailsDTO.setTotalVariablesWithData(variableIds.size());

		final List<MeasurementVariable> studyVariables =
			this.daoFactory.getDmsProjectDAO().getObservationSetVariables(studyId, Arrays.asList(VariableType.STUDY_DETAIL.getId()));
		studyDetailsDTO.setStudySettings(studyVariables);

		final List<ProjectProperty> plotDatasetProperties =
			this.daoFactory.getDmsProjectDAO().getById(plotDataset.getDatasetId()).getProperties();
		final Map<Integer, ProjectProperty> projectPropertiesByVariableId = plotDatasetProperties.stream().collect(Collectors.toMap(
			ProjectProperty::getVariableId, projectProperty -> projectProperty, (pp1, pp2) -> pp1));

		final List<MeasurementVariable> selections = new ArrayList<>();
		final List<MeasurementVariable> entryDetails = new ArrayList<>();
		final Map<String, List<MeasurementVariable>> treatmentFactorsByTreatmentLabel = new HashMap<>();
		final Map<Integer, MeasurementVariable> factorById = new HashMap<>();
		plotDataset.getVariables().forEach(variable -> {
			if (FACTOR_IDS.contains(variable.getTermId())) {
				factorById.put(variable.getTermId(), variable);
			}

			if (variable.getVariableType() == null) {
				return;
			}

			if (VariableType.SELECTION_METHOD.getId().equals(variable.getVariableType().getId())) {
				selections.add(variable);
			} else if (VariableType.ENTRY_DETAIL.getId().equals(variable.getVariableType().getId())) {
				entryDetails.add(variable);
			} else if (VariableType.EXPERIMENTAL_DESIGN.getId().equals(variable.getVariableType().getId())) {
				final ProjectProperty projectProperty = projectPropertiesByVariableId.get(variable.getTermId());
				if (TermId.MULTIFACTORIAL_INFO.getId() == projectProperty.getTypeId() && projectProperty.getValue() != null) {
					List<MeasurementVariable> treatmentFactors = treatmentFactorsByTreatmentLabel.get(projectProperty.getValue());
					if (treatmentFactors == null) {
						treatmentFactors = new ArrayList<>();
						treatmentFactorsByTreatmentLabel.put(projectProperty.getValue(), treatmentFactors);
					}
					treatmentFactors.add(variable);
				}
			}
		});

		studyDetailsDTO.setSelections(selections);
		studyDetailsDTO.setEntryDetails(entryDetails);
		studyDetailsDTO.setTreatmentFactors(this.transformTreatmentFactors(treatmentFactorsByTreatmentLabel, plotDataset.getDatasetId()));
		studyDetailsDTO.setFactorsByIds(factorById);

		final List<MeasurementVariable> environmentConditions = environmentDataset.getVariables()
			.stream()
			.filter(variable -> variable.getVariableType().getRole() == PhenotypicType.VARIATE)
			.collect(Collectors.toList());
		studyDetailsDTO.setEnvironmentConditions(environmentConditions);

		final List<MeasurementVariable> environmentDetails = new ArrayList<>();
		final List<MeasurementVariable> experimentalDesignVariables = new ArrayList<>();
		environmentDataset.getVariables()
			.forEach(variable -> {
				if (variable.getVariableType().getRole() == PhenotypicType.TRIAL_ENVIRONMENT &&
					!EXP_DESIGN_VARIABLES.contains(variable.getTermId()) &&
					!HIDE_STUDY_ENVIRONMENT_FIELDS.contains(variable.getTermId())) {
					environmentDetails.add(variable);
				}
				if (WorkbookBuilder.EXPERIMENTAL_DESIGN_VARIABLES.contains(variable.getTermId())) {
					experimentalDesignVariables.add(variable);
				}
			});

		studyDetailsDTO.setEnvironmentDetails(environmentDetails);
		studyDetailsDTO.setNumberOfEnvironments((int) this.daoFactory.getExperimentDao().count(environmentDataset.getDatasetId()));

		final int experimentalDesignValue;
		if (!CollectionUtils.isEmpty(experimentalDesignVariables)) {
			final ExperimentalDesignVariable experimentalDesignVariable = new ExperimentalDesignVariable(experimentalDesignVariables);
			studyDetailsDTO.setExperimentalDesignDetail(experimentalDesignVariable);
			experimentalDesignValue =
				experimentalDesignVariable.getExperimentalDesign() == null ? 0 :
					Integer.parseInt(experimentalDesignVariable.getExperimentalDesign().getValue());
		} else {
			experimentalDesignValue = 0;
		}

		final Optional<MeasurementVariable> entryTypeVariable =
			this.filterVariableByTermId(plotDataset.getVariables(), TermId.ENTRY_TYPE.getId());
		final Optional<Long> nonReplicatedEntriesCount = this.getNonReplicatedEntriesCount(studyId, experimentalDesignValue);
		final long numberOfChecks =
			this.getCountNumberOfChecks(studyId, experimentalDesignValue, entryTypeVariable.get(), nonReplicatedEntriesCount);
		studyDetailsDTO.setNumberOfChecks((int) numberOfChecks);
		nonReplicatedEntriesCount.ifPresent(count -> studyDetailsDTO.setNonReplicatedEntriesCount(count.intValue()));

		return studyDetailsDTO;
	}

	private List<TreatmentVariable> transformTreatmentFactors(final Map<String, List<MeasurementVariable>> treatmentFactors,
		final Integer plotDataSetId) {
		return treatmentFactors.entrySet().stream().map(entry -> {
			final TreatmentVariable treatmentVariable = new TreatmentVariable();
			entry.getValue().forEach(factor -> {
				if (factor.getName().equals(entry.getKey())) {
					treatmentVariable.setLevelVariable(factor);
				} else {
					treatmentVariable.setValueVariable(factor);
				}
			});
			final List<String> values = this.daoFactory.getExperimentPropertyDao().getTreatmentFactorValues(
				treatmentVariable.getLevelVariable().getTermId(), treatmentVariable.getValueVariable().getTermId(),
				plotDataSetId);
			treatmentVariable.setValues(values);
			return treatmentVariable;
		}).collect(Collectors.toList());
	}

	private Optional<MeasurementVariable> filterVariableByTermId(final List<MeasurementVariable> variables, final Integer termId) {
		return variables.stream().filter(variable -> variable.getTermId() == termId).findFirst();
	}

	private long getCountNumberOfChecks(final Integer studyId, final int experimentalDesignValue,
		final MeasurementVariable entryTypeVariable, final Optional<Long> nonReplicatedEntriesCount) {
		final List<Integer> nonTestEntryTypeIds = entryTypeVariable.getPossibleValues().stream()
			.filter(valueReference -> SystemDefinedEntryType.TEST_ENTRY.getEntryTypeCategoricalId() != valueReference.getId())
			.map(Reference::getId)
			.collect(Collectors.toList());
		final long checkEntriesCount = this.studyEntryService.countStudyGermplasmByEntryTypeIds(studyId, nonTestEntryTypeIds);
		if (TermId.P_REP.getId() == experimentalDesignValue && nonReplicatedEntriesCount.isPresent()) {
			return checkEntriesCount - nonReplicatedEntriesCount.get();
		}
		return checkEntriesCount;
	}

	private Optional<Long> getNonReplicatedEntriesCount(final Integer studyId, final Integer experimentalDesignValue) {
		if (TermId.P_REP.getId() == experimentalDesignValue) {
			return Optional.of(this.studyEntryService.countStudyGermplasmByEntryTypeIds(studyId,
				Collections.singletonList(SystemDefinedEntryType.NON_REPLICATED_ENTRY.getEntryTypeCategoricalId())));
		}
		return Optional.empty();
	}

	private Map<Integer, List<Integer>> getCategoricalValueReferenceIdsByVariablesIds(final Map<Integer, String> variableFilter) {
		final Map<Integer, List<Integer>> studySettingsCategoricalValueReferenceIds = new HashMap<>();
		if (!CollectionUtils.isEmpty(variableFilter)) {
			final Map<Integer, List<ValueReference>> categoricalVariablesMap =
				this.daoFactory.getCvTermRelationshipDao().getCategoriesForCategoricalVariables(new ArrayList<>(variableFilter.keySet()));

			// Try to find value references that contains the search text
			for (final Map.Entry<Integer, List<ValueReference>> entry : categoricalVariablesMap.entrySet()) {
				final List<Integer> valueReferenceIds =
					studySettingsCategoricalValueReferenceIds.computeIfAbsent(entry.getKey(), k -> new ArrayList<>());
				final List<Integer> matchingValueReferenceIds = entry.getValue().stream()
					.filter(valueReference -> valueReference.getDescription() != null && valueReference.getDescription().toLowerCase()
						.contains(variableFilter.get(entry.getKey()).toLowerCase()))
					.map(Reference::getId)
					.collect(Collectors.toList());
				if (CollectionUtils.isEmpty(matchingValueReferenceIds)) {
					break;
				}
				valueReferenceIds.addAll(matchingValueReferenceIds);
			}
		}
		return studySettingsCategoricalValueReferenceIds;
	}

	public void setStudyDataManager(final StudyDataManager studyDataManager) {
		this.studyDataManager = studyDataManager;
	}

	public void setDaoFactory(final DaoFactory daoFactory) {
		this.daoFactory = daoFactory;
	}

}
