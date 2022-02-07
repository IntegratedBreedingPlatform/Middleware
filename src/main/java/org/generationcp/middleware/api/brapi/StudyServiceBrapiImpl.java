package org.generationcp.middleware.api.brapi;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.api.brapi.v2.germplasm.ExternalReferenceDTO;
import org.generationcp.middleware.api.brapi.v2.study.StudyImportRequestDTO;
import org.generationcp.middleware.api.location.LocationDTO;
import org.generationcp.middleware.api.location.search.LocationSearchRequest;
import org.generationcp.middleware.domain.dms.ExperimentType;
import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.InstanceExternalReference;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.GeolocationProperty;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.service.api.ontology.CategoricalValueNameValidator;
import org.generationcp.middleware.service.api.ontology.VariableDataValidatorFactory;
import org.generationcp.middleware.service.api.ontology.VariableValueValidator;
import org.generationcp.middleware.service.api.study.EnvironmentParameter;
import org.generationcp.middleware.service.api.study.ObservationLevel;
import org.generationcp.middleware.service.api.study.StudyDetailsDto;
import org.generationcp.middleware.service.api.study.StudyInstanceDto;
import org.generationcp.middleware.service.api.study.StudyMetadata;
import org.generationcp.middleware.service.api.study.StudySearchFilter;
import org.generationcp.middleware.service.api.study.generation.ExperimentDesignService;
import org.generationcp.middleware.service.api.user.UserDto;
import org.generationcp.middleware.service.api.user.UserService;
import org.generationcp.middleware.service.impl.study.StudyInstanceServiceImpl;
import org.generationcp.middleware.service.impl.study.generation.ExperimentModelGenerator;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
@Transactional
public class StudyServiceBrapiImpl implements StudyServiceBrapi {

	private static final String EXPT_DESIGN = "EXPT_DESIGN";
	private static final String CROP_SEASON_CODE = "Crop_season_Code";

	@Resource
	private ExperimentModelGenerator experimentModelGenerator;

	@Resource
	private ExperimentDesignService experimentDesignService;

	@Resource
	private VariableDataValidatorFactory variableDataValidatorFactory;

	@Resource
	private UserService userService;

	private final DaoFactory daoFactory;
	private final HibernateSessionProvider sessionProvider;


