package org.generationcp.middleware.api.brapi.v2.observationunit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.generationcp.middleware.api.brapi.v1.germplasm.GermplasmDTO;
import org.generationcp.middleware.api.germplasm.GermplasmService;
import org.generationcp.middleware.dao.dms.ExperimentDao;
import org.generationcp.middleware.domain.dms.Enumeration;
import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.domain.search_request.brapi.v1.GermplasmSearchRequestDto;
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
import org.generationcp.middleware.service.api.ontology.VariableDataValidatorFactory;
import org.generationcp.middleware.service.api.ontology.VariableValueValidator;
import org.generationcp.middleware.service.api.phenotype.ObservationUnitDto;
import org.generationcp.middleware.service.api.phenotype.ObservationUnitSearchRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Transactional
public class ObservationUnitServiceImpl implements ObservationUnitService {

	private static final Logger LOG = LoggerFactory.getLogger(ObservationUnitServiceImpl.class);

	private ObjectMapper jacksonMapper;

	@Resource
	private GermplasmService germplasmService;

	@Resource
	private OntologyService ontologyService;

	@Resource
	private VariableDataValidatorFactory variableDataValidatorFactory;

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
		final ExperimentModel experimentModel = experimentDao.getByObsUnitId(observationUnitDbId);

		if (experimentModel == null) {
			throw new MiddlewareRequestException("", "invalid.observation.unit.id");
		}

		try {
			final ObjectMapper mapper = new ObjectMapper();
			final String props = experimentModel.getJsonProps() != null ? experimentModel.getJsonProps() : "{}";
			final Map<String, Object> propsMap = mapper.readValue(props, HashMap.class);
			propsMap.put("geoCoordinates", requestDTO.getObservationUnitPosition().getGeoCoordinates());
			experimentModel.setJsonProps(mapper.writeValueAsString(propsMap));
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
		return this.daoFactory.getPhenotypeDAO().searchObservationUnits(pageSize, pageNumber, requestDTO);
	}

	@Override
	public long countObservationUnits(final ObservationUnitSearchRequestDTO requestDTO) {
		return this.daoFactory.getPhenotypeDAO().countObservationUnits(requestDTO);
	}

