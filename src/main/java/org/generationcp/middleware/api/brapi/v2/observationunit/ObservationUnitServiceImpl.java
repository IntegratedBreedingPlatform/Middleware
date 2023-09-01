package org.generationcp.middleware.api.brapi.v2.observationunit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.map.LinkedMap;
import org.apache.commons.collections.map.MultiKeyMap;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.api.brapi.GermplasmServiceBrapi;
import org.generationcp.middleware.api.brapi.ObservationServiceBrapi;
import org.generationcp.middleware.api.brapi.v1.germplasm.GermplasmDTO;
import org.generationcp.middleware.api.brapi.v2.germplasm.ExternalReferenceDTO;
import org.generationcp.middleware.api.brapi.v2.observation.ObservationDto;
import org.generationcp.middleware.api.brapi.v2.observation.ObservationSearchRequestDto;
import org.generationcp.middleware.api.brapi.v2.observationlevel.ObservationLevel;
import org.generationcp.middleware.api.brapi.v2.observationlevel.ObservationLevelEnum;
import org.generationcp.middleware.api.brapi.v2.observationlevel.ObservationLevelFilter;
import org.generationcp.middleware.dao.dms.ExperimentDao;
import org.generationcp.middleware.domain.dms.DatasetDTO;
import org.generationcp.middleware.domain.dms.Enumeration;
import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.domain.search_request.brapi.v2.GermplasmSearchRequest;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareRequestException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.ExperimentExternalReference;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.ExperimentProperty;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.pojos.dms.StockProperty;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.service.api.ObservationUnitIDGenerator;
import org.generationcp.middleware.service.api.OntologyService;
import org.generationcp.middleware.service.api.PedigreeService;
import org.generationcp.middleware.service.api.analysis.SiteAnalysisService;
import org.generationcp.middleware.service.api.dataset.DatasetService;
import org.generationcp.middleware.service.api.ontology.VariableDataValidatorFactory;
import org.generationcp.middleware.service.api.ontology.VariableValueValidator;
import org.generationcp.middleware.service.api.phenotype.ObservationUnitDto;
import org.generationcp.middleware.service.api.phenotype.ObservationUnitSearchRequestDTO;
import org.generationcp.middleware.service.api.study.StudyInstanceService;
import org.generationcp.middleware.service.impl.study.StudyInstance;
import org.generationcp.middleware.util.CrossExpansionProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Transactional
public class ObservationUnitServiceImpl implements ObservationUnitService {

	private static final Logger LOG = LoggerFactory.getLogger(ObservationUnitServiceImpl.class);

	private static final String PLOT_NO = "PLOT_NO";
	private static final String REP_NO = "REP_NO";
	private static final String BLOCK_NO = "BLOCK_NO";

	public static final String ENTRY_NO = "ENTRY_NO";

	private final ObjectMapper jacksonMapper;

	@Resource
	private GermplasmServiceBrapi germplasmServiceBrapi;

	@Resource
	private OntologyService ontologyService;

	@Resource
	private VariableDataValidatorFactory variableDataValidatorFactory;

	@Resource
	private ObservationServiceBrapi observationService;

	@Resource
	private PedigreeService pedigreeService;

	@Resource
	private CrossExpansionProperties crossExpansionProperties;

	@Resource
	private StudyInstanceService studyInstanceService;

	@Resource
	private DatasetService datasetService;

	@Resource
	private SiteAnalysisService siteAnalysisService;

	private final HibernateSessionProvider sessionProvider;
	private final DaoFactory daoFactory;

