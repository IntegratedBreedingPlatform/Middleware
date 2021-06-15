package org.generationcp.middleware.service.impl.study;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.api.brapi.v2.germplasm.ExternalReferenceDTO;
import org.generationcp.middleware.api.brapi.v2.study.StudyImportRequestDTO;
import org.generationcp.middleware.dao.dms.ExperimentDao;
import org.generationcp.middleware.dao.dms.GeolocationDao;
import org.generationcp.middleware.dao.dms.GeolocationPropertyDao;
import org.generationcp.middleware.dao.dms.PhenotypeDao;
import org.generationcp.middleware.domain.dms.ExperimentType;
import org.generationcp.middleware.domain.dms.InstanceDescriptorData;
import org.generationcp.middleware.domain.dms.InstanceObservationData;
import org.generationcp.middleware.domain.dms.InstanceVariableData;
import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
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
import org.generationcp.middleware.service.Service;
import org.generationcp.middleware.service.api.dataset.DatasetService;
import org.generationcp.middleware.service.api.ontology.VariableDataValidatorFactory;
import org.generationcp.middleware.service.api.ontology.VariableValueValidator;
import org.generationcp.middleware.service.api.study.EnvironmentParameter;
import org.generationcp.middleware.service.api.study.ObservationLevel;
import org.generationcp.middleware.service.api.study.StudyDetailsDto;
import org.generationcp.middleware.service.api.study.StudyInstanceDto;
import org.generationcp.middleware.service.api.study.StudyInstanceService;
import org.generationcp.middleware.service.api.study.StudyMetadata;
import org.generationcp.middleware.service.api.study.StudySearchFilter;
import org.generationcp.middleware.service.api.study.StudyService;
import org.generationcp.middleware.service.api.study.generation.ExperimentDesignService;
import org.generationcp.middleware.service.api.user.UserDto;
import org.generationcp.middleware.service.api.user.UserService;
import org.generationcp.middleware.service.impl.study.generation.ExperimentModelGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
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
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Transactional
public class StudyInstanceServiceImpl extends Service implements StudyInstanceService {

	private static final Logger LOG = LoggerFactory.getLogger(StudyInstanceServiceImpl.class);

	protected static final List<Integer> GEOLOCATION_METADATA =
		Arrays.asList(TermId.LATITUDE.getId(), TermId.LONGITUDE.getId(), TermId.GEODETIC_DATUM.getId(), TermId.ALTITUDE.getId());

	@Resource
	private StudyService studyService;

	@Resource
	private DatasetService datasetService;

	@Resource
	private ExperimentDesignService experimentDesignService;

	@Resource
	private ExperimentModelGenerator experimentModelGenerator;

	@Resource
	private VariableDataValidatorFactory variableDataValidatorFactory;

	@Resource
	private UserService userService;

	private DaoFactory daoFactory;

	public StudyInstanceServiceImpl() {
		// no-arg constuctor is required by CGLIB proxying used by Spring 3x and older.
	}

