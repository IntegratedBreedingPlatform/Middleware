package org.generationcp.middleware.service.impl.study;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.api.location.LocationDTO;
import org.generationcp.middleware.dao.dms.ExperimentDao;
import org.generationcp.middleware.dao.dms.GeolocationDao;
import org.generationcp.middleware.dao.dms.GeolocationPropertyDao;
import org.generationcp.middleware.dao.dms.PhenotypeDao;
import org.generationcp.middleware.domain.dms.ExperimentType;
import org.generationcp.middleware.domain.dms.InstanceDescriptorData;
import org.generationcp.middleware.domain.dms.InstanceObservationData;
import org.generationcp.middleware.domain.dms.InstanceVariableData;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.GeolocationProperty;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.service.Service;
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
public class StudyInstanceServiceImpl extends Service implements StudyInstanceService {

	public static final List<Integer> GEOLOCATION_METADATA =
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
		super(sessionProvider);
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public List<StudyInstance> createStudyInstances(
		final CropType crop, final int studyId, final int datasetId, final int locationId,
		final Integer numberOfInstancesToGenerate) {
		Preconditions.checkArgument(numberOfInstancesToGenerate > 0);

		// Retrieve existing study instances
		final List<Geolocation> geolocations = this.daoFactory.getGeolocationDao().getEnvironmentGeolocations(studyId);
		final List<Integer> instanceNumbers =
			geolocations.stream().mapToInt(o -> Integer.parseInt(o.getDescription())).boxed().collect(Collectors.toList());

		final List<StudyInstance> studyInstances = new ArrayList<>();
		final boolean hasExperimentalDesign = this.experimentDesignService.getStudyExperimentDesignTypeTermId(studyId).isPresent();
		int instancesGenerated = 0;

		final LocationDTO locationDTO = this.daoFactory.getLocationDAO().getLocationDTO(locationId);

		if (locationDTO != null) {
			while (instancesGenerated < numberOfInstancesToGenerate) {
				final Geolocation geolocation = this.createNextGeolocation(instanceNumbers, hasExperimentalDesign);

				final GeolocationProperty locationGeolocationProperty =
					new GeolocationProperty(geolocation, String.valueOf(locationDTO.getId()), 1, TermId.LOCATION_ID.getId());
				geolocation.setProperties(Lists.newArrayList(locationGeolocationProperty));
				this.daoFactory.getGeolocationDao().save(geolocation);

				final ExperimentModel experimentModel =
					this.experimentModelGenerator.generate(crop, datasetId, Optional.of(geolocation), ExperimentType.TRIAL_ENVIRONMENT);

				this.daoFactory.getExperimentDao().save(experimentModel);

				final int instanceNumber = Integer.parseInt(geolocation.getDescription());
				final StudyInstance studyInstance =
					new StudyInstance(geolocation.getLocationId(), instanceNumber, false, false, true);

				studyInstance.setLocationId(locationDTO.getId());
				studyInstance.setLocationName(locationDTO.getName());
				studyInstance.setLocationAbbreviation(locationDTO.getAbbreviation());
				studyInstance.setInstanceId(geolocation.getLocationId());
				studyInstance.setLocationDescriptorDataId(locationGeolocationProperty.getGeolocationPropertyId());
				studyInstance.setExperimentId(experimentModel.getNdExperimentId());

				instanceNumbers.add(instanceNumber);
				studyInstances.add(studyInstance);
				instancesGenerated++;
			}
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
				final Optional<Integer> instanceHasGivenDatasetType =
					this.getDatasetIdForInstanceIdAndDatasetType(instance.getInstanceId(), DatasetTypeEnum.MEANS_DATA);
				if ((hasCrossesOrSelections && instance.isHasExperimentalDesign()) || (hasMeansDataset && instanceHasGivenDatasetType
					.isPresent())) {
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

		// Delete files associated to the instances
		this.daoFactory.getFileMetadataDAO().removeFiles(null, environmentDatasetId, null, instanceIds, null);

		//Update StudyExperimentGeolocation
		this.daoFactory.getExperimentDao().updateStudyExperimentGeolocationIfNecessary(studyId, instanceIds);

		// Delete plot and environment experiments
		final Integer plotDatasetId = this.studyService.getPlotDatasetId(studyId);
		final ExperimentDao experimentDao = this.daoFactory.getExperimentDao();
		experimentDao.deleteExperimentsForDatasets(Arrays.asList(plotDatasetId, environmentDatasetId), instanceNumbersToDelete);

		// Delete geolocation and its properties
		this.daoFactory.getGeolocationDao().deleteGeolocations(instanceIds);

		this.deleteExperimentalDesignIfApplicable(studyId, plotDatasetId);

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
			.updateDependentPhenotypesStatusByGeolocation(
				instanceObservationData.getInstanceId(),
				Arrays.asList(instanceObservationData.getVariableId()));

		return instanceObservationData;
	}

	@Override
	public Optional<InstanceObservationData> getInstanceObservation(
		final Integer instanceId, final Integer observationDataId,
		final Integer variableId) {

		final ExperimentModel experimentModel =
			this.daoFactory.getExperimentDao().getExperimentByTypeInstanceId(ExperimentType.TRIAL_ENVIRONMENT.getTermId(), instanceId);
		final Phenotype phenotype = this.daoFactory.getPhenotypeDAO().getPhenotype(experimentModel.getNdExperimentId(), observationDataId);
		if (phenotype != null) {
			return Optional
				.of(new InstanceObservationData(phenotype.getExperiment().getNdExperimentId(), observationDataId,
					phenotype.getObservableId(),
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
				.updateDependentPhenotypesStatusByGeolocation(
					instanceDescriptorData.getInstanceId(),
					Arrays.asList(instanceDescriptorData.getVariableId()));
		} else {
			final GeolocationProperty property = new GeolocationProperty(geolocation, value, 1, instanceDescriptorData.getVariableId());
			this.daoFactory.getGeolocationPropertyDao().save(property);
			instanceDescriptorData.setInstanceDescriptorDataId(property.getGeolocationPropertyId());
		}

		return instanceDescriptorData;
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
			.updateDependentPhenotypesStatusByGeolocation(
				instanceDescriptorData.getInstanceId(),
				Arrays.asList(instanceDescriptorData.getVariableId()));

		return instanceDescriptorData;
	}

	@Override
	public Optional<InstanceDescriptorData> getInstanceDescriptorData(
		final Integer instanceId, final Integer descriptorDataId,
		final Integer variableId) {

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
	public void deleteInstanceGeoreferences(final Integer instanceId) {
		this.daoFactory.getExperimentDao().deleteGeoreferencesByExperimentTypeAndInstanceId(ExperimentType.PLOT.getTermId(), instanceId);
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

	private void deleteExperimentalDesignIfApplicable(final int studyId, final int plotDatasetId) {
		final boolean hasExperimentalDesign = this.daoFactory.getExperimentDao().count(plotDatasetId) > 0;
		if (!hasExperimentalDesign) {
			this.experimentDesignService.deleteStudyExperimentDesign(studyId);
		}
	}

}