	public ObservationUnitServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.sessionProvider = sessionProvider;
		this.daoFactory = new DaoFactory(this.sessionProvider);
		this.jacksonMapper = new ObjectMapper();
	}

	@Override
	public void update(final String observationUnitDbId, final ObservationUnitPatchRequestDTO requestDTO) {

		final ExperimentDao experimentDao = this.daoFactory.getExperimentDao();
		final Optional<ExperimentModel> experimentModelOptional = experimentDao.getByObsUnitId(observationUnitDbId);

		if (!experimentModelOptional.isPresent()) {
			throw new MiddlewareRequestException("", "invalid.observation.unit.id");
		}

		try {
			final ExperimentModel experimentModel = experimentModelOptional.get();
			final String props = experimentModel.getJsonProps() != null ? experimentModel.getJsonProps() : "{}";
			final Map<String, Object> propsMap = this.jacksonMapper.readValue(props, HashMap.class);
			propsMap.put("geoCoordinates", requestDTO.getObservationUnitPosition().getGeoCoordinates());
			experimentModel.setJsonProps(this.jacksonMapper.writeValueAsString(propsMap));
			experimentDao.save(experimentModel);
		} catch (final Exception e) {
			final String message = "couldn't parse prop column for observationUnitDbId=" + observationUnitDbId;
			LOG.error(message, e);
			throw new MiddlewareException(message);
		}
	}

	@Override
	public List<ObservationUnitDto> searchObservationUnits(final Integer pageSize, final Integer pageNumber,
		final ObservationUnitSearchRequestDTO requestDTO) {
		final List<ObservationUnitDto> dtos = this.daoFactory.getPhenotypeDAO().searchObservationUnits(pageSize, pageNumber, requestDTO);
		if (!CollectionUtils.isEmpty(dtos)) {
			final List<Integer> experimentIds = dtos.stream().map(ObservationUnitDto::getExperimentId).collect(Collectors.toList());

			final Map<String, List<ExternalReferenceDTO>> externalReferencesMap =
				this.daoFactory.getExperimentExternalReferenceDao().getExternalReferences(experimentIds).stream()
					.collect(groupingBy(ExternalReferenceDTO::getEntityId));

			final List<ObservationLevelRelationship> relationships =
				this.daoFactory.getExperimentPropertyDao().getObservationLevelRelationships(experimentIds);
			this.renameObservationLevelNamesToBeDisplayed(relationships);
			final Map<Integer, List<ObservationLevelRelationship>> observationRelationshipsMap = relationships.stream().collect(groupingBy(
				ObservationLevelRelationship::getExperimentId));

			final ObservationSearchRequestDto observationSearchRequest = new ObservationSearchRequestDto();
			observationSearchRequest.setObservationUnitDbIds(
				dtos.stream().map(ObservationUnitDto::getObservationUnitDbId).collect(Collectors.toList()));

			for (final ObservationUnitDto dto : dtos) {
				dto.setExternalReferences(externalReferencesMap.get(dto.getExperimentId().toString()));
				dto.getObservationUnitPosition().setObservationLevelRelationships(observationRelationshipsMap.get(dto.getExperimentId()));
			}

			if (requestDTO.getIncludeObservations()) {
				this.addObservationsPerObservationUnit(observationSearchRequest, dtos);
			}
		}

		return dtos;
	}

	private void addObservationsPerObservationUnit(final ObservationSearchRequestDto observationSearchRequest,
		final List<ObservationUnitDto> observationUnitDtos) {
		final List<ObservationDto> observationDtos = this.observationService.searchObservations(observationSearchRequest, null);
		final Map<String, List<ObservationDto>> phenotypeObservationsMap = observationDtos.stream()
			.collect(groupingBy(ObservationDto::getObservationUnitDbId));

		for (final ObservationUnitDto dto : observationUnitDtos) {
			dto.setObservations(phenotypeObservationsMap.get(dto.getObservationUnitDbId()));
		}
	}

	@Override
	public long countObservationUnits(final ObservationUnitSearchRequestDTO requestDTO) {
		return this.daoFactory.getPhenotypeDAO().countObservationUnits(requestDTO);
	}

	@Override
	public List<String> importObservationUnits(final String crop, final List<ObservationUnitImportRequestDto> requestDtos) {
		final CropType cropType = this.daoFactory.getCropTypeDAO().getByName(crop);

		requestDtos
			.forEach(dto -> this.renameObservationLevelNamesToBeSaved(dto.getObservationUnitPosition().getObservationLevelRelationships()));

		final List<String> germplasmDbIds =
			requestDtos.stream().map(ObservationUnitImportRequestDto::getGermplasmDbId).collect(Collectors.toList());
		final GermplasmSearchRequest germplasmSearchRequest = new GermplasmSearchRequest();
		germplasmSearchRequest.setGermplasmDbIds(germplasmDbIds);
		final Map<String, GermplasmDTO> germplasmDTOMap = this.germplasmServiceBrapi.searchGermplasmDTO(germplasmSearchRequest, null)
			.stream().collect(Collectors.toMap(GermplasmDTO::getGermplasmDbId, Function.identity()));

		final List<Integer> trialIds = requestDtos.stream().map(r -> Integer.valueOf(r.getTrialDbId())).collect(Collectors.toList());
		final List<String> variableNames = new ArrayList<>();
		requestDtos.stream().forEach(dto -> {
			if (!CollectionUtils.isEmpty(dto.getObservationUnitPosition().getObservationLevelRelationships())) {
				variableNames.addAll(dto.getObservationUnitPosition().getObservationLevelRelationships().stream()
					.map(ObservationLevelRelationship::getLevelName).collect(Collectors.toList()));
			}
		});

		final Map<Integer, List<StockModel>> stocks = this.daoFactory.getStockDao().getStocksByStudyIds(trialIds);
		final Map<Integer, MultiKeyMap> stockMap = new HashMap<>();

		for (final Integer trialDbId : trialIds) {
			stockMap.putIfAbsent(trialDbId, MultiKeyMap.decorate(new LinkedMap()));
			stocks.getOrDefault(trialDbId, new ArrayList<>()).forEach(stockModel ->
				stockMap.get(trialDbId).put(stockModel.getGermplasm().getGermplasmUUID(), stockModel.getUniqueName(), stockModel)
			);
		}

		final Map<Integer, Integer> generationLevelByTrialIds = this.daoFactory.getDmsProjectDAO().getByIds(trialIds)
			.stream()
			.collect(
				Collectors.toMap(DmsProject::getProjectId, trial -> trial.getGenerationLevel() == null ? 1 : trial.getGenerationLevel()));
		final Map<Integer, DmsProject> trialIdPlotDatasetMap =
			this.daoFactory.getDmsProjectDAO().getDatasetsByTypeForStudy(trialIds, DatasetTypeEnum.PLOT_DATA.getId()).stream()
				.collect(Collectors.toMap(plotDataset -> plotDataset.getStudy().getProjectId(), Function.identity()));
		// TODO: Adding OBS_UNIT_ID if it does not exist in projectprop.
		this.addObsUnitIdVariableIfNotPresent(trialIdPlotDatasetMap);
		final Map<Integer, DmsProject> trialIdMeansDatasetMap =
			this.daoFactory.getDmsProjectDAO().getDatasetsByTypeForStudy(trialIds, DatasetTypeEnum.MEANS_DATA.getId()).stream()
				.collect(Collectors.toMap(plotDataset -> plotDataset.getStudy().getProjectId(), Function.identity()));
		// TODO: Adding OBS_UNIT_ID if it does not exist in projectprop.
		this.addObsUnitIdVariableIfNotPresent(trialIdMeansDatasetMap);
		final Map<Integer, List<Integer>> plotExperimentVariablesMap = this.populatePlotExperimentVariablesMap(trialIdPlotDatasetMap);

		final Map<String, MeasurementVariable> variableNamesMap =
			this.daoFactory.getCvTermDao().getVariablesByNamesAndVariableType(variableNames, VariableType.EXPERIMENTAL_DESIGN);
		final Map<String, MeasurementVariable> variableSynonymsMap =
			this.daoFactory.getCvTermDao().getVariablesBySynonymsAndVariableType(variableNames, VariableType.EXPERIMENTAL_DESIGN);
		final List<Integer> categoricalVariableIds = new ArrayList<>();
		categoricalVariableIds.addAll(
			variableNamesMap.values().stream()
				.filter(measurementVariable -> DataType.CATEGORICAL_VARIABLE.getId().equals(measurementVariable.getDataTypeId()))
				.map(MeasurementVariable::getTermId).collect(Collectors.toList()));
		categoricalVariableIds.addAll(
			variableSynonymsMap.values().stream()
				.filter(measurementVariable -> DataType.CATEGORICAL_VARIABLE.getId().equals(measurementVariable.getDataTypeId()))
				.map(MeasurementVariable::getTermId).collect(Collectors.toList()));
		final Map<Integer, List<ValueReference>> categoricalVariablesMap =
			this.daoFactory.getCvTermRelationshipDao().getCategoriesForCategoricalVariables(categoricalVariableIds);

		final List<String> observationUnitDbIds = new ArrayList<>();

		final Map<String, Enumeration> entryTypes =
			this.ontologyService.getStandardVariable(TermId.ENTRY_TYPE.getId(), null).getEnumerations()
				.stream().collect(Collectors.toMap(enumeration -> enumeration.getDescription().toUpperCase(), enumeration -> enumeration));

		final Map<String, Map<String, Enumeration>> entryTypesMap = new HashMap<>();
		for (final ObservationUnitImportRequestDto dto : requestDtos) {
			final Integer trialDbId = Integer.valueOf(dto.getTrialDbId());
			final Integer studyDbId = Integer.valueOf(dto.getStudyDbId());
			final boolean isObservationUnitForMeansDataset = this.isObservationUnitForMeans(dto);

			final Optional<String> entryNoOptional =
				!MapUtils.isEmpty(dto.getAdditionalInfo()) ? Optional.ofNullable(dto.getAdditionalInfo().getOrDefault(ENTRY_NO, null)) :
					Optional.empty();

			// If combination of germplasmDbId and entryNumber (if specified) does not exist, create new stock
			if (!stockMap.get(trialDbId)
				.containsKey(dto.getGermplasmDbId(), entryNoOptional.orElse(StringUtils.EMPTY))) {
				final GermplasmDTO germplasmDTO = germplasmDTOMap.get(dto.getGermplasmDbId());
				final Integer generationLevel = generationLevelByTrialIds.get(trialDbId);
				final String crossExpansion = this.pedigreeService
					.getCrossExpansion(Integer.valueOf(germplasmDTO.getGid()), generationLevel, this.crossExpansionProperties);
				final StockModel stockModel =
					this.createStockModel(germplasmDTO, stockMap, dto, trialDbId, entryTypes, entryTypesMap, crossExpansion);
				stockMap.get(trialDbId).put(dto.getGermplasmDbId(), entryNoOptional.orElse(StringUtils.EMPTY), stockModel);
			}

			final ExperimentModel experimentModel = new ExperimentModel();
			if (isObservationUnitForMeansDataset) {
				// If the study does not have an existing means dataset, create a new one.
				if (!trialIdMeansDatasetMap.containsKey(trialDbId)) {
					final int meansDatasetId = this.siteAnalysisService.createMeansDataset(trialDbId);
					trialIdMeansDatasetMap.put(trialDbId, new DmsProject(meansDatasetId));
				}
				experimentModel.setProject(trialIdMeansDatasetMap.get(trialDbId));
				experimentModel.setTypeId(TermId.AVERAGE_EXPERIMENT.getId());
			} else {
				experimentModel.setProject(trialIdPlotDatasetMap.get(trialDbId));
				experimentModel.setTypeId(TermId.PLOT_EXPERIMENT.getId());
				this.setJsonProps(experimentModel, dto);
				this.addExperimentVariablesIfNecessary(dto, plotExperimentVariablesMap, trialIdPlotDatasetMap, variableNamesMap,
					variableSynonymsMap);
				this.addExperimentProperties(experimentModel, dto, variableNamesMap, variableSynonymsMap, categoricalVariablesMap);
			}
			experimentModel.setGeoLocation(new Geolocation(studyDbId));
			experimentModel.setStock(
				(StockModel) stockMap.get(trialDbId).get(dto.getGermplasmDbId(), entryNoOptional.orElse(StringUtils.EMPTY)));

			ObservationUnitIDGenerator.generateObservationUnitIds(cropType, Collections.singletonList(experimentModel));

			this.setExperimentExternalReferences(dto, experimentModel);
			this.daoFactory.getExperimentDao().save(experimentModel);

			observationUnitDbIds.add(experimentModel.getObsUnitId());
		}

		//Update environment dataset to save added project properties
		for (final Integer trialId : trialIds) {
			this.daoFactory.getDmsProjectDAO().update(trialIdPlotDatasetMap.get(trialId));
		}

		return observationUnitDbIds;
	}

	private void addObsUnitIdVariableIfNotPresent(final Map<Integer, DmsProject> plotDatasetMap) {
		for (final Map.Entry<Integer, DmsProject> plotDatasetEntry : plotDatasetMap.entrySet()) {
			final DmsProject plotDataset = plotDatasetEntry.getValue();
			if (!plotDataset.getProperties().stream() //
				.filter(property -> property.getTypeId().equals(VariableType.EXPERIMENTAL_DESIGN.getId()) && //
					property.getVariable().getCvTermId() == TermId.OBS_UNIT_ID.getId() //
				).findFirst().isPresent()) {
				final ProjectProperty property =
					new ProjectProperty(plotDataset, VariableType.EXPERIMENTAL_DESIGN.getId(), null, //
						plotDataset.getProperties().size(), TermId.OBS_UNIT_ID.getId(), TermId.OBS_UNIT_ID.name());
				plotDataset.addProperty(property);
			}
		}
	}

	@Override
	public Map<String, List<String>> getPlotObservationLevelRelationshipsByGeolocations(
		final Set<String> geolocationIds) {
		return this.daoFactory.getExperimentPropertyDao().getPlotObservationLevelRelationshipsByGeolocations(geolocationIds);
	}

	@Override
	public List<ObservationLevel> getObservationLevels(final ObservationLevelFilter observationLevelFilter) {
		final List<ObservationLevel> observationLevels = new ArrayList<>();
		if (StringUtils.isEmpty(observationLevelFilter.getTrialDbId()) && StringUtils.isEmpty(observationLevelFilter.getStudyDbId())) {
			final Iterator<ObservationLevelEnum> iterator = Arrays.stream(ObservationLevelEnum.values()).iterator();
			while (iterator.hasNext()) {
				final ObservationLevelEnum next = iterator.next();
				observationLevels.add(new ObservationLevel(next));
			}
		} else if (StringUtils.isNotEmpty(observationLevelFilter.getStudyDbId())) {
			// Get Observation Levels present in the Instance(Study in BrAPI context)
			this.getObservationLevelsForStudy(observationLevelFilter, observationLevels);
		} else if (StringUtils.isNotEmpty(observationLevelFilter.getTrialDbId())) {
			// Get Observation Levels present in the Study(Trial in BrAPI context)
			this.getObservationLevelsForTrial(observationLevelFilter, observationLevels);
		}
		return observationLevels;
	}

	private void getObservationLevelsForStudy(final ObservationLevelFilter observationLevelFilter,
		final List<ObservationLevel> observationLevels) {
		final Integer instanceId = Integer.valueOf(observationLevelFilter.getStudyDbId());
		final Integer studyId = Integer.valueOf(observationLevelFilter.getTrialDbId());
		observationLevels.add(new ObservationLevel(ObservationLevelEnum.STUDY));
		final StudyInstance studyInstance = this.studyInstanceService.getStudyInstance(studyId, instanceId).get();
		if (studyInstance.isHasExperimentalDesign()) {
			final List<Integer> datasetTypeIdsOfEnvironment = this.daoFactory.getDmsProjectDAO()
				.getDatasetTypeIdsOfEnvironment(instanceId);
			if (datasetTypeIdsOfEnvironment.contains(DatasetTypeEnum.SUMMARY_STATISTICS_DATA.getId())) {
				observationLevels.add(new ObservationLevel(ObservationLevelEnum.SUMMARY_STATISTICS));
			}
			if (studyInstance.getHasFieldLayout()) {
				observationLevels.add(new ObservationLevel(ObservationLevelEnum.FIELD));
			}
			if (datasetTypeIdsOfEnvironment.contains(DatasetTypeEnum.MEANS_DATA.getId())) {
				observationLevels.add(new ObservationLevel(ObservationLevelEnum.MEANS));
			}

			final DatasetDTO plotDataset =
				this.datasetService.getDatasets(studyId, Sets.newHashSet(DatasetTypeEnum.PLOT_DATA.getId())).get(0);
			this.addPlotRelatedObservationLevels(observationLevels, plotDataset);

			if (datasetTypeIdsOfEnvironment.contains(DatasetTypeEnum.QUADRAT_SUBOBSERVATIONS.getId())) {
				observationLevels.add(new ObservationLevel(ObservationLevelEnum.SUB_PLOT));
			}
			if (datasetTypeIdsOfEnvironment.contains(DatasetTypeEnum.PLANT_SUBOBSERVATIONS.getId())) {
				observationLevels.add(new ObservationLevel(ObservationLevelEnum.PLANT));
			}
			if (datasetTypeIdsOfEnvironment.contains(DatasetTypeEnum.TIME_SERIES_SUBOBSERVATIONS.getId())) {
				observationLevels.add(new ObservationLevel(ObservationLevelEnum.TIMESERIES));
			}
			if (datasetTypeIdsOfEnvironment.contains(DatasetTypeEnum.CUSTOM_SUBOBSERVATIONS.getId())) {
				observationLevels.add(new ObservationLevel(ObservationLevelEnum.CUSTOM));
			}
		} else {
			observationLevels.add(new ObservationLevel(ObservationLevelEnum.PLOT));
		}

	}

	private void addPlotRelatedObservationLevels(final List<ObservationLevel> observationLevels, final DatasetDTO plotDataset) {
		final List<ProjectProperty> projectProperties = this.daoFactory.getProjectPropertyDAO()
			.getByProjectId(plotDataset.getDatasetId());
		final Map<Integer, ProjectProperty> projectPropertyMap = projectProperties.stream()
			.collect(Collectors.toMap(ProjectProperty::getVariableId, Function.identity()));
		if (projectPropertyMap.containsKey(TermId.BLOCK_NO.getId())) {
			observationLevels.add(new ObservationLevel(ObservationLevelEnum.BLOCK));
		}
		if (projectPropertyMap.containsKey(TermId.REP_NO.getId())) {
			observationLevels.add(new ObservationLevel(ObservationLevelEnum.REP));
		}
		observationLevels.add(new ObservationLevel(ObservationLevelEnum.PLOT));
	}

	private void getObservationLevelsForTrial(final ObservationLevelFilter observationLevelFilter,
		final List<ObservationLevel> observationLevels) {
		observationLevels.add(new ObservationLevel(ObservationLevelEnum.STUDY));
		final List<DatasetDTO> datasets = this.daoFactory.getDmsProjectDAO()
			.getDatasets(Integer.valueOf(observationLevelFilter.getTrialDbId()));
		final Map<Integer, DatasetDTO> datasetDTOMap = datasets.stream()
			.collect(Collectors.toMap(DatasetDTO::getDatasetTypeId, Function.identity()));
		if (datasetDTOMap.containsKey(DatasetTypeEnum.SUMMARY_STATISTICS_DATA.getId())) {
			observationLevels.add(new ObservationLevel(ObservationLevelEnum.SUMMARY_STATISTICS));
		}
		if (datasetDTOMap.containsKey(DatasetTypeEnum.PLOT_DATA.getId()) &&
			this.daoFactory.getExperimentDao().hasFieldLayout(datasetDTOMap.get(DatasetTypeEnum.PLOT_DATA.getId()).getDatasetId())) {
			observationLevels.add(new ObservationLevel(ObservationLevelEnum.FIELD));
		}
		if (datasetDTOMap.containsKey(DatasetTypeEnum.MEANS_DATA.getId())) {
			observationLevels.add(new ObservationLevel(ObservationLevelEnum.MEANS));
		}
		if (datasetDTOMap.containsKey(DatasetTypeEnum.PLOT_DATA.getId())) {
			this.addPlotRelatedObservationLevels(observationLevels, datasetDTOMap.get(DatasetTypeEnum.PLOT_DATA.getId()));
		}
		if (datasetDTOMap.containsKey(DatasetTypeEnum.QUADRAT_SUBOBSERVATIONS.getId())) {
			observationLevels.add(new ObservationLevel(ObservationLevelEnum.SUB_PLOT));
		}
		if (datasetDTOMap.containsKey(DatasetTypeEnum.PLANT_SUBOBSERVATIONS.getId())) {
			observationLevels.add(new ObservationLevel(ObservationLevelEnum.PLANT));
		}
		if (datasetDTOMap.containsKey(DatasetTypeEnum.TIME_SERIES_SUBOBSERVATIONS.getId())) {
			observationLevels.add(new ObservationLevel(ObservationLevelEnum.TIMESERIES));
		}
		if (datasetDTOMap.containsKey(DatasetTypeEnum.CUSTOM_SUBOBSERVATIONS.getId())) {
			observationLevels.add(new ObservationLevel(ObservationLevelEnum.CUSTOM));
		}
	}

	private boolean isObservationUnitForMeans(final ObservationUnitImportRequestDto observationUnitDto) {
		return observationUnitDto.getObservationUnitPosition() != null
			&& observationUnitDto.getObservationUnitPosition().getObservationLevel() != null
			&& observationUnitDto.getObservationUnitPosition().getObservationLevel().getLevelName()
			.equalsIgnoreCase(DatasetTypeEnum.MEANS_DATA.getName());
	}

	private void setJsonProps(final ExperimentModel model, final ObservationUnitImportRequestDto dto) {
		if (dto.getObservationUnitPosition().getGeoCoordinates() != null) {
			try {
				final Map<String, Object> propsMap = new HashMap<>();
				propsMap.put("geoCoordinates", dto.getObservationUnitPosition().getGeoCoordinates());
				model.setJsonProps(this.jacksonMapper.writeValueAsString(propsMap));
			} catch (final JsonProcessingException e) {
				// Just ignore if there's an issue with mapping
				model.setJsonProps(null);
			}
		}
	}

	private void addExperimentProperties(final ExperimentModel experiment, final ObservationUnitImportRequestDto dto,
		final Map<String, MeasurementVariable> variableNamesMap, final Map<String, MeasurementVariable> variableSynonymsMap,
		final Map<Integer, List<ValueReference>> categoricalVariablesMap) {
		final List<ExperimentProperty> properties = new ArrayList<>();
		final ObservationUnitPosition position = dto.getObservationUnitPosition();
		if (!CollectionUtils.isEmpty(position.getObservationLevelRelationships())) {
			for (final ObservationLevelRelationship levelRelationship : position.getObservationLevelRelationships()) {
				this.addProperty(experiment, variableNamesMap, variableSynonymsMap, categoricalVariablesMap, properties, levelRelationship);
			}
		}

		if (!StringUtils.isEmpty(position.getPositionCoordinateX()) && !StringUtils.isEmpty(position.getPositionCoordinateY())) {
			properties.add(this.createExperimentProperty(experiment, 0, position.getPositionCoordinateX(), TermId.COLUMN_NO.getId()));
			properties.add(this.createExperimentProperty(experiment, 0, position.getPositionCoordinateY(), TermId.RANGE_NO.getId()));
		}
		experiment.setProperties(properties);

	}

	private void addProperty(final ExperimentModel experiment, final Map<String, MeasurementVariable> variableNamesMap,
		final Map<String, MeasurementVariable> variableSynonymsMap, final Map<Integer, List<ValueReference>> categoricalVariablesMap,
		final List<ExperimentProperty> properties, final ObservationLevelRelationship levelRelationship) {
		final String variableName = levelRelationship.getLevelName().toUpperCase();
		final MeasurementVariable measurementVariable =
			variableNamesMap.containsKey(variableName) ? variableNamesMap.get(variableName) : variableSynonymsMap.get(variableName);
		if (measurementVariable != null) {
			measurementVariable.setValue(levelRelationship.getLevelCode());
			final DataType dataType = DataType.getById(measurementVariable.getDataTypeId());
			final java.util.Optional<VariableValueValidator> dataValidator =
				this.variableDataValidatorFactory.getValidator(dataType);
			if (categoricalVariablesMap.containsKey(measurementVariable.getTermId())) {
				measurementVariable.setPossibleValues(categoricalVariablesMap.get(measurementVariable.getTermId()));
			}
			if (!dataValidator.isPresent() || dataValidator.get().isValid(measurementVariable)) {
				properties.add(this.createExperimentProperty(experiment, 1, levelRelationship.getLevelCode(),
					measurementVariable.getTermId()));
			}
		}
	}

	private ExperimentProperty createExperimentProperty(final ExperimentModel experimentModel, final Integer rank, final String value,
		final Integer typeId) {
		final ExperimentProperty experimentProperty = new ExperimentProperty();
		experimentProperty.setExperiment(experimentModel);
		experimentProperty.setRank(rank);
		experimentProperty.setValue(value);
		experimentProperty.setTypeId(typeId);
		return experimentProperty;

	}

	private void addExperimentVariablesIfNecessary(final ObservationUnitImportRequestDto dto,
		final Map<Integer, List<Integer>> plotExperimentVariablesMap, final Map<Integer, DmsProject> trialIdPlotDatasetMap,
		final Map<String, MeasurementVariable> variableNamesMap, final Map<String, MeasurementVariable> variableSynonymsMap) {
		final ObservationUnitPosition position = dto.getObservationUnitPosition();
		final Integer trialDbId = Integer.valueOf(dto.getTrialDbId());
		if (!CollectionUtils.isEmpty(position.getObservationLevelRelationships())) {
			for (final ObservationLevelRelationship levelRelationship : position.getObservationLevelRelationships()) {
				final String variableName = levelRelationship.getLevelName().toUpperCase();
				final MeasurementVariable measurementVariable =
					variableNamesMap.containsKey(variableName) ? variableNamesMap.get(variableName) : variableSynonymsMap.get(variableName);
				if (measurementVariable != null && !plotExperimentVariablesMap.get(trialDbId).contains(measurementVariable.getTermId())) {
					this.addProjectProperty(measurementVariable.getTermId(), variableName, trialDbId, plotExperimentVariablesMap,
						trialIdPlotDatasetMap);
				}
			}
		}

		if (!StringUtils.isEmpty(position.getPositionCoordinateX()) && !StringUtils.isEmpty(position.getPositionCoordinateY())) {
			if (!plotExperimentVariablesMap.get(trialDbId).contains(TermId.RANGE_NO.getId())) {
				this.addProjectProperty(TermId.RANGE_NO.getId(), "FIELDMAP RANGE", trialDbId, plotExperimentVariablesMap,
					trialIdPlotDatasetMap);
			}
			if (!plotExperimentVariablesMap.get(trialDbId).contains(TermId.COLUMN_NO.getId())) {
				this.addProjectProperty(TermId.COLUMN_NO.getId(), "FIELDMAP COLUMN", trialDbId, plotExperimentVariablesMap,
					trialIdPlotDatasetMap);
			}
		}
	}

	private void addProjectProperty(final Integer termId, final String variableName, final Integer trialDbId,
		final Map<Integer, List<Integer>> plotExperimentVariablesMap, final Map<Integer, DmsProject> trialIdPlotDatasetMap) {
		final ProjectProperty projectProperty = new ProjectProperty();
		projectProperty.setProject(trialIdPlotDatasetMap.get(trialDbId));
		projectProperty.setRank(plotExperimentVariablesMap.get(trialDbId).size());
		projectProperty.setTypeId(VariableType.EXPERIMENTAL_DESIGN.getId());
		projectProperty.setVariableId(termId);
		projectProperty.setAlias(variableName);
		trialIdPlotDatasetMap.get(trialDbId).addProperty(projectProperty);
		plotExperimentVariablesMap.get(trialDbId).add(termId);
	}

	private Map<Integer, List<Integer>> populatePlotExperimentVariablesMap(final Map<Integer, DmsProject> plotDatasetMap) {
		final Map<Integer, List<Integer>> plotExperimentVariablesMap = new HashMap<>();
		for (final Map.Entry<Integer, DmsProject> plotDatasetEntry : plotDatasetMap.entrySet()) {
			final Integer key = plotDatasetEntry.getKey();
			final DmsProject plotDataset = plotDatasetEntry.getValue();
			plotExperimentVariablesMap.put(key,
				plotDataset.getProperties().stream().filter(p -> p.getTypeId().equals(VariableType.EXPERIMENTAL_DESIGN.getId()))
					.map(ProjectProperty::getVariableId).collect(Collectors.toList()));
		}
		return plotExperimentVariablesMap;
	}

	private StockModel createStockModel(final GermplasmDTO germplasmDTO, final Map<Integer, MultiKeyMap> stockMap,
		final ObservationUnitImportRequestDto dto, final Integer trialDbId, final Map<String, Enumeration> entryTypes,
		final Map<String, Map<String, Enumeration>> entryTypesMap, final String cross) {

		final StockModel stockModel = new StockModel();
		stockModel.setCross(cross);

		final Optional<String> entryNoOptional =
			!MapUtils.isEmpty(dto.getAdditionalInfo()) ? Optional.ofNullable(dto.getAdditionalInfo().getOrDefault(ENTRY_NO, null)) :
				Optional.empty();

		if (entryNoOptional.isPresent()) {
			// TODO: create entry_code as property
			stockModel.setUniqueName(entryNoOptional.get());
		} else {
			final int entryNo = !stockMap.containsKey(trialDbId) ? 1 : stockMap.get(trialDbId).size() + 1;
			stockModel.setUniqueName(Integer.toString(entryNo));
		}

		stockModel.setProject(new DmsProject(Integer.valueOf(dto.getTrialDbId())));
		stockModel.setIsObsolete(false);

		final Germplasm germplasm = new Germplasm();
		germplasm.setGid(Integer.valueOf(germplasmDTO.getGid()));
		germplasm.setGermplasmUUID(germplasmDTO.getGermplasmDbId());
		stockModel.setGermplasm(germplasm);

		Enumeration entryType = entryTypes.get(dto.getObservationUnitPosition().getEntryType().toUpperCase());
		if (entryType == null) {
			if (!entryTypesMap.containsKey(dto.getProgramDbId())) {
				entryTypesMap.put(dto.getProgramDbId(),
					this.ontologyService.getStandardVariable(TermId.ENTRY_TYPE.getId(), dto.getProgramDbId()).getEnumerations()
						.stream()
						.collect(Collectors.toMap(enumeration -> enumeration.getDescription().toUpperCase(), enumeration -> enumeration)));
			}
			entryType = entryTypesMap.get(dto.getProgramDbId()).get(dto.getObservationUnitPosition().getEntryType().toUpperCase());
		}

		final StockProperty stockProperty =
			new StockProperty(stockModel, TermId.ENTRY_TYPE.getId(), entryType.getName(), entryType.getId());
		final Set<StockProperty> properties = new HashSet<>();
		properties.add(stockProperty);
		stockModel.setProperties(properties);
		this.daoFactory.getStockDao().save(stockModel);
		return stockModel;
	}

	private void setExperimentExternalReferences(final ObservationUnitImportRequestDto dto, final ExperimentModel experimentModel) {
		if (dto.getExternalReferences() != null) {
			final List<ExperimentExternalReference> references = new ArrayList<>();
			dto.getExternalReferences().forEach(reference -> {
				final ExperimentExternalReference externalReference =
					new ExperimentExternalReference(experimentModel, reference.getReferenceID(), reference.getReferenceSource());
				references.add(externalReference);
			});
			experimentModel.setExternalReferences(references);
		}
	}

	/*
	 * FIXME IBP-4289
	 *  - ObservationLevelRelationship not consistent with /observationlevels
	 *  - dto carrying different formats at different points in the call hierarchy (perhaps map directly before saving to ndexpprops?)
	 *  - See ObservationLevelMapper
	 */
	private void renameObservationLevelNamesToBeSaved(final List<ObservationLevelRelationship> relationships) {
		if (!CollectionUtils.isEmpty(relationships)) {
			//Convert observation level relationship names to their equivalent in BMS database
			for (final ObservationLevelRelationship relationship : relationships) {
				if (ObservationLevelEnum.PLOT.getLevelName().equalsIgnoreCase(relationship.getLevelName())) {
					relationship.setLevelName(PLOT_NO);
				} else if (ObservationLevelEnum.REP.getLevelName().equalsIgnoreCase(relationship.getLevelName())) {
					relationship.setLevelName(REP_NO);
				} else if (ObservationLevelEnum.BLOCK.getLevelName().equalsIgnoreCase(relationship.getLevelName())) {
					relationship.setLevelName(BLOCK_NO);
				}
			}
		}
	}

	private void renameObservationLevelNamesToBeDisplayed(final List<ObservationLevelRelationship> relationships) {
		if (!CollectionUtils.isEmpty(relationships)) {
			//Convert observation level relationship names to the accepted values for BRAPI.
			//Reference: https://app.swaggerhub.com/apis/PlantBreedingAPI/BrAPI-Phenotyping/2.0#/ObservationUnitHierarchyLevel
			for (final ObservationLevelRelationship relationship : relationships) {
				if (PLOT_NO.equalsIgnoreCase(relationship.getLevelName())) {
					relationship.setLevelName(ObservationLevelEnum.PLOT.getLevelName());
				} else if (REP_NO.equalsIgnoreCase(relationship.getLevelName())) {
					relationship.setLevelName(ObservationLevelEnum.REP.getLevelName());
				} else if (BLOCK_NO.equalsIgnoreCase(relationship.getLevelName())) {
					relationship.setLevelName(ObservationLevelEnum.BLOCK.getLevelName());
				}
			}
		}
	}

}