	public List<ObservationUnitDto> importObservationUnits(final String crop,
		final List<ObservationUnitImportRequestDto> requestDtos, final Integer userId) {
		final CropType cropType = this.daoFactory.getCropTypeDAO().getByName(crop);

		final List<String> germplasmDbIds =
			requestDtos.stream().map(ObservationUnitImportRequestDto::getGermplasmDbId).collect(Collectors.toList());
		final GermplasmSearchRequestDto germplasmSearchRequestDto = new GermplasmSearchRequestDto();
		germplasmSearchRequestDto.setGermplasmDbIds(germplasmDbIds);
		final Map<String, GermplasmDTO> germplasmDTOMap = this.germplasmService.searchFilteredGermplasm(germplasmSearchRequestDto, null)
			.stream().collect(Collectors.toMap(GermplasmDTO::getGermplasmDbId, Function.identity()));

		final List<Integer> trialIds = requestDtos.stream().map(r -> Integer.valueOf(r.getTrialDbId())).collect(Collectors.toList());
		final List<String> variableNames = new ArrayList<>();
		requestDtos.stream().forEach(dto -> {
			if (!CollectionUtils.isEmpty(dto.getObservationUnitPosition().getObservationLevelRelationships())) {
				variableNames.addAll(dto.getObservationUnitPosition().getObservationLevelRelationships().stream()
					.map(ObservationLevelRelationship::getLevelName).collect(Collectors.toList()));
			}
		});

		final Map<Integer, List<StockModel>> stockMap = this.daoFactory.getStockDao().getStockMapByStudyIds(trialIds);
		final Map<Integer, List<String>> trialIdGermplasmUUIDMap = this.convertToGermplasmUUIDs(stockMap);

		final Map<Integer, DmsProject> trialIdPlotDatasetMap =
			this.daoFactory.getDmsProjectDAO().getDatasetsByTypeForStudy(trialIds, DatasetTypeEnum.PLOT_DATA.getId()).stream()
				.collect(Collectors.toMap(plotDataset -> plotDataset.getStudy().getProjectId(), Function.identity()));
		final Map<Integer, List<Integer>> plotExperimentVariablesMap = this.populatePlotExperimentVariablesMap(trialIdPlotDatasetMap);

		final Map<String, MeasurementVariable> variableNamesMap =
			this.daoFactory.getCvTermDao().getVariablesByNamesAndVariableType(variableNames, VariableType.EXPERIMENTAL_DESIGN);
		final Map<String, MeasurementVariable> variableSynonymsMap =
			this.daoFactory.getCvTermDao().getVariablesBySynonymsAndVariableType(variableNames, VariableType.EXPERIMENTAL_DESIGN);
		final List<Integer> categoricalVariableIds = new ArrayList<>();
		categoricalVariableIds.addAll(
			variableNamesMap.values().stream().filter(var -> DataType.CATEGORICAL_VARIABLE.getId().equals(var.getDataTypeId()))
				.map(MeasurementVariable::getTermId).collect(
				Collectors.toList()));
		categoricalVariableIds.addAll(
			variableSynonymsMap.values().stream().filter(var -> DataType.CATEGORICAL_VARIABLE.getId().equals(var.getDataTypeId()))
				.map(MeasurementVariable::getTermId).collect(
				Collectors.toList()));
		final Map<Integer, List<ValueReference>> categoricalVariablesMap =
			this.daoFactory.getCvTermRelationshipDao().getCategoriesForCategoricalVariables(categoricalVariableIds);

		final List<String> observationUnitDbIds = new ArrayList<>();
		for (final ObservationUnitImportRequestDto dto : requestDtos) {
			final Integer trialDbId = Integer.valueOf(dto.getTrialDbId());
			final Integer studyDbId = Integer.valueOf(dto.getStudyDbId());

			this.addExperimentVariablesIfNecessary(dto, plotExperimentVariablesMap, trialIdPlotDatasetMap, variableNamesMap,
				variableSynonymsMap);
			final Map<String, Integer> entryTypes =
				this.ontologyService.getStandardVariable(TermId.ENTRY_TYPE.getId(), dto.getProgramDbId()).getEnumerations()
					.stream().collect(Collectors.toMap(enumeration -> enumeration.getDescription().toUpperCase(), Enumeration::getId));
			if (!stockMap.containsKey(trialDbId) || !trialIdGermplasmUUIDMap.get(trialDbId).contains(dto.getGermplasmDbId())) {
				final StockModel stockModel =
					this.createStockModel(germplasmDTOMap.get(dto.getGermplasmDbId()), stockMap, dto, trialDbId, entryTypes);
				stockMap.putIfAbsent(trialDbId, new ArrayList<>());
				stockMap.get(trialDbId).add(stockModel);
				trialIdGermplasmUUIDMap.putIfAbsent(trialDbId, new ArrayList<>());
				trialIdGermplasmUUIDMap.get(trialDbId).add(dto.getGermplasmDbId());
			}

			final ExperimentModel experimentModel = new ExperimentModel();
			experimentModel.setProject(trialIdPlotDatasetMap.get(trialDbId));
			experimentModel.setGeoLocation(new Geolocation(studyDbId));
			experimentModel.setTypeId(TermId.PLOT_EXPERIMENT.getId());
			experimentModel.setStock(this.findStock(stockMap.get(trialDbId), dto.getGermplasmDbId()));
			this.setJsonProps(experimentModel, dto);
			ObservationUnitIDGenerator.generateObservationUnitIds(cropType, Collections.singletonList(experimentModel));
			this.addExperimentProperties(experimentModel, dto, variableNamesMap, variableSynonymsMap, categoricalVariablesMap);
			this.setExperimentExternalReferences(dto, experimentModel);
			this.daoFactory.getExperimentDao().save(experimentModel);

			this.sessionProvider.getSession().flush();

			observationUnitDbIds.add(experimentModel.getObsUnitId());
		}

		//Update environment dataset to save added project properties
		for (final Integer trialId : trialIds) {
			this.daoFactory.getDmsProjectDAO().update(trialIdPlotDatasetMap.get(trialId));
			this.sessionProvider.getSession().flush();
		}
		final ObservationUnitSearchRequestDTO searchRequestDTO = new ObservationUnitSearchRequestDTO();
		searchRequestDTO.setObservationUnitDbIds(observationUnitDbIds);
		return this.searchObservationUnits(null, null, searchRequestDTO);
	}

	private void setJsonProps(final ExperimentModel model, final ObservationUnitImportRequestDto dto) {
		if(dto.getObservationUnitPosition().getGeoCoordinates() != null) {
			try {
				model.setJsonProps(this.jacksonMapper.writeValueAsString(dto.getObservationUnitPosition().getGeoCoordinates()));
			} catch (JsonProcessingException e) {
				// Just ignore if there's an issue with mapping
				model.setJsonProps(null);
			}
		}
	}