	public StudyInstanceServiceImpl(final HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public List<StudyInstance> createStudyInstances(final CropType crop, final int studyId, final int datasetId,
		final Integer numberOfInstancesToGenerate) {
		Preconditions.checkArgument(numberOfInstancesToGenerate > 0);

		// Retrieve existing study instances
		final List<Geolocation> geolocations = this.daoFactory.getGeolocationDao().getEnvironmentGeolocations(studyId);
		final List<Integer> instanceNumbers =
			geolocations.stream().mapToInt(o -> Integer.valueOf(o.getDescription())).boxed().collect(Collectors.toList());

		final List<StudyInstance> studyInstances = new ArrayList<>();
		final boolean hasExperimentalDesign = this.experimentDesignService.getStudyExperimentDesignTypeTermId(studyId).isPresent();
		int instancesGenerated = 0;
		while (instancesGenerated < numberOfInstancesToGenerate) {
			// If design is generated, increment last instance number. Otherwise, attempt to find  "gap" instance number first (if any)
			Integer instanceNumber = (!instanceNumbers.isEmpty() ? Collections.max(instanceNumbers) : 0) + 1;
			if (!hasExperimentalDesign) {
				instanceNumber = 1;
				while (instanceNumbers.contains(instanceNumber)) {
					instanceNumber++;
				}
			}

			// The default value of an instance's location name is "Unspecified Location"
			final Optional<Location> location = this.daoFactory.getLocationDAO().getUnspecifiedLocation();

			final Geolocation geolocation = new Geolocation();
			geolocation.setDescription(String.valueOf(instanceNumber));
			final GeolocationProperty locationGeolocationProperty =
				new GeolocationProperty(geolocation, String.valueOf(location.get().getLocid()), 1, TermId.LOCATION_ID.getId());
			geolocation.setProperties(Arrays.asList(locationGeolocationProperty));
			this.daoFactory.getGeolocationDao().save(geolocation);

			final ExperimentModel experimentModel =
				this.experimentModelGenerator.generate(crop, datasetId, Optional.of(geolocation), ExperimentType.TRIAL_ENVIRONMENT);

			this.daoFactory.getExperimentDao().save(experimentModel);

			final StudyInstance studyInstance =
				new StudyInstance(geolocation.getLocationId(), instanceNumber, false, false, false, true);
			if (location.isPresent()) {
				studyInstance.setLocationId(location.get().getLocid());
				studyInstance.setLocationName(location.get().getLname());
				studyInstance.setLocationAbbreviation(location.get().getLabbr());
				studyInstance.setInstanceId(geolocation.getLocationId());
				studyInstance.setLocationDescriptorDataId(locationGeolocationProperty.getGeolocationPropertyId());
				studyInstance.setExperimentId(experimentModel.getNdExperimentId());
			}

			instanceNumbers.add(instanceNumber);
			studyInstances.add(studyInstance);
			instancesGenerated++;
		}

		return studyInstances;
	}

	@Override
	public List<StudyInstance> getStudyInstances(final int studyId) {
		return this.getStudyInstances(studyId, Collections.emptyList());
	}

	private List<StudyInstance> getStudyInstances(final int studyId, final List<Integer> instanceIds) {

		final int environmentDatasetId =
			this.daoFactory.getDmsProjectDAO().getDatasetsByTypeForStudy(studyId, DatasetTypeEnum.SUMMARY_DATA.getId()).get(0)
				.getProjectId();
		final List<StudyInstance> instances = this.daoFactory.getDmsProjectDAO().getDatasetInstances(environmentDatasetId, instanceIds);
		// If study has advance or crosses generated and instance has experiment design, mark instance as cannot be deleted
		final boolean hasCrossesOrSelections = this.studyService.hasCrossesOrSelections(studyId);
		// If study has means dataset
		final boolean hasMeansDataset = this.studyService.studyHasGivenDatasetType(studyId, DatasetTypeEnum.MEANS_DATA.getId());
		if (hasCrossesOrSelections || hasMeansDataset) {
			for (final StudyInstance instance : instances) {
				final Optional<Integer> instanceHasGivenDatasetType = this.getDatasetIdForInstanceIdAndDatasetType(instance.getInstanceId(), DatasetTypeEnum.MEANS_DATA);
				if ((hasCrossesOrSelections && instance.isHasExperimentalDesign()) || (hasMeansDataset && instanceHasGivenDatasetType.isPresent())) {
					instance.setCanBeDeleted(false);
				}
			}
		}
		return instances;
	}

	@Override
	public void deleteStudyInstances(final Integer studyId, final List<Integer> instanceIds) {
		final Integer environmentDatasetId = this.studyService.getEnvironmentDatasetId(studyId);
		final List<Geolocation> allEnvironments = this.daoFactory.getGeolocationDao().getEnvironmentGeolocations(studyId);
		final List<Geolocation> environmentsToDelete = allEnvironments.stream()
			.filter(instance -> instanceIds.contains(instance.getLocationId())).collect(
				Collectors.toList());
		final List<Integer> instanceNumbersToDelete =
			environmentsToDelete.stream().mapToInt(o -> Integer.valueOf(o.getDescription())).boxed()
				.collect(Collectors.toList());

		//Update StudyExperimentGeolocation
		this.daoFactory.getExperimentDao().updateStudyExperimentGeolocationIfNecessary(studyId, instanceIds);

		// Delete plot and environment experiments
		final Integer plotDatasetId = this.studyService.getPlotDatasetId(studyId);
		final ExperimentDao experimentDao = this.daoFactory.getExperimentDao();
		experimentDao.deleteExperimentsForDatasets(Arrays.asList(plotDatasetId, environmentDatasetId), instanceNumbersToDelete);

		// Delete geolocation and its properties
		this.daoFactory.getGeolocationDao().deleteGeolocations(instanceIds);

		this.deleteExperimentalDesignIfApplicable(studyId,plotDatasetId);

		// IF experimental design is not yet generated, re-number succeeding trial instances
		final boolean hasExperimentalDesign = this.experimentDesignService.getStudyExperimentDesignTypeTermId(studyId).isPresent();
		if (!hasExperimentalDesign) {
			final Integer startingInstanceNumber = instanceNumbersToDelete.isEmpty() ? 1 : Collections.min(instanceNumbersToDelete);
			final List<Geolocation> instancesToUpdate =
				allEnvironments.stream()
					.filter(instance -> !instanceNumbersToDelete.contains(Integer.valueOf(instance.getDescription()))
						&& Integer.valueOf(instance.getDescription()) > startingInstanceNumber).collect(
					Collectors.toList());
			// Unfortunately, not possible in MySQL 5 to do batch update as row_number function is only available in MySQL 8
			// Also tried using MySQL variable assignment like @instance_number:=@instance_number + 1 but it causes Hibernate error
			// as it's being treated as named parameter. Hopefully can be optimized when we upgrade Hibernate and/or MySQL version
			Integer instanceNumber = startingInstanceNumber;
			for (final Geolocation instance : instancesToUpdate) {
				instance.setDescription(String.valueOf(instanceNumber++));
				this.daoFactory.getGeolocationDao().saveOrUpdate(instance);
			}
		}
	}

	@Override
	public Optional<StudyInstance> getStudyInstance(final int studyId, final Integer instanceId) {
		final List<StudyInstance> studyInstances = this.getStudyInstances(studyId, Collections.singletonList(instanceId));
		if (!CollectionUtils.isEmpty(studyInstances)) {
			return Optional.of(studyInstances.get(0));
		}
		return Optional.empty();
	}

	@Override
	public InstanceObservationData addInstanceObservation(final InstanceObservationData instanceObservationData) {
		Preconditions.checkNotNull(instanceObservationData.getInstanceId());
		final Integer variableId = instanceObservationData.getVariableId();
		Preconditions.checkNotNull(variableId);
		Preconditions.checkNotNull(instanceObservationData.getValue());

		final ExperimentModel experimentModel =
			this.daoFactory.getExperimentDao()
				.getExperimentByTypeInstanceId(ExperimentType.TRIAL_ENVIRONMENT.getTermId(), instanceObservationData.getInstanceId());
		final Phenotype phenotype =
			new Phenotype(variableId, instanceObservationData.getValue(), experimentModel);
		phenotype.setCreatedDate(new Date());
		phenotype.setUpdatedDate(new Date());
		phenotype.setcValue(instanceObservationData.getCategoricalValueId());
		phenotype.setName(String.valueOf(variableId));

		this.daoFactory.getPhenotypeDAO().save(phenotype);
		instanceObservationData.setInstanceObservationId(phenotype.getPhenotypeId());

		return instanceObservationData;
	}

	private String getEnvironmentDataValue(final InstanceVariableData observationData) {
		return (observationData.getCategoricalValueId() != null && observationData.getCategoricalValueId() > 0) ?
			String.valueOf(observationData.getCategoricalValueId()) :
			observationData.getValue();
	}

	@Override
	public InstanceObservationData updateInstanceObservation(final InstanceObservationData instanceObservationData) {
		Preconditions.checkNotNull(instanceObservationData.getInstanceObservationId());
		Preconditions.checkNotNull(instanceObservationData.getInstanceId());
		Preconditions.checkNotNull(instanceObservationData.getVariableId());
		Preconditions.checkNotNull(instanceObservationData.getValue());

		final PhenotypeDao phenotypeDAO = this.daoFactory.getPhenotypeDAO();
		final Phenotype phenotype = phenotypeDAO.getById(instanceObservationData.getInstanceObservationId());
		Preconditions.checkNotNull(phenotype);
		phenotype.setValue(instanceObservationData.getValue());
		phenotype.setcValue(instanceObservationData.getCategoricalValueId());
		phenotype.setUpdatedDate(new Date());
		phenotypeDAO.update(phenotype);

		// Change the status to OUT_OF_SYNC of calculated traits that depend on the changed/updated variable.
		this.datasetService
			.updateDependentPhenotypesStatusByGeolocation(instanceObservationData.getInstanceId(), Arrays.asList(instanceObservationData.getVariableId()));

		return instanceObservationData;
	}

	@Override
	public Optional<InstanceObservationData> getInstanceObservation(final Integer instanceId, final Integer observationDataId,
		final Integer variableId) {

		final ExperimentModel experimentModel =
			this.daoFactory.getExperimentDao().getExperimentByTypeInstanceId(ExperimentType.TRIAL_ENVIRONMENT.getTermId(), instanceId);
		final Phenotype phenotype = this.daoFactory.getPhenotypeDAO().getPhenotype(experimentModel.getNdExperimentId(), observationDataId);
		if (phenotype != null) {
			return Optional
				.of(new InstanceObservationData(phenotype.getExperiment().getNdExperimentId(), observationDataId, phenotype.getObservableId(),
					phenotype.getValue(), phenotype.getcValueId()));
		}
		return Optional.empty();
	}

	@Override
	public InstanceDescriptorData addInstanceDescriptorData(final InstanceDescriptorData instanceDescriptorData) {

		Preconditions.checkNotNull(instanceDescriptorData.getInstanceId());
		final Integer variableId = instanceDescriptorData.getVariableId();
		Preconditions.checkNotNull(variableId);
		Preconditions.checkNotNull(instanceDescriptorData.getValue());

		final GeolocationDao geolocationDao = this.daoFactory.getGeolocationDao();
		final Geolocation geolocation = geolocationDao.getById(instanceDescriptorData.getInstanceId());
		final String value = this.getEnvironmentDataValue(instanceDescriptorData);

		if (GEOLOCATION_METADATA.contains(instanceDescriptorData.getVariableId())) {
			// Geolocation Metadata variables are stored in Geolocation table.
			// we just need to update their values if they are added to the study. No need to create GeolocationProperty.
			this.mapGeolocationMetaData(geolocation, instanceDescriptorData.getVariableId(), value);
			geolocationDao.save(geolocation);
			// Change the status to OUT_OF_SYNC of calculated traits that depend on the changed/updated variable.
			this.datasetService
				.updateDependentPhenotypesStatusByGeolocation(instanceDescriptorData.getInstanceId(),
					Arrays.asList(instanceDescriptorData.getVariableId()));
		} else {
			final GeolocationProperty property = new GeolocationProperty(geolocation, value, 1, instanceDescriptorData.getVariableId());
			this.daoFactory.getGeolocationPropertyDao().save(property);
			instanceDescriptorData.setInstanceDescriptorDataId(property.getGeolocationPropertyId());
		}

		return instanceDescriptorData;
	}

	@Override
	public StudyDetailsDto getStudyDetailsByInstance(final Integer instanceId) {
		try {
			final StudyMetadata studyMetadata = this.daoFactory.getDmsProjectDAO().getStudyMetadataForInstanceId(instanceId);
			if (studyMetadata != null) {
				final StudyDetailsDto studyDetailsDto = new StudyDetailsDto();
				studyDetailsDto.setMetadata(studyMetadata);

				final List<UserDto> users = new ArrayList<>();
				users.addAll(this.getUsersForEnvironment(studyMetadata.getStudyDbId()));
				users.addAll(this.getUsersAssociatedToStudy(studyMetadata.getNurseryOrTrialId()));
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
					environmentParameters.addAll(this.daoFactory.getPhenotypeDAO()
						.getEnvironmentConditionVariablesByGeoLocationIdAndVariableIds(
							Collections.singletonList(instanceId), variableIds).get(instanceId));
				}
				final List<MeasurementVariable> environmentDetails = this.daoFactory.getDmsProjectDAO()
					.getObservationSetVariables(environmentDataset.getProjectId(),
						Lists.newArrayList(VariableType.ENVIRONMENT_DETAIL.getId()));
				variableIds = environmentDetails.stream().map(MeasurementVariable::getTermId)
					.collect(Collectors.toList());
				if (!variableIds.isEmpty()) {
					environmentParameters.addAll(this.daoFactory.getGeolocationPropertyDao()
						.getEnvironmentDetailVariablesByGeoLocationIdAndVariableIds(
							Collections.singletonList(instanceId), variableIds).get(instanceId));
				}

				final List<MeasurementVariable> environmentVariables = new ArrayList<>(environmentConditions);
				environmentVariables.addAll(environmentDetails);
				environmentParameters.addAll(this.createGeolocationVariables(environmentVariables, instanceId));
				studyDetailsDto.setEnvironmentParameters(environmentParameters);

				final Map<String, String> properties = new HashMap<>();
				variableIds = environmentVariables.stream().map(MeasurementVariable::getTermId)
					.collect(Collectors.toList());
				properties.put("studyObjective", studyMetadata.getStudyObjective() == null ? "" : studyMetadata.getStudyObjective());
				properties.putAll(this.daoFactory.getGeolocationPropertyDao()
					.getGeolocationPropsAndValuesByGeolocation(Collections.singletonList(instanceId), variableIds).get(instanceId));
				final Map<Integer, Map<String, String>> projectPropMap =
					this.daoFactory.getProjectPropertyDAO().getProjectPropsAndValuesByStudyIds(
						Collections.singletonList(studyMetadata.getNurseryOrTrialId()));
				if (projectPropMap.containsKey(studyMetadata.getNurseryOrTrialId())) {
					properties.putAll(projectPropMap.get(studyMetadata.getNurseryOrTrialId()));
				}
				studyDetailsDto.setAdditionalInfo(properties);
				return studyDetailsDto;
			}
			return null;
		} catch (final MiddlewareQueryException e) {
			final String message = "Error with getStudyDetailsForGeolocation() query with instanceId: " + instanceId;
			StudyInstanceServiceImpl.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	@Override
	public InstanceDescriptorData updateInstanceDescriptorData(final InstanceDescriptorData instanceDescriptorData) {

		Preconditions.checkNotNull(instanceDescriptorData.getInstanceDescriptorDataId());
		Preconditions.checkNotNull(instanceDescriptorData.getInstanceId());
		Preconditions.checkNotNull(instanceDescriptorData.getVariableId());
		Preconditions.checkNotNull(instanceDescriptorData.getValue());

		final GeolocationPropertyDao propertyDao = this.daoFactory.getGeolocationPropertyDao();
		final GeolocationProperty property = propertyDao.getById(instanceDescriptorData.getInstanceDescriptorDataId());
		Preconditions.checkNotNull(property);
		property.setValue(this.getEnvironmentDataValue(instanceDescriptorData));
		propertyDao.update(property);

		// Change the status to OUT_OF_SYNC of calculated traits that depend on the changed/updated variable.
		this.datasetService
			.updateDependentPhenotypesStatusByGeolocation(instanceDescriptorData.getInstanceId(), Arrays.asList(instanceDescriptorData.getVariableId()));

		return instanceDescriptorData;
	}

	@Override
	public Optional<InstanceDescriptorData> getInstanceDescriptorData(final Integer instanceId, final Integer descriptorDataId, final Integer variableId) {

		final Geolocation geolocation = this.daoFactory.getGeolocationDao().getById(instanceId);

		if (GEOLOCATION_METADATA.contains(variableId)) {
			return Optional.of(new InstanceDescriptorData(geolocation.getLocationId(), geolocation.getLocationId(), variableId,
				this.getGeolocationMetaDataValue(geolocation, variableId), null));
		} else {
			final GeolocationProperty property =
				this.daoFactory.getGeolocationPropertyDao().getById(descriptorDataId);
			if (property != null) {
				return Optional.of(new InstanceDescriptorData(geolocation.getLocationId(), descriptorDataId, property.getTypeId(),
					property.getValue(), null));
			}
		}

		return Optional.empty();
	}

	@Override
	public Optional<Integer> getDatasetIdForInstanceIdAndDatasetType(final Integer instanceId, final DatasetTypeEnum datasetTypeEnum) {
		return
			Optional.ofNullable(this.daoFactory.getDmsProjectDAO().getDatasetIdByEnvironmentIdAndDatasetType(instanceId, datasetTypeEnum));
	}

	@Override
	public List<StudyInstanceDto> getStudyInstances(final StudySearchFilter studySearchFilter, final Pageable pageable) {
		return this.daoFactory.getDmsProjectDAO().getStudyInstances(studySearchFilter, pageable);
	}

	@Override
	public List<StudyInstanceDto> getStudyInstancesWithMetadata(final StudySearchFilter studySearchFilter, final Pageable pageable) {
		try {
			final List<StudyInstanceDto> studyInstanceDtos = this.daoFactory.getDmsProjectDAO()
				.getStudyInstances(studySearchFilter, pageable);
			if (!CollectionUtils.isEmpty(studyInstanceDtos)) {
				final List<Integer> studyIds = new ArrayList<>(studyInstanceDtos.stream().map(o -> Integer.valueOf(o.getTrialDbId()))
					.collect(Collectors.toSet()));
				final List<Integer> studyInstanceIds = new ArrayList<>(studyInstanceDtos.stream().map(o -> Integer.valueOf(o.getStudyDbId()))
					.collect(Collectors.toSet()));
				final Map<Integer, List<ObservationLevel>> observationLevelsMap = this.daoFactory.getDmsProjectDAO()
					.getObservationLevelsMap(studyIds);
				final Map<Integer, Integer> studyEnvironmentDatasetIdMap = this.daoFactory.getDmsProjectDAO()
					.getStudyIdEnvironmentDatasetIdMap(studyIds);

				final Map<Integer, List<MeasurementVariable>> studyEnvironmentVariablesMap = new HashMap<>();
				final Map<Integer, Map<String, String>> studyAdditionalInfoMap = this.daoFactory.getProjectPropertyDAO()
					.getProjectPropsAndValuesByStudyIds(studyIds);
				final Map<String, List<ExternalReferenceDTO>> externalReferencesMap =
					this.daoFactory.getStudyInstanceExternalReferenceDao().getExternalReferences(studyInstanceIds).stream().collect(groupingBy(
						ExternalReferenceDTO::getEntityId));

				this.populateStudyEnvironmentVariablesMap(studyEnvironmentVariablesMap, studyInstanceDtos, studyEnvironmentDatasetIdMap);
				final List<Integer> variableIds = studyEnvironmentVariablesMap.values().stream().flatMap(variables -> variables.stream())
					.map(variable -> variable.getTermId()).collect(Collectors.toList());
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
					if(environmentConditionsVariablesMap.get(studyDbId) != null) {
						environmentParameterVariables.addAll(environmentConditionsVariablesMap.get(studyDbId));
					}
					if(environmentDetailsVariablesMap.get(studyDbId) != null){
						environmentParameterVariables.addAll(environmentDetailsVariablesMap.get(studyDbId));
					}

					environmentParameterVariables.addAll(this.createGeolocationVariables(environmentVariables, studyDbId));

					final List<EnvironmentParameter> environmentParameters = environmentParameterVariables.stream()
						.map(variable -> new EnvironmentParameter(variable)).collect(Collectors.toList());
					studyInstanceDto.setEnvironmentParameters(environmentParameters);

					if(additionalInfoMap.get(studyDbId) != null) {
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
		} catch (final MiddlewareQueryException e) {
			final String message = "Error with getStudyInstances()";
			StudyInstanceServiceImpl.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
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
		final Map<Integer, DmsProject> trialIdEnvironmentDatasetMap = new HashMap<>();
		studyImportRequestDTOS.stream().forEach(dto -> {
			final Integer trialId = Integer.valueOf(dto.getTrialDbId());
			if (!trialIds.contains(trialId)) {
				trialIds.add(trialId);
				final DmsProject environmentDataset =
					this.daoFactory.getDmsProjectDAO().getDatasetsByTypeForStudy(trialId, DatasetTypeEnum.SUMMARY_DATA.getId()).get(0);
				trialIdEnvironmentDatasetMap.put(trialId, environmentDataset);
			}
			if(!CollectionUtils.isEmpty(dto.getEnvironmentParameters())) {
				environmentVariableIds
					.addAll(
						dto.getEnvironmentParameters().stream().map(EnvironmentParameter::getParameterPUI).collect(Collectors.toList()));
			}
		});

		final Map<Integer, List<Integer>> studyIdEnvironmentVariablesMap =
			this.daoFactory.getProjectPropertyDAO().getEnvironmentVariablesByStudyId(trialIds);

		final Map<Integer, MeasurementVariable> environmentVariablesMap = this.daoFactory.getCvTermDao()
			.getVariablesByIdsAndVariableTypes(environmentVariableIds,
				Arrays.asList(VariableType.ENVIRONMENT_CONDITION.getName(), VariableType.ENVIRONMENT_DETAIL.getName()));

		final List<Integer> categoricalVariableIds =
			environmentVariablesMap.values().stream().filter(var -> DataType.CATEGORICAL_VARIABLE.getId().equals(var.getDataTypeId()))
				.map(MeasurementVariable::getTermId).collect(Collectors.toList());

		//Include season variable to the categorical values
		categoricalVariableIds.add(TermId.SEASON_VAR.getId());

		final Map<Integer, List<ValueReference>> categoricalVariablesMap =
			this.daoFactory.getCvTermRelationshipDao().getCategoriesForCategoricalVariables(categoricalVariableIds);

		for (final StudyImportRequestDTO requestDTO : studyImportRequestDTOS) {
			final Integer trialId = Integer.valueOf(requestDTO.getTrialDbId());
			final Integer environmentDatasetId = trialIdEnvironmentDatasetMap.get(trialId).getProjectId();
			this.addEnvironmentVariablesIfNecessary(requestDTO, studyIdEnvironmentVariablesMap, environmentVariablesMap,
				trialIdEnvironmentDatasetMap);

			// Retrieve existing study instances
			final List<Geolocation> geolocations = this.daoFactory.getGeolocationDao().getEnvironmentGeolocations(trialId);
			final Geolocation geolocation;
			if (geolocations.size() == 1 && this.daoFactory.getExperimentDao()
				.getExperimentByTypeInstanceId(ExperimentType.TRIAL_ENVIRONMENT.getTermId(), geolocations.get(0).getLocationId()) == null) {
				geolocation = geolocations.get(0);
			} else {
				final List<Integer> instanceNumbers =
					geolocations.stream().mapToInt(o -> Integer.valueOf(o.getDescription())).boxed().collect(Collectors.toList());
				final boolean hasExperimentalDesign = this.experimentDesignService.getStudyExperimentDesignTypeTermId(trialId).isPresent();

				// If design is generated, increment last instance number. Otherwise, attempt to find  "gap" instance number first (if any)
				Integer instanceNumber = (!instanceNumbers.isEmpty() ? Collections.max(instanceNumbers) : 0) + 1;
				if (!hasExperimentalDesign) {
					instanceNumber = 1;
					while (instanceNumbers.contains(instanceNumber)) {
						instanceNumber++;
					}
				}

				geolocation = new Geolocation();
				geolocation.setDescription(String.valueOf(instanceNumber));
			}

			final ExperimentModel experimentModel =
				this.experimentModelGenerator
					.generate(cropType, environmentDatasetId, Optional.of(geolocation), ExperimentType.TRIAL_ENVIRONMENT);
			this.addEnvironmentVariableValues(requestDTO, environmentVariablesMap, categoricalVariablesMap, geolocation, experimentModel);
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
		for(final Integer trialId: trialIds) {
			this.daoFactory.getDmsProjectDAO().update(trialIdEnvironmentDatasetMap.get(trialId));
			this.sessionProvider.getSession().flush();
		}

		final StudySearchFilter filter = new StudySearchFilter();
		filter.setStudyDbIds(studyIds);
		return this.getStudyInstancesWithMetadata(filter, null);
	}

	private void populateStudyEnvironmentVariablesMap(final Map<Integer, List<MeasurementVariable>> studyEnvironmentVariablesMap,
		final List<StudyInstanceDto> studyInstanceDtos, final Map<Integer, Integer> studyEnvironmentDatasetIdMap) {

		for(final StudyInstanceDto studyInstanceDto: studyInstanceDtos) {
			final Integer trialDbId = Integer.valueOf(studyInstanceDto.getTrialDbId());
			final Integer environmentDatasetId = studyEnvironmentDatasetIdMap.get(trialDbId);
			if (studyEnvironmentVariablesMap.get(trialDbId) == null) {
				final List<MeasurementVariable> environmentVariables = this.daoFactory.getDmsProjectDAO()
					.getObservationSetVariables(
						environmentDatasetId,
						Lists.newArrayList(VariableType.ENVIRONMENT_CONDITION.getId(), VariableType.ENVIRONMENT_DETAIL.getId()));
				studyEnvironmentVariablesMap.put(trialDbId, environmentVariables);
			}
		}
	}

	private void addExperimentalDesignIfNecessary(final StudyImportRequestDTO requestDTO,
		final Map<Integer, DmsProject> trialIdEnvironmentDatasetMap, final Geolocation geolocation,
		final Map<Integer, List<Integer>> studyIdEnvironmentVariablesMap) {
		final Integer trialDbId = Integer.valueOf(requestDTO.getTrialDbId());

		if(!studyIdEnvironmentVariablesMap.get(trialDbId).contains(TermId.EXPERIMENT_DESIGN_FACTOR.getId())) {
			this.addProjectProperty(studyIdEnvironmentVariablesMap, trialIdEnvironmentDatasetMap, trialDbId, VariableType.ENVIRONMENT_DETAIL,
				TermId.EXPERIMENT_DESIGN_FACTOR.getId(), String.valueOf(TermId.EXTERNALLY_GENERATED.getId()));
		}

		final List<ProjectProperty> experimentalDesignProperty = trialIdEnvironmentDatasetMap.get(trialDbId).getProperties().stream()
			.filter(projectProperty -> projectProperty.getVariableId() == TermId.EXPERIMENT_DESIGN_FACTOR.getId()).collect(Collectors.toList());

		final String externallyGeneratedDesignId = String.valueOf(TermId.EXTERNALLY_GENERATED.getId());
		if(experimentalDesignProperty.get(0).getValue().equals(externallyGeneratedDesignId)) {
			final GeolocationProperty experimentalDesignGeolocProperty = new GeolocationProperty(geolocation,
				externallyGeneratedDesignId, 1, TermId.EXPERIMENT_DESIGN_FACTOR.getId());
			geolocation.getProperties().add(experimentalDesignGeolocProperty);
		}

	}
	private void addSeasonVariableIfNecessary(final StudyImportRequestDTO requestDTO,
		final Map<Integer, List<Integer>> studyIdEnvironmentVariablesMap, final Geolocation geolocation,
		final Map<Integer, List<ValueReference>> categoricalVariablesMap,
		final Map<Integer, DmsProject> environmentDatasetMap) {
		if(!CollectionUtils.isEmpty(requestDTO.getSeasons())) {
			final Integer trialDbId = Integer.valueOf(requestDTO.getTrialDbId());

			final MeasurementVariable seasonVariable = new MeasurementVariable();
			seasonVariable.setTermId(TermId.SEASON_VAR.getId());
			seasonVariable.setPossibleValues(categoricalVariablesMap.get(TermId.SEASON_VAR.getId()));
			seasonVariable.setDataTypeId(DataType.CATEGORICAL_VARIABLE.getId());
			seasonVariable.setValue(requestDTO.getSeasons().get(0));

			final DataType dataType = DataType.getById(seasonVariable.getDataTypeId());
			final java.util.Optional<VariableValueValidator> seasonValidator =
				this.variableDataValidatorFactory.getValidator(dataType);

			if(!seasonValidator.isPresent() || seasonValidator.get().isValid(seasonVariable, false)) {
				//Add season variable if not present to the study
				if (!studyIdEnvironmentVariablesMap.get(trialDbId).contains(seasonVariable.getTermId())) {
					this.addProjectProperty(studyIdEnvironmentVariablesMap, environmentDatasetMap, trialDbId,
						VariableType.ENVIRONMENT_DETAIL, seasonVariable.getTermId(), null);
				}

				//Add season value for the environment
				if(geolocation.getProperties() == null) {
					geolocation.setProperties(new ArrayList<>());
				}
				final Map<String, Integer> seasonValuesMap = categoricalVariablesMap.get(seasonVariable.getTermId()).stream()
					.collect(Collectors.toMap(ValueReference::getDescription, ValueReference::getId));
				LOG.error(categoricalVariablesMap.get(seasonVariable.getTermId()).get(0).toString());
				final GeolocationProperty seasonProperty = new GeolocationProperty(geolocation,
					String.valueOf(seasonValuesMap.get(seasonVariable.getValue())), 1, seasonVariable.getTermId());
				geolocation.getProperties().add(seasonProperty);
			}
		}
	}

	private void addProjectProperty(final Map<Integer, List<Integer>> studyIdEnvironmentVariablesMap,
		final Map<Integer, DmsProject> environmentDatasetMap, final Integer trialDbId, final VariableType variableType,
		final Integer termId, final String value) {
		final ProjectProperty property = new ProjectProperty();
		property.setVariableId(termId);
		property.setTypeId(variableType.getId());
		property.setValue(value);
		property.setRank(environmentDatasetMap.get(trialDbId).getProperties().size());
		property.setProject(environmentDatasetMap.get(trialDbId));
		environmentDatasetMap.get(trialDbId).addProperty(property);
		studyIdEnvironmentVariablesMap.get(trialDbId).add(termId);
	}

	private void addEnvironmentVariableValues(final StudyImportRequestDTO requestDTO,
		final Map<Integer, MeasurementVariable> environmentVariablesMap, final Map<Integer, List<ValueReference>> categoricalValuesMap,
		final Geolocation geolocation, final ExperimentModel experimentModel) {

		// The default value of an instance's location name is "Unspecified Location"
		final Optional<Location> location = StringUtils.isEmpty(requestDTO.getLocationDbId()) ? this.daoFactory.getLocationDAO().getUnspecifiedLocation() :
			Optional.of(this.daoFactory.getLocationDAO().getById(Integer.valueOf(requestDTO.getLocationDbId())));

		final List<GeolocationProperty> properties = new ArrayList<>();
		final List<Phenotype> phenotypes = new ArrayList<>();

		// Add location property
		final GeolocationProperty locationGeolocationProperty =
			new GeolocationProperty(geolocation, String.valueOf(location.get().getLocid()), 1, TermId.LOCATION_ID.getId());
		properties.add(locationGeolocationProperty);

		if (!CollectionUtils.isEmpty(requestDTO.getEnvironmentParameters())) {
			for (final EnvironmentParameter environmentParameter : requestDTO.getEnvironmentParameters()) {
				if (StringUtils.isNotEmpty(environmentParameter.getValue())) {
					final MeasurementVariable measurementVariable =
						environmentVariablesMap.get(Integer.valueOf(environmentParameter.getParameterPUI()));
					if (measurementVariable != null) {
						measurementVariable.setValue(environmentParameter.getValue());
						final DataType dataType = DataType.getById(measurementVariable.getDataTypeId());
						final java.util.Optional<VariableValueValidator> dataValidator =
							this.variableDataValidatorFactory.getValidator(dataType);
						if (categoricalValuesMap.containsKey(measurementVariable.getTermId())) {
							measurementVariable.setPossibleValues(categoricalValuesMap.get(measurementVariable.getTermId()));
						}
						if (!dataValidator.isPresent() || dataValidator.get().isValid(measurementVariable, true)) {
							if (VariableType.ENVIRONMENT_DETAIL.getId().equals(measurementVariable.getVariableType().getId())) {
								if (GEOLOCATION_METADATA.contains(measurementVariable.getTermId())) {
									this.mapGeolocationMetaData(geolocation, environmentParameter);
								} else {
									final GeolocationProperty property = new GeolocationProperty(geolocation, environmentParameter.getValue(), 1, measurementVariable.getTermId());
									properties.add(property);
								}
							} else if (VariableType.ENVIRONMENT_CONDITION.getId().equals(measurementVariable.getVariableType().getId())) {
								final Phenotype phenotype =
									new Phenotype(measurementVariable.getTermId(), environmentParameter.getValue(), experimentModel);
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

		geolocation.setProperties(properties);
		experimentModel.setPhenotypes(phenotypes);
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
				if (!studyIdEnvironmentVariablesMap.get(trialDbId).contains(variableId)) {
					final VariableType variableType = environmentVariablesMap.get(variableId).getVariableType();
					this.addProjectProperty(studyIdEnvironmentVariablesMap, trialIdEnvironmentDatasetMap, trialDbId, variableType,
						variableId, null);
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

	private String getGeolocationMetaDataValue(final Geolocation geolocation, final int variableId) {
		if (TermId.LATITUDE.getId() == variableId) {
			return geolocation.getLatitude() != null ? String.valueOf(geolocation.getLatitude()) : StringUtils.EMPTY;
		} else if (TermId.LONGITUDE.getId() == variableId) {
			return geolocation.getLongitude() != null ? String.valueOf(geolocation.getLongitude()) : StringUtils.EMPTY;
		} else if (TermId.GEODETIC_DATUM.getId() == variableId) {
			return geolocation.getGeodeticDatum();
		} else if (TermId.ALTITUDE.getId() == variableId) {
			return geolocation.getAltitude() != null ? String.valueOf(geolocation.getAltitude()) : StringUtils.EMPTY;
		}
		return StringUtils.EMPTY;
	}

	private void mapGeolocationMetaData(final Geolocation geolocation, final int variableId, final String value) {
		if (TermId.LATITUDE.getId() == variableId) {
			final Double dValue = !StringUtils.isBlank(value) ? Double.valueOf(value) : null;
			geolocation.setLatitude(dValue);
		} else if (TermId.LONGITUDE.getId() == variableId) {
			final Double dValue = !StringUtils.isBlank(value) ? Double.valueOf(value) : null;
			geolocation.setLongitude(dValue);
		} else if (TermId.GEODETIC_DATUM.getId() == variableId) {
			geolocation.setGeodeticDatum(value);
		} else if (TermId.ALTITUDE.getId() == variableId) {
			final Double dValue = !StringUtils.isBlank(value) ? Double.valueOf(value) : null;
			geolocation.setAltitude(dValue);
		}
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

	private void deleteExperimentalDesignIfApplicable(final int studyId, final int plotDatasetId) {
		final boolean hasExperimentalDesign = this.daoFactory.getExperimentDao().count(plotDatasetId) > 0;
		if (!hasExperimentalDesign) {
			this.experimentDesignService.deleteStudyExperimentDesign(studyId);
		}
	}

	public List<UserDto> getUsersForEnvironment(final Integer instanceId) {
		final List<Integer> personIds = this.daoFactory.getDmsProjectDAO().getPersonIdsAssociatedToEnvironment(instanceId);
		if (!CollectionUtils.isEmpty(personIds)) {
			return this.userService.getUsersByPersonIds(personIds);
		}
		return Collections.emptyList();
	}

	public List<UserDto> getUsersAssociatedToStudy(final Integer studyId) {
		final List<Integer> personIds = this.daoFactory.getDmsProjectDAO().getPersonIdsAssociatedToStudy(studyId);
		if (!CollectionUtils.isEmpty(personIds)) {
			return this.userService.getUsersByPersonIds(personIds);
		}
		return Collections.emptyList();
	}
}