	public StudyServiceBrapiImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
		this.sessionProvider = sessionProvider;
	}

	@Override
	public Optional<StudyDetailsDto> getStudyDetailsByInstance(final Integer instanceId) {
			final StudyMetadata studyMetadata = this.daoFactory.getDmsProjectDAO().getStudyMetadataForInstanceId(instanceId);
		if (studyMetadata != null) {
			final StudyDetailsDto studyDetailsDto = new StudyDetailsDto();
			studyDetailsDto.setMetadata(studyMetadata);

			final List<UserDto> users = new ArrayList<>();
			users.addAll(this.getUsersForEnvironment(studyMetadata.getStudyDbId()));
			studyDetailsDto.setContacts(users);

			final DmsProject environmentDataset =
				this.daoFactory.getDmsProjectDAO()
					.getDatasetsByTypeForStudy(studyMetadata.getTrialDbId(), DatasetTypeEnum.SUMMARY_DATA.getId()).get(0);
			final List<MeasurementVariable> environmentConditions = this.daoFactory.getDmsProjectDAO()
				.getObservationSetVariables(environmentDataset.getProjectId(),
					Lists.newArrayList(VariableType.ENVIRONMENT_CONDITION.getId()));
			final List<MeasurementVariable> environmentParameters = new ArrayList<>();
			List<Integer> variableIds = environmentConditions.stream().map(MeasurementVariable::getTermId)
				.collect(Collectors.toList());
			if (!variableIds.isEmpty()) {
				final List<MeasurementVariable> measurementVariables = this.daoFactory.getPhenotypeDAO()
					.getEnvironmentConditionVariablesByGeoLocationIdAndVariableIds(
						Collections.singletonList(instanceId), variableIds).get(instanceId);
				if (!CollectionUtils.isEmpty(measurementVariables)) {
					environmentParameters.addAll(measurementVariables);
				}
			}
			final List<MeasurementVariable> environmentDetails = this.daoFactory.getDmsProjectDAO()
				.getObservationSetVariables(environmentDataset.getProjectId(),
					Lists.newArrayList(VariableType.ENVIRONMENT_DETAIL.getId()));
			variableIds = environmentDetails.stream().map(MeasurementVariable::getTermId)
				.collect(Collectors.toList());
			if (!variableIds.isEmpty()) {
				final List<MeasurementVariable> measurementVariables = this.daoFactory.getGeolocationPropertyDao()
					.getEnvironmentDetailVariablesByGeoLocationIdAndVariableIds(
						Collections.singletonList(instanceId), variableIds).get(instanceId);
				if (!CollectionUtils.isEmpty(measurementVariables)) {
					environmentParameters.addAll(measurementVariables);
				}
			}

			final List<MeasurementVariable> environmentVariables = new ArrayList<>(environmentConditions);
			environmentVariables.addAll(environmentDetails);
			environmentParameters.addAll(this.createGeolocationVariables(environmentVariables, instanceId));
			studyDetailsDto.setEnvironmentParameters(environmentParameters);

			final Map<String, String> properties = new HashMap<>();
			variableIds = environmentVariables.stream().map(MeasurementVariable::getTermId)
				.collect(Collectors.toList());
			properties.put("studyObjective", studyMetadata.getStudyObjective() == null ? "" : studyMetadata.getStudyObjective());
			final Map<String, String> geolocationMap = this.daoFactory.getGeolocationPropertyDao()
				.getGeolocationPropsAndValuesByGeolocation(Collections.singletonList(instanceId), variableIds).get(instanceId);
			if (geolocationMap != null) {
				properties.putAll(geolocationMap);
			}

			final Map<Integer, Map<String, String>> projectPropMap =
				this.daoFactory.getProjectPropertyDAO().getProjectPropsAndValuesByStudyIds(
					Collections.singletonList(studyMetadata.getNurseryOrTrialId()));
			if (projectPropMap.containsKey(studyMetadata.getNurseryOrTrialId())) {
				properties.putAll(projectPropMap.get(studyMetadata.getNurseryOrTrialId()));
			}
			studyDetailsDto.setAdditionalInfo(properties);
			return Optional.of(studyDetailsDto);
		}
		return Optional.empty();

	}

	@Override
	public List<StudyInstanceDto> getStudyInstances(final StudySearchFilter studySearchFilter, final Pageable pageable) {
		return this.daoFactory.getDmsProjectDAO().getStudyInstances(studySearchFilter, pageable);
	}

	@Override
	public List<StudyInstanceDto> getStudyInstancesWithMetadata(final StudySearchFilter studySearchFilter, final Pageable pageable) {
		final List<StudyInstanceDto> studyInstanceDtos = this.daoFactory.getDmsProjectDAO()
			.getStudyInstances(studySearchFilter, pageable);
		if (!CollectionUtils.isEmpty(studyInstanceDtos)) {
			final List<Integer> studyIds = new ArrayList<>(studyInstanceDtos.stream().map(o -> Integer.valueOf(o.getTrialDbId()))
				.collect(Collectors.toSet()));
			final List<Integer> studyInstanceIds =
				new ArrayList<>(studyInstanceDtos.stream().map(o -> Integer.valueOf(o.getStudyDbId()))
					.collect(Collectors.toSet()));
			final Map<Integer, List<ObservationLevel>> observationLevelsMap = this.daoFactory.getDmsProjectDAO()
				.getObservationLevelsMap(studyIds);
			final Map<Integer, Integer> studyEnvironmentDatasetIdMap = this.daoFactory.getDmsProjectDAO()
				.getStudyIdEnvironmentDatasetIdMap(studyIds);

			final Map<Integer, List<MeasurementVariable>> studyEnvironmentVariablesMap = new HashMap<>();
			final Map<Integer, Map<String, String>> studyAdditionalInfoMap = this.daoFactory.getProjectPropertyDAO()
				.getProjectPropsAndValuesByStudyIds(studyIds);
			final Map<String, List<ExternalReferenceDTO>> externalReferencesMap =
				this.daoFactory.getStudyInstanceExternalReferenceDao().getExternalReferences(studyInstanceIds).stream()
					.collect(groupingBy(
						ExternalReferenceDTO::getEntityId));

			this.populateStudyEnvironmentVariablesMap(studyEnvironmentVariablesMap, studyInstanceDtos, studyEnvironmentDatasetIdMap);
			final List<Integer> variableIds = studyEnvironmentVariablesMap.values().stream().flatMap(variables -> variables.stream())
				.map(MeasurementVariable::getTermId).collect(Collectors.toList());
			final Map<Integer, List<MeasurementVariable>> environmentConditionsVariablesMap = this.daoFactory.getPhenotypeDAO()
				.getEnvironmentConditionVariablesByGeoLocationIdAndVariableIds(studyInstanceIds, variableIds);
			final Map<Integer, List<MeasurementVariable>> environmentDetailsVariablesMap = this.daoFactory.getGeolocationPropertyDao()
				.getEnvironmentDetailVariablesByGeoLocationIdAndVariableIds(studyInstanceIds, variableIds);
			final Map<Integer, Map<String, String>> additionalInfoMap = this.daoFactory.getGeolocationPropertyDao()
				.getGeolocationPropsAndValuesByGeolocation(studyInstanceIds, variableIds);
			for (final StudyInstanceDto studyInstanceDto : studyInstanceDtos) {
				final Integer trialDbId = Integer.valueOf(studyInstanceDto.getTrialDbId());
				final Integer studyDbId = Integer.valueOf(studyInstanceDto.getStudyDbId());
				final List<MeasurementVariable> environmentVariables = studyEnvironmentVariablesMap.get(trialDbId);

				final List<MeasurementVariable> environmentParameterVariables = new ArrayList<>();
				if (environmentConditionsVariablesMap.containsKey(studyDbId)) {
					environmentParameterVariables.addAll(environmentConditionsVariablesMap.get(studyDbId));
				}
				if (environmentDetailsVariablesMap.containsKey(studyDbId)) {
					environmentParameterVariables.addAll(environmentDetailsVariablesMap.get(studyDbId));
				}

				environmentParameterVariables.addAll(this.createGeolocationVariables(environmentVariables, studyDbId));

				final List<EnvironmentParameter> environmentParameters = environmentParameterVariables.stream()
					.map(EnvironmentParameter::new).collect(Collectors.toList());
				studyInstanceDto.setEnvironmentParameters(environmentParameters);

				if (additionalInfoMap.containsKey(studyDbId)) {
					studyInstanceDto.getAdditionalInfo().putAll(additionalInfoMap.get(studyDbId));
				}
				if (studyAdditionalInfoMap.containsKey(trialDbId)) {
					studyInstanceDto.getAdditionalInfo().putAll(studyAdditionalInfoMap.get(trialDbId));
				}

				studyInstanceDto.setExternalReferences(externalReferencesMap.get(studyInstanceDto.getStudyDbId()));
				studyInstanceDto.setObservationLevels(observationLevelsMap.get(trialDbId));
			}
		}
		return studyInstanceDtos;

	}

	@Override
	public long countStudyInstances(final StudySearchFilter studySearchFilter) {
		return this.daoFactory.getDmsProjectDAO().countStudyInstances(studySearchFilter);
	}

	@Override
	public List<StudyInstanceDto> saveStudyInstances(final String cropName, final List<StudyImportRequestDTO> studyImportRequestDTOS,
		final Integer userId) {
		final CropType cropType = this.daoFactory.getCropTypeDAO().getByName(cropName);
		final List<String> studyIds = new ArrayList<>();
		final List<Integer> trialIds = new ArrayList<>();
		final List<String> environmentVariableIds = new ArrayList<>();
		final List<Integer> locationDbIds = new ArrayList<>();

		studyImportRequestDTOS.stream().forEach(dto -> {
			final Integer trialId = Integer.valueOf(dto.getTrialDbId());
			if (!trialIds.contains(trialId)) {
				trialIds.add(trialId);
			}
			if (!CollectionUtils.isEmpty(dto.getEnvironmentParameters())) {
				environmentVariableIds
					.addAll(
						dto.getEnvironmentParameters().stream().map(EnvironmentParameter::getParameterPUI).collect(Collectors.toList()));
			}
			if (dto.getLocationDbId() != null) {
				locationDbIds.add(Integer.parseInt(dto.getLocationDbId()));
			}
		});

		final Map<Integer, DmsProject> trialIdEnvironmentDatasetMap =
			this.daoFactory.getDmsProjectDAO().getDatasetsByTypeForStudy(trialIds, DatasetTypeEnum.SUMMARY_DATA.getId()).stream()
				.collect(Collectors.toMap(environmentDataset -> environmentDataset.getStudy().getProjectId(), Function.identity()));

		final Map<Integer, List<Integer>> studyIdEnvironmentVariablesMap =
			this.daoFactory.getProjectPropertyDAO().getEnvironmentDatasetVariables(trialIds);

		final Map<Integer, MeasurementVariable> environmentVariablesMap = this.daoFactory.getCvTermDao()
			.getVariablesByIdsAndVariableTypes(environmentVariableIds,
				Arrays.asList(VariableType.ENVIRONMENT_CONDITION.getName(), VariableType.ENVIRONMENT_DETAIL.getName()));

		final List<Integer> categoricalVariableIds =
			environmentVariablesMap.values().stream().filter(measurementVariable -> DataType.CATEGORICAL_VARIABLE.getId().equals(measurementVariable.getDataTypeId()))
				.map(MeasurementVariable::getTermId).collect(Collectors.toList());

		//Include season variable to the categorical values
		categoricalVariableIds.add(TermId.SEASON_VAR.getId());

		final Map<Integer, List<ValueReference>> categoricalVariablesMap =
			this.daoFactory.getCvTermRelationshipDao().getCategoriesForCategoricalVariables(categoricalVariableIds);

		final Optional<Location> unspecifiedLocation = this.daoFactory.getLocationDAO().getUnspecifiedLocation();
		final Map<Integer, Location> locationsMap = this.getLocationsMap(locationDbIds);

		for (final StudyImportRequestDTO requestDTO : studyImportRequestDTOS) {
			final Integer trialId = Integer.valueOf(requestDTO.getTrialDbId());
			final Integer environmentDatasetId = trialIdEnvironmentDatasetMap.get(trialId).getProjectId();
			this.addEnvironmentVariablesIfNecessary(requestDTO, studyIdEnvironmentVariablesMap, environmentVariablesMap,
				trialIdEnvironmentDatasetMap);

			final Geolocation geolocation = this.resolveGeolocationForStudy(trialId);

			final ExperimentModel experimentModel =
				this.experimentModelGenerator
					.generate(cropType, environmentDatasetId, Optional.of(geolocation), ExperimentType.TRIAL_ENVIRONMENT);
			this.addEnvironmentVariableValues(requestDTO, environmentVariablesMap, categoricalVariablesMap, experimentModel,
				unspecifiedLocation, locationsMap);
			this.addSeasonVariableIfNecessary(requestDTO, studyIdEnvironmentVariablesMap, geolocation, categoricalVariablesMap,
				trialIdEnvironmentDatasetMap);
			this.addExperimentalDesignIfNecessary(requestDTO, trialIdEnvironmentDatasetMap, geolocation, studyIdEnvironmentVariablesMap);
			this.setInstanceExternalReferences(requestDTO, geolocation);
			this.daoFactory.getGeolocationDao().saveOrUpdate(geolocation);
			this.daoFactory.getExperimentDao().save(experimentModel);
			// Unless the session is flushed, the latest changes are not reflected in DTOs returned by method
			this.sessionProvider.getSession().flush();
			studyIds.add(geolocation.getLocationId().toString());
		}

		//Update environment dataset to save added project properties
		for (final Integer trialId : trialIds) {
			this.daoFactory.getDmsProjectDAO().update(trialIdEnvironmentDatasetMap.get(trialId));
			this.sessionProvider.getSession().flush();
		}

		final StudySearchFilter filter = new StudySearchFilter();
		filter.setStudyDbIds(studyIds);
		return this.getStudyInstancesWithMetadata(filter, null);
	}

	private Map<Integer, Location> getLocationsMap(final List<Integer> locationDbIds) {
		return locationDbIds.isEmpty() ?
			Collections.emptyMap() : this.daoFactory.getLocationDAO().getByIds(locationDbIds).stream()
			.collect(Collectors.toMap(Location::getLocid, Function.identity()));
	}

	private Geolocation resolveGeolocationForStudy(final Integer trialId) {
		// Retrieve existing study instances
		final List<Geolocation> geolocations = this.daoFactory.getGeolocationDao().getEnvironmentGeolocations(trialId);
		final Geolocation geolocation;
		if (geolocations.size() == 1 && this.daoFactory.getExperimentDao()
			.getExperimentByTypeInstanceId(ExperimentType.TRIAL_ENVIRONMENT.getTermId(), geolocations.get(0).getLocationId()) == null) {
			geolocation = geolocations.get(0);
		} else {
			// If design is generated, increment last instance number. Otherwise, attempt to find  "gap" instance number first (if any)
			final boolean hasExperimentalDesign = this.experimentDesignService.getStudyExperimentDesignTypeTermId(trialId).isPresent();
			final List<Integer> instanceNumbers =
				geolocations.stream().mapToInt(o -> Integer.parseInt(o.getDescription())).boxed().collect(Collectors.toList());
			geolocation = this.createNextGeolocation(instanceNumbers, hasExperimentalDesign);
		}
		return geolocation;
	}

	private Geolocation createNextGeolocation(final List<Integer> instanceNumbers, final boolean hasExperimentalDesign) {
		Integer instanceNumber = (!instanceNumbers.isEmpty() ? Collections.max(instanceNumbers) : 0) + 1;
		if (!hasExperimentalDesign) {
			instanceNumber = 1;
			while (instanceNumbers.contains(instanceNumber)) {
				instanceNumber++;
			}
		}

		final Geolocation geolocation = new Geolocation();
		geolocation.setDescription(String.valueOf(instanceNumber));
		return geolocation;
	}

	private void populateStudyEnvironmentVariablesMap(final Map<Integer, List<MeasurementVariable>> studyEnvironmentVariablesMap,
		final List<StudyInstanceDto> studyInstanceDtos, final Map<Integer, Integer> studyEnvironmentDatasetIdMap) {

		for (final StudyInstanceDto studyInstanceDto : studyInstanceDtos) {
			final Integer trialDbId = Integer.valueOf(studyInstanceDto.getTrialDbId());
			final Integer environmentDatasetId = studyEnvironmentDatasetIdMap.get(trialDbId);
			studyEnvironmentVariablesMap.computeIfAbsent(trialDbId, k ->
				this.daoFactory.getDmsProjectDAO()
					.getObservationSetVariables(
						environmentDatasetId,
						Lists.newArrayList(VariableType.ENVIRONMENT_CONDITION.getId(), VariableType.ENVIRONMENT_DETAIL.getId()))
			);
		}
	}

	private void addExperimentalDesignIfNecessary(final StudyImportRequestDTO requestDTO,
		final Map<Integer, DmsProject> trialIdEnvironmentDatasetMap, final Geolocation geolocation,
		final Map<Integer, List<Integer>> studyIdEnvironmentVariablesMap) {
		final Integer trialDbId = Integer.valueOf(requestDTO.getTrialDbId());

		if (!studyIdEnvironmentVariablesMap.get(trialDbId).contains(TermId.EXPERIMENT_DESIGN_FACTOR.getId())) {
			this.addProjectProperty(studyIdEnvironmentVariablesMap, trialIdEnvironmentDatasetMap, trialDbId,
				VariableType.ENVIRONMENT_DETAIL,
				TermId.EXPERIMENT_DESIGN_FACTOR.getId(), String.valueOf(TermId.EXTERNALLY_GENERATED.getId()), EXPT_DESIGN);
		}

		final List<ProjectProperty> experimentalDesignProperty = trialIdEnvironmentDatasetMap.get(trialDbId).getProperties().stream()
			.filter(projectProperty -> projectProperty.getVariableId() == TermId.EXPERIMENT_DESIGN_FACTOR.getId())
			.collect(Collectors.toList());

		final String externallyGeneratedDesignId = String.valueOf(TermId.EXTERNALLY_GENERATED.getId());
		if (experimentalDesignProperty.get(0).getValue().equals(externallyGeneratedDesignId)) {
			final GeolocationProperty experimentalDesignGeolocProperty = new GeolocationProperty(geolocation,
				externallyGeneratedDesignId, 1, TermId.EXPERIMENT_DESIGN_FACTOR.getId());
			geolocation.getProperties().add(experimentalDesignGeolocProperty);
		}

	}

	private void addSeasonVariableIfNecessary(final StudyImportRequestDTO requestDTO,
		final Map<Integer, List<Integer>> studyIdEnvironmentVariablesMap, final Geolocation geolocation,
		final Map<Integer, List<ValueReference>> categoricalVariablesMap,
		final Map<Integer, DmsProject> environmentDatasetMap) {
		if (!CollectionUtils.isEmpty(requestDTO.getSeasons())) {
			final Integer trialDbId = Integer.valueOf(requestDTO.getTrialDbId());
			final String seasonValue = requestDTO.getSeasons().get(0);

			final List<String> possibleValues =
				categoricalVariablesMap.get(TermId.SEASON_VAR.getId()).stream().map(ValueReference::getDescription)
					.collect(Collectors.toList());
			if (possibleValues.contains(seasonValue)) {
				//Add season variable if not present to the study
				if (!studyIdEnvironmentVariablesMap.get(trialDbId).contains(TermId.SEASON_VAR.getId())) {
					this.addProjectProperty(studyIdEnvironmentVariablesMap, environmentDatasetMap, trialDbId,
						VariableType.ENVIRONMENT_DETAIL, TermId.SEASON_VAR.getId(), null, CROP_SEASON_CODE);
				}

				//Add season value for the environment
				if (geolocation.getProperties() == null) {
					geolocation.setProperties(new ArrayList<>());
				}
				final Map<String, Integer> seasonValuesMap = categoricalVariablesMap.get(TermId.SEASON_VAR.getId()).stream()
					.collect(Collectors.toMap(ValueReference::getDescription, ValueReference::getId));
				final GeolocationProperty seasonProperty = new GeolocationProperty(geolocation,
					String.valueOf(seasonValuesMap.get(seasonValue)), 1, TermId.SEASON_VAR.getId());
				geolocation.getProperties().add(seasonProperty);
			}
		}
	}

	private void addEnvironmentVariableValues(final StudyImportRequestDTO requestDTO,
		final Map<Integer, MeasurementVariable> environmentVariablesMap, final Map<Integer, List<ValueReference>> categoricalValuesMap,
		final ExperimentModel experimentModel, final Optional<Location> unspecifiedLocation, final Map<Integer, Location> locationsMap) {

		// The default value of an instance's location name is "Unspecified Location"
		final Optional<Location> location =
			StringUtils.isEmpty(requestDTO.getLocationDbId()) ? unspecifiedLocation :
				Optional.of(locationsMap.get(Integer.parseInt(requestDTO.getLocationDbId())));

		final List<GeolocationProperty> properties = new ArrayList<>();
		final List<Phenotype> phenotypes = new ArrayList<>();

		// Add location property
		final GeolocationProperty locationGeolocationProperty =
			new GeolocationProperty(experimentModel.getGeoLocation(), String.valueOf(location.get().getLocid()), 1,
				TermId.LOCATION_ID.getId());
		properties.add(locationGeolocationProperty);

		if (!CollectionUtils.isEmpty(requestDTO.getEnvironmentParameters())) {
			// Use name of categorical value in validating inputs
			final CategoricalValueNameValidator categoricalValueNameValidator = new CategoricalValueNameValidator();
			for (final EnvironmentParameter environmentParameter : requestDTO.getEnvironmentParameters()) {
				if (StringUtils.isNotEmpty(environmentParameter.getValue())) {
					final MeasurementVariable measurementVariable =
						environmentVariablesMap.get(Integer.valueOf(environmentParameter.getParameterPUI()));
					if (measurementVariable != null) {
						measurementVariable.setValue(environmentParameter.getValue());
						final DataType dataType = DataType.getById(measurementVariable.getDataTypeId());
						final java.util.Optional<VariableValueValidator> dataValidator = DataType.CATEGORICAL_VARIABLE.equals(dataType) ? Optional.of(categoricalValueNameValidator) :
							this.variableDataValidatorFactory.getValidator(dataType);
						if (categoricalValuesMap.containsKey(measurementVariable.getTermId())) {
							measurementVariable.setPossibleValues(categoricalValuesMap.get(measurementVariable.getTermId()));
						}
						if (!dataValidator.isPresent() || dataValidator.get().isValid(measurementVariable)) {
							if (VariableType.ENVIRONMENT_DETAIL.getId().equals(measurementVariable.getVariableType().getId())) {
								if (StudyInstanceServiceImpl.GEOLOCATION_METADATA.contains(measurementVariable.getTermId())) {
									this.mapGeolocationMetaData(experimentModel.getGeoLocation(), environmentParameter);
								} else {
									final GeolocationProperty property = new GeolocationProperty(experimentModel.getGeoLocation(),
										this.getEnvironmentParameterValue(environmentParameter, categoricalValuesMap), 1,
										measurementVariable.getTermId());
									properties.add(property);
								}
							} else if (VariableType.ENVIRONMENT_CONDITION.getId().equals(measurementVariable.getVariableType().getId())) {
								final Phenotype phenotype = new Phenotype(measurementVariable.getTermId(),
									this.getEnvironmentParameterValue(environmentParameter, categoricalValuesMap), experimentModel);
								phenotype.setCreatedDate(new Date());
								phenotype.setUpdatedDate(new Date());
								phenotype.setName(String.valueOf(measurementVariable.getTermId()));
								phenotypes.add(phenotype);
							}
						}
					}

				}
			}
		}

		experimentModel.getGeoLocation().setProperties(properties);
		experimentModel.setPhenotypes(phenotypes);
	}

	private String getEnvironmentParameterValue(final EnvironmentParameter environmentParameter,
		final Map<Integer, List<ValueReference>> categoricalValuesMap) {
		final Integer variableId = Integer.valueOf(environmentParameter.getParameterPUI());

		if (categoricalValuesMap.containsKey(variableId)) {
			final Map<String, Integer> possibleValuesMap = categoricalValuesMap.get(variableId).stream()
				.collect(Collectors.toMap(ValueReference::getName, ValueReference::getId));
			//The reference ID should be saved for categorical environment variables
			return String.valueOf(possibleValuesMap.get(environmentParameter.getValue()));
		} else {
			return environmentParameter.getValue();
		}
	}

	private void mapGeolocationMetaData(final Geolocation geolocation, final EnvironmentParameter environmentParameter) {
		final Integer variableId = Integer.valueOf(environmentParameter.getParameterPUI());
		if (TermId.LATITUDE.getId() == variableId) {
			geolocation.setLatitude(Double.valueOf(environmentParameter.getValue()));
		} else if (TermId.LONGITUDE.getId() == variableId) {
			geolocation.setLongitude(Double.valueOf(environmentParameter.getValue()));
		} else if (TermId.GEODETIC_DATUM.getId() == variableId) {
			geolocation.setGeodeticDatum(environmentParameter.getValue());
		} else if (TermId.ALTITUDE.getId() == variableId) {
			geolocation.setAltitude(Double.valueOf(environmentParameter.getValue()));
		}
	}

	private void addEnvironmentVariablesIfNecessary(final StudyImportRequestDTO requestDTO,
		final Map<Integer, List<Integer>> studyIdEnvironmentVariablesMap, final Map<Integer, MeasurementVariable> environmentVariablesMap,
		final Map<Integer, DmsProject> trialIdEnvironmentDatasetMap) {
		if (!CollectionUtils.isEmpty(requestDTO.getEnvironmentParameters())) {
			final Integer trialDbId = Integer.valueOf(requestDTO.getTrialDbId());
			for (final EnvironmentParameter environmentParameter : requestDTO.getEnvironmentParameters()) {
				final Integer variableId = Integer.valueOf(environmentParameter.getParameterPUI());
				if (!studyIdEnvironmentVariablesMap.get(trialDbId).contains(variableId) && environmentVariablesMap
					.containsKey(variableId)) {
					final VariableType variableType = environmentVariablesMap.get(variableId).getVariableType();
					this.addProjectProperty(studyIdEnvironmentVariablesMap, trialIdEnvironmentDatasetMap, trialDbId, variableType,
						variableId, null, environmentVariablesMap.get(variableId).getName());
				}
			}
		}
	}

	private List<MeasurementVariable> createGeolocationVariables(final List<MeasurementVariable> measurementVariables,
		final Integer geolocationId) {
		final List<MeasurementVariable> geolocationVariables = new ArrayList<>();
		final List<Integer> variableIds = measurementVariables.stream().map(MeasurementVariable::getTermId)
			.collect(Collectors.toList());
		if (variableIds.contains(TermId.ALTITUDE.getId()) || variableIds.contains(TermId.LATITUDE.getId())
			|| variableIds.contains(TermId.LONGITUDE.getId()) || variableIds.contains(TermId.GEODETIC_DATUM.getId())) {
			final Geolocation geolocation = this.daoFactory.getGeolocationDao().getById(geolocationId);
			final Map<Integer, MeasurementVariable> variableMap = new HashMap<>();
			for (final MeasurementVariable mvar : measurementVariables) {
				variableMap.put(mvar.getTermId(), mvar);
			}
			if (variableIds.contains(TermId.ALTITUDE.getId())) {
				final String value = geolocation.getAltitude() == null ? "" : geolocation.getAltitude().toString();
				variableMap.get(TermId.ALTITUDE.getId()).setValue(value);
				geolocationVariables.add(variableMap.get(TermId.ALTITUDE.getId()));
			}
			if (variableIds.contains(TermId.LATITUDE.getId())) {
				final String value = geolocation.getLatitude() == null ? "" : geolocation.getLatitude().toString();
				variableMap.get(TermId.LATITUDE.getId()).setValue(value);
				geolocationVariables.add(variableMap.get(TermId.LATITUDE.getId()));
			}
			if (variableIds.contains(TermId.LONGITUDE.getId())) {
				final String value = geolocation.getLongitude() == null ? "" : geolocation.getLongitude().toString();
				variableMap.get(TermId.LONGITUDE.getId()).setValue(value);
				geolocationVariables.add(variableMap.get(TermId.LONGITUDE.getId()));
			}
			if (variableIds.contains(TermId.GEODETIC_DATUM.getId())) {
				final String value = geolocation.getGeodeticDatum() == null ? "" : geolocation.getGeodeticDatum();
				variableMap.get(TermId.GEODETIC_DATUM.getId()).setValue(value);
				geolocationVariables.add(variableMap.get(TermId.GEODETIC_DATUM.getId()));
			}

		}
		return geolocationVariables;
	}

	private void addProjectProperty(final Map<Integer, List<Integer>> studyIdEnvironmentVariablesMap,
		final Map<Integer, DmsProject> environmentDatasetMap, final Integer trialDbId, final VariableType variableType,
		final Integer termId, final String value, final String alias) {
		final ProjectProperty property = new ProjectProperty();
		property.setVariableId(termId);
		property.setTypeId(variableType.getId());
		property.setValue(value);
		property.setRank(environmentDatasetMap.get(trialDbId).getProperties().size());
		property.setProject(environmentDatasetMap.get(trialDbId));
		property.setAlias(alias);
		environmentDatasetMap.get(trialDbId).addProperty(property);
		studyIdEnvironmentVariablesMap.get(trialDbId).add(termId);
	}

	private void setInstanceExternalReferences(final StudyImportRequestDTO studyImportRequestDTO, final Geolocation geolocation) {
		if (studyImportRequestDTO.getExternalReferences() != null) {
			final List<InstanceExternalReference> references = new ArrayList<>();
			studyImportRequestDTO.getExternalReferences().forEach(reference -> {
				final InstanceExternalReference externalReference =
					new InstanceExternalReference(geolocation, reference.getReferenceID(), reference.getReferenceSource());
				references.add(externalReference);
			});
			geolocation.setExternalReferences(references);
		}
	}

	private List<UserDto> getUsersForEnvironment(final Integer instanceId) {
		final List<Integer> personIds = this.daoFactory.getDmsProjectDAO().getPersonIdsAssociatedToEnvironment(instanceId);
		if (!CollectionUtils.isEmpty(personIds)) {
			return this.userService.getUsersByPersonIds(personIds);
		}
		return Collections.emptyList();
	}

}