	private void addExperimentProperties(final ExperimentModel experiment, final ObservationUnitImportRequestDto dto,
		final Map<String, MeasurementVariable> variableNamesMap, final Map<String, MeasurementVariable> variableSynonymsMap,
		final Map<Integer, List<ValueReference>> categoricalVariablesMap) {
		final List<ExperimentProperty> properties = new ArrayList<>();
		final ObservationUnitPositionImportRequestDto position = dto.getObservationUnitPosition();
		if (!CollectionUtils.isEmpty(position.getObservationLevelRelationships())) {
			for (ObservationLevelRelationship levelRelationship : position.getObservationLevelRelationships()) {
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
		}

		if (!StringUtils.isEmpty(position.getPositionCoordinateX()) && !StringUtils.isEmpty(position.getPositionCoordinateY())) {
			properties.add(this.createExperimentProperty(experiment, 0, position.getPositionCoordinateX(), TermId.RANGE_NO.getId()));
			properties.add(this.createExperimentProperty(experiment, 0, position.getPositionCoordinateY(), TermId.COLUMN_NO.getId()));
		}
		experiment.setProperties(properties);

	}

	private ExperimentProperty createExperimentProperty(final ExperimentModel experimentModel, final Integer rank, final String value,
		final Integer typeId) {
		final ExperimentProperty experimentProperty = new ExperimentProperty();
		experimentProperty.setExperiment(experimentModel);
		experimentProperty.setRank(1);
		experimentProperty.setValue(value);
		experimentProperty.setTypeId(typeId);
		return experimentProperty;

	}

	private void addExperimentVariablesIfNecessary(final ObservationUnitImportRequestDto dto,
		final Map<Integer, List<Integer>> plotExperimentVariablesMap, final Map<Integer, DmsProject> trialIdPlotDatasetMap,
		final Map<String, MeasurementVariable> variableNamesMap, final Map<String, MeasurementVariable> variableSynonymsMap) {
		final ObservationUnitPositionImportRequestDto position = dto.getObservationUnitPosition();
		final Integer trialDbId = Integer.valueOf(dto.getTrialDbId());
		if (!CollectionUtils.isEmpty(position.getObservationLevelRelationships())) {
			for (ObservationLevelRelationship levelRelationship : position.getObservationLevelRelationships()) {
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
		for (Map.Entry plotDatasetEntry : plotDatasetMap.entrySet()) {
			final Integer key = (Integer) plotDatasetEntry.getKey();
			final DmsProject plotDataset = (DmsProject) plotDatasetEntry.getValue();
			plotExperimentVariablesMap.put(key,
				plotDataset.getProperties().stream().filter(p -> p.getTypeId().equals(VariableType.EXPERIMENTAL_DESIGN.getId()))
					.map(ProjectProperty::getVariableId).collect(Collectors.toList()));
		}
		return plotExperimentVariablesMap;
	}

	private StockModel findStock(final List<StockModel> stocks, final String germplasmDbId) {
		for (final StockModel stock : stocks) {
			if (stock.getGermplasm().getGermplasmUUID().equals(germplasmDbId)) {
				return stock;
			}
		}
		return null;
	}

	private Map<Integer, List<String>> convertToGermplasmUUIDs(final Map<Integer, List<StockModel>> stockMap) {
		final Map<Integer, List<String>> trialIdGermplasmUUIDMap = new HashMap<>();
		for (Map.Entry mapElement : stockMap.entrySet()) {
			final Integer key = (Integer) mapElement.getKey();
			final List<StockModel> stocks = (List<StockModel>) mapElement.getValue();
			trialIdGermplasmUUIDMap.put(key, stocks.stream().map(s -> s.getGermplasm().getGermplasmUUID()).collect(Collectors.toList()));
		}
		return trialIdGermplasmUUIDMap;
	}

	private StockModel createStockModel(final GermplasmDTO germplasmDTO, final Map<Integer, List<StockModel>> stockMap,
		final ObservationUnitImportRequestDto dto, final Integer trialDbId, final Map<String, Integer> entryTypes) {
		final StockModel stockModel = new StockModel();
		final Integer entryNo = !stockMap.containsKey(trialDbId) ? 1 : stockMap.get(trialDbId).size() + 1;
		stockModel.setUniqueName(entryNo.toString());
		stockModel.setValue(entryNo.toString());
		stockModel.setName(germplasmDTO.getGermplasmName());
		stockModel.setProject(new DmsProject(Integer.valueOf(dto.getTrialDbId())));
		stockModel.setIsObsolete(false);

		final Germplasm germplasm = new Germplasm();
		germplasm.setGid(Integer.valueOf(germplasmDTO.getGid()));
		germplasm.setGermplasmUUID(germplasmDTO.getGermplasmDbId());
		stockModel.setGermplasm(germplasm);
		stockModel.setTypeId(TermId.ENTRY_CODE.getId());

		final Set<StockProperty> properties = new HashSet<>();
		final StockProperty stockProperty = new StockProperty();
		stockProperty.setStock(stockModel);
		stockProperty.setValue(entryTypes.get(dto.getObservationUnitPosition().getEntryType().toUpperCase()).toString());
		stockProperty.setTypeId(TermId.ENTRY_TYPE.getId());
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
}
