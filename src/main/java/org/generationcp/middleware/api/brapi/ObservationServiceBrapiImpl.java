package org.generationcp.middleware.api.brapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections.map.MultiKeyMap;
import org.generationcp.middleware.api.brapi.v2.germplasm.ExternalReferenceDTO;
import org.generationcp.middleware.api.brapi.v2.observation.ObservationDto;
import org.generationcp.middleware.api.brapi.v2.observation.ObservationSearchRequestDto;
import org.generationcp.middleware.dao.dms.PhenotypeDao;
import org.generationcp.middleware.dao.dms.ProjectPropertyDao;
import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.ontology.daoElements.VariableFilter;
import org.generationcp.middleware.pojos.PhenotypeExternalReference;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.generationcp.middleware.pojos.oms.CVTermProperty;
import org.generationcp.middleware.service.api.user.UserService;
import org.generationcp.middleware.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

@Service
@Transactional
public class ObservationServiceBrapiImpl implements ObservationServiceBrapi {

	public static final String NOT_AVAILABLE_VALUE = "NA";

	private final DaoFactory daoFactory;
	private final HibernateSessionProvider sessionProvider;

	@Autowired
	private UserService userService;

	private final Set<Integer> observationTypeVariables = new HashSet<>();

	public ObservationServiceBrapiImpl(final HibernateSessionProvider sessionProvider) {
		this.sessionProvider = sessionProvider;
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public List<ObservationDto> searchObservations(final ObservationSearchRequestDto observationSearchRequestDto, final Pageable pageable) {
		final List<ObservationDto> observationDtos =
			this.daoFactory.getPhenotypeDAO().searchObservations(observationSearchRequestDto, pageable);
		if (!CollectionUtils.isEmpty(observationDtos)) {

			final Map<Integer, Variable> variablesMap = this.getVariableMap(observationDtos);

			observationDtos.forEach(o -> {
				// If variable is datetime, convert yyyyMMdd to yyyy-MM-dd (brapi standard) format
				final Integer variableId = Integer.valueOf(o.getObservationVariableDbId());
				if (variablesMap.containsKey(variableId) && this.isDateTime(variablesMap.get(variableId))) {
					try {
						o.setValue(Util.convertDate(o.getValue(), Util.DATE_AS_NUMBER_FORMAT, Util.FRONTEND_DATE_FORMAT));
					} catch (final ParseException e) {
						o.setValue(org.apache.commons.lang3.StringUtils.EMPTY);
					}
				}
			});

			final List<Integer> observationDbIds =
				new ArrayList<>(observationDtos.stream().map(o -> Integer.valueOf(o.getObservationDbId()))
					.collect(Collectors.toSet()));
			final Map<String, List<ExternalReferenceDTO>> externalReferencesMap =
				this.daoFactory.getPhenotypeExternalReferenceDAO().getExternalReferences(observationDbIds).stream()
					.collect(groupingBy(
						ExternalReferenceDTO::getEntityId));
			for (final ObservationDto observationDto : observationDtos) {
				observationDto.setExternalReferences(externalReferencesMap.get(observationDto.getObservationDbId()));
			}
		}
		return observationDtos;
	}

	@Override
	public long countObservations(final ObservationSearchRequestDto observationSearchRequestDto) {
		return this.daoFactory.getPhenotypeDAO().countObservations(observationSearchRequestDto);
	}

	@Override
	public List<ObservationDto> createObservations(final List<ObservationDto> observations) {

		final Map<String, ExperimentModel> experimentModelMap = this.daoFactory.getExperimentDao()
			.getByObsUnitIds(observations.stream().map(ObservationDto::getObservationUnitDbId).collect(Collectors.toList())).stream()
			.collect(Collectors.toMap(ExperimentModel::getObsUnitId, Function.identity()));
		final Map<Integer, List<ValueReference>> validValuesForCategoricalVariables =
			this.getCategoriesForCategoricalVariables(observations);

		final Map<Integer, Variable> variablesMap = this.getVariableMap(observations);

		// Automatically associate variables to the study. This is to ensure that the TRAIT, ANALYSIS, ANALYSIS variables
		// are added to their respective dataset before creating the observation.
		this.associateVariablesToDatasets(observations, experimentModelMap, variablesMap);

		final ObservationSearchRequestDto observationSearchRequestDto = new ObservationSearchRequestDto();
		final List<String> observationUnitDbIds =
			observations.stream().filter(o -> org.apache.commons.lang3.StringUtils.isNotEmpty(o.getObservationUnitDbId()))
				.map(ObservationDto::getObservationUnitDbId).collect(Collectors.toList());
		final List<String> variableIds =
			observations.stream().filter(o -> org.apache.commons.lang3.StringUtils.isNotEmpty(o.getObservationVariableDbId()))
				.map(ObservationDto::getObservationVariableDbId).collect(Collectors.toList());

		observationSearchRequestDto.setObservationUnitDbIds(observationUnitDbIds);
		observationSearchRequestDto.setObservationVariableDbIds(variableIds);

		final List<ObservationDto> existingObservations =
			this.searchObservations(observationSearchRequestDto, null);
		final MultiKeyMap existingObservationsMap = new MultiKeyMap();
		if (org.apache.commons.collections.CollectionUtils.isNotEmpty(existingObservations)) {
			for (final ObservationDto existingObservation : existingObservations) {
				existingObservationsMap.put(existingObservation.getObservationUnitDbId(),
					existingObservation.getObservationVariableDbId(), existingObservation);
			}
		}

		final List<String> observationDbIds = new ArrayList<>();
		for (final ObservationDto observation : observations) {

			final Variable variable = variablesMap.get(Integer.valueOf(observation.getObservationVariableDbId()));
			final Phenotype saved;
			if (existingObservationsMap.containsKey(observation.getObservationUnitDbId(),
				observation.getObservationVariableDbId())) {
				saved = this.updateExistingPhenotype(validValuesForCategoricalVariables, observation,
					(ObservationDto) existingObservationsMap.get(observation.getObservationUnitDbId(),
						observation.getObservationVariableDbId()), variable);
			} else {
				saved = this.createNewPhenotype(experimentModelMap, validValuesForCategoricalVariables,
					observation, variable);
			}
			observationDbIds.add(saved.getPhenotypeId().toString());
		}
		this.sessionProvider.getSession().flush();
		if (!CollectionUtils.isEmpty(observationDbIds)) {
			final ObservationSearchRequestDto searchRequestDTO = new ObservationSearchRequestDto();
			searchRequestDTO.setObservationDbIds(observationDbIds);
			return this.searchObservations(searchRequestDTO, null);
		} else {
			return new ArrayList<>();
		}
	}

	private Map<Integer, Variable> getVariableMap(List<ObservationDto> observations) {
		// Get the variables from the request observations
		final Set<Integer> variableIdsFromRequest = observations.stream().map(o -> {
            return Integer.valueOf(o.getObservationVariableDbId());
        }).collect(Collectors.toSet());
		final VariableFilter variableFilter = new VariableFilter();
		variableIdsFromRequest.forEach(variableFilter::addVariableId);
		final Map<Integer, Variable> variablesMap =
				this.daoFactory.getCvTermDao().getVariablesWithFilterById(variableFilter);
		return variablesMap;
	}

	@Override
	public List<ObservationDto> updateObservations(final List<ObservationDto> observations) {
		final Map<Integer, List<ValueReference>> validValuesForCategoricalVariables =
			this.getCategoriesForCategoricalVariables(observations);

		final Map<Integer, Variable> variablesMap = this.getVariableMap(observations);

		final Map<String, ExperimentModel> experimentModelMap = this.daoFactory.getExperimentDao()
			.getByObsUnitIds(observations.stream().map(ObservationDto::getObservationUnitDbId).collect(Collectors.toList())).stream()
			.collect(Collectors.toMap(ExperimentModel::getObsUnitId, Function.identity()));
		this.associateVariablesToDatasets(observations, experimentModelMap, variablesMap);

		final ObservationSearchRequestDto observationSearchRequestDto = new ObservationSearchRequestDto();
		final List<String> observationUnitDbIds =
			observations.stream().filter(o -> org.apache.commons.lang3.StringUtils.isNotEmpty(o.getObservationUnitDbId()))
				.map(ObservationDto::getObservationUnitDbId).collect(Collectors.toList());
		final List<String> variableIds =
			observations.stream().filter(o -> org.apache.commons.lang3.StringUtils.isNotEmpty(o.getObservationVariableDbId()))
				.map(ObservationDto::getObservationVariableDbId).collect(Collectors.toList());
		observationSearchRequestDto.setObservationUnitDbIds(observationUnitDbIds);
		observationSearchRequestDto.setObservationVariableDbIds(variableIds);

		final List<ObservationDto> existingObservations =
			this.searchObservations(observationSearchRequestDto, null);

		final MultiKeyMap existingObservationsMap = new MultiKeyMap();
		if (org.apache.commons.collections.CollectionUtils.isNotEmpty(existingObservations)) {
			for (final ObservationDto existingObservation : existingObservations) {
				existingObservationsMap.put(existingObservation.getObservationUnitDbId(),
					existingObservation.getObservationVariableDbId(), existingObservation);
			}
		}

		final List<String> observationDbIds = new ArrayList<>();
		for (final ObservationDto observation : observations) {
			final Variable variable = variablesMap.get(Integer.valueOf(observation.getObservationVariableDbId()));
			final Phenotype updatedPhenotype;
			if (existingObservationsMap.containsKey(observation.getObservationUnitDbId(),
				observation.getObservationVariableDbId())) {
				updatedPhenotype = this.updateExistingPhenotype(validValuesForCategoricalVariables, observation,
					(ObservationDto) existingObservationsMap.get(observation.getObservationUnitDbId(),
						observation.getObservationVariableDbId()), variable);
				observationDbIds.add(updatedPhenotype.getPhenotypeId().toString());
			}
		}
		this.sessionProvider.getSession().flush();
		if (!CollectionUtils.isEmpty(observationDbIds)) {
			final ObservationSearchRequestDto searchRequestDTO = new ObservationSearchRequestDto();
			searchRequestDTO.setObservationDbIds(observationDbIds);
			return this.searchObservations(searchRequestDTO, null);
		} else {
			return new ArrayList<>();
		}
	}

	private Phenotype createNewPhenotype(final Map<String, ExperimentModel> experimentModelMap,
										 final Map<Integer, List<ValueReference>> validValuesForCategoricalVariables,
										 final ObservationDto observation, final Variable variable) {
		final Phenotype phenotype = new Phenotype();
		this.populatePhenotypeValues(validValuesForCategoricalVariables, observation, phenotype, variable);

		final Date currentDate = new Date();
		final Integer loggedInUser = this.userService.getCurrentlyLoggedInUserId();

		phenotype.setCreatedDate(currentDate);
		phenotype.setUpdatedDate(currentDate);
		phenotype.setCreatedBy(loggedInUser);
		phenotype.setUpdatedBy(loggedInUser);
		phenotype.setObservableId(Integer.valueOf(observation.getObservationVariableDbId()));
		phenotype.setExperiment(experimentModelMap.get(observation.getObservationUnitDbId()));

		this.populatePhenotypeJsonProps(observation, phenotype);

		this.setPhenotypeExternalReferences(observation, phenotype);
		return this.daoFactory.getPhenotypeDAO().save(phenotype);
	}

	private Phenotype updateExistingPhenotype(final Map<Integer, List<ValueReference>> validValuesForCategoricalVariables,
											  final ObservationDto inputObservation, final ObservationDto existingObservation, final Variable variable) {

		final PhenotypeDao dao = this.daoFactory.getPhenotypeDAO();
		final Phenotype existingPhenotype = dao.getById(Integer.parseInt(existingObservation.getObservationDbId()));

		if (!StringUtils.isEmpty(existingObservation.getValue()) && existingObservation.getValue()
			.equalsIgnoreCase(inputObservation.getValue())) {
			/*Phenotype exists and imported value is equal to value => Erase draft data*/
			existingPhenotype.setDraftValue(null);
			existingPhenotype.setDraftCValueId(null);
		} else {
			this.populatePhenotypeValues(validValuesForCategoricalVariables, inputObservation, existingPhenotype, variable);
		}
		this.populatePhenotypeJsonProps(inputObservation, existingPhenotype);

		// Add or update observation/phenotype instance external references
		this.addOrUpdateInstanceExternalReferences(inputObservation.getExternalReferences(), existingPhenotype);

		return dao.update(existingPhenotype);
	}

	private void populatePhenotypeJsonProps(final ObservationDto inputObservation, final Phenotype phenotype) {
		// update json props from observation request
		if (!CollectionUtils.isEmpty(inputObservation.getAdditionalInfo())) {
			try {
				phenotype.setJsonProps(new ObjectMapper().writeValueAsString(inputObservation.getAdditionalInfo()));
			} catch (final JsonProcessingException e) {
				throw new MiddlewareQueryException("Error in updatePhenotypeJsonProps in ObservationServiceBrapiImpl: "
					+ e.getMessage(), e);
			}
		}
	}

	private void addOrUpdateInstanceExternalReferences(final List<ExternalReferenceDTO> externalReferenceDTOList,
		final Phenotype existingPhenotype) {
		final Map<String, PhenotypeExternalReference> phenotypeExternalReferenceMap =
			existingPhenotype.getExternalReferences().stream().collect(toMap(PhenotypeExternalReference::getSource, Function.identity()));
		if (!CollectionUtils.isEmpty(externalReferenceDTOList)) {
			externalReferenceDTOList.forEach(reference -> {
				if (phenotypeExternalReferenceMap.containsKey(reference.getReferenceSource())) {
					phenotypeExternalReferenceMap.get(reference.getReferenceSource()).setReferenceId(reference.getReferenceID());
				} else {
					final PhenotypeExternalReference externalReference =
						new PhenotypeExternalReference(existingPhenotype, reference.getReferenceID(), reference.getReferenceSource());
					existingPhenotype.getExternalReferences().add(externalReference);
				}
			});
		}
	}

	private void populatePhenotypeValues(final Map<Integer, List<ValueReference>> validValuesForCategoricalVariables,
                                         final ObservationDto observation,
                                         final Phenotype phenotype, final Variable variable) {
		final String variableId = observation.getObservationVariableDbId();
		final boolean isObservationType = this.observationTypeVariables.contains(Integer.parseInt(variableId));

		final Optional<Integer> categoricalIdOptional =
			this.resolveCategoricalIdValue(Integer.valueOf(observation.getObservationVariableDbId()), observation.getValue(),
				validValuesForCategoricalVariables);

        String value = observation.getValue();

        // NA (Not Available) in statistics is synonymous to "missing" value in BMS.
        // So we need to convert NA value to "missing"
        if (NOT_AVAILABLE_VALUE.equals(observation.getValue())) {
            // Only categorical and numeric type variables support "missing" value,
            // so for other data types, NA (Not Available) should be treated as empty string.
            value = this.isNumericOrCategorical(variable) ? Phenotype.MISSING_VALUE : org.apache.commons.lang3.StringUtils.EMPTY;
        } else if (this.isDateTime(variable)) {
			// If variable is datetime convert yyyy-MM-dd (brapi standard) to yyyyMMdd format
			try {
				value = Util.convertDate(observation.getValue(), Util.FRONTEND_DATE_FORMAT, Util.DATE_AS_NUMBER_FORMAT);
			} catch (final ParseException e) {
				value = org.apache.commons.lang3.StringUtils.EMPTY;
			}
		}

		if (isObservationType) {
			phenotype.setDraftValue(value);
			if (categoricalIdOptional.isPresent()) {
				phenotype.setDraftCValueId(categoricalIdOptional.get());
			}
		} else {
			phenotype.setValue(observation.getValue());
			if (categoricalIdOptional.isPresent()) {
				phenotype.setcValue(categoricalIdOptional.get());
			}
		}
	}

    private boolean isNumericOrCategorical(final Variable variable) {
        return variable.getScale().getDataType().equals(DataType.NUMERIC_VARIABLE)
                || variable.getScale().getDataType().equals(DataType.CATEGORICAL_VARIABLE);
    }

	private boolean isDateTime(final Variable variable) {
		return variable.getScale().getDataType().equals(DataType.DATE_TIME_VARIABLE);
	}

    private void setPhenotypeExternalReferences(final ObservationDto observationDto, final Phenotype phenotype) {
		if (observationDto.getExternalReferences() != null) {
			final List<PhenotypeExternalReference> references = new ArrayList<>();
			observationDto.getExternalReferences().forEach(reference -> {
				final PhenotypeExternalReference externalReference =
					new PhenotypeExternalReference(phenotype, reference.getReferenceID(), reference.getReferenceSource());
				references.add(externalReference);
			});
			phenotype.setExternalReferences(references);
		}
	}

	private void associateVariablesToDatasets(final List<ObservationDto> observations,
		final Map<String, ExperimentModel> experimentModelMap, final Map<Integer, Variable> variablesMap) {

		// Get the variables grouped by dataset from the request observations
		final Map<Integer, Set<Integer>> projectVariablesMap =
			observations.stream()
				.collect(Collectors.groupingBy(o -> experimentModelMap.get(o.getObservationUnitDbId()).getProject().getProjectId(),
					Collectors.mapping(o -> Integer.valueOf(o.getObservationVariableDbId()), toSet())));

		final Map<Integer, DmsProject> dmsProjectMap = this.daoFactory.getDmsProjectDAO().getByIds(projectVariablesMap.keySet()).stream()
			.collect(toMap(DmsProject::getProjectId, Function.identity()));

		// Get the variableTypes of the variables from the request
		final Map<Integer, List<VariableType>> variableTypesOfVariables =
			this.daoFactory.getCvTermPropertyDao()
				.getByCvTermIdsAndType(new ArrayList<>(variablesMap.keySet()), TermId.VARIABLE_TYPE.getId()).stream()
				.collect(groupingBy(CVTermProperty::getCvTermId, Collectors.mapping(o -> VariableType.getByName(o.getValue()), toList())));

		// Get the existing variables associated to the datasets
		final Map<Integer, Map<Integer, ProjectProperty>> datasetVariablesMap =
			this.daoFactory.getProjectPropertyDAO().getPropsForProjectIds(new ArrayList<>(projectVariablesMap.keySet())).entrySet().stream()
				.collect(toMap(Map.Entry::getKey,
					entry -> entry.getValue().stream().collect(toMap(ProjectProperty::getVariableId, Function.identity()))));

		// Associate the variables to their respective datasets
		projectVariablesMap.entrySet().forEach(entry -> {
			final DmsProject dmsProject = dmsProjectMap.get(entry.getKey());
			final Set<Integer> variableIds = entry.getValue();
			variableIds.forEach(variableId -> {
				// Do not proceed if variable doesn't have variable types
				if (!variableTypesOfVariables.containsKey(variableId))
					return;

				final boolean isVariableExistingInDataset =
					datasetVariablesMap.containsKey(dmsProject.getProjectId()) && datasetVariablesMap.get(dmsProject.getProjectId())
						.containsKey(variableId);
				this.assignVariableToDataset(variableTypesOfVariables, dmsProject, variableId, variablesMap.get(variableId),
					isVariableExistingInDataset);

			});
		});
	}

	private void assignVariableToDataset(final Map<Integer, List<VariableType>> variableTypesOfVariables, final DmsProject dmsProject,
		final Integer variableId, final Variable variable, final boolean isVariableExistingInDataset) {
		if ((dmsProject.getDatasetType().isObservationType() || dmsProject.getDatasetType().isSubObservationType())
			&& variableTypesOfVariables.get(variableId).contains(VariableType.TRAIT)) {
			this.addProjectPropertyToDataset(dmsProject, variable, VariableType.TRAIT, isVariableExistingInDataset);
			this.observationTypeVariables.add(variableId);
		} else if ((dmsProject.getDatasetType().isObservationType() || dmsProject.getDatasetType().isSubObservationType())
			&& variableTypesOfVariables.get(variableId).contains(VariableType.SELECTION_METHOD)) {
			this.addProjectPropertyToDataset(dmsProject, variable, VariableType.SELECTION_METHOD, isVariableExistingInDataset);
			this.observationTypeVariables.add(variableId);
		} else if (dmsProject.getDatasetType().getDatasetTypeId() == DatasetTypeEnum.MEANS_DATA.getId()
			&& variableTypesOfVariables.get(variableId).contains(VariableType.ANALYSIS)) {
			this.addProjectPropertyToDataset(dmsProject, variable, VariableType.ANALYSIS, isVariableExistingInDataset);
		} else if (dmsProject.getDatasetType().getDatasetTypeId() == DatasetTypeEnum.SUMMARY_STATISTICS_DATA.getId()
			&& variableTypesOfVariables.get(variableId).contains(VariableType.ANALYSIS_SUMMARY)) {
			this.addProjectPropertyToDataset(dmsProject, variable, VariableType.ANALYSIS_SUMMARY, isVariableExistingInDataset);
		}
	}

	private void addProjectPropertyToDataset(final DmsProject dmsProject, final Variable variable, final VariableType variableType,
		final boolean isVariableExistingInDataset) {
		if (isVariableExistingInDataset)
			return;

		final ProjectPropertyDao projectPropertyDao = this.daoFactory.getProjectPropertyDAO();
		final ProjectProperty projectProperty = new ProjectProperty();
		projectProperty.setProject(dmsProject);
		projectProperty.setTypeId(variableType.getId());
		projectProperty.setAlias(StringUtils.isEmpty(variable.getAlias()) ? variable.getName() : variable.getAlias());
		projectProperty.setVariableId(Integer.valueOf(variable.getId()));
		projectProperty.setRank(projectPropertyDao.getNextRank(dmsProject.getProjectId()));
		projectPropertyDao.save(projectProperty);
	}

	private Map<Integer, List<ValueReference>> getCategoriesForCategoricalVariables(final List<ObservationDto> observations) {
		final Set<Integer> variableIds =
			observations.stream().map(o -> Integer.valueOf(o.getObservationVariableDbId())).collect(Collectors.toSet());
		return this.daoFactory.getCvTermRelationshipDao().getCategoriesForCategoricalVariables(new ArrayList<>(variableIds));
	}

	private Optional<Integer> resolveCategoricalIdValue(final Integer variableId, final String value,
		final Map<Integer, List<ValueReference>> validValuesForCategoricalVariables) {
		if (validValuesForCategoricalVariables.containsKey(variableId)) {
			final Optional<ValueReference> match =
				validValuesForCategoricalVariables.get(variableId).stream().filter(v -> v.getName().equals(value)).findAny();
			return match.isPresent() ? Optional.of(Integer.valueOf(match.get().getKey())) : Optional.empty();
		}
		return Optional.empty();
	}
}
