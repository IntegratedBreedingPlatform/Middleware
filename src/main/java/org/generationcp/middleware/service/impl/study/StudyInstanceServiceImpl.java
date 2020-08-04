package org.generationcp.middleware.service.impl.study;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.dao.dms.ExperimentDao;
import org.generationcp.middleware.dao.dms.GeolocationDao;
import org.generationcp.middleware.dao.dms.GeolocationPropertyDao;
import org.generationcp.middleware.dao.dms.PhenotypeDao;
import org.generationcp.middleware.domain.dms.ExperimentType;
import org.generationcp.middleware.domain.dms.ObservationData;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.GeolocationProperty;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.service.api.dataset.DatasetService;
import org.generationcp.middleware.service.api.study.StudyInstanceService;
import org.generationcp.middleware.service.api.study.StudyService;
import org.generationcp.middleware.service.api.study.generation.ExperimentDesignService;
import org.generationcp.middleware.service.impl.study.generation.ExperimentModelGenerator;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
public class StudyInstanceServiceImpl implements StudyInstanceService {

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

	private DaoFactory daoFactory;

	public StudyInstanceServiceImpl() {
		// no-arg constuctor is required by CGLIB proxying used by Spring 3x and older.
	}

	public StudyInstanceServiceImpl(final HibernateSessionProvider sessionProvider) {
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
			final Optional<Location> location = this.getUnspecifiedLocation();

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
				studyInstance.setLocationObservationId(locationGeolocationProperty.getGeolocationPropertyId());
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
		if (hasCrossesOrSelections) {
			for (final StudyInstance instance : instances) {
				if (instance.isHasExperimentalDesign()) {
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
	public ObservationData addInstanceObservation(final ObservationData observationData, final boolean isEnvironmentCondition) {
		Preconditions.checkNotNull(observationData.getInstanceId());
		final Integer variableId = observationData.getVariableId();
		Preconditions.checkNotNull(variableId);
		Preconditions.checkNotNull(observationData.getValue());

		// Environment conditions are stored in phenotype. Other environment details are saved in nd_experimentprop
		if (isEnvironmentCondition) {
			final ExperimentModel experimentModel =
				this.daoFactory.getExperimentDao()
					.getExperimentByTypeInstanceId(ExperimentType.TRIAL_ENVIRONMENT.getTermId(), observationData.getInstanceId());
			final Phenotype phenotype =
				new Phenotype(variableId, observationData.getValue(), experimentModel);
			phenotype.setCreatedDate(new Date());
			phenotype.setUpdatedDate(new Date());
			phenotype.setcValue(observationData.getCategoricalValueId());
			phenotype.setName(String.valueOf(variableId));

			this.daoFactory.getPhenotypeDAO().save(phenotype);
			observationData.setObservationId(phenotype.getPhenotypeId());
		} else {

			final GeolocationDao geolocationDao = this.daoFactory.getGeolocationDao();
			final Geolocation geolocation = geolocationDao.getById(observationData.getInstanceId());
			final String value = this.getEnvironmentDataValue(observationData);

			if (GEOLOCATION_METADATA.contains(observationData.getVariableId())) {
				// Geolocation Metadata variables are stored in Geolocation table.
				// we just need to update their values if they are added to the study. No need to create GeolocationProperty.
				this.mapGeolocationMetaData(geolocation, observationData.getVariableId(), value);
				geolocationDao.save(geolocation);
				// Change the status to OUT_OF_SYNC of calculated traits that depend on the changed/updated variable.
				this.datasetService
					.updateDependentPhenotypesStatusByGeolocation(observationData.getInstanceId(),
						Arrays.asList(observationData.getVariableId()));
			} else {
				final GeolocationProperty property = new GeolocationProperty(geolocation, value, 1, observationData.getVariableId());
				this.daoFactory.getGeolocationPropertyDao().save(property);
				observationData.setObservationId(property.getGeolocationPropertyId());
			}

		}

		return observationData;
	}

	private String getEnvironmentDataValue(final ObservationData observationData) {
		return (observationData.getCategoricalValueId() != null && observationData.getCategoricalValueId() > 0) ?
			String.valueOf(observationData.getCategoricalValueId()) :
			observationData.getValue();
	}

	@Override
	public ObservationData updateInstanceObservation(final ObservationData observationData, final boolean isEnvironmentCondition) {
		Preconditions.checkNotNull(observationData.getObservationId());
		Preconditions.checkNotNull(observationData.getInstanceId());
		Preconditions.checkNotNull(observationData.getVariableId());
		Preconditions.checkNotNull(observationData.getValue());

		// Environment oonditions are stored in phenotype. Other environment details are saved in nd_experimentprop
		if (isEnvironmentCondition) {
			final PhenotypeDao phenotypeDAO = this.daoFactory.getPhenotypeDAO();
			final Phenotype phenotype = phenotypeDAO.getById(observationData.getObservationId());
			Preconditions.checkNotNull(phenotype);
			phenotype.setValue(observationData.getValue());
			phenotype.setcValue(observationData.getCategoricalValueId());
			phenotype.setUpdatedDate(new Date());
			phenotypeDAO.update(phenotype);
		} else {
			final GeolocationPropertyDao propertyDao = this.daoFactory.getGeolocationPropertyDao();
			final GeolocationProperty property = propertyDao.getById(observationData.getObservationId());
			Preconditions.checkNotNull(property);
			property.setValue(this.getEnvironmentDataValue(observationData));
			propertyDao.update(property);
		}

		// Change the status to OUT_OF_SYNC of calculated traits that depend on the changed/updated variable.
		this.datasetService
			.updateDependentPhenotypesStatusByGeolocation(observationData.getInstanceId(), Arrays.asList(observationData.getVariableId()));

		return observationData;
	}

	@Override
	public Optional<ObservationData> getInstanceObservation(final Integer instanceId, final Integer observationId, final Integer variableId,
		final boolean isEnvironmentCondition) {

		if (isEnvironmentCondition) {
			final ExperimentModel experimentModel =
				this.daoFactory.getExperimentDao().getExperimentByTypeInstanceId(ExperimentType.TRIAL_ENVIRONMENT.getTermId(), instanceId);
			final Phenotype phenotype = this.daoFactory.getPhenotypeDAO().getPhenotype(experimentModel.getNdExperimentId(), observationId);
			if (phenotype != null) {
				return Optional
					.of(new ObservationData(phenotype.getExperiment().getNdExperimentId(), observationId, phenotype.getObservableId(),
						phenotype.getValue(), phenotype.getcValueId()));
			}
		} else {
			final Geolocation geolocation = this.daoFactory.getGeolocationDao().getById(instanceId);

			if (GEOLOCATION_METADATA.contains(variableId)) {
				return Optional.of(new ObservationData(geolocation.getLocationId(), geolocation.getLocationId(), variableId,
					this.getGeolocationMetaDataValue(geolocation, variableId), null));
			} else {
				final GeolocationProperty property =
					this.daoFactory.getGeolocationPropertyDao().getById(observationId);
				if (property != null) {
					return Optional.of(new ObservationData(geolocation.getLocationId(), observationId, property.getTypeId(),
						property.getValue(), null));
				}
			}
		}
		return Optional.empty();
	}

	protected Optional<Location> getUnspecifiedLocation() {
		final List<Location> locations = this.daoFactory.getLocationDAO().getByName(Location.UNSPECIFIED_LOCATION, Operation.EQUAL);
		if (!locations.isEmpty()) {
			return Optional.of(locations.get(0));
		}
		return Optional.empty();
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
			geolocation.setLatitude(Double.valueOf(value));
		} else if (TermId.LONGITUDE.getId() == variableId) {
			geolocation.setLongitude(Double.valueOf(value));
		} else if (TermId.GEODETIC_DATUM.getId() == variableId) {
			geolocation.setGeodeticDatum(value);
		} else if (TermId.ALTITUDE.getId() == variableId) {
			geolocation.setAltitude(Double.valueOf(value));
		}
	}

}
